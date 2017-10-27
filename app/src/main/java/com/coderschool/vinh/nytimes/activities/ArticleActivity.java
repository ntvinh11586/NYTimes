package com.coderschool.vinh.nytimes.activities;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.view.View;
import android.widget.ProgressBar;

import com.coderschool.vinh.nytimes.R;
import com.coderschool.vinh.nytimes.adapters.ArticleArrayAdapter;
import com.coderschool.vinh.nytimes.api.ArticleApi;
import com.coderschool.vinh.nytimes.contracts.ArticleContract;
import com.coderschool.vinh.nytimes.datas.CurrentPageRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.NYTimesRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.SearchRequestRepositoryImpl;
import com.coderschool.vinh.nytimes.fragments.FilterDialog;
import com.coderschool.vinh.nytimes.models.Article;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchResponse;
import com.coderschool.vinh.nytimes.presenters.ArticlePresenter;
import com.coderschool.vinh.nytimes.repositories.CurrentPageRepository;
import com.coderschool.vinh.nytimes.repositories.NYTimesRepository;
import com.coderschool.vinh.nytimes.repositories.SearchRequestRepository;
import com.coderschool.vinh.nytimes.utils.ItemClickSupport;
import com.coderschool.vinh.nytimes.utils.RetrofitUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity
        implements ArticleContract.View,
        FilterDialog.FilterDialogListener {
    @BindView(R.id.recycle_view_results)
    RecyclerView rvResult;
    @BindView(R.id.pbLoadMore)
    ProgressBar pbFooter;
    @BindView(R.id.pbLoading)
    ProgressBar pbBody;
    SearchView searchView;

    private ArticleArrayAdapter adapter;

    private ArticleContract.Presenter presenter;

    private NYTimesRepository nyTimesRepository;
    private CurrentPageRepository currentPageRepository;
    private SearchRequestRepository searchRequestRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setupRecycleView();

        setBodyProgressBar(View.VISIBLE);

        // INJECT CODE
        ArticleApi mArticleApi = RetrofitUtils.getArticle()
                .create(ArticleApi.class);
        nyTimesRepository = new NYTimesRepositoryImpl(mArticleApi);
        currentPageRepository = new CurrentPageRepositoryImpl();

        SharedPreferences pref = getApplicationContext()
                .getSharedPreferences("Settings", Context.MODE_PRIVATE);

        searchRequestRepository = new SearchRequestRepositoryImpl(
                pref,
                currentPageRepository,
                new Gson()
        );

        presenter = new ArticlePresenter(
                nyTimesRepository,
                currentPageRepository,
                searchRequestRepository,
                this
        );
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
        rvResult.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
        );
        rvResult.setLayoutManager(gridLayoutManager);

        adapter.setOnLoadMoreListener(() -> {
            presenter.fetchMoreArticles();
        });

        ItemClickSupport.addTo(rvResult)
                .setOnItemClickListener((recyclerView, position, v)
                        -> loadWebView(position));
    }

    // TODO: 21/10/17 Need to refactor loadWebView
    private void loadWebView(int position) {
        PendingIntent pendingIntent = getShareIntentAction(
                adapter.getArticle(position).getWebUrl());

        CustomTabsIntent.Builder customTabsIntent = new CustomTabsIntent.Builder();
        customTabsIntent.setToolbarColor(
                ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                .setActionButton(getBitmap(getBaseContext(), R.drawable.ic_menu_share),
                        "Share Link", pendingIntent, true)
                .build()
                .launchUrl(ArticleActivity.this,
                        Uri.parse(adapter.getArticle(position).getWebUrl()));
    }

    PendingIntent getShareIntentAction(String url) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, url);
        int requestCode = 100;

        return PendingIntent.getActivity(getBaseContext(),
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private Bitmap getBitmap(Context context, int drawableId) {
        Drawable drawable = ContextCompat.getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            return getBitmap((VectorDrawable) drawable);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
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
        rvResult.scrollToPosition(0);
    }
}