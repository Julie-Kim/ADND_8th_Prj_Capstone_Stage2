package com.example.purchasenoti.utilities;

import com.example.purchasenoti.BuildConfig;

public class JsonQueryUtils {

    private static final String API_KEY = BuildConfig.API_KEY;

    private static final String QUERY_TYPE = "search";

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

    //Sort by
    private static final String MOST_RECENT = "most_recent";
    private static final String PRICE_LOW_TO_HIGH = "price_low_to_high";
    private static final String PRICE_HIGH_TO_LOW = "price_high_to_low";
    private static final String FEATURED = "featured";
    private static final String AVERAGE_REVIEW = "average_review";

    public static String getApiKey() {
        return API_KEY;
    }

    public static String getQueryType() {
        return QUERY_TYPE;
    }

    public static String getAmazonDomain() {
        return AMAZON_US;   //TODO: get from setting preference
    }

    public static String getSortBy() {
        return MOST_RECENT;   //TODO: get from setting preference
    }
}
