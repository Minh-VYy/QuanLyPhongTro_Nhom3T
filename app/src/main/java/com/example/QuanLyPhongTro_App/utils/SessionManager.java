// app/src/main/java/com/example/QuanLyPhongTro_App/utils/SessionManager.java
package com.example.QuanLyPhongTro_App.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_IS_LANDLORD = "isLandlord";
    private static final String KEY_DISPLAY_ROLE = "displayRole";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_REFRESH_TOKEN = "refreshToken";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String userId, String userName, String email, String userType) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_TYPE, userType);
        editor.apply();
    }

    public void createLoginSession(String userId, String userName, String email, String userType, String phoneNumber) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_EMAIL, email);
        editor.putString(KEY_USER_TYPE, userType);
        editor.putString(KEY_USER_PHONE, phoneNumber);
        editor.apply();
    }

    public void setLandlordStatus(boolean isLandlord) {
        editor.putBoolean(KEY_IS_LANDLORD, isLandlord);
        editor.apply();
    }

    public boolean isLandlord() {
        return pref.getBoolean(KEY_IS_LANDLORD, false);
    }

    public void setDisplayRole(String role) {
        editor.putString(KEY_DISPLAY_ROLE, role);
        editor.apply();
    }

    public String getDisplayRole() {
        return pref.getString(KEY_DISPLAY_ROLE, "tenant");
    }

    public String getUserRole() {
        return pref.getString(KEY_USER_TYPE, "tenant");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    public String getUserName() {
        return pref.getString(KEY_USER_NAME, null);
    }

    public String getUserEmail() {
        return pref.getString(KEY_USER_EMAIL, null);
    }

    public String getUserPhone() {
        return pref.getString(KEY_USER_PHONE, null);
    }

    public String getUserType() {
        return pref.getString(KEY_USER_TYPE, null);
    }

    // ==================== TOKEN MANAGEMENT ====================

    public String getToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public void saveToken(String token) {
        editor.putString(KEY_TOKEN, token);
        editor.apply();

        // AUTO-EXTRACT userId from JWT nameid claim
        try {
            String userId = extractUserIdFromJWT(token);
            if (userId != null && !userId.isEmpty()) {
                editor.putString(KEY_USER_ID, userId);
                editor.apply();
                Log.d("SessionManager", "✅ Extracted userId from JWT: " + userId);
            }
        } catch (Exception e) {
            Log.w("SessionManager", "⚠️ Failed to extract userId from JWT: " + e.getMessage());
        }
    }

    /**
     * Extracts userId (GUID) from JWT token's "nameid" claim
     */
    private String extractUserIdFromJWT(String token) {
        try {
            if (token == null || token.isEmpty()) {
                Log.e("SessionManager", "❌ Token is null or empty");
                return null;
            }

            Log.d("SessionManager", "========== JWT EXTRACTION DEBUG ==========");
            Log.d("SessionManager", "Token length: " + token.length());
            Log.d("SessionManager", "Token preview: " + token.substring(0, Math.min(50, token.length())) + "...");

            String[] parts = token.split("\\.");
            Log.d("SessionManager", "JWT parts count: " + parts.length);

            if (parts.length != 3) {
                Log.e("SessionManager", "❌ Invalid JWT format - expected 3 parts, got " + parts.length);
                return null;
            }

            // Decode the payload (second part) using Android's Base64
            String payload = parts[1];
            Log.d("SessionManager", "Payload (base64) length: " + payload.length());

            byte[] decoded = Base64.decode(payload, Base64.DEFAULT);
            String decodedStr = new String(decoded, "UTF-8");

            Log.d("SessionManager", "========== JWT PAYLOAD ==========");
            Log.d("SessionManager", decodedStr);
            Log.d("SessionManager", "==================================");

            // Extract "nameid" value using simple string parsing
            // JSON format: "nameid":"<GUID>" or "http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier":"<GUID>"

            // Try standard "nameid" first
            int idx = decodedStr.indexOf("\"nameid\"");
            if (idx == -1) {
                // Try long form claim type
                Log.w("SessionManager", "⚠️ 'nameid' not found, trying long form claim...");
                idx = decodedStr.indexOf("\"http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier\"");
            }

            if (idx == -1) {
                Log.e("SessionManager", "❌ nameid claim NOT FOUND in JWT payload");
                Log.e("SessionManager", "This means C# backend didn't include ClaimTypes.NameIdentifier in token");
                return null;
            }

            idx = decodedStr.indexOf("\"", idx + 9); // Skip past "nameid": or claim type
            if (idx == -1) {
                Log.e("SessionManager", "❌ Malformed nameid claim (no opening quote)");
                return null;
            }

            int endIdx = decodedStr.indexOf("\"", idx + 1);
            if (endIdx == -1) {
                Log.e("SessionManager", "❌ Malformed nameid claim (no closing quote)");
                return null;
            }

            String userId = decodedStr.substring(idx + 1, endIdx);
            Log.d("SessionManager", "✅ Extracted userId: " + userId);
            Log.d("SessionManager", "========================================");

            return userId;
        } catch (Exception e) {
            Log.e("SessionManager", "❌ JWT decode error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public String getRefreshToken() {
        return pref.getString(KEY_REFRESH_TOKEN, null);
    }

    public void saveRefreshToken(String refreshToken) {
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public void saveTokens(String token, String refreshToken) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_REFRESH_TOKEN, refreshToken);
        editor.apply();
    }

    public boolean hasValidToken() {
        String token = getToken();
        return token != null && !token.isEmpty();
    }

    // ==================== LOGOUT ====================

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
