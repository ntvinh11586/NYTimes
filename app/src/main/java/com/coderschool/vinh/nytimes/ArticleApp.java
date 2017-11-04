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
    public ArticlePresenter injectArticlePresenter(ArticleContract.View view) {
        ArticleApi mArticleApi = RetrofitUtils.getArticle()
                .create(ArticleApi.class);
        NYTimesRepository nyTimesRepository = new NYTimesRepositoryImpl(mArticleApi);
        CurrentPageRepository currentPageRepository = new CurrentPageRepositoryImpl();

        SharedPreferences pref = getApplicationContext()
                .getSharedPreferences("Settings", Context.MODE_PRIVATE);

        SearchRequestRepository searchRequestRepository = new SearchRequestRepositoryImpl(
                pref,
                currentPageRepository,
                new Gson()
        );

        return new ArticlePresenter(
                nyTimesRepository,
                currentPageRepository,
                searchRequestRepository,
                view
        );
    }
}
