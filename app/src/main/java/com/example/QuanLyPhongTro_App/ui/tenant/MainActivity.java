package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;
import com.example.QuanLyPhongTro_App.data.repository.RoomRepository;
import com.example.QuanLyPhongTro_App.data.repository.RoomRepository.RoomDto;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button btnFilter;
    private RecyclerView roomRecyclerView;
    private ArrayList<Room> roomList = new ArrayList<>();
    private ArrayList<Room> originalRoomList = new ArrayList<>();
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
    // FAB chatbot removed - không có trong layout
    // Icon tin nhắn
    private ImageView btnMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        sessionManager = new SessionManager(this);

        roomList = new ArrayList<>();
        originalRoomList = new ArrayList<>();

        initViews();
        setupRoleDropdown();
        setupBottomNavigation();
        setupRoomRecyclerView();
        setupSearchBar();
        setupFilterButton();
        setupQuickFilters();
        setupChatbot();

        loadRoomsFromAPI();
    }

    /**
     * PHƯƠNG THỨC: loadRoomsFromAPI()
     * Load rooms from API endpoint with fallback to MockData
     */
    private void loadRoomsFromAPI() {
        Log.d("MainActivity", "Loading rooms from API...");

        RoomRepository roomRepository =
            new RoomRepository();

        // Get first page with price range 0 - 10,000,000
        roomRepository.getRooms(1, 50, 0, 10000000, new RoomRepository.RoomsCallback() {
            @Override
            public void onSuccess(java.util.List<RoomDto> rooms, int totalCount) {
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
     * Load MockData as fallback when API fails or returns no data.
     * NOTE: Project currently doesn't ship MockData anymore, so fallback is an empty list.
     */
    private void loadMockDataFallback() {
        Log.w("MainActivity", "No MockData available - showing empty list");

        roomList.clear();
        originalRoomList.clear();
        if (roomAdapter != null) roomAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Không tải được dữ liệu từ API", Toast.LENGTH_SHORT).show();
    }

    /**
     * Convert RoomDto list to Room list (for adapter compatibility)
     */
    private ArrayList<Room> convertRoomDtosToRooms(java.util.List<RoomDto> roomDtos) {
        ArrayList<Room> rooms = new ArrayList<>();

        for (RoomDto dto : roomDtos) {
            // Tạo object Room đầy đủ nhất có thể từ DTO
            Room room = new Room();

            // ✅ Quan trọng nhất: set phongId để các màn khác dùng (chi tiết/đặt lịch)
            try {
                String phongId = dto.getPhongId();
                if (phongId != null && !phongId.trim().isEmpty()) {
                    room.setPhongId(phongId);
                }
            } catch (Exception ignored) {
            }

            // Title
            room.setTitle(dto.getTieuDe() != null ? dto.getTieuDe() : "Phòng");

            // Price
            if (dto.getGiaTien() != null) {
                room.setPriceValue(dto.getGiaTien());
            }

            // Location: ưu tiên quận/huyện nếu DTO có, fallback text cũ
            String location = null;
            try {
                // Một số DTO có thể có field quan/huyen (tuỳ backend)
                java.lang.reflect.Method m = dto.getClass().getMethod("getQuanHuyen");
                Object v = m.invoke(dto);
                if (v != null) location = String.valueOf(v);
            } catch (Exception ignored) {
            }
            if (location == null || location.trim().isEmpty()) {
                location = "Phòng trọ";
            }
            room.setLocation(location);

            // Description (MoTa)
            try {
                String moTa = dto.getMoTa();
                if (moTa != null && !moTa.trim().isEmpty()) {
                    room.setDescription(moTa);
                }
            } catch (Exception ignored) {
            }

            // Address: nếu DTO có DiaChiNhaTro thì set để hiển thị
            try {
                java.lang.reflect.Method m = dto.getClass().getMethod("getDiaChiNhaTro");
                Object v = m.invoke(dto);
                if (v != null) {
                    String addr = String.valueOf(v);
                    if (!addr.trim().isEmpty()) room.setAddress(addr);
                }
            } catch (Exception ignored) {
            }

            // Default image
            room.setImageResId(android.R.drawable.ic_menu_gallery);

            // Ảnh phòng: ưu tiên ảnh đại diện từ backend (AnhDaiDien), fallback danhSachAnhUrl[0]
            String cover = null;
            try {
                cover = dto.getAnhDaiDien();
            } catch (Exception ignored) {
            }
            if (cover != null && !cover.trim().isEmpty()) {
                room.setImageUrl(cover);
            } else if (dto.getDanhSachAnhUrl() != null && !dto.getDanhSachAnhUrl().isEmpty()) {
                room.setImageUrl(dto.getDanhSachAnhUrl().get(0));
            }

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
            return String.format(Locale.getDefault(), "%.1f triệu", million);
        } else if (price >= 1_000) {
            double thousand = price / 1_000.0;
            return String.format(Locale.getDefault(), "%.0f nghìn", thousand);
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
        // fabChatbot removed - không có trong layout hiện tại
    }

    /**
     * Thiết lập chatbot button
     */
    // Chatbot currently disabled (no FAB in layout)
    private void setupChatbot() {
        // FAB chatbot removed - không có trong layout hiện tại
        // Nếu cần kích hoạt lại, thêm FloatingActionButton với id fabChatbot vào layout
    }

    private void setupRoomRecyclerView() {
        roomAdapter = new RoomAdapter(roomList, room -> {
            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            intent.putExtra("room", room);
            // Quan trọng: truyền thêm phongId để màn chi tiết có thể fetch đầy đủ
            if (room != null && room.getPhongId() != null) {
                intent.putExtra("roomId", room.getPhongId());
            }
            startActivity(intent);
        });
        roomRecyclerView.setAdapter(roomAdapter);

        Log.d("MainActivity", "API-only mode (direct DB disabled)");
    }

    /**
     * Legacy DB loader kept for call sites, but it is effectively API.
     */
    private void loadRoomsFromDatabase() {
        Log.w("MainActivity", "loadRoomsFromDatabase() is disabled. Using API.");
        loadRoomsFromAPI();
    }

    private void setupQuickFilters() {
        // Nút Giá tốt - reload API (server supports min/max params)
        if (btnPriceFilter != null) {
            btnPriceFilter.setOnClickListener(v -> {
                Toast.makeText(this, "Lọc theo giá tốt", Toast.LENGTH_SHORT).show();
                loadRoomsFromAPIWithPrice(0L, 5_000_000L);
            });
        }

        if (btnNearbyFilter != null) {
            btnNearbyFilter.setOnClickListener(v -> Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show());
        }

        if (btnRatingFilter != null) {
            btnRatingFilter.setOnClickListener(v -> {
                Toast.makeText(this, "Lọc theo đánh giá cao", Toast.LENGTH_SHORT).show();
                loadRoomsFromAPI();
            });
        }

        if (btnNewestFilter != null) {
            btnNewestFilter.setOnClickListener(v -> {
                Toast.makeText(this, "Hiển thị phòng mới nhất", Toast.LENGTH_SHORT).show();
                loadRoomsFromAPI();
            });
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void setupSearchBar() {
        if (searchButton != null) {
            searchButton.setOnClickListener(v -> performSearch());
        }

        if (searchInput != null) {
            searchInput.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    performSearch();
                    return true;
                }
                return false;
            });
        }

        // Icon tin nhắn
        btnMessages = findViewById(R.id.btnMessages);
        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatListActivity.class);
                startActivity(intent);
            });
        }
    }

    private void performSearch() {
        String keyword = searchInput != null ? searchInput.getText().toString().trim() : "";

        if (keyword.isEmpty()) {
            loadRoomsFromAPI();
            return;
        }

        ArrayList<Room> filtered = new ArrayList<>();
        String q = keyword.toLowerCase();
        for (Room r : originalRoomList.isEmpty() ? roomList : originalRoomList) {
            String title = r.getTitle() != null ? r.getTitle().toLowerCase() : "";
            String loc = r.getLocation() != null ? r.getLocation().toLowerCase() : "";
            if (title.contains(q) || loc.contains(q)) {
                filtered.add(r);
            }
        }
        roomList.clear();
        roomList.addAll(filtered);
        if (roomAdapter != null) roomAdapter.notifyDataSetChanged();

        Toast.makeText(this, "Tìm thấy " + filtered.size() + " phòng", Toast.LENGTH_SHORT).show();
    }

    private void setupFilterButton() {
        if (btnFilter == null) return;

        btnFilter.setOnClickListener(v -> {
            // Use existing bottom sheet if present; otherwise provide a simple quick filter.
            try {
                AdvancedFilterBottomSheet sheet = AdvancedFilterBottomSheet.newInstance();
                sheet.setFilterListener(filters -> {
                    float minPrice = filters.getFloat("minPrice", 0);
                    float maxPrice = filters.getFloat("maxPrice", 100);
                    loadRoomsFromAPIWithPrice((long) (minPrice * 1_000_000L), (long) (maxPrice * 1_000_000L));
                });
                sheet.show(getSupportFragmentManager(), "AdvancedFilter");
            } catch (Throwable t) {
                new AlertDialog.Builder(this)
                        .setTitle("Lọc")
                        .setMessage("Không mở được bộ lọc nâng cao. Sẽ hiển thị tất cả phòng.")
                        .setPositiveButton("OK", null)
                        .show();
                loadRoomsFromAPI();
            }
        });
    }

    private void setupRoleDropdown() {
        if (roleSwitcher == null) return;

        roleSwitcher.setOnClickListener(v -> {
            if (!sessionManager.isLoggedIn()) {
                new AlertDialog.Builder(this)
                        .setTitle("Chọn giao diện")
                        .setItems(new String[]{"Đăng nhập Người thuê", "Đăng nhập Chủ trọ"}, (dialog, which) -> {
                            Intent intent = new Intent(this, LoginActivity.class);
                            intent.putExtra("targetRole", which == 0 ? "tenant" : "landlord");
                            startActivity(intent);
                        })
                        .setNegativeButton("Hủy", null)
                        .show();
            } else {
                Toast.makeText(this, "Bạn đang đăng nhập: " + sessionManager.getUserRole(), Toast.LENGTH_SHORT).show();
            }
        });

        applyRoleUI();
    }

    private void applyRoleUI() {
        // Minimal UI setup, keep original layout texts if present.
        String role = sessionManager.getDisplayRole();
        if (role == null || role.isEmpty()) {
            role = sessionManager.getUserRole();
        }

        if (txtRolePrimary != null) {
            txtRolePrimary.setText("landlord".equals(role) ? "Chủ trọ" : "Người thuê");
        }
        if (txtRoleSecondary != null) {
            txtRoleSecondary.setText("landlord".equals(role) ? "(Quản lý phòng)" : "(Tìm phòng)");
        }
        if (iconRole != null) {
            iconRole.setImageResource("landlord".equals(role) ? R.drawable.ic_home : R.drawable.ic_search);
        }
    }

    /**
     * Reload rooms from API with price range.
     */
    private void loadRoomsFromAPIWithPrice(long minPrice, long maxPrice) {
        Log.d("MainActivity", "Loading rooms from API with price range: " + minPrice + "-" + maxPrice);

        RoomRepository roomRepository =
                new RoomRepository();

        roomRepository.getRooms(1, 50, minPrice, maxPrice, new RoomRepository.RoomsCallback() {
            @Override
            public void onSuccess(java.util.List<RoomDto> rooms, int totalCount) {
                ArrayList<Room> convertedRooms = convertRoomDtosToRooms(rooms);
                runOnUiThread(() -> {
                    if (convertedRooms == null || convertedRooms.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Không có phòng phù hợp", Toast.LENGTH_SHORT).show();
                        roomList.clear();
                        if (roomAdapter != null) roomAdapter.notifyDataSetChanged();
                        return;
                    }

                    roomList.clear();
                    roomList.addAll(convertedRooms);
                    originalRoomList.clear();
                    originalRoomList.addAll(convertedRooms);

                    if (roomAdapter != null) roomAdapter.notifyDataSetChanged();
                    Toast.makeText(MainActivity.this, "Tìm thấy " + rooms.size() + " phòng", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                Log.e("MainActivity", "Error loading rooms (price filter): " + error);
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu: " + error, Toast.LENGTH_LONG).show());
            }
        });
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
        loadRoomsFromAPI();
    }
}
