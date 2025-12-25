package com.example.QuanLyPhongTro_App.data.dao;

import android.util.Log;

import com.example.QuanLyPhongTro_App.data.model.Phong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PhongDao {
    private static final String TAG = "PhongDao";

    /**
     * Lấy danh sách phòng đã duyệt và chưa bị khóa (cho người thuê)
     */
    public List<Phong> getAllPhongAvailable(Connection conn) {
        List<Phong> list = new ArrayList<>();
        String query = "SELECT p.PhongId, p.TieuDe, p.DienTich, p.GiaTien, p.TienCoc, " +
                      "p.SoNguoiToiDa, p.TrangThai, p.DiemTrungBinh, p.SoLuongDanhGia, p.MoTa, " +
                      "nt.DiaChi, qh.Ten AS QuanHuyen, ph.Ten AS Phuong, " +
                      "(SELECT TOP 1 t.DuongDan FROM PhongAnh pa " +
                      " JOIN TapTin t ON pa.TapTinId = t.TapTinId " +
                      " WHERE pa.PhongId = p.PhongId ORDER BY pa.ThuTu) AS AnhDaiDien " +
                      "FROM Phong p " +
                      "JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "LEFT JOIN QuanHuyen qh ON nt.QuanHuyenId = qh.QuanHuyenId " +
                      "LEFT JOIN Phuong ph ON nt.PhuongId = ph.PhuongId " +
                      "WHERE p.IsDuyet = 1 AND p.IsBiKhoa = 0 AND p.IsDeleted = 0 " +
                      "ORDER BY p.CreatedAt DESC";

        Log.d(TAG, "Executing query: " + query);
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            int count = 0;
            while (rs.next()) {
                try {
                    Phong phong = new Phong();
                    phong.setPhongId(rs.getString("PhongId"));
                    phong.setTieuDe(rs.getString("TieuDe"));
                    phong.setDienTich(rs.getDouble("DienTich"));
                    phong.setGiaTien(rs.getLong("GiaTien"));
                    phong.setTienCoc(rs.getLong("TienCoc"));
                    phong.setSoNguoiToiDa(rs.getInt("SoNguoiToiDa"));
                    phong.setTrangThai(rs.getString("TrangThai"));
                    
                    // Handle null values
                    float diemTB = rs.getFloat("DiemTrungBinh");
                    phong.setDiemTrungBinh(rs.wasNull() ? 0 : diemTB);
                    
                    phong.setSoLuongDanhGia(rs.getInt("SoLuongDanhGia"));
                    phong.setMoTa(rs.getString("MoTa"));
                    phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                    phong.setTenQuanHuyen(rs.getString("QuanHuyen"));
                    phong.setTenPhuong(rs.getString("Phuong"));
                    
                    // Lấy ảnh đại diện
                    List<String> anhList = new ArrayList<>();
                    String anhDaiDien = rs.getString("AnhDaiDien");
                    if (anhDaiDien != null && !anhDaiDien.isEmpty()) {
                        anhList.add(anhDaiDien);
                    }
                    phong.setDanhSachAnhUrl(anhList);
                    
                    list.add(phong);
                    count++;
                    
                    Log.d(TAG, "Loaded phong #" + count + ": " + phong.getTieuDe() + 
                          " - " + phong.getGiaTien() + " VND");
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing phong row: " + e.getMessage(), e);
                }
            }
            
            Log.d(TAG, "✅ Successfully loaded " + list.size() + " phòng from database");
            
            if (list.isEmpty()) {
                Log.w(TAG, "⚠️ No rooms found! Check if data exists in database with IsDuyet=1, IsBiKhoa=0, IsDeleted=0");
            }
            
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error loading phòng: " + e.getMessage(), e);
            Log.e(TAG, "SQL State: " + e.getSQLState());
            Log.e(TAG, "Error Code: " + e.getErrorCode());
        }
        return list;
    }

    /**
     * Lấy chi tiết phòng theo ID
     */
    public Phong getPhongById(Connection conn, String phongId) {
        String query = "SELECT p.*, nt.DiaChi, qh.Ten AS QuanHuyen, ph.Ten AS Phuong " +
                      "FROM Phong p " +
                      "JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "LEFT JOIN QuanHuyen qh ON nt.QuanHuyenId = qh.QuanHuyenId " +
                      "LEFT JOIN Phuong ph ON nt.PhuongId = ph.PhuongId " +
                      "WHERE p.PhongId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phongId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Phong phong = new Phong();
                phong.setPhongId(rs.getString("PhongId"));
                phong.setNhaTroId(rs.getString("NhaTroId"));
                phong.setTieuDe(rs.getString("TieuDe"));
                phong.setDienTich(rs.getDouble("DienTich"));
                phong.setGiaTien(rs.getLong("GiaTien"));
                phong.setTienCoc(rs.getLong("TienCoc"));
                phong.setSoNguoiToiDa(rs.getInt("SoNguoiToiDa"));
                phong.setTrangThai(rs.getString("TrangThai"));
                phong.setDiemTrungBinh(rs.getFloat("DiemTrungBinh"));
                phong.setSoLuongDanhGia(rs.getInt("SoLuongDanhGia"));
                phong.setMoTa(rs.getString("MoTa"));
                phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                phong.setTenQuanHuyen(rs.getString("QuanHuyen"));
                phong.setTenPhuong(rs.getString("Phuong"));
                
                // Load tất cả ảnh
                phong.setDanhSachAnhUrl(getPhongImages(conn, phongId));
                
                // Load tiện ích
                phong.setTienIch(getPhongTienIch(conn, phongId));
                
                return phong;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error loading phòng detail: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Lấy danh sách ảnh của phòng
     */
    private List<String> getPhongImages(Connection conn, String phongId) {
        List<String> images = new ArrayList<>();
        String query = "SELECT t.DuongDan FROM PhongAnh pa " +
                      "JOIN TapTin t ON pa.TapTinId = t.TapTinId " +
                      "WHERE pa.PhongId = ? ORDER BY pa.ThuTu";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phongId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("DuongDan"));
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error loading images: " + e.getMessage());
        }
        return images;
    }

    /**
     * Lấy danh sách tiện ích của phòng
     */
    private List<String> getPhongTienIch(Connection conn, String phongId) {
        List<String> tienIch = new ArrayList<>();
        String query = "SELECT ti.Ten FROM PhongTienIch pti " +
                      "JOIN TienIch ti ON pti.TienIchId = ti.TienIchId " +
                      "WHERE pti.PhongId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phongId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                tienIch.add(rs.getString("Ten"));
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error loading tien ich: " + e.getMessage());
        }
        return tienIch;
    }

    /**
     * Tìm kiếm phòng theo điều kiện
     */
    public List<Phong> searchPhong(Connection conn, String keyword, Long minPrice, Long maxPrice, String quanHuyen) {
        List<Phong> list = new ArrayList<>();
        StringBuilder query = new StringBuilder(
            "SELECT p.PhongId, p.TieuDe, p.DienTich, p.GiaTien, p.TienCoc, " +
            "p.SoNguoiToiDa, p.TrangThai, p.DiemTrungBinh, p.SoLuongDanhGia, p.MoTa, " +
            "nt.DiaChi, qh.Ten AS QuanHuyen, ph.Ten AS Phuong, " +
            "(SELECT TOP 1 t.DuongDan FROM PhongAnh pa " +
            " JOIN TapTin t ON pa.TapTinId = t.TapTinId " +
            " WHERE pa.PhongId = p.PhongId ORDER BY pa.ThuTu) AS AnhDaiDien " +
            "FROM Phong p " +
            "JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
            "LEFT JOIN QuanHuyen qh ON nt.QuanHuyenId = qh.QuanHuyenId " +
            "LEFT JOIN Phuong ph ON nt.PhuongId = ph.PhuongId " +
            "WHERE p.IsDuyet = 1 AND p.IsBiKhoa = 0 AND p.IsDeleted = 0 "
        );

        List<Object> params = new ArrayList<>();
        
        // Tìm kiếm theo từ khóa (tiêu đề, mô tả, địa chỉ, quận, phường)
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append("AND (p.TieuDe COLLATE Latin1_General_CI_AI LIKE ? " +
                        "OR p.MoTa COLLATE Latin1_General_CI_AI LIKE ? " +
                        "OR nt.DiaChi COLLATE Latin1_General_CI_AI LIKE ? " +
                        "OR qh.Ten COLLATE Latin1_General_CI_AI LIKE ? " +
                        "OR ph.Ten COLLATE Latin1_General_CI_AI LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
            
            Log.d(TAG, "Searching with keyword: " + keyword);
        }
        
        if (minPrice != null) {
            query.append("AND p.GiaTien >= ? ");
            params.add(minPrice);
            Log.d(TAG, "Min price: " + minPrice);
        }
        
        if (maxPrice != null) {
            query.append("AND p.GiaTien <= ? ");
            params.add(maxPrice);
            Log.d(TAG, "Max price: " + maxPrice);
        }
        
        if (quanHuyen != null && !quanHuyen.trim().isEmpty()) {
            query.append("AND qh.Ten = ? ");
            params.add(quanHuyen);
            Log.d(TAG, "District: " + quanHuyen);
        }
        
        query.append("ORDER BY p.CreatedAt DESC");

        Log.d(TAG, "Executing search query: " + query.toString());

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                try {
                    Phong phong = new Phong();
                    phong.setPhongId(rs.getString("PhongId"));
                    phong.setTieuDe(rs.getString("TieuDe"));
                    phong.setDienTich(rs.getDouble("DienTich"));
                    phong.setGiaTien(rs.getLong("GiaTien"));
                    phong.setTienCoc(rs.getLong("TienCoc"));
                    phong.setSoNguoiToiDa(rs.getInt("SoNguoiToiDa"));
                    phong.setTrangThai(rs.getString("TrangThai"));
                    
                    float diemTB = rs.getFloat("DiemTrungBinh");
                    phong.setDiemTrungBinh(rs.wasNull() ? 0 : diemTB);
                    
                    phong.setSoLuongDanhGia(rs.getInt("SoLuongDanhGia"));
                    phong.setMoTa(rs.getString("MoTa"));
                    phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                    phong.setTenQuanHuyen(rs.getString("QuanHuyen"));
                    phong.setTenPhuong(rs.getString("Phuong"));
                    
                    List<String> anhList = new ArrayList<>();
                    String anhDaiDien = rs.getString("AnhDaiDien");
                    if (anhDaiDien != null && !anhDaiDien.isEmpty()) {
                        anhList.add(anhDaiDien);
                    }
                    phong.setDanhSachAnhUrl(anhList);
                    
                    list.add(phong);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing search result row: " + e.getMessage(), e);
                }
            }
            Log.d(TAG, "✅ Search found " + list.size() + " phòng");
        } catch (SQLException e) {
            Log.e(TAG, "❌ Error searching phòng: " + e.getMessage(), e);
            Log.e(TAG, "SQL State: " + e.getSQLState());
            Log.e(TAG, "Error Code: " + e.getErrorCode());
        }
        return list;
    }
}
