package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.ArrayList;
import java.util.List;

public class SavedRoomsActivity extends AppCompatActivity {

    private RecyclerView savedRoomsRecyclerView;
    private LinearLayout emptyState;
    private Spinner sortSpinner;
    private Button btnExploreRooms;
    private SavedRoomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_saved_rooms);

        initViews();
        setupToolbar();
        setupSpinner();
        loadSavedRooms();
    }

    private void initViews() {
        savedRoomsRecyclerView = findViewById(R.id.savedRoomsRecyclerView);
        emptyState = findViewById(R.id.emptyState);
        sortSpinner = findViewById(R.id.sortSpinner);
        btnExploreRooms = findViewById(R.id.btnExploreRooms);

        savedRoomsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupSpinner() {
        String[] sortOptions = {"Mới nhất", "Giá tăng dần", "Giá giảm dần"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, sortOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
    }

    private void loadSavedRooms() {
        // TODO: Load from database
        List<Room> savedRooms = getSampleSavedRooms();

        if (savedRooms.isEmpty()) {
            savedRoomsRecyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);

            btnExploreRooms.setOnClickListener(v -> {
                finish(); // Go back to home
            });
        } else {
            savedRoomsRecyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            adapter = new SavedRoomAdapter(this, savedRooms);
            savedRoomsRecyclerView.setAdapter(adapter);
        }
    }

    private List<Room> getSampleSavedRooms() {
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room("Phòng trọ cao cấp gần trường", "2.5 triệu/tháng", "Quận 1, TP.HCM", "4.5", true));
        rooms.add(new Room("Phòng giá rẻ tiện nghi", "1.8 triệu/tháng", "Quận 3, TP.HCM", "4.2", true));
        return rooms;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

