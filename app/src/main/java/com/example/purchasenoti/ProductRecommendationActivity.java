package com.example.purchasenoti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

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
import com.example.purchasenoti.utilities.RetrofitConnection;
import com.example.purchasenoti.utilities.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                updatePurchaseItemInfo(mPurchaseItem);

                int id = mPurchaseItem.getId();
                Log.d(TAG, "onCreate() purchase item id: " + id);

                mBinding.ivEdit.setOnClickListener(v -> AppExecutors.getInstance().diskIO().execute(() -> {
                    Log.d(TAG, "onCreate() Edit click, item id: " + id);

                    PurchaseItem currentItem = mDb.purchaseDao().loadPurchaseItemById(id);

                    AppExecutors.getInstance().mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            AlertDialog.Builder builder = EditDialogUtils.getItemInputDialog(
                                    ProductRecommendationActivity.this, mDb.purchaseDao(), currentItem);

                            builder.setOnDismissListener(dialog -> {
                                ItemViewModelFactory factory = new ItemViewModelFactory(mDb, id);
                                mViewModel = new ViewModelProvider(ProductRecommendationActivity.this, factory).get(ItemViewModel.class);
                                mObserver = getObserver();

                                mViewModel.getItem().observe(ProductRecommendationActivity.this, mObserver);
                            });

                            builder.show();
                        }
                    });
                }));
            }
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvProductList.setLayoutManager(layoutManager);
        mBinding.rvProductList.setHasFixedSize(true);

        mAdapter = new ProductListAdapter(this);
        mBinding.rvProductList.setAdapter(mAdapter);

        mBinding.ivRefresh.setOnClickListener(v -> loadProductList(mPurchaseItem.getItemName()));

        loadProductList(mPurchaseItem.getItemName());
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

    private void loadProductList(String itemName) {
        showOrHideLoadingIndicator(true);
        showOrHideProductList(true);

        RetrofitInterface retrofitInterface = RetrofitConnection.getRetrofitInstance().create(RetrofitInterface.class);
        Call<SearchResults> call = retrofitInterface.geProductList(
                JsonQueryUtils.getApiKey(),
                JsonQueryUtils.getQueryType(),
                JsonQueryUtils.getAmazonDomain(),
                itemName,
                JsonQueryUtils.getSortBy()
        );

        call.enqueue(new Callback<SearchResults>() {
            @Override
            public void onResponse(Call<SearchResults> call, Response<SearchResults> response) {
                if (response.isSuccessful()) {
                    SearchResults searchResults = response.body();
                    ArrayList<Product> productList = null;
                    if (searchResults != null) {
                        productList = searchResults.getProducts();
                    }

                    if (productList == null || productList.isEmpty()) {
                        Log.d(TAG, "onResponse() productList is empty.");
                        updateProductList(null);
                    } else {
                        Log.d(TAG, "onResponse() size of productList: " + productList.size());
                        updateProductList(productList);
                    }
                } else {
                    Log.e(TAG, "onResponse() response is not successful.");
                    updateProductList(null);
                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                Log.e(TAG, "onFailure() : " + t.toString());
                updateProductList(null);
            }
        });
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

            // TODO: updatePreferencesForWidget(products);
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

        mViewModel.getItem().removeObserver(mObserver);
    }
}
