package com.coderschool.vinh.nytimes.contracts;

import com.coderschool.vinh.nytimes.BasePresenter;
import com.coderschool.vinh.nytimes.BaseView;

public interface ArticleContract {
    interface View extends BaseView<Presenter> {
    }

    interface Presenter extends BasePresenter {
    }
}
