package com.example.QuanLyPhongTro_App.data.model;

import java.util.Date;

public class YeuCauHoTro {
    private String hoTroId;
    private String phongId;
    private String nguoiYeuCau;
    private int loaiHoTroId;
    private String tieuDe;
    private String moTa;
    private String trangThai;
    private Date thoiGianTao;
    
    // Thông tin bổ sung (JOIN)
    private String tenLoaiHoTro;
    private String tenNguoiYeuCau;
    private String tenPhong;

    public YeuCauHoTro() {}

    // Getters and Setters
    public String getHoTroId() { return hoTroId; }
    public void setHoTroId(String hoTroId) { this.hoTroId = hoTroId; }

    public String getPhongId() { return phongId; }
    public void setPhongId(String phongId) { this.phongId = phongId; }

    public String getNguoiYeuCau() { return nguoiYeuCau; }
    public void setNguoiYeuCau(String nguoiYeuCau) { this.nguoiYeuCau = nguoiYeuCau; }

    public int getLoaiHoTroId() { return loaiHoTroId; }
    public void setLoaiHoTroId(int loaiHoTroId) { this.loaiHoTroId = loaiHoTroId; }

    public String getTieuDe() { return tieuDe; }
    public void setTieuDe(String tieuDe) { this.tieuDe = tieuDe; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public Date getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(Date thoiGianTao) { this.thoiGianTao = thoiGianTao; }

    public String getTenLoaiHoTro() { return tenLoaiHoTro; }
    public void setTenLoaiHoTro(String tenLoaiHoTro) { this.tenLoaiHoTro = tenLoaiHoTro; }

    public String getTenNguoiYeuCau() { return tenNguoiYeuCau; }
    public void setTenNguoiYeuCau(String tenNguoiYeuCau) { this.tenNguoiYeuCau = tenNguoiYeuCau; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }
}
