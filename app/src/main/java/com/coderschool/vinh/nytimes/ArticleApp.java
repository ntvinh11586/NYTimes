package com.coderschool.vinh.nytimes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.coderschool.vinh.nytimes.api.ArticleApi;
import com.coderschool.vinh.nytimes.contracts.ArticleContract;
import com.coderschool.vinh.nytimes.datas.CurrentPageRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.NYTimesRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.SearchRequestRepositoryImpl;
import com.coderschool.vinh.nytimes.presenters.ArticlePresenter;
import com.coderschool.vinh.nytimes.repositories.CurrentPageRepository;
import com.coderschool.vinh.nytimes.repositories.NYTimesRepository;
import com.coderschool.vinh.nytimes.repositories.SearchRequestRepository;
import com.coderschool.vinh.nytimes.utils.RetrofitUtils;
import com.google.gson.Gson;

public class ArticleApp extends Application {
    private NYTimesRepository getNYTimesRepository() {
        ArticleApi mArticleApi = RetrofitUtils.getArticle()
                .create(ArticleApi.class);
        return new NYTimesRepositoryImpl(mArticleApi);
    }

    private CurrentPageRepository getCurrentPageRepository() {
        return new CurrentPageRepositoryImpl();
    }

    private SharedPreferences getSettingsPref() {
        return getApplicationContext()
                .getSharedPreferences("Settings", Context.MODE_PRIVATE);
    }

    private Gson getGson() {
        return new Gson();
    }

    private SearchRequestRepository getSearchRequestRepository() {
        return new SearchRequestRepositoryImpl(
                getSettingsPref(),
                getCurrentPageRepository(),
                getGson()
        );
    }

    public ArticlePresenter injectArticlePresenter(ArticleContract.View view) {
        return new ArticlePresenter(
                getNYTimesRepository(),
                getCurrentPageRepository(),
                getSearchRequestRepository(),
                view
        );
    }
}
