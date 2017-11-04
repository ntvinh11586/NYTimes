package com.coderschool.vinh.nytimes.activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.coderschool.vinh.nytimes.ArticleApp;
import com.coderschool.vinh.nytimes.R;
import com.coderschool.vinh.nytimes.adapters.ArticleArrayAdapter;
import com.coderschool.vinh.nytimes.contracts.ArticleContract;
import com.coderschool.vinh.nytimes.fragments.FilterDialog;
import com.coderschool.vinh.nytimes.models.Article;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchResponse;
import com.coderschool.vinh.nytimes.utils.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.coderschool.vinh.nytimes.utils.DrawableMapper.toBitMap;

public class ArticleActivity extends AppCompatActivity
        implements ArticleContract.View,
        FilterDialog.FilterDialogListener {

    private int WEB_VIEW_SHARE_ACTION = 100;

    @BindView(R.id.recycle_view_results)
    RecyclerView rvArticles;
    @BindView(R.id.pbLoadMore)
    ProgressBar pbFooter;
    @BindView(R.id.pbLoading)
    ProgressBar pbBody;
    SearchView searchView;

    private ArticleArrayAdapter adapter;

    private ArticleContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecycleView();

        presenter = ((ArticleApp) getApplication())
                .injectArticlePresenter(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setOnCloseListener(() -> {
            presenter.fetchMainArticles();
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                presenter.fetchSearchArticles(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search_filter:
                FragmentManager fm = getSupportFragmentManager();
                FilterDialog filterDialog = FilterDialog.newInstance();
                filterDialog.show(fm, "fragment_search_filter");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setupRecycleView() {
        adapter = new ArticleArrayAdapter(this, new ArrayList<>());
        rvArticles.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
        );
        rvArticles.setLayoutManager(gridLayoutManager);

        adapter.setOnLoadMoreListener(()
                -> presenter.fetchMoreArticles());

        ItemClickSupport
                .addTo(rvArticles)
                .setOnItemClickListener((recyclerView, position, v) -> {
                    Article article = adapter.getArticle(position);
                    if (article != null) {
                        loadWebView(article.getWebUrl());
                    }
                });
    }

    private void loadWebView(@NonNull String url) {
        new CustomTabsIntent
                .Builder()
                .setToolbarColor(ContextCompat.getColor(
                        this,
                        R.color.colorPrimary
                ))
                .setActionButton(
                        toBitMap(ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_menu_share
                        )),
                        getString(R.string.share_link),
                        getShareIntentAction(url),
                        true
                )
                .build()
                .launchUrl(
                        this,
                        Uri.parse(url)
                );
    }

    private PendingIntent getShareIntentAction(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);

        return PendingIntent.getActivity(
                this,
                WEB_VIEW_SHARE_ACTION,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    @Override
    public void onFinishFilterDialog(Filter filter) {
        presenter.fetchArticlesWithFilter(filter);
    }

    @Override
    public void setPresenter(ArticleContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onArticlesLoaded(SearchResponse searchResponse) {
        if (searchResponse != null) {
            List<Article> articlesResult = searchResponse.getArticles();
            adapter.clearAll();
            adapter.addAll(articlesResult);
        }
    }

    @Override
    public void onArticlesLoadedMore(SearchResponse searchResponse) {
        if (searchResponse != null) {
            List<Article> articlesResult = searchResponse.getArticles();
            adapter.addAll(articlesResult);
        }
    }

    @Override
    public void setBodyProgressBar(int visibility) {
        pbBody.setVisibility(visibility);
    }

    @Override
    public void setFooterProgressBar(int visibility) {
        pbFooter.setVisibility(visibility);
    }

    @Override
    public void clearFocusedSearch() {
        if (searchView != null) {
            searchView.clearFocus();
        }
    }

    @Override
    public void scrollToTopPosition() {
        rvArticles.scrollToPosition(0);
    }
}