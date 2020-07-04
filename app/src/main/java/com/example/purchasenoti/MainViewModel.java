package com.example.purchasenoti;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.purchasenoti.database.PurchaseItemDatabase;
import com.example.purchasenoti.model.PurchaseItem;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = MainViewModel.class.getSimpleName();

    private LiveData<List<PurchaseItem>> mPurchaseItems;

    public MainViewModel(@NonNull Application application) {
        super(application);

        PurchaseItemDatabase database = PurchaseItemDatabase.getInstance(this.getApplication());
        Log.d(TAG, "Actively retrieving the purchase items from the database");
        mPurchaseItems = database.purchaseDao().loadAllPurchaseItems();
    }

    public LiveData<List<PurchaseItem>> getPurchaseItems() {
        return mPurchaseItems;
    }
}
