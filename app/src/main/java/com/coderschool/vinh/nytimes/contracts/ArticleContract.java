package com.coderschool.vinh.nytimes.contracts;

import com.coderschool.vinh.nytimes.BasePresenter;
import com.coderschool.vinh.nytimes.BaseView;
import com.coderschool.vinh.nytimes.models.SearchResponse;

public interface ArticleContract {
    interface View extends BaseView<Presenter> {
        void showSuccessfullyLoadedArticle(SearchResponse searchResponse);

        void toggleBodyProgressBar();

        void toggleFooterProgressBar();
    }

    interface Presenter extends BasePresenter {
    }
}