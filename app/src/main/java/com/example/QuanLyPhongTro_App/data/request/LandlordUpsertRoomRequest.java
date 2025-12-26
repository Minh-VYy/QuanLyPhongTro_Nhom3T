package com.example.QuanLyPhongTro_App.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for landlord create/update room.
 * Swagger: CreatePhongRequest requires NhaTroId + GiaTien.
 */
public class LandlordUpsertRoomRequest {
    @SerializedName(value = "NhaTroId", alternate = {"nhaTroId"})
    public String nhaTroId;

    @SerializedName(value = "TieuDe", alternate = {"tieuDe"})
    public String tieuDe;

    @SerializedName(value = "GiaTien", alternate = {"giaTien"})
    public long giaTien;

    @SerializedName(value = "MoTa", alternate = {"moTa"})
    public String moTa;

    @SerializedName(value = "DienTich", alternate = {"dienTich"})
    public double dienTich;

    @SerializedName(value = "TienCoc", alternate = {"tienCoc"})
    public Long tienCoc;

    @SerializedName(value = "SoNguoiToiDa", alternate = {"soNguoiToiDa"})
    public int soNguoiToiDa;

    public LandlordUpsertRoomRequest(String nhaTroId,
                                    String tieuDe,
                                    long giaTien,
                                    String moTa,
                                    double dienTich,
                                    Long tienCoc,
                                    int soNguoiToiDa) {
        this.nhaTroId = nhaTroId;
        this.tieuDe = tieuDe;
        this.giaTien = giaTien;
        this.moTa = moTa;
        this.dienTich = dienTich;
        this.tienCoc = tienCoc;
        this.soNguoiToiDa = soNguoiToiDa;
    }

    /**
     * Backward compatible ctor (keeps existing call sites compiling).
     * Uses safe defaults aligned with old DB scripts.
     */
    public LandlordUpsertRoomRequest(String nhaTroId, String tieuDe, long giaTien, String moTa) {
        this(nhaTroId, tieuDe, giaTien, moTa,
                25.0, // default diện tích
                giaTien, // default tiền cọc = giá
                2 // default số người tối đa
        );
    }
}
