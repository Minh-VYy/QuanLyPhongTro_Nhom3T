package com.example.QuanLyPhongTro_App.utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.util.List;
import com.example.QuanLyPhongTro_App.data.request.LoginRequest;
import com.example.QuanLyPhongTro_App.data.request.RegisterRequest;
import com.example.QuanLyPhongTro_App.data.response.LoginResponse;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;

/**
 * Retrofit API Service - định nghĩa toàn bộ endpoints
 */
public interface ApiService {

    // ==================== AUTH ENDPOINTS ====================

    /**
     * POST /api/nguoidung/login
     * Body: { email, password }
     * Response: { token }
     */
    @POST("/api/nguoidung/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    /**
     * POST /api/nguoidung/register
     * Body: { email, password, dienThoai, hoTen, vaiTroId }
     * Response: user data
     */
    @POST("/api/nguoidung/register")
    Call<GenericResponse<Object>> register(@Body RegisterRequest request);

    // ==================== PHÒNG ENDPOINTS ====================

    /**
     * GET /api/phong?page=1&pageSize=10&minPrice=0&maxPrice=5000000
     * Public - không cần token
     */
    @GET("/api/phong")
    Call<GenericResponse<List<Room>>> getRooms(
            @Query("page") int page,
            @Query("pageSize") int pageSize,
            @Query("minPrice") long minPrice,
            @Query("maxPrice") long maxPrice
    );

    /**
     * GET /api/phong/{id}
     * Public - lấy chi tiết phòng
     */
    @GET("/api/phong/{id}")
    Call<GenericResponse<Room>> getRoomDetail(@Path("id") String roomId);

    // ==================== THÔNG BÁO ENDPOINTS ====================

    /**
     * GET /api/thongbao
     * Authenticated - lấy danh sách notifications
     */
    @GET("/api/thongbao")
    Call<GenericResponse<List<Object>>> getNotifications();

    /**
     * POST /api/thongbao/{id}/mark-as-read
     */
    @POST("/api/thongbao/{id}/mark-as-read")
    Call<GenericResponse<Object>> markNotificationAsRead(@Path("id") String notificationId);

    // ==================== CHAT ENDPOINTS ====================

    /**
     * POST /api/tinnhan
     * Send message
     */
    @POST("/api/tinnhan")
    Call<GenericResponse<Object>> sendMessage(@Body Object messageRequest);

    /**
     * GET /api/tinnhan/conversation/{otherUserId}
     * Get message history with another user
     */
    @GET("/api/tinnhan/conversation/{otherUserId}")
    Call<GenericResponse<List<Object>>> getMessageHistory(@Path("otherUserId") String otherUserId);

    /**
     * PUT /api/tinnhan/{otherUserId}/read
     * Mark messages as read
     */
    @PUT("/api/tinnhan/{otherUserId}/read")
    Call<GenericResponse<Object>> markMessagesAsRead(@Path("otherUserId") String otherUserId);

    // ==================== ĐẶT PHÒNG ENDPOINTS ====================

    /**
     * POST /api/datphong
     * Create booking
     */
    @POST("/api/datphong")
    Call<GenericResponse<Object>> createBooking(@Body Object bookingRequest);

    /**
     * GET /api/datphong/my-bookings
     * Get user's bookings
     */
    @GET("/api/datphong/my-bookings")
    Call<GenericResponse<List<Object>>> getMyBookings();
}

