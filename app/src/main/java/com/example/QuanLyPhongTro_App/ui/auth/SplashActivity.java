package com.example.QuanLyPhongTro_App.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.MainActivity;

public class SplashActivity extends AppCompatActivity {

    // Thời gian hiển thị splash screen (2 giây)
    private static final int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Sau 2 giây, chuyển sang màn hình Home (Guest Mode)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Đóng SplashActivity để không quay lại
        }, SPLASH_DISPLAY_LENGTH);
    }
}