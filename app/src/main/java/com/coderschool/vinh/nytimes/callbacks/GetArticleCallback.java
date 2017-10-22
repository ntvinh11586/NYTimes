package com.coderschool.vinh.nytimes.callbacks;

import com.coderschool.vinh.nytimes.models.SearchResponse;

public interface GetArticleCallback {
    void onResult(SearchResponse searchResponse);
}