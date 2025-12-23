package com.example.QuanLyPhongTro_App.data;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.landlord.AllListingsActivity; // Thêm import
import com.example.QuanLyPhongTro_App.ui.landlord.EditTin;
import com.example.QuanLyPhongTro_App.ui.tenant.Booking;
import com.example.QuanLyPhongTro_App.ui.tenant.Notification;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    public static List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();
        Room room1 = new Room(
                1,
                "Phòng trọ cao cấp gần ĐH Bách Khoa",
                3500000,
                "Quận Liên Chiểu",
                "123 Nguyễn Sinh Sắc, P. Hòa Minh, Q. Liên Chiểu, Đà Nẵng",
                25.0,
                "Phòng mới xây, đầy đủ nội thất (giường, tủ, điều hòa). An ninh tốt, có camera 24/7. Gần chợ và trường đại học.",
                null,
                4.5, 12,
                101, "Nguyễn Văn Chủ", 4.8, 56, "0905123456",
                true, false, "2024-05-20"
        );
        room1.setImageResId(R.drawable.room_1);
        rooms.add(room1);

        Room room2 = new Room(
                2,
                "Căn hộ mini view biển Mỹ Khê",
                5500000,
                "Quận Sơn Trà",
                "45 Võ Nguyên Giáp, P. Mân Thái, Q. Sơn Trà, Đà Nẵng",
                40.0,
                "Căn hộ 1 phòng ngủ, view biển tuyệt đẹp. Full nội thất cao cấp. Thích hợp cho người nước ngoài hoặc vợ chồng trẻ.",
                null,
                4.9, 28,
                102, "Trần Thị B", 4.9, 120, "0905987654",
                false, true, "2024-05-15"
        );
        room2.setImageResId(R.drawable.room_2);
        rooms.add(room2);

        return rooms;
    }

    public static List<Room> getSavedRooms() {
        List<Room> allRooms = getRooms();
        List<Room> savedRooms = new ArrayList<>();
        if (allRooms.size() > 0) savedRooms.add(allRooms.get(0));
        if (allRooms.size() > 1) savedRooms.add(allRooms.get(1));
        return savedRooms;
    }

    public static List<Booking> getPendingBookings() {
        List<Booking> bookings = new ArrayList<>();
        bookings.add(new Booking("1", "Phòng trọ đẹp, gần ĐH Bách Khoa", "2.5 triệu/tháng", "15/12/2024", "Sáng (8-12h)", "pending", "Nguyễn Văn A", "Quận 10, TP.HCM"));
        return bookings;
    }

    public static List<Booking> getConfirmedBookings() { return new ArrayList<>(); }
    public static List<Booking> getCompletedBookings() { return new ArrayList<>(); }
    public static List<Booking> getCancelledBookings() { return new ArrayList<>(); }

    public static List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("Nguyễn Văn A", "Chào bạn, tôi có thể xem phòng...", "3 giờ trước", "message", true));
        notifications.add(new Notification("Lịch hẹn được xác nhận", "Chủ trọ đã xác nhận lịch xem phòng của bạn.", "2 giờ trước", "calendar", false));
        notifications.add(new Notification("Tin đăng mới phù hợp", "Có 3 tin đăng mới phù hợp với tìm kiếm của bạn.", "5 giờ trước", "home", false));
        return notifications;
    }

    public static class LandlordData {

        public static class ListingItem {
            public String title;
            public String price;
            public String status;
            public boolean isActive;

            public ListingItem(String title, String price, String status, boolean isActive) {
                this.title = title;
                this.price = price;
                this.status = status;
                this.isActive = isActive;
            }
        }

        public static List<ListingItem> getListings() {
            List<ListingItem> listings = new ArrayList<>();
            listings.add(new ListingItem("Phòng trọ cao cấp gần ĐH Bách Khoa", "3.500.000 đ", "Còn trống", true));
            listings.add(new ListingItem("Căn hộ mini view biển Mỹ Khê", "5.500.000 đ", "Còn trống", true));
            listings.add(new ListingItem("Phòng trọ giá rẻ cho sinh viên", "1.800.000 đ", "Đã thuê", false));
            return listings;
        }
    }

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
        utilities.add(new UtilityItem(R.drawable.ic_add, "Thêm tin trọ", "Đăng tin cho thuê phòng trọ mới", EditTin.class));
        utilities.add(new UtilityItem(R.drawable.ic_list, "Danh sách tin đăng", "Xem và quản lý tin đăng", AllListingsActivity.class));
        return utilities;
    }
}
