package com.example.QuanLyPhongTro_App.data.model;

import java.util.Date;

public class DatPhong {
    private String datPhongId;
    private String phongId;
    private String nguoiThueId;
    private String loai;
    private Date batDau;
    private Date ketThuc;
    private Date thoiGianTao;
    private int trangThaiId;
    private String tapTinBienLaiId;
    private int soDatPhong;
    private String ghiChu;
    
    // Thông tin bổ sung (JOIN)
    private String tenPhong;
    private long giaPhong;
    private String tenNguoiThue;
    private String tenTrangThai;
    private String diaChiPhong;

    public DatPhong() {}

    // Getters and Setters
    public String getDatPhongId() { return datPhongId; }
    public void setDatPhongId(String datPhongId) { this.datPhongId = datPhongId; }

    public String getPhongId() { return phongId; }
    public void setPhongId(String phongId) { this.phongId = phongId; }

    public String getNguoiThueId() { return nguoiThueId; }
    public void setNguoiThueId(String nguoiThueId) { this.nguoiThueId = nguoiThueId; }

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

    public String getTapTinBienLaiId() { return tapTinBienLaiId; }
    public void setTapTinBienLaiId(String tapTinBienLaiId) { this.tapTinBienLaiId = tapTinBienLaiId; }

    public int getSoDatPhong() { return soDatPhong; }
    public void setSoDatPhong(int soDatPhong) { this.soDatPhong = soDatPhong; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getTenPhong() { return tenPhong; }
    public void setTenPhong(String tenPhong) { this.tenPhong = tenPhong; }

    public long getGiaPhong() { return giaPhong; }
    public void setGiaPhong(long giaPhong) { this.giaPhong = giaPhong; }

    public String getTenNguoiThue() { return tenNguoiThue; }
    public void setTenNguoiThue(String tenNguoiThue) { this.tenNguoiThue = tenNguoiThue; }

    public String getTenTrangThai() { return tenTrangThai; }
    public void setTenTrangThai(String tenTrangThai) { this.tenTrangThai = tenTrangThai; }

    public String getDiaChiPhong() { return diaChiPhong; }
    public void setDiaChiPhong(String diaChiPhong) { this.diaChiPhong = diaChiPhong; }
}
