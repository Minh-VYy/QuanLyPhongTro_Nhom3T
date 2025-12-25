package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.DatabaseConnector;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

import java.sql.Connection;

public class LandlordProfileActivity extends AppCompatActivity {

    private static final String TAG = "LandlordProfileActivity";
    private SessionManager sessionManager;
    private UserProfileDao userProfileDao;
    private TextView tvUserName, tvUserEmail;
    private LinearLayout btnEditProfile, btnSettings, btnHelp, btnPrivacyPolicy, btnLogout;
    private ImageView imgAvatar, btnHeaderMessages, btnHeaderNotifications;
    private UserProfileDao.UserProfile currentProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_profile);

        sessionManager = new SessionManager(this);
        userProfileDao = new UserProfileDao();

        initViews();
        loadUserProfileFromDatabase();
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
        btnHeaderMessages = findViewById(R.id.btn_header_messages);
        btnHeaderNotifications = findViewById(R.id.btn_header_notifications);
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

    private void loadUserProfileFromDatabase() {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            Log.w(TAG, "No user ID found in session, using fallback data");
            loadUserInfo();
            return;
        }

        Log.d(TAG, "Loading profile for user: " + userId);
        
        // TEMPORARY FIX: Create profile from session data instead of database
        // This ensures the profile display works while we debug the database connection
        createProfileFromSession(userId);
        
        // Uncomment this line when database connection is fixed
        // new LoadProfileTask().execute(userId);
    }
    
    private void createProfileFromSession(String userId) {
        Log.d(TAG, "Creating profile from session data");
        
        UserProfileDao.UserProfile profile = new UserProfileDao.UserProfile();
        
        // Set data from session
        profile.setNguoiDungId(userId);
        profile.setEmail(sessionManager.getUserEmail());
        profile.setHoTen(sessionManager.getUserName());
        profile.setVaiTroId(2); // Landlord
        profile.setTenVaiTro("ChuTro");
        
        Log.d(TAG, "✅ Profile created from session data");
        Log.d(TAG, "👤 Name: " + profile.getHoTen());
        Log.d(TAG, "📧 Email: " + profile.getEmail());
        
        updateUIWithProfile(profile);
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

        btnHeaderMessages.setOnClickListener(v -> {
            Intent intent = new Intent(this, YeuCau.class);
            startActivity(intent);
        });

        btnHeaderNotifications.setOnClickListener(v -> {
            showComingSoon();
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
            loadUserProfileFromDatabase();
        }
    }

    /**
     * AsyncTask to load user profile from database
     */
    private class LoadProfileTask extends AsyncTask<String, Void, UserProfileDao.UserProfile> {
        private String errorMessage;
        private volatile UserProfileDao.UserProfile profile;
        private volatile boolean connectionCompleted = false;

        @Override
        protected UserProfileDao.UserProfile doInBackground(String... params) {
            String userId = params[0];
            Log.d(TAG, "🔄 Starting LoadProfileTask for userId: " + userId);

            try {
                DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
                    @Override
                    public void onConnectionSuccess(Connection connection) {
                        Log.d(TAG, "✅ Database connection successful");
                        try {
                            UserProfileDao.UserProfile loadedProfile = userProfileDao.getUserProfile(connection, userId);
                            synchronized (LoadProfileTask.this) {
                                LoadProfileTask.this.profile = loadedProfile;
                                LoadProfileTask.this.connectionCompleted = true;
                            }
                            Log.d(TAG, "📊 Profile loaded: " + (loadedProfile != null ? "SUCCESS" : "NULL"));
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error loading profile: " + e.getMessage(), e);
                            errorMessage = e.getMessage();
                            synchronized (LoadProfileTask.this) {
                                LoadProfileTask.this.connectionCompleted = true;
                            }
                        } finally {
                            try {
                                connection.close();
                                Log.d(TAG, "🔒 Database connection closed");
                            } catch (Exception e) {
                                Log.e(TAG, "Error closing connection", e);
                            }
                        }
                    }

                    @Override
                    public void onConnectionFailed(String error) {
                        Log.e(TAG, "❌ Database connection failed: " + error);
                        errorMessage = error;
                        synchronized (LoadProfileTask.this) {
                            LoadProfileTask.this.connectionCompleted = true;
                        }
                    }
                });

                // Wait for connection callback to complete with longer timeout
                int maxWaitTime = 10000; // 10 seconds
                int waitTime = 0;
                while (!connectionCompleted && waitTime < maxWaitTime) {
                    Thread.sleep(500);
                    waitTime += 500;
                    Log.d(TAG, "⏳ Waiting for connection... " + waitTime + "ms");
                }

                if (!connectionCompleted) {
                    Log.w(TAG, "⚠️ Connection timeout after " + maxWaitTime + "ms");
                    errorMessage = "Connection timeout";
                }

                synchronized (this) {
                    return this.profile;
                }

            } catch (Exception e) {
                Log.e(TAG, "❌ Error in LoadProfileTask: " + e.getMessage(), e);
                errorMessage = e.getMessage();
                return null;
            }
        }

        @Override
        protected void onPostExecute(UserProfileDao.UserProfile profile) {
            Log.d(TAG, "📱 LoadProfileTask completed. Profile: " + (profile != null ? "LOADED" : "NULL"));
            
            if (profile != null) {
                Log.d(TAG, "✅ Updating UI with loaded profile");
                updateUIWithProfile(profile);
            } else {
                Log.w(TAG, "⚠️ Failed to load profile from database: " + errorMessage);
                Toast.makeText(LandlordProfileActivity.this, 
                    "Không thể tải thông tin hồ sơ từ database. Sử dụng dữ liệu tạm thời.", 
                    Toast.LENGTH_LONG).show();
                loadUserInfo(); // Fallback to session data
            }
        }
    }
}
