package com.example.purchasenoti;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.example.purchasenoti.utilities.AdUtils;
import com.example.purchasenoti.utilities.DateUtils;
import com.example.purchasenoti.utilities.EditDialogUtils;
import com.example.purchasenoti.utilities.PreferenceUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements PurchaseItemListAdapter.PurchaseItemOnClickHandler {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ActivityMainBinding mBinding;
    private PurchaseItemListAdapter mAdapter;

    private PurchaseItemDatabase mDb;
    private ViewModelProvider.Factory mViewModelFactory;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mDb = PurchaseItemDatabase.getInstance(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        setAppbarTitle(getString(R.string.app_name));

        initAdView();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mBinding.rvPurchaseItemList.setLayoutManager(layoutManager);
        mBinding.rvPurchaseItemList.setHasFixedSize(true);

        mAdapter = new PurchaseItemListAdapter(this);
        mBinding.rvPurchaseItemList.setAdapter(mAdapter);

        loadPurchaseItemList();

        mBinding.btAdd.setOnClickListener(v -> {
            EditDialogUtils.showItemInputDialog(this, mDb.purchaseDao(), null);
            Bundle params = new Bundle();
            params.putString("button", "add");
            mFirebaseAnalytics.logEvent("button_click", params);
        });
    }

    private void initAdView() {
        AdUtils.initMobileAds(this, findViewById(R.id.adView), mBinding.itemListLayout);
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

    private void loadPurchaseItemList() {
        setupViewModel();
    }

    @Override
    public void onItemClick(PurchaseItem purchaseItem) {
        Intent intent = new Intent(this, ProductRecommendationActivity.class);
        intent.putExtra(ItemConstant.KEY_PURCHASE_ITEM, purchaseItem);
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

        viewModel.getPurchaseItems().observe(this, purchaseItems -> {
            Log.d(TAG, "setupViewModel() observe, Updating list of purchase items from LiveData in ViewModel");
            showOrHideLoadingIndicator(false);

            if (purchaseItems.isEmpty()) {
                showOrHidePurchaseItems(false);
            } else {
                showOrHidePurchaseItems(true);
                mAdapter.setPurchaseItemList(new ArrayList<>(purchaseItems));

                for (PurchaseItem item : purchaseItems) {
                    setNotification(item);
                }
                updatePreferencesForWidget(new ArrayList<>(purchaseItems));
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

    private void setNotification(PurchaseItem purchaseItem) {
        String nextDate = purchaseItem.getNextPurchaseDate();
        if (DateUtils.getDiffDay(nextDate) < 0) {
            Log.d(TAG, "setNotification() not future alarm, no notification.");
            return;
        }

        Calendar calendar = DateUtils.getDate(purchaseItem.getNextPurchaseDate());
        Log.d(TAG, "setNotification() item: " + purchaseItem);
        if (calendar == null) {
            Log.e(TAG, "setNotification() calendar is null, no notification.");
            return;
        }
        Log.d(TAG, "setNotification() next date: " + calendar.getTime());

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra(ItemConstant.KEY_ITEM_NAME, purchaseItem.getItemName());
        intent.putExtra(ItemConstant.KEY_ITEM_ID, purchaseItem.getId());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    private void updatePreferencesForWidget(ArrayList<PurchaseItem> purchaseItems) {
        PreferenceUtils.setPurchaseItemListForWidget(this, purchaseItems);
    }
}