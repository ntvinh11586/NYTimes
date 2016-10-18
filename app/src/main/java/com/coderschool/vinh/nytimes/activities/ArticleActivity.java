package com.coderschool.vinh.nytimes.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.coderschool.vinh.nytimes.Article;
import com.coderschool.vinh.nytimes.R;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        Article article = (Article)getIntent().getSerializableExtra("article");

        WebView webView = (WebView)findViewById(R.id.wvArticle);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            @SuppressWarnings("deprecation")
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        webView.loadUrl(article.getWebUrl());
    }
}
