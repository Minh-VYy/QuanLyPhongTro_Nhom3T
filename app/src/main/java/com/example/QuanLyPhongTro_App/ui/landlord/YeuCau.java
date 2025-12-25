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
import com.example.QuanLyPhongTro_App.ui.tenant.DatabaseConnector;
import com.example.QuanLyPhongTro_App.ui.tenant.MessageDetailActivity;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class YeuCau extends AppCompatActivity {

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
        
        // DEBUG: Long click to create test data
        btnDatLich.setOnLongClickListener(v -> {
            Log.d("YeuCau", "Long clicked Dat Lich - creating test data");
            createTestDataImmediate();
            return true;
        });
        
        btnTinNhan.setOnClickListener(v -> {
            Log.d("YeuCau", "Clicked Tin Nhan tab");
            showTab("tinnhan");
        });
        // DEBUG: Long click to check database
        btnThanhToan.setOnLongClickListener(v -> {
            Log.d("YeuCau", "Long clicked Thanh Toan - checking database");
            checkDatabaseData();
            return true;
        });
        
        // DEBUG: Double tap to create database test data
        btnThanhToan.setOnClickListener(new View.OnClickListener() {
            private long lastClickTime = 0;
            
            @Override
            public void onClick(View v) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime < 500) { // Double tap within 500ms
                    Log.d("YeuCau", "Double tap detected - creating database test data");
                    createDatabaseTestData();
                } else {
                    Log.d("YeuCau", "Single click Thanh Toan tab");
                    showTab("thanhtoan");
                }
                lastClickTime = currentTime;
            }
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
    
    // Immediate test data creation for debugging - called after database check
    private void createTestDataImmediate() {
        Log.d("YeuCau", "Creating immediate test booking data");
        
        String currentUserId = sessionManager.getUserId();
        Log.d("YeuCau", "Current user ID for immediate test data: " + currentUserId);
        
        // Clear and add test data
        bookingRequests.clear();
        
        BookingRequest test1 = new BookingRequest();
        test1.setDatPhongId("IMMEDIATE_TEST001");
        test1.setTenNguoiThue("Nguyễn Văn A (Test)");
        test1.setTenPhong("Phòng 101 - Quận 1");
        test1.setTenTrangThai("ChoXacNhan");
        test1.setTrangThaiId(1);
        test1.setLoai("Xem phòng");
        test1.setGhiChu("Test data - Muốn xem phòng");
        test1.setChuTroId(currentUserId);
        
        BookingRequest test2 = new BookingRequest();
        test2.setDatPhongId("IMMEDIATE_TEST002");
        test2.setTenNguoiThue("Trần Thị B (Test)");
        test2.setTenPhong("Phòng 205 - Quận 7");
        test2.setTenTrangThai("DaXacNhan");
        test2.setTrangThaiId(2);
        test2.setLoai("Thuê phòng");
        test2.setGhiChu("Test data - Đã xác nhận");
        test2.setChuTroId(currentUserId);
        
        bookingRequests.add(test1);
        bookingRequests.add(test2);
        
        Log.d("YeuCau", "Immediate test data added: " + bookingRequests.size() + " items");
        
        if (bookingsAdapter != null) {
            bookingsAdapter.notifyDataSetChanged();
            Log.d("YeuCau", "Immediate test data - adapter notified");
        }
        
        // Show a toast to confirm
        Toast.makeText(this, "DEBUG: Tạo " + bookingRequests.size() + " yêu cầu test", Toast.LENGTH_SHORT).show();
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
        Log.d("YeuCau", "User role: " + sessionManager.getUserRole());
        Log.d("YeuCau", "User name: " + sessionManager.getUserName());
        Log.d("YeuCau", "Current bookings list size: " + bookingRequests.size());
        
        if (landlordId == null) {
            Log.e("YeuCau", "LandlordId is null!");
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin chủ trọ", Toast.LENGTH_SHORT).show();
            // Tạo dữ liệu test tạm thời
            createTestData();
            return;
        }

        Log.d("YeuCau", "Starting LoadBookingRequestsTask with landlordId: " + landlordId);
        new LoadBookingRequestsTask().execute(landlordId);
    }
    
    private void loadPaymentRequests() {
        String landlordId = sessionManager.getUserId();
        Log.d("YeuCau", "=== LOADING PAYMENT REQUESTS ===");
        Log.d("YeuCau", "Loading payment requests for landlord: " + landlordId);
        Log.d("YeuCau", "User role: " + sessionManager.getUserRole());
        Log.d("YeuCau", "User name: " + sessionManager.getUserName());
        
        if (landlordId == null) {
            Log.e("YeuCau", "LandlordId is null for payments!");
            Toast.makeText(this, "DEBUG: LandlordId is null - creating test data", Toast.LENGTH_LONG).show();
            createTestPaymentData();
            return;
        }

        Log.d("YeuCau", "Starting LoadPaymentRequestsTask with landlordId: " + landlordId);
        Toast.makeText(this, "DEBUG: Đang tải dữ liệu thanh toán cho ID: " + landlordId, Toast.LENGTH_SHORT).show();
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
        
        PaymentRequest payment3 = new PaymentRequest();
        payment3.setBienLaiId("PAY003");
        payment3.setTenNguoiThue("Lê Văn C");
        payment3.setTenPhong("Phòng 302 - Quận 3");
        payment3.setSoTien(4200000);
        payment3.setLoaiThanhToan("Tiền thuê tháng 11");
        payment3.setTrangThai("DaHuy");
        payment3.setGhiChu("Hủy do chuyển nhà");
        payment3.setChuTroId(currentUserId);
        
        PaymentRequest payment4 = new PaymentRequest();
        payment4.setBienLaiId("PAY004");
        payment4.setTenNguoiThue("Phạm Thị D");
        payment4.setTenPhong("Phòng 105 - Quận 10");
        payment4.setSoTien(2800000);
        payment4.setLoaiThanhToan("Tiền điện nước");
        payment4.setTrangThai("ChoXacNhan");
        payment4.setGhiChu("Thanh toán tiền điện nước tháng 12");
        payment4.setChuTroId(currentUserId);
        
        paymentRequests.add(payment1);
        paymentRequests.add(payment2);
        paymentRequests.add(payment3);
        paymentRequests.add(payment4);
        
        Log.d("YeuCau", "Test payment data created: " + paymentRequests.size() + " items");
        
        if (paymentsAdapter != null) {
            paymentsAdapter.notifyDataSetChanged();
            Log.d("YeuCau", "Payment adapter notified of data change");
            Toast.makeText(this, "✅ Hiển thị " + paymentRequests.size() + " yêu cầu thanh toán test", Toast.LENGTH_SHORT).show();
        } else {
            Log.e("YeuCau", "Payment adapter is null!");
        }
        
        Log.d("YeuCau", "=== TEST PAYMENT DATA CREATION COMPLETED ===");
    }
    
    private void createTestData() {
        Log.d("YeuCau", "Creating test booking data");
        bookingRequests.clear();
        
        String currentUserId = sessionManager.getUserId();
        Log.d("YeuCau", "Current user ID for test data: " + currentUserId);
        
        // Tạo một số yêu cầu đặt phòng test
        BookingRequest test1 = new BookingRequest();
        test1.setDatPhongId("TEST001");
        test1.setTenNguoiThue("Nguyễn Văn A");
        test1.setTenPhong("Phòng 101 - Quận 1");
        test1.setTenTrangThai("ChoXacNhan");
        test1.setTrangThaiId(1);
        test1.setLoai("Xem phòng");
        test1.setGhiChu("Muốn xem phòng vào chiều mai");
        test1.setChuTroId(currentUserId); // Sử dụng ID hiện tại
        
        BookingRequest test2 = new BookingRequest();
        test2.setDatPhongId("TEST002");
        test2.setTenNguoiThue("Trần Thị B");
        test2.setTenPhong("Phòng 205 - Quận 7");
        test2.setTenTrangThai("DaXacNhan");
        test2.setTrangThaiId(2);
        test2.setLoai("Thuê phòng");
        test2.setGhiChu("Đã xác nhận thuê phòng");
        test2.setChuTroId(currentUserId);
        
        BookingRequest test3 = new BookingRequest();
        test3.setDatPhongId("TEST003");
        test3.setTenNguoiThue("Lê Văn C");
        test3.setTenPhong("Phòng 302 - Quận 3");
        test3.setTenTrangThai("DaHuy");
        test3.setTrangThaiId(3);
        test3.setLoai("Xem phòng");
        test3.setGhiChu("Không phù hợp với yêu cầu");
        test3.setChuTroId(currentUserId);
        
        BookingRequest test4 = new BookingRequest();
        test4.setDatPhongId("TEST004");
        test4.setTenNguoiThue("Phạm Thị D");
        test4.setTenPhong("Phòng 105 - Quận 10");
        test4.setTenTrangThai("ChoXacNhan");
        test4.setTrangThaiId(1);
        test4.setLoai("Thuê phòng");
        test4.setGhiChu("Cần thuê gấp trong tuần này");
        test4.setChuTroId(currentUserId);
        
        bookingRequests.add(test1);
        bookingRequests.add(test2);
        bookingRequests.add(test3);
        bookingRequests.add(test4);
        
        if (bookingsAdapter != null) {
            bookingsAdapter.notifyDataSetChanged();
            Log.d("YeuCau", "Test data created and adapter notified: " + bookingRequests.size() + " items");
            Toast.makeText(this, "✅ Hiển thị " + bookingRequests.size() + " yêu cầu test", Toast.LENGTH_SHORT).show();
            
            // Force refresh the RecyclerView
            runOnUiThread(() -> {
                if (rvBookings != null && rvBookings.getVisibility() == View.VISIBLE) {
                    rvBookings.getAdapter().notifyDataSetChanged();
                    Log.d("YeuCau", "Forced RecyclerView refresh");
                }
            });
        } else {
            Log.e("YeuCau", "Adapter is null!");
        }
    }

    private class LoadBookingRequestsTask extends AsyncTask<String, Void, List<BookingRequest>> {
        private String errorMsg = null;

        @Override
        protected List<BookingRequest> doInBackground(String... params) {
            String landlordId = params[0];
            final List<BookingRequest>[] result = new List[]{new ArrayList<>()};
            final boolean[] completed = {false};
            final String[] error = {null};
            
            // Use direct connection in background thread
            Thread dbThread = new Thread(() -> {
                Connection connection = null;
                try {
                    String url = "jdbc:jtds:sqlserver://192.168.0.117:1433/QuanLyPhongTro";
                    String username = "sa";
                    String password = "27012005";
                    
                    connection = DriverManager.getConnection(url, username, password);
                    result[0] = bookingDao.getBookingRequestsByLandlord(connection, landlordId);
                    
                } catch (Exception e) {
                    error[0] = e.getMessage();
                    Log.e("YeuCau", "Direct booking connection error", e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Exception e) {
                            Log.e("YeuCau", "Error closing booking connection", e);
                        }
                    }
                    completed[0] = true;
                }
            });
            
            dbThread.start();
            
            // Wait for completion
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            errorMsg = error[0];
            return result[0];
        }

        @Override
        protected void onPostExecute(List<BookingRequest> requests) {
            Log.d("YeuCau", "=== LoadBookingRequestsTask onPostExecute ===");
            Log.d("YeuCau", "Error message: " + errorMsg);
            Log.d("YeuCau", "Requests size: " + (requests != null ? requests.size() : "null"));
            
            // ALWAYS create test data for now to ensure UI works
            Log.d("YeuCau", "FORCING test data creation for debugging");
            createTestData();
            
            if (errorMsg != null) {
                Log.e("YeuCau", "Database error: " + errorMsg);
                Toast.makeText(YeuCau.this, "Lỗi kết nối database: " + errorMsg + " (Hiển thị test data)", Toast.LENGTH_LONG).show();
                return;
            }
            
            if (requests != null && !requests.isEmpty()) {
                // Có dữ liệu thật từ database - thêm vào sau test data
                Log.d("YeuCau", "Adding " + requests.size() + " real requests to existing test data");
                bookingRequests.addAll(requests);
                bookingsAdapter.notifyDataSetChanged();
                Toast.makeText(YeuCau.this, "Tải thành công " + requests.size() + " yêu cầu từ database + test data", Toast.LENGTH_SHORT).show();
            } else {
                // Không có dữ liệu thật - chỉ có test data
                Log.d("YeuCau", "No real data from database, showing test data only");
                Toast.makeText(YeuCau.this, "Hiển thị test data (database trống)", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateBookingStatus(String datPhongId, String statusName, int position) {
        // Check if this is test data
        if (datPhongId.startsWith("TEST")) {
            // Handle test data locally
            if (position >= 0 && position < bookingRequests.size()) {
                bookingRequests.get(position).setTenTrangThai(statusName);
                bookingsAdapter.notifyItemChanged(position);
                Toast.makeText(this, "Cập nhật test data thành công: " + statusName, Toast.LENGTH_SHORT).show();
            }
            return;
        }
        
        // Handle real database data
        new UpdateBookingStatusTask(datPhongId, statusName, position).execute();
    }

    private class UpdateBookingStatusTask extends AsyncTask<Void, Void, Boolean> {
        private String datPhongId;
        private String statusName;
        private int position;
        private String errorMsg = null;

        public UpdateBookingStatusTask(String datPhongId, String statusName, int position) {
            this.datPhongId = datPhongId;
            this.statusName = statusName;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final Boolean[] result = {false};
            final boolean[] completed = {false};
            
            DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
                @Override
                public void onConnectionSuccess(Connection connection) {
                    try {
                        int statusId = bookingDao.getStatusIdByName(connection, statusName);
                        if (statusId != -1) {
                            result[0] = bookingDao.updateBookingStatus(connection, datPhongId, statusId);
                        }
                    } catch (Exception e) {
                        errorMsg = e.getMessage();
                        Log.e("YeuCau", "Error updating booking status", e);
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (Exception e) {
                                Log.e("YeuCau", "Error closing connection", e);
                            }
                        }
                        completed[0] = true;
                    }
                }

                @Override
                public void onConnectionFailed(String error) {
                    errorMsg = error;
                    completed[0] = true;
                }
            });
            
            // Wait for connection to complete
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            return result[0];
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Update local data
                if (position >= 0 && position < bookingRequests.size()) {
                    bookingRequests.get(position).setTenTrangThai(statusName);
                    bookingsAdapter.notifyItemChanged(position);
                }
                Toast.makeText(YeuCau.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
            } else {
                String msg = errorMsg != null ? errorMsg : "Cập nhật thất bại";
                Toast.makeText(YeuCau.this, msg, Toast.LENGTH_SHORT).show();
            }
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
            
            // Use a separate thread for database operations
            Thread dbThread = new Thread(() -> {
                try {
                    DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
                        @Override
                        public void onConnectionSuccess(Connection connection) {
                            // This runs on main thread, so we need to move DB operations to background
                            Thread.currentThread().interrupt(); // Signal that we got connection
                        }

                        @Override
                        public void onConnectionFailed(String errorMsg) {
                            error[0] = errorMsg;
                            completed[0] = true;
                        }
                    });
                    
                    // Wait a bit for connection
                    Thread.sleep(1000);
                    
                    // Try direct connection in background thread
                    Connection connection = null;
                    try {
                        String url = "jdbc:jtds:sqlserver://192.168.0.117:1433/QuanLyPhongTro";
                        String username = "sa";
                        String password = "27012005";
                        
                        connection = DriverManager.getConnection(url, username, password);
                        result[0] = paymentDao.getPaymentRequestsByLandlord(connection, landlordId);
                        
                    } catch (Exception e) {
                        error[0] = e.getMessage();
                        Log.e("YeuCau", "Direct connection error", e);
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (Exception e) {
                                Log.e("YeuCau", "Error closing connection", e);
                            }
                        }
                        completed[0] = true;
                    }
                    
                } catch (Exception e) {
                    error[0] = e.getMessage();
                    completed[0] = true;
                }
            });
            
            dbThread.start();
            
            // Wait for completion
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            errorMsg = error[0];
            return result[0];
        }

        @Override
        protected void onPostExecute(List<PaymentRequest> requests) {
            Log.d("YeuCau", "=== LoadPaymentRequestsTask onPostExecute ===");
            Log.d("YeuCau", "Error message: " + errorMsg);
            Log.d("YeuCau", "Requests size: " + (requests != null ? requests.size() : "null"));
            
            if (errorMsg != null) {
                Log.e("YeuCau", "Payment database error: " + errorMsg);
                Toast.makeText(YeuCau.this, "DEBUG: Lỗi database - " + errorMsg, Toast.LENGTH_LONG).show();
                // Fallback to test data only if database fails
                createTestPaymentData();
                return;
            }
            
            if (requests != null && !requests.isEmpty()) {
                // Có dữ liệu thật từ database - sử dụng dữ liệu thật
                Log.d("YeuCau", "Using real data from database: " + requests.size() + " payments");
                paymentRequests.clear();
                paymentRequests.addAll(requests);
                paymentsAdapter.notifyDataSetChanged();
                Toast.makeText(YeuCau.this, "✅ Tải thành công " + requests.size() + " yêu cầu thanh toán từ database", Toast.LENGTH_SHORT).show();
            } else {
                // Không có dữ liệu thật - hiển thị thông báo trống
                Log.d("YeuCau", "No real data from database - showing empty list");
                paymentRequests.clear();
                paymentsAdapter.notifyDataSetChanged();
                Toast.makeText(YeuCau.this, "DEBUG: Database trống - Chưa có yêu cầu thanh toán nào", Toast.LENGTH_LONG).show();
                
                // Tạm thời tạo test data để debug
                Log.d("YeuCau", "Creating test data for debugging");
                createTestPaymentData();
            }
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
                
                // Force refresh adapter
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
        
        Log.d("YeuCau", "Tab visibility - Bookings: " + rvBookings.getVisibility() + 
                       ", Messages: " + rvMessages.getVisibility() + 
                       ", Payments: " + rvPayments.getVisibility());
    }

    static class Booking {
        String name, time, note;
        Booking(String n, String t, String no) { name = n; time = t; note = no; }
    }
    static class MessageItem {
        String name, preview, time;
        MessageItem(String n, String p, String t) { name = n; preview = p; time = t; }
    }
    static class Payment {
        String title, date, amount;
        Payment(String ti, String da, String am) { title = ti; date = da; amount = am; }
    }

    static class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.VH> {
        private final List<BookingRequest> list;
        private final YeuCau activity;
        private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault());

        BookingsAdapter(List<BookingRequest> l, YeuCau activity) { 
            this.list = l; 
            this.activity = activity;
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

        @NonNull
        @Override 
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d("BookingsAdapter", "Creating view holder");
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_booking, parent, false);
            return new VH(v);
        }

        @Override 
        public void onBindViewHolder(VH holder, int pos) {
            Log.d("BookingsAdapter", "Binding view holder at position: " + pos);
            BookingRequest booking = list.get(pos);
            
            Log.d("BookingsAdapter", "Booking data: " + booking.getTenNguoiThue() + " - " + booking.getTenPhong());
            
            holder.tvName.setText(booking.getTenNguoiThue());
            
            // Format time
            String timeText = "";
            if (booking.getBatDau() != null) {
                timeText = dateFormat.format(booking.getBatDau());
            } else {
                // For test data without time
                timeText = "Chưa có lịch hẹn";
            }
            holder.tvTime.setText(timeText);
            
            // Format note with room name
            String noteText = booking.getLoai() + " '" + booking.getTenPhong() + "'";
            if (booking.getGhiChu() != null && !booking.getGhiChu().trim().isEmpty()) {
                noteText += "\nGhi chú: " + booking.getGhiChu();
            }
            holder.tvNote.setText(noteText);
            
            // Set status
            if (holder.tvStatus != null) {
                holder.tvStatus.setText(booking.getTenTrangThai());
                
                // Set status color based on status name
                int statusColor = Color.GRAY;
                String status = booking.getTenTrangThai();
                if ("ChoXacNhan".equals(status) || "Chờ xác nhận".equals(status)) {
                    statusColor = Color.parseColor("#FF9800"); // Orange
                } else if ("DaXacNhan".equals(status) || "Đã xác nhận".equals(status)) {
                    statusColor = Color.parseColor("#4CAF50"); // Green
                } else if ("DaHuy".equals(status) || "Từ chối".equals(status)) {
                    statusColor = Color.parseColor("#F44336"); // Red
                }
                holder.tvStatus.setTextColor(statusColor);
            }
            
            // Handle buttons based on status
            boolean isChoXacNhan = "ChoXacNhan".equals(booking.getTenTrangThai()) || "Chờ xác nhận".equals(booking.getTenTrangThai());
            holder.btnAccept.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            holder.btnReject.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            
            holder.btnAccept.setOnClickListener(v -> {
                Log.d("BookingsAdapter", "Accept clicked for: " + booking.getDatPhongId());
                activity.updateBookingStatus(booking.getDatPhongId(), "DaXacNhan", pos);
            });
            
            holder.btnReject.setOnClickListener(v -> {
                Log.d("BookingsAdapter", "Reject clicked for: " + booking.getDatPhongId());
                activity.updateBookingStatus(booking.getDatPhongId(), "DaHuy", pos);
            });
            
            Log.d("BookingsAdapter", "View holder bound successfully");
        }

        @Override 
        public int getItemCount() { 
            Log.d("BookingsAdapter", "getItemCount: " + list.size());
            return list.size(); 
        }
    }

    static class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.VH> {
        private final Context context;
        private final ArrayList<MessageItem> items;
        MessagesAdapter(Context context, ArrayList<MessageItem> list){ this.context = context; this.items = list; }
        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPreview, tvTime, tvAvatar;
            VH(View v){ super(v);
                tvName = v.findViewById(R.id.tv_msg_name);
                tvPreview = v.findViewById(R.id.tv_msg_preview);
                tvTime = v.findViewById(R.id.tv_msg_time);
                tvAvatar = v.findViewById(R.id.tv_avatar_initial);
            }
        }
        @NonNull
        @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_message,parent,false);
            return new VH(v);
        }
        @Override public void onBindViewHolder(VH holder,int pos){
            MessageItem it = items.get(pos);
            holder.tvName.setText(it.name);
            holder.tvPreview.setText(it.preview);
            holder.tvTime.setText(it.time);
            if (it.name!=null && it.name.length()>0) holder.tvAvatar.setText(it.name.trim().substring(0,1).toUpperCase());
            else holder.tvAvatar.setText("?");

            holder.itemView.setOnClickListener(v-> {
                Log.d("YeuCauActivity", "Clicked on item: " + it.name);
                Intent intent = new Intent(context, MessageDetailActivity.class);
                intent.putExtra("USER_NAME", it.name);
                context.startActivity(intent);
            });
        }
        @Override public int getItemCount(){ return items.size(); }
    }

    static class PaymentsAdapter extends RecyclerView.Adapter<PaymentsAdapter.VH> {
        private final List<PaymentRequest> list;
        private final YeuCau activity;
        
        PaymentsAdapter(List<PaymentRequest> l, YeuCau activity) { 
            this.list = l; 
            this.activity = activity;
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
        
        @NonNull
        @Override 
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_payment_v2, parent, false);
            return new VH(v);
        }
        
        @Override 
        public void onBindViewHolder(VH holder, int pos) {
            PaymentRequest payment = list.get(pos);
            
            // Title: Tên người thuê - Loại thanh toán
            String title = payment.getTenNguoiThue() + " - " + payment.getLoaiThanhToan();
            holder.tvTitle.setText(title);
            
            // Date: For now, show "Chưa có ngày" since we don't have date in test data
            holder.tvDate.setText("Chưa có ngày thanh toán");
            
            // Amount: Formatted money
            holder.tvAmount.setText(payment.getFormattedAmount());
            
            // Status with color
            if (holder.tvStatus != null) {
                holder.tvStatus.setText(payment.getTrangThai());
                
                int statusColor = Color.GRAY;
                String status = payment.getTrangThai();
                if ("ChoXacNhan".equals(status)) {
                    statusColor = Color.parseColor("#FF9800"); // Orange
                } else if ("DaXacNhan".equals(status)) {
                    statusColor = Color.parseColor("#4CAF50"); // Green
                } else if ("DaHuy".equals(status)) {
                    statusColor = Color.parseColor("#F44336"); // Red
                }
                holder.tvStatus.setTextColor(statusColor);
            }
            
            // Buttons based on status
            boolean isChoXacNhan = "ChoXacNhan".equals(payment.getTrangThai());
            if (holder.btnAccept != null) {
                holder.btnAccept.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            }
            if (holder.btnReject != null) {
                holder.btnReject.setVisibility(isChoXacNhan ? View.VISIBLE : View.GONE);
            }
            
            // Button click handlers
            if (holder.btnAccept != null) {
                holder.btnAccept.setOnClickListener(v -> {
                    activity.updatePaymentStatus(payment.getBienLaiId(), "DaXacNhan", pos);
                });
            }
            
            if (holder.btnReject != null) {
                holder.btnReject.setOnClickListener(v -> {
                    activity.updatePaymentStatus(payment.getBienLaiId(), "DaHuy", pos);
                });
            }
            
            // Item click for details
            holder.itemView.setOnClickListener(v -> {
                String details = "Chi tiết thanh toán:\n" +
                        "Người thuê: " + payment.getTenNguoiThue() + "\n" +
                        "Phòng: " + payment.getTenPhong() + "\n" +
                        "Số tiền: " + payment.getFormattedAmount() + "\n" +
                        "Loại: " + payment.getLoaiThanhToan() + "\n" +
                        "Ghi chú: " + payment.getGhiChu();
                Toast.makeText(v.getContext(), details, Toast.LENGTH_LONG).show();
            });
        }
        
        @Override 
        public int getItemCount() { 
            return list.size(); 
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
        
        // For real database data, update in database
        new UpdatePaymentStatusTask(bienLaiId, newStatus, position).execute();
    }
    
    private class UpdatePaymentStatusTask extends AsyncTask<Void, Void, Boolean> {
        private String bienLaiId;
        private String newStatus;
        private int position;
        private String errorMsg = null;

        public UpdatePaymentStatusTask(String bienLaiId, String newStatus, int position) {
            this.bienLaiId = bienLaiId;
            this.newStatus = newStatus;
            this.position = position;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final Boolean[] result = {false};
            final boolean[] completed = {false};
            
            DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
                @Override
                public void onConnectionSuccess(Connection connection) {
                    try {
                        result[0] = paymentDao.updatePaymentStatus(connection, bienLaiId, newStatus);
                    } catch (Exception e) {
                        errorMsg = e.getMessage();
                        Log.e("YeuCau", "Error updating payment status", e);
                    } finally {
                        if (connection != null) {
                            try {
                                connection.close();
                            } catch (Exception e) {
                                Log.e("YeuCau", "Error closing connection", e);
                            }
                        }
                        completed[0] = true;
                    }
                }

                @Override
                public void onConnectionFailed(String error) {
                    errorMsg = error;
                    completed[0] = true;
                }
            });
            
            while (!completed[0]) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            
            return result[0];
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                // Update local data
                if (position >= 0 && position < paymentRequests.size()) {
                    paymentRequests.get(position).setTrangThai(newStatus);
                    paymentsAdapter.notifyItemChanged(position);
                }
                Toast.makeText(YeuCau.this, "✅ Cập nhật trạng thái thanh toán thành công", Toast.LENGTH_SHORT).show();
            } else {
                String msg = errorMsg != null ? ("Lỗi: " + errorMsg) : "Cập nhật thất bại";
                Toast.makeText(YeuCau.this, msg, Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    // DEBUG method to check database data
    private void checkDatabaseData() {
        Log.d("YeuCau", "=== CHECKING DATABASE DATA ===");
        Toast.makeText(this, "DEBUG: Kiểm tra dữ liệu database...", Toast.LENGTH_SHORT).show();
        
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                final StringBuilder result = new StringBuilder();
                final boolean[] completed = {false};
                
                DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
                    @Override
                    public void onConnectionSuccess(Connection connection) {
                        try {
                            // Check total BienLai records
                            PreparedStatement stmt1 = connection.prepareStatement("SELECT COUNT(*) as total FROM BienLai");
                            ResultSet rs1 = stmt1.executeQuery();
                            if (rs1.next()) {
                                result.append("Total BienLai: ").append(rs1.getInt("total")).append("\n");
                            }
                            rs1.close();
                            stmt1.close();
                            
                            // Check DatPhong records
                            PreparedStatement stmt2 = connection.prepareStatement("SELECT COUNT(*) as total FROM DatPhong");
                            ResultSet rs2 = stmt2.executeQuery();
                            if (rs2.next()) {
                                result.append("Total DatPhong: ").append(rs2.getInt("total")).append("\n");
                            }
                            rs2.close();
                            stmt2.close();
                            
                            // Check current user's DatPhong
                            String userId = sessionManager.getUserId();
                            PreparedStatement stmt3 = connection.prepareStatement("SELECT COUNT(*) as total FROM DatPhong WHERE ChuTroId = ?");
                            stmt3.setString(1, userId);
                            ResultSet rs3 = stmt3.executeQuery();
                            if (rs3.next()) {
                                result.append("DatPhong for user ").append(userId).append(": ").append(rs3.getInt("total")).append("\n");
                            }
                            rs3.close();
                            stmt3.close();
                            
                            // Check BienLai with JOIN
                            PreparedStatement stmt4 = connection.prepareStatement(
                                "SELECT COUNT(*) as total FROM BienLai bl " +
                                "INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId " +
                                "WHERE dp.ChuTroId = ?");
                            stmt4.setString(1, userId);
                            ResultSet rs4 = stmt4.executeQuery();
                            if (rs4.next()) {
                                result.append("BienLai for user ").append(userId).append(": ").append(rs4.getInt("total"));
                            }
                            rs4.close();
                            stmt4.close();
                            
                        } catch (Exception e) {
                            result.append("Error: ").append(e.getMessage());
                        } finally {
                            try {
                                if (connection != null) connection.close();
                            } catch (Exception e) {
                                Log.e("YeuCau", "Error closing connection", e);
                            }
                            completed[0] = true;
                        }
                    }

                    @Override
                    public void onConnectionFailed(String error) {
                        result.append("Connection failed: ").append(error);
                        completed[0] = true;
                    }
                });
                
                while (!completed[0]) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                
                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("YeuCau", "Database check result: " + result);
                Toast.makeText(YeuCau.this, "DEBUG Database:\n" + result, Toast.LENGTH_LONG).show();
            }
        }.execute();
    }
    
    // DEBUG method to create test data in database
    private void createDatabaseTestData() {
        Log.d("YeuCau", "=== CREATING DATABASE TEST DATA ===");
        Toast.makeText(this, "DEBUG: Tạo dữ liệu test trong database...", Toast.LENGTH_SHORT).show();
        
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                final StringBuilder result = new StringBuilder();
                
                Connection connection = null;
                try {
                    String url = "jdbc:jtds:sqlserver://192.168.0.117:1433/QuanLyPhongTro";
                    String username = "sa";
                    String password = "27012005";
                    
                    connection = DriverManager.getConnection(url, username, password);
                    String userId = sessionManager.getUserId();
                    
                    // 1. Tìm hoặc tạo NguoiThue
                    String nguoiThueId = null;
                    PreparedStatement findTenant = connection.prepareStatement(
                        "SELECT TOP 1 nd.NguoiDungId FROM NguoiDung nd " +
                        "INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId " +
                        "WHERE vt.TenVaiTro = 'NguoiThue'");
                    ResultSet tenantRs = findTenant.executeQuery();
                    if (tenantRs.next()) {
                        nguoiThueId = tenantRs.getString("NguoiDungId");
                    }
                    tenantRs.close();
                    findTenant.close();
                    
                    // 2. Tìm hoặc tạo Phong
                    String phongId = null;
                    PreparedStatement findRoom = connection.prepareStatement("SELECT TOP 1 PhongId FROM Phong");
                    ResultSet roomRs = findRoom.executeQuery();
                    if (roomRs.next()) {
                        phongId = roomRs.getString("PhongId");
                    }
                    roomRs.close();
                    findRoom.close();
                    
                    if (nguoiThueId == null || phongId == null) {
                        result.append("Missing NguoiThue or Phong data");
                        return result.toString();
                    }
                    
                    // 3. Tạo DatPhong
                    String datPhongId = java.util.UUID.randomUUID().toString();
                    PreparedStatement createBooking = connection.prepareStatement(
                        "INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, BatDau, KetThuc, Loai, TrangThaiId, GhiChu) " +
                        "VALUES (?, ?, ?, ?, DATEADD(day, -30, GETDATE()), DATEADD(day, 335, GETDATE()), ?, " +
                        "(SELECT TOP 1 TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaXacNhan'), ?)");
                    createBooking.setString(1, datPhongId);
                    createBooking.setString(2, phongId);
                    createBooking.setString(3, nguoiThueId);
                    createBooking.setString(4, userId);
                    createBooking.setString(5, "Thuê phòng dài hạn");
                    createBooking.setString(6, "Test booking for payments");
                    createBooking.executeUpdate();
                    createBooking.close();
                    
                    // 4. Tạo BienLai test
                    String[] bienLaiIds = {
                        java.util.UUID.randomUUID().toString(),
                        java.util.UUID.randomUUID().toString(),
                        java.util.UUID.randomUUID().toString(),
                        java.util.UUID.randomUUID().toString()
                    };
                    
                    long[] amounts = {3500000, 5000000, 850000, 3500000};
                    boolean[] statuses = {false, true, false, false}; // false = ChoXacNhan, true = DaXacNhan
                    int[] daysBefore = {5, 10, 2, 35};
                    
                    for (int i = 0; i < 4; i++) {
                        PreparedStatement createPayment = connection.prepareStatement(
                            "INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai) " +
                            "VALUES (?, ?, ?, ?, ?, DATEADD(day, ?, GETDATE()))");
                        createPayment.setString(1, bienLaiIds[i]);
                        createPayment.setString(2, datPhongId);
                        createPayment.setString(3, nguoiThueId);
                        createPayment.setLong(4, amounts[i]);
                        createPayment.setBoolean(5, statuses[i]);
                        createPayment.setInt(6, -daysBefore[i]);
                        createPayment.executeUpdate();
                        createPayment.close();
                    }
                    
                    result.append("Created 1 DatPhong and 4 BienLai records successfully!");
                    
                } catch (Exception e) {
                    result.append("Error: ").append(e.getMessage());
                    Log.e("YeuCau", "Error creating test data", e);
                } finally {
                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (Exception e) {
                            Log.e("YeuCau", "Error closing connection", e);
                        }
                    }
                }
                
                return result.toString();
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("YeuCau", "Create test data result: " + result);
                Toast.makeText(YeuCau.this, "DEBUG Create Data:\n" + result, Toast.LENGTH_LONG).show();
                
                // Reload payment data after creating
                if (result.contains("successfully")) {
                    loadPaymentRequests();
                }
            }
        }.execute();
    }
}
