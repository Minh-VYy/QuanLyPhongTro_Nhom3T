package com.example.QuanLyPhongTro_App.data.repository;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.QuanLyPhongTro_App.ui.tenant.Booking;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Local cache for tenant bookings.
 *
 * Purpose: provide a fallback list when backend endpoint /api/DatPhong/my-bookings is temporarily broken (HTTP 500).
 *
 * It stores the most recent bookings created from the device so the user can still see "Đã đặt" items.
 */
public class BookingCache {
    private static final String PREFS = "booking_cache";
    private static final String KEY_LIST = "tenant_bookings";
    private static final int MAX_ITEMS = 50;

    private final SharedPreferences prefs;
    private final Gson gson = new Gson();

    public BookingCache(Context context) {
        this.prefs = context.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public synchronized void addOrUpdate(Booking booking) {
        if (booking == null || booking.getId() == null || booking.getId().trim().isEmpty()) return;

        List<Booking> list = getAll();
        // remove same id
        for (int i = 0; i < list.size(); i++) {
            Booking b = list.get(i);
            if (b != null && booking.getId().equals(b.getId())) {
                list.remove(i);
                break;
            }
        }
        // add first
        list.add(0, booking);

        // trim
        if (list.size() > MAX_ITEMS) {
            list = new ArrayList<>(list.subList(0, MAX_ITEMS));
        }

        saveAll(list);
    }

    public synchronized List<Booking> getAll() {
        String json = prefs.getString(KEY_LIST, null);
        if (json == null || json.trim().isEmpty()) return new ArrayList<>();

        try {
            Type type = new TypeToken<List<Booking>>() {}.getType();
            List<Booking> list = gson.fromJson(json, type);
            return list != null ? new ArrayList<>(list) : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public synchronized void clear() {
        prefs.edit().remove(KEY_LIST).apply();
    }

    private void saveAll(List<Booking> list) {
        String json = gson.toJson(list != null ? list : Collections.emptyList());
        prefs.edit().putString(KEY_LIST, json).apply();
    }

    /**
     * Return cached list filtered by type (upcoming/past).
     */
    public synchronized List<Booking> getFiltered(@Nullable String bookingType) {
        List<Booking> all = getAll();
        if (bookingType == null) return all;

        List<Booking> out = new ArrayList<>();
        for (Booking b : all) {
            if (b == null) continue;
            String s = b.getStatus();
            if (s == null) s = "pending";

            switch (bookingType) {
                case "upcoming":
                    if ("pending".equals(s) || "confirmed".equals(s)) out.add(b);
                    break;
                case "past":
                    if ("completed".equals(s) || "cancelled".equals(s)) out.add(b);
                    break;
                case "completed":
                    if ("completed".equals(s)) out.add(b);
                    break;
                case "cancelled":
                    if ("cancelled".equals(s)) out.add(b);
                    break;
                default:
                    out.add(b);
            }
        }
        return out;
    }
}

