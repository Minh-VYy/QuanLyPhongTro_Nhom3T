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
                DatPhong dp = new DatPhong();
                dp.setDatPhongId(rs.getString("DatPhongId"));
                dp.setPhongId(rs.getString("PhongId"));
                dp.setNguoiThueId(rs.getString("NguoiThueId"));
                dp.setLoai(rs.getString("Loai"));
                dp.setBatDau(rs.getTimestamp("BatDau"));
                dp.setKetThuc(rs.getTimestamp("KetThuc"));
                dp.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
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
            }
            Log.d(TAG, "Loaded " + list.size() + " đặt phòng");
        } catch (SQLException e) {
            Log.e(TAG, "Error loading đặt phòng: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Tạo đặt phòng mới
     */
    public boolean createDatPhong(Connection conn, DatPhong datPhong) {
        String query = "INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, Loai, BatDau, KetThuc, TrangThaiId, GhiChu) " +
                      "VALUES (NEWID(), ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, datPhong.getPhongId());
            stmt.setString(2, datPhong.getNguoiThueId());
            stmt.setString(3, datPhong.getLoai());
            stmt.setTimestamp(4, new java.sql.Timestamp(datPhong.getBatDau().getTime()));
            if (datPhong.getKetThuc() != null) {
                stmt.setTimestamp(5, new java.sql.Timestamp(datPhong.getKetThuc().getTime()));
            } else {
                stmt.setNull(5, java.sql.Types.TIMESTAMP);
            }
            stmt.setInt(6, datPhong.getTrangThaiId());
            stmt.setString(7, datPhong.getGhiChu());
            
            int rows = stmt.executeUpdate();
            Log.d(TAG, "Created đặt phòng: " + (rows > 0));
            return rows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error creating đặt phòng: " + e.getMessage(), e);
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
                dp.setBatDau(rs.getTimestamp("BatDau"));
                dp.setKetThuc(rs.getTimestamp("KetThuc"));
                dp.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
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
}
