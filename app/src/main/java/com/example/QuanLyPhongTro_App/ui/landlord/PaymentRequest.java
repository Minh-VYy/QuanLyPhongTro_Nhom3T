package com.example.QuanLyPhongTro_App.ui.landlord;

import java.io.Serializable;
import java.util.Date;

public class PaymentRequest implements Serializable {
    private String bienLaiId;
    private String datPhongId;
    private String nguoiThueId;
    private String chuTroId;
    private String tenNguoiThue;
    private String tenPhong;
    private long soTien;
    private String loaiThanhToan;
    private Date ngayTao;
    private Date ngayThanhToan;
    private String trangThai;
    private String ghiChu;
    private String tapTinId; // File đính kèm nếu có

    // Constructor
    public PaymentRequest() {}

    // Getters and Setters
    public String getBienLaiId() { return bienLaiId; }
    public void setBienLaiId(String bienLaiId) { this.bienLaiId = bienLaiId; }

    public String getDatPhongId() { return datPhongId; }
    public void setDatPhongId(String datPhongId) { this.datPhongId = datPhongId; }

    public String getNguoiThueId() { return nguoiThueId; }
    public void setNguoiThueId(String nguoiThueId) { this.nguoiThueId = nguoiThueId; }

    public String getChuTroId() { return chuTroId; }
    public void setChuTroId(String chuTroId) { this.chuTroId = chuTroId; }

    public String getTenNguoiThue() { return tenNguoiThue; }
    public void setTenNguoiThue(String tenNguoiThue) { this.tenNguoiThue = tenNguoiThue; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public long getSoTien() { return soTien; }
    public void setSoTien(long soTien) { this.soTien = soTien; }

    public String getLoaiThanhToan() { return loaiThanhToan; }
    public void setLoaiThanhToan(String loaiThanhToan) { this.loaiThanhToan = loaiThanhToan; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(Date ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTapTinId() { return tapTinId; }
    public void setTapTinId(String tapTinId) { this.tapTinId = tapTinId; }

    // Helper methods
    public String getFormattedAmount() {
        return String.format("%,d đ", soTien);
    }

    public boolean isChoXacNhan() {
        return "ChoXacNhan".equals(trangThai);
    }

    public boolean isDaXacNhan() {
        return "DaXacNhan".equals(trangThai);
    }

    public boolean isDaHuy() {
        return "DaHuy".equals(trangThai);
    }
}