package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private EditText searchInput;
    private ImageButton searchButton;
    private Button btnPriceFilter;
    private Button btnNearbyFilter;
    private Button btnRatingFilter;
    private Button btnNewestFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        sessionManager = new SessionManager(this);

        initViews();
        setupRoleDropdown();
        setupBottomNavigation();
        setupRoomRecyclerView();
        setupSearchBar();
        setupFilterButton();
        setupQuickFilters();
    }

    private void initViews() {
        btnFilter = findViewById(R.id.btnFilter);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        txtRoleSecondary = roleSwitcher.findViewById(R.id.txtRoleSecondary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);
        progressBar = findViewById(R.id.progressBar);
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);
        btnPriceFilter = findViewById(R.id.btnPriceFilter);
        btnNearbyFilter = findViewById(R.id.btnNearbyFilter);
        btnRatingFilter = findViewById(R.id.btnRatingFilter);
        btnNewestFilter = findViewById(R.id.btnNewestFilter);
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
                conn = DatabaseHelper.getConnection();
                PhongDao dao = new PhongDao();
                return dao.getAllPhongAvailable(conn);
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
    
    /**
     * Thiết lập thanh tìm kiếm
     */
    private void setupSearchBar() {
        // Tìm kiếm khi nhấn nút search
        searchButton.setOnClickListener(v -> performSearch());
        
        // Tìm kiếm khi nhấn Enter trên bàn phím
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && 
                 event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch();
                return true;
            }
            return false;
        });
    }
    
    /**
     * Thực hiện tìm kiếm
     */
    private void performSearch() {
        String keyword = searchInput.getText().toString().trim();
        
        if (keyword.isEmpty()) {
            // Nếu không có từ khóa, load lại tất cả phòng
            loadRoomsFromDatabase();
            Toast.makeText(this, "Hiển thị tất cả phòng", Toast.LENGTH_SHORT).show();
        } else {
            // Tìm kiếm với từ khóa
            searchRoomsFromDatabase(keyword, null, null, null);
        }
        
        // Ẩn bàn phím
        searchInput.clearFocus();
        android.view.inputmethod.InputMethodManager imm = 
            (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchInput.getWindowToken(), 0);
        }
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
     * Thiết lập các nút filter nhanh
     */
    private void setupQuickFilters() {
        // Nút Giá tốt - sắp xếp theo giá từ thấp đến cao
        btnPriceFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Lọc theo giá tốt", Toast.LENGTH_SHORT).show();
            // Tìm kiếm với giá từ 0 đến 5 triệu
            searchRoomsFromDatabase(null, 0L, 5000000L, null);
        });
        
        // Nút Gần tôi - lọc theo vị trí (tạm thời hiển thị tất cả)
        btnNearbyFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            // TODO: Implement location-based filtering
        });
        
        // Nút Đánh giá - lọc phòng có rating cao
        btnRatingFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Lọc theo đánh giá cao", Toast.LENGTH_SHORT).show();
            // Load tất cả và filter theo rating trong adapter
            loadRoomsFromDatabase();
        });
        
        // Nút Mới nhất - load phòng mới nhất
        btnNewestFilter.setOnClickListener(v -> {
            Toast.makeText(this, "Hiển thị phòng mới nhất", Toast.LENGTH_SHORT).show();
            loadRoomsFromDatabase();
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