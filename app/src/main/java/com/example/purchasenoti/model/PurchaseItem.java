package com.example.purchasenoti.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.purchasenoti.utilities.DateUtils;

import java.util.Locale;

@Entity(tableName = "purchase_item")
public class PurchaseItem implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private int mId;

    @ColumnInfo(name = "item_name")
    private String mItemName;

    @ColumnInfo(name = "purchase_term_year")
    private int mPurchaseTermYear;

    @ColumnInfo(name = "purchase_term_month")
    private int mPurchaseTermMonth;

    @ColumnInfo(name = "purchase_term_day")
    private int mPurchaseTermDay;

    @ColumnInfo(name = "last_purchased_date")
    private String mLastPurchasedDate;

    @Ignore
    private String mNextPurchaseDate;

    public PurchaseItem(int id, String itemName, int purchaseTermYear, int purchaseTermMonth, int purchaseTermDay, String lastPurchasedDate) {
        mId = id;
        mItemName = itemName;
        mPurchaseTermYear = purchaseTermYear;
        mPurchaseTermMonth = purchaseTermMonth;
        mPurchaseTermDay = purchaseTermDay;
        mLastPurchasedDate = lastPurchasedDate;
    }

    @Ignore
    public PurchaseItem(String itemName, int purchaseTermYear, int purchaseTermMonth, int purchaseTermDay, String lastPurchasedDate) {
        mItemName = itemName;
        mPurchaseTermYear = purchaseTermYear;
        mPurchaseTermMonth = purchaseTermMonth;
        mPurchaseTermDay = purchaseTermDay;
        mLastPurchasedDate = lastPurchasedDate;
    }

    @Ignore
    protected PurchaseItem(Parcel in) {
        mId = in.readInt();
        mItemName = in.readString();
        mPurchaseTermYear = in.readInt();
        mPurchaseTermMonth = in.readInt();
        mPurchaseTermDay = in.readInt();
        mLastPurchasedDate = in.readString();
        mNextPurchaseDate = in.readString();
    }

    public static final Creator<PurchaseItem> CREATOR = new Creator<PurchaseItem>() {
        @Override
        public PurchaseItem createFromParcel(Parcel in) {
            return new PurchaseItem(in);
        }

        @Override
        public PurchaseItem[] newArray(int size) {
            return new PurchaseItem[size];
        }
    };

    @NonNull
    @Override
    public String toString() {
        return "_id: " + mId +
                "\nitem name: " + mItemName +
                "\npurchase term: " + mPurchaseTermYear + " year(s) " + mPurchaseTermMonth + " month(s) " + mPurchaseTermDay + " day(s)" +
                "\nlast purchased date: " + mLastPurchasedDate +
                "\nnext purchase date: " + mNextPurchaseDate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mItemName);
        dest.writeInt(mPurchaseTermYear);
        dest.writeInt(mPurchaseTermMonth);
        dest.writeInt(mPurchaseTermDay);
        dest.writeString(mLastPurchasedDate);
        dest.writeString(mNextPurchaseDate);
    }

    public int getId() {
        return mId;
    }

    public String getItemName() {
        return mItemName;
    }

    public int getPurchaseTermYear() {
        return mPurchaseTermYear;
    }

    public int getPurchaseTermMonth() {
        return mPurchaseTermMonth;
    }

    public int getPurchaseTermDay() {
        return mPurchaseTermDay;
    }

    public String getLastPurchasedDate() {
        return mLastPurchasedDate;
    }

    public String getNextPurchaseDate() {
        if (TextUtils.isEmpty(mNextPurchaseDate)) {
            calculateNextPurchaseDate();
        }

        return mNextPurchaseDate;
    }

    private void calculateNextPurchaseDate() {
        String nextDate = DateUtils.getNextDate(mLastPurchasedDate,
                mPurchaseTermYear, mPurchaseTermMonth, mPurchaseTermDay);

        mNextPurchaseDate = String.format(Locale.getDefault(), "%s  %s",
                nextDate, DateUtils.getDDayString(nextDate));
    }
}