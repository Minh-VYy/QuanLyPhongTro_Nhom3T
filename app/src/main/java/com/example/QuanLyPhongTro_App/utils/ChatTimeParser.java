package com.example.QuanLyPhongTro_App.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * ChatTimeParser - parse trường ThoiGian/Timestamp từ backend về epoch millis.
 *
 * Backend có thể trả:
 * - ISO 8601: 2025-12-27T14:30:15 hoặc 2025-12-27T14:30:15Z
 * - ISO có millis: 2025-12-27T14:30:15.123Z
 * - Epoch millis dạng chuỗi: "1735290615123"
 */
public final class ChatTimeParser {
    private static final String TAG = "ChatTimeParser";

    private ChatTimeParser() {}

    public static long parseToMillis(String thoiGian) {
        if (thoiGian == null) return 0L;
        String s = thoiGian.trim();
        if (s.isEmpty()) return 0L;

        // epoch millis string
        if (s.matches("^\\d{12,}$")) {
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                Log.w(TAG, "Cannot parse epoch millis: " + s);
                return 0L;
            }
        }

        // Try common ISO formats
        String[] patterns = new String[] {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss.SSS",
                "yyyy-MM-dd'T'HH:mm:ss"
        };

        for (String p : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(p, Locale.US);
                // If it contains Z, it's UTC.
                if (p.endsWith("'Z'")) {
                    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                }
                Date d = sdf.parse(s);
                if (d != null) return d.getTime();
            } catch (ParseException ignore) {
                // try next
            } catch (Exception e) {
                Log.w(TAG, "Unexpected parse error for pattern=" + p + " value=" + s + " err=" + e.getMessage());
            }
        }

        Log.w(TAG, "Unrecognized ThoiGian format: " + s);
        return 0L;
    }
}

