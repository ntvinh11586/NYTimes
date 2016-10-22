package com.coderschool.vinh.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
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
import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

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
                Intent i = new Intent(MainActivity.this, SearchFilterActivity.class);
                startActivityForResult(i, REQUEST_CODE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            searchFilter = Parcels.unwrap(data.getParcelableExtra("filter"));
            onArticleSearch(0);
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
                        Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                        intent.putExtra("article", Parcels.wrap(articles.get(position)));
                        startActivity(intent);
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

}

