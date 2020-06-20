package com.example.purchasenoti.model;

import androidx.annotation.NonNull;

public class PurchaseItem {

    private String mItemName;

    private int mPurchaseTermYear;
    private int mPurchaseTermMonth;
    private int mPurchaseTermDay;

    private String mLastPurchaseDate;

    private String mNextPurchaseDate;

    public PurchaseItem(String itemName, int termYear, int termMonth, int termDay, String lastPurchaseDate) {
        mItemName = itemName;
        mPurchaseTermYear = termYear;
        mPurchaseTermMonth = termMonth;
        mPurchaseTermDay = termDay;
        mLastPurchaseDate = lastPurchaseDate;
    }

    @NonNull
    @Override
    public String toString() {
        return super.toString();    //TODO
    }
}