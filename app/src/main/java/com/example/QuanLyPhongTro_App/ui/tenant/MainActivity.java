package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.example.QuanLyPhongTro_App.ui.chatbot.ChatbotActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TỐI ƯU: Thay AsyncTask bằng ExecutorService + Handler
 */
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
    private FloatingActionButton fabChatbot;
    
    // TỐI ƯU: Sử dụng ExecutorService thay vì AsyncTask
    private ExecutorService executorService;
    private Handler mainHandler;
    
    // Cache để tránh load lại không cần thiết
    private boolean isDataLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        sessionManager = new SessionManager(this);
        
        // TỐI ƯU: Khởi tạo ExecutorService
        executorService = Executors.newFixedThreadPool(2);
        mainHandler = new Handler(Looper.getMainLooper());

        initViews();
        setupRoleDropdown();
        setupBottomNavigation();
        setupRoomRecyclerView();
        setupFilterButton();
        setupChatbot();
    }

    private void initViews() {
        btnFilter = findViewById(R.id.btnFilter);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        txtRoleSecondary = roleSwitcher.findViewById(R.id.txtRoleSecondary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);
        progressBar = findViewById(R.id.progressBar);
        fabChatbot = findViewById(R.id.fabChatbot);
    }
    
    private void setupChatbot() {
        if (fabChatbot != null) {
            fabChatbot.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
                intent.putExtra("user_type", "tenant");
                intent.putExtra("context", "home");
                startActivity(intent);
            });
        }
    }

    private void setupRoomRecyclerView() {
        // TỐI ƯU: Cải thiện hiệu suất RecyclerView
        roomRecyclerView.setHasFixedSize(true);  // Kích thước cố định
        roomRecyclerView.setItemViewCacheSize(20);  // Cache nhiều item hơn
        roomRecyclerView.setDrawingCacheEnabled(true);
        roomRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        
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
     * Load danh sách phòng từ database (TỐI ƯU)
     */
    private void loadRoomsFromDatabase() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        executorService.execute(() -> {
            Connection conn = null;
            List<Phong> phongList = null;
            String errorMsg = null;
            
            try {
                conn = DatabaseHelper.getConnection();
                PhongDao dao = new PhongDao();
                phongList = dao.getAllPhongAvailable(conn);
            } catch (Exception e) {
                errorMsg = e.getMessage();
            } finally {
                DatabaseHelper.releaseConnection(conn);  // TỐI ƯU: Trả về pool
            }
            
            // Update UI trên main thread
            final List<Phong> finalList = phongList;
            final String finalError = errorMsg;
            
            mainHandler.post(() -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (finalList != null && !finalList.isEmpty()) {
                    roomList.clear();
                    for (Phong phong : finalList) {
                        Room room = convertPhongToRoom(phong);
                        roomList.add(room);
                    }
                    roomAdapter.notifyDataSetChanged();
                    isDataLoaded = true;
                    Toast.makeText(MainActivity.this, 
                        "Đã tải " + finalList.size() + " phòng", 
                        Toast.LENGTH_SHORT).show();
                } else {
                    if (finalError != null) {
                        Toast.makeText(MainActivity.this, 
                            "Lỗi kết nối: " + finalError, 
                            Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, 
                            "Không có phòng nào", 
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });
    }
    
    /**
     * Convert model Phong từ database sang Room (UI model)
     */
    private Room convertPhongToRoom(Phong phong) {
        Room room = new Room();
        
        // ID phòng - Room dùng int, Phong dùng String UUID
        // Tạm thời dùng hashCode để convert
        room.setId(phong.getPhongId() != null ? phong.getPhongId().hashCode() : 0);
        
        // Lưu PhongId (UUID) từ database
        room.setPhongId(phong.getPhongId());
        
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
        
        // Long click để test database (hidden feature)
        btnFilter.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DatabaseTestActivity.class);
            startActivity(intent);
            return true;
        });
    }
    
    /**
     * Tìm kiếm phòng với filter (TỐI ƯU)
     */
    private void searchRoomsFromDatabase(String keyword, Long minPrice, Long maxPrice, String quanHuyen) {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        executorService.execute(() -> {
            Connection conn = null;
            List<Phong> phongList = null;
            String errorMsg = null;
            
            try {
                conn = DatabaseHelper.getConnection();
                PhongDao dao = new PhongDao();
                
                // Xử lý tên quận
                String finalQuanHuyen = quanHuyen;
                if (finalQuanHuyen != null && finalQuanHuyen.startsWith("Quận ")) {
                    finalQuanHuyen = finalQuanHuyen.substring(5);
                } else if (finalQuanHuyen != null && finalQuanHuyen.startsWith("Huyện ")) {
                    finalQuanHuyen = finalQuanHuyen.substring(6);
                }
                
                phongList = dao.searchPhong(conn, keyword, minPrice, maxPrice, finalQuanHuyen);
            } catch (Exception e) {
                errorMsg = e.getMessage();
            } finally {
                DatabaseHelper.releaseConnection(conn);  // TỐI ƯU: Trả về pool
            }
            
            final List<Phong> finalList = phongList;
            final String finalError = errorMsg;
            
            mainHandler.post(() -> {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                if (finalList != null) {
                    roomList.clear();
                    for (Phong phong : finalList) {
                        Room room = convertPhongToRoom(phong);
                        roomList.add(room);
                    }
                    roomAdapter.notifyDataSetChanged();
                    
                    if (finalList.isEmpty()) {
                        Toast.makeText(MainActivity.this, 
                            "Không tìm thấy phòng phù hợp", 
                            Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, 
                            "Tìm thấy " + finalList.size() + " phòng", 
                            Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, 
                        "Lỗi tìm kiếm: " + finalError, 
                        Toast.LENGTH_LONG).show();
                }
            });
        });
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
        
        // TỐI ƯU: Chỉ load data lần đầu, không load lại mỗi lần resume
        if (!isDataLoaded) {
            loadRoomsFromDatabase();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TỐI ƯU: Dọn dẹp ExecutorService
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}