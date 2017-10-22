package com.coderschool.vinh.nytimes.datas;

import android.support.annotation.NonNull;

import com.coderschool.vinh.nytimes.api.ArticleApi;
import com.coderschool.vinh.nytimes.callbacks.GetArticleCallback;
import com.coderschool.vinh.nytimes.models.SearchRequest;
import com.coderschool.vinh.nytimes.models.SearchResponse;
import com.coderschool.vinh.nytimes.repositories.NYTimesRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NYTimesRepositoryImpl implements NYTimesRepository {
    private ArticleApi articleApi;

    public NYTimesRepositoryImpl(ArticleApi articleApi) {
        this.articleApi = articleApi;
    }

    @Override
    public void getArticle(@NonNull SearchRequest searchRequest,
                           @NonNull GetArticleCallback getArticleCallback) {
        articleApi.search(searchRequest.getParam())
                .enqueue(new Callback<SearchResponse>() {
                    @Override
                    public void onResponse(Call<SearchResponse> call,
                                           Response<SearchResponse> response) {
                        getArticleCallback.onResult(response.body());
                    }

                    @Override
                    public void onFailure(Call<SearchResponse> call,
                                          Throwable t) {
                    }
                });
    }
}
