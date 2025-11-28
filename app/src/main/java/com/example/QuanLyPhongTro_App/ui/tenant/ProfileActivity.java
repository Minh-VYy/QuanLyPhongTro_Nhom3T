package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileAvatar;
    private TextView profileName, profileContact;
    private CardView btnEditAvatar;
    private LinearLayout menuSavedRooms, menuBookings, menuPersonalInfo, menuSettings;
    private LinearLayout menuHelp, menuLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_profile);

        sessionManager = new SessionManager(this);

        initViews();
        setupBottomNavigation();
        setupListeners();
        loadUserData();
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "profile");
    }

    private void initViews() {
        profileAvatar = findViewById(R.id.profileAvatar);
        profileName = findViewById(R.id.profileName);
        profileContact = findViewById(R.id.profileContact);
        btnEditAvatar = findViewById(R.id.btn_edit_avatar);

        menuSavedRooms = findViewById(R.id.menuSavedRooms);
        menuBookings = findViewById(R.id.menuBookings);
        menuPersonalInfo = findViewById(R.id.menuPersonalInfo);
        menuSettings = findViewById(R.id.menuSettings);
        menuHelp = findViewById(R.id.menuHelp);
        menuLogout = findViewById(R.id.menuLogout);
    }

    private void setupListeners() {
        try {
            if (btnEditAvatar != null) {
                btnEditAvatar.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    startActivity(intent);
                });
            }

            if (menuSavedRooms != null) {
                menuSavedRooms.setOnClickListener(v -> {
                    startActivity(new Intent(this, SavedRoomsActivity.class));
                });
            }

            if (menuBookings != null) {
                menuBookings.setOnClickListener(v -> {
                    startActivity(new Intent(this, BookingListActivity.class));
                });
            }

            if (menuPersonalInfo != null) {
                menuPersonalInfo.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    startActivity(intent);
                });
            }

            if (menuSettings != null) {
                menuSettings.setOnClickListener(v -> {
                    Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
                });
            }

            if (menuHelp != null) {
                menuHelp.setOnClickListener(v -> {
                    Toast.makeText(this, "Trợ giúp", Toast.LENGTH_SHORT).show();
                });
            }


            if (menuLogout != null) {
                menuLogout.setOnClickListener(v -> {
                    sessionManager.logout();
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
            }
        } catch (Exception e) {
            android.util.Log.e("ProfileActivity", "Error in setupListeners: " + e.getMessage(), e);
        }
    }

    private void loadUserData() {
        try {
            String userName = sessionManager.getUserName();
            String userEmail = sessionManager.getUserEmail();

            if (profileName != null) {
                if (userName != null && !userName.isEmpty()) {
                    profileName.setText(userName);
                } else {
                    profileName.setText("Người dùng");
                }
            }

            if (profileContact != null) {
                if (userEmail != null && !userEmail.isEmpty()) {
                    profileContact.setText(userEmail);
                } else {
                    profileContact.setText("Chưa cập nhật");
                }
            }
        } catch (Exception e) {
            android.util.Log.e("ProfileActivity", "Error in loadUserData: " + e.getMessage(), e);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBottomNavigation();
    }
}
