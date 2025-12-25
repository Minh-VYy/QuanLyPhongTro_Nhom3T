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
import com.example.QuanLyPhongTro_App.data.response.ChatThreadDto;

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

    /**
     * GET /api/nguoidung/me
     * Authenticated - Get current user profile
     * Response: { nguoiDungId, email, hoTen, vaiTroId, ... }
     */
    @GET("/api/nguoidung/me")
    Call<GenericResponse<Object>> getUserProfile();

    // ==================== PHÒNG ENDPOINTS ====================

    /**
     * GET /api/phong?page=1&pageSize=10&minPrice=0&maxPrice=5000000
     * Public - không cần token - Lấy danh sách phòng đã duyệt
     */
    @GET("/api/phong")
    Call<GenericResponse<List<Object>>> getRooms(
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
    Call<GenericResponse<Object>> getRoomDetail(@Path("id") String roomId);

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
    // ⚠️ IMPORTANT: C# backend uses /api/Chat/send (capital C) not /api/chat/send

    /**
     * POST /api/Chat/send (note: capital C)
     * Send message
     * Body: { FromUserId, ToUserId, Content, MessageType }
     */
    @POST("/api/Chat/send")
    Call<GenericResponse<Object>> sendMessage(@Body Object messageRequest);

    /**
     * GET /api/Chat/history?user1={userId1}&user2={userId2}&page=1&pageSize=50
     * Get message history between two users - Returns JSON ARRAY directly, not wrapped
     */
    @GET("/api/Chat/history")
    Call<List<Object>> getMessageHistory(
            @Query("user1") String user1,
            @Query("user2") String user2,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    /**
     * GET /api/Chat/contacts?userId={userId}
     * Get list of chat contacts (threads) - Returns JSON ARRAY directly, not wrapped
     */
    @GET("/api/Chat/contacts")
    Call<List<Object>> getChatContacts(@Query("userId") String userId);

    /**
     * PUT /api/Chat/read/{messageId}
     * Mark message as read
     */
    @PUT("/api/Chat/read/{messageId}")
    Call<GenericResponse<Object>> markMessageAsRead(@Path("messageId") String messageId);

    /**
     * PUT /api/Chat/read-all?fromUserId={fromId}&toUserId={toId}
     * Mark all messages as read
     */
    @PUT("/api/Chat/read-all")
    Call<GenericResponse<Object>> markAllMessagesAsRead(
            @Query("fromUserId") String fromUserId,
            @Query("toUserId") String toUserId
    );

    /**
     * GET /api/Chat/unread-count?userId={userId}
     * Get unread message count
     */
    @GET("/api/Chat/unread-count")
    Call<GenericResponse<Object>> getUnreadCount(@Query("userId") String userId);

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
