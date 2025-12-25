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
        
        // Simplified query - remove problematic JOINs for now
        String query = "SELECT p.PhongId, p.TieuDe, p.DienTich, p.GiaTien, p.TienCoc, " +
                      "p.SoNguoiToiDa, p.TrangThai, p.DiemTrungBinh, p.SoLuongDanhGia, p.MoTa, " +
                      "nt.DiaChi " +
                      "FROM Phong p " +
                      "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "WHERE p.IsDuyet = 1 AND p.IsBiKhoa = 0 AND p.IsDeleted = 0 " +
                      "ORDER BY p.CreatedAt DESC";

        Log.d(TAG, "Executing simplified query: " + query);
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            int count = 0;
            Log.d(TAG, "Starting to process ResultSet...");
            
            while (rs.next()) {
                try {
                    count++;
                    Log.d(TAG, "Processing row #" + count);
                    
                    Phong phong = new Phong();
                    
                    // Get basic fields with null checks
                    String phongId = rs.getString("PhongId");
                    String tieuDe = rs.getString("TieuDe");
                    Log.d(TAG, "Row #" + count + " - PhongId: " + phongId + ", TieuDe: " + tieuDe);
                    
                    phong.setPhongId(phongId);
                    phong.setTieuDe(tieuDe != null ? tieuDe : "Chưa có tiêu đề");
                    
                    // Handle numeric fields safely
                    try {
                        phong.setDienTich(rs.getDouble("DienTich"));
                    } catch (Exception e) {
                        Log.w(TAG, "Error getting DienTich for row " + count + ": " + e.getMessage());
                        phong.setDienTich(0.0);
                    }
                    
                    try {
                        phong.setGiaTien(rs.getLong("GiaTien"));
                    } catch (Exception e) {
                        Log.w(TAG, "Error getting GiaTien for row " + count + ": " + e.getMessage());
                        phong.setGiaTien(0L);
                    }
                    
                    try {
                        phong.setTienCoc(rs.getLong("TienCoc"));
                    } catch (Exception e) {
                        Log.w(TAG, "Error getting TienCoc for row " + count + ": " + e.getMessage());
                        phong.setTienCoc(0L);
                    }
                    
                    phong.setSoNguoiToiDa(rs.getInt("SoNguoiToiDa"));
                    phong.setTrangThai(rs.getString("TrangThai"));
                    
                    // Handle null values for rating
                    float diemTB = rs.getFloat("DiemTrungBinh");
                    phong.setDiemTrungBinh(rs.wasNull() ? 0 : diemTB);
                    
                    phong.setSoLuongDanhGia(rs.getInt("SoLuongDanhGia"));
                    phong.setMoTa(rs.getString("MoTa"));
                    phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                    
                    // Set default values for missing fields
                    phong.setTenQuanHuyen("Chưa xác định");
                    phong.setTenPhuong("Chưa xác định");
                    
                    // Set empty image list for now
                    phong.setDanhSachAnhUrl(new ArrayList<>());
                    
                    list.add(phong);
                    Log.d(TAG, "Successfully added phong #" + count + ": " + phong.getTieuDe() + 
                          " - " + phong.getGiaTien() + " VND");
                          
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing phong row #" + count + ": " + e.getMessage(), e);
                    // Continue processing other rows
                }
            }
            
            Log.d(TAG, "✅ Successfully processed " + count + " rows, added " + list.size() + " phòng to list");
            
            if (list.isEmpty()) {
                Log.w(TAG, "⚠️ No rooms added to list! Check parsing logic or data format");
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
     * Lấy danh sách phòng của chủ trọ theo ChuTroId (không bao gồm phòng đã xóa)
     */
    public List<Phong> getPhongByChuTroId(Connection conn, String chuTroId) {
        List<Phong> list = new ArrayList<>();
        
        String query = "SELECT p.PhongId, p.TieuDe, p.DienTich, p.GiaTien, p.TienCoc, " +
                      "p.SoNguoiToiDa, p.TrangThai, p.DiemTrungBinh, p.SoLuongDanhGia, " +
                      "p.IsDuyet, p.IsBiKhoa, " +
                      "nt.DiaChi " +
                      "FROM Phong p " +
                      "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "WHERE nt.ChuTroId = ? AND p.IsDeleted = 0 " +
                      "ORDER BY p.CreatedAt DESC";

        Log.d(TAG, "Executing landlord query for ChuTroId: " + chuTroId);
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                Log.d(TAG, "Starting to process landlord ResultSet...");
                
                while (rs.next()) {
                    try {
                        count++;
                        Log.d(TAG, "Processing landlord row #" + count);
                        
                        Phong phong = new Phong();
                        
                        // Get basic fields with null checks
                        String phongId = rs.getString("PhongId");
                        String tieuDe = rs.getString("TieuDe");
                        Log.d(TAG, "Row #" + count + " - PhongId: " + phongId + ", TieuDe: " + tieuDe);
                        
                        phong.setPhongId(phongId);
                        phong.setTieuDe(tieuDe != null ? tieuDe : "Chưa có tiêu đề");
                        
                        // Handle numeric fields safely
                        try {
                            phong.setDienTich(rs.getDouble("DienTich"));
                        } catch (Exception e) {
                            Log.w(TAG, "Error getting DienTich for row " + count + ": " + e.getMessage());
                            phong.setDienTich(0.0);
                        }
                        
                        try {
                            phong.setGiaTien(rs.getLong("GiaTien"));
                        } catch (Exception e) {
                            Log.w(TAG, "Error getting GiaTien for row " + count + ": " + e.getMessage());
                            phong.setGiaTien(0L);
                        }
                        
                        try {
                            phong.setTienCoc(rs.getLong("TienCoc"));
                        } catch (Exception e) {
                            Log.w(TAG, "Error getting TienCoc for row " + count + ": " + e.getMessage());
                            phong.setTienCoc(0L);
                        }
                        
                        phong.setSoNguoiToiDa(rs.getInt("SoNguoiToiDa"));
                        phong.setTrangThai(rs.getString("TrangThai"));
                        
                        // Handle null values for rating
                        try {
                            float diemTB = rs.getFloat("DiemTrungBinh");
                            phong.setDiemTrungBinh(rs.wasNull() ? 0 : diemTB);
                        } catch (Exception e) {
                            phong.setDiemTrungBinh(0);
                        }
                        
                        try {
                            phong.setSoLuongDanhGia(rs.getInt("SoLuongDanhGia"));
                        } catch (Exception e) {
                            phong.setSoLuongDanhGia(0);
                        }
                        
                        phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                        
                        // Get actual status from database
                        try {
                            phong.setDuyet(rs.getBoolean("IsDuyet"));
                        } catch (Exception e) {
                            phong.setDuyet(true);  // Mặc định là đã duyệt
                        }
                        
                        try {
                            phong.setBiKhoa(rs.getBoolean("IsBiKhoa"));
                        } catch (Exception e) {
                            phong.setBiKhoa(false); // Mặc định không bị khóa
                        }
                        
                        phong.setDeleted(false); // Đã lọc IsDeleted = 0 trong query
                        phong.setMoTa(""); // Mặc định mô tả trống
                        
                        // Set default values for missing fields
                        phong.setTenQuanHuyen("Chưa xác định");
                        phong.setTenPhuong("Chưa xác định");
                        
                        // Set empty image list for now
                        phong.setDanhSachAnhUrl(new ArrayList<>());
                        
                        list.add(phong);
                        Log.d(TAG, "Successfully added landlord phong #" + count + ": " + phong.getTieuDe() + 
                              " - " + phong.getGiaTien() + " VND - Status: " + phong.getTrangThai() +
                              " - Duyet: " + phong.isDuyet() + " - BiKhoa: " + phong.isBiKhoa());
                              
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing landlord phong row #" + count + ": " + e.getMessage(), e);
                        // Continue processing other rows
                    }
                }
                
                Log.d(TAG, "✅ Successfully processed " + count + " landlord rows, added " + list.size() + " phòng to list");
                
                if (list.isEmpty()) {
                    Log.w(TAG, "⚠️ No active rooms found for ChuTroId: " + chuTroId);
                }
                
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error loading phòng for landlord: " + e.getMessage(), e);
            Log.e(TAG, "SQL State: " + e.getSQLState());
            Log.e(TAG, "Error Code: " + e.getErrorCode());
        }
        return list;
    }
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
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            query.append("AND (p.TieuDe LIKE ? OR p.MoTa LIKE ? OR nt.DiaChi LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (minPrice != null) {
            query.append("AND p.GiaTien >= ? ");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            query.append("AND p.GiaTien <= ? ");
            params.add(maxPrice);
        }
        
        if (quanHuyen != null && !quanHuyen.trim().isEmpty()) {
            query.append("AND qh.Ten = ? ");
            params.add(quanHuyen);
        }
        
        query.append("ORDER BY p.CreatedAt DESC");

        try (PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Phong phong = new Phong();
                phong.setPhongId(rs.getString("PhongId"));
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
                
                List<String> anhList = new ArrayList<>();
                String anhDaiDien = rs.getString("AnhDaiDien");
                if (anhDaiDien != null) {
                    anhList.add(anhDaiDien);
                }
                phong.setDanhSachAnhUrl(anhList);
                
                list.add(phong);
            }
            Log.d(TAG, "Search found " + list.size() + " phòng");
        } catch (SQLException e) {
            Log.e(TAG, "Error searching phòng: " + e.getMessage(), e);
        }
        return list;
    }
}
