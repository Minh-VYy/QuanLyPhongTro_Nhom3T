package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.PhongDao;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

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
    private ProgressBar progressBar;

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
    }

    private void initViews() {
        btnFilter = findViewById(R.id.btnFilter);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        txtRoleSecondary = roleSwitcher.findViewById(R.id.txtRoleSecondary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRoomRecyclerView() {
        roomAdapter = new RoomAdapter(roomList, room -> {
            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });
        roomRecyclerView.setAdapter(roomAdapter);
        
        // Load dữ liệu từ database
        loadRoomsFromDatabase();
    }
    
    /**
     * Load danh sách phòng từ database
     */
    private void loadRoomsFromDatabase() {
        new LoadPhongTask().execute();
    }
    
    /**
     * AsyncTask để load phòng từ database
     */
    private class LoadPhongTask extends AsyncTask<Void, Void, List<Phong>> {
        private String errorMsg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Phong> doInBackground(Void... voids) {
            Connection conn = null;
            try {
                Log.d("MainActivity", "=== LOADING ROOMS FROM DATABASE ===");
                conn = DatabaseHelper.getConnection();
                Log.d("MainActivity", "Database connection successful");
                
                PhongDao dao = new PhongDao();
                List<Phong> result = dao.getAllPhongAvailable(conn);
                Log.d("MainActivity", "Query result: " + (result != null ? result.size() : "null") + " rooms");
                return result;
            } catch (Exception e) {
                Log.e("MainActivity", "Error loading rooms: " + e.getMessage(), e);
                errorMsg = e.getMessage();
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
                Log.d("MainActivity", "Database connection closed");
            }
        }

        @Override
        protected void onPostExecute(List<Phong> phongList) {
            super.onPostExecute(phongList);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            if (phongList != null && !phongList.isEmpty()) {
                // Convert Phong to Room và update adapter
                roomList.clear();
                for (Phong phong : phongList) {
                    Room room = convertPhongToRoom(phong);
                    roomList.add(room);
                }
                roomAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, 
                    "Đã tải " + phongList.size() + " phòng", 
                    Toast.LENGTH_SHORT).show();
            } else {
                if (errorMsg != null) {
                    Toast.makeText(MainActivity.this, 
                        "Lỗi kết nối: " + errorMsg, 
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, 
                        "Không có phòng nào", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    
    /**
     * Convert model Phong từ database sang Room (UI model)
     */
    private Room convertPhongToRoom(Phong phong) {
        Room room = new Room();
        
        // ID phòng - Room dùng int, Phong dùng String UUID
        // Tạm thời dùng hashCode để convert
        room.setId(phong.getPhongId() != null ? phong.getPhongId().hashCode() : 0);
        
        // Thông tin cơ bản
        room.setTitle(phong.getTieuDe());
        room.setPriceValue(phong.getGiaTien()); // Room dùng priceValue (double)
        room.setArea(phong.getDienTich());
        room.setAddress(phong.getDiaChiNhaTro());
        room.setLocation(phong.getTenQuanHuyen()); // Room dùng location cho quận
        room.setDescription(phong.getMoTa());
        
        // Đánh giá phòng
        room.setRoomRating(phong.getDiemTrungBinh());
        room.setRoomReviewCount(phong.getSoLuongDanhGia());
        
        // Lấy ảnh đầu tiên làm ảnh đại diện
        if (phong.getDanhSachAnhUrl() != null && !phong.getDanhSachAnhUrl().isEmpty()) {
            room.setImageUrl(phong.getDanhSachAnhUrl().get(0));
        }
        
        // Set tiện ích - convert List<String> sang ArrayList<String>
        if (phong.getTienIch() != null) {
            room.setAmenities(new ArrayList<>(phong.getTienIch()));
        }
        
        return room;
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> {
            AdvancedFilterBottomSheet filterSheet = AdvancedFilterBottomSheet.newInstance();
            
            // Set listener để nhận kết quả filter
            filterSheet.setFilterListener(filters -> {
                // Lấy các giá trị filter
                float minPrice = filters.getFloat("minPrice", 0);
                float maxPrice = filters.getFloat("maxPrice", 100);
                String area = filters.getString("area", null);
                
                // Convert triệu -> VNĐ
                long minPriceVnd = (long) (minPrice * 1000000);
                long maxPriceVnd = (long) (maxPrice * 1000000);
                
                // Gọi search với filter
                searchRoomsFromDatabase(null, minPriceVnd, maxPriceVnd, area);
            });
            
            filterSheet.show(getSupportFragmentManager(), "AdvancedFilter");
        });
        
        // DEBUG: Long click to open test activity
        btnFilter.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestDatabaseActivity.class);
            startActivity(intent);
            return true;
        });
        
        // Long click để test database (hidden feature)
        btnFilter.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TestDatabaseActivity.class);
            startActivity(intent);
            return true;
        });
    }
    
    /**
     * Tìm kiếm phòng với filter
     */
    private void searchRoomsFromDatabase(String keyword, Long minPrice, Long maxPrice, String quanHuyen) {
        new SearchPhongTask().execute(keyword, 
            minPrice != null ? String.valueOf(minPrice) : null,
            maxPrice != null ? String.valueOf(maxPrice) : null,
            quanHuyen);
    }
    
    /**
     * AsyncTask để tìm kiếm phòng
     */
    private class SearchPhongTask extends AsyncTask<String, Void, List<Phong>> {
        private String errorMsg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Phong> doInBackground(String... params) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                PhongDao dao = new PhongDao();
                
                String keyword = params[0];
                Long minPrice = params[1] != null ? Long.parseLong(params[1]) : null;
                Long maxPrice = params[2] != null ? Long.parseLong(params[2]) : null;
                String quanHuyen = params[3];
                
                // Xử lý tên quận (bỏ "Quận " ở đầu nếu có)
                if (quanHuyen != null && quanHuyen.startsWith("Quận ")) {
                    quanHuyen = quanHuyen.substring(5);
                } else if (quanHuyen != null && quanHuyen.startsWith("Huyện ")) {
                    quanHuyen = quanHuyen.substring(6);
                }
                
                return dao.searchPhong(conn, keyword, minPrice, maxPrice, quanHuyen);
            } catch (Exception e) {
                errorMsg = e.getMessage();
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(List<Phong> phongList) {
            super.onPostExecute(phongList);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            if (phongList != null) {
                roomList.clear();
                for (Phong phong : phongList) {
                    Room room = convertPhongToRoom(phong);
                    roomList.add(room);
                }
                roomAdapter.notifyDataSetChanged();
                
                if (phongList.isEmpty()) {
                    Toast.makeText(MainActivity.this, 
                        "Không tìm thấy phòng phù hợp", 
                        Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, 
                        "Tìm thấy " + phongList.size() + " phòng", 
                        Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, 
                    "Lỗi tìm kiếm: " + errorMsg, 
                    Toast.LENGTH_LONG).show();
            }
        }
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

    @Override
    protected void onResume() {
        super.onResume();
        applyRoleUI();
        // Refresh data khi quay lại màn hình
        loadRoomsFromDatabase();
    }
}