package com.example.purchasenoti;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.purchasenoti.databinding.ActivityMainBinding;
import com.example.purchasenoti.databinding.ItemInputDialogBinding;
import com.example.purchasenoti.model.PurchaseItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

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

        mBinding.btAdd.setOnClickListener(this::showItemInputDialog);
    }

    private void loadPurchaseItemList() {
        //TODO
    }

    @Override
    public void onClick(PurchaseItem purchaseItem) {
        //TODO
    }

    private void showItemInputDialog(View v) {
        ItemInputDialogBinding dialogBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.item_input_dialog, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.enter_purchase_item);
        builder.setView(dialogBinding.getRoot());

        initPurchaseTermPicker(dialogBinding);

        //TODO: when edit item info, set date
        //dialogBinding.datePicker.init(2000, 11-1,11, null);

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.dismiss();

            String itemName = Objects.requireNonNull(dialogBinding.inputItemName.getText()).toString();
            //TODO: item name empty check

            int year = dialogBinding.yearPicker.getValue();
            int month = dialogBinding.monthPicker.getValue();
            int day = dialogBinding.dayPicker.getValue();
            String purchaseTerm = year + " year(s) " + month + " month(s) " + day + " day(s)";

            String formattedDate = getDate(dialogBinding.datePicker);
            Log.d(TAG, "showItemInputDialog() onClick, itemName: " + itemName + ", purchaseTerm: " + purchaseTerm
                    + ", last purchased date: " + formattedDate);
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

        dialogBinding.dayPicker.setMinValue(1);
        dialogBinding.dayPicker.setMaxValue(30);
        dialogBinding.dayPicker.setValue(1);
        dialogBinding.dayPicker.setWrapSelectorWheel(false);
    }

    private String getDate(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        //TODO: date boundary check

        //Date date = formatter.parse(formattedDate);

        return formatter.format(calendar.getTime());
    }
}