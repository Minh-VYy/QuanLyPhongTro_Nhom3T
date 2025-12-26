package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.ui.auth.DangKyNguoiThueActivity;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Activity hiển thị chi tiết phòng trọ
 * ĐÃ CẬP NHẬT: Thêm hiển thị đánh giá phòng và đánh giá chủ trọ riêng biệt
 */
public class RoomDetailActivity extends AppCompatActivity {

    private static final String TAG = "RoomDetailActivity";
    public static final String EXTRA_ROOM_ID = "roomId";
    public static final String EXTRA_ROOM = "room";

    // ========== VIEWS ==========
    private EditText searchInput;
    private ImageView roomImage, shareButtonHeader, moreButtonHeader, backButton;
    private TextView detailTitle, detailPrice, detailLocation, detailArea, detailDescription, detailAddress;

    // ========== VIEWS MỚI - ĐÁNH GIÁ ==========
    private TextView roomRatingText;          // Đánh giá của phòng
    private TextView landlordName;
    private TextView landlordRatingText;      // Đánh giá của chủ trọ
    private View roomRatingContainer;         // Container để ẩn/hiện đánh giá phòng
    private View landlordRatingContainer;     // Container để ẩn/hiện đánh giá chủ trọ

    private Button contactButton, saveButton, bookButton, viewMapButton, getDirectionsButton;
    private RecyclerView amenitiesRecyclerView, suggestedRoomsRecyclerView;

    // ========== SESSION & DATA ==========
    private SessionManager sessionManager;
    private Room currentRoom; // Lưu room hiện tại để truyền sang BookingCreateActivity
    private String phongId;
    private String landlordUserId; // UUID của chủ trọ để chat

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_room_detail);

        sessionManager = new SessionManager(this);

        // Ưu tiên lấy roomId để fetch detail đầy đủ
        phongId = getIntent().getStringExtra(EXTRA_ROOM_ID);
        currentRoom = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
        if ((phongId == null || phongId.trim().isEmpty()) && currentRoom != null) {
            phongId = currentRoom.getPhongId();
        }

        initViews();

        // Hiển thị tạm dữ liệu intent (nếu có)
        setupData(currentRoom);
        setupClickListeners();

        // Luôn cố gắng fetch từ API để có đủ chủ trọ + tiện nghi
        if (phongId != null && !phongId.trim().isEmpty()) {
            fetchRoomDetail(phongId);
        } else {
            Log.w(TAG, "No phongId provided. Room detail will be incomplete.");
        }
    }

    private void initViews() {
        // Views cũ
        roomImage = findViewById(R.id.roomImage);
        shareButtonHeader = findViewById(R.id.shareButtonHeader);
        moreButtonHeader = findViewById(R.id.moreButtonHeader);
        backButton = findViewById(R.id.backButton);
        searchInput = findViewById(R.id.searchInput);
        detailTitle = findViewById(R.id.detailTitle);
        detailPrice = findViewById(R.id.detailPrice);
        detailLocation = findViewById(R.id.detailLocation);
        detailArea = findViewById(R.id.detailArea);
        detailDescription = findViewById(R.id.detailDescription);
        detailAddress = findViewById(R.id.detailAddress);

        // ========== VIEWS MỚI - ĐÁNH GIÁ ==========
        roomRatingText = findViewById(R.id.roomRatingText);
        roomRatingContainer = findViewById(R.id.roomRatingContainer);
        landlordName = findViewById(R.id.landlordName);
        landlordRatingText = findViewById(R.id.landlordRatingText);
        landlordRatingContainer = findViewById(R.id.landlordRatingContainer);

        // Views khác
        contactButton = findViewById(R.id.contactButton);
        saveButton = findViewById(R.id.saveButton);
        bookButton = findViewById(R.id.bookButton);
        viewMapButton = findViewById(R.id.viewMapButton);
        getDirectionsButton = findViewById(R.id.getDirectionsButton);
        amenitiesRecyclerView = findViewById(R.id.amenitiesGridView);
        suggestedRoomsRecyclerView = findViewById(R.id.suggestedRoomsRecyclerView);
    }


    private void setupData(Room room) {
        if (room != null) {
            // ========== HIỂN THỊ THÔNG TIN CƠ BẢN ==========
            // Hình ảnh
            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                // TODO: Dùng Glide để load ảnh từ URL
                // Glide.with(this).load(room.getImageUrl()).into(roomImage);
                roomImage.setImageResource(R.drawable.tro); // Tạm thời
            } else if (room.getImageResId() != 0) {
                roomImage.setImageResource(room.getImageResId());
            }

            // Tiêu đề và giá
            detailTitle.setText(room.getTitle());
            // SỬ DỤNG getFormattedPrice() - tự động format từ database
            detailPrice.setText(room.getFormattedPrice());

            // Vị trí và diện tích
            detailLocation.setText(room.getLocation());
            // SỬ DỤNG getFormattedArea() - tự động format
            if (room.getArea() > 0) {
                detailArea.setText(room.getFormattedArea());
            } else {
                detailArea.setText("20m²"); // Giá trị mặc định tạm thời
            }

            // Mô tả
            if (room.getDescription() != null && !room.getDescription().isEmpty()) {
                detailDescription.setText(room.getDescription());
            } else {
                detailDescription.setText("Phòng trọ mới xây, sạch sẽ, thoáng mát. Có cửa sổ lớn, ánh sáng tự nhiên.");
            }

            // Địa chỉ
            if (room.getAddress() != null && !room.getAddress().isEmpty()) {
                detailAddress.setText(room.getAddress());
            } else {
                detailAddress.setText("123 Đường ABC, " + room.getLocation());
            }

            // ========== HIỂN THỊ ĐÁNH GIÁ PHÒNG ==========
            if (room.hasRoomRating()) {
                // Phòng có đánh giá -> hiển thị
                roomRatingContainer.setVisibility(View.VISIBLE);
                roomRatingText.setText(room.getFormattedRoomRating());
            } else {
                // Chưa có đánh giá -> ẩn hoặc hiển thị "Chưa có đánh giá"
                roomRatingContainer.setVisibility(View.VISIBLE);
                roomRatingText.setText("Chưa có đánh giá");
            }

            // ========== HIỂN THỊ THÔNG TIN CHỦ TRỌ ==========
            if (room.getLandlordName() != null && !room.getLandlordName().isEmpty()) {
                landlordName.setText(room.getLandlordName());
            } else {
                landlordName.setText("Nguyễn Văn A"); // Tạm thời
            }

            // ========== HIỂN THỊ ĐÁNH GIÁ CHỦ TRỌ ==========
            if (room.hasLandlordRating()) {
                // Chủ trọ có đánh giá -> hiển thị
                landlordRatingContainer.setVisibility(View.VISIBLE);
                landlordRatingText.setText(room.getFormattedLandlordRating());
            } else {
                // Chưa có đánh giá -> ẩn hoặc hiển thị "Chưa có đánh giá"
                landlordRatingContainer.setVisibility(View.VISIBLE);
                landlordRatingText.setText("Chưa có đánh giá");
            }

            setupAmenities();
            setupSuggestedRooms();
            setupImageBadge(room);
        }
    }

    private void setupImageBadge(Room room) {
        TextView imageBadge = findViewById(R.id.imageBadge);
        // Kiểm tra trường isNew từ database
        if (room.isNew()) {
            imageBadge.setVisibility(View.VISIBLE);
            imageBadge.setText("MỚI");
        } else if (room.isPromo()) {
            imageBadge.setVisibility(View.VISIBLE);
            imageBadge.setText("KHUYẾN MÃI");
        } else {
            imageBadge.setVisibility(View.GONE);
        }
    }

    private void setupAmenitiesFromList(List<String> amenities) {
        if (amenities == null) amenities = new ArrayList<>();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        amenitiesRecyclerView.setLayoutManager(layoutManager);
        AmenityAdapter adapter = new AmenityAdapter(amenities);
        amenitiesRecyclerView.setAdapter(adapter);
    }

    private void setupAmenities() {
        // Nếu currentRoom đã có amenities từ API thì d��ng, không thì fallback dữ liệu mẫu
        List<String> amenities = (currentRoom != null ? currentRoom.getAmenities() : null);
        if (amenities != null && !amenities.isEmpty()) {
            setupAmenitiesFromList(amenities);
            return;
        }

        List<String> fallback = Arrays.asList(
                "WC riêng", "Máy lạnh", "Wifi", "Tủ lạnh",
                "Máy giặt", "Bếp", "Chỗ để xe", "Camera an ninh"
        );
        setupAmenitiesFromList(fallback);
    }

    private void setupSuggestedRooms() {
        // TODO: Lấy danh sách phòng gợi ý từ database
        List<Room> suggestedRooms = getSuggestedRooms();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        suggestedRoomsRecyclerView.setLayoutManager(layoutManager);

        SuggestedRoomsAdapter adapter = new SuggestedRoomsAdapter(
                suggestedRooms,
                this::navigateToRoomDetail
        );
        suggestedRoomsRecyclerView.setAdapter(adapter);
    }

    private List<Room> getSuggestedRooms() {
        // Dữ liệu mẫu - TODO: Thay bằng dữ liệu từ database
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("Phòng A", "2 triệu", "Quận 1", R.drawable.tro));
        rooms.add(new Room("Phòng B", "2.5 triệu", "Quận 3", R.drawable.tro));
        rooms.add(new Room("Phòng C", "3 triệu", "Quận 5", R.drawable.tro));
        rooms.add(new Room("Phòng D", "3.5 triệu", "Quận 7", R.drawable.tro));
        return rooms;
    }

    private void navigateToRoomDetail(Room room) {
        Intent intent = new Intent(this, RoomDetailActivity.class);
        intent.putExtra("room", room);
        startActivity(intent);
    }

    private void setupClickListeners() {
        shareButtonHeader.setOnClickListener(v -> shareRoom());
        moreButtonHeader.setOnClickListener(v -> showMoreMenu());
        backButton.setOnClickListener(v -> onBackPressed());

        contactButton.setOnClickListener(v -> handleContactClick());
        saveButton.setOnClickListener(v -> handleSaveClick());
        bookButton.setOnClickListener(v -> handleBookingClick());

        viewMapButton.setOnClickListener(v -> showMap());
        getDirectionsButton.setOnClickListener(v -> getDirections());
        roomImage.setOnClickListener(v -> showImageFullScreen());

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            String query = searchInput.getText().toString();
            if (!query.isEmpty()) {
                searchRooms(query);
            }
            return false;
        });
    }

    private void shareRoom() {
        String shareText = detailTitle.getText().toString() + " - " + detailPrice.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(intent, "Chia sẻ phòng trọ"));
    }

    private void showMoreMenu() {
        PopupMenu popupMenu = new PopupMenu(this, moreButtonHeader);
        popupMenu.getMenuInflater().inflate(R.menu.menu_room_detail, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.menu_home) {
                goToHome();
                return true;
            } else if (itemId == R.id.menu_report) {
                reportRoom();
                return true;
            } else if (itemId == R.id.menu_help) {
                showHelp();
                return true;
            }
            return false;
        });

        popupMenu.show();
    }

    private void searchRooms(String query) {
        Toast.makeText(this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
    }

    private void goToHome() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void reportRoom() {
        new AlertDialog.Builder(this)
                .setTitle("Tố cáo trọ này")
                .setMessage("Vui lòng cho biết lý do tố cáo:")
                .setPositiveButton("Gửi", (dialog, which) -> {
                    Toast.makeText(this, "Đã gửi tố cáo", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showHelp() {
        new AlertDialog.Builder(this)
                .setTitle("Bạn cần giúp đỡ?")
                .setMessage("Liên hệ với chúng tôi để được hỗ trợ.")
                .setPositiveButton("Liên hệ", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:support@example.com"));
                    startActivity(intent);
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void showMap() {
        Toast.makeText(this, "Đang mở bản đồ...", Toast.LENGTH_SHORT).show();
    }

    private void getDirections() {
        Toast.makeText(this, "Đang mở chỉ đường...", Toast.LENGTH_SHORT).show();
    }

    private void showImageFullScreen() {
        Toast.makeText(this, "Nhấn giữ để phóng to ảnh", Toast.LENGTH_SHORT).show();
    }

    /**
     * Xử lý khi nhấn nút Liên hệ
     */
    private void handleContactClick() {
        if (!hasValidLoginForActions()) {
            showLoginDialog("liên hệ với chủ trọ");
            return;
        }

        // Đã đăng nhập → Cho phép liên hệ
        showContactOptions();
    }

    /**
     * Xử lý khi nhấn nút Lưu tin
     */
    private void handleSaveClick() {
        if (!sessionManager.isLoggedIn()) {
            showLoginDialog("lưu tin");
            return;
        }

        // Đã đăng nhập → Lưu vào danh sách yêu thích
        // TODO: Lưu vào database
        Toast.makeText(this, "Đã lưu tin vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
        saveButton.setText("Đã lưu");
        saveButton.setEnabled(false);
    }

    /**
     * Xử lý khi nhấn nút Đặt lịch xem phòng
     */
    private void handleBookingClick() {
        if (!hasValidLoginForActions()) {
            showLoginDialog("đặt lịch xem phòng");
            return;
        }

        if (currentRoom == null) {
            Toast.makeText(this, "Không tìm thấy thông tin phòng", Toast.LENGTH_SHORT).show();
            return;
        }

        String phongId = currentRoom.getPhongId();
        if (phongId == null || phongId.trim().isEmpty()) {
            Toast.makeText(this,
                    "Phòng này chưa có ID (PhongId).\n" +
                            "Bạn vui lòng mở phòng từ danh sách phòng thật (không phải phòng gợi ý mẫu) rồi thử lại.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // ⚠️ Backend /api/Phong/{id} hiện KHÔNG trả ChuTroId => không thể đặt lịch đúng chủ trọ.
        if (landlordUserId == null || landlordUserId.trim().isEmpty()) {
            Log.w(TAG, "Cannot book: missing ChuTroId from API room detail. Backend should include ChuTroId or provide endpoint to resolve via NhaTroId.");
            Toast.makeText(this,
                    "Không thể đặt lịch vì API chi tiết phòng chưa có ChuTroId (chủ trọ).\n" +
                            "Vui lòng cập nhật backend: /api/Phong/{id} trả thêm ChuTroId hoặc NhaTro kèm ChuTroId.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, BookingCreateActivity.class);
        intent.putExtra("roomId", phongId);
        intent.putExtra("room", currentRoom);
        intent.putExtra("chuTroId", landlordUserId.trim());
        startActivity(intent);
    }

    /**
     * Hiển thị dialog yêu cầu đăng nhập
     */
    private void showLoginDialog(String feature) {
        new AlertDialog.Builder(this)
                .setTitle("Yêu cầu đăng nhập")
                .setMessage("Bạn cần đăng nhập để " + feature)
                .setPositiveButton("Đăng nhập", (dialog, which) -> {
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.putExtra("targetRole", "tenant");
                    startActivity(intent);
                })
                .setNegativeButton("Đăng ký", (dialog, which) -> {
                    Intent intent = new Intent(this, DangKyNguoiThueActivity.class);
                    startActivity(intent);
                })
                .setNeutralButton("Hủy", null)
                .show();
    }

    /**
     * Fetch chi tiết phòng từ backend để hiển thị đúng chủ trọ + tiện nghi
     */
    private void fetchRoomDetail(String roomId) {
        try {
            ApiService api = ApiClient.getRetrofit().create(ApiService.class);
            api.getRoomDetail(roomId).enqueue(new Callback<GenericResponse<Object>>() {
                @Override
                public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        Log.e(TAG, "getRoomDetail failed: http=" + response.code());
                        return;
                    }

                    Object data = response.body().data;
                    if (data == null) {
                        Log.w(TAG, "getRoomDetail: data is null");
                        return;
                    }

                    Room mapped = mapRoomFromApiData(data);
                    if (mapped != null) {
                        mapped.setPhongId(roomId);
                        currentRoom = mapped;
                    }

                    landlordUserId = extractLandlordUserId(data);
                    String landlordDisplayName = extractLandlordName(data);

                    // ✅ Nếu room detail không có ChuTroId/TenChuTro thì resolve qua NhaTroId
                    if ((landlordUserId == null || landlordUserId.trim().isEmpty())
                            || (landlordDisplayName == null || landlordDisplayName.trim().isEmpty())) {
                        String nhaTroId = extractNhaTroId(data);
                        if (nhaTroId != null && !nhaTroId.trim().isEmpty()) {
                            resolveLandlordFromNhaTroId(nhaTroId);
                        } else {
                            Log.w(TAG, "Room detail API did not contain ChuTroId and also missing NhaTroId. Cannot resolve landlord.");
                        }
                    }

                    if (currentRoom != null) {
                        if (landlordDisplayName != null && !landlordDisplayName.trim().isEmpty()) {
                            currentRoom.setLandlordName(landlordDisplayName.trim());
                        }
                    }

                    runOnUiThread(() -> setupData(currentRoom));
                }

                @Override
                public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                    Log.e(TAG, "getRoomDetail onFailure: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "fetchRoomDetail error: " + e.getMessage(), e);
        }
    }

    private String extractNhaTroId(Object data) {
        try {
            if (data instanceof LinkedTreeMap) {
                LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) data;
                Object v = map.get("NhaTroId");
                if (v == null) v = map.get("nhaTroId");
                if (v != null) return String.valueOf(v);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private void resolveLandlordFromNhaTroId(String nhaTroId) {
        try {
            ApiService api = ApiClient.getRetrofit().create(ApiService.class);
            api.getNhaTroDetail(nhaTroId).enqueue(new Callback<Object>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        String err = "";
                        try {
                            if (response.errorBody() != null) err = response.errorBody().string();
                        } catch (Exception ignored) {
                        }
                        Log.w(TAG, "getNhaTroDetail failed http=" + response.code() + " err=" + err);
                        return;
                    }

                    Object raw = response.body();

                    String chuTroId = null;
                    String displayName = null;

                    try {
                        if (raw instanceof LinkedTreeMap) {
                            LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) raw;

                            Object v = map.get("ChuTroId");
                            if (v == null) v = map.get("chuTroId");
                            if (v != null) chuTroId = String.valueOf(v);

                            // API mẫu: không có TenChuTro, nên fallback sang TieuDe (tên nhà trọ)
                            Object ten = map.get("TenChuTro");
                            if (ten == null) ten = map.get("tenChuTro");
                            if (ten != null) displayName = String.valueOf(ten);

                            if (displayName == null || displayName.trim().isEmpty()) {
                                Object tieuDe = map.get("TieuDe");
                                if (tieuDe == null) tieuDe = map.get("tieuDe");
                                if (tieuDe != null) displayName = String.valueOf(tieuDe);
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    if (chuTroId != null && !chuTroId.trim().isEmpty()) {
                        landlordUserId = chuTroId.trim();
                    }
                    if (currentRoom != null && displayName != null && !displayName.trim().isEmpty()) {
                        currentRoom.setLandlordName(displayName.trim());
                    }

                    Log.d(TAG, "Resolved landlord from NhaTroId=" + nhaTroId + " => ChuTroId=" + landlordUserId + ", DisplayName=" + displayName);
                    runOnUiThread(() -> setupData(currentRoom));
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    Log.e(TAG, "getNhaTroDetail onFailure: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "resolveLandlordFromNhaTroId error: " + e.getMessage(), e);
        }
    }

    /**
     * Map dữ liệu trả về từ GET /api/phong/{id} sang Room để hiển thị trên UI.
     * Backend có thể trả PascalCase hoặc camelCase.
     */
    private Room mapRoomFromApiData(Object data) {
        try {
            if (data instanceof LinkedTreeMap) {
                LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) data;
                Room room = new Room();

                Object tieuDe = map.get("tieuDe");
                if (tieuDe == null) tieuDe = map.get("TieuDe");
                if (tieuDe != null) room.setTitle(String.valueOf(tieuDe));

                Object giaTien = map.get("giaTien");
                if (giaTien == null) giaTien = map.get("GiaTien");
                if (giaTien instanceof Number) room.setPriceValue(((Number) giaTien).doubleValue());

                Object dienTich = map.get("dienTich");
                if (dienTich == null) dienTich = map.get("DienTich");
                if (dienTich instanceof Number) room.setArea(((Number) dienTich).doubleValue());

                Object moTa = map.get("moTa");
                if (moTa == null) moTa = map.get("MoTa");
                if (moTa != null) room.setDescription(String.valueOf(moTa));

                Object diaChi = map.get("diaChiNhaTro");
                if (diaChi == null) diaChi = map.get("DiaChiNhaTro");
                if (diaChi != null) room.setAddress(String.valueOf(diaChi));

                Object quan = map.get("tenQuanHuyen");
                if (quan == null) quan = map.get("TenQuanHuyen");
                Object phuong = map.get("tenPhuong");
                if (phuong == null) phuong = map.get("TenPhuong");
                String loc = null;
                if (quan != null) loc = String.valueOf(quan);
                if (phuong != null) loc = (loc == null ? "" : loc + ", ") + String.valueOf(phuong);
                if (loc != null && !loc.trim().isEmpty()) room.setLocation(loc);

                // tiện nghi
                Object tienIch = map.get("tienIch");
                if (tienIch == null) tienIch = map.get("TienIch");
                if (tienIch instanceof List) {
                    ArrayList<String> amenities = new ArrayList<>();
                    for (Object o : (List<?>) tienIch) {
                        if (o != null) amenities.add(String.valueOf(o));
                    }
                    room.setAmenities(amenities);
                }

                // ảnh: danhSachAnhUrl (có thể là URL hoặc TapTinId)
                Object danhSachAnhUrl = map.get("danhSachAnhUrl");
                if (danhSachAnhUrl == null) danhSachAnhUrl = map.get("DanhSachAnhUrl");
                if (danhSachAnhUrl instanceof List) {
                    List<?> lst = (List<?>) danhSachAnhUrl;
                    if (!lst.isEmpty() && lst.get(0) != null) {
                        room.setImageUrl(String.valueOf(lst.get(0)));
                    }
                }

                // landlord name nếu backend có
                Object tenChuTro = map.get("tenChuTro");
                if (tenChuTro == null) tenChuTro = map.get("TenChuTro");
                if (tenChuTro != null) room.setLandlordName(String.valueOf(tenChuTro));

                return room;
            }
        } catch (Exception e) {
            Log.e(TAG, "mapRoomFromApiData error: " + e.getMessage(), e);
        }
        return null;
    }

    private String extractLandlordName(Object data) {
        try {
            if (data instanceof LinkedTreeMap) {
                LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) data;
                Object v = map.get("tenChuTro");
                if (v == null) v = map.get("TenChuTro");
                if (v == null) v = map.get("chuTroTen");
                if (v == null) v = map.get("ChuTroTen");
                if (v == null) v = map.get("UserName");
                if (v == null) v = map.get("userName");
                if (v != null) return String.valueOf(v);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private String extractLandlordUserId(Object data) {
        try {
            if (data instanceof LinkedTreeMap) {
                LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) data;
                // thử nhiều key khác nhau tuỳ backend
                Object v = map.get("chuTroId");
                if (v == null) v = map.get("ChuTroId");
                if (v == null) v = map.get("landlordId");
                if (v == null) v = map.get("LandlordId");
                // fallback: room detail may include nested nhaTro
                if (v == null) {
                    Object nhaTro = map.get("nhaTro");
                    if (nhaTro == null) nhaTro = map.get("NhaTro");
                    if (nhaTro instanceof LinkedTreeMap) {
                        Object vv = ((LinkedTreeMap<?, ?>) nhaTro).get("chuTroId");
                        if (vv == null) vv = ((LinkedTreeMap<?, ?>) nhaTro).get("ChuTroId");
                        v = vv;
                    }
                }
                if (v != null) return String.valueOf(v);
            }
        } catch (Exception ignore) {
        }
        return null;
    }

    private boolean hasValidLoginForActions() {
        String token = sessionManager.getToken();
        String userId = sessionManager.getUserId();
        return token != null && !token.trim().isEmpty() && userId != null && userId.matches("(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    private void openChatWithLandlord() {
        if (!hasValidLoginForActions()) {
            showLoginDialog("nhắn tin với chủ trọ");
            return;
        }

        if (landlordUserId == null || landlordUserId.trim().isEmpty()) {
            Log.w(TAG, "Cannot chat: missing ChuTroId from API room detail.");
            Toast.makeText(this,
                    "Không thể nhắn tin vì API chi tiết phòng chưa có ChuTroId (chủ trọ).",
                    Toast.LENGTH_LONG).show();
            return;
        }

        String name = (currentRoom != null && currentRoom.getLandlordName() != null && !currentRoom.getLandlordName().isEmpty())
                ? currentRoom.getLandlordName()
                : "Chủ trọ";

        Intent intent = new Intent(this, ChatActivity.class);
        // ChatActivity đọc các key này
        intent.putExtra("user_id", sessionManager.getUserId());
        intent.putExtra("user_name", sessionManager.getUserName());
        intent.putExtra("other_user_id", landlordUserId);
        intent.putExtra("other_user_name", name);
        // context thêm (không bắt buộc)
        intent.putExtra("room_id", phongId);
        startActivity(intent);
    }

    /**
     * Hiển thị các tùy chọn liên hệ
     * YÊU CẦU: chỉ cho nhắn tin/chat, bỏ gọi điện.
     */
    private void showContactOptions() {
        // Chỉ mở chat trong app
        openChatWithLandlord();
    }
}
