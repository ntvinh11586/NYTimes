package com.coderschool.vinh.nytimes.presenters;

import com.coderschool.vinh.nytimes.contracts.ArticleContract;
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
        fetchArticle();
    }

    private void fetchArticle() {
        SearchRequest searchRequest = new SearchRequest();
        nyTimesRepository.getArticle(searchRequest, searchResponse -> {
            view.toggleBodyProgressBar();
            view.toggleFooterProgressBar();
            view.showSuccessfullyLoadedArticle(searchResponse);
        });
    }
}