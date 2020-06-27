package com.example.purchasenoti.utilities;

import android.content.Context;

import com.example.purchasenoti.BuildConfig;

public class JsonQueryUtils {

    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String QUERY_TYPE = "search";

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getQueryType() {
        return QUERY_TYPE;
    }

    public static String getAmazonDomain(Context context) {
        return PreferenceUtils.getAmazonDomainSetting(context);
    }

    public static String getSortBy(Context context) {
        return PreferenceUtils.getSortBySetting(context);
    }
}
