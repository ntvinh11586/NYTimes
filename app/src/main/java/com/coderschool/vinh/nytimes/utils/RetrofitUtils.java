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

import static com.coderschool.vinh.nytimes.utils.Constant.BASE_URL;

/**
 * Created by Vinh on 10/23/2016.
 */

public class RetrofitUtils {
    // Media Type, appropriate to describe
    // the content type of an HTTP request or response body.
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public static Retrofit getArticle() {
        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
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

            // Response of Okhttp3
            ResponseBody body = response.body();
            // We need to build a new response from parsing
            // to json to take only response Object data (pre-processing)
            ApiResponse apiResponse = (new Gson()).fromJson(body.string(), ApiResponse.class);
            // Remember to close
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
                    .addQueryParameter("api_key", Constant.API_KEY)
                    .build();
            request = request.newBuilder()
                    .url(url)
                    .build();
            return chain.proceed(request);
        };
    }

}

