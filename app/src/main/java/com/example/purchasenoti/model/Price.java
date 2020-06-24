package com.example.purchasenoti.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Price implements Parcelable {

    @SerializedName("symbol")
    private String mSymbol;

    @SerializedName("value")
    private String mValue;

    @SerializedName("currency")
    private String mCurrency;

    @SerializedName("raw")
    private String mRaw;

    @SerializedName("name")
    private String mName;

    public Price(String symbol, String value, String currency, String raw, String name) {
        mSymbol = symbol;
        mValue = value;
        mCurrency = currency;
        mRaw = raw;
        mName = name;
    }

    protected Price(Parcel in) {
        mSymbol = in.readString();
        mValue = in.readString();
        mCurrency = in.readString();
        mRaw = in.readString();
        mName = in.readString();
    }

    public static final Creator<Price> CREATOR = new Creator<Price>() {
        @Override
        public Price createFromParcel(Parcel in) {
            return new Price(in);
        }

        @Override
        public Price[] newArray(int size) {
            return new Price[size];
        }
    };

    public String getSymbol() {
        return mSymbol;
    }

    public String getValue() {
        return mValue;
    }

    public String getCurrency() {
        return mCurrency;
    }

    public String getRaw() {
        return mRaw;
    }

    public String getName() {
        return mName;
    }

    @NonNull
    @Override
    public String toString() {
        return "symbol: " + mSymbol +
                "\nvalue: " + mValue +
                "\ncurrency: " + mCurrency +
                "\nraw: " + mRaw +
                "\nname: " + mName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSymbol);
        dest.writeString(mValue);
        dest.writeString(mCurrency);
        dest.writeString(mRaw);
        dest.writeString(mName);
    }
}
