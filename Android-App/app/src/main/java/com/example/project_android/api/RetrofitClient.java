package com.example.project_android.api;


import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://10.0.2.2:12345/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            /*HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                    .build();*/

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .callbackExecutor(Executors.newSingleThreadExecutor())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            // If need to debug add:  retrofit = new Retrofit.Builder()
            //                    .baseUrl(BASE_URL)
            //                    .client(client)
            //                    .callbackExecutor(Executors.newSingleThreadExecutor())
            //                    .addConverterFactory(GsonConverterFactory.create())
            //                    .build();
        }
        return retrofit;
    }

    public static ApiService getApiService() {
        return getRetrofitInstance().create(ApiService.class);
    }
}
