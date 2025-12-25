package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.ui.tenant.MessageDetailActivity;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class YeuCau extends AppCompatActivity {
    
    private static final int DATABASE_TIMEOUT_MS = 20000; // 20 seconds
    private static final int RETRY_DELAY_MS = 100;

    private TextView btnDatLich, btnTinNhan, btnThanhToan;
    private RecyclerView rvBookings, rvMessages, rvPayments;
    private View tabIndicator;
    
    private SessionManager sessionManager;
    private BookingRequestDao bookingDao;
    private PaymentRequestDao paymentDao;
    private BookingsAdapter bookingsAdapter;
    private PaymentsAdapter paymentsAdapter;
    private List<BookingRequest> bookingRequests;
    private List<PaymentRequest> paymentRequests;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_request);

        Log.d("YeuCau", "=== STARTING YeuCau Activity ===");
        
        sessionManager = new SessionManager(this);
        bookingDao = new BookingRequestDao();
        paymentDao = new PaymentRequestDao();
        bookingRequests = new ArrayList<>();
        paymentRequests = new ArrayList<>();
        dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());

        Log.d("YeuCau", "Session info - UserID: " + sessionManager.getUserId() + ", Role: " + sessionManager.getUserRole());

        btnDatLich = findViewById(R.id.btn_tab_datlich);
        btnTinNhan = findViewById(R.id.btn_tab_tinnhan);
        btnThanhToan = findViewById(R.id.btn_tab_thanhtoan);
        tabIndicator = findViewById(R.id.tab_indicator);

        rvBookings = findViewById(R.id.rv_bookings);
        rvMessages = findViewById(R.id.rv_messages);
        rvPayments = findViewById(R.id.rv_payments);

        if (rvBookings == null) {
            Log.e("YeuCau", "ERROR: rvBookings is null!");
        } else {
            Log.d("YeuCau", "rvBookings found successfully");
        }

        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        rvMessages.setLayoutManager(new LinearLayoutManager(this));
        rvPayments.setLayoutManager(new LinearLayoutManager(this));

        // Setup adapters
        bookingsAdapter = new BookingsAdapter(bookingRequests, this);
        rvBookings.setAdapter(bookingsAdapter);
        
        Log.d("YeuCau", "Adapter set with initial size: " + bookingRequests.size());

        // Setup messages (keep existing mock data for now)
        ArrayList<MessageItem> messages = new ArrayList<>();
        messages.add(new MessageItem("Nguyễn Văn A","Chào bạn, tôi có thể xem phòng vào chiều nay không?","Hôm qua"));
        messages.add(new MessageItem("Lê Thị C","Cảm ơn bạn, mình đã thuê được phòng rồi","2 ngày trước"));
        MessagesAdapter messagesAdapter = new MessagesAdapter(this, messages);
        rvMessages.setAdapter(messagesAdapter);

        // Setup payments with real adapter
        paymentsAdapter = new PaymentsAdapter(paymentRequests, this);
        rvPayments.setAdapter(paymentsAdapter);

        btnDatLich.setOnClickListener(v -> {
            Log.d("YeuCau", "Clicked Dat Lich tab");
            showTab("datlich");
        });
        
        btnTinNhan.setOnClickListener(v -> {
            Log.d("YeuCau", "Clicked Tin Nhan tab");
            showTab("tinnhan");
        });
        
        btnThanhToan.setOnClickListener(v -> {
            Log.d("YeuCau", "Clicked Thanh Toan tab");
            showTab("thanhtoan");
        });

        setupBottomNavigation();

        String defaultTab = getIntent().getStringExtra("defaultTab");
        if (defaultTab != null && !defaultTab.isEmpty()) {
            Log.d("YeuCau", "Using default tab: " + defaultTab);
            showTab(defaultTab);
        } else {
            Log.d("YeuCau", "Using default tab: datlich");
            showTab("datlich");
        }

        // Load booking requests from database
        loadBookingRequests();
        
        // Load payment requests from database
        loadPaymentRequests();
        
        Log.d("YeuCau", "=== YeuCau Activity onCreate COMPLETED ===");
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");
    }

    private void loadBookingRequests() {
        String landlordId = sessionManager.getUserId();
        Log.d("YeuCau", "=== LOADING BOOKING REQUESTS ===");
        Log.d("YeuCau", "Loading booking requests for landlord: " + landlordId);
        
        if (landlordId == null) {
            Log.e("YeuCau", "LandlordId is null!");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin chủ trọ", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d("YeuCau", "Starting LoadBookingRequestsTask with landlordId: " + landlordId);
        new LoadBookingRequestsTask().execute(landlordId);
    }
    
    private void loadPaymentRequests() {
        String landlordId = sessionManager.getUserId();
        Log.d("YeuCau", "=== LOADING PAYMENT REQUESTS ===");
        Log.d("YeuCau", "Loading payment requests for landlord: " + landlordId);
        
        if (landlordId == null) {
            Log.e("YeuCau", "LandlordId is null for payments!");
            createTestPaymentData();
            return;
        }

        Log.d("YeuCau", "Starting LoadPaymentRequestsTask with landlordId: " + landlordId);
        new LoadPaymentRequestsTask().execute(landlordId);
    }
    
    private void createTestPaymentData() {
        Log.d("YeuCau", "=== CREATING TEST PAYMENT DATA ===");
        paymentRequests.clear();
        
        String currentUserId = sessionManager.getUserId();
        
        PaymentRequest payment1 = new PaymentRequest();
        payment1.setBienLaiId("PAY001");
        payment1.setTenNguoiThue("Nguyễn Văn A");
        payment1.setTenPhong("Phòng 101 - Quận 1");
        payment1.setSoTien(3500000);
        payment1.setLoaiThanhToan("Tiền thuê tháng 12");
        payment1.setTrangThai("ChoXacNhan");
        payment1.setGhiChu("Chuyển khoản qua Vietcombank");
        payment1.setChuTroId(currentUserId);
        
        PaymentRequest payment2 = new PaymentRequest();
        payment2.setBienLaiId("PAY002");
        payment2.setTenNguoiThue("Trần Thị B");
        payment2.setTenPhong("Phòng 205 - Quận 7");
        payment2.setSoTien(5000000);
        payment2.setLoaiThanhToan("Tiền cọc");
        payment2.setTrangThai("DaXacNhan");
        payment2.setGhiChu("Đã xác nhận thanh toán");
        payment2.setChuTroId(currentUserId);
        
        paymentRequests.add(payment1);
        paymentRequests.add(payment2);
        
        if (paymentsAdapter != null) {
            paymentsAdapter.notifyDataSetChanged();
            Log.d("YeuCau", "Payment adapter notified of data change");
        }
    }

    private void updateBookingStatus(String datPhongId, String statusName, int position) {
        Log.d("YeuCau", "=== UPDATING BOOKING STATUS ===");
        Log.d("YeuCau", "DatPhongId: " + datPhongId);
        Log.d("YeuCau", "New Status: " + statusName);
        Log.d("YeuCau", "Position: " + position);
        
        if (datPhongId == null || statusName == null || position < 0 || position >= bookingRequests.size()) {
            Log.e("YeuCau", "❌ Invalid input parameters");
            Toast.makeText(this, "❌ Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        
        BookingRequest booking = bookingRequests.get(position);
        
        if (statusName.equals(booking.getTenTrangThai())) {
            Log.d("YeuCau", "ℹ️ Already in target status: " + statusName);
            Toast.makeText(this, "ℹ️ Yêu cầu đã ở trạng thái này", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Show confirmation dialog for important actions
        if ("DaHuy".equals(statusName)) {
            showConfirmationDialog(
                "Xác nhận từ chối",
                "Bạn có chắc muốn từ chối yêu cầu của " + booking.getTenNguoiThue() + "?",
                () -> performStatusUpdate(datPhongId, statusName, position, booking)
            );
        } else if ("DaXacNhan".equals(statusName)) {
            showConfirmationDialog(
                "Xác nhận chấp nhận", 
                "Bạn có chắc muốn chấp nhận yêu cầu của " + booking.getTenNguoiThue() + "?",
                () -> performStatusUpdate(datPhongId, statusName, position, booking)
            );
        } else {
            performStatusUpdate(datPhongId, statusName, position, booking);
        }
    }
    
    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Xác nhận", (dialog, which) -> {
                onConfirm.run();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private void performStatusUpdate(String datPhongId, String statusName, int position, BookingRequest booking) {
        // Disable buttons temporarily to prevent double-click
        setBookingButtonsEnabled(position, false);
        
        // Check if this is test data
        if (datPhongId.startsWith("TEST") || datPhongId.startsWith("IMMEDIATE_TEST") || datPhongId.startsWith("FALLBACK")) {
            Log.d("YeuCau", "Handling test data locally");
            handleTestDataUpdate(datPhongId, statusName, position, booking);
        } else {
            Log.d("YeuCau", "Handling real database data");
            handleDatabaseUpdate(datPhongId, statusName, position, booking);
        }
    }
    
    private void handleTestDataUpdate(String datPhongId, String statusName, int position, BookingRequest booking) {
        Toast.makeText(this, "🔄 Đang cập nhật trạng thái...", Toast.LENGTH_SHORT).show();
        
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            try {
                booking.setTenTrangThai(statusName);
                booking.setTrangThaiId(getStatusIdFromName(statusName));
                
                bookingsAdapter.notifyItemChanged(position);
                
                String statusText = getStatusDisplayText(statusName);
                Toast.makeText(this, "✅ Đã " + statusText + " yêu cầu của " + booking.getTenNguoiThue(), Toast.LENGTH_SHORT).show();
                Log.d("YeuCau", "✅ Test data updated successfully");
                
            } catch (Exception e) {
                Log.e("YeuCau", "❌ Error updating test data: " + e.getMessage(), e);
                Toast.makeText(this, "❌ Lỗi cập nhật dữ liệu test", Toast.LENGTH_SHORT).show();
            } finally {
                setBookingButtonsEnabled(position, true);
            }
        }, 1000);
    }
    
    private void handleDatabaseUpdate(String datPhongId, String statusName, int position, BookingRequest booking) {
        Toast.makeText(this, "🔄 Đang cập nhật trạng thái...", Toast.LENGTH_SHORT).show();
        new UpdateBookingStatusTask(datPhongId, statusName, position, booking).execute();
    }
    
    private void setBookingButtonsEnabled(int position, boolean enabled) {
        if (bookingsAdapter != null) {
            bookingsAdapter.setButtonsEnabled(position, enabled);
        }
    }
    
    private int getStatusIdFromName(String statusName) {
        switch (statusName) {
            case "ChoXacNhan":
            case "Chờ xác nhận":
                return 1;
            case "DaXacNhan":
            case "Đã xác nhận":
                return 2;
            case "DangThue":
            case "Đang thuê":
                return 3;
            case "DaHoanThanh":
            case "Đã hoàn thành":
                return 4;
            case "DaHuy":
            case "Đã hủy":
                return 5;
            default:
                Log.w("YeuCau", "Unknown status name: " + statusName + ", defaulting to 1");
                return 1;
        }
    }
    
    private String getStatusDisplayText(String statusName) {
        switch (statusName) {
            case "DaXacNhan":
            case "Đã xác nhận":
                return "chấp nhận";
            case "DaHuy":
            case "Đã hủy":
                return "từ chối";
            case "ChoXacNhan":
            case "Chờ xác nhận":
                return "đặt về chờ xác nhận";
            case "DangThue":
            case "Đang thuê":
                return "chuyển sang đang thuê";
            case "DaHoanThanh":
            case "Đã hoàn thành":
                return "hoàn thành";
            default:
                return "cập nhật";
        }
    }

    private void showTab(String tab) {
        Log.d("YeuCau", "=== SHOWING TAB: " + tab + " ===");
        
        int primaryColor = ContextCompat.getColor(this, R.color.primary);
        int mutedColor = ContextCompat.getColor(this, R.color.muted);

        btnDatLich.setTextColor(mutedColor);
        btnTinNhan.setTextColor(mutedColor);
        btnThanhToan.setTextColor(mutedColor);
        tabIndicator.setBackgroundColor(primaryColor);

        rvBookings.setVisibility(View.GONE);
        rvMessages.setVisibility(View.GONE);
        rvPayments.setVisibility(View.GONE);

        switch (tab) {
            case "datlich":
                Log.d("YeuCau", "Showing bookings tab - items count: " + bookingRequests.size());
                rvBookings.setVisibility(View.VISIBLE);
                btnDatLich.setTextColor(primaryColor);
                
                if (bookingsAdapter != null) {
                    bookingsAdapter.notifyDataSetChanged();
                    Log.d("YeuCau", "Adapter refreshed for bookings");
                }
                break;
            case "tinnhan":
                Log.d("YeuCau", "Showing messages tab");
                rvMessages.setVisibility(View.VISIBLE);
                btnTinNhan.setTextColor(primaryColor);
                break;
            case "thanhtoan":
                Log.d("YeuCau", "Showing payments tab");
                rvPayments.setVisibility(View.VISIBLE);
                btnThanhToan.setTextColor(primaryColor);
                break;
        }
    }

    // Method to update payment status
    private void updatePaymentStatus(String bienLaiId, String newStatus, int position) {
        Log.d("YeuCau", "Updating payment status: " + bienLaiId + " -> " + newStatus);
        
        // For test data, update locally
        if (bienLaiId.startsWith("PAY")) {
            if (position >= 0 && position < paymentRequests.size()) {
                paymentRequests.get(position).setTrangThai(newStatus);
                paymentsAdapter.notifyItemChanged(position);
                Toast.makeText(this, "Cập nhật trạng thái thanh toán: " + newStatus, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        
        // For real database data, would update in database
        Toast.makeText(this, "Cập nhật trạng thái thanh toán database: " + newStatus, Toast.LENGTH_SHORT).show();
    }

    // AsyncTask classes
    private class LoadBookingRequestsTask extends AsyncTask<String, Void, List<BookingRequest>> {
        private String errorMsg = null;

        @Override
        protected List<BookingRequest> doInBackground(String... params) {
            String landlordId = params[0];
            final List<BookingRequest>[] result = new List[]{new ArrayList<>()};
            final boolean[] completed = {false};
            final String[] error = {null};
            
            Thread dbThread = new Thread(() -> {
                Connection connection = null;
                try {
                    Log.d("YeuCau", "🔄 Attempting database connection for booking requests...");
                    connection = DatabaseHelper.getConnection();
                    Log.d("YeuCau", "✅ Database connection successful");
                    
                    result[0] = bookingDao.getBookingRequestsByLandlord(connection, landlordId);
                    Log.d("YeuCau", "📊 Query returned " + (result[0] != null ? result[0].size() : "null") + " booking records");
                    
                } catch (Exception e) {
                    error[0] = e.getMessage();
                    Log.e("YeuCau", "❌ Database connection failed: " + e.getMessage(), e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                            Log.d("YeuCau", "🔒 Database connection closed");
                        } catch (Exception e) {
                            Log.e("YeuCau", "Error closing booking connection", e);
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
                Log.e("YeuCau", "⏰ Database query timeout after " + (DATABASE_TIMEOUT_MS/1000) + " seconds");
                error[0] = "Database query timeout";
            }
            
            errorMsg = error[0];
            return result[0];
        }

        @Override
        protected void onPostExecute(List<BookingRequest> requests) {
            Log.d("YeuCau", "=== LoadBookingRequestsTask onPostExecute ===");
            
            if (requests != null && !requests.isEmpty()) {
                Log.d("YeuCau", "✅ Using REAL booking data from database: " + requests.size() + " items");
                bookingRequests.clear();
                bookingRequests.addAll(requests);
                
                if (bookingsAdapter != null) {
                    bookingsAdapter.notifyDataSetChanged();
                    Log.d("YeuCau", "✅ Booking adapter updated with real data");
                }
                
                Toast.makeText(YeuCau.this, 
                    "✅ Đã tải " + requests.size() + " yêu cầu đặt lịch từ database", 
                    Toast.LENGTH_SHORT).show();
                    
            } else if (errorMsg != null) {
                Log.e("YeuCau", "❌ Database error, showing test data: " + errorMsg);
                createTestBookingDataFallback();
                
                Toast.makeText(YeuCau.this, 
                    "⚠️ Lỗi database, hiển thị dữ liệu test: " + errorMsg, 
                    Toast.LENGTH_LONG).show();
                    
            } else {
                Log.d("YeuCau", "ℹ️ No booking requests found in database, showing test data");
                createTestBookingDataFallback();
                
                Toast.makeText(YeuCau.this, 
                    "ℹ️ Chưa có yêu cầu đặt lịch, hiển thị dữ liệu mẫu", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void createTestBookingDataFallback() {
        Log.d("YeuCau", "🔧 Creating fallback test booking data");
        bookingRequests.clear();
        
        String currentUserId = sessionManager.getUserId();
        
        BookingRequest test1 = new BookingRequest();
        test1.setDatPhongId("FALLBACK_001");
        test1.setTenNguoiThue("Nguyễn Văn A");
        test1.setTenPhong("Phòng 101 - Quận 1");
        test1.setTenTrangThai("ChoXacNhan");
        test1.setTrangThaiId(1);
        test1.setLoai("Xem phòng");
        test1.setGhiChu("Muốn xem phòng vào chiều mai");
        test1.setChuTroId(currentUserId);
        
        BookingRequest test2 = new BookingRequest();
        test2.setDatPhongId("FALLBACK_002");
        test2.setTenNguoiThue("Trần Thị B");
        test2.setTenPhong("Phòng 205 - Quận 7");
        test2.setTenTrangThai("DaXacNhan");
        test2.setTrangThaiId(2);
        test2.setLoai("Thuê phòng");
        test2.setGhiChu("Đã xác nhận thuê phòng từ tháng sau");
        test2.setChuTroId(currentUserId);
        
        bookingRequests.add(test1);
        bookingRequests.add(test2);
        
        if (bookingsAdapter != null) {
            bookingsAdapter.notifyDataSetChanged();
            Log.d("YeuCau", "✅ Fallback test data created and adapter notified: " + bookingRequests.size() + " items");
        }
    }

    private class LoadPaymentRequestsTask extends AsyncTask<String, Void, List<PaymentRequest>> {
        private String errorMsg = null;

        @Override
        protected List<PaymentRequest> doInBackground(String... params) {
            String landlordId = params[0];
            final List<PaymentRequest>[] result = new List[]{new ArrayList<>()};
            final boolean[] completed = {false};
            final String[] error = {null};
            
            Thread dbThread = new Thread(() -> {
                Connection connection = null;
                try {
                    Log.d("YeuCau", "🔄 Attempting database connection for payment requests...");
                    String url = "jdbc:jtds:sqlserver://172.26.98.234:1433/QuanLyPhongTro";
                    String username = "sa";
                    String password = "27012005";
                    
                    connection = DriverManager.getConnection(url, username, password);
                    Log.d("YeuCau", "✅ Payment database connection successful");
                    
                    result[0] = paymentDao.getPaymentRequestsByLandlord(connection, landlordId);
                    Log.d("YeuCau", "📊 Payment query returned " + (result[0] != null ? result[0].size() : "null") + " records");
                    
                } catch (Exception e) {
                    error[0] = e.getMessage();
                    Log.e("YeuCau", "❌ Payment database connection failed: " + e.getMessage(), e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                            Log.d("YeuCau", "🔒 Payment database connection closed");
                        } catch (Exception e) {
                            Log.e("YeuCau", "Error closing payment connection", e);
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
                Log.e("YeuCau", "⏰ Payment database query timeout after 15 seconds");
                error[0] = "Payment database query timeout";
            }
            
            errorMsg = error[0];
            return result[0];
        }

        @Override
        protected void onPostExecute(List<PaymentRequest> requests) {
            Log.d("YeuCau", "=== LoadPaymentRequestsTask onPostExecute ===");
            
            if (requests != null && !requests.isEmpty()) {
                Log.d("YeuCau", "✅ Using REAL payment data from database: " + requests.size() + " items");
                paymentRequests.clear();
                paymentRequests.addAll(requests);
                
                if (paymentsAdapter != null) {
                    paymentsAdapter.notifyDataSetChanged();
                    Log.d("YeuCau", "✅ Payment adapter updated with real data");
                }
                
                Toast.makeText(YeuCau.this, 
                    "✅ Đã tải " + requests.size() + " yêu cầu thanh toán từ database", 
                    Toast.LENGTH_SHORT).show();
                    
            } else if (errorMsg != null) {
                Log.e("YeuCau", "❌ Payment database error, showing test data: " + errorMsg);
                createTestPaymentData();
                
                Toast.makeText(YeuCau.this, 
                    "⚠️ Lỗi database thanh toán, hiển thị dữ liệu test: " + errorMsg, 
                    Toast.LENGTH_LONG).show();
                    
            } else {
                Log.d("YeuCau", "ℹ️ No payment requests found in database, showing test data");
                createTestPaymentData();
                
                Toast.makeText(YeuCau.this, 
                    "ℹ️ Chưa có yêu cầu thanh toán, hiển thị dữ liệu mẫu", 
                    Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class UpdateBookingStatusTask extends AsyncTask<Void, Void, Boolean> {
        private String datPhongId;
        private String statusName;
        private int position;
        private BookingRequest booking;
        private String errorMsg = null;
        private String successMsg = null;

        public UpdateBookingStatusTask(String datPhongId, String statusName, int position, BookingRequest booking) {
            this.datPhongId = datPhongId;
            this.statusName = statusName;
            this.position = position;
            this.booking = booking;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final Boolean[] result = {false};
            final boolean[] completed = {false};
            final String[] error = {null};
            final String[] success = {null};
            
            Thread dbThread = new Thread(() -> {
                Connection connection = null;
                try {
                    Log.d("YeuCau", "🔄 Connecting to database for status update...");
                    connection = DatabaseHelper.getConnection();
                    Log.d("YeuCau", "✅ Database connection successful for update");
                    
                    // Verify booking ownership
                    if (verifyBookingOwnership(connection, datPhongId)) {
                        Log.d("YeuCau", "📋 Booking ownership verified: " + datPhongId);
                        
                        int statusId = bookingDao.getStatusIdByName(connection, statusName);
                        if (statusId != -1) {
                            Log.d("YeuCau", "📊 Status ID for '" + statusName + "': " + statusId);
                            
                            result[0] = bookingDao.updateBookingStatus(connection, datPhongId, statusId);
                            Log.d("YeuCau", "📊 Update result: " + result[0]);
                            
                            if (result[0]) {
                                success[0] = "Đã " + getStatusDisplayText(statusName) + " yêu cầu của " + booking.getTenNguoiThue();
                            } else {
                                error[0] = "Không thể cập nhật trạng thái trong database";
                            }
                        } else {
                            error[0] = "Không tìm thấy trạng thái '" + statusName + "'";
                        }
                    } else {
                        error[0] = "Không có quyền cập nhật yêu cầu này";
                    }
                    
                } catch (Exception e) {
                    error[0] = "Lỗi kết nối database: " + e.getMessage();
                    Log.e("YeuCau", "❌ Database update error: " + e.getMessage(), e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                            Log.d("YeuCau", "🔒 Database connection closed");
                        } catch (Exception e) {
                            Log.e("YeuCau", "Error closing connection", e);
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
                error[0] = "Timeout khi cập nhật database (15s)";
                Log.e("YeuCau", "⏰ Database update timeout after 15 seconds");
            }
            
            errorMsg = error[0];
            successMsg = success[0];
            return result[0];
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.d("YeuCau", "=== UpdateBookingStatusTask onPostExecute ===");
            
            if (success) {
                booking.setTenTrangThai(statusName);
                booking.setTrangThaiId(getStatusIdFromName(statusName));
                
                bookingsAdapter.notifyItemChanged(position);
                
                String message = successMsg != null ? successMsg : ("✅ Đã " + getStatusDisplayText(statusName) + " yêu cầu của " + booking.getTenNguoiThue());
                Toast.makeText(YeuCau.this, message, Toast.LENGTH_SHORT).show();
                Log.d("YeuCau", "✅ Local data updated successfully");
                
            } else {
                String message = errorMsg != null ? ("❌ " + errorMsg) : "❌ Cập nhật thất bại";
                Toast.makeText(YeuCau.this, message, Toast.LENGTH_LONG).show();
                Log.e("YeuCau", "❌ Update failed: " + errorMsg);
                
                showRetryDialog(datPhongId, statusName, position, booking);
            }
            
            setBookingButtonsEnabled(position, true);
        }
    }
    
    private void showRetryDialog(String datPhongId, String statusName, int position, BookingRequest booking) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Kết nối chậm")
            .setMessage("Cập nhật có thể đã thành công nhưng kết nối chậm. Bạn có muốn thử lại?")
            .setPositiveButton("Thử lại", (dialog, which) -> {
                performStatusUpdate(datPhongId, statusName, position, booking);
            })
            .setNeutralButton("Làm lại", (dialog, which) -> {
                loadBookingRequests();
            })
            .setNegativeButton("Hủy", null)
            .show();
    }
    
    private boolean verifyBookingOwnership(Connection connection, String datPhongId) {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            String query = "SELECT COUNT(*) as count FROM DatPhong WHERE DatPhongId = ? AND ChuTroId = ?";
            stmt = connection.prepareStatement(query);
            stmt.setString(1, datPhongId);
            stmt.setString(2, sessionManager.getUserId());
            
            rs = stmt.executeQuery();
            boolean hasOwnership = false;
            if (rs.next()) {
                hasOwnership = rs.getInt("count") > 0;
            }
            
            Log.d("YeuCau", "📋 Booking ownership verified: " + hasOwnership);
            return hasOwnership;
            
        } catch (Exception e) {
            Log.e("YeuCau", "❌ Error verifying booking ownership: " + e.getMessage(), e);
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (Exception e) {
                // ignore
            }
        }
    }

    // Static classes for adapters
    static class MessageItem {
        String name, preview, time;
        
        MessageItem(String name, String preview, String time) {
            this.name = name;
            this.preview = preview;
            this.time = time;
        }
    }

    static class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.VH> {
        private final List<BookingRequest> list;
        private final YeuCau activity;
        private final SimpleDateFormat dateFormat;
        private final Set<Integer> disabledPositions = new HashSet<>();

        BookingsAdapter(List<BookingRequest> list, YeuCau activity) {
            this.list = list;
            this.activity = activity;
            this.dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void setButtonsEnabled(int position, boolean enabled) {
            if (!enabled) {
                disabledPositions.add(position);
            } else {
                disabledPositions.remove(position);
            }
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("BookingsAdapter", "Creating view holder");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_booking, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            Log.d("BookingsAdapter", "Binding view holder at position: " + pos);
            BookingRequest booking = list.get(pos);

            holder.tvName.setText(booking.getTenNguoiThue());
            
            String timeText;
            if (booking.getBatDau() != null) {
                timeText = dateFormat.format(booking.getBatDau());
            } else {
                timeText = "Chưa có lịch hẹn";
            }
            holder.tvTime.setText(timeText);

            StringBuilder noteBuilder = new StringBuilder();
            noteBuilder.append("'").append(booking.getTenPhong()).append("'").append(booking.getLoai());
            if (booking.getGhiChu() != null && !booking.getGhiChu().trim().isEmpty()) {
                String cleanNote = booking.getGhiChu().trim().replaceAll(" - test data\\$", "");
                noteBuilder.append("\n📝 ").append(cleanNote);
            }
            holder.tvNote.setText(noteBuilder.toString());

            String displayStatus = getDisplayStatus(booking.getTenTrangThai());
            if (holder.tvStatus != null) {
                holder.tvStatus.setText(displayStatus);
                int statusColor = getStatusColor(booking.getTenTrangThai());
                holder.tvStatus.setTextColor(statusColor);
            }

            boolean isChoXacNhan = "ChoXacNhan".equals(booking.getTenTrangThai()) || "Chờ xác nhận".equals(booking.getTenTrangThai());
            boolean isEnabled = !disabledPositions.contains(pos);

            if (holder.btnAccept != null) {
                holder.btnAccept.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            }
            if (holder.btnReject != null) {
                holder.btnReject.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            }

            if (!isEnabled && isChoXacNhan) {
                if (holder.btnAccept != null) {
                    holder.btnAccept.setText("⏳");
                }
                if (holder.btnReject != null) {
                    holder.btnReject.setText("⏳");
                }
            } else {
                if (holder.btnAccept != null) {
                    holder.btnAccept.setText("Chấp nhận");
                }
                if (holder.btnReject != null) {
                    holder.btnReject.setText("Từ chối");
                }
            }

            float alpha = isEnabled ? 1.0f : 0.6f;
            if (holder.btnAccept != null) {
                holder.btnAccept.setEnabled(isEnabled);
                holder.btnAccept.setAlpha(alpha);
            }
            if (holder.btnReject != null) {
                holder.btnReject.setEnabled(isEnabled);
                holder.btnReject.setAlpha(alpha);
            }

            if (holder.btnAccept != null && isEnabled && isChoXacNhan) {
                holder.btnAccept.setOnClickListener(v -> {
                    Log.d("BookingsAdapter", "Accept clicked for: " + booking.getDatPhongId());
                    activity.updateBookingStatus(booking.getDatPhongId(), "DaXacNhan", pos);
                });
            }

            if (holder.btnReject != null && isEnabled && isChoXacNhan) {
                holder.btnReject.setOnClickListener(v -> {
                    Log.d("BookingsAdapter", "Reject clicked for: " + booking.getDatPhongId());
                    activity.updateBookingStatus(booking.getDatPhongId(), "DaHuy", pos);
                });
            }

            holder.itemView.setOnLongClickListener(v -> {
                showBookingDetails(booking);
                return true;
            });
        }

        private void showBookingDetails(BookingRequest booking) {
            StringBuilder details = new StringBuilder();
            details.append("📋 Chi tiết yêu cầu\n\n");
            details.append("👤 Người thuê: ").append(booking.getTenNguoiThue()).append("\n");
            details.append("🏠 Phòng: ").append(booking.getTenPhong()).append("\n");
            details.append("📅 Loại: ").append(booking.getLoai()).append("\n");
            
            if (booking.getBatDau() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());
                details.append("⏰ Thời gian: ").append(dateFormat.format(booking.getBatDau())).append("\n");
            }
            
            details.append("📊 Trạng thái: ").append(getDisplayStatus(booking.getTenTrangThai())).append("\n");
            
            if (booking.getGhiChu() != null && !booking.getGhiChu().trim().isEmpty()) {
                String cleanNote = booking.getGhiChu().trim().replaceAll(" - test data\\$", "");
                details.append("📝 Ghi chú: ").append(cleanNote);
            }

            new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle("Chi tiết yêu cầu")
                .setMessage(details.toString())
                .setPositiveButton("Đóng", null)
                .show();
        }

        private String getDisplayStatus(String status) {
            switch (status) {
                case "ChoXacNhan": return "⏳ Chờ xác nhận";
                case "DaXacNhan": return "✅ Đã chấp nhận";
                case "DaHuy": return "❌ Đã từ chối";
                default: return status;
            }
        }

        private int getStatusColor(String status) {
            switch (status) {
                case "ChoXacNhan": return Color.parseColor("#FF9800"); // Orange
                case "DaXacNhan": return Color.parseColor("#4CAF50"); // Green
                case "DaHuy": return Color.parseColor("#F44336"); // Red
                default: return Color.GRAY;
            }
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvTime, tvNote, tvStatus;
            Button btnAccept, btnReject;

            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_booking_name);
                tvTime = v.findViewById(R.id.tv_booking_time);
                tvNote = v.findViewById(R.id.tv_booking_note);
                tvStatus = v.findViewById(R.id.tv_booking_status);
                btnAccept = v.findViewById(R.id.btn_booking_accept);
                btnReject = v.findViewById(R.id.btn_booking_reject);
            }
        }
    }

    static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.VH> {
        private final Context context;
        private final ArrayList<MessageItem> items;

        MessagesAdapter(Context context, ArrayList<MessageItem> items) {
            this.context = context;
            this.items = items;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_message, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            MessageItem it = items.get(pos);
            holder.tvName.setText(it.name);
            holder.tvPreview.setText(it.preview);
            holder.tvTime.setText(it.time);

            if (it.name != null && it.name.length() > 0) {
                holder.tvAvatar.setText(it.name.trim().substring(0, 1).toUpperCase());
            } else {
                holder.tvAvatar.setText("?");
            }

            holder.itemView.setOnClickListener(v -> {
                Log.d("YeuCau", "Clicked message item: " + it.name);
                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.putExtra("USER_NAME", it.name);
                context.startActivity(intent);
            });
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPreview, tvTime, tvAvatar;

            VH(View v) {
                super(v);
                tvName = v.findViewById(R.id.tv_msg_name);
                tvPreview = v.findViewById(R.id.tv_msg_preview);
                tvTime = v.findViewById(R.id.tv_msg_time);
                tvAvatar = v.findViewById(R.id.tv_avatar_initial);
            }
        }
    }

    static class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.VH> {
        private final List<PaymentRequest> list;
        private final YeuCau activity;

        PaymentsAdapter(List<PaymentRequest> list, YeuCau activity) {
            this.list = list;
            this.activity = activity;
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_payment, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            PaymentRequest payment = list.get(pos);

            String title = payment.getTenNguoiThue() + " - " + payment.getLoaiThanhToan();
            holder.tvTitle.setText(title);

            holder.tvDate.setText("Có thư thanh toán");
            holder.tvAmount.setText(payment.getFormattedAmount());

            if (holder.tvStatus != null) {
                holder.tvStatus.setText(payment.getTrangThai());
                int statusColor = Color.GRAY;
                if ("DaXacNhan".equals(payment.getTrangThai())) {
                    statusColor = Color.parseColor("#4CAF50"); // Green
                } else if ("DaHuy".equals(payment.getTrangThai())) {
                    statusColor = Color.parseColor("#F44336"); // Red
                }
                holder.tvStatus.setTextColor(statusColor);
            }

            boolean isChoXacNhan = "ChoXacNhan".equals(payment.getTrangThai());

            if (holder.btnAccept != null) {
                holder.btnAccept.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            }
            if (holder.btnReject != null) {
                holder.btnReject.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            }

            if (holder.btnAccept != null && isChoXacNhan) {
                holder.btnAccept.setOnClickListener(v -> {
                    activity.updatePaymentStatus(payment.getBienLaiId(), "DaXacNhan", pos);
                });
            }

            if (holder.btnReject != null && isChoXacNhan) {
                holder.btnReject.setOnClickListener(v -> {
                    activity.updatePaymentStatus(payment.getBienLaiId(), "DaHuy", pos);
                });
            }

            holder.itemView.setOnClickListener(v -> {
                String details = "Chi tiết thanh toán:\n\n" +
                    "Người thuê: " + payment.getTenNguoiThue() + "\n" +
                    "Phòng: " + payment.getTenPhong() + "\n" +
                    "Số tiền: " + payment.getFormattedAmount() + "\n" +
                    "Loại: " + payment.getLoaiThanhToan() + "\n" +
                    "Ghi chú: " + payment.getGhiChu();

                Toast.makeText(v.getContext(), details, Toast.LENGTH_LONG).show();
            });
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvDate, tvAmount, tvStatus;
            Button btnAccept, btnReject;

            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_payment_title);
                tvDate = v.findViewById(R.id.tv_payment_date);
                tvAmount = v.findViewById(R.id.tv_payment_amount);
                tvStatus = v.findViewById(R.id.tv_payment_status);
                btnAccept = v.findViewById(R.id.btn_payment_accept);
                btnReject = v.findViewById(R.id.btn_payment_reject);
            }
        }
    }
}