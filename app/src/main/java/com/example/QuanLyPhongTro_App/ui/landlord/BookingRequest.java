package com.example.QuanLyPhongTro_App.ui.landlord;

import java.io.Serializable;
import java.util.Date;

public class BookingRequest implements Serializable {
    private String datPhongId;
    private String phongId;
    private String nguoiThueId;
    private String chuTroId;
    private String tenNguoiThue;
    private String tenPhong;
    private String loai;
    private Date batDau;
    private Date ketThuc;
    private Date thoiGianTao;
    private int trangThaiId;
    private String tenTrangThai;
    private String ghiChu;
    private int soDatPhong;

    // Constructor
    public BookingRequest() {}

    public BookingRequest(String datPhongId, String phongId, String nguoiThueId, String chuTroId,
                         String tenNguoiThue, String tenPhong, String loai, Date batDau, Date ketThuc,
                         Date thoiGianTao, int trangThaiId, String tenTrangThai, String ghiChu, int soDatPhong) {
        this.datPhongId = datPhongId;
        this.phongId = phongId;
        this.nguoiThueId = nguoiThueId;
        this.chuTroId = chuTroId;
        this.tenNguoiThue = tenNguoiThue;
        this.tenPhong = tenPhong;
        this.loai = loai;
        this.batDau = batDau;
        this.ketThuc = ketThuc;
        this.thoiGianTao = thoiGianTao;
        this.trangThaiId = trangThaiId;
        this.tenTrangThai = tenTrangThai;
        this.ghiChu = ghiChu;
        this.soDatPhong = soDatPhong;
    }

    // Getters and Setters
    public String getDatPhongId() { return datPhongId; }
    public void setDatPhongId(String datPhongId) { this.datPhongId = datPhongId; }

    public String getPhongId() { return phongId; }
    public void setPhongId(String phongId) { this.phongId = phongId; }

    public String getNguoiThueId() { return nguoiThueId; }
    public void setNguoiThueId(String nguoiThueId) { this.nguoiThueId = nguoiThueId; }

    public String getChuTroId() { return chuTroId; }
    public void setChuTroId(String chuTroId) { this.chuTroId = chuTroId; }

    public String getTenNguoiThue() { return tenNguoiThue; }
    public void setTenNguoiThue(String tenNguoiThue) { this.tenNguoiThue = tenNguoiThue; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public String getLoai() { return loai; }
    public void setLoai(String loai) { this.loai = loai; }

    public Date getBatDau() { return batDau; }
    public void setBatDau(Date batDau) { this.batDau = batDau; }

    public Date getKetThuc() { return ketThuc; }
    public void setKetThuc(Date ketThuc) { this.ketThuc = ketThuc; }

    public Date getThoiGianTao() { return thoiGianTao; }
    public void setThoiGianTao(Date thoiGianTao) { this.thoiGianTao = thoiGianTao; }

    public int getTrangThaiId() { return trangThaiId; }
    public void setTrangThaiId(int trangThaiId) { this.trangThaiId = trangThaiId; }

    public String getTenTrangThai() { return tenTrangThai; }
    public void setTenTrangThai(String tenTrangThai) { this.tenTrangThai = tenTrangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public int getSoDatPhong() { return soDatPhong; }
    public void setSoDatPhong(int soDatPhong) { this.soDatPhong = soDatPhong; }

    // Helper methods
    public boolean isChoXacNhan() {
        return "ChoXacNhan".equals(tenTrangThai);
    }

    public boolean isDaXacNhan() {
        return "DaXacNhan".equals(tenTrangThai);
    }

    public boolean isDaHuy() {
        return "DaHuy".equals(tenTrangThai);
    }
}