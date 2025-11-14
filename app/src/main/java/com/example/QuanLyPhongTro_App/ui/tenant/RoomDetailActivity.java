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

public class RoomDetailActivity extends AppCompatActivity {

    private EditText searchInput;
    private ImageView roomImage, shareButtonHeader, moreButtonHeader, backButton;
    private TextView detailTitle, detailPrice, detailLocation, detailArea, detailDescription, detailAddress, landlordName;
    private Button contactButton, saveButton, bookButton, viewMapButton, getDirectionsButton;
    private RecyclerView amenitiesRecyclerView, suggestedRoomsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_room_detail);

        Room room = (Room) getIntent().getSerializableExtra("room");

        initViews();
        setupData(room);
        setupClickListeners();
    }

    private void initViews() {
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
        landlordName = findViewById(R.id.landlordName);
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
            roomImage.setImageResource(room.getImageResId());
            detailTitle.setText(room.getTitle());
            detailPrice.setText(room.getPrice());
            detailLocation.setText(room.getLocation());
            detailArea.setText("20m²");
            detailDescription.setText("Phòng trọ mới xây, sạch sẽ, thoáng mát. Có cửa sổ lớn, ánh sáng tự nhiên. Khu vực an ninh, gần chợ, siêu thị và các tiện ích khác. Phù hợp cho sinh viên và người đi làm.");
            detailAddress.setText("123 Đường ABC, " + room.getLocation());
            landlordName.setText("Nguyễn Văn A");

            setupAmenities();
            setupSuggestedRooms();
            setupImageBadge(room);
        }
    }

    private void setupImageBadge(Room room) {
        TextView imageBadge = findViewById(R.id.imageBadge);
        if (room.getTitle().toLowerCase().contains("mới") || room.getTitle().toLowerCase().contains("new")) {
            imageBadge.setVisibility(View.VISIBLE);
            imageBadge.setText("MỚI");
        }
    }

    private void setupAmenities() {
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

        // Thêm: Search listener
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
        PopupMenu popupMenu = new PopupMenu(this, moreButtonHeader);  // Thay đổi
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
        // TODO: Implement search functionality
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
