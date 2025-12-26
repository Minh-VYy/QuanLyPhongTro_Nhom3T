package com.example.QuanLyPhongTro_App.utils;

/**
 * Helper to resolve TapTin id / URL to an absolute URL usable by Glide.
 */
public final class FileUrlResolver {
    private FileUrlResolver() {}

    /**
     * If server already returns a full URL -> keep it.
     * If it returns a relative path starting with "/" -> prefix BASE_URL.
     * If it returns a UUID (TapTinId) -> build /api/TapTin/{id}.
     */
    public static String resolve(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;

        if (s.startsWith("http://") || s.startsWith("https://")) {
            return s;
        }

        if (s.startsWith("/")) {
            return ApiClient.getBaseUrl() + s;
        }

        // UUID?
        if (s.matches("(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$")) {
            return ApiClient.getBaseUrl() + "/api/TapTin/" + s;
        }

        // Unknown format: treat as relative path
        return ApiClient.getBaseUrl() + "/" + s;
    }
}

