package com.example.QuanLyPhongTro_App.ui.landlord;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

public class LandlordStatsActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private TextView tvTotalRooms, tvOccupiedRooms, tvVacantRooms;
    private TextView tvMonthlyRevenue, tvTotalBookings, tvPendingRequests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_stats);

        sessionManager = new SessionManager(this);

        initViews();
        loadStatistics();
        setupBottomNavigation();
    }

    private void initViews() {
        tvTotalRooms = findViewById(R.id.tv_total_rooms);
        tvOccupiedRooms = findViewById(R.id.tv_occupied_rooms);
        tvVacantRooms = findViewById(R.id.tv_vacant_rooms);
        tvMonthlyRevenue = findViewById(R.id.tv_monthly_revenue);
        tvTotalBookings = findViewById(R.id.tv_total_bookings);
        tvPendingRequests = findViewById(R.id.tv_pending_requests);
    }

    private void loadStatistics() {
        // Load mock statistics
        tvTotalRooms.setText("12");
        tvOccupiedRooms.setText("8");
        tvVacantRooms.setText("4");
        tvMonthlyRevenue.setText("24.000.000 đ");
        tvTotalBookings.setText("23");
        tvPendingRequests.setText("5");
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "stats");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "stats");
    }
}
