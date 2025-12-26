package com.example.QuanLyPhongTro_App.ui.landlord;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.example.QuanLyPhongTro_App.data.repository.LandlordRoomRepository;
import com.example.QuanLyPhongTro_App.data.repository.LandlordBookingRepository;
import com.example.QuanLyPhongTro_App.data.repository.RoomRepository;
import com.example.QuanLyPhongTro_App.utils.ApiClient;

import java.util.List;
import java.util.Map;

public class LandlordStatsActivity extends AppCompatActivity {

    private static final String TAG = "LandlordStatsActivity";

    private SessionManager sessionManager;
    private TextView tvTotalRooms, tvOccupiedRooms, tvVacantRooms;
    private TextView tvMonthlyRevenue, tvTotalBookings, tvPendingRequests;
    private TextView tvTotalRevenue, tvOccupancyRate, tvApprovalRate;

    private LandlordRoomRepository landlordRoomRepository;
    private LandlordBookingRepository landlordBookingRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_stats);

        sessionManager = new SessionManager(this);
        landlordRoomRepository = new LandlordRoomRepository();
        landlordBookingRepository = new LandlordBookingRepository();

        // Ensure token for authenticated endpoints
        ApiClient.setToken(sessionManager.getToken());

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
        
        // Các TextView bổ sung sẽ được null nếu không có trong layout
        tvTotalRevenue = null;
        tvOccupancyRate = null;
        tvApprovalRate = null;
    }

    private void loadStatistics() {
        String landlordId = sessionManager.getUserId();

        if (landlordId == null || landlordId.isEmpty()) {
            loadMockStatistics();
            return;
        }

        loadStatsFromApi(landlordId);
    }

    private void loadStatsFromApi(String landlordId) {
        final int[] totalRooms = {0};
        final int[] occupiedRooms = {0};
        final int[] vacantRooms = {0};

        final int[] totalBookings = {0};
        final int[] pendingRequests = {0};

        final boolean[] roomsDone = {false};
        final boolean[] bookingsDone = {false};

        landlordRoomRepository.getMyRooms(landlordId, new LandlordRoomRepository.ListRoomsCallback() {
            @Override
            public void onSuccess(List<RoomRepository.RoomDto> rooms) {
                totalRooms[0] = rooms != null ? rooms.size() : 0;
                occupiedRooms[0] = 0;
                vacantRooms[0] = 0;

                if (rooms != null) {
                    for (RoomRepository.RoomDto r : rooms) {
                        String status = r.getTrangThai();
                        if (status == null) status = "";
                        String s = status.toLowerCase();
                        if (s.contains("thuê") || s.contains("da_thue") || s.contains("đã thuê")) {
                            occupiedRooms[0]++;
                        } else if (s.contains("trống") || s.contains("con_trong") || s.contains("còn trống")) {
                            vacantRooms[0]++;
                        }
                    }
                }

                roomsDone[0] = true;
                maybeRender(totalRooms[0], occupiedRooms[0], vacantRooms[0], totalBookings[0], pendingRequests[0], roomsDone, bookingsDone);
            }

            @Override
            public void onError(String message) {
                Log.w(TAG, "Failed to load rooms for stats: " + message);
                roomsDone[0] = true;
                maybeRender(totalRooms[0], occupiedRooms[0], vacantRooms[0], totalBookings[0], pendingRequests[0], roomsDone, bookingsDone);
            }
        });

        landlordBookingRepository.getBookingRequests(landlordId, new LandlordBookingRepository.ListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> rawItems) {
                totalBookings[0] = rawItems != null ? rawItems.size() : 0;
                pendingRequests[0] = 0;

                if (rawItems != null) {
                    for (Map<String, Object> item : rawItems) {
                        Object trangThaiId = item.get("trangThaiId");
                        if (trangThaiId == null) trangThaiId = item.get("TrangThaiId");
                        int id = -1;
                        try {
                            if (trangThaiId instanceof Number) id = ((Number) trangThaiId).intValue();
                            else if (trangThaiId != null) id = Integer.parseInt(trangThaiId.toString().replace(".0", ""));
                        } catch (Exception ignore) {}

                        if (id == 1) pendingRequests[0]++;
                    }
                }

                bookingsDone[0] = true;
                maybeRender(totalRooms[0], occupiedRooms[0], vacantRooms[0], totalBookings[0], pendingRequests[0], roomsDone, bookingsDone);
            }

            @Override
            public void onError(String message) {
                Log.w(TAG, "Failed to load booking requests for stats: " + message);
                bookingsDone[0] = true;
                maybeRender(totalRooms[0], occupiedRooms[0], vacantRooms[0], totalBookings[0], pendingRequests[0], roomsDone, bookingsDone);
            }
        });
    }

    private void maybeRender(int totalRooms, int occupiedRooms, int vacantRooms,
                             int totalBookings, int pendingRequests,
                             boolean[] roomsDone, boolean[] bookingsDone) {
        if (!roomsDone[0] || !bookingsDone[0]) return;

        runOnUiThread(() -> {
            tvTotalRooms.setText(String.valueOf(totalRooms));
            tvOccupiedRooms.setText(String.valueOf(occupiedRooms));
            tvVacantRooms.setText(String.valueOf(vacantRooms));
            tvTotalBookings.setText(String.valueOf(totalBookings));
            tvPendingRequests.setText(String.valueOf(pendingRequests));

            // Revenue isn't available via API in current app/backend. Show 0.
            tvMonthlyRevenue.setText("0 đ");
            if (tvTotalRevenue != null) tvTotalRevenue.setText("0 đ");

            if (tvOccupancyRate != null) {
                double rate = totalRooms == 0 ? 0.0 : (occupiedRooms * 100.0 / totalRooms);
                tvOccupancyRate.setText(String.format("%.1f%%", rate));
            }

            if (tvApprovalRate != null) {
                double rate = totalBookings == 0 ? 0.0 : ((totalBookings - pendingRequests) * 100.0 / totalBookings);
                tvApprovalRate.setText(String.format("%.1f%%", rate));
            }
        });
    }

    private void loadMockStatistics() {
        Log.d("LandlordStats", "Loading mock statistics");
        tvTotalRooms.setText("12");
        tvOccupiedRooms.setText("8");
        tvVacantRooms.setText("4");
        tvMonthlyRevenue.setText("24.000.000 đ");
        tvTotalBookings.setText("23");
        tvPendingRequests.setText("5");
        
        if (tvTotalRevenue != null) tvTotalRevenue.setText("120.000.000 đ");
        if (tvOccupancyRate != null) tvOccupancyRate.setText("66.7%");
        if (tvApprovalRate != null) tvApprovalRate.setText("78.3%");
        
        // Mock data loaded silently
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
