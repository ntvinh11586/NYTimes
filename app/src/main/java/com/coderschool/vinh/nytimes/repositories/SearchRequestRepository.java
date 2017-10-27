package com.coderschool.vinh.nytimes.repositories;

import android.support.annotation.NonNull;

import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchRequest;

public interface SearchRequestRepository {
    SearchRequest getSearchRequest();
    void setSearchRequestFilter(Filter filter);
    void setSearchQuery(@NonNull String searchQuery);
    void clearSearchRequest();
}
