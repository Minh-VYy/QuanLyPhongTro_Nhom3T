package com.example.QuanLyPhongTro_App.data;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Helper class để quản lý kết nối database
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    
    // Database credentials - THAY ĐỔI THEO MÁY CHỦ CỦA BẠN
    private static final String IP = "192.168.1.6";
    private static final String PORT = "1433";
    private static final String DATABASE = "QuanLyPhongTro";
    private static final String USERNAME = "mhoang";
    private static final String PASSWORD = "12345";
    
    private static final String DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    private static final String URL = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE;

    /**
     * Tạo kết nối mới đến database
     * LƯU Ý: Phải gọi trong AsyncTask hoặc background thread
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        Log.d(TAG, "Attempting to connect to database...");
        Log.d(TAG, "URL: " + URL);
        
        Class.forName(DRIVER);
        Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        
        if (conn != null) {
            Log.d(TAG, "✅ Database connection successful!");
        }
        
        return conn;
    }

    /**
     * Đóng kết nối database
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                Log.d(TAG, "Connection closed");
            } catch (SQLException e) {
                Log.e(TAG, "Error closing connection: " + e.getMessage());
            }
        }
    }

    /**
     * Test kết nối database
     */
    public static boolean testConnection() {
        Connection conn = null;
        try {
            conn = getConnection();
            return conn != null && !conn.isClosed();
        } catch (Exception e) {
            Log.e(TAG, "Connection test failed: " + e.getMessage(), e);
            return false;
        } finally {
            closeConnection(conn);
        }
    }
}
