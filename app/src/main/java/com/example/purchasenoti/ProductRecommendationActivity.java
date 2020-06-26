package com.example.purchasenoti;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.purchasenoti.databinding.ActivityProductRecommendationBinding;
import com.example.purchasenoti.model.Product;
import com.example.purchasenoti.model.SearchResults;
import com.example.purchasenoti.utilities.JsonQueryUtils;
import com.example.purchasenoti.utilities.RetrofitConnection;
import com.example.purchasenoti.utilities.RetrofitInterface;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductRecommendationActivity extends AppCompatActivity implements ProductListAdapter.ProductOnClickHandler {
    public static final String TAG = ProductRecommendationActivity.class.getSimpleName();

    private ActivityProductRecommendationBinding mBinding;
    private ProductListAdapter mAdapter;

    private String mItemName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_recommendation);

        Intent intent = getIntent();
        if (intent == null) {
            Log.e(TAG, "onCreate() intent is null.");
            return;
        }

        if (intent.hasExtra(ItemConstant.KEY_ITEM_NAME)) {
            mItemName = intent.getStringExtra(ItemConstant.KEY_ITEM_NAME);
            Log.d(TAG, "onCreate() purchase item name: " + mItemName);

            mBinding.tvPurchaseItemName.setText(mItemName);
        }

        if (intent.hasExtra(ItemConstant.KEY_PURCHASE_TERM)) {
            mBinding.tvPurchaseTerm.setText(intent.getStringExtra(ItemConstant.KEY_PURCHASE_TERM));
        }

        if (intent.hasExtra(ItemConstant.KEY_NEXT_PURCHASED_DATE)) {
            mBinding.tvNextPurchaseDate.setText(intent.getStringExtra(ItemConstant.KEY_NEXT_PURCHASED_DATE));
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvProductList.setLayoutManager(layoutManager);
        mBinding.rvProductList.setHasFixedSize(true);

        mAdapter = new ProductListAdapter(this);
        mBinding.rvProductList.setAdapter(mAdapter);

        loadProductList();
    }

    private void loadProductList() {
        showOrHideLoadingIndicator(true);

        RetrofitInterface retrofitInterface = RetrofitConnection.getRetrofitInstance().create(RetrofitInterface.class);
        Call<SearchResults> call = retrofitInterface.geProductList(
                JsonQueryUtils.getApiKey(),
                JsonQueryUtils.getQueryType(),
                JsonQueryUtils.getAmazonDomain(),
                mItemName,
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

                        showOrHideLoadingIndicator(false);
                    } else {
                        Log.d(TAG, "onResponse() size of productList: " + productList.size());

                        updateProductList(productList);
                    }
                } else {
                    showOrHideLoadingIndicator(false);
                    Log.e(TAG, "onResponse() response is not successful.");
                }
            }

            @Override
            public void onFailure(Call<SearchResults> call, Throwable t) {
                showOrHideLoadingIndicator(false);

                Log.e(TAG, "onFailure() : " + t.toString());
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
        mBinding.tvEmptyMessage.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mBinding.rvProductList.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}
