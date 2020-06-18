package com.example.purchasenoti.model;

import androidx.annotation.NonNull;

import java.util.Date;

public class PurchaseItem {

    private String mItemName;

    private Date mPurchaseTerm;

    private String mPurchaseTermString;

    private Date mLastPurchaseDate;

    private String mLastPurchaseDateString;

    private Date mNextPurchaseDate;

    private String mNextPurchaseDateString;

    @NonNull
    @Override
    public String toString() {
        return super.toString();    //TODO
    }
}