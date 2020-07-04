package com.example.purchasenoti.widget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.purchasenoti.MainActivity;
import com.example.purchasenoti.R;
import com.example.purchasenoti.utilities.PreferenceUtils;

import java.util.ArrayList;

public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = WidgetRemoteViewsFactory.class.getSimpleName();

    private Context mContext;

    private ArrayList<String> mItemList = new ArrayList<>();

    public WidgetRemoteViewsFactory(Context context) {
        mContext = context;
    }

    @Override
    public void onCreate() {
        mItemList = PreferenceUtils.getPurchaseItemListForWidget(mContext);
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (mItemList.isEmpty()) {
            return 0;
        }
        return mItemList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        String item = mItemList.get(position);
        Log.d(TAG, "getViewAt() item: " + item);

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_purchase_list_item);
        views.setTextViewText(R.id.tv_purchase_item_info, item);

        Intent intent = new Intent(mContext, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.tv_purchase_item_info, pendingIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }
}
