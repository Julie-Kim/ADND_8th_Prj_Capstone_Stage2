package com.example.purchasenoti;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.purchasenoti.database.PurchaseItemDatabase;
import com.example.purchasenoti.databinding.ActivityProductRecommendationBinding;
import com.example.purchasenoti.model.Product;
import com.example.purchasenoti.model.PurchaseItem;
import com.example.purchasenoti.model.SearchResults;
import com.example.purchasenoti.utilities.DateUtils;
import com.example.purchasenoti.utilities.EditDialogUtils;
import com.example.purchasenoti.utilities.JsonQueryUtils;
import com.example.purchasenoti.utilities.PreferenceUtils;
import com.example.purchasenoti.utilities.RetrofitConnection;
import com.example.purchasenoti.utilities.RetrofitInterface;
import com.google.android.material.appbar.AppBarLayout;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import retrofit2.Call;

public class ProductRecommendationActivity extends AppCompatActivity implements ProductListAdapter.ProductOnClickHandler {
    private static final String TAG = ProductRecommendationActivity.class.getSimpleName();

    private ActivityProductRecommendationBinding mBinding;
    private ProductListAdapter mAdapter;

    private PurchaseItem mPurchaseItem;

    private PurchaseItemDatabase mDb;
    private ItemViewModel mViewModel;
    private Observer<PurchaseItem> mObserver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_recommendation);
        mDb = PurchaseItemDatabase.getInstance(this);

        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "onCreate() intent is null.");
            return;
        }

        if (intent.hasExtra(ItemConstant.KEY_PURCHASE_ITEM)) {
            mPurchaseItem = intent.getParcelableExtra(ItemConstant.KEY_PURCHASE_ITEM);

            if (mPurchaseItem != null) {
                setAppbarTitle(mPurchaseItem.getItemName());

                updatePurchaseItemInfo(mPurchaseItem);

                int id = mPurchaseItem.getId();
                Log.d(TAG, "onCreate() purchase item id: " + id);

                mBinding.ivEdit.setOnClickListener(v -> AppExecutors.getInstance().diskIO().execute(() -> {
                    Log.d(TAG, "onCreate() Edit click, item id: " + id);

                    PurchaseItem currentItem = mDb.purchaseDao().loadPurchaseItemById(id);

                    AppExecutors.getInstance().mainThread().execute(() -> {
                        AlertDialog.Builder builder = EditDialogUtils.getItemInputDialog(
                                ProductRecommendationActivity.this, mDb.purchaseDao(), currentItem);

                        builder.setOnDismissListener(dialog -> {
                            ItemViewModelFactory factory = new ItemViewModelFactory(mDb, id);
                            mViewModel = new ViewModelProvider(ProductRecommendationActivity.this, factory).get(ItemViewModel.class);
                            mObserver = getObserver();

                            mViewModel.getItem().observe(ProductRecommendationActivity.this, mObserver);
                        });

                        builder.show();
                    });
                }));
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvProductList.setLayoutManager(layoutManager);
        mBinding.rvProductList.setHasFixedSize(true);

        mAdapter = new ProductListAdapter(this);
        mBinding.rvProductList.setAdapter(mAdapter);

        mBinding.ivRefresh.setOnClickListener(v -> loadProductList());

        loadProductList();
    }

    private void setAppbarTitle(final String title) {
        mBinding.appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }

                if (scrollRange + verticalOffset == 0) {
                    mBinding.toolbarTitle.setText(title);
                    isShow = true;
                } else if (isShow) {
                    mBinding.toolbarTitle.setText("");
                    isShow = false;
                }
            }
        });
    }

    private Observer<PurchaseItem> getObserver() {
        return purchaseItem -> {
            Log.d(TAG, "onChanged() load item data from database. item name: " + purchaseItem.getItemName());
            String prevItemName = mPurchaseItem.getItemName();

            updatePurchaseItemInfo(purchaseItem);

            if (!prevItemName.equals(purchaseItem.getItemName())) {
                loadProductList(purchaseItem.getItemName());
            }
        };
    }

    private void updatePurchaseItemInfo(PurchaseItem purchaseItem) {
        mPurchaseItem = purchaseItem;

        mBinding.tvPurchaseItemName.setText(mPurchaseItem.getItemName());

        mBinding.tvPurchaseTerm.setText(DateUtils.getTermString(ProductRecommendationActivity.this,
                mPurchaseItem.getPurchaseTermYear(), mPurchaseItem.getPurchaseTermMonth(), mPurchaseItem.getPurchaseTermDay()));

        mBinding.tvNextPurchaseDate.setText(mPurchaseItem.getNextPurchaseDate());
    }

    private void loadProductList() {
        loadProductList(mPurchaseItem.getItemName());
    }

    private void loadProductList(String itemName) {
        new FetchProductListTask(this).execute(itemName);
    }

    @Override
    public void onClick(Product product) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(product.getLink()));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private void updateProductList(ArrayList<Product> products) {
        showOrHideLoadingIndicator(false);

        if (products != null && !products.isEmpty()) {
            showOrHideProductList(true);
            mAdapter.setProductList(products);
        } else {
            showOrHideProductList(false);
        }
    }

    private void showOrHideLoadingIndicator(boolean show) {
        mBinding.pbLoadingIndicator.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void showOrHideProductList(boolean show) {
        mBinding.refreshLayout.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mBinding.rvProductList.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mViewModel != null) {
            mViewModel.getItem().removeObserver(mObserver);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.query_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_domain:
                showAmazonDomainSelectionDialog();
                return true;

            case R.id.action_sort_by:
                showSortBySelectionDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAmazonDomainSelectionDialog() {
        int checkedItem = PreferenceUtils.getAmazonDomainSettingValue(this);
        Log.d(TAG, "showAmazonDomainSelectionDialog() checked item: " + checkedItem);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.action_amazon_domain)
                .setSingleChoiceItems(R.array.amazon_domain_setting_strings,
                        checkedItem,
                        (dialog1, which) -> {
                            Log.d(TAG, "showAmazonDomainSelectionDialog() clicked item: " + which);
                            PreferenceUtils.setAmazonDomainSettingValue(this, which);
                        })
                .setPositiveButton(android.R.string.ok, (dialog12, which) -> loadProductList()).create()
                .show();
    }

    private void showSortBySelectionDialog() {
        int checkedItem = PreferenceUtils.getSortBySettingValue(this);
        Log.d(TAG, "showSortBySelectionDialog() checked item: " + checkedItem);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(R.string.action_sort_by)
                .setSingleChoiceItems(R.array.sort_by_setting_strings,
                        checkedItem,
                        (dialog1, which) -> {
                            Log.d(TAG, "showSortBySelectionDialog() clicked item: " + which);
                            PreferenceUtils.setSortBySettingValue(this, which);
                        })
                .setPositiveButton(android.R.string.ok, (dialog12, which) -> loadProductList()).create()
                .show();
    }

    private static class FetchProductListTask extends AsyncTask<String, Void, ArrayList<Product>> {

        private WeakReference<ProductRecommendationActivity> mActivityReference;

        FetchProductListTask(ProductRecommendationActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            ProductRecommendationActivity activity = mActivityReference.get();
            if (activity == null || activity.isFinishing()) {
                Log.e(TAG, "FetchProductListTask, onPreExecute() activity is null or is finishing.");
                return;
            }

            activity.showOrHideLoadingIndicator(true);
            activity.showOrHideProductList(true);
        }

        @Override
        protected ArrayList<Product> doInBackground(String... strings) {
            if (strings.length == 0) {
                Log.e(TAG, "FetchProductListTask, no parameter.");
                return null;
            }

            String itemName = strings[0];
            if (TextUtils.isEmpty(itemName)) {
                Log.e(TAG, "FetchProductListTask, wrong parameter.");
                return null;
            }

            Context context = mActivityReference.get();

            RetrofitInterface retrofitInterface = RetrofitConnection.getRetrofitInstance().create(RetrofitInterface.class);
            Call<SearchResults> call = retrofitInterface.geProductList(
                    JsonQueryUtils.getApiKey(),
                    JsonQueryUtils.getQueryType(),
                    JsonQueryUtils.getAmazonDomain(context),
                    itemName,
                    JsonQueryUtils.getSortBy(context)
            );

            ArrayList<Product> productList = null;

            try {
                SearchResults searchResults = call.execute().body();
                if (searchResults != null) {
                    productList = searchResults.getProducts();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return productList;
        }

        @Override
        protected void onPostExecute(ArrayList<Product> productList) {
            super.onPostExecute(productList);

            ProductRecommendationActivity activity = mActivityReference.get();
            if (activity == null || activity.isFinishing()) {
                Log.e(TAG, "FetchProductListTask, onPostExecute() activity is null or is finishing.");
                return;
            }

            if (productList == null || productList.isEmpty()) {
                Log.d(TAG, "FetchProductListTask, onPostExecute() productList is empty.");
                activity.updateProductList(null);
            } else {
                Log.d(TAG, "FetchProductListTask, onPostExecute() size of productList: " + productList.size());
                activity.updateProductList(productList);
            }
        }
    }
}
