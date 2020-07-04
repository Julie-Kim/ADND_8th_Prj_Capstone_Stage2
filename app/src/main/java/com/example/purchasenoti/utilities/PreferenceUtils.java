package com.example.purchasenoti.utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.purchasenoti.model.PurchaseItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public class PreferenceUtils {

    private static final String PREF_NAME = "pref";

    private static final String PREF_KEY_AMAZON_DOMAIN = "amazon_domain";
    private static final String PREF_KEY_SORT_BY = "sort_by";

    private static final int DEFAULT_VALUE = 0;

    //Amazon domain
    private static final String AMAZON_AU = "amazon.com.au";   //Australia
    private static final String AMAZON_BR = "amazon.com.br";  //Brazil
    private static final String AMAZON_CA = "amazon.ca";  //Canada
    private static final String AMAZON_FR = "amazon.fr";  //France
    private static final String AMAZON_DE = "amazon.de";  //Germany
    private static final String AMAZON_IN = "amazon.in";  //India
    private static final String AMAZON_IT = "amazon.it";  //Italy
    private static final String AMAZON_JP = "amazon.co.jp";  //Japan
    private static final String AMAZON_MX = "amazon.com.mx";  //Mexico
    private static final String AMAZON_NL = "amazon.nl";  //Netherlands
    private static final String AMAZON_ES = "amazon.es";  //Spain
    private static final String AMAZON_AE = "amazon.ae";  //UAE
    private static final String AMAZON_UK = "amazon.co.uk";  //United Kingdom
    private static final String AMAZON_US = "amazon.com";  //United States

    private static final String[] AMAZON_DOMAIN = new String[]{
            AMAZON_AU,
            AMAZON_BR,
            AMAZON_CA,
            AMAZON_FR,
            AMAZON_DE,
            AMAZON_IN,
            AMAZON_IT,
            AMAZON_JP,
            AMAZON_MX,
            AMAZON_NL,
            AMAZON_ES,
            AMAZON_AE,
            AMAZON_UK,
            AMAZON_US
    };

    //Sort by
    private static final String MOST_RECENT = "most_recent";
    private static final String PRICE_LOW_TO_HIGH = "price_low_to_high";
    private static final String PRICE_HIGH_TO_LOW = "price_high_to_low";
    private static final String FEATURED = "featured";
    private static final String AVERAGE_REVIEW = "average_review";

    private static final String[] SORT_BY = new String[]{
            MOST_RECENT,
            PRICE_LOW_TO_HIGH,
            PRICE_HIGH_TO_LOW,
            FEATURED,
            AVERAGE_REVIEW
    };

    public static void setAmazonDomainSettingValue(Context context, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_KEY_AMAZON_DOMAIN, value);
        editor.apply();
    }

    public static int getAmazonDomainSettingValue(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int value = pref.getInt(PREF_KEY_AMAZON_DOMAIN, DEFAULT_VALUE);

        if (value < DEFAULT_VALUE) {
            value = DEFAULT_VALUE;
        }
        return value;
    }

    public static String getAmazonDomainSetting(Context context) {
        return AMAZON_DOMAIN[getAmazonDomainSettingValue(context)];
    }

    public static void setSortBySettingValue(Context context, int value) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt(PREF_KEY_SORT_BY, value);
        editor.apply();
    }

    public static int getSortBySettingValue(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        int value = pref.getInt(PREF_KEY_SORT_BY, DEFAULT_VALUE);

        if (value < DEFAULT_VALUE) {
            value = DEFAULT_VALUE;
        }
        return value;
    }

    public static String getSortBySetting(Context context) {
        return SORT_BY[getSortBySettingValue(context)];
    }

    private static final String PREF_WIDGET = "pref_widget";

    private static final String PREF_KEY_ITEMS = "items";

    public static void setPurchaseItemListForWidget(Context context, ArrayList<PurchaseItem> purchaseItems) {
        Set<String> widgetItemList = new HashSet<>();
        for (PurchaseItem item : purchaseItems) {
            widgetItemList.add(item.getItemName() + "  " + DateUtils.getDDayString(item.getNextPurchaseDate()));
        }

        SharedPreferences pref = context.getSharedPreferences(PREF_WIDGET, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putStringSet(PREF_KEY_ITEMS, widgetItemList);
        editor.apply();
    }

    public static ArrayList<String> getPurchaseItemListForWidget(Context context) {
        SharedPreferences pref = context.getSharedPreferences(PREF_WIDGET, MODE_PRIVATE);
        Set<String> items = pref.getStringSet(PREF_KEY_ITEMS, new HashSet<>());
        return new ArrayList<>(items);
    }
}
