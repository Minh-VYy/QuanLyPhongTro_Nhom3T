package com.example.QuanLyPhongTro_App.data.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit = null;
    // THAY THẾ BẰNG URL CƠ SỞ CỦA API CỦA BẠN
    private static final String BASE_URL = "YOUR_API_BASE_URL";

    public static ApiService getApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ApiService.class);
    }
}
