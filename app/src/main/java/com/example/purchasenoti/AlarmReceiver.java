package com.example.purchasenoti;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = AlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String itemName = "";
        if (intent.hasExtra(ItemConstant.KEY_ITEM_NAME)) {
            itemName = intent.getStringExtra(ItemConstant.KEY_ITEM_NAME);
        } else {
            Log.e(TAG, "onReceive(), no intent extra.");
            return;
        }

        int id = intent.getIntExtra(ItemConstant.KEY_ITEM_ID, 0);
        Log.d(TAG, "onReceive(), item: " + itemName + ", id: " + id);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.drawable.ic_launcher_foreground);

            String channelName = context.getString(R.string.channel_name);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel("default", channelName, importance);
            channel.setDescription(description);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        } else {
            builder.setSmallIcon(R.mipmap.ic_launcher);
        }

        builder.setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setWhen(Calendar.getInstance().getTimeInMillis())
                .setTicker(context.getString(R.string.purchase_item))
                .setContentTitle(context.getString(R.string.time_to_buy))
                .setContentText(context.getString(R.string.time_to_buy_item, /*purchaseItem.getItemName()*/itemName))
                .setContentIntent(pendingIntent);

        if (notificationManager != null) {
            notificationManager.notify(id/*purchaseItem.getId()*/, builder.build());
        }
    }
}
