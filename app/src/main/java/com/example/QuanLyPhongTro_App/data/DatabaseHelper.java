package com.example.QuanLyPhongTro_App.data;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Helper class để quản lý kết nối database
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";

    // Database credentials - THAY ĐỔI THEO MÁY CHỦ CỦA BẠN
    private static final String IP = "172.26.98.220";
    private static final String PORT = "1433";
    private static final String DATABASE = "QuanLyPhongTro";
    private static final String USERNAME = "sa";
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

    // ========== CÁC PHƯƠNG THỨC CHUYỂN ĐỔI ==========

    /**
     * Chuyển đổi OffsetDateTime sang Timestamp
     */
    public static Timestamp toTimestamp(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) return null;
        try {
            return Timestamp.valueOf(offsetDateTime.toLocalDateTime());
        } catch (Exception e) {
            Log.e(TAG, "Error converting OffsetDateTime to Timestamp", e);
            return null;
        }
    }

    /**
     * Chuyển đổi Timestamp sang OffsetDateTime
     */
    public static OffsetDateTime toOffsetDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        try {
            return timestamp.toLocalDateTime().atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            Log.e(TAG, "Error converting Timestamp to OffsetDateTime", e);
            return null;
        }
    }

    /**
     * Chuyển đổi java.sql.Date sang LocalDate
     */
    public static LocalDate toLocalDate(java.sql.Date sqlDate) {
        if (sqlDate == null) return null;
        try {
            return sqlDate.toLocalDate();
        } catch (Exception e) {
            Log.e(TAG, "Error converting sql.Date to LocalDate", e);
            return null;
        }
    }

    /**
     * Chuyển đổi LocalDate sang java.sql.Date
     */
    public static java.sql.Date toSqlDate(LocalDate localDate) {
        if (localDate == null) return null;
        try {
            return java.sql.Date.valueOf(localDate);
        } catch (Exception e) {
            Log.e(TAG, "Error converting LocalDate to sql.Date", e);
            return null;
        }
    }

    /**
     * Chuyển đổi java.sql.Timestamp sang LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) return null;
        try {
            return timestamp.toLocalDateTime();
        } catch (Exception e) {
            Log.e(TAG, "Error converting Timestamp to LocalDateTime", e);
            return null;
        }
    }

    /**
     * Lấy thời gian hiện tại dạng Timestamp
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}