package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import com.example.QuanLyPhongTro_App.ui.auth.DangKyNguoiThueActivity;
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
    private FloatingActionButton fabChatbot;
    // Icon tin nhắn
    private ImageView btnMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        sessionManager = new SessionManager(this);

        // Initialize roomList BEFORE setting up views
        roomList = new ArrayList<>();
        originalRoomList = new ArrayList<>();

        // Khởi tạo các view từ layout
        initViews();
        setupRoleDropdown();
        setupBottomNavigation();
        setupRoomRecyclerView();
        setupSearchBar();
        setupFilterButton();
        setupQuickFilters();
        setupChatbot();
        // Thiết lập tìm kiếm
        setupSearch();

        // Load rooms from API AFTER views are set up
        loadRoomsFromAPI();
    }

    /**
     * PHƯƠNG THỨC: loadRoomsFromAPI()
     * Load rooms from API endpoint with fallback to MockData
     */
    private void loadRoomsFromAPI() {
        Log.d("MainActivity", "Loading rooms from API...");

        com.example.QuanLyPhongTro_App.data.repository.RoomRepository roomRepository =
            new com.example.QuanLyPhongTro_App.data.repository.RoomRepository();

        // Get first page with price range 0 - 10,000,000
        roomRepository.getRooms(1, 50, 0, 10000000, new com.example.QuanLyPhongTro_App.data.repository.RoomRepository.RoomsCallback() {
            @Override
            public void onSuccess(java.util.List<com.example.QuanLyPhongTro_App.data.repository.RoomRepository.RoomDto> rooms, int totalCount) {
                Log.d("MainActivity", "✅ Got " + rooms.size() + " rooms from API");

                // Convert RoomDto to Room
                ArrayList<Room> convertedRooms = convertRoomDtosToRooms(rooms);

                // Update UI on main thread
                runOnUiThread(() -> {
                    // If API returns empty or null, use MockData as fallback
                    if (convertedRooms == null || convertedRooms.isEmpty()) {
                        Log.w("MainActivity", "⚠️ API returned no rooms, using MockData");
                        loadMockDataFallback();
                    } else {
                        // Clear and update the lists
                        roomList.clear();
                        roomList.addAll(convertedRooms);
                        originalRoomList.clear();
                        originalRoomList.addAll(convertedRooms);

                        // Notify adapter of data change
                        if (roomRecyclerView != null && roomRecyclerView.getAdapter() != null) {
                            roomRecyclerView.getAdapter().notifyDataSetChanged();
                        }

                        Toast.makeText(MainActivity.this, "Đã tải " + rooms.size() + " phòng", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String error) {
                Log.e("MainActivity", "❌ Error loading rooms: " + error);

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Đang tải dữ liệu mẫu...", Toast.LENGTH_SHORT).show();
                    // Load MockData as fallback when API fails
                    loadMockDataFallback();
                });
            }
        });
    }

    /**
     * Load MockData as fallback when API fails or returns no data
     */
    private void loadMockDataFallback() {
        Log.d("MainActivity", "Loading MockData fallback...");

        // Get mock data from MockData class
        java.util.List<Room> mockRooms = com.example.QuanLyPhongTro_App.data.MockData.getRooms();

        if (mockRooms != null && !mockRooms.isEmpty()) {
            roomList.clear();
            roomList.addAll(new ArrayList<>(mockRooms));
            originalRoomList.clear();
            originalRoomList.addAll(new ArrayList<>(mockRooms));

            // Notify adapter
            if (roomRecyclerView != null && roomRecyclerView.getAdapter() != null) {
                roomRecyclerView.getAdapter().notifyDataSetChanged();
            }

            Log.d("MainActivity", "✅ Loaded " + mockRooms.size() + " rooms from MockData");
            Toast.makeText(this, "Đã tải " + mockRooms.size() + " phòng (dữ liệu mẫu)", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("MainActivity", "❌ MockData is also empty!");
            Toast.makeText(this, "Không có dữ liệu phòng", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Convert RoomDto list to Room list (for adapter compatibility)
     */
    private ArrayList<Room> convertRoomDtosToRooms(java.util.List<com.example.QuanLyPhongTro_App.data.repository.RoomRepository.RoomDto> roomDtos) {
        ArrayList<Room> rooms = new ArrayList<>();

        for (com.example.QuanLyPhongTro_App.data.repository.RoomRepository.RoomDto dto : roomDtos) {
            // Use simple constructor for demo
            String priceText = formatPrice(dto.getGiaTien());
            String location = "Phòng trọ"; // Default location

            Room room = new Room(
                dto.getTieuDe() != null ? dto.getTieuDe() : "Phòng",
                priceText,
                location,
                android.R.drawable.ic_menu_gallery // Default image
            );

            // Note: Room class uses private fields, so we can't set them directly
            // The Room constructor should be called with all needed data
            // If we need to add description, we should modify Room class or use full constructor

            rooms.add(room);
        }

        return rooms;
    }

    /**
     * Format price from long to string (e.g., 2500000 -> "2.5 triệu")
     */
    private String formatPrice(Long price) {
        if (price == null) return "0 đ";

        if (price >= 1_000_000) {
            double million = price / 1_000_000.0;
            return String.format("%.1f triệu", million);
        } else if (price >= 1_000) {
            double thousand = price / 1_000.0;
            return String.format("%.0f nghìn", thousand);
        }
        return price + " đ";
    }

    /**
     * PHƯƠNG THỨC: initViews()
     * CHỨC NĂNG: Khởi tạo các view (layout elements) từ file layout XML
     */
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
        fabChatbot = findViewById(R.id.fabChatbot);
    }

    /**
     * Thiết lập chatbot button
     */
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

        // ID phòng từ SQL Server database
        room.setPhongId(phong.getPhongId());

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
        // Icon tin nhắn
        btnMessages = findViewById(R.id.btnMessages);

        // Setup message button click listener
        setupMessagesButton();
    }

    private void setupMessagesButton() {
        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                if (!sessionManager.isLoggedIn()) {
                    Toast.makeText(this, "Vui lòng đăng nhập để nhắn tin", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("targetRole", "tenant");
                    startActivity(intent);
                    return;
                }

                // Mở ChatListActivity để xem danh sách tin nhắn
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent);
            });
        }
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
        // Ensure roomList is not null
        if (roomList == null) {
            roomList = new ArrayList<>();
        }

        // Tạo adapter với callback khi click vào phòng
        RoomAdapter adapter = new RoomAdapter(roomList, room -> {
            // Khi click vào một phòng:
            // 1. Tạo intent mở RoomDetailActivity
            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            // 2. Truyền object phòng qua intent
            intent.putExtra("room", room);
            // 3. Mở activity
            startActivity(intent);
        });

        // Gắn adapter vào RecyclerView
        roomRecyclerView.setAdapter(adapter);
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

        // Thiết lập listener để nhận kết quả lọc
        filterSheet.setFilterListener(filters -> {
            applyFilters(filters);
        });

        // Hiển thị BottomSheet
        filterSheet.show(getSupportFragmentManager(), "AdvancedFilter");
    }

    /**
     * PHƯƠNG THỨC: applyFilters()
     * CHỨC NĂNG: Áp dụng bộ lọc lên danh sách phòng
     * - Lọc theo giá, khu vực, loại phòng, tiện nghi
     */
    private void applyFilters(Bundle filters) {
        // Lấy danh sách phòng gốc
        ArrayList<Room> filteredList = new ArrayList<>(originalRoomList);

        // 1. Lọc theo giá
        float minPrice = filters.getFloat("minPrice", 0.5f);
        float maxPrice = filters.getFloat("maxPrice", 10.0f);

        ArrayList<Room> priceFiltered = new ArrayList<>();
        for (Room room : filteredList) {
            double priceInMillions = room.getPriceValue() / 1000000.0;
            if (priceInMillions >= minPrice && priceInMillions <= maxPrice) {
                priceFiltered.add(room);
            }
        }
        filteredList = priceFiltered;

        // 2. Lọc theo khu vực
        String selectedArea = filters.getString("area");
        if (selectedArea != null && !selectedArea.isEmpty()) {
            ArrayList<Room> areaFiltered = new ArrayList<>();
            for (Room room : filteredList) {
                if (room.getLocation() != null && room.getLocation().contains(selectedArea)) {
                    areaFiltered.add(room);
                }
            }
            filteredList = areaFiltered;
        }

        // 3. Lọc theo loại phòng (nếu có)
        ArrayList<String> roomTypes = filters.getStringArrayList("roomTypes");
        if (roomTypes != null && !roomTypes.isEmpty()) {
            ArrayList<Room> typeFiltered = new ArrayList<>();
            for (Room room : filteredList) {
                // Kiểm tra roomType field trước
                if (room.getRoomType() != null && roomTypes.contains(room.getRoomType())) {
                    typeFiltered.add(room);
                } else {
                    // Fallback: Kiểm tra title nếu roomType không có
                    String title = room.getTitle().toLowerCase();
                    for (String type : roomTypes) {
                        if (type.equals("Nguyên căn") && (title.contains("nguyên căn") || title.contains("nhà") || title.contains("căn hộ"))) {
                            typeFiltered.add(room);
                            break;
                        } else if (type.equals("Phòng riêng") && (title.contains("phòng") || title.contains("studio"))) {
                            typeFiltered.add(room);
                            break;
                        } else if (type.equals("Ở ghép") && title.contains("ghép")) {
                            typeFiltered.add(room);
                            break;
                        }
                    }
                }
            }
            if (!typeFiltered.isEmpty()) {
                filteredList = typeFiltered;
            }
        }

        // 4. Lọc theo tiện nghi (nếu có)
        ArrayList<String> amenities = filters.getStringArrayList("amenities");
        if (amenities != null && !amenities.isEmpty()) {
            ArrayList<Room> amenityFiltered = new ArrayList<>();
            for (Room room : filteredList) {
                boolean hasAllAmenities = true;

                // Kiểm tra amenities field trước
                if (room.getAmenities() != null && !room.getAmenities().isEmpty()) {
                    for (String amenity : amenities) {
                        if (!room.getAmenities().contains(amenity)) {
                            hasAllAmenities = false;
                            break;
                        }
                    }
                } else {
                    // Fallback: Kiểm tra description nếu amenities không có
                    String description = room.getDescription() != null ? room.getDescription().toLowerCase() : "";
                    String title = room.getTitle().toLowerCase();

                    for (String amenity : amenities) {
                        boolean hasAmenity = false;
                        if (amenity.equals("Máy lạnh") && (description.contains("máy lạnh") || description.contains("điều hòa") || title.contains("điều hòa"))) {
                            hasAmenity = true;
                        } else if (amenity.equals("Wi-Fi") && (description.contains("wifi") || description.contains("wi-fi"))) {
                            hasAmenity = true;
                        } else if (amenity.equals("Giữ xe") && (description.contains("giữ xe") || description.contains("đậu xe") || description.contains("garage"))) {
                            hasAmenity = true;
                        } else if (amenity.equals("WC riêng") && (description.contains("wc riêng") || description.contains("toilet riêng"))) {
                            hasAmenity = true;
                        }

                        if (!hasAmenity) {
                            hasAllAmenities = false;
                            break;
                        }
                    }
                }

                if (hasAllAmenities) {
                    amenityFiltered.add(room);
                }
            }
            if (!amenityFiltered.isEmpty()) {
                filteredList = amenityFiltered;
            }
        }

        // Cập nhật RecyclerView với danh sách đã lọc
        roomList.clear();
        roomList.addAll(filteredList);
        roomRecyclerView.getAdapter().notifyDataSetChanged();

        // Xóa text tìm kiếm khi áp dụng filter
        searchInput.setText("");

        // Hiển thị thông báo kết quả
        String message = "Tìm thấy " + filteredList.size() + " phòng phù hợp";
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show();
    }

    /**
     * PHƯƠNG THỨC: setupSearch()
     * CHỨC NĂNG: Thiết lập chức năng tìm kiếm
     * - Tìm kiếm theo tên phòng khi người dùng nhập
     * - Tìm kiếm khi nhấn nút search
     */
    private void setupSearch() {
        // Tìm kiếm khi nhập text (real-time search)
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần xử lý
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Tìm kiếm khi text thay đổi
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần xử lý
            }
        });

        // Tìm kiếm khi nhấn nút search
        searchButton.setOnClickListener(v -> {
            String query = searchInput.getText().toString();
            performSearch(query);
        });
    }

    /**
     * PHƯƠNG THỨC: performSearch()
     * CHỨC NĂNG: Thực hiện tìm kiếm phòng theo từ khóa
     * - Tìm kiếm trong tên phòng (title)
     * - Hỗ trợ tìm kiếm không dấu
     * - Hiển thị kết quả tìm kiếm
     * @param query Từ khóa tìm kiếm
     */
    private void performSearch(String query) {
        // Nếu query rỗng, hiển thị tất cả phòng
        if (query == null || query.trim().isEmpty()) {
            roomList.clear();
            roomList.addAll(originalRoomList);
            roomRecyclerView.getAdapter().notifyDataSetChanged();
            return;
        }

        // Chuyển query về lowercase và bỏ dấu để tìm kiếm
        String searchQuery = removeVietnameseAccents(query.toLowerCase().trim());

        // Lọc danh sách phòng
        ArrayList<Room> searchResults = new ArrayList<>();
        for (Room room : originalRoomList) {
            // Kiểm tra nếu tên phòng chứa từ khóa tìm kiếm
            if (room.getTitle() != null) {
                String roomTitle = removeVietnameseAccents(room.getTitle().toLowerCase());
                if (roomTitle.contains(searchQuery)) {
                    searchResults.add(room);
                }
            }
        }

        // Cập nhật danh sách hiển thị
        roomList.clear();
        roomList.addAll(searchResults);
        roomRecyclerView.getAdapter().notifyDataSetChanged();

        // Hiển thị thông báo kết quả (tùy chọn)
        if (searchResults.isEmpty()) {
            android.widget.Toast.makeText(this,
                "Không tìm thấy phòng phù hợp",
                android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * PHƯƠNG THỨC: removeVietnameseAccents()
     * CHỨC NĂNG: Chuyển đổi chuỗi tiếng Việt có dấu thành không dấu
     * - Hỗ trợ tìm kiếm linh hoạt hơn
     * @param str Chuỗi cần chuyển đổi
     * @return Chuỗi không dấu
     */
    private String removeVietnameseAccents(String str) {
        if (str == null) return "";

        // Bảng chuyển đổi các ký tự có dấu sang không dấu
        str = str.replaceAll("[àáạảãâầấậẩẫăằắặẳẵ]", "a");
        str = str.replaceAll("[èéẹẻẽêềếệểễ]", "e");
        str = str.replaceAll("[ìíịỉĩ]", "i");
        str = str.replaceAll("[òóọỏõôồốộổỗơờớợởỡ]", "o");
        str = str.replaceAll("[ùúụủũưừứựửữ]", "u");
        str = str.replaceAll("[ỳýỵỷỹ]", "y");
        str = str.replaceAll("đ", "d");

        str = str.replaceAll("[ÀÁẠẢÃÂẦẤẬẨẪĂẰẮẶẲẴ]", "A");
        str = str.replaceAll("[ÈÉẸẺẼÊỀẾỆỂỄ]", "E");
        str = str.replaceAll("[ÌÍỊỈĨ]", "I");
        str = str.replaceAll("[ÒÓỌỎÕÔỒỐỘỔỖƠỜỚỢỞỠ]", "O");
        str = str.replaceAll("[ÙÚỤỦŨƯỪỨỰỬỮ]", "U");
        str = str.replaceAll("[ỲÝỴỶỸ]", "Y");
        str = str.replaceAll("Đ", "D");

        return str;
    }

    /**
     * PHƯƠNG THỨC: onResume()
     * CHỨC NĂNG: Được gọi mỗi khi Activity quay trở lại từ background
     * - Cập nhật giao diện vai trò
     */
    @Override
    protected void onResume() {
        super.onResume();
        applyRoleUI();
        // Refresh data khi quay lại màn hình
        loadRoomsFromDatabase();
    }
}