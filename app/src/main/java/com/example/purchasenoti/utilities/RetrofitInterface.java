package com.example.purchasenoti.utilities;

import com.example.purchasenoti.model.SearchResults;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("request?")
    Call<SearchResults> geProductList(@Query("api_key") String apiKey,
                                      @Query("type") String type,
                                      @Query("amazon_domain") String domain,
                                      @Query("search_term") String searchTerm,
                                      @Query("sort_by") String sortBy);
}