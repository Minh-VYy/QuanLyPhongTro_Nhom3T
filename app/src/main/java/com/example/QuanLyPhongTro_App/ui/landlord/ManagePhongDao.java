package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;

import com.example.QuanLyPhongTro_App.data.model.Phong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ManagePhongDao {
    private static final String TAG = "ManagePhongDao";

    /**
     * Xóa phòng (soft delete)
     */
    public boolean deletePhong(Connection conn, String phongId, String chuTroId) {
        try {
            // Kiểm tra quyền sở hữu trước khi xóa
            if (!isPhongOwnedByLandlord(conn, phongId, chuTroId)) {
                Log.e(TAG, "Phong " + phongId + " is not owned by landlord " + chuTroId);
                return false;
            }

            // Soft delete - chỉ đánh dấu IsDeleted = 1
            String updateQuery = "UPDATE Phong SET IsDeleted = 1, UpdatedAt = GETDATE() " +
                               "WHERE PhongId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, phongId);
                
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    Log.d(TAG, "✅ Successfully deleted phong: " + phongId);
                    return true;
                } else {
                    Log.e(TAG, "❌ Failed to delete phong: " + phongId);
                    return false;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error deleting phong: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Cập nhật thông tin phòng
     */
    public boolean updatePhong(Connection conn, String phongId, String chuTroId, 
                              String tieuDe, long giaTien, String moTa) {
        try {
            // Kiểm tra quyền sở hữu trước khi cập nhật
            if (!isPhongOwnedByLandlord(conn, phongId, chuTroId)) {
                Log.e(TAG, "Phong " + phongId + " is not owned by landlord " + chuTroId);
                return false;
            }

            String updateQuery = "UPDATE Phong SET TieuDe = ?, GiaTien = ?, TienCoc = ?, " +
                               "MoTa = ?, UpdatedAt = GETDATE() WHERE PhongId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, tieuDe);
                stmt.setLong(2, giaTien);
                stmt.setLong(3, giaTien); // TienCoc = GiaTien
                stmt.setString(4, moTa != null ? moTa : "");
                stmt.setString(5, phongId);
                
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    Log.d(TAG, "✅ Successfully updated phong: " + phongId);
                    Log.d(TAG, "New TieuDe: " + tieuDe);
                    Log.d(TAG, "New GiaTien: " + giaTien);
                    Log.d(TAG, "New MoTa: " + moTa);
                    return true;
                } else {
                    Log.e(TAG, "❌ Failed to update phong: " + phongId);
                    return false;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error updating phong: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy thông tin chi tiết phòng để edit
     */
    public Phong getPhongForEdit(Connection conn, String phongId, String chuTroId) {
        try {
            // Kiểm tra quyền sở hữu
            if (!isPhongOwnedByLandlord(conn, phongId, chuTroId)) {
                Log.e(TAG, "Phong " + phongId + " is not owned by landlord " + chuTroId);
                return null;
            }

            String query = "SELECT p.PhongId, p.TieuDe, p.GiaTien, p.TienCoc, p.DienTich, " +
                          "p.SoNguoiToiDa, p.TrangThai, p.DiemTrungBinh, p.SoLuongDanhGia, " +
                          "p.MoTa, p.IsDuyet, p.IsBiKhoa, " +
                          "nt.DiaChi " +
                          "FROM Phong p " +
                          "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                          "WHERE p.PhongId = ? AND nt.ChuTroId = ? AND p.IsDeleted = 0";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, phongId);
                stmt.setString(2, chuTroId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Phong phong = new Phong();
                        
                        phong.setPhongId(rs.getString("PhongId"));
                        phong.setTieuDe(rs.getString("TieuDe"));
                        phong.setGiaTien(rs.getLong("GiaTien"));
                        phong.setTienCoc(rs.getLong("TienCoc"));
                        phong.setDienTich(rs.getDouble("DienTich"));
                        phong.setSoNguoiToiDa(rs.getInt("SoNguoiToiDa"));
                        phong.setTrangThai(rs.getString("TrangThai"));
                        phong.setDiemTrungBinh(rs.getFloat("DiemTrungBinh"));
                        phong.setSoLuongDanhGia(rs.getInt("SoLuongDanhGia"));
                        phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                        
                        // Lấy mô tả (có thể null)
                        String moTa = rs.getString("MoTa");
                        phong.setMoTa(moTa != null ? moTa : "");
                        
                        // Lấy trạng thái duyệt và khóa
                        phong.setDuyet(rs.getBoolean("IsDuyet"));
                        phong.setBiKhoa(rs.getBoolean("IsBiKhoa"));
                        
                        Log.d(TAG, "✅ Successfully loaded phong for edit: " + phong.getTieuDe());
                        Log.d(TAG, "MoTa: " + phong.getMoTa());
                        Log.d(TAG, "GiaTien: " + phong.getGiaTien());
                        
                        return phong;
                    }
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error loading phong for edit: " + e.getMessage(), e);
        }
        
        return null;
    }

    /**
     * Kiểm tra phòng có thuộc về chủ trọ không
     */
    private boolean isPhongOwnedByLandlord(Connection conn, String phongId, String chuTroId) {
        try {
            String query = "SELECT COUNT(*) as count FROM Phong p " +
                          "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                          "WHERE p.PhongId = ? AND nt.ChuTroId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, phongId);
                stmt.setString(2, chuTroId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("count") > 0;
                    }
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Error checking phong ownership: " + e.getMessage(), e);
        }
        
        return false;
    }

    /**
     * Toggle trạng thái hoạt động của phòng
     */
    public boolean togglePhongActive(Connection conn, String phongId, String chuTroId, boolean isActive) {
        try {
            // Kiểm tra quyền sở hữu
            if (!isPhongOwnedByLandlord(conn, phongId, chuTroId)) {
                Log.e(TAG, "Phong " + phongId + " is not owned by landlord " + chuTroId);
                return false;
            }

            // Cập nhật trạng thái (giả sử có cột IsActive hoặc dùng TrangThai)
            String newStatus = isActive ? "Còn trống" : "Không hoạt động";
            String updateQuery = "UPDATE Phong SET TrangThai = ?, UpdatedAt = GETDATE() " +
                               "WHERE PhongId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setString(1, newStatus);
                stmt.setString(2, phongId);
                
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    Log.d(TAG, "✅ Successfully toggled phong active status: " + phongId + " -> " + isActive);
                    return true;
                } else {
                    Log.e(TAG, "❌ Failed to toggle phong active status: " + phongId);
                    return false;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error toggling phong active status: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Khôi phục phòng đã xóa (restore from soft delete)
     */
    public boolean restorePhong(Connection conn, String phongId, String chuTroId) {
        try {
            // Kiểm tra quyền sở hữu (bao gồm cả phòng đã xóa)
            String ownershipQuery = "SELECT COUNT(*) as count FROM Phong p " +
                                  "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                                  "WHERE p.PhongId = ? AND nt.ChuTroId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(ownershipQuery)) {
                stmt.setString(1, phongId);
                stmt.setString(2, chuTroId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next() || rs.getInt("count") == 0) {
                        Log.e(TAG, "Phong " + phongId + " is not owned by landlord " + chuTroId);
                        return false;
                    }
                }
            }

            // Khôi phục phòng
            String restoreQuery = "UPDATE Phong SET IsDeleted = 0, UpdatedAt = GETDATE() " +
                                "WHERE PhongId = ?";

            try (PreparedStatement stmt = conn.prepareStatement(restoreQuery)) {
                stmt.setString(1, phongId);
                
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    Log.d(TAG, "✅ Successfully restored phong: " + phongId);
                    return true;
                } else {
                    Log.e(TAG, "❌ Failed to restore phong: " + phongId);
                    return false;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error restoring phong: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy danh sách phòng đã xóa của chủ trọ (để khôi phục)
     */
    public List<Phong> getDeletedPhongByChuTroId(Connection conn, String chuTroId) {
        List<Phong> list = new ArrayList<>();
        
        String query = "SELECT p.PhongId, p.TieuDe, p.DienTich, p.GiaTien, p.TienCoc, " +
                      "p.SoNguoiToiDa, p.TrangThai, p.DiemTrungBinh, p.SoLuongDanhGia, " +
                      "p.UpdatedAt, nt.DiaChi " +
                      "FROM Phong p " +
                      "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "WHERE nt.ChuTroId = ? AND p.IsDeleted = 1 " +
                      "ORDER BY p.UpdatedAt DESC";

        Log.d(TAG, "Loading deleted phong for ChuTroId: " + chuTroId);
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Phong phong = new Phong();
                    
                    phong.setPhongId(rs.getString("PhongId"));
                    phong.setTieuDe(rs.getString("TieuDe"));
                    phong.setDienTich(rs.getDouble("DienTich"));
                    phong.setGiaTien(rs.getLong("GiaTien"));
                    phong.setTienCoc(rs.getLong("TienCoc"));
                    phong.setSoNguoiToiDa(rs.getInt("SoNguoiToiDa"));
                    phong.setTrangThai("Đã xóa"); // Đánh dấu trạng thái đã xóa
                    phong.setDiemTrungBinh(rs.getFloat("DiemTrungBinh"));
                    phong.setSoLuongDanhGia(rs.getInt("SoLuongDanhGia"));
                    phong.setDiaChiNhaTro(rs.getString("DiaChi"));
                    phong.setDeleted(true);
                    
                    list.add(phong);
                }
                
                Log.d(TAG, "✅ Found " + list.size() + " deleted phong for landlord");
                
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error loading deleted phong: " + e.getMessage(), e);
        }
        return list;
    }
}