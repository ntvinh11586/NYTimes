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
import android.widget.Toast;

import com.coderschool.vinh.nytimes.R;
import com.coderschool.vinh.nytimes.adapters.ArticleArrayAdapter;
import com.coderschool.vinh.nytimes.api.ArticleApi;
import com.coderschool.vinh.nytimes.callbacks.GetArticleCallback;
import com.coderschool.vinh.nytimes.contracts.ArticleContract;
import com.coderschool.vinh.nytimes.datas.CurrentPageRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.NYTimesRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.SearchRequestRepositoryImpl;
import com.coderschool.vinh.nytimes.fragments.FilterDialog;
import com.coderschool.vinh.nytimes.models.Article;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchRequest;
import com.coderschool.vinh.nytimes.models.SearchResponse;
import com.coderschool.vinh.nytimes.presenters.ArticlePresenter;
import com.coderschool.vinh.nytimes.repositories.CurrentPageRepository;
import com.coderschool.vinh.nytimes.repositories.NYTimesRepository;
import com.coderschool.vinh.nytimes.repositories.SearchRequestRepository;
import com.coderschool.vinh.nytimes.utils.ItemClickSupport;
import com.coderschool.vinh.nytimes.utils.NetworkHelper;
import com.coderschool.vinh.nytimes.utils.RetrofitUtils;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticleActivity extends AppCompatActivity
        implements ArticleContract.View,
        FilterDialog.FilterDialogListener,
        GetArticleCallback {
    @BindView(R.id.recycle_view_results)
    RecyclerView rvResult;
    @BindView(R.id.pbLoadMore)
    ProgressBar pbLoadMore;
    @BindView(R.id.pbLoading)
    ProgressBar pbLoading;

    private ArrayList<Article> articles;
    private ArticleArrayAdapter adapter;
    private SearchRequest searchRequest;

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

        pbLoading.setVisibility(View.VISIBLE);

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

        // TODO: 22/10/17 searchRequest should be a kind of DTO
        searchRequest = new SearchRequest();
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.start();
    }

    private void search() {
        // TODO: 22/10/17 Should handle network in Service
        if (NetworkHelper.isNetworkAvailable(getBaseContext())
                && NetworkHelper.isOnline()) {
            nyTimesRepository.getArticle(searchRequest, this);
        } else {
            Toast.makeText(this, "Offline", Toast.LENGTH_LONG).show();
            pbLoadMore.setVisibility(View.GONE);
            pbLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnCloseListener(() -> {
            searchRequest.setSearchQuery("");
            return false;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentPageRepository.resetCurrentPage();

                searchRequest.setSearchQuery(query);
                searchRequest.setPage(
                        currentPageRepository.getCurrentPage()
                );
                pbLoading.setVisibility(View.VISIBLE);


                articles.clear();
                adapter.notifyDataSetChanged();

                search();

                searchView.clearFocus();

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
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        rvResult.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvResult.setLayoutManager(gridLayoutManager);

        adapter.setOnLoadMoreListener(() -> {
            pbLoadMore.setVisibility(View.VISIBLE);

            currentPageRepository.moveToNextPage();

            searchRequest.setPage(currentPageRepository.getCurrentPage());
            search();
        });

        ItemClickSupport.addTo(rvResult)
                .setOnItemClickListener(this::loadWebView);
    }

    // TODO: 21/10/17 Need to refactor loadWebView
    private void loadWebView(RecyclerView recyclerView, int position, View v) {
        PendingIntent pendingIntent = getShareIntentAction(
                articles.get(position).getWebUrl());

        CustomTabsIntent.Builder customTabsIntent = new CustomTabsIntent.Builder();
        customTabsIntent.setToolbarColor(
                ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                .setActionButton(getBitmap(getBaseContext(), R.drawable.ic_menu_share),
                        "Share Link", pendingIntent, true)
                .build()
                .launchUrl(ArticleActivity.this,
                        Uri.parse(articles.get(position).getWebUrl()));
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
        currentPageRepository.resetCurrentPage();

        searchRequest.setPage(currentPageRepository.getCurrentPage());
        searchRequest.setSearchFilter(filter);
        pbLoading.setVisibility(View.VISIBLE);
        articles.clear();
        adapter.notifyDataSetChanged();
        search();
    }

    @Override
    public void onResult(SearchResponse searchResponse) {
        pbLoadMore.setVisibility(View.GONE);
        pbLoading.setVisibility(View.GONE);

        if (searchResponse != null) {
            List<Article> articlesResult = searchResponse.getArticles();
            if (currentPageRepository.getCurrentPage() == 0) {
                articles.clear();
            }

            articles.addAll(articlesResult);
            if (currentPageRepository.getCurrentPage() == 0) {
                rvResult.scrollToPosition(0);
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void setPresenter(ArticleContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showSuccessfullyLoadedArticle(SearchResponse searchResponse) {
        if (searchResponse != null) {
            List<Article> articlesResult = searchResponse.getArticles();
            if (currentPageRepository.getCurrentPage() == 0) {
                articles.clear();
            }

            articles.addAll(articlesResult);
            if (currentPageRepository.getCurrentPage() == 0) {
                rvResult.scrollToPosition(0);
            }

            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void toggleBodyProgressBar() {
        if (pbLoading.getVisibility() == View.GONE) {
            pbLoading.setVisibility(View.VISIBLE);
        } else {
            pbLoading.setVisibility(View.GONE);
        }
    }

    @Override
    public void toggleFooterProgressBar() {
        if (pbLoadMore.getVisibility() == View.GONE) {
            pbLoadMore.setVisibility(View.VISIBLE);
        } else {
            pbLoadMore.setVisibility(View.GONE);
        }
    }
}