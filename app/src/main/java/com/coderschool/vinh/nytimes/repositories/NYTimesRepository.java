package com.coderschool.vinh.nytimes.repositories;

import android.support.annotation.NonNull;

import com.coderschool.vinh.nytimes.callbacks.GetArticleCallback;
import com.coderschool.vinh.nytimes.models.SearchRequest;

public interface NYTimesRepository {
    void getArticle(@NonNull SearchRequest searchRequest,
                    @NonNull GetArticleCallback getArticleCallback);
}