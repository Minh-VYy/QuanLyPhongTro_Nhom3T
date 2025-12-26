package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Backward/forward-compatible response for GET /api/datphong/my-bookings.
 * Some backends return wrapped { success, Data: [...] }, others may return a plain array.
 */
public class MyBookingsResponse extends GenericResponse<List<MyBookingsResponse.MyBookingDto>> {
    // no extra fields

    public static class MyBookingDto {
        // Try to cover typical naming conventions from C# (PascalCase) and possible camelCase.
        @SerializedName(value = "DatPhongId", alternate = {"datPhongId", "id"})
        public String datPhongId;

        @SerializedName(value = "PhongId", alternate = {"phongId"})
        public String phongId;

        @SerializedName(value = "TenPhong", alternate = {"tenPhong", "roomName"})
        public String tenPhong;

        @SerializedName(value = "DiaChiPhong", alternate = {"diaChiPhong", "address"})
        public String diaChiPhong;

        @SerializedName(value = "GiaPhong", alternate = {"giaPhong", "price"})
        public Long giaPhong;

        @SerializedName(value = "TenNguoiThue", alternate = {"tenNguoiThue", "tenantName"})
        public String tenNguoiThue;

        @SerializedName(value = "BatDau", alternate = {"batDau", "startTime"})
        public String batDau;

        @SerializedName(value = "KetThuc", alternate = {"ketThuc", "endTime"})
        public String ketThuc;

        @SerializedName(value = "TrangThaiId", alternate = {"trangThaiId", "statusId"})
        public Integer trangThaiId;

        @SerializedName(value = "GhiChu", alternate = {"ghiChu", "note"})
        public String ghiChu;

        @SerializedName(value = "ThoiGianTao", alternate = {"thoiGianTao", "createdAt"})
        public String thoiGianTao;
    }
}
