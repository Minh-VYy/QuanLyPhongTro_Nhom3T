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

    public List<BookingRequest> getBookingRequestsByLandlord(Connection connection, String chuTroId) {
        List<BookingRequest> bookings = new ArrayList<>();
        
        Log.d(TAG, "=== LOADING BOOKING REQUESTS ===");
        Log.d(TAG, "Input ChuTroId: " + chuTroId);
        
        // Simplified query for better performance
        String query = "SELECT TOP 20 " +
                "DatPhongId, PhongId, NguoiThueId, ChuTroId, " +
                "ISNULL(Loai, 'Đặt lịch xem phòng') as Loai, " +
                "BatDau, KetThuc, ThoiGianTao, " +
                "ISNULL(TrangThaiId, 1) as TrangThaiId, " +
                "ISNULL(GhiChu, '') as GhiChu " +
                "FROM DatPhong " +
                "WHERE ChuTroId = ? " +
                "ORDER BY ThoiGianTao DESC";

        Log.d(TAG, "Executing simplified query");
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    BookingRequest booking = new BookingRequest();
                    
                    // Set basic fields
                    booking.setDatPhongId(rs.getString("DatPhongId"));
                    booking.setPhongId(rs.getString("PhongId"));
                    booking.setNguoiThueId(rs.getString("NguoiThueId"));
                    booking.setChuTroId(rs.getString("ChuTroId"));
                    booking.setLoai(rs.getString("Loai"));
                    booking.setBatDau(rs.getTimestamp("BatDau"));
                    booking.setKetThuc(rs.getTimestamp("KetThuc"));
                    booking.setThoiGianTao(rs.getTimestamp("ThoiGianTao"));
                    booking.setTrangThaiId(rs.getInt("TrangThaiId"));
                    booking.setGhiChu(rs.getString("GhiChu"));
                    booking.setSoDatPhong(count);
                    
                    // Set default values for missing fields
                    booking.setTenNguoiThue("Người thuê #" + count);
                    booking.setTenPhong("Phòng " + booking.getPhongId().substring(0, 8));
                    
                    // Set status name based on TrangThaiId
                    switch (booking.getTrangThaiId()) {
                        case 1:
                            booking.setTenTrangThai("Chờ xác nhận");
                            break;
                        case 2:
                            booking.setTenTrangThai("Đã xác nhận");
                            break;
                        case 5:
                            booking.setTenTrangThai("Đã hủy");
                            break;
                        default:
                            booking.setTenTrangThai("Chờ xử lý");
                            break;
                    }
                    
                    bookings.add(booking);
                    Log.d(TAG, "✅ Loaded booking #" + count + ": " + booking.getLoai() + " - " + booking.getTenTrangThai());
                }
                Log.d(TAG, "✅ Total bookings loaded: " + count);
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error: " + e.getMessage(), e);
        }
        
        return bookings;
    }

    public boolean updateBookingStatus(Connection connection, String datPhongId, int newStatusId) {
        String query = "UPDATE DatPhong SET TrangThaiId = ? WHERE DatPhongId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, newStatusId);
            stmt.setString(2, datPhongId);
            
            int rowsAffected = stmt.executeUpdate();
            Log.d(TAG, "Updated booking status. Rows affected: " + rowsAffected);
            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating booking status: " + e.getMessage(), e);
            return false;
        }
    }

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
            Log.e(TAG, "Error getting status ID: " + e.getMessage(), e);
        }
        
        return 1; // Default to "Chờ xác nhận"
    }
}