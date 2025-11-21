package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity hiển thị chi tiết phòng trọ
 * ĐÃ CẬP NHẬT: Thêm hiển thị đánh giá phòng và đánh giá chủ trọ riêng biệt
 */
public class RoomDetailActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_room_detail);

        // Lấy đối tượng Room từ Intent
        Room room = (Room) getIntent().getSerializableExtra("room");

        initViews();
        setupData(room);
        setupClickListeners();
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

    private void setupAmenities() {
        // TODO: Lấy danh sách tiện nghi từ database
        List<String> amenities = Arrays.asList(
                "WC riêng", "Máy lạnh", "Wifi", "Tủ lạnh",
                "Máy giặt", "Bếp", "Chỗ để xe", "Camera an ninh"
        );

        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        amenitiesRecyclerView.setLayoutManager(layoutManager);

        AmenityAdapter adapter = new AmenityAdapter(amenities);
        amenitiesRecyclerView.setAdapter(adapter);
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

        contactButton.setOnClickListener(v -> showLoginPrompt("liên hệ với chủ trọ"));
        saveButton.setOnClickListener(v -> showLoginPrompt("lưu tin"));
        bookButton.setOnClickListener(v -> showLoginPrompt("đặt lịch xem phòng"));

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

    private void showLoginPrompt(String feature) {
        Toast.makeText(this, "Vui lòng đăng nhập để " + feature, Toast.LENGTH_SHORT).show();
    }
}
