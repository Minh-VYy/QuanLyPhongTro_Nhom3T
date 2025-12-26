package com.example.QuanLyPhongTro_App.data;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.Booking;
import com.example.QuanLyPhongTro_App.ui.tenant.Notification;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class chứa tất cả dữ liệu tĩnh cho ứng dụng (Tenant + Landlord)
 * Đã cập nhật: Thêm dữ liệu chi tiết cho chủ trọ và người thuê, sử dụng hình ảnh mới.
 */
public class MockData {

    // ==================== 1. TENANT DATA (Dữ liệu cho người thuê) ====================
    
    /**
     * ĐÃ LOẠI BỎ: Dữ liệu phòng giả lập
     * Hiện tại app chỉ sử dụng dữ liệu từ SQL Server database thông qua PhongDao
     * Nếu cần test, hãy thêm dữ liệu vào database bằng file SQL/QuanLyPhongTro.sql
     */
    
    @Deprecated
    public static List<Room> getRooms() {
        // Trả về danh sách rỗng - không còn dùng mock data
        return new ArrayList<>();
    }

    @Deprecated
    public static List<Room> getSavedRooms() {
        // Trả về danh sách rỗng - không còn dùng mock data
        return new ArrayList<>();
    }

    // ==================== BOOKINGS & NOTIFICATIONS (Giữ nguyên) ====================

    public static List<Booking> getPendingBookings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("1", "Phòng trọ đẹp, gần ĐH Bách Khoa", "2.5 triệu/tháng", "15/12/2024", "Sáng (8-12h)", "pending", "Nguyễn Văn A", "Quận 10, TP.HCM"));
        return bookings;
    }

    public static List<Booking> getConfirmedBookings() {
        return new ArrayList<>();
    }

    public static List<Booking> getCompletedBookings() {
        return new ArrayList<>();
    }

    public static List<Booking> getCancelledBookings() {
        return new ArrayList<>();
    }

    public static List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("Lịch hẹn được xác nhận", "Chủ trọ đã xác nhận lịch xem phòng của bạn.", "2 giờ trước", "calendar", false));
        notifications.add(new Notification("Tin đăng mới phù hợp", "Có 3 tin đăng mới phù hợp với tìm kiếm của bạn.", "5 giờ trước", "home", false));
        return notifications;
    }

    // ==================== 2. LANDLORD DATA (Dữ liệu cho Chủ trọ) ====================

    /**
     * Cấu trúc dữ liệu cũ để tương thích với AllListingsAdapter
     */
    public static class LandlordData {

        public static class ListingItem {
            public String title;
            public String price;
            public String status; // "Còn trống", "Đã thuê", "Chờ xử lý"
            public boolean isActive;
            public String imageName; // THÊM "public" HOẶC TẠO GETTER

            public ListingItem(String title, String price, String status, boolean isActive, String imageName) {
                this.title = title;
                this.price = price;
                this.status = status;
                this.isActive = isActive;
                this.imageName = imageName; // PHẢI CÓ DÒNG NÀY
            }
        }

        /**
         * Lấy danh sách tin đăng cho trang quản lý của chủ trọ
         */
        public static List<ListingItem> getListings() {
            List<ListingItem> listings = new ArrayList<>();

            // DÙNG TÊN FILE TRONG DRAWABLE (KHÔNG CẦN ĐUÔI .png/.jpg)
            listings.add(new ListingItem("Phòng trọ cao cấp gần ĐH Bách Khoa",
                    "3.600.000 đ", "Còn trống", true,
                    "room_1")); // <-- TÊN FILE ẢNH

            listings.add(new ListingItem("Căn hộ mini view biển Mỹ Khê",
                    "5.500.000 đ", "Còn trống", true,
                    "room_2")); // <-- TÊN FILE ẢNH KHÁC

            listings.add(new ListingItem("Phòng trọ giá rẻ cho sinh viên",
                    "1.800.000 đ", "Đã thuê", false,
                    "room_3")); // <-- TÊN FILE ẢNH KHÁC

            listings.add(new ListingItem("Studio hiện đại full nội thất",
                    "4.500.000 đ", "Còn trống", true,
                    "room_4")); // <-- TÊN FILE ẢNH KHÁC

            listings.add(new ListingItem("Phòng có gác lửng tiện nghi",
                    "2.800.000 đ", "Chờ xử lý", true,
                    "room_5")); // <-- TÊN FILE ẢNH KHÁC

            listings.add(new ListingItem("Chung cư mini cao cấp",
                    "6.000.000 đ", "Còn trống", true,
                    "room_6")); // <-- TÊN FILE ẢNH KHÁC

            return listings;
        }
    }

    // ==================== 3. UTILITIES (Tiện ích) ====================

    public static class UtilityItem {
        private int icon;
        private String title;
        private String description;
        private Class<?> targetActivity;

        public UtilityItem(int icon, String title, String description, Class<?> targetActivity) {
            this.icon = icon;
            this.title = title;
            this.description = description;
            this.targetActivity = targetActivity;
        }

        public int getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Class<?> getTargetActivity() { return targetActivity; }
    }

    public static List<UtilityItem> getLandlordUtilities() {
        List<UtilityItem> utilities = new ArrayList<>();
        // Trả về danh sách tiện ích cho chủ trọ (như code cũ)
        utilities.add(new UtilityItem(R.drawable.ic_add, "Thêm tin trọ", "Đăng tin cho thuê phòng trọ mới", com.example.QuanLyPhongTro_App.ui.landlord.EditTin.class)); // Placeholder class
        utilities.add(new UtilityItem(R.drawable.ic_list, "Danh sách tin đăng", "Xem và quản lý tin đăng", com.example.QuanLyPhongTro_App.ui.landlord.AllListingsActivity.class)); // Placeholder
        return utilities;
    }

    public static List<UtilityItem> getTenantUtilities() {
        List<UtilityItem> utilities = new ArrayList<>();
        // Trả về danh sách tiện ích cho người thuê
        utilities.add(new UtilityItem(R.drawable.ic_search, "Tìm phòng trọ", "Tìm kiếm phòng trọ phù hợp", com.example.QuanLyPhongTro_App.ui.tenant.MainActivity.class));
        utilities.add(new UtilityItem(
                R.drawable.ic_add,
                "Thêm tin trọ",
                "Đăng tin cho thuê phòng trọ mới",
                com.example.QuanLyPhongTro_App.ui.landlord.EditTin.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_list,
                "Danh sách tin đăng",
                "Xem và quản lý tất cả tin đăng",
                com.example.QuanLyPhongTro_App.ui.landlord.AllListingsActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_edit,
                "Chỉnh sửa trọ",
                "Quản lý và chỉnh sửa thông tin phòng trọ",
                com.example.QuanLyPhongTro_App.ui.landlord.EditTin.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_analytics,
                "Thống kê",
                "Xem báo cáo và thống kê tin đăng",
                com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class
        ));
        return utilities;
    }
}
