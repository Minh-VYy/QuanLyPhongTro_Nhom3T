package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View; // Đảm bảo import này có mặt
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profileAvatar;
    private TextView profileName, profileContact;
    private TextView btnEditProfile;
    private LinearLayout menuSavedRooms, menuBookings, menuPersonalInfo, menuSettings;
    private LinearLayout menuHelp, menuLogout;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_profile); // Đã dùng tên layout chính xác

        sessionManager = new SessionManager(this);

        initViews();
        setupBottomNavigation();
        setupListeners(); // Toàn bộ sự kiện click được gọi từ đây
        loadUserData();
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "profile");
    }

    private void initViews() {
        profileAvatar = findViewById(R.id.profileAvatar);
        profileName = findViewById(R.id.profileName);
        profileContact = findViewById(R.id.profileContact);
        btnEditProfile = findViewById(R.id.btnEditProfile);

        menuSavedRooms = findViewById(R.id.menuSavedRooms);
        menuBookings = findViewById(R.id.menuBookings);
        menuPersonalInfo = findViewById(R.id.menuPersonalInfo);
        menuSettings = findViewById(R.id.menuSettings);
        menuHelp = findViewById(R.id.menuHelp);
        menuLogout = findViewById(R.id.menuLogout);
    }

    private void setupListeners() {
        // Nút Header "Chỉnh sửa thông tin" (Dùng Toast hoặc chuyển trang nếu muốn)
        // Lưu ý: Nếu muốn nút này chuyển trang, bạn cần sửa lại logic ở đây
        btnEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng chỉnh sửa thông tin trên header", Toast.LENGTH_SHORT).show();
            // Nếu muốn nó chuyển trang, thay thế dòng Toast bằng:
            // Intent intent = new Intent(this, EditProfileActivity.class);
            // startActivity(intent);
        });

        menuSavedRooms.setOnClickListener(v -> {
            Intent intent = new Intent(this, SavedRoomsActivity.class);
            startActivity(intent);
        });

        menuBookings.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingListActivity.class);
            startActivity(intent);
        });

        // 💡 ĐÂY LÀ PHẦN SỬA LỖI: Gắn sự kiện chuyển trang cho mục menu chính
        menuPersonalInfo.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
        });

        menuSettings.setOnClickListener(v -> {
            Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
        });

        menuHelp.setOnClickListener(v -> {
            Toast.makeText(this, "Trợ giúp & Hỏi đáp", Toast.LENGTH_SHORT).show();
        });

        menuLogout.setOnClickListener(v -> {
            // Đăng xuất
            sessionManager.logout();
            Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

            // Quay về MainActivity (Guest mode)
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadUserData() {
        // Load từ SessionManager
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();

        profileName.setText(userName);
        if (userEmail != null) {
            profileContact.setText(userEmail);
        } else {
            profileContact.setText("Chưa cập nhật");
        }
    }
}