package com.example.QuanLyPhongTro_App.utils;

import com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponse;
import com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponseStringDeserializer;

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

                        String token = cachedToken;

                        okhttp3.Request.Builder requestBuilder = original.newBuilder();
                        boolean hasToken = token != null && !token.isEmpty();
                        if (hasToken) {
                            requestBuilder.header("Authorization", "Bearer " + token);
                        }
                        requestBuilder.header("Content-Type", "application/json");

                        okhttp3.Request request = requestBuilder.build();
                        android.util.Log.d("ApiClient", "Request URL: " + request.url());
                        android.util.Log.d("ApiClient", "Authorization header: " + (hasToken ? "Bearer (set)" : "MISSING"));

                        okhttp3.Response resp = chain.proceed(request);
                        if (resp.code() >= 400) {
                            android.util.Log.w("ApiClient", "HTTP " + resp.code() + " for " + request.method() + " " + request.url());
                        }
                        return resp;
                    });

                    // Add logging interceptor for debug
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY);
                    httpClient.addInterceptor(logging);

                    // ✅ Degrade gracefully for broken /api/TapTin/{id} (server 500):
                    // If image endpoint fails, return a synthetic 204 so Glide won't crash the app.
                    httpClient.addInterceptor(chain -> {
                        okhttp3.Request req = chain.request();
                        okhttp3.Response res = chain.proceed(req);

                        try {
                            String path = req.url().encodedPath();
                            if ("GET".equalsIgnoreCase(req.method())
                                    && path != null
                                    && path.startsWith("/api/TapTin/")
                                    && res.code() >= 500) {
                                android.util.Log.w("ApiClient", "TapTin endpoint failed http=" + res.code() + " url=" + req.url());
                                // Return empty response to avoid repeated retries / crashes in image loader
                                return res.newBuilder()
                                        .code(204)
                                        .message("No Content (TapTin fallback)")
                                        .body(okhttp3.ResponseBody.create(null, new byte[0]))
                                        .build();
                            }
                        } catch (Exception ignored) {
                        }

                        return res;
                    });

                    // Set timeouts
                    httpClient.connectTimeout(30, TimeUnit.SECONDS);
                    httpClient.readTimeout(30, TimeUnit.SECONDS);
                    httpClient.writeTimeout(30, TimeUnit.SECONDS);

                    // Setup Gson with ISO date format + tolerant response adapters
                    Gson gson = new GsonBuilder()
                            // Backend returns DateTimeOffset like: 2025-12-24T12:50:28.4247144+00:00
                            // Use a timezone-aware pattern to avoid parsing issues.
                            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                            .registerTypeAdapter(ApiOrArrayResponse.class, new ApiOrArrayResponseStringDeserializer())
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
