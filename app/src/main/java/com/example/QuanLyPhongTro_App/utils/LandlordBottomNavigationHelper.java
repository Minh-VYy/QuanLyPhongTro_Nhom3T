package com.example.QuanLyPhongTro_App.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity;
import com.example.QuanLyPhongTro_App.ui.landlord.LandlordProfileActivity;
import com.example.QuanLyPhongTro_App.ui.landlord.LandlordStatsActivity;
import com.example.QuanLyPhongTro_App.ui.landlord.YeuCau;

/**
 * Helper class to manage bottom navigation for Landlord activities
 */
public class LandlordBottomNavigationHelper {

    private static final String TAG = "LandlordBottomNav";
    private static final int COLOR_ACTIVE = 0xFF4a90e2; // #4a90e2
    private static final int COLOR_INACTIVE = 0xFF666666; // #666

    /**
     * Setup bottom navigation for a landlord activity
     * @param activity The current activity
     * @param activeItem Which item should be highlighted (home, requests, stats, profile)
     */
    public static void setupBottomNavigation(Activity activity, String activeItem) {
        try {
            View bottomNav = activity.findViewById(R.id.bottomNav);

            if (bottomNav == null) {
                Log.e(TAG, "Bottom navigation view not found in layout!");
                return;
            }

            LinearLayout navHome = bottomNav.findViewById(R.id.navHome);
            LinearLayout navRequests = bottomNav.findViewById(R.id.navRequests);
            LinearLayout navStats = bottomNav.findViewById(R.id.navStats);
            LinearLayout navProfile = bottomNav.findViewById(R.id.navProfile);

            ImageView iconHome = bottomNav.findViewById(R.id.navHomeIcon);
            ImageView iconRequests = bottomNav.findViewById(R.id.navRequestsIcon);
            ImageView iconStats = bottomNav.findViewById(R.id.navStatsIcon);
            ImageView iconProfile = bottomNav.findViewById(R.id.navProfileIcon);

            TextView textHome = bottomNav.findViewById(R.id.navHomeText);
            TextView textRequests = bottomNav.findViewById(R.id.navRequestsText);
            TextView textStats = bottomNav.findViewById(R.id.navStatsText);
            TextView textProfile = bottomNav.findViewById(R.id.navProfileText);

            // Null checks
            if (navHome == null || navRequests == null || navStats == null || navProfile == null) {
                Log.e(TAG, "One or more navigation items not found!");
                return;
            }

            if (iconHome == null || iconRequests == null || iconStats == null || iconProfile == null) {
                Log.e(TAG, "One or more navigation icons not found!");
                return;
            }

            if (textHome == null || textRequests == null || textStats == null || textProfile == null) {
                Log.e(TAG, "One or more navigation texts not found!");
                return;
            }

            // Reset all to inactive state
            setNavItemState(iconHome, textHome, false);
            setNavItemState(iconRequests, textRequests, false);
            setNavItemState(iconStats, textStats, false);
            setNavItemState(iconProfile, textProfile, false);

            // Set active item
            switch (activeItem.toLowerCase()) {
                case "home":
                    setNavItemState(iconHome, textHome, true);
                    break;
                case "requests":
                    setNavItemState(iconRequests, textRequests, true);
                    break;
                case "stats":
                    setNavItemState(iconStats, textStats, true);
                    break;
                case "profile":
                    setNavItemState(iconProfile, textProfile, true);
                    break;
            }

            // Setup click listeners
            navHome.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("home")) {
                    Intent intent = new Intent(activity, LandlordHomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            navRequests.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("requests")) {
                    Intent intent = new Intent(activity, YeuCau.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            navStats.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("stats")) {
                    Intent intent = new Intent(activity, LandlordStatsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            navProfile.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("profile")) {
                    Intent intent = new Intent(activity, LandlordProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            Log.d(TAG, "Bottom navigation setup completed for: " + activeItem);
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation", e);
        }
    }

    /**
     * Set the visual state of a navigation item
     */
    private static void setNavItemState(ImageView icon, TextView text, boolean isActive) {
        int color = isActive ? COLOR_ACTIVE : COLOR_INACTIVE;
        icon.setColorFilter(color);
        text.setTextColor(color);
        if (isActive) {
            text.setTypeface(null, android.graphics.Typeface.BOLD);
        } else {
            text.setTypeface(null, android.graphics.Typeface.NORMAL);
        }
    }
}

