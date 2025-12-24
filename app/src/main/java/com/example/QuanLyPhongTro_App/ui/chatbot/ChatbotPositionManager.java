package com.example.QuanLyPhongTro_App.ui.chatbot;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Quản lý vị trí của chatbot button
 */
public class ChatbotPositionManager {
    private static final String PREF_NAME = "ChatbotPosition";
    private static final String KEY_X = "chatbot_x";
    private static final String KEY_Y = "chatbot_y";
    
    private SharedPreferences prefs;
    
    public ChatbotPositionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
    
    /**
     * Lưu vị trí chatbot
     */
    public void savePosition(float x, float y) {
        prefs.edit()
            .putFloat(KEY_X, x)
            .putFloat(KEY_Y, y)
            .apply();
    }
    
    /**
     * Lấy vị trí X đã lưu
     */
    public float getSavedX(float defaultValue) {
        return prefs.getFloat(KEY_X, defaultValue);
    }
    
    /**
     * Lấy vị trí Y đã lưu
     */
    public float getSavedY(float defaultValue) {
        return prefs.getFloat(KEY_Y, defaultValue);
    }
    
    /**
     * Kiểm tra đã có vị trí lưu chưa
     */
    public boolean hasPosition() {
        return prefs.contains(KEY_X) && prefs.contains(KEY_Y);
    }
    
    /**
     * Xóa vị trí đã lưu
     */
    public void clearPosition() {
        prefs.edit().clear().apply();
    }
}
