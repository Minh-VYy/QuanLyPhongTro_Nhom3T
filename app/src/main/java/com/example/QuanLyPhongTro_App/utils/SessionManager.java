// app/src/main/java/com/example/QuanLyPhongTro_App/utils/SessionManager.java
package com.example.QuanLyPhongTro_App.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_IS_LANDLORD = "isLandlord";
    private static final String KEY_DISPLAY_ROLE = "displayRole";

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

    public String getUserType() {
        return pref.getString(KEY_USER_TYPE, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
