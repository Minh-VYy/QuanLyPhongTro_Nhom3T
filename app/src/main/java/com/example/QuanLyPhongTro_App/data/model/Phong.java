package com.example.QuanLyPhongTro_App.data.model;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Phong {
    @SerializedName(value = "PhongId", alternate = {"phongId"})
    private String phongId;

    @SerializedName(value = "NhaTroId", alternate = {"nhaTroId"})
    private String nhaTroId;

    @SerializedName(value = "TieuDe", alternate = {"tieuDe"})
    private String tieuDe;

    @SerializedName(value = "DienTich", alternate = {"dienTich"})
    private double dienTich;

    @SerializedName(value = "GiaTien", alternate = {"giaTien"})
    private long giaTien;

    @SerializedName(value = "TienCoc", alternate = {"tienCoc"})
    private Long tienCoc;

    @SerializedName(value = "SoNguoiToiDa", alternate = {"soNguoiToiDa"})
    private int soNguoiToiDa;

    @SerializedName(value = "TrangThai", alternate = {"trangThai"})
    private String trangThai;

    @SerializedName(value = "CreatedAt", alternate = {"createdAt"})
    private Date createdAt;

    @SerializedName(value = "UpdatedAt", alternate = {"updatedAt"})
    private Date updatedAt;

    @SerializedName(value = "DiemTrungBinh", alternate = {"diemTrungBinh"})
    private float diemTrungBinh;

    @SerializedName(value = "SoLuongDanhGia", alternate = {"soLuongDanhGia"})
    private int soLuongDanhGia;

    @SerializedName(value = "IsDuyet", alternate = {"isDuyet"})
    private boolean isDuyet;

    @SerializedName(value = "NguoiDuyet", alternate = {"nguoiDuyet"})
    private String nguoiDuyet;

    @SerializedName(value = "ThoiGianDuyet", alternate = {"thoiGianDuyet"})
    private Date thoiGianDuyet;

    @SerializedName(value = "IsBiKhoa", alternate = {"isBiKhoa"})
    private boolean isBiKhoa;

    @SerializedName(value = "MoTa", alternate = {"moTa"})
    private String moTa;

    @SerializedName(value = "IsDeleted", alternate = {"isDeleted"})
    private boolean isDeleted;

    // Thông tin bổ sung (JOIN)
    @SerializedName(value = "DiaChiNhaTro", alternate = {"diaChiNhaTro"})
    private String diaChiNhaTro;

    @SerializedName(value = "TenQuanHuyen", alternate = {"tenQuanHuyen"})
    private String tenQuanHuyen;

    @SerializedName(value = "TenPhuong", alternate = {"tenPhuong"})
    private String tenPhuong;

    @SerializedName(value = "DanhSachAnhUrl", alternate = {"danhSachAnhUrl"})
    private List<String> danhSachAnhUrl;

    @SerializedName(value = "TienIch", alternate = {"tienIch"})
    private List<String> tienIch;

    @com.google.gson.annotations.SerializedName(value = "AnhDaiDien", alternate = {"anhDaiDien", "DuongDan", "duongDan"})
    private String anhDaiDien;

    public Phong() {}

    // Getters and Setters
    public String getPhongId() { return phongId; }
    public void setPhongId(String phongId) { this.phongId = phongId; }

    public String getNhaTroId() { return nhaTroId; }
    public void setNhaTroId(String nhaTroId) { this.nhaTroId = nhaTroId; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public double getDienTich() { return dienTich; }
    public void setDienTich(double dienTich) { this.dienTich = dienTich; }

    public long getGiaTien() { return giaTien; }
    public void setGiaTien(long giaTien) { this.giaTien = giaTien; }

    public long getTienCoc() { return tienCoc; }
    public void setTienCoc(long tienCoc) { this.tienCoc = tienCoc; }

    public int getSoNguoiToiDa() { return soNguoiToiDa; }
    public void setSoNguoiToiDa(int soNguoiToiDa) { this.soNguoiToiDa = soNguoiToiDa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    public Date getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }

    public float getDiemTrungBinh() { return diemTrungBinh; }
    public void setDiemTrungBinh(float diemTrungBinh) { this.diemTrungBinh = diemTrungBinh; }

    public int getSoLuongDanhGia() { return soLuongDanhGia; }
    public void setSoLuongDanhGia(int soLuongDanhGia) { this.soLuongDanhGia = soLuongDanhGia; }

    public boolean isDuyet() { return isDuyet; }
    public void setDuyet(boolean duyet) { isDuyet = duyet; }

    public String getNguoiDuyet() { return nguoiDuyet; }
    public void setNguoiDuyet(String nguoiDuyet) { this.nguoiDuyet = nguoiDuyet; }

    public Date getThoiGianDuyet() { return thoiGianDuyet; }
    public void setThoiGianDuyet(Date thoiGianDuyet) { this.thoiGianDuyet = thoiGianDuyet; }

    public boolean isBiKhoa() { return isBiKhoa; }
    public void setBiKhoa(boolean biKhoa) { isBiKhoa = biKhoa; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public boolean isDeleted() { return isDeleted; }
    public void setDeleted(boolean deleted) { isDeleted = deleted; }

    public String getDiaChiNhaTro() { return diaChiNhaTro; }
    public void setDiaChiNhaTro(String diaChiNhaTro) { this.diaChiNhaTro = diaChiNhaTro; }

    public String getTenQuanHuyen() { return tenQuanHuyen; }
    public void setTenQuanHuyen(String tenQuanHuyen) { this.tenQuanHuyen = tenQuanHuyen; }

    public String getTenPhuong() { return tenPhuong; }
    public void setTenPhuong(String tenPhuong) { this.tenPhuong = tenPhuong; }

    public List<String> getDanhSachAnhUrl() { return danhSachAnhUrl; }
    public void setDanhSachAnhUrl(List<String> danhSachAnhUrl) { this.danhSachAnhUrl = danhSachAnhUrl; }

    public List<String> getTienIch() { return tienIch; }
    public void setTienIch(List<String> tienIch) { this.tienIch = tienIch; }

    public String getAnhDaiDien() { return anhDaiDien; }
    public void setAnhDaiDien(String anhDaiDien) { this.anhDaiDien = anhDaiDien; }
}
