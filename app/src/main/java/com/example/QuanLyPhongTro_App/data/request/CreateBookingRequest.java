package com.example.QuanLyPhongTro_App.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for POST /api/datphong
 * Note: naming uses PascalCase to match typical ASP.NET Core binding.
 */
public class CreateBookingRequest {

    @SerializedName("PhongId")
    public String phongId;

    @SerializedName("ChuTroId")
    public String chuTroId;

    @SerializedName("Loai")
    public String loai;

    @SerializedName("BatDau")
    public String batDau;

    @SerializedName("KetThuc")
    public String ketThuc;

    @SerializedName("GhiChu")
    public String ghiChu;

    public CreateBookingRequest() {}

    public CreateBookingRequest(String phongId, String chuTroId, String loai, String batDau, String ketThuc, String ghiChu) {
        this.phongId = phongId;
        this.chuTroId = chuTroId;
        this.loai = loai;
        this.batDau = batDau;
        this.ketThuc = ketThuc;
        this.ghiChu = ghiChu;
    }
}

