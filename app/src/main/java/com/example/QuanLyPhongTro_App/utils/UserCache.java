package com.example.QuanLyPhongTro_App.utils;

import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 * UserCache - Lưu cache tên người dùng để hiển thị trong chat
 * Tránh gọi API nhiều lần cho cùng một user
 */
public class UserCache {
    private static final String TAG = "UserCache";
    private static final Map<String, String> userNameCache = new HashMap<>();
    private static final Map<String, UserInfo> userInfoCache = new HashMap<>();
    private static ApiService apiService;

    // Inner class để lưu user info
    public static class UserInfo {
        public String userId;
        public String hoTen;
        public String email;
        public long cachedTime;

        public UserInfo(String userId, String hoTen, String email) {
            this.userId = userId;
            this.hoTen = hoTen;
            this.email = email;
            this.cachedTime = System.currentTimeMillis();
        }

        // Check if cache is still valid (5 minutes)
        public boolean isValid() {
            return (System.currentTimeMillis() - cachedTime) < (5 * 60 * 1000);
        }
    }

    public UserCache(ApiService apiService) {
        UserCache.apiService = apiService;
    }

    /**
     * Get user name from cache or return userId as fallback
     */
    public static String getUserName(String userId) {
        if (userId == null || userId.isEmpty()) {
            return "Ẩn danh";
        }

        // Check cache first
        if (userNameCache.containsKey(userId)) {
            return userNameCache.get(userId);
        }

        // If not in cache, return userId (will be fetched async later)
        return userId;
    }

    /**
     * Get user name async - fetch from API if not in cache
     */
    public static void getUserNameAsync(String userId, UserNameCallback callback) {
        if (userId == null || userId.isEmpty()) {
            callback.onUserName("Ẩn danh");
            return;
        }

        // Check cache first
        if (userNameCache.containsKey(userId)) {
            Log.d(TAG, "✅ Found in cache: " + userId);
            callback.onUserName(userNameCache.get(userId));
            return;
        }

        // Fetch from API
        if (apiService != null) {
            Log.d(TAG, "🔍 Fetching user info from API: " + userId);
            // Note: You need to add a getUserById endpoint to ApiService
            // For now, we'll store it when we receive chat messages
        }
    }

    /**
     * Add user to cache
     */
    public static void addUser(String userId, String hoTen) {
        if (userId != null && !userId.isEmpty() && hoTen != null && !hoTen.isEmpty()) {
            Log.d(TAG, "💾 Caching user: " + userId + " -> " + hoTen);
            userNameCache.put(userId, hoTen);
        }
    }

    /**
     * Add user info to cache
     */
    public static void addUserInfo(String userId, String hoTen, String email) {
        if (userId != null && !userId.isEmpty()) {
            userInfoCache.put(userId, new UserInfo(userId, hoTen, email));
            addUser(userId, hoTen);
        }
    }

    /**
     * Get user info from cache
     */
    public static UserInfo getUserInfo(String userId) {
        if (userInfoCache.containsKey(userId)) {
            UserInfo info = userInfoCache.get(userId);
            if (info != null && info.isValid()) {
                return info;
            }
        }
        return null;
    }

    /**
     * Clear all cache
     */
    public static void clearCache() {
        Log.d(TAG, "🗑️ Clearing user cache");
        userNameCache.clear();
        userInfoCache.clear();
    }

    /**
     * Get cache size
     */
    public static int getCacheSize() {
        return userNameCache.size();
    }

    /**
     * Debug: Print all cached users
     */
    public static void printCache() {
        Log.d(TAG, "=== USER CACHE ===");
        for (Map.Entry<String, String> entry : userNameCache.entrySet()) {
            Log.d(TAG, entry.getKey() + " -> " + entry.getValue());
        }
        Log.d(TAG, "Total: " + userNameCache.size());
    }

    // Callback interface
    public interface UserNameCallback {
        void onUserName(String name);
    }
}

