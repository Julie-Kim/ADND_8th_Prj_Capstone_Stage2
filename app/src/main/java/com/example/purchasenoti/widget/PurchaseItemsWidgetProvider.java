package com.example.purchasenoti.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.purchasenoti.MainActivity;
import com.example.purchasenoti.R;

public class PurchaseItemsWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent mainPendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        Intent serviceIntent = new Intent(context, WidgetRemoteViewsService.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.purchase_items_widget);
        views.setRemoteAdapter(R.id.widget_purchase_item_list, serviceIntent);
        views.setEmptyView(R.id.widget_purchase_item_list, R.id.tv_empty_message);
        views.setOnClickPendingIntent(R.id.purchase_items_widget_layout, mainPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        PurchaseItemsWidgetService.startActionUpdateWidgets(context);
    }

    public static void updateWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}
