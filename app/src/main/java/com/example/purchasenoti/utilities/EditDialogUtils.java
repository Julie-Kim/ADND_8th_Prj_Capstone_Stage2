package com.example.purchasenoti.utilities;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.example.purchasenoti.AlarmReceiver;
import com.example.purchasenoti.AppExecutors;
import com.example.purchasenoti.ItemConstant;
import com.example.purchasenoti.R;
import com.example.purchasenoti.database.PurchaseItemDao;
import com.example.purchasenoti.databinding.ItemInputDialogBinding;
import com.example.purchasenoti.model.PurchaseItem;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class EditDialogUtils {
    private static final String TAG = EditDialogUtils.class.getSimpleName();

    public static void showItemInputDialog(Activity activity, PurchaseItemDao dao, PurchaseItem currentItem) {
        getItemInputDialog(activity, dao, currentItem).show();
    }

    public static AlertDialog.Builder getItemInputDialog(Activity activity, PurchaseItemDao dao, PurchaseItem currentItem) {
        ItemInputDialogBinding dialogBinding = DataBindingUtil.inflate(activity.getLayoutInflater(),
                R.layout.item_input_dialog, null, false);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.enter_purchase_item);
        builder.setView(dialogBinding.getRoot());

        initPurchaseTermPicker(dialogBinding);

        if (currentItem != null) {
            String itemName = currentItem.getItemName();
            dialogBinding.inputItemName.setText(currentItem.getItemName());
            dialogBinding.inputItemName.setSelection(itemName.length());

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
                Log.e(TAG, "getItemInputDialog() positiveButton click, item name is empty.");
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
            Log.d(TAG, "getItemInputDialog() positiveButton click, item: " + purchaseItem.toString());

            AppExecutors.getInstance().diskIO().execute(() -> {
                if (currentItem != null) {
                    dao.updatePurchaseItem(currentItem.getId(),
                            itemName, year, month, day, lastPurchasedDate);
                } else {
                    dao.insertPurchaseItem(purchaseItem);
                }
            });

            setNotification(activity, purchaseItem);
        });

        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        return builder;
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
        dialogBinding.dayPicker.setWrapSelectorWheel(false);
    }

    private static void setNotification(Activity activity, PurchaseItem purchaseItem) {
        Calendar calendar = DateUtils.getNextDate(purchaseItem.getLastPurchasedDate(), purchaseItem.getPurchaseTermYear(),
                purchaseItem.getPurchaseTermMonth(), purchaseItem.getPurchaseTermDay());

        Intent intent = new Intent(activity, AlarmReceiver.class);
        intent.putExtra(ItemConstant.KEY_PURCHASE_ITEM, purchaseItem);
        intent.putExtra(ItemConstant.KEY_ITEM_NAME, purchaseItem.getItemName());
        intent.putExtra(ItemConstant.KEY_ITEM_ID, purchaseItem.getId());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}
