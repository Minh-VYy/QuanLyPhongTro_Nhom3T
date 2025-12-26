package com.example.QuanLyPhongTro_App.utils;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
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
import com.example.QuanLyPhongTro_App.data.request.CreateBookingRequest;
import com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse;
import com.example.QuanLyPhongTro_App.data.request.UpdateBookingStatusRequest;
import com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponse;

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
     * GET /api/Phong/{id}
     * Public - lấy chi tiết phòng
     */
    @GET("/api/Phong/{id}")
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
     * POST /api/DatPhong
     * Create booking
     */
    @POST("/api/DatPhong")
    Call<GenericResponse<Object>> createBooking(@Body Object bookingRequest);

    /**
     * POST /api/DatPhong
     * Create booking (typed)
     */
    @POST("/api/DatPhong")
    Call<GenericResponse<Object>> createBooking(@Body CreateBookingRequest request);

    /**
     * POST /api/DatPhong (raw)
     * Debug/compat: return raw body so client can parse inconsistent response formats.
     */
    @POST("/api/DatPhong")
    Call<okhttp3.ResponseBody> createBookingRaw(@Body CreateBookingRequest request);

    /**
     * GET /api/DatPhong/my-bookings
     * Get user's bookings
     */
    @GET("/api/DatPhong/my-bookings")
    Call<GenericResponse<List<Object>>> getMyBookings();

    /**
     * GET /api/DatPhong/my-bookings (typed)
     */
    @GET("/api/DatPhong/my-bookings")
    Call<MyBookingsResponse> getMyBookingsTyped();

    /**
     * GET /api/DatPhong/my-bookings
     * Tolerant: backend may return either wrapped object or a plain JSON array.
     */
    @GET("/api/DatPhong/my-bookings")
    Call<ApiOrArrayResponse<MyBookingsResponse.MyBookingDto>> getMyBookingsTolerant();

    /**
     * PUT /api/DatPhong/{id}/status
     * Update booking status (e.g., cancel booking -> trangThaiId=4)
     */
    @PUT("/api/DatPhong/{id}/status")
    Call<GenericResponse<Object>> updateBookingStatus(
            @Path("id") String bookingId,
            @Body UpdateBookingStatusRequest request
    );

    // ==================== NHÀ TRỌ (LANDLORD) ENDPOINTS ====================

    /**
     * GET /api/NhaTro/my-houses
     * Authenticated landlord - list houses owned by current landlord.
     */
    @GET("/api/NhaTro/my-houses")
    Call<List<Object>> getMyHouses();

    /**
     * GET /api/NhaTro/{id}
     * Swagger trả raw JSON object (không wrapped {success, Data}).
     */
    @GET("/api/NhaTro/{id}")
    Call<Object> getNhaTroDetail(@Path("id") String nhaTroId);

    // ==================== PHÒNG (CRUD) ENDPOINTS ====================

    /**
     * GET /api/Phong?nhaTroId={uuid}&page=1&pageSize=50
     * Swagger responses are wrapped (object), not a raw JSON array.
     */
    @GET("/api/Phong")
    Call<GenericResponse<List<Object>>> getRoomsByHouseWrapped(
            @Query("nhaTroId") String nhaTroId,
            @Query("page") int page,
            @Query("pageSize") int pageSize
    );

    /**
     * POST /api/Phong
     */
    @POST("/api/Phong")
    Call<Object> createPhong(@Body Object request);

    /**
     * PUT /api/Phong/{id}
     */
    @PUT("/api/Phong/{id}")
    Call<Object> updatePhong(@Path("id") String phongId, @Body Object request);

    /**
     * DELETE /api/Phong/{id}
     * NOTE: Backend currently does NOT allow DELETE (405 Allow: GET, PUT).
     * Keep this for reference but DO NOT use it.
     */
    @Deprecated
    @DELETE("/api/Phong/{id}")
    Call<Object> deletePhong(@Path("id") String phongId);

    /**
     * PUT /api/Phong/lock/{id}?isLocked=true|false
     * Use this as the "delete" (soft lock/unlock) action from the app.
     */
    @PUT("/api/Phong/lock/{id}")
    Call<Object> lockPhong(@Path("id") String phongId, @Query("isLocked") boolean isLocked);

    // ==================== LANDLORD BOOKING REQUESTS ENDPOINTS ====================

    /**
     * Swagger:
     * GET /api/DatPhong/landlord-requests
     * Authenticated landlord - list booking requests for current landlord.
     */
    @GET("/api/DatPhong/landlord-requests")
    Call<GenericResponse<List<Object>>> getLandlordBookingRequestsV2();

    /**
     * Swagger:
     * PUT /api/DatPhong/status/{id}?status={int}
     * Update booking request status.
     */
    @PUT("/api/DatPhong/status/{id}")
    Call<GenericResponse<Object>> updateLandlordBookingStatusV2(
            @Path("id") String bookingId,
            @Query("status") int status
    );

    /**
     * Swagger:
     * GET /api/DatPhong/landlord-requests
     * Some backend builds return plain JSON array, not wrapped.
     */
    @GET("/api/DatPhong/landlord-requests")
    Call<List<Object>> getLandlordBookingRequestsV2Raw();

    /**
     * Swagger:
     * PUT /api/DatPhong/status/{id}?status={int}
     * Some backend builds return plain text / empty body.
     */
    @PUT("/api/DatPhong/status/{id}")
    Call<Object> updateLandlordBookingStatusV2Raw(
            @Path("id") String bookingId,
            @Query("status") int status
    );

    /**
     * Legacy (kept for backward compatibility):
     * GET /api/datphong/landlord/requests/{chuTroId}
     */
    @Deprecated
    @GET("/api/datphong/landlord/requests/{chuTroId}")
    Call<GenericResponse<List<Object>>> getLandlordBookingRequestsByPath(@Path("chuTroId") String chuTroId);

    /**
     * Legacy (kept for backward compatibility):
     * GET /api/datphong/landlord/requests?chuTroId={chuTroId}
     */
    @Deprecated
    @GET("/api/datphong/landlord/requests")
    Call<GenericResponse<List<Object>>> getLandlordBookingRequests(@Query("chuTroId") String chuTroId);

    /**
     * Legacy (kept for backward compatibility):
     * PUT /api/datphong/landlord/{id}/status
     */
    @Deprecated
    @PUT("/api/datphong/landlord/{id}/status")
    Call<GenericResponse<Object>> updateLandlordBookingStatus(
            @Path("id") String bookingId,
            @Body com.example.QuanLyPhongTro_App.data.request.UpdateBookingStatusByLandlordRequest request
    );

    // ==================== HỒ SƠ NGƯỜI DÙNG ====================

    /**
     * GET /api/HoSoNguoiDung/me
     * Authenticated - Get current user's profile (HoSoNguoiDung)
     */
    @GET("/api/HoSoNguoiDung/me")
    Call<GenericResponse<Object>> getMyUserProfile();

    /**
     * POST /api/HoSoNguoiDung
     * Create/update user profile
     */
    @POST("/api/HoSoNguoiDung")
    Call<GenericResponse<Object>> createOrUpdateUserProfile(@Body Object request);

    // ==================== TẬP TIN (UPLOAD/DOWNLOAD) ====================

    /**
     * POST /api/TapTin/upload
     * multipart/form-data with field name: file
     */
    @retrofit2.http.Multipart
    @POST("/api/TapTin/upload")
    Call<GenericResponse<Object>> uploadFile(@retrofit2.http.Part okhttp3.MultipartBody.Part file);

    /**
     * GET /api/TapTin/{id}
     * Returns file content (image bytes). Use this URL directly with Glide.
     */
    @GET("/api/TapTin/{id}")
    Call<okhttp3.ResponseBody> getFileById(@Path("id") String id);
}
