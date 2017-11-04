package com.coderschool.vinh.nytimes.utils;

public class RetrofitUtils {
//    public static Retrofit getArticle() {
//        return new Retrofit.Builder()
//                .baseUrl(BaseConstant.API_BASE_URL)
//                .client(client())
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//    }
//
//    private static OkHttpClient client() {
//        return new OkHttpClient.Builder()
//                .addInterceptor(apiKeyInterceptor())
//                .addInterceptor(responseInterceptor())
//                .build();
//    }
//
//    private static Interceptor responseInterceptor() {
//        return chain -> {
//            Request request = chain.request();
//            Response response = chain.proceed(request);
//
//            // Response of Okhttp3
//            ResponseBody body = response.body();
//            // We need to build a new response from parsing
//            // to json to take only response Object data (pre-processing)
//            ApiResponse apiResponse = (new Gson()).fromJson(body.string(), ApiResponse.class);
//            // Remember to close
//            body.close();
//
//            // Prepare data body
//            MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
//            JsonObject responseJSON = apiResponse.getResponse();
//
//            return response.newBuilder()
//                    .body(ResponseBody.create(jsonMediaType, responseJSON.toString()))
//                    .build();
//        };
//    }
//
//    private static Interceptor apiKeyInterceptor() {
//        return chain -> {
//            Request request = chain.request();
//            HttpUrl url = request.url()
//                    .newBuilder()
//                    .addQueryParameter("api_key", BaseConstant.API_KEY)
//                    .build();
//            request = request.newBuilder()
//                    .url(url)
//                    .build();
//            return chain.proceed(request);
//        };
//    }
}

