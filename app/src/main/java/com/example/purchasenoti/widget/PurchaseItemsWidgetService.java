package com.example.purchasenoti.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.purchasenoti.R;

public class PurchaseItemsWidgetService extends IntentService {
    private static final String TAG = PurchaseItemsWidgetService.class.getSimpleName();

    public static final String ACTION_UPDATE_WIDGETS = "com.example.purchasenoti.widget.action.update_widgets";

    public PurchaseItemsWidgetService() {
        super("PurchaseItemsWidgetService");
    }

    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, PurchaseItemsWidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String action = intent.getAction();

            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateWidgets();
            }
        }
    }

    private void handleActionUpdateWidgets() {
        Log.d(TAG, "handleActionUpdateWidgets()");

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, PurchaseItemsWidgetProvider.class));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.purchase_items_widget_layout);
        PurchaseItemsWidgetProvider.updateWidgets(this, appWidgetManager, appWidgetIds);
    }
}
