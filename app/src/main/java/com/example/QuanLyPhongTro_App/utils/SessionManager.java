// app/src/main/java/com/example/QuanLyPhongTro_App/utils/SessionManager.java
package com.example.QuanLyPhongTro_App.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;
import org.json.JSONObject;

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

        // ✅ Auto-extract userId/role from JWT if possible
        try {
            String extractedUserId = JwtTokenParser.getUserIdFromToken(token);
            if (extractedUserId != null && !extractedUserId.trim().isEmpty()) {
                editor.putString(KEY_USER_ID, extractedUserId);
            }

            String role = JwtTokenParser.getRoleFromToken(token);
            if (role != null && !role.trim().isEmpty()) {
                editor.putString(KEY_USER_TYPE, role);
                editor.putString(KEY_DISPLAY_ROLE, role);
            }
        } catch (Exception ignore) {
        }

        editor.apply();
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

            String payloadB64 = parts[1];
            Log.d("SessionManager", "Payload (base64) length: " + payloadB64.length());

            byte[] decoded = Base64.decode(payloadB64, Base64.DEFAULT);
            String decodedStr = new String(decoded, "UTF-8");

            Log.d("SessionManager", "========== JWT PAYLOAD ==========");
            Log.d("SessionManager", decodedStr);
            Log.d("SessionManager", "==================================");

            // Prefer robust JSON parsing instead of manual substring math.
            JSONObject obj = new JSONObject(decodedStr);

            String userId = null;
            if (obj.has("nameid")) {
                userId = obj.optString("nameid", null);
            }
            if ((userId == null || userId.isEmpty()) && obj.has("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier")) {
                userId = obj.optString("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/nameidentifier", null);
            }

            if (userId == null || userId.isEmpty()) {
                Log.e("SessionManager", "❌ nameid claim NOT FOUND in JWT payload");
                Log.e("SessionManager", "This means C# backend didn't include ClaimTypes.NameIdentifier in token");
                return null;
            }

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
