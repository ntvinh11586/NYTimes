package com.coderschool.vinh.nytimes.presenters;

import android.view.View;

import com.coderschool.vinh.nytimes.contracts.ArticleContract;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchRequest;
import com.coderschool.vinh.nytimes.repositories.CurrentPageRepository;
import com.coderschool.vinh.nytimes.repositories.NYTimesRepository;
import com.coderschool.vinh.nytimes.repositories.SearchRequestRepository;

public class ArticlePresenter implements ArticleContract.Presenter {
    private NYTimesRepository nyTimesRepository;
    private CurrentPageRepository currentPageRepository;
    private SearchRequestRepository searchRequestRepository;

    private ArticleContract.View view;

    public ArticlePresenter(NYTimesRepository nyTimesRepository,
                            CurrentPageRepository currentPageRepository,
                            SearchRequestRepository searchRequestRepository,
                            ArticleContract.View view) {
        this.nyTimesRepository = nyTimesRepository;
        this.currentPageRepository = currentPageRepository;
        this.searchRequestRepository = searchRequestRepository;
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {
        view.setBodyProgressBar(View.VISIBLE);
        fetchMainArticles();
    }

    private void fetchArticles() {
        SearchRequest searchRequest = searchRequestRepository.getSearchRequest();
        nyTimesRepository.getArticle(searchRequest, searchResponse -> {
            view.setBodyProgressBar(View.GONE);
            view.setFooterProgressBar(View.GONE);
            view.showSuccessfullyLoadedArticle(searchResponse);
        });
    }

    @Override
    public void fetchMoreArticles() {
        view.setFooterProgressBar(View.VISIBLE);
        currentPageRepository.moveToNextPage();
        fetchArticles();
    }

    @Override
    public void fetchSearchArticles(String query) {
        currentPageRepository.resetCurrentPage();
        searchRequestRepository.setSearchQuery(query);
        view.setBodyProgressBar(View.VISIBLE);
        fetchArticles();
        view.clearFocusedSearch();
    }

    @Override
    public void fetchArticlesWithFilter(Filter filter) {
        currentPageRepository.resetCurrentPage();
        searchRequestRepository.setSearchRequestFilter(filter);
        view.setBodyProgressBar(View.VISIBLE);
        fetchArticles();
    }

    @Override
    public void fetchMainArticles() {
        searchRequestRepository.clearSearchRequest();
        fetchArticles();
    }
}