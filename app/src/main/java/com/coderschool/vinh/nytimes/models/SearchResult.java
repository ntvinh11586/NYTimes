package com.coderschool.vinh.nytimes.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Vinh on 10/23/2016.
 */

public class SearchResult {

    @SerializedName("docs")
    private List<Article> articles;

    public List<Article> getArticles() {
        return articles;
    }
}
