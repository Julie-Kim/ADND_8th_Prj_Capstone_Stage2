package com.example.purchasenoti;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.purchasenoti.databinding.ActivityProductRecommendationBinding;
import com.example.purchasenoti.model.Product;

public class ProductRecommendationActivity extends AppCompatActivity implements ProductListAdapter.ProductOnClickHandler {
    public static final String TAG = ProductRecommendationActivity.class.getSimpleName();

    private ActivityProductRecommendationBinding mBinding;
    private ProductListAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_product_recommendation);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvProductList.setLayoutManager(layoutManager);
        mBinding.rvProductList.setHasFixedSize(true);

        mAdapter = new ProductListAdapter(this);
        mBinding.rvProductList.setAdapter(mAdapter);

        loadProductList();
    }

    private void loadProductList() {
        //TODO
    }

    @Override
    public void onClick(Product mProduct) {
        //TODO
    }
}
