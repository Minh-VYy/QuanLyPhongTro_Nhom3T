package com.example.QuanLyPhongTro_App.data.dao;

import android.util.Log;

import com.example.QuanLyPhongTro_App.data.model.YeuCauHoTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class YeuCauHoTroDao {
    private static final String TAG = "YeuCauHoTroDao";

    /**
     * Lấy danh sách yêu cầu hỗ trợ của người dùng
     */
    public List<YeuCauHoTro> getYeuCauByNguoiDung(Connection conn, String nguoiDungId) {
        List<YeuCauHoTro> list = new ArrayList<>();
        String query = "SELECT yc.*, lht.TenLoai, hs.HoTen AS TenNguoiYeuCau, p.TieuDe AS TenPhong " +
                      "FROM YeuCauHoTro yc " +
                      "JOIN LoaiHoTro lht ON yc.LoaiHoTroId = lht.LoaiHoTroId " +
                      "JOIN HoSoNguoiDung hs ON yc.NguoiYeuCau = hs.NguoiDungId " +
                      "LEFT JOIN Phong p ON yc.PhongId = p.PhongId " +
                      "WHERE yc.NguoiYeuCau = ? " +
                      "ORDER BY yc.ThoiGianTao DESC";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, nguoiDungId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                YeuCauHoTro yc = new YeuCauHoTro();
                yc.setHoTroId(rs.getString("HoTroId"));
                yc.setPhongId(rs.getString("PhongId"));
                yc.setNguoiYeuCau(rs.getString("NguoiYeuCau"));
                yc.setLoaiHoTroId(rs.getInt("LoaiHoTroId"));
                yc.setTieuDe(rs.getString("TieuDe"));
                yc.setMoTa(rs.getString("MoTa"));
                yc.setTrangThai(rs.getString("TrangThai"));
                yc.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
                
                // Thông tin bổ sung
                yc.setTenLoaiHoTro(rs.getString("TenLoai"));
                yc.setTenNguoiYeuCau(rs.getString("TenNguoiYeuCau"));
                yc.setTenPhong(rs.getString("TenPhong"));
                
                list.add(yc);
            }
            Log.d(TAG, "Loaded " + list.size() + " yêu cầu hỗ trợ");
        } catch (SQLException e) {
            Log.e(TAG, "Error loading yêu cầu hỗ trợ: " + e.getMessage(), e);
        }
        return list;
    }

    /**
     * Tạo yêu cầu hỗ trợ mới
     */
    public boolean createYeuCau(Connection conn, YeuCauHoTro yeuCau) {
        String query = "INSERT INTO YeuCauHoTro (HoTroId, PhongId, NguoiYeuCau, LoaiHoTroId, TieuDe, MoTa, TrangThai) " +
                      "VALUES (NEWID(), ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            if (yeuCau.getPhongId() != null) {
                stmt.setString(1, yeuCau.getPhongId());
            } else {
                stmt.setNull(1, java.sql.Types.VARCHAR);
            }
            stmt.setString(2, yeuCau.getNguoiYeuCau());
            stmt.setInt(3, yeuCau.getLoaiHoTroId());
            stmt.setString(4, yeuCau.getTieuDe());
            stmt.setString(5, yeuCau.getMoTa());
            stmt.setString(6, "Moi"); // Trạng thái mặc định
            
            int rows = stmt.executeUpdate();
            Log.d(TAG, "Created yêu cầu hỗ trợ: " + (rows > 0));
            return rows > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error creating yêu cầu hỗ trợ: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy tất cả loại hỗ trợ
     */
    public List<String> getAllLoaiHoTro(Connection conn) {
        List<String> list = new ArrayList<>();
        String query = "SELECT TenLoai FROM LoaiHoTro ORDER BY TenLoai";
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                list.add(rs.getString("TenLoai"));
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error loading loại hỗ trợ: " + e.getMessage());
        }
        return list;
    }

    /**
     * Lấy ID loại hỗ trợ theo tên
     */
    public int getLoaiHoTroIdByName(Connection conn, String tenLoai) {
        String query = "SELECT LoaiHoTroId FROM LoaiHoTro WHERE TenLoai = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, tenLoai);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("LoaiHoTroId");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting loại hỗ trợ ID: " + e.getMessage());
        }
        return -1;
    }
}
