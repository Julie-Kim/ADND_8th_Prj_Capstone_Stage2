package com.example.purchasenoti;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.purchasenoti.databinding.ActivityMainBinding;
import com.example.purchasenoti.model.PurchaseItem;

public class MainActivity extends AppCompatActivity implements PurchaseItemListAdapter.PurchaseItemOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private PurchaseItemListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvPurchaseItemList.setLayoutManager(layoutManager);
        mBinding.rvPurchaseItemList.setHasFixedSize(true);

        mAdapter = new PurchaseItemListAdapter(this);
        mBinding.rvPurchaseItemList.setAdapter(mAdapter);

        loadPurchaseItemList();
    }

    private void loadPurchaseItemList() {
        //TODO
    }

    @Override
    public void onClick(PurchaseItem purchaseItem) {
        //TODO
    }
}