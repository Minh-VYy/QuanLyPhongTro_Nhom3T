package com.example.QuanLyPhongTro_App.data;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.Booking;
import com.example.QuanLyPhongTro_App.ui.tenant.Notification;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================================
 * MockDataKnhi.java - Lớp dữ liệu tĩnh (Mock Data) cho ứng dụng Quản Lý Phòng Trọ
 * ============================================================================
 * MỤC ĐÍCH: Cung cấp dữ liệu mẫu để test giao diện trước khi có backend
 *
 * GỒMCÁC PHẦN:
 * - Danh sách phòng trọ ở Đà Nẵng (8 phòng)
 * - Phòng yêu thích (saved rooms)
 * - Lịch hẹn (bookings)
 * - Thông báo (notifications)
 * - Dữ liệu chủ trọ (landlord data)
 * ============================================================================
 */
public class MockDataKnhi {

    /**
     * PHƯƠNG THỨC: getRooms()
     * CHỨC NĂNG: Lấy danh sách tất cả 8 phòng trọ ở Đà Nẵng để hiển thị trên trang chủ
     * RETURN: List<Room> - Danh sách phòng có đầy đủ thông tin
     */
    public static List<Room> getRooms() {
        // Tạo list để chứa các phòng
        List<Room> rooms = new ArrayList<>();

        // ============ PHÒNG 1: Cao cấp gần ĐH Đông Á ============
        rooms.add(new Room(
                1, "Phòng trọ cao cấp gần ĐH Đông Á", 2500000, "Quận Hải Châu",
                "123 Nguyễn Văn Linh, P.Thạch Thang, Q.Hải Châu, TP.Đà Nẵng", 25.0,
                "Phòng mới xây, đầy đủ nội thất: giường, tủ, bàn học, máy lạnh. Khu vực an ninh 24/7.",
                "", 4.5, 8, 101, "Chị Hoa", 4.8, 25, "0901234567",
                true, false, "2024-11-20"
        ));

        // ============ PHÒNG 2: Chung cư mini gần biển Mỹ Khê ============
        // Phòng cao cấp với view biển, có thang máy và bảo vệ 24/7
        rooms.add(new Room(
                2, "Chung cư mini view biển Mỹ Khê", 3200000, "Quận Sơn Trà",
                "456 Võ Nguyên Giáp, P.Phước Mỹ, Q.Sơn Trà, TP.Đà Nẵng", 30.0,
                "Full nội thất cao cấp, có thang máy, bảo vệ 24/7. View biển đẹp, gần bãi tắm.",
                "", 4.7, 12, 102, "Anh Tuấn", 4.6, 18, "0912345678",
                false, true, "2024-10-15"
        ));

        // ============ PHÒNG 3: Giá rẻ gần ĐH Duy Tân ============
        // Phòng phù hợp cho sinh viên với giá cạnh tranh
        rooms.add(new Room(
                3, "Phòng trọ giá rẻ cho sinh viên", 1800000, "Quận Thanh Khê",
                "789 Trần Cao Vân, P.Thanh Khê Đông, Q.Thanh Khê, TP.Đà Nẵng", 18.0,
                "Phòng sạch sẽ, gần trường học. Phù hợp sinh viên, người đi làm.",
                "", 4.2, 5, 103, "Cô Mai", 4.5, 15, "0923456789",
                true, false, "2024-11-25"
        ));

        // ============ PHÒNG 4: Studio gần Cầu Rồng ============
        // Studio hiện đại với view sông Hàn, nội thất nhập khẩu
        rooms.add(new Room(
                4, "Studio cao cấp gần Cầu Rồng", 4500000, "Quận Hải Châu",
                "321 Bạch Đằng, P.Thạch Thang, Q.Hải Châu, TP.Đà Nẵng", 35.0,
                "Studio thiết kế hiện đại, nội thất nhập khẩu. Đầy đủ tiện nghi, view sông Hàn.",
                "", 4.8, 15, 104, "Chị Lan", 4.9, 30, "0934567890",
                false, false, "2024-09-10"
        ));

        // ============ PHÒNG 5: Có gác lửng khu Liên Chiểu ============
        // Phòng có gác lửng rộng, thích hợp ở nhiều người
        rooms.add(new Room(
                5, "Phòng có gác lửng rộng rãi", 2200000, "Quận Liên Chiểu",
                "159 Tôn Thất Đạm, P.Hòa Khánh Bắc, Q.Liên Chiểu, TP.Đà Nẵng", 28.0,
                "Phòng có gác lửng, phù hợp ở nhiều người. WC riêng, máy nước nóng.",
                "", 4.3, 6, 105, "Anh Nam", 4.4, 12, "0945678901",
                false, false, "2024-08-20"
        ));

        // ============ PHÒNG 6: Dịch vụ cao cấp gần sân bay ============
        // Căn hộ 5 sao với đầy đủ tiện ích: bể bơi, gym, dọn phòng hàng ngày
        rooms.add(new Room(
                6, "Căn hộ dịch vụ 5 sao", 6000000, "Quận Hải Châu",
                "753 Điện Biên Phủ, P.Chính Gián, Q.Thanh Khê, TP.Đà Nẵng", 40.0,
                "Căn hộ dịch vụ 5 sao, có bể bơi, gym. Dọn phòng hàng ngày. Gần sân bay.",
                "", 4.9, 20, 106, "Chị Hương", 5.0, 35, "0956789012",
                true, true, "2024-11-15"
        ));

        // ============ PHÒNG 7: Gần Chợ Hàn ============
        // Phòng sạch sẽ ở vị trí thuận lợi gần chợ và trung tâm thành phố
        rooms.add(new Room(
                7, "Phòng trọ gần Chợ Hàn", 2000000, "Quận Hải Châu",
                "357 Trần Phú, P.Phước Ninh, Q.Hải Châu, TP.Đà Nẵng", 20.0,
                "Phòng sạch sẽ, có ban công. Gần chợ, siêu thị, trung tâm thành phố.",
                "", 4.1, 4, 107, "Cô Phương", 4.3, 10, "0967890123",
                false, false, "2024-10-01"
        ));

        // ============ PHÒNG 8: Nguyên căn khu Ngũ Hành Sơn ============
        // Nhà 1 trệt 1 lầu với 3 phòng ngủ, gần biển Non Nước
        rooms.add(new Room(
                8, "Nhà nguyên căn 3 phòng ngủ", 5000000, "Quận Ngũ Hành Sơn",
                "951 Nguyễn Tất Thành, P.Hòa Hải, Q.Ngũ Hành Sơn, TP.Đà Nẵng", 80.0,
                "Nhà 1 trệt 1 lầu, 3 phòng ngủ. Có sân xe, khu an ninh. Gần biển Non Nước.",
                "", 4.6, 10, 108, "Anh Khoa", 4.7, 22, "0978901234",
                false, false, "2024-07-10"
        ));

        // Trả về danh sách phòng đã tạo
        return rooms;
    }

    /**
     * PHƯƠNG THỨC: getSavedRooms()
     * CHỨC NĂNG: Lấy danh sách phòng yêu thích (saved) của người dùng
     * RETURN: List<Room> - Danh sách 3 phòng được chọn làm yêu thích
     *
     * LOGIC: Lấy tất cả phòng, sau đó chọn phòng thứ 1, 4, 6 đánh dấu là đã lưu (saved)
     */
    public static List<Room> getSavedRooms() {
        // Tạo list mới để chứa phòng yêu thích
        List<Room> saved = new ArrayList<>();
        // Lấy tất cả phòng
        List<Room> all = getRooms();

        // Kiểm tra danh sách có đủ 6 phòng không
        if (all.size() >= 6) {
            // Chọn phòng thứ 1 (index 0) - "Phòng trọ cao cấp gần ĐH Đông Á"
            Room r1 = all.get(0);
            r1.setSaved(true); // Đánh dấu là đã lưu
            saved.add(r1);

            // Chọn phòng thứ 4 (index 3) - "Studio cao cấp gần Cầu Rồng"
            Room r2 = all.get(3);
            r2.setSaved(true); // Đánh dấu là đã lưu
            saved.add(r2);

            // Chọn phòng thứ 6 (index 5) - "Căn hộ dịch vụ 5 sao"
            Room r3 = all.get(5);
            r3.setSaved(true); // Đánh dấu là đã lưu
            saved.add(r3);
        }

        // Trả về danh sách 3 phòng yêu thích
        return saved;
    }

    /**
     * PHƯƠNG THỨC: getPendingBookings()
     * CHỨC NĂNG: Lấy danh sách lịch hẹn chưa xác nhận (đợi chủ trọ xác nhận)
     * RETURN: List<Booking> - Danh sách lịch hẹn đang chờ xử lý
     */
    public static List<Booking> getPendingBookings() {
        // Tạo list lịch hẹn
        List<Booking> bookings = new ArrayList<>();

        // Lịch hẹn 1: Xem phòng 1 vào sáng 15/12/2025
        bookings.add(new Booking(
                "BK001", "Phòng trọ cao cấp gần ĐH Đông Á", "2.5 triệu/tháng",
                "15/12/2025", "Sáng (8:00 - 12:00)", "pending", "Chị Hoa", "Quận Hải Châu, TP.Đà Nẵng"
        ));

        // Lịch hẹn 2: Xem phòng 4 vào chiều 18/12/2025
        bookings.add(new Booking(
                "BK002", "Studio cao cấp gần Cầu Rồng", "4.5 triệu/tháng",
                "18/12/2025", "Chiều (13:00 - 17:00)", "pending", "Chị Lan", "Quận Hải Châu, TP.Đà Nẵng"
        ));

        // Trả về danh sách lịch hẹn chờ xác nhận
        return bookings;
    }

    /**
     * PHƯƠNG THỨC: getConfirmedBookings()
     * CHỨC NĂNG: Lấy danh sách lịch hẹn đã được xác nhận bởi chủ trọ
     * RETURN: List<Booking> - Danh sách lịch hẹn xác nhận
     */
    public static List<Booking> getConfirmedBookings() {
        List<Booking> bookings = new ArrayList<>();

        // Lịch hẹn xác nhận 1: Ngày 20/12/2025
        bookings.add(new Booking(
                "BK003", "Chung cư mini view biển Mỹ Khê", "3.2 triệu/tháng",
                "20/12/2025", "Sáng (9:00 - 11:00)", "confirmed", "Anh Tuấn", "Quận Sơn Trà, TP.Đà Nẵng"
        ));

        // Lịch hẹn xác nhận 2: Ngày 22/12/2025
        bookings.add(new Booking(
                "BK004", "Căn hộ dịch vụ 5 sao", "6.0 triệu/tháng",
                "22/12/2025", "Tối (18:00 - 20:00)", "confirmed", "Chị Hương", "Quận Hải Châu, TP.Đà Nẵng"
        ));

        return bookings;
    }

    /**
     * PHƯƠNG THỨC: getCompletedBookings()
     * CHỨC NĂNG: Lấy danh sách lịch hẹn đã hoàn thành (người dùng đã xem phòng)
     * RETURN: List<Booking> - Danh sách lịch hẹn hoàn tất
     */
    public static List<Booking> getCompletedBookings() {
        List<Booking> bookings = new ArrayList<>();

        // Lịch hẹn hoàn tất: Xem phòng 3 vào 10/11/2025
        bookings.add(new Booking(
                "BK005", "Phòng trọ giá rẻ cho sinh viên", "1.8 triệu/tháng",
                "10/11/2025", "Sáng (8:00 - 10:00)", "completed", "Cô Mai", "Quận Thanh Khê, TP.Đà Nẵng"
        ));

        return bookings;
    }

    /**
     * PHƯƠNG THỨC: getCancelledBookings()
     * CHỨC NĂNG: Lấy danh sách lịch hẹn đã bị hủy
     * RETURN: List<Booking> - Danh sách lịch hẹn bị hủy
     */
    public static List<Booking> getCancelledBookings() {
        List<Booking> bookings = new ArrayList<>();

        // Lịch hẹn bị hủy: Ngày 01/11/2025
        bookings.add(new Booking(
                "BK007", "Phòng có gác lửng rộng rãi", "2.2 triệu/tháng",
                "01/11/2025", "Chiều (15:00 - 17:00)", "cancelled", "Anh Nam", "Quận Liên Chiểu, TP.Đà Nẵng"
        ));

        return bookings;
    }

    /**
     * PHƯƠNG THỨC: getNotifications()
     * CHỨC NĂNG: Lấy danh sách thông báo cho người dùng
     * RETURN: List<Notification> - Danh sách các thông báo từ hệ thống
     */
    public static List<Notification> getNotifications() {
        List<Notification> notifications = new ArrayList<>();

        // Thông báo 1: Lịch hẹn được xác nhận bởi chủ trọ
        notifications.add(new Notification(
                "Lịch hẹn được xác nhận",
                "Chủ trọ Anh Tuấn đã xác nhận lịch xem phòng của bạn vào 20/12/2025 lúc 9:00",
                "2 giờ trước", "calendar", false // false = đã đọc
        ));

        // Thông báo 2: Có tin đăng mới phù hợp
        notifications.add(new Notification(
                "Tin đăng mới phù hợp",
                "Có 5 tin đăng mới ở Quận Hải Châu phù hợp với tiêu chí tìm kiếm của bạn",
                "5 giờ trước", "home", false
        ));

        // Thông báo 3: Nhắc nhở lịch hẹn (chưa đọc)
        notifications.add(new Notification(
                "Nhắc nhở lịch hẹn",
                "Bạn có lịch xem phòng vào ngày mai (15/12/2025) lúc 8:00 tại Quận Hải Châu",
                "1 ngày trước", "calendar", true // true = chưa đọc
        ));

        return notifications;
    }

    /**
     * CLASS NỘI BỘ: LandlordData
     * CHỨC NĂNG: Chứa dữ liệu cho giao diện chủ trọ
     */
    public static class LandlordData {

        /**
         * CLASS NỘI BỘ: ListingItem
         * CHỨC NĂNG: Đại diện cho một tin đăng của chủ trọ
         */
        public static class ListingItem {
            public int id;                  // ID tin đăng
            public String title;            // Tiêu đề tin đăng
            public String price;            // Giá phòng
            public String status;           // Trạng thái (Còn trống, Đã thuê, v.v.)
            public boolean isActive;        // Tin đăng có đang hoạt động không
            public int viewCount;           // Số lượt xem
            public int bookingCount;        // Số lượt đặt lịch

            /**
             * Constructor: Khởi tạo một tin đăng
             */
            public ListingItem(int id, String title, String price, String status,
                               boolean isActive, int viewCount, int bookingCount) {
                this.id = id;
                this.title = title;
                this.price = price;
                this.status = status;
                this.isActive = isActive;
                this.viewCount = viewCount;
                this.bookingCount = bookingCount;
            }
        }

        /**
         * PHƯƠNG THỨC: getListings()
         * CHỨC NĂNG: Lấy danh sách tin đăng của chủ trọ
         * RETURN: List<ListingItem> - Danh sách 4 tin đăng mẫu
         */
        public static List<ListingItem> getListings() {
            List<ListingItem> listings = new ArrayList<>();

            // Tin đăng 1: Phòng cao cấp - Còn trống, có 156 lượt xem, 3 đặt lịch
            listings.add(new ListingItem(1, "Phòng trọ cao cấp gần ĐH Đông Á", "2.500.000 đ/tháng", "Còn trống", true, 156, 3));

            // Tin đăng 2: Chung cư mini - Đã thuê, có 289 lượt xem, 8 đặt lịch
            listings.add(new ListingItem(2, "Chung cư mini view biển Mỹ Khê", "3.200.000 đ/tháng", "Đã thuê", true, 289, 8));

            // Tin đăng 3: Phòng sinh viên - Chờ duyệt, chưa có lượt xem
            listings.add(new ListingItem(3, "Phòng trọ giá rẻ cho sinh viên", "1.800.000 đ/tháng", "Chờ duyệt", false, 0, 0));

            // Tin đăng 4: Studio cao cấp - Còn trống, có 412 lượt xem, 12 đặt lịch
            listings.add(new ListingItem(4, "Studio cao cấp gần Cầu Rồng", "4.500.000 đ/tháng", "Còn trống", true, 412, 12));

            return listings;
        }
    }
}
