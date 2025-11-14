package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.auth.DangKyNguoiThueActivity;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnFilter;
    private RecyclerView roomRecyclerView;
    private ArrayList<Room> roomList;
    private SessionManager sessionManager;
    private View roleSwitcher;
    private TextView txtRolePrimary;
    private TextView txtRoleSecondary;
    private ImageView iconRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        sessionManager = new SessionManager(this);

        initRoomList();
        initViews();
        setupRoleDropdown();
        setupBottomNavigation();
        setupRoomRecyclerView();
        setupFilterButton();
    }

    private void initRoomList() {
        roomList = new ArrayList<>();
        roomList.add(new Room("Phòng trọ đẹp, gần ĐH Bách Khoa", "2.5 triệu/tháng", "Quận 10, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Chung cư mini full nội thất", "3.2 triệu/tháng", "Quận 1, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Phòng trọ mới xây", "1.8 triệu/tháng", "Quận Tân Bình, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Studio cao cấp", "4.5 triệu/tháng", "Quận 3, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Phòng trọ giá rẻ", "1.5 triệu/tháng", "Quận Bình Thạnh, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Nhà nguyên căn", "5.0 triệu/tháng", "Quận 7, TP.HCM", R.drawable.tro));
    }

    private void initViews() {
        btnFilter = findViewById(R.id.btnFilter);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        txtRoleSecondary = roleSwitcher.findViewById(R.id.txtRoleSecondary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);
    }

    private void setupRoleDropdown() {
        roleSwitcher.setOnClickListener(v -> {
            boolean landlordAccount = "landlord".equals(sessionManager.getUserRole());
            String[] options = landlordAccount ? new String[]{"Người thuê", "Chủ trọ"} : new String[]{"Người thuê", "Đăng nhập Chủ trọ"};
            new AlertDialog.Builder(this)
                    .setTitle("Chọn giao diện")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            sessionManager.setDisplayRole("tenant");
                            applyRoleUI();
                        } else if (which == 1) {
                            if (landlordAccount) {
                                sessionManager.setDisplayRole("landlord");
                                applyRoleUI();
                            } else {
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

    private void applyRoleUI() {
        String display = sessionManager.getDisplayRole();
        if (display.equals("landlord")) {
            txtRolePrimary.setText("Chủ trọ");
            txtRoleSecondary.setText("Chuyển vai trò");
            iconRole.setImageResource(R.drawable.ic_home); // placeholder landlord icon
        } else {
            txtRolePrimary.setText("Người thuê");
            txtRoleSecondary.setText("Chuyển vai trò");
            iconRole.setImageResource(R.drawable.ic_user);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    /**
     * Kiểm tra xem người dùng đã đăng nhập chưa (cho Người thuê)
     */
    private boolean checkLoginRequired() {
        // Require user logged in as tenant for tenant features
        if (!sessionManager.isLoggedIn()) {
            showTenantLoginDialog();
            return false;
        }
        // display role determines UI but tenant access always allowed if any role logged in
        return true;
    }

    /**
     * Hiển thị dialog đăng nhập cho Người thuê
     */
    private void showTenantLoginDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập tài khoản Người thuê để sử dụng tính năng này")
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.putExtra("targetRole", "tenant");
                    startActivity(intent);
                })
                .setNegativeButton("Đăng ký", (dialog, which) -> {
                    Intent intent = new Intent(MainActivity.this, DangKyNguoiThueActivity.class);
                    startActivity(intent);
                })
                .setNeutralButton("Hủy", null)
                .show();
    }

    private void setupRoomRecyclerView() {
        RoomAdapter roomAdapter = new RoomAdapter(roomList, room -> {
            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });

        roomRecyclerView.setAdapter(roomAdapter);
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> showAdvancedFilter());
    }

    /**
     * Hiển thị bộ lọc nâng cao
     */
    public void showAdvancedFilter() {
        AdvancedFilterBottomSheet filterSheet = AdvancedFilterBottomSheet.newInstance();
        filterSheet.show(getSupportFragmentManager(), "AdvancedFilter");
    }

    @Override
    protected void onResume() {
        super.onResume();
        BottomNavigationHelper.setupBottomNavigation(this, "home");
        applyRoleUI();
    }
}
