package com.example.QuanLyPhongTro_App.data;

import com.example.QuanLyPhongTro_App.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Dữ liệu mẫu cho Chủ trọ
 * Tạo bởi: knhi
 */
public class MockDataLandlord {

    /**
     * Lấy danh sách tin đăng của chủ trọ
     */
    public static List<LandlordListing> getLandlordListings() {
        List<LandlordListing> listings = new ArrayList<>();

        listings.add(new LandlordListing(
                1,
                "Phòng trọ cao cấp gần ĐH Bách Khoa",
                3500000,
                "Quận Liên Chiểu, Đà Nẵng",
                "123 Nguyễn Sinh Sắc, Phường Hòa Minh, Quận Liên Chiểu",
                25.0,
                "Phòng mới xây, đầy đủ nội thất, an ninh 24/7. Gần trường học, siêu thị.",
                R.drawable.room_1,
                4.7,
                12,
                true,
                "Còn trống",
                8
        ));

        listings.add(new LandlordListing(
                2,
                "Căn hộ mini 2 phòng ngủ",
                5200000,
                "Quận Hải Châu, Đà Nẵng",
                "456 Trần Phú, Phường Thạch Thang, Quận Hải Châu",
                40.0,
                "Căn hộ rộng rãi, 2 phòng ngủ, bếp riêng, ban công view biển.",
                R.drawable.room_2,
                4.9,
                20,
                true,
                "Còn trống",
                5
        ));

        listings.add(new LandlordListing(
                3,
                "Phòng trọ sinh viên giá rẻ",
                1800000,
                "Quận Ngũ Hành Sơn, Đà Nẵng",
                "789 Nguyễn Tất Thành, Phường Khuê Mỹ, Ngũ Hành Sơn",
                18.0,
                "Phòng sạch sẽ, giá phù hợp sinh viên. Gần ĐH Duy Tân.",
                R.drawable.room_3,
                4.3,
                8,
                true,
                "Còn trống",
                15
        ));

        listings.add(new LandlordListing(
                4,
                "Studio hiện đại full nội thất",
                4500000,
                "Quận Sơn Trà, Đà Nẵng",
                "234 Võ Nguyên Giáp, Phường Phước Mỹ, Quận Sơn Trà",
                30.0,
                "Studio hiện đại, máy lạnh, nóng lạnh, tủ lạnh, máy giặt đầy đủ. View biển tuyệt đẹp.",
                R.drawable.room_4,
                4.8,
                15,
                true,
                "Còn trống",
                3
        ));

        listings.add(new LandlordListing(
                5,
                "Phòng có gác lửng tiện nghi",
                2800000,
                "Quận Thanh Khê, Đà Nẵng",
                "567 Điện Biên Phủ, Phường Thanh Khê Tây, Thanh Khê",
                22.0,
                "Phòng có gác lửng, thoáng mát, gần Sân bay Quốc tế Đà Nẵng.",
                R.drawable.room_5,
                4.5,
                10,
                false,
                "Đã thuê",
                0
        ));

        listings.add(new LandlordListing(
                6,
                "Chung cư mini cao cấp",
                6000000,
                "Quận Hải Châu, Đà Nẵng",
                "890 Nguyễn Văn Linh, Phường Nam Dương, Quận Hải Châu",
                45.0,
                "Chung cư mini cao cấp, bảo vệ 24/7, thang máy, chỗ đậu xe. Gần cầu Rồng.",
                R.drawable.room_6,
                5.0,
                25,
                true,
                "Còn trống",
                2
        ));

        listings.add(new LandlordListing(
                7,
                "Phòng trọ gần Chợ Hàn",
                3200000,
                "Quận Hải Châu, Đà Nẵng",
                "345 Hùng Vương, Phường Vĩnh Trung, Quận Hải Châu",
                20.0,
                "Vị trí đắc địa, gần Chợ Hàn, trung tâm thành phố.",
                R.drawable.room_7,
                4.6,
                18,
                true,
                "Còn trống",
                7
        ));

        listings.add(new LandlordListing(
                8,
                "Nhà nguyên căn 3 tầng",
                15000000,
                "Quận Cẩm Lệ, Đà Nẵng",
                "678 Nguyễn Hữu Thọ, Phường Khuê Trung, Quận Cẩm Lệ",
                120.0,
                "Nhà nguyên căn 3 tầng, 4 phòng ngủ, sân thượng, garage ô tô. Gần Khu công nghệ cao.",
                R.drawable.room_8,
                4.9,
                30,
                false,
                "Chờ xử lý",
                0
        ));

        return listings;
    }

    /**
     * Lấy danh sách yêu cầu thuê phòng
     */
    public static List<RentalRequest> getRentalRequests() {
        List<RentalRequest> requests = new ArrayList<>();

        requests.add(new RentalRequest(
                1,
                "Nguyễn Văn An",
                "0901234567",
                1,
                "Phòng trọ cao cấp gần ĐH Bách Khoa",
                "15/12/2024",
                "Sáng (8-12h)",
                "Tôi muốn xem phòng và có thể chuyển vào ngay nếu phù hợp.",
                "pending"
        ));

        requests.add(new RentalRequest(
                2,
                "Trần Thị Bình",
                "0912345678",
                2,
                "Căn hộ mini 2 phòng ngủ",
                "16/12/2024",
                "Chiều (13-17h)",
                "Gia đình tôi 3 người muốn thuê dài hạn.",
                "pending"
        ));

        requests.add(new RentalRequest(
                3,
                "Lê Văn Cường",
                "0923456789",
                4,
                "Studio hiện đại full nội thất",
                "14/12/2024",
                "Tối (18-20h)",
                "Tôi đang làm việc gần đây, muốn thuê ngay.",
                "confirmed"
        ));

        requests.add(new RentalRequest(
                4,
                "Phạm Thị Dung",
                "0934567890",
                3,
                "Phòng trọ sinh viên giá rẻ",
                "13/12/2024",
                "Sáng (8-12h)",
                "Tôi là sinh viên, muốn tìm phòng giá rẻ.",
                "confirmed"
        ));

        requests.add(new RentalRequest(
                5,
                "Hoàng Văn Em",
                "0945678901",
                6,
                "Chung cư mini cao cấp",
                "10/11/2024",
                "Chiều (13-17h)",
                "Đã xem và đồng ý thuê.",
                "completed"
        ));

        return requests;
    }

    /**
     * Lấy thống kê cho chủ trọ
     */
    public static LandlordStats getLandlordStats() {
        return new LandlordStats(
                8,      // Tổng số phòng
                5,      // Số phòng còn trống
                2,      // Số phòng đã thuê
                1,      // Số phòng chờ xử lý
                5,      // Tổng yêu cầu xem phòng
                2,      // Yêu cầu đang chờ
                2,      // Yêu cầu đã xác nhận
                1,      // Yêu cầu đã hoàn thành
                24500000, // Doanh thu tháng này (VNĐ)
                4.7     // Đánh giá trung bình
        );
    }

    // ==================== INNER CLASSES ====================

    /**
     * Class đại diện cho tin đăng của chủ trọ
     */
    public static class LandlordListing {
        private int id;
        private String title;
        private double price;
        private String location;
        private String address;
        private double area;
        private String description;
        private int imageResId;
        private double rating;
        private int reviewCount;
        private boolean isActive;
        private String status; // "Còn trống", "Đã thuê", "Chờ xử lý"
        private int viewCount;

        public LandlordListing(int id, String title, double price, String location,
                               String address, double area, String description,
                               int imageResId, double rating, int reviewCount,
                               boolean isActive, String status, int viewCount) {
            this.id = id;
            this.title = title;
            this.price = price;
            this.location = location;
            this.address = address;
            this.area = area;
            this.description = description;
            this.imageResId = imageResId;
            this.rating = rating;
            this.reviewCount = reviewCount;
            this.isActive = isActive;
            this.status = status;
            this.viewCount = viewCount;
        }

        // Getters
        public int getId() { return id; }
        public String getTitle() { return title; }
        public double getPrice() { return price; }
        public String getLocation() { return location; }
        public String getAddress() { return address; }
        public double getArea() { return area; }
        public String getDescription() { return description; }
        public int getImageResId() { return imageResId; }
        public double getRating() { return rating; }
        public int getReviewCount() { return reviewCount; }
        public boolean isActive() { return isActive; }
        public String getStatus() { return status; }
        public int getViewCount() { return viewCount; }

        // Helper methods
        public String getFormattedPrice() {
            return String.format("%.1f triệu/tháng", price / 1000000);
        }

        public String getFormattedArea() {
            return String.format("%.0fm²", area);
        }

        public String getFormattedRating() {
            return String.format("%.1f (%d đánh giá)", rating, reviewCount);
        }
    }

    /**
     * Class đại diện cho yêu cầu thuê phòng
     */
    public static class RentalRequest {
        private int id;
        private String customerName;
        private String customerPhone;
        private int roomId;
        private String roomTitle;
        private String date;
        private String timeSlot;
        private String note;
        private String status; // "pending", "confirmed", "completed", "cancelled"

        public RentalRequest(int id, String customerName, String customerPhone,
                             int roomId, String roomTitle, String date,
                             String timeSlot, String note, String status) {
            this.id = id;
            this.customerName = customerName;
            this.customerPhone = customerPhone;
            this.roomId = roomId;
            this.roomTitle = roomTitle;
            this.date = date;
            this.timeSlot = timeSlot;
            this.note = note;
            this.status = status;
        }

        // Getters
        public int getId() { return id; }
        public String getCustomerName() { return customerName; }
        public String getCustomerPhone() { return customerPhone; }
        public int getRoomId() { return roomId; }
        public String getRoomTitle() { return roomTitle; }
        public String getDate() { return date; }
        public String getTimeSlot() { return timeSlot; }
        public String getNote() { return note; }
        public String getStatus() { return status; }

        public String getStatusText() {
            switch (status) {
                case "pending": return "Đang chờ";
                case "confirmed": return "Đã xác nhận";
                case "completed": return "Đã hoàn thành";
                case "cancelled": return "Đã hủy";
                default: return status;
            }
        }
    }

    /**
     * Class đại diện cho thống kê của chủ trọ
     */
    public static class LandlordStats {
        private int totalRooms;
        private int vacantRooms;
        private int occupiedRooms;
        private int pendingRooms;
        private int totalRequests;
        private int pendingRequests;
        private int confirmedRequests;
        private int completedRequests;
        private double monthlyRevenue;
        private double averageRating;

        public LandlordStats(int totalRooms, int vacantRooms, int occupiedRooms,
                             int pendingRooms, int totalRequests, int pendingRequests,
                             int confirmedRequests, int completedRequests,
                             double monthlyRevenue, double averageRating) {
            this.totalRooms = totalRooms;
            this.vacantRooms = vacantRooms;
            this.occupiedRooms = occupiedRooms;
            this.pendingRooms = pendingRooms;
            this.totalRequests = totalRequests;
            this.pendingRequests = pendingRequests;
            this.confirmedRequests = confirmedRequests;
            this.completedRequests = completedRequests;
            this.monthlyRevenue = monthlyRevenue;
            this.averageRating = averageRating;
        }

        // Getters
        public int getTotalRooms() { return totalRooms; }
        public int getVacantRooms() { return vacantRooms; }
        public int getOccupiedRooms() { return occupiedRooms; }
        public int getPendingRooms() { return pendingRooms; }
        public int getTotalRequests() { return totalRequests; }
        public int getPendingRequests() { return pendingRequests; }
        public int getConfirmedRequests() { return confirmedRequests; }
        public int getCompletedRequests() { return completedRequests; }
        public double getMonthlyRevenue() { return monthlyRevenue; }
        public double getAverageRating() { return averageRating; }

        public String getFormattedRevenue() {
            return String.format("%.1f triệu", monthlyRevenue / 1000000);
        }
    }
}