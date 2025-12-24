package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingRequestDao {
    private static final String TAG = "BookingRequestDao";

    /**
     * Lấy danh sách yêu cầu đặt phòng của chủ trọ
     */
    public List<BookingRequest> getBookingRequestsByLandlord(Connection connection, String chuTroId) {
        List<BookingRequest> bookings = new ArrayList<>();
        
        String query = "SELECT " +
                "dp.DatPhongId, dp.PhongId, dp.NguoiThueId, dp.ChuTroId, " +
                "dp.Loai, dp.BatDau, dp.KetThuc, dp.ThoiGianTao, " +
                "dp.TrangThaiId, dp.GhiChu, dp.SoDatPhong, " +
                "hs.HoTen as TenNguoiThue, " +
                "p.TieuDe as TenPhong, " +
                "tt.TenTrangThai " +
                "FROM DatPhong dp " +
                "INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId " +
                "INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId " +
                "INNER JOIN Phong p ON dp.PhongId = p.PhongId " +
                "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                "INNER JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId " +
                "WHERE nt.ChuTroId = ? " +
                "ORDER BY dp.ThoiGianTao DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            
            Log.d(TAG, "Executing query for landlord: " + chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookingRequest booking = new BookingRequest();
                    booking.setDatPhongId(rs.getString("DatPhongId"));
                    booking.setPhongId(rs.getString("PhongId"));
                    booking.setNguoiThueId(rs.getString("NguoiThueId"));
                    booking.setChuTroId(rs.getString("ChuTroId"));
                    booking.setTenNguoiThue(rs.getString("TenNguoiThue"));
                    booking.setTenPhong(rs.getString("TenPhong"));
                    booking.setLoai(rs.getString("Loai"));
                    booking.setBatDau(rs.getTimestamp("BatDau"));
                    booking.setKetThuc(rs.getTimestamp("KetThuc"));
                    booking.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
                    booking.setTrangThaiId(rs.getInt("TrangThaiId"));
                    booking.setTenTrangThai(rs.getString("TenTrangThai"));
                    booking.setGhiChu(rs.getString("GhiChu"));
                    booking.setSoDatPhong(rs.getInt("SoDatPhong"));
                    
                    bookings.add(booking);
                    Log.d(TAG, "Found booking: " + booking.getTenNguoiThue() + " - " + booking.getTenPhong());
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting booking requests", e);
        }
        
        Log.d(TAG, "Total bookings found: " + bookings.size());
        return bookings;
    }

    /**
     * Cập nhật trạng thái yêu cầu đặt phòng
     */
    public boolean updateBookingStatus(Connection connection, String datPhongId, int newStatusId) {
        String query = "UPDATE DatPhong SET TrangThaiId = ? WHERE DatPhongId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newStatusId);
            stmt.setString(2, datPhongId);
            
            int rowsAffected = stmt.executeUpdate();
            Log.d(TAG, "Updated booking status. Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating booking status", e);
            return false;
        }
    }

    /**
     * Lấy ID trạng thái theo tên
     */
    public int getStatusIdByName(Connection connection, String statusName) {
        String query = "SELECT TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, statusName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TrangThaiId");
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting status ID", e);
        }
        
        return -1; // Not found
    }

    /**
     * Lấy danh sách yêu cầu theo trạng thái
     */
    public List<BookingRequest> getBookingRequestsByStatus(Connection connection, String chuTroId, String statusName) {
        List<BookingRequest> bookings = new ArrayList<>();
        
        String query = "SELECT " +
                "dp.DatPhongId, dp.PhongId, dp.NguoiThueId, dp.ChuTroId, " +
                "dp.Loai, dp.BatDau, dp.KetThuc, dp.ThoiGianTao, " +
                "dp.TrangThaiId, dp.GhiChu, dp.SoDatPhong, " +
                "hs.HoTen as TenNguoiThue, " +
                "p.TieuDe as TenPhong, " +
                "tt.TenTrangThai " +
                "FROM DatPhong dp " +
                "INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId " +
                "INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId " +
                "INNER JOIN Phong p ON dp.PhongId = p.PhongId " +
                "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                "INNER JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId " +
                "WHERE nt.ChuTroId = ? AND tt.TenTrangThai = ? " +
                "ORDER BY dp.ThoiGianTao DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            stmt.setString(2, statusName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    BookingRequest booking = new BookingRequest();
                    booking.setDatPhongId(rs.getString("DatPhongId"));
                    booking.setPhongId(rs.getString("PhongId"));
                    booking.setNguoiThueId(rs.getString("NguoiThueId"));
                    booking.setChuTroId(rs.getString("ChuTroId"));
                    booking.setTenNguoiThue(rs.getString("TenNguoiThue"));
                    booking.setTenPhong(rs.getString("TenPhong"));
                    booking.setLoai(rs.getString("Loai"));
                    booking.setBatDau(rs.getTimestamp("BatDau"));
                    booking.setKetThuc(rs.getTimestamp("KetThuc"));
                    booking.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
                    booking.setTrangThaiId(rs.getInt("TrangThaiId"));
                    booking.setTenTrangThai(rs.getString("TenTrangThai"));
                    booking.setGhiChu(rs.getString("GhiChu"));
                    booking.setSoDatPhong(rs.getInt("SoDatPhong"));
                    
                    bookings.add(booking);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error getting booking requests by status", e);
        }
        
        return bookings;
    }
}