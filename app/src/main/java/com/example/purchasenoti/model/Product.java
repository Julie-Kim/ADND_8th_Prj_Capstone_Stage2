package com.example.purchasenoti.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Product implements Parcelable {

    @SerializedName("title")
    private String mTitle;

    @SerializedName("link")
    private String mLink;

    @SerializedName("image")
    private String mImage;

    @SerializedName("prices")
    private ArrayList<Price> mPrices;

    @SerializedName("rating")
    private float mRating;

    public Product(String title, String link, String image, ArrayList<Price> prices, float rating) {
        mTitle = title;
        mLink = link;
        mImage = image;
        mPrices = prices;
        mRating = rating;
    }

    protected Product(Parcel in) {
        mTitle = in.readString();
        mLink = in.readString();
        mImage = in.readString();
        mPrices = in.createTypedArrayList(Price.CREATOR);
        mRating = in.readFloat();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getTitle() {
        return mTitle;
    }

    public String getLink() {
        return mLink;
    }

    public String getImage() {
        return mImage;
    }

    public ArrayList<Price> getPrices() {
        return mPrices;
    }

    public float getRating() {
        return mRating;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder productString = new StringBuilder("title: " + mTitle);

        productString.append("\nlink: ").append(mLink);

        productString.append("\nimage: ").append(mImage);

        if (mPrices != null && !mPrices.isEmpty()) {
            productString.append("\nprice: ").append(mPrices.get(0).getRaw());
        }

        productString.append("\nrating: ").append(mRating);

        return productString.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeString(mLink);
        dest.writeString(mImage);
        dest.writeTypedList(mPrices);
        dest.writeFloat(mRating);
    }
}
