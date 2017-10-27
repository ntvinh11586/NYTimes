package com.coderschool.vinh.nytimes.datas;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchRequest;
import com.coderschool.vinh.nytimes.repositories.CurrentPageRepository;
import com.coderschool.vinh.nytimes.repositories.SearchRequestRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class SearchRequestRepositoryImpl implements SearchRequestRepository {
    private static String PREF_FILTER = "PREF_FILTER";
    private static String PREF_SEARCH_QUERY = "PREF_SEARCH_QUERY";

    private SharedPreferences preferences;
    private CurrentPageRepository currentPageRepository;
    private Gson gson;

    public SearchRequestRepositoryImpl(
            SharedPreferences preferences,
            CurrentPageRepository currentPageRepository,
            Gson gson) {
        this.preferences = preferences;
        this.currentPageRepository = currentPageRepository;
        this.gson = gson;
    }

    @Override
    public SearchRequest getSearchRequest() {
        String searchQuery = preferences.getString(PREF_SEARCH_QUERY, "");
        int page = currentPageRepository.getCurrentPage();
        String searchRequestFilter = preferences.getString(PREF_FILTER, null);
        // TODO: 22/10/17 Filter should have default value
        if (searchRequestFilter != null) {
            Filter filter = gson.fromJson(searchRequestFilter, Filter.class);
            return new SearchRequest(page, searchQuery, filter);
        }
        return new SearchRequest(page, searchQuery, null);
    }

    @Override
    public void setSearchRequestFilter(Filter filter) {
        Type type = new TypeToken<Filter>() {}.getType();
        String searchRequestFilter = gson.toJson(filter, type);
        preferences.edit()
                .putString(PREF_FILTER, searchRequestFilter)
                .commit();
    }

    @Override
    public void setSearchQuery(@NonNull String searchQuery) {
        preferences.edit()
                .putString(PREF_SEARCH_QUERY, searchQuery)
                .commit();
    }

    @Override
    public void clearSearchRequest() {
        currentPageRepository.resetCurrentPage();
        preferences.edit().clear();
    }
}