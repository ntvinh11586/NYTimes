package com.coderschool.vinh.nytimes.api;

import com.coderschool.vinh.nytimes.models.SearchResponse;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

/**
 * Created by Vinh on 10/23/2016.
 */

public interface ArticleApi {

    @GET("articlesearch.json")
    Call<SearchResponse> search(@QueryMap(encoded = true) Map<String, String> options);
}
