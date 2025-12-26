package com.example.QuanLyPhongTro_App.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Utility to format dates for ASP.NET Core APIs. */
public final class IsoDateTime {
    private IsoDateTime() {}

    /**
     * Format as ISO-8601 without timezone offset ("yyyy-MM-dd'T'HH:mm:ss").
     * Matches ApiClient Gson date format.
     */
    public static String formatLocal(Date date) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        return sdf.format(date);
    }

    /**
     * Format as ISO-8601 in UTC ("yyyy-MM-dd'T'HH:mm:ss'Z'").
     * Use this if backend expects UTC times.
     */
    public static String formatUtc(Date date) {
        if (date == null) return null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(date);
    }
}

