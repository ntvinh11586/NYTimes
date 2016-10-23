package com.coderschool.vinh.nytimes.activities;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.coderschool.vinh.nytimes.R;
import com.coderschool.vinh.nytimes.adapters.ArticleArrayAdapter;
import com.coderschool.vinh.nytimes.api.ArticleApi;
import com.coderschool.vinh.nytimes.fragments.FilterDialogFragment;
import com.coderschool.vinh.nytimes.models.Article;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.models.SearchResult;
import com.coderschool.vinh.nytimes.utils.ItemClickSupport;
import com.coderschool.vinh.nytimes.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogListener {

    private final int REQUEST_CODE = 1;

    @BindView(R.id.recycle_view_results)
    RecyclerView rvResult;
    SearchView searchView;

    private ArrayList<Article> articles;
    private ArticleArrayAdapter adapter;

    private String searchQuery = "";
    private Filter searchFilter;
    private int page = 0;
    private ArticleApi mArticleApi;

    private interface Listener {
        void onResult(SearchResult searchResult);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRecycleView();
        mArticleApi = RetrofitUtils.getArticle().create(ArticleApi.class);

        page = 0;
        search();
    }

    private void search() {

        Map<String, String> map = new HashMap<>();
        map.put("page", String.valueOf(page));

        if (!searchQuery.equals("")) {
            map.put("q", searchQuery);
        }

        if (searchFilter != null) {
            String day = searchFilter.day >= 10 ?
                    String.valueOf(searchFilter.day) :
                    "0" + String.valueOf(searchFilter.day);
            String month = searchFilter.month >= 10 ?
                    String.valueOf(searchFilter.month) :
                    "0" + String.valueOf(searchFilter.month);
            String year = String.valueOf(searchFilter.year);
            map.put("begin_date", year + month + day);

            if (searchFilter.sortOrder.equals("Newest")) {
                map.put("sort", "newest");
            } else if (searchFilter.sortOrder.equals("Oldest")) {
                map.put("sort", "oldest");
            }

            if (searchFilter.isArts == 1) {
                map.put("fq", "news_desk:(\"Arts\")");
            } else if (searchFilter.isFashionStyle == 1) {
                map.put("fq", "news_desk:(\"Fashion & Style\")");
            } else if (searchFilter.isSports == 1) {
                map.put("fq", "news_desk:(\"Sports\")");
            }
        }

        fetchArticles(map, searchResult -> {

            if (searchResult != null) {
                List<Article> articlesResult = searchResult.getArticles();
                if (page == 0) {
                    articles.clear();
                }
                adapter.notifyDataSetChanged();
                articles.addAll(articlesResult);
                if (page == 0) {
                    rvResult.scrollToPosition(0);
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void fetchArticles(Map<String, String> map, Listener listener) {


        mArticleApi.search(map).enqueue(new Callback<SearchResult>() {
            @Override
            public void onResponse(Call<SearchResult> call, Response<SearchResult> response) {
                listener.onResult(response.body());
            }

            @Override
            public void onFailure(Call<SearchResult> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchItem.expandActionView();
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchQuery = query;
                page = 0;
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
                FilterDialogFragment filterDialogFragment = FilterDialogFragment.newInstance();
                filterDialogFragment.show(fm, "fragment_search_filter");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setRecycleView() {
        articles = new ArrayList<>();
        adapter = new ArticleArrayAdapter(this, articles);
        rvResult.setAdapter(adapter);
        adapter.setListener(() -> {
            page++;
            search();
        });
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvResult.setLayoutManager(gridLayoutManager);

        ItemClickSupport.addTo(rvResult).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        PendingIntent pendingIntent = getShareIntentAction(
                                articles.get(position).getWebUrl());

                        CustomTabsIntent.Builder customTabsIntent = new CustomTabsIntent.Builder();
                        customTabsIntent.setToolbarColor(
                                ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                                .setActionButton(getBitmap(getBaseContext(), R.drawable.ic_menu_share),
                                        "Share Link", pendingIntent, true)
                                .build()
                                .launchUrl(MainActivity.this,
                                        Uri.parse(articles.get(position).getWebUrl()));
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
                }
        );
    }

    @Override
    public void onFinishFilterDialog(Filter filter) {
        searchFilter = filter;
        page = 0;
        search();
    }
}

