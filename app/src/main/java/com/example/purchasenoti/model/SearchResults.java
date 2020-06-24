package com.example.purchasenoti.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SearchResults {

    @SerializedName("search_results")
    private ArrayList<Product> mProducts;

    public ArrayList<Product> getProducts() {
        return mProducts;
    }
}
