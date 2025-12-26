package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;

import com.example.QuanLyPhongTro_App.data.model.Phong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LandlordPhongDao {
    private static final String TAG = "LandlordPhongDao";

    /**
     * Lấy danh sách phòng của chủ trọ theo ChuTroId
     */
    public List<Phong> getPhongByChuTroId(Connection conn, String chuTroId) {
        List<Phong> list = new ArrayList<>();
        
        String query = "SELECT p.PhongId, p.TieuDe, p.DienTich, p.GiaTien, p.TienCoc, " +
                      "p.SoNguoiToiDa, p.TrangThai, p.DiemTrungBinh, p.SoLuongDanhGia, " +
                      "p.IsDuyet, p.IsBiKhoa, p.IsDeleted, p.CreatedAt, " +
                      "nt.DiaChi, nt.TenNhaTro " +
                      "FROM Phong p " +
                      "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "WHERE nt.ChuTroId = ? AND p.IsDeleted = 0 " +
                      "ORDER BY p.CreatedAt DESC";

        Log.d(TAG, "Executing query for ChuTroId: " + chuTroId);
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                Log.d(TAG, "Starting to process ResultSet...");
                
                while (rs.next()) {
                    try {
                        count++;
                        Log.d(TAG, "Processing row #" + count);
                        
                        Phong phong = new Phong();
                        
                        // Get basic fields
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
                        phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                        
                        // Thêm thông tin trạng thái duyệt và khóa
                        phong.setDuyet(rs.getBoolean("IsDuyet"));
                        phong.setBiKhoa(rs.getBoolean("IsBiKhoa"));
                        phong.setDeleted(rs.getBoolean("IsDeleted"));
                        
                        // Set default values for missing fields
                        phong.setTenQuanHuyen("Chưa xác định");
                        phong.setTenPhuong("Chưa xác định");
                        
                        // Set empty image list for now
                        phong.setDanhSachAnhUrl(new ArrayList<>());
                        
                        list.add(phong);
                        Log.d(TAG, "Successfully added phong #" + count + ": " + phong.getTieuDe() + 
                              " - " + phong.getGiaTien() + " VND - Status: " + phong.getTrangThai());
                              
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing phong row #" + count + ": " + e.getMessage(), e);
                        // Continue processing other rows
                    }
                }
                
                Log.d(TAG, "✅ Successfully processed " + count + " rows, added " + list.size() + " phòng to list");
                
                if (list.isEmpty()) {
                    Log.w(TAG, "⚠️ No rooms found for ChuTroId: " + chuTroId);
                }
                
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error loading phòng for landlord: " + e.getMessage(), e);
            Log.e(TAG, "SQL State: " + e.getSQLState());
            Log.e(TAG, "Error Code: " + e.getErrorCode());
        }
        return list;
    }

    /**
     * Đếm số phòng theo trạng thái của chủ trọ
     */
    public PhongStats getPhongStats(Connection conn, String chuTroId) {
        PhongStats stats = new PhongStats();
        
        String query = "SELECT " +
                      "COUNT(*) as TotalPhong, " +
                      "SUM(CASE WHEN p.IsDuyet = 1 AND p.IsBiKhoa = 0 THEN 1 ELSE 0 END) as ActivePhong, " +
                      "SUM(CASE WHEN p.TrangThai = 'Còn trống' THEN 1 ELSE 0 END) as AvailablePhong, " +
                      "SUM(CASE WHEN p.TrangThai = 'Đã thuê' THEN 1 ELSE 0 END) as RentedPhong, " +
                      "SUM(CASE WHEN p.IsDuyet = 0 THEN 1 ELSE 0 END) as PendingPhong " +
                      "FROM Phong p " +
                      "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "WHERE nt.ChuTroId = ? AND p.IsDeleted = 0";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.totalPhong = rs.getInt("TotalPhong");
                    stats.activePhong = rs.getInt("ActivePhong");
                    stats.availablePhong = rs.getInt("AvailablePhong");
                    stats.rentedPhong = rs.getInt("RentedPhong");
                    stats.pendingPhong = rs.getInt("PendingPhong");
                    
                    Log.d(TAG, "Stats for ChuTroId " + chuTroId + ": " +
                          "Total=" + stats.totalPhong + 
                          ", Active=" + stats.activePhong + 
                          ", Available=" + stats.availablePhong + 
                          ", Rented=" + stats.rentedPhong + 
                          ", Pending=" + stats.pendingPhong);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting phong stats: " + e.getMessage(), e);
        }
        
        return stats;
    }

    /**
     * Lấy ảnh đại diện của phòng
     */
    public String getPhongImageUrl(Connection conn, String phongId) {
        String query = "SELECT TOP 1 t.DuongDan FROM PhongAnh pa " +
                      "JOIN TapTin t ON pa.TapTinId = t.TapTinId " +
                      "WHERE pa.PhongId = ? ORDER BY pa.ThuTu";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phongId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("DuongDan");
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting image for phong " + phongId + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Class để lưu thống kê phòng
     */
    public static class PhongStats {
        public int totalPhong = 0;
        public int activePhong = 0;
        public int availablePhong = 0;
        public int rentedPhong = 0;
        public int pendingPhong = 0;
    }
}