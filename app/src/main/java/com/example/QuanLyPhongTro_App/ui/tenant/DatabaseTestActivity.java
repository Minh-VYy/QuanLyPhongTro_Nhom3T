package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;

import java.net.InetAddress;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseTestActivity extends AppCompatActivity {

    private TextView tvStatus;
    private Button btnTest;
    private static final String TAG = "DatabaseTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        tvStatus = findViewById(R.id.tv_status);
        btnTest = findViewById(R.id.btn_test);

        btnTest.setOnClickListener(v -> testDatabaseConnection());
    }

    private void testDatabaseConnection() {
        tvStatus.setText("Đang kiểm tra kết nối mạng...");
        btnTest.setEnabled(false);

        // First test ping
        testPing();
    }

    private void testPing() {
        new Thread(() -> {
            try {
                // Test ping to SQL Server
                Process process = Runtime.getRuntime().exec("ping -c 1 127.0.0.1");
                int returnVal = process.waitFor();
                
                runOnUiThread(() -> {
                    if (returnVal == 0) {
                        tvStatus.setText("✅ Ping thành công!\nĐang test kết nối database...");
                        // Ping successful, now test database
                        testDatabase();
                    } else {
                        tvStatus.setText("❌ Ping thất bại!\nKhông thể kết nối đến máy SQL Server.\n\nKiểm tra:\n- Cùng mạng WiFi?\n- IP đúng chưa?\n- Firewall có block không?");
                        btnTest.setEnabled(true);
                    }
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    tvStatus.setText("❌ Lỗi ping: " + e.getMessage() + "\n\nĐang thử kết nối database...");
                    // Even if ping fails, try database connection
                    testDatabase();
                });
            }
        }).start();
    }

    private void testDatabase() {
        DatabaseConnector.connect(new DatabaseConnector.ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                runOnUiThread(() -> {
                    tvStatus.setText("✅ Kết nối thành công!\nĐang test query...");
                    Toast.makeText(DatabaseTestActivity.this, "Kết nối database thành công!", Toast.LENGTH_SHORT).show();
                });

                // Test a simple query
                testQuery(connection);
            }

            @Override
            public void onConnectionFailed(String error) {
                runOnUiThread(() -> {
                    tvStatus.setText("❌ Kết nối thất bại:\n" + error);
                    btnTest.setEnabled(true);
                    Toast.makeText(DatabaseTestActivity.this, "Lỗi kết nối: " + error, Toast.LENGTH_LONG).show();
                });
                Log.e(TAG, "Connection failed: " + error);
            }
        });
    }

    private void testQuery(Connection connection) {
        new Thread(() -> {
            try {
                // Test query to count rooms
                String query = "SELECT COUNT(*) as total FROM Phong WHERE IsDuyet = 1 AND IsBiKhoa = 0";
                PreparedStatement stmt = connection.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    int roomCount = rs.getInt("total");
                    runOnUiThread(() -> {
                        tvStatus.setText("✅ Kết nối và query thành công!\n" +
                                "Số phòng có sẵn: " + roomCount);
                        btnTest.setEnabled(true);
                    });
                }
                
                rs.close();
                stmt.close();
                connection.close();
                
            } catch (SQLException e) {
                runOnUiThread(() -> {
                    tvStatus.setText("✅ Kết nối OK, nhưng query lỗi:\n" + e.getMessage());
                    btnTest.setEnabled(true);
                });
                Log.e(TAG, "Query error: " + e.getMessage(), e);
            }
        }).start();
    }
}