package com.example.QuanLyPhongTro_App.data.model;

import java.util.Date;
import java.util.List;

public class Phong {
    private String phongId;
    private String nhaTroId;
    private String tieuDe;
    private double dienTich;
    private long giaTien;
    private long tienCoc;
    private int soNguoiToiDa;
    private String trangThai;
    private Date createdAt;
    private Date updatedAt;
    private float diemTrungBinh;
    private int soLuongDanhGia;
    private boolean isDuyet;
    private String nguoiDuyet;
    private Date thoiGianDuyet;
    private boolean isBiKhoa;
    private String moTa;
    private boolean isDeleted;
    
    // Thông tin bổ sung (JOIN)
    private String diaChiNhaTro;
    private String tenQuanHuyen;
    private String tenPhuong;
    private List<String> danhSachAnhUrl;
    private List<String> tienIch;

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
}
