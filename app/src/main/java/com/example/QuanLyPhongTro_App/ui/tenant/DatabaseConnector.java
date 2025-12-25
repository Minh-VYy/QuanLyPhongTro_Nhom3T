package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseConnector {

    // For WiFi connection (recommended)
    private static final String IP = "172.26.98.219";    // WiFi IP
        // For Android Emulator
    
    private static final String PORT = "1433";
    private static final String DATABASE = "QuanLyPhongTro";
    private static final String USERNAME = "mhoang";
    private static final String PASSWORD = "12345";
    private static final String TAG = "DatabaseConnector";

    public interface ConnectionCallback {
        void onConnectionSuccess(Connection connection);
        void onConnectionFailed(String error);
    }

    public static void connect(ConnectionCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler mainHandler = new Handler(Looper.getMainLooper());
        
        executor.execute(() -> {
            Connection connection = null;
            String error = null;
            
            // Try multiple connection approaches
            String[] connectionUrls = {
                "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE,
                "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE + ";instance=MSSQLSERVER",
                "jdbc:jtds:sqlserver://" + IP + "/" + DATABASE,
                "jdbc:jtds:sqlserver://" + IP + ":" + PORT + ";databaseName=" + DATABASE
            };
            
            for (int i = 0; i < connectionUrls.length && connection == null; i++) {
                try {
                    String url = connectionUrls[i];
                    Log.d(TAG, "Attempt " + (i+1) + " - Trying URL: " + url);
                    
                    // Load JTDS driver
                    Class.forName("net.sourceforge.jtds.jdbc.Driver");
                    
                    // Set connection properties
                    Properties props = new Properties();
                    props.setProperty("user", USERNAME);
                    props.setProperty("password", PASSWORD);
                    props.setProperty("loginTimeout", "10");
                    props.setProperty("socketTimeout", "10");
                    props.setProperty("useNTLMv2", "false");
                    props.setProperty("domain", "");
                    props.setProperty("instance", "");
                    props.setProperty("ssl", "off");
                    props.setProperty("trustServerCertificate", "true");
                    
                    // Attempt connection
                    connection = DriverManager.getConnection(url, props);
                    
                    if (connection != null && !connection.isClosed()) {
                        Log.d(TAG, "✅ Connection successful with URL: " + url);
                        break;
                    }
                    
                } catch (Exception e) {
                    Log.w(TAG, "Attempt " + (i+1) + " failed: " + e.getMessage());
                    error = e.getMessage();
                    if (i == connectionUrls.length - 1) {
                        // Last attempt failed
                        Log.e(TAG, "All connection attempts failed. Last error: " + error, e);
                    }
                }
            }
            
            // Return to main thread
            final Connection finalConnection = connection;
            final String finalError = error;
            
            mainHandler.post(() -> {
                if (finalConnection != null) {
                    Log.d(TAG, "Calling onConnectionSuccess");
                    callback.onConnectionSuccess(finalConnection);
                } else {
                    Log.e(TAG, "Calling onConnectionFailed with error: " + finalError);
                    callback.onConnectionFailed(finalError != null ? finalError : "All connection attempts failed");
                }
            });
        });
    }
    
    // Utility method to test connection
    public static void testConnection() {
        connect(new ConnectionCallback() {
            @Override
            public void onConnectionSuccess(Connection connection) {
                Log.i(TAG, "✅ Test connection successful!");
                try {
                    if (connection != null && !connection.isClosed()) {
                        connection.close();
                        Log.d(TAG, "Connection closed successfully");
                    }
                } catch (SQLException e) {
                    Log.e(TAG, "Error closing connection: " + e.getMessage());
                }
            }

            @Override
            public void onConnectionFailed(String error) {
                Log.e(TAG, "❌ Test connection failed: " + error);
            }
        });
    }
}
