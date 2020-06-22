package com.example.purchasenoti.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.purchasenoti.model.PurchaseItem;

@Database(entities = {PurchaseItem.class}, version = 1, exportSchema = false)
public abstract class PurchaseItemDatabase extends RoomDatabase {
    private static final String TAG = PurchaseItemDatabase.class.getSimpleName();

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "purchase_item_database";
    private static PurchaseItemDatabase sInstance;

    public static PurchaseItemDatabase getInstance(Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                Log.d(TAG, "Creating new database instance");
                sInstance = Room.databaseBuilder(context.getApplicationContext(), PurchaseItemDatabase.class,
                        PurchaseItemDatabase.DATABASE_NAME).build();
            }
        }
        Log.d(TAG, "Getting the database instance");
        return sInstance;
    }

    public abstract PurchaseItemDao purchaseDao();
}
