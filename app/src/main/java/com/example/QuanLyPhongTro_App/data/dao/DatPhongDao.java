package com.example.QuanLyPhongTro_App.data.dao;

import android.util.Log;

import com.example.QuanLyPhongTro_App.data.model.DatPhong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatPhongDao {
    private static final String TAG = "DatPhongDao";

    /**
     * Lấy danh sách đặt phòng của người thuê
     */
    public List<DatPhong> getDatPhongByNguoiThue(Connection conn, String nguoiThueId) {
        List<DatPhong> list = new ArrayList<>();
        String query = "SELECT dp.*, p.TieuDe AS TenPhong, p.GiaTien, " +
                      "nt.DiaChi, tt.TenTrangThai, hs.HoTen AS TenNguoiThue " +
                      "FROM DatPhong dp " +
                      "JOIN Phong p ON dp.PhongId = p.PhongId " +
                      "JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId " +
                      "JOIN HoSoNguoiDung hs ON dp.NguoiThueId = hs.NguoiDungId " +
                      "WHERE dp.NguoiThueId = ? " +
                      "ORDER BY dp.ThoiGianTao DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nguoiThueId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                try {
                    DatPhong dp = new DatPhong();
                    dp.setDatPhongId(rs.getString("DatPhongId"));
                    dp.setPhongId(rs.getString("PhongId"));
                    dp.setNguoiThueId(rs.getString("NguoiThueId"));
                    dp.setLoai(rs.getString("Loai"));
                    
                    // FIX: Dùng getString() rồi parse thay vì getTimestamp() để tránh lỗi DATETIMEOFFSET
                    String batDauStr = rs.getString("BatDau");
                    if (batDauStr != null) {
                        // Parse DATETIMEOFFSET string: "2025-12-27 13:00:00.8033333 +00:00"
                        // Bỏ phần timezone và microseconds
                        batDauStr = batDauStr.split("\\+")[0].split("\\.")[0]; // "2025-12-27 13:00:00"
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dp.setBatDau(sdf.parse(batDauStr));
                    }
                    
                    String ketThucStr = rs.getString("KetThuc");
                    if (ketThucStr != null && !ketThucStr.isEmpty()) {
                        ketThucStr = ketThucStr.split("\\+")[0].split("\\.")[0];
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dp.setKetThuc(sdf.parse(ketThucStr));
                    }
                    
                    String thoiGianTaoStr = rs.getString("ThoiGianTao");
                    if (thoiGianTaoStr != null) {
                        thoiGianTaoStr = thoiGianTaoStr.split("\\+")[0].split("\\.")[0];
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dp.setThoiGianTao(sdf.parse(thoiGianTaoStr));
                    }
                    
                    dp.setTrangThaiId(rs.getInt("TrangThaiId"));
                    dp.setTapTinBienLaiId(rs.getString("TapTinBienLaiId"));
                    dp.setSoDatPhong(rs.getInt("SoDatPhong"));
                    dp.setGhiChu(rs.getString("GhiChu"));
                    
                    // Thông tin bổ sung
                    dp.setTenPhong(rs.getString("TenPhong"));
                    dp.setGiaPhong(rs.getLong("GiaTien"));
                    dp.setDiaChiPhong(rs.getString("DiaChi"));
                    dp.setTenTrangThai(rs.getString("TenTrangThai"));
                    dp.setTenNguoiThue(rs.getString("TenNguoiThue"));
                    
                    list.add(dp);
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing booking row: " + e.getMessage(), e);
                }
            }
            Log.d(TAG, "✅ Loaded " + list.size() + " đặt phòng");
        } catch (SQLException e) {
            Log.e(TAG, "Error loading đặt phòng: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Tạo đặt phòng mới
     */
    public boolean createDatPhong(Connection conn, DatPhong datPhong) {
        // Query lấy ChuTroId từ PhongId
        String getChuTroQuery = "SELECT nt.ChuTroId FROM Phong p " +
                               "JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                               "WHERE p.PhongId = ?";
        
        String insertQuery = "INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, Loai, BatDau, KetThuc, TrangThaiId, GhiChu) " +
                            "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?, ?)";
        
        Log.d(TAG, "=== CREATE DAT PHONG ===");
        Log.d(TAG, "PhongId: " + datPhong.getPhongId());
        Log.d(TAG, "NguoiThueId: " + datPhong.getNguoiThueId());
        Log.d(TAG, "Loai: " + datPhong.getLoai());
        Log.d(TAG, "BatDau: " + datPhong.getBatDau());
        Log.d(TAG, "KetThuc: " + datPhong.getKetThuc());
        Log.d(TAG, "TrangThaiId: " + datPhong.getTrangThaiId());
        Log.d(TAG, "GhiChu: " + datPhong.getGhiChu());
        
        try {
            // 1. Lấy ChuTroId từ PhongId
            String chuTroId = null;
            try (PreparedStatement getChuTroStmt = conn.prepareStatement(getChuTroQuery)) {
                getChuTroStmt.setString(1, datPhong.getPhongId());
                ResultSet rs = getChuTroStmt.executeQuery();
                if (rs.next()) {
                    chuTroId = rs.getString("ChuTroId");
                    Log.d(TAG, "✅ ChuTroId: " + chuTroId);
                } else {
                    Log.e(TAG, "❌ Không tìm thấy ChuTroId cho PhongId: " + datPhong.getPhongId());
                    return false;
                }
            }
            
            if (chuTroId == null) {
                Log.e(TAG, "❌ ChuTroId is NULL");
                return false;
            }
            
            // 2. Insert DatPhong với ChuTroId
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, datPhong.getPhongId());
                stmt.setString(2, datPhong.getNguoiThueId());
                stmt.setString(3, chuTroId);  // ChuTroId
                stmt.setString(4, datPhong.getLoai());
                stmt.setTimestamp(5, new java.sql.Timestamp(datPhong.getBatDau().getTime()));
                
                if (datPhong.getKetThuc() != null) {
                    stmt.setTimestamp(6, new java.sql.Timestamp(datPhong.getKetThuc().getTime()));
                } else {
                    stmt.setNull(6, java.sql.Types.TIMESTAMP);
                }
                
                stmt.setInt(7, datPhong.getTrangThaiId());
                stmt.setString(8, datPhong.getGhiChu());
                
                Log.d(TAG, "Executing insert query...");
                int rows = stmt.executeUpdate();
                
                boolean success = rows > 0;
                Log.d(TAG, success ? "✅ Created đặt phòng successfully" : "❌ Failed to create đặt phòng");
                Log.d(TAG, "Rows affected: " + rows);
                
                return success;
            }
            
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error creating đặt phòng:");
            Log.e(TAG, "  Message: " + e.getMessage());
            Log.e(TAG, "  SQL State: " + e.getSQLState());
            Log.e(TAG, "  Error Code: " + e.getErrorCode());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật trạng thái đặt phòng
     */
    public boolean updateTrangThai(Connection conn, String datPhongId, int trangThaiId) {
        String query = "UPDATE DatPhong SET TrangThaiId = ? WHERE DatPhongId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, trangThaiId);
            stmt.setString(2, datPhongId);
            
            int rows = stmt.executeUpdate();
            Log.d(TAG, "Updated trạng thái: " + (rows > 0));
            return rows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating trạng thái: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy chi tiết đặt phòng
     */
    public DatPhong getDatPhongById(Connection conn, String datPhongId) {
        String query = "SELECT dp.*, p.TieuDe AS TenPhong, p.GiaTien, " +
                      "nt.DiaChi, tt.TenTrangThai, hs.HoTen AS TenNguoiThue " +
                      "FROM DatPhong dp " +
                      "JOIN Phong p ON dp.PhongId = p.PhongId " +
                      "JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId " +
                      "JOIN HoSoNguoiDung hs ON dp.NguoiThueId = hs.NguoiDungId " +
                      "WHERE dp.DatPhongId = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, datPhongId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                DatPhong dp = new DatPhong();
                dp.setDatPhongId(rs.getString("DatPhongId"));
                dp.setPhongId(rs.getString("PhongId"));
                dp.setNguoiThueId(rs.getString("NguoiThueId"));
                dp.setLoai(rs.getString("Loai"));
                
                // FIX: Parse DATETIMEOFFSET
                try {
                    String batDauStr = rs.getString("BatDau");
                    if (batDauStr != null) {
                        batDauStr = batDauStr.split("\\+")[0].split("\\.")[0];
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dp.setBatDau(sdf.parse(batDauStr));
                    }
                    
                    String ketThucStr = rs.getString("KetThuc");
                    if (ketThucStr != null && !ketThucStr.isEmpty()) {
                        ketThucStr = ketThucStr.split("\\+")[0].split("\\.")[0];
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dp.setKetThuc(sdf.parse(ketThucStr));
                    }
                    
                    String thoiGianTaoStr = rs.getString("ThoiGianTao");
                    if (thoiGianTaoStr != null) {
                        thoiGianTaoStr = thoiGianTaoStr.split("\\+")[0].split("\\.")[0];
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        dp.setThoiGianTao(sdf.parse(thoiGianTaoStr));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parsing dates: " + e.getMessage());
                }
                
                dp.setTrangThaiId(rs.getInt("TrangThaiId"));
                dp.setTapTinBienLaiId(rs.getString("TapTinBienLaiId"));
                dp.setSoDatPhong(rs.getInt("SoDatPhong"));
                dp.setGhiChu(rs.getString("GhiChu"));
                dp.setTenPhong(rs.getString("TenPhong"));
                dp.setGiaPhong(rs.getLong("GiaTien"));
                dp.setDiaChiPhong(rs.getString("DiaChi"));
                dp.setTenTrangThai(rs.getString("TenTrangThai"));
                dp.setTenNguoiThue(rs.getString("TenNguoiThue"));
                
                return dp;
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error loading đặt phòng detail: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * Lấy số điện thoại chủ trọ từ PhongId
     */
    public String getLandlordPhone(Connection conn, String phongId) {
        String query = "SELECT nd.DienThoai FROM Phong p " +
                      "JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                      "JOIN NguoiDung nd ON nt.ChuTroId = nd.NguoiDungId " +
                      "WHERE p.PhongId = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, phongId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("DienThoai");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting landlord phone: " + e.getMessage());
        }
        return null;
    }
}
