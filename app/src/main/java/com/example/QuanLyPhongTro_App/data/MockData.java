package com.example.QuanLyPhongTro_App.data;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.Booking;
import com.example.QuanLyPhongTro_App.ui.tenant.Notification;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Class chứa tất cả dữ liệu tĩnh cho ứng dụng (Tenant + Landlord)
 * Đã cập nhật: Thêm dữ liệu chi tiết cho chủ trọ và người thuê, sử dụng hình ảnh mới.
 */
public class MockData {

    // ==================== 1. TENANT DATA (Dữ liệu cho người thuê) ====================

    /**
     * Lấy danh sách tất cả phòng trọ (Hiển thị ở trang chủ Tenant)
     */
    public static List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();

        // Phòng 1
        Room room1 = new Room(
                1,
                "Phòng trọ cao cấp gần ĐH Bách Khoa",
                3500000, // 3.5 triệu
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

        // Phòng 2
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

        // Phòng 3
        Room room3 = new Room(
                3,
                "Phòng trọ giá rẻ cho sinh viên",
                1800000,
                "Quận Thanh Khê",
                "K123 Điện Biên Phủ, P. Chính Gián, Q. Thanh Khê, Đà Nẵng",
                18.0,
                "Phòng trọ thoáng mát, yên tĩnh, giá rẻ điện nước giá dân. Ưu tiên sinh viên nữ.",
                null,
                4.0, 5,
                103, "Lê Văn C", 4.2, 15, "0905111222",
                false, false, "2024-05-10"
        );
        room3.setImageResId(R.drawable.room_3);
        rooms.add(room3);

        // Phòng 4
        Room room4 = new Room(
                4,
                "Studio hiện đại full nội thất",
                4500000,
                "Quận Sơn Trà",
                "234 Võ Nguyên Giáp, P. Phước Mỹ, Q. Sơn Trà, Đà Nẵng",
                30.0,
                "Studio hiện đại, máy lạnh, nóng lạnh, tủ lạnh, máy giặt đầy đủ. View biển tuyệt đẹp.",
                null,
                4.8, 15,
                102, "Trần Thị B", 4.9, 120, "0905987654",
                true, false, "2024-05-25"
        );
        room4.setImageResId(R.drawable.room_4);
        rooms.add(room4);

        // Phòng 5
        Room room5 = new Room(
                5,
                "Phòng có gác lửng tiện nghi",
                2800000,
                "Quận Thanh Khê",
                "567 Điện Biên Phủ, P. Thanh Khê Tây, Q. Thanh Khê, Đà Nẵng",
                22.0,
                "Phòng có gác lửng, thoáng mát, gần Sân bay Quốc tế Đà Nẵng.",
                null,
                4.5, 10,
                104, "Phạm Thị D", 4.6, 30, "0905333444",
                false, false, "2024-05-18"
        );
        room5.setImageResId(R.drawable.room_5);
        rooms.add(room5);

        // Phòng 6
        Room room6 = new Room(
                6,
                "Chung cư mini cao cấp",
                6000000,
                "Quận Hải Châu",
                "890 Nguyễn Văn Linh, P. Nam Dương, Q. Hải Châu, Đà Nẵng",
                45.0,
                "Chung cư mini cao cấp, bảo vệ 24/7, thang máy, chỗ đậu xe. Gần cầu Rồng.",
                null,
                5.0, 25,
                105, "Hoàng Văn E", 5.0, 40, "0905555666",
                true, true, "2024-05-28"
        );
        room6.setImageResId(R.drawable.room_6);
        rooms.add(room6);

        // Phòng 7
        Room room7 = new Room(
                7,
                "Phòng trọ gần Chợ Hàn",
                3200000,
                "Quận Hải Châu",
                "345 Hùng Vương, P. Vĩnh Trung, Q. Hải Châu, Đà Nẵng",
                20.0,
                "Vị trí đắc địa, gần Chợ Hàn, trung tâm thành phố.",
                null,
                4.6, 18,
                104, "Phạm Thị D", 4.6, 30, "0905333444",
                false, false, "2024-05-22"
        );
        room7.setImageResId(R.drawable.room_7);
        rooms.add(room7);

        // Phòng 8
        Room room8 = new Room(
                8,
                "Nhà nguyên căn 3 tầng",
                15000000,
                "Quận Cẩm Lệ",
                "678 Nguyễn Hữu Thọ, P. Khuê Trung, Q. Cẩm Lệ, Đà Nẵng",
                120.0,
                "Nhà nguyên căn 3 tầng, 4 phòng ngủ, sân thượng, garage ô tô. Gần Khu công nghệ cao.",
                null,
                4.9, 30,
                106, "Vũ Thị F", 4.8, 10, "0905777888",
                false, true, "2024-05-12"
        );
        room8.setImageResId(R.drawable.room_8);
        rooms.add(room8);

        return rooms;
    }

    public static List<Room> getSavedRooms() {
        List<Room> allRooms = getRooms();
        List<Room> savedRooms = new ArrayList<>();
        // Giả lập lấy vài phòng đã lưu
        if (allRooms.size() > 0) savedRooms.add(allRooms.get(0));
        if (allRooms.size() > 3) savedRooms.add(allRooms.get(3));
        return savedRooms;
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

            public ListingItem(String title, String price, String status, boolean isActive) {
                this.title = title;
                this.price = price;
                this.status = status;
                this.isActive = isActive;
            }
        }

        /**
         * Lấy danh sách tin đăng cho trang quản lý của chủ trọ
         */
        public static List<ListingItem> getListings() {
            List<ListingItem> listings = new ArrayList<>();

            listings.add(new ListingItem("Phòng trọ cao cấp gần ĐH Bách Khoa", "3.500.000 đ", "Còn trống", true));
            listings.add(new ListingItem("Căn hộ mini view biển Mỹ Khê", "5.500.000 đ", "Còn trống", true));
            listings.add(new ListingItem("Phòng trọ giá rẻ cho sinh viên", "1.800.000 đ", "Đã thuê", false));
            listings.add(new ListingItem("Studio hiện đại full nội thất", "4.500.000 đ", "Còn trống", true));
            listings.add(new ListingItem("Phòng có gác lửng tiện nghi", "2.800.000 đ", "Chờ xử lý", true));
            listings.add(new ListingItem("Chung cư mini cao cấp", "6.000.000 đ", "Còn trống", true));

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

    public static List<UtilityItem> getTenantUtilities() {
        List<UtilityItem> utilities = new ArrayList<>();
        // Trả về danh sách tiện ích cho người thuê
        utilities.add(new UtilityItem(R.drawable.ic_search, "Tìm phòng trọ", "Tìm kiếm phòng trọ phù hợp", com.example.QuanLyPhongTro_App.ui.tenant.MainActivity.class));
        return utilities;
    }
}
