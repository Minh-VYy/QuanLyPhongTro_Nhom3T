package com.example.QuanLyPhongTro_App.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.BookingListActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.MainActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.NotificationsActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.ProfileActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.SavedRoomsActivity;

/**
 * Helper class to manage bottom navigation across tenant activities
 */
public class BottomNavigationHelper {

    private static final int COLOR_ACTIVE = 0xFF4a90e2; // #4a90e2
    private static final int COLOR_INACTIVE = 0xFF666666; // #666

    /**
     * Setup bottom navigation for an activity
     * @param activity The current activity
     * @param activeItem Which item should be highlighted (home, booking, notification, profile)
     */
    public static void setupBottomNavigation(Activity activity, String activeItem) {
        try {
            View bottomNav = activity.findViewById(R.id.bottomNav);
            if (bottomNav == null) {
                android.util.Log.w("BottomNavHelper", "bottomNav not found in activity");
                return;
            }

            LinearLayout navHome = bottomNav.findViewById(R.id.navHome);
            LinearLayout navBooking = bottomNav.findViewById(R.id.navBooking);
            LinearLayout navNotification = bottomNav.findViewById(R.id.navNotification);
            LinearLayout navProfile = bottomNav.findViewById(R.id.navProfile);

            ImageView iconHome = bottomNav.findViewById(R.id.navHomeIcon);
            ImageView iconBooking = bottomNav.findViewById(R.id.navBookingIcon);
            ImageView iconNotification = bottomNav.findViewById(R.id.navNotificationIcon);
            ImageView iconProfile = bottomNav.findViewById(R.id.navProfileIcon);

            TextView textHome = bottomNav.findViewById(R.id.navHomeText);
            TextView textBooking = bottomNav.findViewById(R.id.navBookingText);
            TextView textNotification = bottomNav.findViewById(R.id.navNotificationText);
            TextView textProfile = bottomNav.findViewById(R.id.navProfileText);

            // Check if all views are found
            if (navHome == null || navBooking == null || navNotification == null || navProfile == null) {
                android.util.Log.w("BottomNavHelper", "One or more nav items not found");
                return;
            }

            if (iconHome == null || iconBooking == null || iconNotification == null || iconProfile == null) {
                android.util.Log.w("BottomNavHelper", "One or more nav icons not found");
                return;
            }

            if (textHome == null || textBooking == null || textNotification == null || textProfile == null) {
                android.util.Log.w("BottomNavHelper", "One or more nav texts not found");
                return;
            }

            // Reset all to inactive state
            setNavItemState(iconHome, textHome, false);
            setNavItemState(iconBooking, textBooking, false);
            setNavItemState(iconNotification, textNotification, false);
            setNavItemState(iconProfile, textProfile, false);

            // Set active item
            switch (activeItem.toLowerCase()) {
                case "home":
                    setNavItemState(iconHome, textHome, true);
                    break;
                case "booking":
                    setNavItemState(iconBooking, textBooking, true);
                    break;
                case "notification":
                    setNavItemState(iconNotification, textNotification, true);
                    break;
                case "profile":
                    setNavItemState(iconProfile, textProfile, true);
                    break;
            }

            // Setup click listeners
            navHome.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("home")) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            navBooking.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("booking")) {
                    Intent intent = new Intent(activity, BookingListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            navNotification.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("notification")) {
                    Intent intent = new Intent(activity, NotificationsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });

            navProfile.setOnClickListener(v -> {
                if (!activeItem.equalsIgnoreCase("profile")) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                    activity.overridePendingTransition(0, 0);
                }
            });
        } catch (Exception e) {
            android.util.Log.e("BottomNavHelper", "Error in setupBottomNavigation: " + e.getMessage(), e);
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

