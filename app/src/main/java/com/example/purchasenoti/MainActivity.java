package com.example.purchasenoti;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.purchasenoti.database.PurchaseItemDatabase;
import com.example.purchasenoti.databinding.ActivityMainBinding;
import com.example.purchasenoti.model.PurchaseItem;
import com.example.purchasenoti.utilities.DateUtils;
import com.example.purchasenoti.utilities.EditDialogUtils;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements PurchaseItemListAdapter.PurchaseItemOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private PurchaseItemListAdapter mAdapter;

    private PurchaseItemDatabase mDb;
    private ViewModelProvider.Factory mViewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mDb = PurchaseItemDatabase.getInstance(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvPurchaseItemList.setLayoutManager(layoutManager);
        mBinding.rvPurchaseItemList.setHasFixedSize(true);

        mAdapter = new PurchaseItemListAdapter(this);
        mBinding.rvPurchaseItemList.setAdapter(mAdapter);

        loadPurchaseItemList();

        mBinding.btAdd.setOnClickListener(v ->
                EditDialogUtils.showItemInputDialog(this, mDb.purchaseDao(), null));
    }

    private void loadPurchaseItemList() {
        setupViewModel();
    }

    @Override
    public void onItemClick(PurchaseItem purchaseItem) {
        Intent intent = new Intent(this, ProductRecommendationActivity.class);
        intent.putExtra(ItemConstant.KEY_ID, purchaseItem.getId());
        intent.putExtra(ItemConstant.KEY_ITEM_NAME, purchaseItem.getItemName());
        intent.putExtra(ItemConstant.KEY_PURCHASE_TERM, DateUtils.getTermString(this,
                purchaseItem.getPurchaseTermYear(), purchaseItem.getPurchaseTermMonth(), purchaseItem.getPurchaseTermDay()));
        intent.putExtra(ItemConstant.KEY_NEXT_PURCHASED_DATE, purchaseItem.getNextPurchaseDate());
        startActivity(intent);
    }

    @Override
    public void onEdit(PurchaseItem purchaseItem) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            int id = purchaseItem.getId();
            Log.d(TAG, "onEdit() id: " + id);

            PurchaseItem currentItem = mDb.purchaseDao().loadPurchaseItemById(id);
            AppExecutors.getInstance().mainThread().execute(() ->
                    EditDialogUtils.showItemInputDialog(this, mDb.purchaseDao(), currentItem));
        });
    }

    @Override
    public void onDelete(PurchaseItem purchaseItem) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            int id = purchaseItem.getId();
            Log.d(TAG, "onDelete() id: " + id);

            mDb.purchaseDao().deleteById(id);
        });
    }

    private void setupViewModel() {
        showOrHideLoadingIndicator(true);

        if (mViewModelFactory == null) {
            mViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }
        MainViewModel viewModel = new ViewModelProvider(this, mViewModelFactory).get(MainViewModel.class);

        viewModel.getmPurchaseItems().observe(this, purchaseItems -> {
            Log.d(TAG, "Updating list of purchase items from LiveData in ViewModel");
            showOrHideLoadingIndicator(false);

            if (purchaseItems.isEmpty()) {
                showOrHidePurchaseItems(false);
            } else {
                showOrHidePurchaseItems(true);
                mAdapter.setPurchaseItemList(new ArrayList<>(purchaseItems));
            }
        });
    }

    private void showOrHideLoadingIndicator(boolean show) {
        mBinding.pbLoadingIndicator.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    private void showOrHidePurchaseItems(boolean show) {
        mBinding.tvEmptyMessage.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mBinding.rvPurchaseItemList.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}