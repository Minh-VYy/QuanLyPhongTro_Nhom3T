package com.example.QuanLyPhongTro_App.data;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Helper class để quản lý kết nối database với Connection Pooling
 * TỐI ƯU: Sử dụng connection pool để tránh tạo connection mới liên tục
 */
public class DatabaseHelper {
    private static final String TAG = "DatabaseHelper";
    
<<<<<<< HEAD
    // Database credentials - THAY ĐỔI THEO MÁY CHỦ CỦA BẠN
    private static final String IP = "172.26.98.234";
=======
    // Database credentials
    private static final String IP = "172.26.98.219";
>>>>>>> 72f4d68c29696a544d76cadc9461c37ded9330d8
    private static final String PORT = "1433";
    private static final String DATABASE = "QuanLyPhongTro";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "27012005";
    
    private static final String DRIVER = "net.sourceforge.jtds.jdbc.Driver";
    private static final String URL = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE 
            + ";loginTimeout=5;socketTimeout=10";  // Thêm timeout để tránh treo
    
    // Connection Pool
    private static final int POOL_SIZE = 3;
    private static BlockingQueue<Connection> connectionPool;
    private static volatile boolean isInitialized = false;

    /**
     * Khởi tạo connection pool
     */
    private static synchronized void initializePool() {
        if (isInitialized) return;
        
        connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
        isInitialized = true;
        Log.d(TAG, "Connection pool initialized with size: " + POOL_SIZE);
    }

    /**
     * Lấy connection từ pool (TỐI ƯU)
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        if (!isInitialized) {
            initializePool();
        }
        
        // Thử lấy connection có sẵn từ pool
        Connection conn = connectionPool.poll();
        
        if (conn != null && !conn.isClosed() && isConnectionValid(conn)) {
            Log.d(TAG, "Reusing connection from pool");
            return conn;
        }
        
        // Nếu không có hoặc connection hỏng, tạo mới
        Log.d(TAG, "Creating new connection...");
        Class.forName(DRIVER);
        conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        
        if (conn != null) {
            // Tối ưu connection
            conn.setAutoCommit(true);
            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            Log.d(TAG, "✅ New connection created");
        }
        
        return conn;
    }

    /**
     * Trả connection về pool thay vì đóng (TỐI ƯU)
     */
    public static void releaseConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed() && isConnectionValid(conn)) {
                    // Trả về pool nếu còn chỗ
                    if (!connectionPool.offer(conn, 1, TimeUnit.SECONDS)) {
                        conn.close();
                        Log.d(TAG, "Pool full, connection closed");
                    } else {
                        Log.d(TAG, "Connection returned to pool");
                    }
                } else {
                    conn.close();
                    Log.d(TAG, "Invalid connection closed");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error releasing connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Kiểm tra connection có hợp lệ không (thay thế isValid() không tương thích với JTDS)
     */
    private static boolean isConnectionValid(Connection conn) {
        if (conn == null) return false;
        try {
            // Thử execute một query đơn giản thay vì dùng isValid()
            return !conn.isClosed() && conn.createStatement().execute("SELECT 1");
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Đóng connection (dùng khi thực sự cần đóng)
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
     * Dọn dẹp toàn bộ pool (gọi khi app đóng)
     */
    public static synchronized void closePool() {
        if (connectionPool != null) {
            for (Connection conn : connectionPool) {
                closeConnection(conn);
            }
            connectionPool.clear();
            isInitialized = false;
            Log.d(TAG, "Connection pool closed");
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
            releaseConnection(conn);
        }
    }
}
