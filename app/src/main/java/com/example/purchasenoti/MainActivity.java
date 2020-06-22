package com.example.purchasenoti;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.purchasenoti.database.PurchaseItemDatabase;
import com.example.purchasenoti.databinding.ActivityMainBinding;
import com.example.purchasenoti.databinding.ItemInputDialogBinding;
import com.example.purchasenoti.model.PurchaseItem;
import com.example.purchasenoti.util.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;

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

        mBinding.btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showItemInputDialog(null);
            }
        });
    }

    private void loadPurchaseItemList() {
        showOrHidePurchaseItems(true);
        setupViewModel();
    }

    @Override
    public void onItemClick(PurchaseItem purchaseItem) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            int id = purchaseItem.getId();
            Log.d(TAG, "onDelete() id: " + id);

            PurchaseItem currentItem = mDb.purchaseDao().loadPurchaseItemById(id);
            AppExecutors.getInstance().mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    showItemInputDialog(currentItem);
                }
            });
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

    private void showItemInputDialog(PurchaseItem currentItem) {
        ItemInputDialogBinding dialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_input_dialog, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_purchase_item);
        builder.setView(dialogBinding.getRoot());

        initPurchaseTermPicker(dialogBinding);

        if (currentItem != null) {
            dialogBinding.inputItemName.setText(currentItem.getItemName());

            dialogBinding.yearPicker.setValue(currentItem.getPurchaseTermYear());
            dialogBinding.monthPicker.setValue(currentItem.getPurchaseTermMonth());
            dialogBinding.dayPicker.setValue(currentItem.getPurchaseTermDay());

            Calendar calendar = DateUtils.getCalendar(currentItem.getLastPurchasedDate());
            if (calendar != null) {
                dialogBinding.datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DATE), null);
            }
        }

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            Editable itemNameInput = dialogBinding.inputItemName.getText();
            if (TextUtils.isEmpty(itemNameInput)) {
                Log.e(TAG, "showItemInputDialog() item name is empty.");
                Toast.makeText(this, R.string.item_name_error, Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();

            String itemName = itemNameInput.toString();

            int year = dialogBinding.yearPicker.getValue();
            int month = dialogBinding.monthPicker.getValue();
            int day = dialogBinding.dayPicker.getValue();

            String lastPurchasedDate = DateUtils.getDate(dialogBinding.datePicker);
            PurchaseItem purchaseItem = new PurchaseItem(itemName, year, month, day, lastPurchasedDate);
            Log.d(TAG, "showItemInputDialog() onClick, item: " + purchaseItem.toString());

            AppExecutors.getInstance().diskIO().execute(() -> {
                if (currentItem != null) {
                    mDb.purchaseDao().updatePurchaseItem(currentItem.getId(),
                            itemName, year, month, day, lastPurchasedDate);
                } else {
                    mDb.purchaseDao().insertPurchaseItem(purchaseItem);
                }
            });
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void initPurchaseTermPicker(ItemInputDialogBinding dialogBinding) {
        dialogBinding.yearPicker.setMinValue(0);
        dialogBinding.yearPicker.setMaxValue(20);
        dialogBinding.yearPicker.setWrapSelectorWheel(false);

        dialogBinding.monthPicker.setMinValue(0);
        dialogBinding.monthPicker.setMaxValue(11);
        dialogBinding.monthPicker.setWrapSelectorWheel(false);

        dialogBinding.dayPicker.setMinValue(0);
        dialogBinding.dayPicker.setMaxValue(30);
        dialogBinding.dayPicker.setValue(1);
        dialogBinding.dayPicker.setWrapSelectorWheel(false);
    }

    private void setupViewModel() {
        if (mViewModelFactory == null) {
            mViewModelFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication());
        }
        MainViewModel viewModel = new ViewModelProvider(this, mViewModelFactory).get(MainViewModel.class);

        viewModel.getmPurchaseItems().observe(this, purchaseItems -> {
            Log.d(TAG, "Updating list of purchase items from LiveData in ViewModel");

            if (purchaseItems.isEmpty()) {
                showOrHidePurchaseItems(false);
            } else {
                showOrHidePurchaseItems(true);
                mAdapter.setPurchaseItemList(new ArrayList<>(purchaseItems));
            }
        });
    }

    private void showOrHidePurchaseItems(boolean show) {
        mBinding.tvEmptyMessage.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        mBinding.rvPurchaseItemList.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }
}