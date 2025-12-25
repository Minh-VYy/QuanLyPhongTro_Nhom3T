package com.example.QuanLyPhongTro_App.ui.landlord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

import java.sql.Connection;
import java.sql.DriverManager;

public class LandlordStatsActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private StatsDao statsDao;
    private TextView tvTotalRooms, tvOccupiedRooms, tvVacantRooms;
    private TextView tvMonthlyRevenue, tvTotalBookings, tvPendingRequests;
    private TextView tvTotalRevenue, tvOccupancyRate, tvApprovalRate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_stats);

        sessionManager = new SessionManager(this);
        statsDao = new StatsDao();

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
        
        if (landlordId == null) {
            Log.e("LandlordStats", "LandlordId is null!");
            loadMockStatistics();
            return;
        }
        
        Log.d("LandlordStats", "Loading statistics for landlord: " + landlordId);
        new LoadStatsTask().execute(landlordId);
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
        
        Toast.makeText(this, "Hiển thị dữ liệu mẫu", Toast.LENGTH_SHORT).show();
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "stats");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "stats");
    }
    
    /**
     * AsyncTask để tải dữ liệu thống kê từ database
     */
    private class LoadStatsTask extends AsyncTask<String, Void, StatsDao.LandlordStats> {
        private String errorMsg = null;

        @Override
        protected StatsDao.LandlordStats doInBackground(String... params) {
            String landlordId = params[0];
            final StatsDao.LandlordStats[] result = new StatsDao.LandlordStats[]{null};
            final boolean[] completed = {false};
            final String[] error = {null};
            
            Thread dbThread = new Thread(() -> {
                Connection connection = null;
                try {
                    Log.d("LandlordStats", "🔄 Connecting to database for statistics...");
                    String url = "jdbc:jtds:sqlserver://172.26.98.234:1433/QuanLyPhongTro";
                    String username = "sa";
                    String password = "27012005";
                    
                    connection = DriverManager.getConnection(url, username, password);
                    Log.d("LandlordStats", "✅ Database connection successful");
                    
                    result[0] = statsDao.getLandlordStats(connection, landlordId);
                    Log.d("LandlordStats", "📊 Statistics loaded successfully");
                    
                } catch (Exception e) {
                    error[0] = e.getMessage();
                    Log.e("LandlordStats", "❌ Database connection failed: " + e.getMessage(), e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                            Log.d("LandlordStats", "🔒 Database connection closed");
                        } catch (Exception e) {
                            Log.e("LandlordStats", "Error closing connection", e);
                        }
                    }
                    completed[0] = true;
                }
            });
            
            dbThread.start();
            
            long startTime = System.currentTimeMillis();
            while (!completed[0] && (System.currentTimeMillis() - startTime) < 15000) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            if (!completed[0]) {
                Log.e("LandlordStats", "⏰ Database query timeout after 15 seconds");
                error[0] = "Database query timeout";
            }
            
            errorMsg = error[0];
            return result[0];
        }

        @Override
        protected void onPostExecute(StatsDao.LandlordStats stats) {
            Log.d("LandlordStats", "=== LoadStatsTask onPostExecute ===");
            
            if (stats != null) {
                Log.d("LandlordStats", "✅ Using REAL statistics from database");
                displayRealStatistics(stats);
                
                Toast.makeText(LandlordStatsActivity.this, 
                    "✅ Đã tải thống kê từ database", 
                    Toast.LENGTH_SHORT).show();
                    
            } else if (errorMsg != null) {
                Log.e("LandlordStats", "❌ Database error, showing mock data: " + errorMsg);
                loadMockStatistics();
                
                Toast.makeText(LandlordStatsActivity.this, 
                    "⚠️ Lỗi database, hiển thị dữ liệu mẫu: " + errorMsg, 
                    Toast.LENGTH_LONG).show();
                    
            } else {
                Log.d("LandlordStats", "ℹ️ No statistics found, showing mock data");
                loadMockStatistics();
                
                Toast.makeText(LandlordStatsActivity.this, 
                    "ℹ️ Chưa có dữ liệu thống kê, hiển thị dữ liệu mẫu", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Hiển thị thống kê thật từ database
     */
    private void displayRealStatistics(StatsDao.LandlordStats stats) {
        Log.d("LandlordStats", "📊 Displaying real statistics");
        
        // Thống kê phòng
        tvTotalRooms.setText(String.valueOf(stats.getTotalRooms()));
        tvOccupiedRooms.setText(String.valueOf(stats.getOccupiedRooms()));
        tvVacantRooms.setText(String.valueOf(stats.getVacantRooms()));
        
        // Thống kê booking
        tvTotalBookings.setText(String.valueOf(stats.getTotalBookings()));
        tvPendingRequests.setText(String.valueOf(stats.getPendingRequests()));
        
        // Thống kê doanh thu
        tvMonthlyRevenue.setText(stats.getFormattedMonthlyRevenue());
        
        // Thống kê bổ sung (nếu có TextView)
        if (tvTotalRevenue != null) {
            tvTotalRevenue.setText(stats.getFormattedTotalRevenue());
        }
        
        if (tvOccupancyRate != null) {
            tvOccupancyRate.setText(String.format("%.1f%%", stats.getOccupancyRate()));
        }
        
        if (tvApprovalRate != null) {
            tvApprovalRate.setText(String.format("%.1f%%", stats.getApprovalRate()));
        }
        
        Log.d("LandlordStats", "✅ Real statistics displayed successfully");
        Log.d("LandlordStats", "📈 Summary - Rooms: " + stats.getTotalRooms() + 
              ", Bookings: " + stats.getTotalBookings() + 
              ", Revenue: " + stats.getFormattedMonthlyRevenue());
    }
}
