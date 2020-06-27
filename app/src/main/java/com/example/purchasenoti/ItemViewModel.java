package com.example.purchasenoti;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.purchasenoti.database.PurchaseItemDatabase;
import com.example.purchasenoti.model.PurchaseItem;

public class ItemViewModel extends ViewModel {

    private LiveData<PurchaseItem> mItem;

    public ItemViewModel(PurchaseItemDatabase database, int id) {
        mItem = database.purchaseDao().loadLivePurchaseItemById(id);
    }

    public LiveData<PurchaseItem> getItem() {
        return mItem;
    }
}
