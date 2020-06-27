package com.example.purchasenoti;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.purchasenoti.database.PurchaseItemDatabase;

public class ItemViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final PurchaseItemDatabase mDb;
    private final int mId;


    public ItemViewModelFactory(PurchaseItemDatabase database, int id) {
        mDb = database;
        mId = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ItemViewModel(mDb, mId);
    }
}
