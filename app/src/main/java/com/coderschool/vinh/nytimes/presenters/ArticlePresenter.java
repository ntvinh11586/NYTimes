package com.coderschool.vinh.nytimes.presenters;

import com.coderschool.vinh.nytimes.contracts.ArticleContract;

public class ArticlePresenter implements ArticleContract.Presenter {
    private ArticleContract.View view;

    public ArticlePresenter(ArticleContract.View view) {
        this.view = view;
        view.setPresenter(this);
    }

    @Override
    public void start() {

    }
}
