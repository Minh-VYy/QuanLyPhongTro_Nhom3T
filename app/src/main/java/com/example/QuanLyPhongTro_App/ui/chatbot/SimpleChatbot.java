package com.example.QuanLyPhongTro_App.ui.chatbot;

import java.util.HashMap;
import java.util.Map;

public class SimpleChatbot {
    private Map<String, String> tenantResponses;
    private Map<String, String> landlordResponses;
    private Map<String, String> commonResponses;
    private String userType;

    public SimpleChatbot(String userType) {
        this.userType = userType;
        tenantResponses = new HashMap<>();
        landlordResponses = new HashMap<>();
        commonResponses = new HashMap<>();
        initResponses();
    }

    private void initResponses() {
        initCommonResponses();
        initTenantResponses();
        initLandlordResponses();
    }

    /**
     * Câu trả lời chung cho cả 2 loại người dùng
     */
    private void initCommonResponses() {
        // Câu chào
        commonResponses.put("xin chào", "Xin chào! Tôi có thể giúp gì cho bạn?");
        commonResponses.put("chào", "Chào bạn! Tôi là trợ lý AI. Bạn cần hỗ trợ gì?");
        commonResponses.put("hello", "Hello! Tôi có thể giúp gì cho bạn?");
        commonResponses.put("hi", "Hi! Bạn cần hỗ trợ gì?");
        
        // Câu cảm ơn
        commonResponses.put("cảm ơn", "Không có gì! Rất vui được giúp bạn. 😊");
        commonResponses.put("cám ơn", "Không có gì! Rất vui được giúp bạn. 😊");
        commonResponses.put("thank", "You're welcome! 😊");
        commonResponses.put("thanks", "You're welcome! 😊");
        
        // Câu hỏi về app
        commonResponses.put("app", "Đây là ứng dụng quản lý phòng trọ giúp kết nối người thuê và chủ trọ.");
        commonResponses.put("ứng dụng", "Ứng dụng giúp bạn tìm kiếm, đặt phòng và quản lý phòng trọ dễ dàng.");
    }

    /**
     * Câu trả lời dành riêng cho NGƯỜI THUÊ
     */
    private void initTenantResponses() {
        // Tìm kiếm phòng
        tenantResponses.put("tìm phòng", "Bạn có thể tìm phòng ở tab 'Trang chủ'. Sử dụng bộ lọc để tìm phòng phù hợp với nhu cầu của bạn.");
        tenantResponses.put("tìm kiếm", "Nhấn vào nút 'Lọc' trên màn hình chính để tìm phòng theo giá, diện tích, địa điểm.");
        tenantResponses.put("lọc", "Bộ lọc giúp bạn tìm phòng theo: giá, diện tích, số người, tiện ích. Nhấn nút 'Lọc' để sử dụng.");
        
        // Giá phòng
        tenantResponses.put("giá phòng", "Giá phòng dao động từ 1-5 triệu/tháng tùy vào diện tích và tiện ích. Bạn có thể lọc theo mức giá mong muốn.");
        tenantResponses.put("giá", "Giá phòng thay đổi tùy khu vực và tiện ích. Sử dụng bộ lọc để tìm phòng trong tầm giá của bạn.");
        tenantResponses.put("rẻ", "Để tìm phòng giá rẻ, vào bộ lọc và chọn khoảng giá dưới 2 triệu/tháng.");
        
        // Đặt phòng
        tenantResponses.put("đặt phòng", "Để đặt phòng:\n1. Chọn phòng bạn thích\n2. Nhấn 'Đặt phòng'\n3. Điền thông tin và chọn ngày xem\n4. Chờ chủ trọ xác nhận");
        tenantResponses.put("đặt lịch", "Vào tab 'Đặt lịch' để xem các lịch hẹn của bạn. Bạn có thể hủy hoặc đổi lịch ở đây.");
        tenantResponses.put("xem phòng", "Sau khi đặt lịch, chủ trọ sẽ xác nhận. Bạn nhận thông báo và có thể xem chi tiết trong tab 'Đặt lịch'.");
        tenantResponses.put("hủy đặt", "Để hủy lịch hẹn, vào tab 'Đặt lịch', chọn lịch cần hủy và nhấn 'Hủy'.");
        
        // Yêu thích
        tenantResponses.put("yêu thích", "Nhấn icon trái tim ở góc phòng để lưu vào danh sách yêu thích. Xem lại trong menu.");
        tenantResponses.put("lưu phòng", "Nhấn icon trái tim để lưu phòng. Xem danh sách đã lưu trong menu chính.");
        
        // Thông báo
        tenantResponses.put("thông báo", "Tab 'Thông báo' hiển thị:\n• Xác nhận đặt lịch\n• Tin nhắn từ chủ trọ\n• Cập nhật phòng yêu thích");
        tenantResponses.put("tin nhắn", "Bạn nhận tin nhắn từ chủ trọ trong tab 'Thông báo'. Có thể trả lời trực tiếp.");
        
        // Tiện ích
        tenantResponses.put("tiện ích", "Các tiện ích phổ biến:\n• Điều hòa, nóng lạnh\n• WiFi miễn phí\n• Giường, tủ, bàn học\n• Máy giặt chung\n• Bãi xe");
        tenantResponses.put("điều hòa", "Hầu hết phòng đều có điều hòa. Kiểm tra mục 'Tiện ích' trong chi tiết phòng.");
        tenantResponses.put("wifi", "WiFi miễn phí có sẵn ở hầu hết các phòng. Xem chi tiết trong thông tin phòng.");
        
        // Diện tích
        tenantResponses.put("diện tích", "Phòng có diện tích từ 15m² đến 40m². Sử dụng bộ lọc để tìm theo diện tích mong muốn.");
        tenantResponses.put("rộng", "Phòng rộng thường từ 30m² trở lên. Dùng bộ lọc để tìm phòng rộng.");
        
        // Hợp đồng & Thanh toán
        tenantResponses.put("hợp đồng", "Hợp đồng thuê thường tối thiểu 6 tháng. Chi tiết thỏa thuận trực tiếp với chủ trọ khi xem phòng.");
        tenantResponses.put("thanh toán", "Thanh toán:\n• Tiền mặt khi ký hợp đồng\n• Chuyển khoản hàng tháng\nChi tiết thỏa thuận với chủ trọ.");
        tenantResponses.put("cọc", "Tiền cọc thường bằng 1-2 tháng tiền phòng. Thỏa thuận cụ thể khi xem phòng.");
        
        // Địa điểm
        tenantResponses.put("địa chỉ", "Xem địa chỉ chi tiết trong thông tin phòng. Có bản đồ để xem vị trí chính xác.");
        tenantResponses.put("gần", "Dùng bộ lọc 'Khu vực' để tìm phòng gần trường, công ty, hoặc địa điểm bạn muốn.");
        
        // Liên hệ
        tenantResponses.put("liên hệ", "Sau khi đặt lịch, bạn có thể liên hệ chủ trọ qua:\n• Tin nhắn trong app\n• Số điện thoại (hiển thị sau khi đặt lịch)");
        tenantResponses.put("gọi", "Số điện thoại chủ trọ hiển thị sau khi bạn đặt lịch xem phòng thành công.");
    }

    /**
     * Câu trả lời dành riêng cho CHỦ TRỌ
     */
    private void initLandlordResponses() {
        // Đăng tin
        landlordResponses.put("đăng tin", "Để đăng tin:\n1. Nhấn nút '+' ở góc dưới\n2. Điền thông tin phòng\n3. Thêm ảnh\n4. Nhấn 'Đăng tin'");
        landlordResponses.put("tạo tin", "Nhấn nút '+' màu xanh ở góc dưới màn hình để tạo tin đăng mới.");
        landlordResponses.put("đăng phòng", "Vào tab 'Trang chủ', nhấn nút '+' để đăng phòng mới. Điền đầy đủ thông tin để thu hút người thuê.");
        
        // Quản lý tin đăng
        landlordResponses.put("sửa tin", "Để sửa tin:\n1. Vào 'Trang chủ'\n2. Nhấn vào tin cần sửa\n3. Chỉnh sửa thông tin\n4. Lưu lại");
        landlordResponses.put("xóa tin", "Nhấn vào tin đăng, chọn 'Xóa' để gỡ tin. Tin đã xóa không thể khôi phục.");
        landlordResponses.put("ẩn tin", "Tắt công tắc 'Hoạt động' trên tin đăng để tạm ẩn. Bật lại khi cần.");
        landlordResponses.put("quản lý tin", "Tab 'Trang chủ' hiển thị tất cả tin đăng của bạn. Bạn có thể sửa, xóa, bật/tắt tin.");
        
        // Yêu cầu đặt phòng
        landlordResponses.put("yêu cầu", "Tab 'Yêu cầu' hiển thị:\n• Lịch hẹn xem phòng\n• Tin nhắn từ người thuê\n• Thông báo mới");
        landlordResponses.put("đặt lịch", "Người thuê đặt lịch xem phòng sẽ hiện trong tab 'Yêu cầu'. Bạn có thể chấp nhận hoặc từ chối.");
        landlordResponses.put("xác nhận", "Để xác nhận lịch hẹn:\n1. Vào tab 'Yêu cầu'\n2. Chọn yêu cầu\n3. Nhấn 'Chấp nhận' hoặc 'Từ chối'");
        landlordResponses.put("từ chối", "Bạn có thể từ chối yêu cầu nếu không phù hợp. Người thuê sẽ nhận thông báo.");
        
        // Tin nhắn
        landlordResponses.put("tin nhắn", "Tab 'Yêu cầu' > 'Tin nhắn' để xem và trả lời tin nhắn từ người thuê.");
        landlordResponses.put("trả lời", "Nhấn vào tin nhắn để đọc và trả lời người thuê. Trả lời nhanh để tăng uy tín.");
        landlordResponses.put("chat", "Bạn có thể chat trực tiếp với người thuê trong tab 'Tin nhắn'.");
        
        // Thống kê
        landlordResponses.put("thống kê", "Tab 'Thống kê' hiển thị:\n• Doanh thu\n• Số phòng đang cho thuê\n• Lượt xem tin\n• Yêu cầu đặt phòng");
        landlordResponses.put("doanh thu", "Xem doanh thu chi tiết trong tab 'Thống kê'. Có biểu đồ theo tháng.");
        landlordResponses.put("báo cáo", "Tab 'Thống kê' cung cấp báo cáo chi tiết về hoạt động cho thuê của bạn.");
        
        // Quản lý phòng
        landlordResponses.put("quản lý", "Quản lý phòng trọ:\n• Trang chủ: Xem/sửa tin đăng\n• Yêu cầu: Xử lý đặt lịch\n• Thống kê: Xem doanh thu\n• Hồ sơ: Cài đặt");
        landlordResponses.put("phòng trống", "Đánh dấu phòng 'Còn trống' bằng công tắc trên tin đăng để người thuê biết.");
        landlordResponses.put("phòng đầy", "Tắt công tắc 'Hoạt động' khi phòng đã có người thuê.");
        
        // Giá & Tiện ích
        landlordResponses.put("giá", "Đặt giá cạnh tranh dựa trên:\n• Vị trí\n• Diện tích\n• Tiện ích\nXem giá phòng tương tự để tham khảo.");
        landlordResponses.put("tiện ích", "Thêm tiện ích khi đăng tin:\n• Điều hòa\n• WiFi\n• Máy giặt\n• Bãi xe\nNhiều tiện ích = thu hút hơn!");
        landlordResponses.put("ảnh", "Thêm ảnh đẹp, rõ nét để tin đăng hấp dẫn hơn. Tối thiểu 3-5 ảnh.");
        
        // Mô tả
        landlordResponses.put("mô tả", "Viết mô tả chi tiết:\n• Diện tích, giá\n• Tiện ích\n• Vị trí (gần trường, chợ...)\n• Quy định\nMô tả tốt = nhiều người quan tâm!");
        landlordResponses.put("viết tin", "Mẹo viết tin tốt:\n• Tiêu đề ngắn gọn, hấp dẫn\n• Mô tả chi tiết, trung thực\n• Thêm nhiều ảnh\n• Cập nhật thường xuyên");
        
        // Hỗ trợ
        landlordResponses.put("trợ giúp", "Vào tab 'Hồ sơ' > 'Trợ giúp' để xem hướng dẫn chi tiết về các tính năng.");
        landlordResponses.put("hướng dẫn", "Tab 'Trợ giúp' có video và bài viết hướng dẫn sử dụng app cho chủ trọ.");
    }

    public String getResponse(String message) {
        message = message.toLowerCase().trim();
        
        // Kiểm tra câu trả lời chung trước
        for (Map.Entry<String, String> entry : commonResponses.entrySet()) {
            if (message.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Kiểm tra theo loại người dùng
        Map<String, String> responses = "tenant".equals(userType) ? tenantResponses : landlordResponses;
        
        for (Map.Entry<String, String> entry : responses.entrySet()) {
            if (message.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        
        // Câu trả lời mặc định theo loại người dùng
        return getDefaultResponse();
    }

    private String getDefaultResponse() {
        if ("tenant".equals(userType)) {
            return "Xin lỗi, tôi chưa hiểu câu hỏi của bạn. Bạn có thể hỏi về:\n\n" +
                   "🔍 Tìm kiếm:\n• Tìm phòng, lọc phòng\n• Giá phòng, diện tích\n• Tiện ích, địa chỉ\n\n" +
                   "📅 Đặt phòng:\n• Cách đặt lịch xem phòng\n• Hủy đặt lịch\n• Liên hệ chủ trọ\n\n" +
                   "❤️ Khác:\n• Lưu phòng yêu thích\n• Xem thông báo\n• Thanh toán, hợp đồng";
        } else {
            return "Xin lỗi, tôi chưa hiểu câu hỏi của bạn. Bạn có thể hỏi về:\n\n" +
                   "📝 Đăng tin:\n• Cách đăng tin mới\n• Sửa/xóa tin đăng\n• Viết mô tả hấp dẫn\n\n" +
                   "📋 Quản lý:\n• Xử lý yêu cầu đặt phòng\n• Trả lời tin nhắn\n• Quản lý phòng trống\n\n" +
                   "📊 Khác:\n• Xem thống kê doanh thu\n• Đặt giá phòng\n• Thêm tiện ích";
        }
    }
}
