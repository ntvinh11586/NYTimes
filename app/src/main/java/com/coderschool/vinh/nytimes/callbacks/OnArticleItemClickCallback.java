package com.coderschool.vinh.nytimes.callbacks;

import android.support.annotation.NonNull;

import com.coderschool.vinh.nytimes.models.Article;

public interface OnArticleItemClickCallback {
    void onArticleItemClick(@NonNull Article article);
}
