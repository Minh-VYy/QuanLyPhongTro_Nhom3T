package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

public class LandlordProfileActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvUserName, tvUserEmail;
    private LinearLayout btnEditProfile, btnSettings, btnHelp, btnPrivacyPolicy, btnLogout;
    private ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_profile);

        sessionManager = new SessionManager(this);

        initViews();
        loadUserInfo();
        setupButtons();
        setupBottomNavigation();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        imgAvatar = findViewById(R.id.img_avatar);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnSettings = findViewById(R.id.btn_settings);
        btnHelp = findViewById(R.id.btn_help);
        btnPrivacyPolicy = findViewById(R.id.btn_privacy_policy);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadUserInfo() {
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        if (userName != null) {
            tvUserName.setText(userName);
        } else {
            tvUserName.setText("Chủ trọ");
        }

        if (userEmail != null) {
            tvUserEmail.setText(userEmail);
        } else {
            tvUserEmail.setText("chotro@example.com");
        }
    }

    private void setupButtons() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, LandlordEditProfileActivity.class);
            startActivity(intent);
        });

        btnSettings.setOnClickListener(v -> {
            // TODO: Open settings
            showComingSoon();
        });

        btnHelp.setOnClickListener(v -> {
            startActivity(new Intent(this, TroGiup.class));
        });

        btnPrivacyPolicy.setOnClickListener(v -> {
            // TODO: Open privacy policy
            showComingSoon();
        });

        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Đăng xuất")
                    .setMessage("Bạn có chắc muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void showComingSoon() {
        new AlertDialog.Builder(this)
                .setTitle("Thông báo")
                .setMessage("Tính năng đang phát triển")
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "profile");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "profile");
    }
}
