package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.ui.chatbot.ChatbotActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Button btnFilter;
    private RecyclerView roomRecyclerView;
<<<<<<< HEAD
    private ArrayList<Room> roomList = new ArrayList<>();
    private SessionManager sessionManager;
    private android.view.View roleSwitcher;
=======
    // Danh sách phòng trọ
    private ArrayList<Room> roomList;
    // Danh sách phòng gốc (không bị thay đổi)
    private ArrayList<Room> originalRoomList;
    // Quản lý phiên làm việc
    private SessionManager sessionManager;
    private FloatingActionButton fabChatbot;
    private View roleSwitcher;
>>>>>>> 26753fc93360948aa995ca218c479715fbfc7ff1
    private TextView txtRolePrimary;
    private TextView txtRoleSecondary;
    private ImageView iconRole;
<<<<<<< HEAD
    private RoomAdapter roomAdapter;
=======
    // Ô tìm kiếm
    private EditText searchInput;
    // Nút tìm kiếm
    private ImageButton searchButton;
>>>>>>> 26753fc93360948aa995ca218c479715fbfc7ff1

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home);

        sessionManager = new SessionManager(this);

        initViews();
        setupRoleDropdown();
        setupBottomNavigation();
        setupRoomRecyclerView(); // Sẽ hiển thị danh sách rỗng
        setupFilterButton();
        // Thiết lập tìm kiếm
        setupSearch();
        setupChatbot();
    }

<<<<<<< HEAD
=======
    /**
     * PHƯƠNG THỨC: initRoomList()
     * CHỨC NĂNG: Tải danh sách phòng từ MockData
     * - Lấy phòng từ MockData.getRooms()
     * - Lưu vào ArrayList roomList và originalRoomList
     */
    private void initRoomList() {
        // Tạo ArrayList mới và thêm tất cả phòng từ MockData
        roomList = new ArrayList<>(MockData.getRooms());
        // Lưu bản sao gốc để dùng cho tìm kiếm
        originalRoomList = new ArrayList<>(MockData.getRooms());
    }

    /**
     * PHƯƠNG THỨC: initViews()
     * CHỨC NĂNG: Khởi tạo các view (layout elements) từ file layout XML
     */
>>>>>>> 26753fc93360948aa995ca218c479715fbfc7ff1
    private void initViews() {
        btnFilter = findViewById(R.id.btnFilter);
        roomRecyclerView = findViewById(R.id.roomRecyclerView);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        txtRoleSecondary = roleSwitcher.findViewById(R.id.txtRoleSecondary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);
        // Ô tìm kiếm
        searchInput = findViewById(R.id.searchInput);
        // Nút tìm kiếm
        searchButton = findViewById(R.id.searchButton);
    }

    private void setupRoomRecyclerView() {
        roomAdapter = new RoomAdapter(roomList, room -> {
            Intent intent = new Intent(MainActivity.this, RoomDetailActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });
        roomRecyclerView.setAdapter(roomAdapter);
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> {
            AdvancedFilterBottomSheet filterSheet = AdvancedFilterBottomSheet.newInstance();
            filterSheet.show(getSupportFragmentManager(), "AdvancedFilter");
        });
        
        // Long click để test database (hidden feature)
        btnFilter.setOnLongClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, DatabaseTestActivity.class);
            startActivity(intent);
            return true;
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

<<<<<<< HEAD
=======
    /**
     * PHƯƠNG THỨC: setupBottomNavigation()
     * CHỨC NĂNG: Thiết lập thanh điều hướng dưới cùng của app
     * - Đánh dấu tab "home" là đang hoạt động
     */
    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void setupChatbot() {
        fabChatbot = findViewById(R.id.fabChatbot);
        if (fabChatbot != null) {
            fabChatbot.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ChatbotActivity.class);
                intent.putExtra("user_type", "tenant");
                intent.putExtra("context", "home");
                startActivity(intent);
            });
        }
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
>>>>>>> 26753fc93360948aa995ca218c479715fbfc7ff1
    @Override
    protected void onResume() {
        super.onResume();
        applyRoleUI();
    }
}
