package com.coderschool.vinh.nytimes.utils;

import com.coderschool.vinh.nytimes.models.ApiResponse;
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

public class RetrofitUtils {
    public static Retrofit getArticle() {
        return new Retrofit.Builder()
                .baseUrl(Constant.API_BASE_URL)
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

            // Prepare data body
            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
            JsonObject responseJSON = apiResponse.getResponse();

            return response.newBuilder()
                    .body(ResponseBody.create(jsonMediaType, responseJSON.toString()))
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

