package com.example.QuanLyPhongTro_App.utils;

import android.util.Base64;
import org.json.JSONObject;

/**
 * JWT Token Parser - để lấy claims từ token
 */
public class JwtTokenParser {

    /**
     * Decode JWT token và lấy claims (payload)
     */
    public static JSONObject decodeToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }

            // JWT format: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return null;
            }

            // Decode payload (base64)
            String payload = parts[1];
            // Add padding if needed
            int padNeeded = 4 - (payload.length() % 4);
            if (padNeeded != 4) {
                payload += "==".substring(0, padNeeded);
            }

            byte[] decodedBytes = Base64.decode(payload, Base64.URL_SAFE);
            String decodedString = new String(decodedBytes, "UTF-8");

            return new JSONObject(decodedString);
        } catch (Exception e) {
            android.util.Log.e("JwtTokenParser", "Error decoding token: " + e.getMessage());
            return null;
        }
    }

    /**
     * Get role from token
     * Claims thường có: NameIdentifier (user id), VaiTroId (role id)
     */
    public static String getRoleFromToken(String token) {
        try {
            JSONObject claims = decodeToken(token);
            if (claims == null) {
                return "tenant";
            }

            // Try different claim names
            int roleId = 0;

            if (claims.has("VaiTroId")) {
                roleId = claims.getInt("VaiTroId");
            } else if (claims.has("vaiTroId")) {
                roleId = claims.getInt("vaiTroId");
            } else if (claims.has("role")) {
                return claims.getString("role").toLowerCase();
            }

            // Convert role ID to string
            switch (roleId) {
                case 1:
                    return "admin";
                case 2:
                    return "landlord";
                case 3:
                    return "tenant";
                default:
                    return "tenant";
            }
        } catch (Exception e) {
            android.util.Log.e("JwtTokenParser", "Error getting role: " + e.getMessage());
            return "tenant";
        }
    }

    /**
     * Get user ID from token
     */
    public static String getUserIdFromToken(String token) {
        try {
            JSONObject claims = decodeToken(token);
            if (claims == null) {
                return null;
            }

            // Try different claim names for user ID
            if (claims.has("NameIdentifier")) {
                return claims.getString("NameIdentifier");
            } else if (claims.has("sub")) {
                return claims.getString("sub");
            } else if (claims.has("id")) {
                return claims.getString("id");
            }

            return null;
        } catch (Exception e) {
            android.util.Log.e("JwtTokenParser", "Error getting user ID: " + e.getMessage());
            return null;
        }
    }
}

