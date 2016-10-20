package com.coderschool.vinh.nytimes.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.coderschool.vinh.nytimes.Article;
import com.coderschool.vinh.nytimes.ArticleArrayAdapter;
import com.coderschool.vinh.nytimes.Filter;
import com.coderschool.vinh.nytimes.ItemClickSupport;
import com.coderschool.vinh.nytimes.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    RecyclerView rvResult;

    ArrayList<Article> articles;
    ArticleArrayAdapter adapter;

    String searchQuery = "";
    private final int REQUEST_CODE = 1;

    private Filter searchFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        getSupportActionBar().setTitle("NYTimesSearch");

        setupViews();

        onArticleSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                searchQuery = query;
                Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();

                onArticleSearch();

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
        // Handle presses on the action bar items
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
            searchFilter = (Filter) Parcels.unwrap(data.getParcelableExtra("filter"));
            onArticleSearch();
        }
    }

    public void setupViews() {
        rvResult = (RecyclerView)findViewById(R.id.rvContacts);

        articles = new ArrayList<>();

        adapter = new ArticleArrayAdapter(this, articles);
        rvResult.setAdapter(adapter);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        rvResult.setLayoutManager(gridLayoutManager);

        ItemClickSupport.addTo(rvResult).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Article article = articles.get(position);

                        Intent i = new Intent(getApplicationContext(), ArticleActivity.class);
                        i.putExtra("article", Parcels.wrap(article));
                        startActivity(i);
                    }
                }
        );
    }

    public void onArticleSearch() {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";

        RequestParams params = new RequestParams();
        params.put("api-key", "51065f56d04445baa91280fa70489e8e");
        params.put("page", 0);

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

        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Log.d("DEBUG", response.toString());
                JSONArray articleJsonResults = null;

                try {
                    articles.clear();
                    adapter.notifyDataSetChanged();

                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");

                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    adapter.notifyDataSetChanged();
                    rvResult.scrollToPosition(0);

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
