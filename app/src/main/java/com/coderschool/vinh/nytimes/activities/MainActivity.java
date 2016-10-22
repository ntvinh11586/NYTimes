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
import com.coderschool.vinh.nytimes.fragments.FilterDialogFragment;
import com.coderschool.vinh.nytimes.models.Article;
import com.coderschool.vinh.nytimes.models.Filter;
import com.coderschool.vinh.nytimes.utils.Constant;
import com.coderschool.vinh.nytimes.utils.EndlessRecyclerViewScrollListener;
import com.coderschool.vinh.nytimes.utils.ItemClickSupport;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity implements FilterDialogFragment.FilterDialogListener {

    private final int REQUEST_CODE = 1;

    @BindView(R.id.recycle_view_results)
    RecyclerView rvResult;
    SearchView searchView;

    private ArrayList<Article> articles;
    private ArticleArrayAdapter adapter;

    private String searchQuery = "";
    private Filter searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setRecycleView();
        onArticleSearch(0);
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
                onArticleSearch(0);
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

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        rvResult.setLayoutManager(gridLayoutManager);
        rvResult.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                onArticleSearch(page);
            }

        });

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

    public void onArticleSearch(final int page) {
        AsyncHttpClient client = new AsyncHttpClient();
        if (page == 0) {
            rvResult.scrollToPosition(0);
        }

        RequestParams params = new RequestParams();
        params.put("api-key", Constant.API_KEY);
        params.put("page", page);

        if (searchFilter != null) {
            String day = searchFilter.day >= 10 ?
                    String.valueOf(searchFilter.day) :
                    "0" + String.valueOf(searchFilter.day);
            String month = searchFilter.month >= 10 ?
                    String.valueOf(searchFilter.month) :
                    "0" + String.valueOf(searchFilter.month);
            String year = String.valueOf(searchFilter.year);
            params.put("begin_date", year + month + day);

            if (searchFilter.sortOrder.equals("Newest")) {
                params.put("sort", "newest");
            } else if (searchFilter.sortOrder.equals("Oldest")) {
                params.put("sort", "oldest");
            }

            if (searchFilter.isArts == 1) {
                params.put("fq", "news_desk:(\"Arts\")");
            } else if (searchFilter.isFashionStyle == 1) {
                params.put("fq", "news_desk:(\"Fashion & Style\")");
            } else if (searchFilter.isSports == 1) {
                params.put("fq", "news_desk:(\"Sports\")");
            }
        }

        if (!searchQuery.equals(""))
            params.put("q", searchQuery);

        client.get(Constant.ARTICLE_SEARCH, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults;

                try {
                    if (page == 0) {
                        articles.clear();
                    }
                    adapter.notifyDataSetChanged();
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    if (page == 0) {
                        rvResult.scrollToPosition(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String res, Throwable t) {

            }
        });
    }

    @Override
    public void onFinishFilterDialog(Filter filter) {
        searchFilter = filter;
        onArticleSearch(0);
    }
}

