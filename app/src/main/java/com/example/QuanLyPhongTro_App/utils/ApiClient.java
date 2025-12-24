package com.example.QuanLyPhongTro_App.utils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.concurrent.TimeUnit;

/**
 * Retrofit client setup cho API calls
 * - Base URL: http://18.140.64.80:5000
 * - JWT token injected via Authorization header interceptor
 */
public class ApiClient {
    private static final String BASE_URL = "http://18.140.64.80:5000";
    private static Retrofit retrofit = null;
    private static final Object lock = new Object();
    private static String cachedToken = null;

    /**
     * Lấy Retrofit instance (singleton)
     */
    public static Retrofit getRetrofit() {
        if (retrofit == null) {
            synchronized (lock) {
                if (retrofit == null) {
                    // Setup OkHttp with token interceptor
                    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

                    // Add JWT token interceptor
                    httpClient.addInterceptor(chain -> {
                        okhttp3.Request original = chain.request();

                        // Get token from cached token or use empty
                        String token = cachedToken;

                        okhttp3.Request.Builder requestBuilder = original.newBuilder();
                        if (token != null && !token.isEmpty()) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }
                        requestBuilder.header("Content-Type", "application/json");

                        okhttp3.Request request = requestBuilder.build();
                        return chain.proceed(request);
                    });

                    // Add logging interceptor for debug
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    httpClient.addInterceptor(logging);

                    // Set timeouts
                    httpClient.connectTimeout(30, TimeUnit.SECONDS);
                    httpClient.readTimeout(30, TimeUnit.SECONDS);
                    httpClient.writeTimeout(30, TimeUnit.SECONDS);

                    // Setup Gson with ISO date format
                    Gson gson = new GsonBuilder()
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                            .create();

                    // Create Retrofit instance
                    retrofit = new Retrofit.Builder()
                            .baseUrl(BASE_URL)
                            .client(httpClient.build())
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();
                }
            }
        }
        return retrofit;
    }

    /**
     * Set token to be used in requests
     */
    public static void setToken(String token) {
        cachedToken = token;
    }

    /**
     * Reset retrofit (khi token bị invalid/expired)
     */
    public static void resetRetrofit() {
        retrofit = null;
        cachedToken = null;
    }

    public static String getBaseUrl() {
        return BASE_URL;
    }
}

