package com.coderschool.vinh.nytimes;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.coderschool.vinh.nytimes.api.ArticleApi;
import com.coderschool.vinh.nytimes.contracts.ArticleContract;
import com.coderschool.vinh.nytimes.datas.CurrentPageRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.NYTimesRepositoryImpl;
import com.coderschool.vinh.nytimes.datas.SearchRequestRepositoryImpl;
import com.coderschool.vinh.nytimes.models.ApiResponse;
import com.coderschool.vinh.nytimes.presenters.ArticlePresenter;
import com.coderschool.vinh.nytimes.repositories.CurrentPageRepository;
import com.coderschool.vinh.nytimes.repositories.NYTimesRepository;
import com.coderschool.vinh.nytimes.repositories.SearchRequestRepository;
import com.coderschool.vinh.nytimes.utils.BaseConstant;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticleApp extends Application {

    private Retrofit getArticleAPI() {
        return new Retrofit.Builder()
                .baseUrl(BaseConstant.API_BASE_URL)
                .client(client())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private OkHttpClient client() {
        return new OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor())
                .addInterceptor(responseInterceptor())
                .build();
    }

    private Interceptor responseInterceptor() {
        return chain -> {
            Request request = chain.request();
            Response response = chain.proceed(request);

            // Response of Okhttp3
            ResponseBody body = response.body();
            // We need to build a new response from parsing
            // to json to take only response Object data (pre-processing)
            ApiResponse apiResponse = (new Gson()).fromJson(body.string(), ApiResponse.class);
            // Remember to close
            body.close();

            // Prepare data body
            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            JsonObject responseJSON = apiResponse.getResponse();

            return response.newBuilder()
                    .body(ResponseBody.create(jsonMediaType, responseJSON.toString()))
                    .build();
        };
    }

    private Interceptor apiKeyInterceptor() {
        return chain -> {
            Request request = chain.request();
            HttpUrl url = request.url()
                    .newBuilder()
                    .addQueryParameter("api_key", BaseConstant.API_KEY)
                    .build();
            request = request.newBuilder()
                    .url(url)
                    .build();
            return chain.proceed(request);
        };
    }


    private NYTimesRepository getNYTimesRepository() {
        ArticleApi mArticleApi = getArticleAPI()
                .create(ArticleApi.class);
        return new NYTimesRepositoryImpl(mArticleApi);
    }

    private CurrentPageRepository getCurrentPageRepository() {
        return new CurrentPageRepositoryImpl();
    }

    private SharedPreferences getSettingsPref() {
        return getApplicationContext()
                .getSharedPreferences("Settings", Context.MODE_PRIVATE);
    }

    private Gson getGson() {
        return new Gson();
    }

    private SearchRequestRepository getSearchRequestRepository() {
        return new SearchRequestRepositoryImpl(
                getSettingsPref(),
                getCurrentPageRepository(),
                getGson()
        );
    }

    public ArticlePresenter injectArticlePresenter(ArticleContract.View view) {
        return new ArticlePresenter(
                getNYTimesRepository(),
                getCurrentPageRepository(),
                getSearchRequestRepository(),
                view
        );
    }
}
