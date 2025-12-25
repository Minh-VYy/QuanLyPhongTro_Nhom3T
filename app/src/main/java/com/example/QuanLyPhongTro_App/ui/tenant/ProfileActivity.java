package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.chatbot.ChatbotActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

/**
 * ============================================================================
 * ProfileActivity - Màn hình hồ sơ cá nhân người thuê
 * ============================================================================
 * CHỨC NĂNG:
 * - Hiển thị thông tin hồ sơ của người dùng (tên, email)
 * - Cung cấp các menu điều hướng: Phòng yêu thích, Lịch hẹn, Thông tin cá nhân, v.v.
 * - Cho phép chỉnh sửa thông tin hoặc đăng xuất
 *
 * GHI CHÚ: Đã xóa menuTerms vì layout không có element tương ứng
 * ============================================================================
 */
public class ProfileActivity extends AppCompatActivity {

    // ========== BIẾN GIAO DIỆN ==========
    // Avatar người dùng
    private ImageView profileAvatar;
    // Tên người dùng và thông tin liên hệ
    private TextView profileName, profileContact;
    // Nút chỉnh sửa hồ sơ (Avatar)
    private CardView btnEditAvatar;
    
    // Các menu chức năng trong hồ sơ
    private LinearLayout menuSavedRooms;      // Menu: Phòng yêu thích
    private LinearLayout menuBookings;        // Menu: Lịch hẹn
    private LinearLayout menuPersonalInfo;    // Menu: Thông tin cá nhân
    private LinearLayout menuSettings;        // Menu: Cài đặt
    private LinearLayout menuHelp;            // Menu: Trợ giúp
    private LinearLayout menuLogout;          // Menu: Đăng xuất

    // Quản lý phiên làm việc (session)
    private SessionManager sessionManager;

    /**
     * PHƯƠNG THỨC: onCreate()
     * CHỨC NĂNG: Được gọi khi Activity được tạo, khởi tạo giao diện
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gắn layout với Activity
        setContentView(R.layout.activity_tenant_profile);

        // Khởi tạo SessionManager để quản lý đăng nhập/đăng xuất
        sessionManager = new SessionManager(this);

        // Khởi tạo các view từ layout
        initViews();
        // Thiết lập thanh điều hướng dưới cùng
        setupBottomNavigation();
        // Gắn các sự kiện click cho các nút
        setupListeners();
        // Tải dữ liệu người dùng từ SessionManager
        loadUserData();
    }

    /**
     * PHƯƠNG THỨC: setupBottomNavigation()
     * CHỨC NĂNG: Thiết lập thanh điều hướng dưới cùng của app
     * - Đánh dấu tab "profile" là đang hoạt động
     */
    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "profile");
    }

    /**
     * PHƯƠNG THỨC: initViews()
     * CHỨC NĂNG: Khởi tạo các view (layout elements) từ file layout XML
     * - Tìm các view bằng findViewById() và lưu vào biến
     */
    private void initViews() {
        // Khởi tạo các view từ layout
        profileAvatar = findViewById(R.id.profileAvatar);        // Avatar
        profileName = findViewById(R.id.profileName);            // Tên người dùng
        profileContact = findViewById(R.id.profileContact);      // Thông tin liên hệ
        btnEditAvatar = findViewById(R.id.btn_edit_avatar);      // Nút chỉnh sửa

        // Khởi tạo các menu
        menuSavedRooms = findViewById(R.id.menuSavedRooms);      // Phòng yêu thích
        menuBookings = findViewById(R.id.menuBookings);          // Lịch hẹn
        menuPersonalInfo = findViewById(R.id.menuPersonalInfo);  // Thông tin cá nhân
        menuSettings = findViewById(R.id.menuSettings);          // Cài đặt
        menuHelp = findViewById(R.id.menuHelp);                  // Trợ giúp
        menuLogout = findViewById(R.id.menuLogout);              // Đăng xuất
    }

    /**
     * PHƯƠNG THỨC: setupListeners()
     * CHỨC NĂNG: Gắn sự kiện click cho các nút và menu
     * - Mỗi nút khi được click sẽ thực hiện một hành động
     */
    private void setupListeners() {
        try {
            // ===== NÚT CHỈNH SỬA HỒ SƠ (AVATAR) =====
            if (btnEditAvatar != null) {
                btnEditAvatar.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    startActivity(intent);
                });
            }

            // ===== MENU PHÒNG YÊU THÍCH =====
            // Khi click: Mở Activity SavedRoomsActivity để xem phòng đã lưu
            if (menuSavedRooms != null) {
                menuSavedRooms.setOnClickListener(v -> {
                    startActivity(new Intent(this, SavedRoomsActivity.class));
                });
            }

            // ===== MENU LỊCH HẸN =====
            // Khi click: Mở Activity BookingListActivity để xem lịch hẹn
            if (menuBookings != null) {
                menuBookings.setOnClickListener(v -> {
                    startActivity(new Intent(this, BookingListActivity.class));
                });
            }

            // ===== MENU THÔNG TIN CÁ NHÂN =====
            // Khi click: Mở Activity chỉnh sửa hồ sơ
            if (menuPersonalInfo != null) {
                menuPersonalInfo.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EditProfileActivity.class);
                    startActivity(intent);
                });
            }

            // ===== MENU CÀI ĐẶT =====
            // Khi click: Hiển thị Toast (tạm thời, chưa phát triển đầy đủ)
            if (menuSettings != null) {
                menuSettings.setOnClickListener(v -> {
                    Toast.makeText(this, "Cài đặt", Toast.LENGTH_SHORT).show();
                });
            }

            // ===== MENU TRỢ GIÚP =====
            // Khi click: Mở ChatbotActivity để hỏi trợ lý AI
            if (menuHelp != null) {
                menuHelp.setOnClickListener(v -> {
                    Intent intent = new Intent(this, ChatbotActivity.class);
                    intent.putExtra("user_type", "tenant");
                    intent.putExtra("context", "help");
                    startActivity(intent);
                });
            }

            // ===== MENU ĐĂNG XUẤT =====
            // Khi click:
            // 1. Gọi sessionManager.logout() để xóa dữ liệu đăng nhập
            // 2. Hiển thị thông báo "Đã đăng xuất"
            // 3. Quay về MainActivity
            if (menuLogout != null) {
                menuLogout.setOnClickListener(v -> {
                    // Xóa session đăng nhập
                    sessionManager.logout();
                    // Hiển thị thông báo
                    Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

                    // Quay về MainActivity
                    Intent intent = new Intent(this, MainActivity.class);
                    // FLAG_ACTIVITY_NEW_TASK: Tạo task mới
                    // FLAG_ACTIVITY_CLEAR_TASK: Xóa tất cả activity cũ khỏi stack
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Kết thúc activity hiện tại
                });
            }
        } catch (Exception e) {
            // Nếu có lỗi, ghi log để debug
            android.util.Log.e("ProfileActivity", "Error in setupListeners: " + e.getMessage(), e);
        }
    }

    /**
     * PHƯƠNG THỨC: loadUserData()
     * CHỨC NĂNG: Tải dữ liệu người dùng từ SessionManager và hiển thị lên giao diện
     * - Lấy tên và email từ session
     * - Hiển thị trên profileName và profileContact
     */
    private void loadUserData() {
        try {
            // Lấy tên người dùng từ session
            String userName = sessionManager.getUserName();
            // Lấy email người dùng từ session
            String userEmail = sessionManager.getUserEmail();

            // Hiển thị tên người dùng
            if (profileName != null) {
                profileName.setText((userName != null && !userName.isEmpty()) ? userName : "Người dùng");
            }

            // Hiển thị thông tin liên hệ (email)
            if (profileContact != null) {
                profileContact.setText((userEmail != null && !userEmail.isEmpty()) ? userEmail : "Chưa cập nhật");
            }
        } catch (Exception e) {
            // Nếu có lỗi, ghi log để debug
            android.util.Log.e("ProfileActivity", "Error in loadUserData: " + e.getMessage(), e);
        }
    }

    /**
     * PHƯƠNG THỨC: onResume()
     * CHỨC NĂNG: Được gọi mỗi khi Activity quay trở lại từ background
     * - Cập nhật thanh điều hướng
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại thanh điều hướng để đảm bảo tab "profile" vẫn được chọn
        setupBottomNavigation();
    }
}
