package com.coderschool.vinh.nytimes.utils;

import com.coderschool.vinh.nytimes.models.ApiResponse;
import com.google.gson.Gson;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Vinh on 10/23/2016.
 */

public class RetrofitUtils {

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    public static final Gson GSON = new Gson();

    public static Retrofit getArticle() {
        return new Retrofit.Builder()
                .baseUrl("https://api.nytimes.com/svc/search/v2/")
                .client(client())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static OkHttpClient client() {
        return new OkHttpClient.Builder()
                .addInterceptor(apiKeyInterceptor())
                .addInterceptor(responseInterceptor())
                .build();
    }

    private static Interceptor responseInterceptor() {
        return chain -> {
            Request request = chain.request();
            Response response = chain.proceed(request);
            ResponseBody body = response.body();
            ApiResponse apiResponse = GSON.fromJson(body.string(), ApiResponse.class);
            body.close();
            return response.newBuilder()
                    .body(ResponseBody.create(JSON, apiResponse.getResponse().toString()))
                    .build();
        };
    }

    private static Interceptor apiKeyInterceptor() {
        return chain -> {
            Request request = chain.request();
            HttpUrl url = request.url()
                    .newBuilder()
                    .addQueryParameter("api_key", "51065f56d04445baa91280fa70489e8e")
                    .build();
            request = request.newBuilder()
                    .url(url)
                    .build();
            return chain.proceed(request);
        };
    }

}

