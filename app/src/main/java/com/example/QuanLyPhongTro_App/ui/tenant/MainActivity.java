package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

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
 * - Hiển thị danh sách phòng trọ từ MockData (dữ liệu Đà Nẵng)
 * - Cho phép tìm kiếm/lọc phòng
 * - Cho phép chuyển đổi giữa vai trò Người thuê và Chủ trọ
 * - Cung cấp điều hướng đến các màn hình khác
 *
 * DỮ LIỆU: Sử dụng MockData.getRooms() để lấy danh sách phòng
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
    // Danh sách phòng gốc (không bị thay đổi)
    private ArrayList<Room> originalRoomList;
    // Quản lý phiên làm việc
    private SessionManager sessionManager;
    private View roleSwitcher;
    private TextView txtRolePrimary;
    // Hiển thị tùy chọn chuyển vai trò
    private TextView txtRoleSecondary;
    // Icon hiển thị vai trò
    private ImageView iconRole;
    // Ô tìm kiếm
    private EditText searchInput;
    // Nút tìm kiếm
    private ImageButton searchButton;
    // Icon tin nhắn
    private ImageView btnMessages;

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

        // Initialize roomList BEFORE setting up views
        roomList = new ArrayList<>();
        originalRoomList = new ArrayList<>();

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
        // Ô tìm kiếm
        searchInput = findViewById(R.id.searchInput);
        // Nút tìm kiếm
        searchButton = findViewById(R.id.searchButton);
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
     * PHƯƠNG THỨC: setupRoleDropdown()
     * CHỨC NĂNG: Thiết lập dropdown để chọn vai trò (Người thuê hoặc Chủ trọ)
     * - Nếu người dùng là chủ trọ: Cho phép chuyển sang Chủ trọ
     * - Nếu người dùng chỉ là Người thuê: Cho phép đăng nhập làm Chủ trọ
     */
    private void setupRoleDropdown() {
        roleSwitcher.setOnClickListener(v -> {
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
        if (!sessionManager.isLoggedIn()) {
            txtRolePrimary.setText("Khách");
            txtRoleSecondary.setText("Đăng nhập");
            iconRole.setImageResource(R.drawable.ic_user);
            return;
        }

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
        // Cập nhật lại giao diện vai trò khi quay trở lại
        applyRoleUI();
    }
}
