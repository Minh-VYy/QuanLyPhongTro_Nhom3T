package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SavedRoomsActivity extends AppCompatActivity {

    private Spinner sortSpinner;
    private RecyclerView savedRoomsRecyclerView;
    private LinearLayout emptyState;
    private Button btnExploreRooms;

    private SavedRoomAdapter adapter;
    private List<Room> savedRoomsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_saved_rooms);

        initViews();
        setupToolbar();
        setupSortSpinner();
        loadSavedRooms();
        setupRecyclerView();
        updateEmptyState();
        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void initViews() {
        LinearLayout toolbarLayout = findViewById(R.id.toolbar);
        ImageView btnBack = toolbarLayout.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
        sortSpinner = findViewById(R.id.sortSpinner);
        savedRoomsRecyclerView = findViewById(R.id.savedRoomsRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        btnExploreRooms = findViewById(R.id.btnExploreRooms);
    }

    private void setupToolbar() {
        // Toolbar handling done in initViews()
    }


    private void setupSortSpinner() {
        // Các tùy chọn sắp xếp
        String[] sortOptions = {
            "Mới nhất",
            "Giá thấp đến cao",
            "Giá cao đến thấp",
            "Đánh giá cao nhất"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
            this,
            android.R.layout.simple_spinner_item,
            sortOptions
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortRooms(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void loadSavedRooms() {
        // Load danh sách phòng đã lưu (sample data)
        savedRoomsList = getSampleSavedRooms();
    }

    private void setupRecyclerView() {
        savedRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SavedRoomAdapter(this, savedRoomsList);
        savedRoomsRecyclerView.setAdapter(adapter);

        // Setup button explore rooms
        btnExploreRooms.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void sortRooms(int sortType) {
        if (savedRoomsList == null || savedRoomsList.isEmpty()) {
            return;
        }

        switch (sortType) {
            case 0: // Mới nhất (không sắp xếp, giữ nguyên thứ tự)
                loadSavedRooms();
                break;
            case 1: // Giá thấp đến cao
                Collections.sort(savedRoomsList, new Comparator<Room>() {
                    @Override
                    public int compare(Room r1, Room r2) {
                        return extractPrice(r1.getPrice()) - extractPrice(r2.getPrice());
                    }
                });
                break;
            case 2: // Giá cao đến thấp
                Collections.sort(savedRoomsList, new Comparator<Room>() {
                    @Override
                    public int compare(Room r1, Room r2) {
                        return extractPrice(r2.getPrice()) - extractPrice(r1.getPrice());
                    }
                });
                break;
            case 3: // Đánh giá cao nhất
                Collections.sort(savedRoomsList, new Comparator<Room>() {
                    @Override
                    public int compare(Room r1, Room r2) {
                        return Float.compare(extractRating(r2.getRating()), extractRating(r1.getRating()));
                    }
                });
                break;
        }

        adapter.notifyDataSetChanged();
    }

    private int extractPrice(String priceStr) {
        try {
            // Extract number from string like "2.5 triệu/tháng"
            String numberStr = priceStr.replaceAll("[^0-9.]", "");
            return (int) (Float.parseFloat(numberStr) * 1000000); // Convert to VND
        } catch (Exception e) {
            return 0;
        }
    }

    private float extractRating(String ratingStr) {
        try {
            if (ratingStr == null) return 0;
            // Extract number from string like "4.5"
            String numberStr = ratingStr.replaceAll("[^0-9.]", "");
            return Float.parseFloat(numberStr);
        } catch (Exception e) {
            return 0;
        }
    }

    private void updateEmptyState() {
        if (savedRoomsList == null || savedRoomsList.isEmpty()) {
            savedRoomsRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            savedRoomsRecyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);
        }
    }

    /**
     * Sample data for saved rooms
     * TODO: Replace with real data from database
     */
    private List<Room> getSampleSavedRooms() {
        List<Room> rooms = new ArrayList<>();

        rooms.add(new Room(
            "Phòng trọ cao cấp Quận 1",
            "4.5 triệu/tháng",
            "123 Nguyễn Huệ, Quận 1, TP.HCM",
            "4.8",
            true
        ));

        rooms.add(new Room(
            "Phòng trọ giá rẻ Bình Thạnh",
            "2.5 triệu/tháng",
            "456 Xô Viết Nghệ Tĩnh, Bình Thạnh, TP.HCM",
            "4.5",
            true
        ));

        rooms.add(new Room(
            "Căn hộ mini Quận 7",
            "5.0 triệu/tháng",
            "789 Nguyễn Văn Linh, Quận 7, TP.HCM",
            "4.9",
            true
        ));

        rooms.add(new Room(
            "Phòng trọ sinh viên Thủ Đức",
            "2.0 triệu/tháng",
            "321 Võ Văn Ngân, Thủ Đức, TP.HCM",
            "4.3",
            true
        ));

        rooms.add(new Room(
            "Phòng đầy đủ tiện nghi Phú Nhuận",
            "3.5 triệu/tháng",
            "567 Phan Đăng Lưu, Phú Nhuận, TP.HCM",
            "4.6",
            true
        ));

        // Set image resource for all rooms
        for (Room room : rooms) {
            room.setImageResId(R.drawable.room_image_1);
        }

        return rooms;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this activity
        if (adapter != null) {
            adapter.notifyDataSetChanged();
            updateEmptyState();
        }
    }
}

