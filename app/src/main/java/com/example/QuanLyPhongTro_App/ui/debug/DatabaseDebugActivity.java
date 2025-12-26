package com.example.QuanLyPhongTro_App.ui.debug;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.landlord.BookingRequestDao;
import com.example.QuanLyPhongTro_App.ui.landlord.BookingRequest;
import com.example.QuanLyPhongTro_App.ui.tenant.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class DatabaseDebugActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnTestConnection, btnTestQuery, btnTestBookings;
    private static final String TAG = "DatabaseDebug";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_debug);

        tvStatus = findViewById(R.id.tv_debug_status);
        btnTestConnection = findViewById(R.id.btn_test_connection);
        btnTestQuery = findViewById(R.id.btn_test_query);
        btnTestBookings = findViewById(R.id.btn_test_bookings);

        btnTestConnection.setOnClickListener(v -> testConnection());
        btnTestQuery.setOnClickListener(v -> testBasicQuery());
        btnTestBookings.setOnClickListener(v -> testBookingQuery());
    }

    private void testConnection() {
        tvStatus.setText("Đang test kết nối database...");
        
        DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                runOnUiThread(() -> {
                    tvStatus.setText("✅ Kết nối database thành công!\n\nDatabase: QuanLyPhongTro\nIP: 172.26.98.234:1433");
                    Toast.makeText(DatabaseDebugActivity.this, "Kết nối thành công!", Toast.LENGTH_SHORT).show();
                });
                
                try {
                    connection.close();
                } catch (Exception e) {
                    Log.e(TAG, "Error closing connection", e);
                }
            }

            @Override
            public void onConnectionFailed(String error) {
                runOnUiThread(() -> {
                    tvStatus.setText("❌ Kết nối thất bại:\n\n" + error + 
                                   "\n\nKiểm tra:\n- Cùng mạng WiFi?\n- SQL Server đang chạy?\n- Firewall có block không?");
                    Toast.makeText(DatabaseDebugActivity.this, "Kết nối thất bại!", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void testBasicQuery() {
        tvStatus.setText("Đang test query cơ bản...");
        
        DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                new Thread(() -> {
                    try {
                        // Test query to count users
                        String query = "SELECT COUNT(*) as userCount FROM NguoiDung";
                        PreparedStatement stmt = connection.prepareStatement(query);
                        ResultSet rs = stmt.executeQuery();
                        
                        if (rs.next()) {
                            int userCount = rs.getInt("userCount");
                            
                            runOnUiThread(() -> {
                                tvStatus.setText("✅ Query thành công!\n\nSố người dùng trong database: " + userCount);
                                Toast.makeText(DatabaseDebugActivity.this, "Query thành công!", Toast.LENGTH_SHORT).show();
                            });
                        }
                        
                        rs.close();
                        stmt.close();
                        connection.close();
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            tvStatus.setText("❌ Query thất bại:\n\n" + e.getMessage());
                            Toast.makeText(DatabaseDebugActivity.this, "Query thất bại!", Toast.LENGTH_SHORT).show();
                        });
                        Log.e(TAG, "Query error", e);
                    }
                }).start();
            }

            @Override
            public void onConnectionFailed(String error) {
                runOnUiThread(() -> {
                    tvStatus.setText("❌ Không thể kết nối để test query:\n\n" + error);
                });
            }
        });
    }

    private void testBookingQuery() {
        tvStatus.setText("Đang test query yêu cầu đặt phòng...");
        
        DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                new Thread(() -> {
                    try {
                        BookingRequestDao dao = new BookingRequestDao();
                        String landlordId = "00000000-0000-0000-0000-000000000002"; // Demo landlord ID
                        
                        List<BookingRequest> bookings = dao.getBookingRequestsByLandlord(connection, landlordId);
                        
                        runOnUiThread(() -> {
                            if (bookings != null && !bookings.isEmpty()) {
                                StringBuilder sb = new StringBuilder();
                                sb.append("✅ Tìm thấy ").append(bookings.size()).append(" yêu cầu đặt phòng:\n\n");
                                
                                for (int i = 0; i < Math.min(bookings.size(), 3); i++) {
                                    BookingRequest booking = bookings.get(i);
                                    sb.append((i+1)).append(". ").append(booking.getTenNguoiThue())
                                      .append(" - ").append(booking.getTenPhong())
                                      .append(" (").append(booking.getTenTrangThai()).append(")\n");
                                }
                                
                                if (bookings.size() > 3) {
                                    sb.append("... và ").append(bookings.size() - 3).append(" yêu cầu khác");
                                }
                                
                                tvStatus.setText(sb.toString());
                                Toast.makeText(DatabaseDebugActivity.this, "Tìm thấy " + bookings.size() + " yêu cầu!", Toast.LENGTH_SHORT).show();
                            } else {
                                tvStatus.setText("⚠️ Không tìm thấy yêu cầu đặt phòng nào\n\nCó thể:\n- Chưa có dữ liệu demo\n- Landlord ID không đúng\n- Dữ liệu chưa được tạo");
                                Toast.makeText(DatabaseDebugActivity.this, "Không có dữ liệu", Toast.LENGTH_SHORT).show();
                            }
                        });
                        
                        connection.close();
                        
                    } catch (Exception e) {
                        runOnUiThread(() -> {
                            tvStatus.setText("❌ Lỗi query booking:\n\n" + e.getMessage());
                            Toast.makeText(DatabaseDebugActivity.this, "Lỗi query booking!", Toast.LENGTH_SHORT).show();
                        });
                        Log.e(TAG, "Booking query error", e);
                    }
                }).start();
            }

            @Override
            public void onConnectionFailed(String error) {
                runOnUiThread(() -> {
                    tvStatus.setText("❌ Không thể kết nối để test booking query:\n\n" + error);
                });
            }
        });
    }
}