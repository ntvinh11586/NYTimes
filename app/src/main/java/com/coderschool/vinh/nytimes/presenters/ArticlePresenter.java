package com.coderschool.vinh.nytimes.presenters;

import com.coderschool.vinh.nytimes.contracts.ArticleContract;
import com.coderschool.vinh.nytimes.models.SearchRequest;
import com.coderschool.vinh.nytimes.repositories.NYTimesRepository;

public class ArticlePresenter implements ArticleContract.Presenter {
    private NYTimesRepository nyTimesRepository;
    private ArticleContract.View view;

    public ArticlePresenter(NYTimesRepository nyTimesRepository,
                            ArticleContract.View view) {
        this.nyTimesRepository = nyTimesRepository;
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
