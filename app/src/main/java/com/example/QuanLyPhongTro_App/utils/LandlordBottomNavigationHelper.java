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
        // Try to find the included layout first
        View bottomNavContainer = activity.findViewById(R.id.bottomNav);

        if (bottomNavContainer == null) {
            Log.e(TAG, "Bottom navigation container not found in layout!");
            return;
        }

        // The views are directly in the included layout
        LinearLayout navHome = bottomNavContainer.findViewById(R.id.navHome);
        LinearLayout navRequests = bottomNavContainer.findViewById(R.id.navRequests);
        LinearLayout navStats = bottomNavContainer.findViewById(R.id.navStats);
        LinearLayout navProfile = bottomNavContainer.findViewById(R.id.navProfile);

        if (navHome == null || navRequests == null || navStats == null || navProfile == null) {
            Log.e(TAG, "Navigation items not found! navHome=" + navHome + ", navRequests=" + navRequests
                + ", navStats=" + navStats + ", navProfile=" + navProfile);
            return;
        }

        ImageView iconHome = bottomNavContainer.findViewById(R.id.navHomeIcon);
        ImageView iconRequests = bottomNavContainer.findViewById(R.id.navRequestsIcon);
        ImageView iconStats = bottomNavContainer.findViewById(R.id.navStatsIcon);
        ImageView iconProfile = bottomNavContainer.findViewById(R.id.navProfileIcon);

        TextView textHome = bottomNavContainer.findViewById(R.id.navHomeText);
        TextView textRequests = bottomNavContainer.findViewById(R.id.navRequestsText);
        TextView textStats = bottomNavContainer.findViewById(R.id.navStatsText);
        TextView textProfile = bottomNavContainer.findViewById(R.id.navProfileText);

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
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        });

        navRequests.setOnClickListener(v -> {
            if (!activeItem.equalsIgnoreCase("requests")) {
                Intent intent = new Intent(activity, YeuCau.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        });

        navStats.setOnClickListener(v -> {
            if (!activeItem.equalsIgnoreCase("stats")) {
                Intent intent = new Intent(activity, LandlordStatsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        });

        navProfile.setOnClickListener(v -> {
            if (!activeItem.equalsIgnoreCase("profile")) {
                Intent intent = new Intent(activity, LandlordProfileActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(intent);
                activity.overridePendingTransition(0, 0);
                activity.finish();
            }
        });

        Log.d(TAG, "Bottom navigation setup completed for: " + activeItem);
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

