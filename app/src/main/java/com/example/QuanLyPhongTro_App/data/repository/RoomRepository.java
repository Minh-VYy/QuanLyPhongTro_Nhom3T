package com.example.QuanLyPhongTro_App.data.repository;

import android.util.Log;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Room Repository - fetch rooms from API
 */
public class RoomRepository {
    private static final String TAG = "RoomRepository";
    private ApiService apiService;

    public RoomRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    /**
     * Get list of rooms with pagination and filters
     * GET /api/phong?page=1&pageSize=10&minPrice=0&maxPrice=5000000
     */
    public void getRooms(int page, int pageSize, long minPrice, long maxPrice, RoomsCallback callback) {
        try {
            String url = "/api/phong?page=" + page + "&pageSize=" + pageSize + "&minPrice=" + minPrice + "&maxPrice=" + maxPrice;
            Log.d(TAG, "Fetching rooms: page=" + page + ", pageSize=" + pageSize + ", price=" + minPrice + "-" + maxPrice);

            // 🔍 DEBUG: Log request
            com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logRequest("GET", url, null);

            apiService.getRooms(page, pageSize, minPrice, maxPrice).enqueue(new Callback<GenericResponse<List<Object>>>() {
                @Override
                public void onResponse(Call<GenericResponse<List<Object>>> call, Response<GenericResponse<List<Object>>> response) {
                    Log.d(TAG, "Response code: " + response.code());

                    // 🔍 DEBUG: Log response
                    if (response.body() != null) {
                        com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logResponse(response.code(), response.body());
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        GenericResponse<List<Object>> responseBody = response.body();
                        List<Object> roomsData = responseBody.data;

                        if (roomsData == null) {
                            roomsData = new ArrayList<>();
                        }

                        List<RoomDto> rooms = convertToRoomDtos(roomsData);

                        Log.d(TAG, "✅ Got " + rooms.size() + " rooms");

                        // Get total count from response wrapper (if available)
                        int totalCount = rooms.size();
                        try {
                            // Try to get totalCount from response body if it exists
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            com.google.gson.JsonObject jsonObj = gson.fromJson(gson.toJson(responseBody), com.google.gson.JsonObject.class);
                            if (jsonObj.has("totalCount")) {
                                totalCount = jsonObj.get("totalCount").getAsInt();
                            }
                        } catch (Exception e) {
                            Log.w(TAG, "Could not extract totalCount from response");
                        }

                        callback.onSuccess(rooms, totalCount);
                    } else {
                        Log.e(TAG, "Response not successful or body null, code: " + response.code());

                        // 🔍 DEBUG: Log error
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logError(
                            response.code(),
                            response.message(),
                            errorBody
                        );

                        callback.onError("Failed to fetch rooms: " + response.message());
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<List<Object>>> call, Throwable t) {
                    Log.e(TAG, "❌ Network error: " + t.getMessage());

                    // 🔍 DEBUG: Log network failure
                    com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logNetworkFailure(
                        call.request().url().toString(),
                        t
                    );

                    callback.onError("Network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception: " + e.getMessage());
            callback.onError("Exception: " + e.getMessage());
        }
    }

    /**
     * Get room detail by ID
     * GET /api/phong/{id}
     */
    public void getRoomDetail(String roomId, RoomDetailCallback callback) {
        try {
            Log.d(TAG, "Fetching room detail: " + roomId);

            apiService.getRoomDetail(roomId).enqueue(new Callback<GenericResponse<Object>>() {
                @Override
                public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                    Log.d(TAG, "Response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        RoomDto room = convertToRoomDto(response.body().data);
                        Log.d(TAG, "✅ Got room detail");
                        callback.onSuccess(room);
                    } else {
                        Log.e(TAG, "Response not successful");
                        callback.onError("Failed to fetch room detail");
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                    Log.e(TAG, "❌ Network error: " + t.getMessage());
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception: " + e.getMessage());
            callback.onError("Exception: " + e.getMessage());
        }
    }

    /**
     * Convert API response list to RoomDto list
     */
    private List<RoomDto> convertToRoomDtos(List<Object> roomsData) {
        List<RoomDto> rooms = new ArrayList<>();

        if (roomsData == null) {
            return rooms;
        }

        Gson gson = new Gson();

        for (Object room : roomsData) {
            try {
                RoomDto roomDto = convertToRoomDto(room);
                rooms.add(roomDto);
            } catch (Exception e) {
                Log.e(TAG, "Error converting room: " + e.getMessage());
            }
        }

        return rooms;
    }

    /**
     * Convert single room object to RoomDto
     * C# backend uses PascalCase: PhongId, TieuDe, GiaTien, etc.
     */
    private RoomDto convertToRoomDto(Object roomObj) {
        if (roomObj == null) {
            return new RoomDto();
        }

        Gson gson = new Gson();
        Map<String, Object> roomMap = gson.fromJson(
            gson.toJson(roomObj),
            Map.class
        );

        RoomDto room = new RoomDto();

        // Map fields from API response (C# PascalCase)
        // Try both PascalCase and camelCase for compatibility
        Object phongId = roomMap.get("PhongId");
        if (phongId == null) phongId = roomMap.get("phongId");
        if (phongId != null) room.setPhongId(phongId.toString());

        Object nhaTroId = roomMap.get("NhaTroId");
        if (nhaTroId == null) nhaTroId = roomMap.get("nhaTroId");
        if (nhaTroId != null) room.setNhaTroId(nhaTroId.toString());

        Object tieuDe = roomMap.get("TieuDe");
        if (tieuDe == null) tieuDe = roomMap.get("tieuDe");
        if (tieuDe != null) room.setTieuDe(tieuDe.toString());

        // Add description from MoTa field
        Object moTa = roomMap.get("MoTa");
        if (moTa == null) moTa = roomMap.get("moTa");
        if (moTa != null) room.setMoTa(moTa.toString());

        Object dienTich = roomMap.get("DienTich");
        if (dienTich == null) dienTich = roomMap.get("dienTich");
        if (dienTich != null) {
            try {
                room.setDienTich(Double.parseDouble(dienTich.toString()));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing DienTich: " + e.getMessage());
            }
        }

        Object giaTien = roomMap.get("GiaTien");
        if (giaTien == null) giaTien = roomMap.get("giaTien");
        if (giaTien != null) {
            try {
                room.setGiaTien(Long.parseLong(giaTien.toString().replace(".0", "")));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing GiaTien: " + e.getMessage());
            }
        }

        Object tienCoc = roomMap.get("TienCoc");
        if (tienCoc == null) tienCoc = roomMap.get("tienCoc");
        if (tienCoc != null) {
            try {
                room.setTienCoc(Long.parseLong(tienCoc.toString().replace(".0", "")));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing TienCoc");
            }
        }

        Object soNguoiToiDa = roomMap.get("SoNguoiToiDa");
        if (soNguoiToiDa == null) soNguoiToiDa = roomMap.get("soNguoiToiDa");
        if (soNguoiToiDa != null) {
            try {
                room.setSoNguoiToiDa(Integer.parseInt(soNguoiToiDa.toString().replace(".0", "")));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing SoNguoiToiDa");
            }
        }

        Object trangThai = roomMap.get("TrangThai");
        if (trangThai == null) trangThai = roomMap.get("trangThai");
        if (trangThai != null) room.setTrangThai(trangThai.toString());

        Object diemTrungBinh = roomMap.get("DiemTrungBinh");
        if (diemTrungBinh == null) diemTrungBinh = roomMap.get("diemTrungBinh");
        if (diemTrungBinh != null) {
            try {
                room.setDiemTrungBinh(Double.parseDouble(diemTrungBinh.toString()));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing DiemTrungBinh");
            }
        }

        Object soLuongDanhGia = roomMap.get("SoLuongDanhGia");
        if (soLuongDanhGia == null) soLuongDanhGia = roomMap.get("soLuongDanhGia");
        if (soLuongDanhGia != null) {
            try {
                room.setSoLuongDanhGia(Integer.parseInt(soLuongDanhGia.toString().replace(".0", "")));
            } catch (Exception e) {
                Log.e(TAG, "Error parsing SoLuongDanhGia");
            }
        }

        Object createdAt = roomMap.get("CreatedAt");
        if (createdAt == null) createdAt = roomMap.get("createdAt");
        if (createdAt != null) room.setCreatedAt(createdAt.toString());

        return room;
    }

    // ==================== CALLBACKS ====================

    public interface RoomsCallback {
        void onSuccess(List<RoomDto> rooms, int totalCount);
        void onError(String error);
    }

    public interface RoomDetailCallback {
        void onSuccess(RoomDto room);
        void onError(String error);
    }

    // ==================== DATA CLASS ====================

    public static class RoomDto {
        private String phongId;
        private String nhaTroId;
        private String tieuDe;
        private String moTa;
        private Double dienTich;
        private Long giaTien;
        private Long tienCoc;
        private Integer soNguoiToiDa;
        private String trangThai;
        private Double diemTrungBinh;
        private Integer soLuongDanhGia;
        private String createdAt;

        // Getters & Setters
        public String getPhongId() { return phongId; }
        public void setPhongId(String phongId) { this.phongId = phongId; }

        public String getNhaTroId() { return nhaTroId; }
        public void setNhaTroId(String nhaTroId) { this.nhaTroId = nhaTroId; }

        public String getTieuDe() { return tieuDe; }
        public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

        public String getMoTa() { return moTa; }
        public void setMoTa(String moTa) { this.moTa = moTa; }

        public Double getDienTich() { return dienTich; }
        public void setDienTich(Double dienTich) { this.dienTich = dienTich; }

        public Long getGiaTien() { return giaTien; }
        public void setGiaTien(Long giaTien) { this.giaTien = giaTien; }

        public Long getTienCoc() { return tienCoc; }
        public void setTienCoc(Long tienCoc) { this.tienCoc = tienCoc; }

        public Integer getSoNguoiToiDa() { return soNguoiToiDa; }
        public void setSoNguoiToiDa(Integer soNguoiToiDa) { this.soNguoiToiDa = soNguoiToiDa; }

        public String getTrangThai() { return trangThai; }
        public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

        public Double getDiemTrungBinh() { return diemTrungBinh; }
        public void setDiemTrungBinh(Double diemTrungBinh) { this.diemTrungBinh = diemTrungBinh; }

        public Integer getSoLuongDanhGia() { return soLuongDanhGia; }
        public void setSoLuongDanhGia(Integer soLuongDanhGia) { this.soLuongDanhGia = soLuongDanhGia; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    }
}

