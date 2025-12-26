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
        Log.d(TAG, "=== UPDATING BOOKING STATUS ===");
        Log.d(TAG, "DatPhongId: " + datPhongId + ", NewStatusId: " + newStatusId);
        
        // Start transaction
        try {
            connection.setAutoCommit(false);
            
            // First verify the booking exists
            String checkQuery = "SELECT TrangThaiId FROM DatPhong WHERE DatPhongId = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, datPhongId);
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        Log.e(TAG, "❌ Booking not found: " + datPhongId);
                        connection.rollback();
                        return false;
                    }
                    int currentStatus = rs.getInt("TrangThaiId");
                    Log.d(TAG, "Current status: " + currentStatus + " → New status: " + newStatusId);
                }
            }
            
            // Update the status
            String updateQuery = "UPDATE DatPhong SET TrangThaiId = ?, UpdatedAt = GETDATE() WHERE DatPhongId = ?";
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, newStatusId);
                updateStmt.setString(2, datPhongId);
                
                int rowsAffected = updateStmt.executeUpdate();
                Log.d(TAG, "Update result - Rows affected: " + rowsAffected);
                
                if (rowsAffected > 0) {
                    connection.commit();
                    Log.d(TAG, "✅ Booking status updated successfully");
                    return true;
                } else {
                    connection.rollback();
                    Log.e(TAG, "❌ No rows affected during update");
                    return false;
                }
            }
            
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error updating booking status: " + e.getMessage(), e);
            try {
                connection.rollback();
                Log.d(TAG, "Transaction rolled back");
            } catch (SQLException rollbackEx) {
                Log.e(TAG, "Error during rollback: " + rollbackEx.getMessage(), rollbackEx);
            }
            return false;
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                Log.e(TAG, "Error resetting auto-commit: " + e.getMessage(), e);
            }
        }
    }

    public int getStatusIdByName(Connection connection, String statusName) {
        Log.d(TAG, "=== GETTING STATUS ID ===");
        Log.d(TAG, "Looking for status: " + statusName);
        
        // Map common status names to IDs (fallback)
        int fallbackId = getFallbackStatusId(statusName);
        
        String query = "SELECT TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, statusName);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int statusId = rs.getInt("TrangThaiId");
                    Log.d(TAG, "✅ Found status ID: " + statusId + " for " + statusName);
                    return statusId;
                } else {
                    Log.w(TAG, "⚠️ Status not found in DB, using fallback: " + fallbackId);
                    return fallbackId;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ Error getting status ID: " + e.getMessage(), e);
            Log.w(TAG, "Using fallback status ID: " + fallbackId);
            return fallbackId;
        }
    }
    
    private int getFallbackStatusId(String statusName) {
        switch (statusName) {
            case "ChoXacNhan":
            case "Chờ xác nhận":
                return 1;
            case "DaXacNhan":
            case "Đã xác nhận":
                return 2;
            case "DangThue":
            case "Đang thuê":
                return 3;
            case "DaHoanThanh":
            case "Đã hoàn thành":
                return 4;
            case "DaHuy":
            case "Đã hủy":
                return 5;
            default:
                Log.w(TAG, "Unknown status name: " + statusName + ", defaulting to 1");
                return 1;
        }
    }
}