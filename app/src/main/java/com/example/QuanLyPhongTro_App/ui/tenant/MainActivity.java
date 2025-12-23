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
import com.example.QuanLyPhongTro_App.data.AppDatabase;
import com.example.QuanLyPhongTro_App.ui.auth.DangKyNguoiThueActivity;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private Button btnFilter;
    private RecyclerView roomRecyclerView;
    private ArrayList<Room> roomList = new ArrayList<>();
    private SessionManager sessionManager;
    private android.view.View roleSwitcher;
    private TextView txtRolePrimary;
    private TextView txtRoleSecondary;
    private ImageView iconRole;
    private RoomAdapter roomAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        sessionManager = new SessionManager(this);

        initViews();
        setupRoleDropdown();
        setupBottomNavigation();
        setupRoomRecyclerView();
        setupFilterButton();

        // Tải dữ liệu từ cơ sở dữ liệu thay vì MockData
        loadRoomsFromDatabase();
    }

    private void loadRoomsFromDatabase() {
        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        Executors.newSingleThreadExecutor().execute(() -> {
            // Lấy dữ liệu từ DB
            List<Room> dbRooms = db.roomDao().getAll();

            // Cập nhật giao diện trên luồng chính
            runOnUiThread(() -> {
                roomList.clear();
                roomList.addAll(dbRooms);
                roomAdapter.notifyDataSetChanged(); // Báo cho adapter biết dữ liệu đã thay đổi
            });
        });
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

            boolean landlordAccount = "landlord".equals(sessionManager.getUserRole());

            String[] options = landlordAccount
                    ? new String[]{"Người thuê", "Chủ trọ"}
                    : new String[]{"Người thuê", "Đăng nhập Chủ trọ"};

            new AlertDialog.Builder(this)
                    .setTitle("Chọn giao diện")
                    .setItems(options, (dialog, which) -> {
                        if (which == 0) {
                            sessionManager.setDisplayRole("tenant");
                            applyRoleUI();
                        } else if (which == 1) {
                            if (landlordAccount) {
                                sessionManager.setDisplayRole("landlord");
                                Intent intent = new Intent(this,
                                        com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
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
        if (!sessionManager.isLoggedIn()) {
            txtRolePrimary.setText("Khách");
            txtRoleSecondary.setText("Đăng nhập");
            iconRole.setImageResource(R.drawable.ic_user);
            return;
        }

        String display = sessionManager.getDisplayRole();

        if (display.equals("landlord")) {
            txtRolePrimary.setText("Chủ trọ");
            txtRoleSecondary.setText("Chuyển vai trò");
            iconRole.setImageResource(R.drawable.ic_home);
        } else {
            txtRolePrimary.setText("Người thuê");
            txtRoleSecondary.setText("Chuyển vai trò");
            iconRole.setImageResource(R.drawable.ic_user);
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void setupRoomRecyclerView() {
        // Khởi tạo adapter với danh sách rỗng ban đầu
        roomAdapter = new RoomAdapter(roomList, room -> {
            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });

        roomRecyclerView.setAdapter(roomAdapter);
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> showAdvancedFilter());
    }

    public void showAdvancedFilter() {
        AdvancedFilterBottomSheet filterSheet = AdvancedFilterBottomSheet.newInstance();
        filterSheet.show(getSupportFragmentManager(), "AdvancedFilter");
    }

    @Override
    protected void onResume() {
        super.onResume();
        applyRoleUI();
    }
}
