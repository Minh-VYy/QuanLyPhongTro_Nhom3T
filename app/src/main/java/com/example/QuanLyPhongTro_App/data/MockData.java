package com.example.QuanLyPhongTro_App.data;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.Booking;
import com.example.QuanLyPhongTro_App.ui.tenant.Notification;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Class chứa tất cả dữ liệu tĩnh để test UI
 * Sử dụng trong lúc chưa có backend
 */
public class MockData {

    // ==================== ROOMS DATA ====================

    public static List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();

        rooms.add(new Room(
            "Phòng trọ đẹp, gần ĐH Bách Khoa",
            "2.5 triệu/tháng",
            "Quận 10, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Chung cư mini full nội thất",
            "3.2 triệu/tháng",
            "Quận 1, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Phòng trọ mới xây",
            "1.8 triệu/tháng",
            "Quận Tân Bình, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Studio cao cấp view đẹp",
            "4.5 triệu/tháng",
            "Quận 3, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Phòng trọ giá rẻ cho sinh viên",
            "1.5 triệu/tháng",
            "Quận Bình Thạnh, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Nhà nguyên căn 3 phòng ngủ",
            "5.0 triệu/tháng",
            "Quận 7, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Căn hộ dịch vụ cao cấp",
            "6.0 triệu/tháng",
            "Quận 2, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Phòng có gác lửng",
            "2.2 triệu/tháng",
            "Quận Gò Vấp, TP.HCM",
            R.drawable.tro
        ));

        return rooms;
    }

    public static List<Room> getSavedRooms() {
        List<Room> rooms = new ArrayList<>();

        rooms.add(new Room(
            "Phòng trọ đẹp, gần ĐH Bách Khoa",
            "2.5 triệu/tháng",
            "Quận 10, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Studio cao cấp view đẹp",
            "4.5 triệu/tháng",
            "Quận 3, TP.HCM",
            R.drawable.tro
        ));

        rooms.add(new Room(
            "Căn hộ dịch vụ cao cấp",
            "6.0 triệu/tháng",
            "Quận 2, TP.HCM",
            R.drawable.tro
        ));

        return rooms;
    }

    // ==================== BOOKINGS DATA ====================

    public static List<Booking> getPendingBookings() {
        List<Booking> bookings = new ArrayList<>();

        bookings.add(new Booking(
            "1",
            "Phòng trọ đẹp, gần ĐH Bách Khoa",
            "2.5 triệu/tháng",
            "15/12/2024",
            "Sáng (8-12h)",
            "pending",
            "Nguyễn Văn A",
            "Quận 10, TP.HCM"
        ));

        bookings.add(new Booking(
            "2",
            "Studio cao cấp view đẹp",
            "4.5 triệu/tháng",
            "18/12/2024",
            "Chiều (13-17h)",
            "pending",
            "Trần Thị B",
            "Quận 3, TP.HCM"
        ));

        return bookings;
    }

    public static List<Booking> getConfirmedBookings() {
        List<Booking> bookings = new ArrayList<>();

        bookings.add(new Booking(
            "3",
            "Chung cư mini full nội thất",
            "3.2 triệu/tháng",
            "20/12/2024",
            "Sáng (8-12h)",
            "confirmed",
            "Lê Văn C",
            "Quận 1, TP.HCM"
        ));

        bookings.add(new Booking(
            "4",
            "Căn hộ dịch vụ cao cấp",
            "6.0 triệu/tháng",
            "22/12/2024",
            "Tối (18-20h)",
            "confirmed",
            "Phạm Thị D",
            "Quận 2, TP.HCM"
        ));

        return bookings;
    }

    public static List<Booking> getCompletedBookings() {
        List<Booking> bookings = new ArrayList<>();

        bookings.add(new Booking(
            "5",
            "Phòng trọ mới xây",
            "1.8 triệu/tháng",
            "10/11/2024",
            "Sáng (8-12h)",
            "completed",
            "Hoàng Văn E",
            "Quận Tân Bình, TP.HCM"
        ));

        return bookings;
    }

    public static List<Booking> getCancelledBookings() {
        List<Booking> bookings = new ArrayList<>();

        bookings.add(new Booking(
            "6",
            "Phòng trọ giá rẻ cho sinh viên",
            "1.5 triệu/tháng",
            "05/11/2024",
            "Chiều (13-17h)",
            "cancelled",
            "Vũ Thị F",
            "Quận Bình Thạnh, TP.HCM"
        ));

        return bookings;
    }

    // ==================== NOTIFICATIONS DATA ====================

    public static List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();

        notifications.add(new Notification(
            "Lịch hẹn được xác nhận",
            "Chủ trọ đã xác nhận lịch xem phòng của bạn vào 20/12/2024 lúc 9:00",
            "2 giờ trước",
            "calendar",
            false
        ));

        notifications.add(new Notification(
            "Tin đăng mới phù hợp",
            "Có 3 tin đăng mới ở Quận 10 phù hợp với tìm kiếm của bạn",
            "5 giờ trước",
            "home",
            false
        ));

        notifications.add(new Notification(
            "Nhắc nhở lịch hẹn",
            "Bạn có lịch xem phòng vào ngày mai lúc 14:00",
            "1 ngày trước",
            "calendar",
            true
        ));

        notifications.add(new Notification(
            "Giá phòng thay đổi",
            "Phòng trọ bạn đã lưu có thay đổi giá từ 2.5tr → 2.3tr/tháng",
            "2 ngày trước",
            "home",
            true
        ));

        notifications.add(new Notification(
            "Chủ trọ gửi tin nhắn",
            "Bạn có tin nhắn mới từ chủ trọ về lịch xem phòng",
            "3 ngày trước",
            "message",
            true
        ));

        return notifications;
    }

    // ==================== LANDLORD DATA ====================

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

            listings.add(new ListingItem(
                "Phòng trọ gần ĐH Bách Khoa",
                "2.500.000 đ",
                "Còn trống",
                true
            ));

            listings.add(new ListingItem(
                "Chung cư mini full nội thất",
                "3.200.000 đ",
                "Đã thuê",
                true
            ));

            listings.add(new ListingItem(
                "Phòng sinh viên giá rẻ",
                "1.800.000 đ",
                "Chờ xử lý",
                false
            ));

            listings.add(new ListingItem(
                "Studio cao cấp view đẹp",
                "4.500.000 đ",
                "Còn trống",
                true
            ));

            listings.add(new ListingItem(
                "Căn hộ dịch vụ",
                "6.000.000 đ",
                "Còn trống",
                true
            ));

            listings.add(new ListingItem(
                "Phòng có gác lửng",
                "2.200.000 đ",
                "Đã thuê",
                false
            ));

            return listings;
        }
    }
    // ==================== UTILITY ITEMS DATA ====================

    /**
     * Model class cho các tiện ích trong danh sách
     */
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

        // Getters
        public int getIcon() { return icon; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public Class<?> getTargetActivity() { return targetActivity; }
    }

    /**
     * Lấy danh sách các tiện ích cho chủ trọ
     */
    public static List<UtilityItem> getLandlordUtilities() {
        List<UtilityItem> utilities = new ArrayList<>();

        utilities.add(new UtilityItem(
                R.drawable.ic_add,
                "Thêm tin trọ",
                "Đăng tin cho thuê phòng trọ mới",
                com.example.QuanLyPhongTro_App.ui.landlord.EditTin.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_edit,
                "Chỉnh sửa trọ",
                "Quản lý và chỉnh sửa thông tin phòng trọ",
                com.example.QuanLyPhongTro_App.ui.landlord.EditTin.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_list,
                "Danh sách tin đăng",
                "Xem và quản lý tất cả tin đăng của bạn",
                com.example.QuanLyPhongTro_App.ui.landlord.AllListingsActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_analytics,
                "Thống kê",
                "Xem báo cáo và thống kê tin đăng",
                com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_request,
                "Yêu cầu thuê",
                "Quản lý yêu cầu thuê từ người dùng",
                com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_message,
                "Tin nhắn",
                "Quản lý tin nhắn với người thuê",
                com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_settings,
                "Cài đặt",
                "Cài đặt tài khoản và ứng dụng",
                com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class
        ));

        return utilities;
    }

    /**
     * Lấy danh sách các tiện ích cho người thuê
     */
    public static List<UtilityItem> getTenantUtilities() {
        List<UtilityItem> utilities = new ArrayList<>();

        utilities.add(new UtilityItem(
                R.drawable.ic_search,
                "Tìm phòng trọ",
                "Tìm kiếm phòng trọ phù hợp",
                com.example.QuanLyPhongTro_App.ui.tenant.MainActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_saved,
                "Phòng đã lưu",
                "Xem lại các phòng trọ đã lưu",
                com.example.QuanLyPhongTro_App.ui.tenant.MainActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_booking,
                "Lịch hẹn xem phòng",
                "Quản lý lịch hẹn xem phòng",
                com.example.QuanLyPhongTro_App.ui.tenant.MainActivity.class
        ));

        utilities.add(new UtilityItem(
                R.drawable.ic_notification,
                "Thông báo",
                "Xem thông báo và cập nhật",
                com.example.QuanLyPhongTro_App.ui.tenant.MainActivity.class
        ));

        return utilities;
    }
}

