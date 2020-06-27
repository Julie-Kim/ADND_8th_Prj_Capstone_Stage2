package com.example.purchasenoti.utilities;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.example.purchasenoti.AppExecutors;
import com.example.purchasenoti.R;
import com.example.purchasenoti.database.PurchaseItemDao;
import com.example.purchasenoti.databinding.ItemInputDialogBinding;
import com.example.purchasenoti.model.PurchaseItem;

import java.util.Calendar;

public class EditDialogUtils {
    public static final String TAG = EditDialogUtils.class.getSimpleName();

    public static void showItemInputDialog(Activity activity, PurchaseItemDao dao, PurchaseItem currentItem) {
        ItemInputDialogBinding dialogBinding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.item_input_dialog, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
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
                Log.e(TAG, "showItemInputDialog() positiveButton click, item name is empty.");
                Toast.makeText(activity, R.string.item_name_error, Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();

            String itemName = itemNameInput.toString();

            int year = dialogBinding.yearPicker.getValue();
            int month = dialogBinding.monthPicker.getValue();
            int day = dialogBinding.dayPicker.getValue();

            String lastPurchasedDate = DateUtils.getDate(dialogBinding.datePicker);
            PurchaseItem purchaseItem = new PurchaseItem(itemName, year, month, day, lastPurchasedDate);
            Log.d(TAG, "showItemInputDialog() positiveButton click, item: " + purchaseItem.toString());

            AppExecutors.getInstance().diskIO().execute(() -> {
                if (currentItem != null) {
                    dao.updatePurchaseItem(currentItem.getId(),
                            itemName, year, month, day, lastPurchasedDate);
                } else {
                    dao.insertPurchaseItem(purchaseItem);
                }
            });
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private static void initPurchaseTermPicker(ItemInputDialogBinding dialogBinding) {
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
}
