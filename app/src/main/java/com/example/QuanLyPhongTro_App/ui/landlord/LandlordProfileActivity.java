package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandlordProfileActivity extends AppCompatActivity {

    private static final String TAG = "LandlordProfileActivity";
    private SessionManager sessionManager;
    private TextView tvUserName, tvUserEmail;
    private LinearLayout btnEditProfile, btnSettings, btnHelp, btnPrivacyPolicy, btnLogout;
    private UserProfileDao.UserProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_profile);

        sessionManager = new SessionManager(this);

        initViews();
        loadUserProfileFromApi();
        setupButtons();
        setupBottomNavigation();
    }

    private void initViews() {
        tvUserName = findViewById(R.id.tv_user_name);
        tvUserEmail = findViewById(R.id.tv_user_email);
        btnEditProfile = findViewById(R.id.btn_edit_profile);
        btnSettings = findViewById(R.id.btn_settings);
        btnHelp = findViewById(R.id.btn_help);
        btnPrivacyPolicy = findViewById(R.id.btn_privacy_policy);
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadUserInfo() {
        // Fallback to session data if database load fails
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

    private void loadUserProfileFromApi() {
        if (!sessionManager.isLoggedIn() || sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            Log.w(TAG, "Not logged in or missing token, using session fallback");
            loadUserInfo();
            return;
        }

        ApiClient.setToken(sessionManager.getToken());
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        api.getUserProfile().enqueue(new Callback<GenericResponse<Object>>() {
            @Override
            public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "getUserProfile failed: " + response.code());
                    loadUserInfo();
                    return;
                }

                GenericResponse<Object> body = response.body();
                if (!body.success || body.data == null) {
                    Log.w(TAG, "getUserProfile returned empty: " + body.message);
                    loadUserInfo();
                    return;
                }

                try {
                    //noinspection unchecked
                    Map<String, Object> map = (Map<String, Object>) body.data;

                    UserProfileDao.UserProfile profile = new UserProfileDao.UserProfile();

                    Object nguoiDungId = map.get("nguoiDungId");
                    if (nguoiDungId == null) nguoiDungId = map.get("NguoiDungId");
                    if (nguoiDungId != null) profile.setNguoiDungId(nguoiDungId.toString());

                    Object email = map.get("email");
                    if (email == null) email = map.get("Email");
                    if (email != null) profile.setEmail(email.toString());

                    Object hoTen = map.get("hoTen");
                    if (hoTen == null) hoTen = map.get("HoTen");
                    if (hoTen != null) profile.setHoTen(hoTen.toString());

                    Object vaiTroId = map.get("vaiTroId");
                    if (vaiTroId == null) vaiTroId = map.get("VaiTroId");
                    if (vaiTroId != null) {
                        try { profile.setVaiTroId(((Number) vaiTroId).intValue()); } catch (Exception ignore) {}
                    }

                    Object tenVaiTro = map.get("tenVaiTro");
                    if (tenVaiTro == null) tenVaiTro = map.get("TenVaiTro");
                    if (tenVaiTro != null) profile.setTenVaiTro(tenVaiTro.toString());

                    // Update session cache if needed
                    if (profile.getNguoiDungId() != null && !profile.getNguoiDungId().isEmpty()) {
                        sessionManager.createLoginSession(
                                profile.getNguoiDungId(),
                                profile.getHoTen() != null ? profile.getHoTen() : sessionManager.getUserName(),
                                profile.getEmail() != null ? profile.getEmail() : sessionManager.getUserEmail(),
                                sessionManager.getUserType()
                        );
                    }

                    updateUIWithProfile(profile);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing profile API response", e);
                    loadUserInfo();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                Log.e(TAG, "getUserProfile error", t);
                loadUserInfo();
            }
        });
    }

    private void updateUIWithProfile(UserProfileDao.UserProfile profile) {
        if (profile != null) {
            currentProfile = profile;

            // Update display name
            String displayName = profile.getDisplayName();
            tvUserName.setText(displayName);

            // Update email
            String email = profile.getEmail();
            if (email != null && !email.trim().isEmpty()) {
                tvUserEmail.setText(email);
            } else {
                tvUserEmail.setText("Chưa cập nhật email");
            }

            Log.d(TAG, "✅ UI updated with profile data");
            Log.d(TAG, "👤 Name: " + displayName);
            Log.d(TAG, "📧 Email: " + email);
        } else {
            Log.w(TAG, "Profile is null, using fallback data");
            loadUserInfo();
        }
    }

    private void setupButtons() {
        btnEditProfile.setOnClickListener(v -> {
            Intent intent = new Intent(this, LandlordEditProfileActivity.class);
            // Pass current profile data to edit activity
            if (currentProfile != null) {
                intent.putExtra("userId", currentProfile.getNguoiDungId());
            }
            startActivityForResult(intent, 1001); // Use startActivityForResult to refresh data when returning
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            // Profile was updated, reload data
            loadUserProfileFromApi();
        }
    }
}
