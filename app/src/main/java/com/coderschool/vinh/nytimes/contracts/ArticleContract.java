package com.coderschool.vinh.nytimes.contracts;

import com.coderschool.vinh.nytimes.BasePresenter;
import com.coderschool.vinh.nytimes.BaseView;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchResponse;

public interface ArticleContract {

    interface View extends BaseView<Presenter> {

        void onArticlesLoaded(SearchResponse searchResponse);

        void onArticlesLoadedMore(SearchResponse searchResponse);

        void setBodyProgressBar(int visibility);

        void setFooterProgressBar(int visibility);

        void clearFocusedSearch();

        void scrollToTopPosition();
    }

    interface Presenter extends BasePresenter {

        void fetchMoreArticles();

        void fetchSearchArticles(String query);

        void fetchArticlesWithFilter(Filter filter);

        void fetchMainArticles();
    }
}