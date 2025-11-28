package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
<<<<<<< HEAD
import com.example.QuanLyPhongTro_App.data.MockDataKnhi;
=======
import com.example.QuanLyPhongTro_App.data.MockData;
>>>>>>> main
import com.example.QuanLyPhongTro_App.ui.auth.DangKyNguoiThueActivity;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

import java.util.ArrayList;

/**
 * ============================================================================
 * MainActivity - Trang chủ ứng dụng (Người thuê)
 * ============================================================================
 * CHỨC NĂNG:
 * - Hiển thị danh sách phòng trọ từ MockDataKnhi (dữ liệu Đà Nẵng)
 * - Cho phép tìm kiếm/lọc phòng
 * - Cho phép chuyển đổi giữa vai trò Người thuê và Chủ trọ
 * - Cung cấp điều hướng đến các màn hình khác
 *
 * DỮ LIỆU: Sử dụng MockDataKnhi.getRooms() để lấy 8 phòng ở Đà Nẵng
 * ============================================================================
 */
public class MainActivity extends AppCompatActivity {

    // ========== BIẾN GIAO DIỆN ==========
    // Nút bộ lọc phòng
    private Button btnFilter;
    // RecyclerView để hiển thị danh sách phòng
    private RecyclerView roomRecyclerView;
    // Danh sách phòng trọ
    private ArrayList<Room> roomList;
    // Quản lý phiên làm việc
    private SessionManager sessionManager;
    // View chuyên dụng để chọn vai trò
    private android.view.View roleSwitcher;
    // Hiển thị vai trò chính (Người thuê hoặc Chủ trọ)
    private TextView txtRolePrimary;
    // Hiển thị tùy chọn chuyển vai trò
    private TextView txtRoleSecondary;
    // Icon hiển thị vai trò
    private ImageView iconRole;

    /**
     * PHƯƠNG THỨC: onCreate()
     * CHỨC NĂNG: Được gọi khi Activity được tạo, khởi tạo giao diện và dữ liệu
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Gắn layout với Activity
        setContentView(R.layout.activity_tenant_home);

        // Khởi tạo SessionManager để quản lý đăng nhập
        sessionManager = new SessionManager(this);

<<<<<<< HEAD
        // ===== TỰ ĐỘNG ĐĂNG NHẬP GUEST =====
        // Nếu người dùng chưa đăng nhập, tự động tạo session cho guest
        // (Chỉ sử dụng khi testing, không cần đăng nhập thực)
        if (!sessionManager.isLoggedIn()) {
            sessionManager.createLoginSession("guest_user", "Khách", "guest@example.com", "tenant");
        }
=======
>>>>>>> main

        // Khởi tạo dữ liệu phòng từ MockDataKnhi
        initRoomList();
        // Khởi tạo các view từ layout
        initViews();
        // Thiết lập dropdown chọn vai trò
        setupRoleDropdown();
        // Thiết lập thanh điều hướng dưới cùng
        setupBottomNavigation();
        // Thiết lập RecyclerView hiển thị danh sách phòng
        setupRoomRecyclerView();
        // Thiết lập nút bộ lọc
        setupFilterButton();
    }

    /**
     * PHƯƠNG THỨC: initRoomList()
     * CHỨC NĂNG: Tải danh sách phòng từ MockDataKnhi
     * - Lấy 8 phòng ở Đà Nẵng từ MockDataKnhi.getRooms()
     * - Lưu vào ArrayList roomList
     */
    private void initRoomList() {
        // Tạo ArrayList mới và thêm tất cả phòng từ MockDataKnhi
        roomList = new ArrayList<>(MockDataKnhi.getRooms());
    }

    /**
     * PHƯƠNG THỨC: initViews()
     * CHỨC NĂNG: Khởi tạo các view (layout elements) từ file layout XML
     */
    private void initViews() {
        // Nút bộ lọc
        btnFilter = findViewById(R.id.btnFilter);
        // RecyclerView chứa danh sách phòng
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        // View để chọn vai trò
        roleSwitcher = findViewById(R.id.roleSwitcher);
        // Text hiển thị vai trò hiện tại (ví dụ: "Người thuê")
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        // Text hiển thị tùy chọn chuyển vai trò
        txtRoleSecondary = roleSwitcher.findViewById(R.id.txtRoleSecondary);
        // Icon vai trò
        iconRole = roleSwitcher.findViewById(R.id.iconRole);
    }

    /**
     * PHƯƠNG THỨC: setupRoleDropdown()
     * CHỨC NĂNG: Thiết lập dropdown để chọn vai trò (Người thuê hoặc Chủ trọ)
     * - Nếu người dùng là chủ trọ: Cho phép chuyển sang Chủ trọ
     * - Nếu người dùng chỉ là Người thuê: Cho phép đăng nhập làm Chủ trọ
     */
    private void setupRoleDropdown() {
        roleSwitcher.setOnClickListener(v -> {
<<<<<<< HEAD
            // Kiểm tra người dùng hiện tại có phải chủ trọ không
=======
            // Kiểm tra đã đăng nhập chưa
            if (!sessionManager.isLoggedIn()) {
                new AlertDialog.Builder(this)
                        .setTitle("Chọn giao diện")
                        .setItems(new String[]{"Đăng nhập Người thuê", "Đăng nhập Chủ trọ"}, (dialog, which) -> {
                            Intent intent = new Intent(this, LoginActivity.class);
                            if (which == 0) {
                                intent.putExtra("targetRole", "tenant");
                            } else {
                                intent.putExtra("targetRole", "landlord");
                            }
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
                return;
            }

            // Nếu đã đăng nhập
>>>>>>> main
            boolean landlordAccount = "landlord".equals(sessionManager.getUserRole());

            // Tạo danh sách tùy chọn
            // Nếu là chủ trọ: ["Người thuê", "Chủ trọ"]
            // Nếu chỉ là người thuê: ["Người thuê", "Đăng nhập Chủ trọ"]
            String[] options = landlordAccount
                    ? new String[]{"Người thuê", "Chủ trọ"}
                    : new String[]{"Người thuê", "Đăng nhập Chủ trọ"};

            // Hiển thị dialog chọn vai trò
            new AlertDialog.Builder(this)
                    .setTitle("Chọn giao diện")
                    .setItems(options, (dialog, which) -> {
                        // which == 0: Chọn Người thuê
                        if (which == 0) {
                            sessionManager.setDisplayRole("tenant");
                            applyRoleUI(); // Cập nhật giao diện
                        }
                        // which == 1: Chuyển đổi vai trò
                        else if (which == 1) {
                            if (landlordAccount) {
                                // Nếu người dùng là chủ trọ: Chuyển sang giao diện chủ trọ
                                sessionManager.setDisplayRole("landlord");
                                Intent intent = new Intent(this,
                                        com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            } else {
                                // Nếu người dùng chỉ là Người thuê: Mở trang đăng nhập Chủ trọ
                                Intent i = new Intent(this, LoginActivity.class);
                                i.putExtra("targetRole", "landlord");
                                startActivity(i);
                            }
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    /**
     * PHƯƠNG THỨC: applyRoleUI()
     * CHỨC NĂNG: Cập nhật giao diện dựa trên vai trò hiện tại
     * - Hiển thị tên vai trò (Người thuê hoặc Chủ trọ)
     * - Cập nhật icon phù hợp
     */
    private void applyRoleUI() {
<<<<<<< HEAD
        // Lấy vai trò hiển thị từ session
=======
        if (!sessionManager.isLoggedIn()) {
            txtRolePrimary.setText("Khách");
            txtRoleSecondary.setText("Đăng nhập");
            iconRole.setImageResource(R.drawable.ic_user);
            return;
        }

>>>>>>> main
        String display = sessionManager.getDisplayRole();

        if (display.equals("landlord")) {
            // Nếu là Chủ trọ
            txtRolePrimary.setText("Chủ trọ");
            txtRoleSecondary.setText("Chuyển vai trò");
            iconRole.setImageResource(R.drawable.ic_home); // Icon nhà
        } else {
            // Nếu là Người thuê
            txtRolePrimary.setText("Người thuê");
            txtRoleSecondary.setText("Chuyển vai trò");
            iconRole.setImageResource(R.drawable.ic_user); // Icon người dùng
        }
    }

    /**
     * PHƯƠNG THỨC: setupBottomNavigation()
     * CHỨC NĂNG: Thiết lập thanh điều hướng dưới cùng của app
     * - Đánh dấu tab "home" là đang hoạt động
     */
    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    /**
     * PHƯƠNG THỨC: checkLoginRequired()
     * CHỨC NĂNG: Kiểm tra người dùng đã đăng nhập chưa
     * - Dùng trước khi cho phép các tính năng yêu cầu đăng nhập
     * RETURN: true = đã đăng nhập, false = chưa đăng nhập
     */
    private boolean checkLoginRequired() {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (!sessionManager.isLoggedIn()) {
            // Nếu chưa, hiển thị dialog yêu cầu đăng nhập
            showTenantLoginDialog();
            return false;
        }
        // Nếu đã đăng nhập
        return true;
    }

    /**
     * PHƯƠNG THỨC: showTenantLoginDialog()
     * CHỨC NĂNG: Hiển thị dialog yêu cầu đăng nhập người dùng
     * - Cho phép người dùng chọn: Đăng nhập, Đăng ký, hoặc Hủy
     */
    private void showTenantLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập tài khoản Người thuê để sử dụng tính năng này")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    // Nút "Đăng nhập": Mở trang LoginActivity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("targetRole", "tenant");
                    startActivity(intent);
                })
                .setNegativeButton("Đăng ký", (dialog, which) -> {
                    // Nút "Đăng ký": Mở trang DangKyNguoiThueActivity
                    Intent intent = new Intent(MainActivity.this, DangKyNguoiThueActivity.class);
                    startActivity(intent);
                })
                .setNeutralButton("Hủy", null)
                .show();
    }

    /**
     * PHƯƠNG THỨC: setupRoomRecyclerView()
     * CHỨC NĂNG: Thiết lập RecyclerView để hiển thị danh sách phòng
     * - Tạo RoomAdapter để điều khiển hiển thị
     * - Gắn sự kiện click: Khi click vào phòng -> mở RoomDetailActivity
     */
    private void setupRoomRecyclerView() {
        // Tạo adapter với callback khi click vào phòng
        RoomAdapter roomAdapter = new RoomAdapter(roomList, room -> {
            // Khi click vào một phòng:
            // 1. Tạo intent mở RoomDetailActivity
            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            // 2. Truyền object phòng qua intent
            intent.putExtra("room", room);
            // 3. Mở activity
            startActivity(intent);
        });

        // Gắn adapter vào RecyclerView
        roomRecyclerView.setAdapter(roomAdapter);
    }

    /**
     * PHƯƠNG THỨC: setupFilterButton()
     * CHỨC NĂNG: Thiết lập sự kiện click cho nút bộ lọc
     * - Khi click: Mở AdvancedFilterBottomSheet
     */
    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> showAdvancedFilter());
    }

    /**
     * PHƯƠNG THỨC: showAdvancedFilter()
     * CHỨC NĂNG: Hiển thị bộ lọc nâng cao dưới dạng BottomSheet
     * - Cho phép người dùng lọc phòng theo tiêu chí
     */
    public void showAdvancedFilter() {
        // Tạo BottomSheet filter
        AdvancedFilterBottomSheet filterSheet = AdvancedFilterBottomSheet.newInstance();
        // Hiển thị BottomSheet
        filterSheet.show(getSupportFragmentManager(), "AdvancedFilter");
    }

    /**
     * PHƯƠNG THỨC: onResume()
     * CHỨC NĂNG: Được gọi mỗi khi Activity quay trở lại từ background
     * - Cập nhật giao diện vai trò
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật lại giao diện vai trò khi quay trở lại
        applyRoleUI();
    }
}
