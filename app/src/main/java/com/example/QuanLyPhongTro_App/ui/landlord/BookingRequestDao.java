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
        
        Log.d(TAG, "=== DEBUGGING BOOKING REQUESTS ===");
        Log.d(TAG, "Input ChuTroId: " + chuTroId);
        
        // Try a simpler query first to check data existence
        String simpleQuery = "SELECT COUNT(*) as total FROM DatPhong WHERE ChuTroId = ?";
        try (PreparedStatement simpleStmt = connection.prepareStatement(simpleQuery)) {
            simpleStmt.setString(1, chuTroId);
            try (ResultSet simpleRs = simpleStmt.executeQuery()) {
                if (simpleRs.next()) {
                    int directCount = simpleRs.getInt("total");
                    Log.d(TAG, "Direct ChuTroId match count: " + directCount);
                    
                    // If no data found, return empty list immediately
                    if (directCount == 0) {
                        Log.d(TAG, "No DatPhong records found for ChuTroId: " + chuTroId);
                        return bookings;
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Simple query error: " + e.getMessage(), e);
            return bookings; // Return empty list on error
        }
        
        // Main query with proper error handling
        String query = "SELECT " +
                "dp.DatPhongId, dp.PhongId, dp.NguoiThueId, dp.ChuTroId, " +
                "ISNULL(dp.Loai, 'Đặt lịch xem phòng') as Loai, " +
                "dp.BatDau, dp.KetThuc, " +
                "ISNULL(dp.ThoiGianTao, GETDATE()) as ThoiGianTao, " +
                "ISNULL(dp.TrangThaiId, 1) as TrangThaiId, " +
                "ISNULL(dp.GhiChu, '') as GhiChu, " +
                "ISNULL(hs.HoTen, 'Người thuê') as TenNguoiThue, " +
                "ISNULL(p.TieuDe, 'Phòng trọ') as TenPhong, " +
                "ISNULL(tt.TenTrangThai, 'ChoXacNhan') as TenTrangThai " +
                "FROM DatPhong dp " +
                "LEFT JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId " +
                "LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId " +
                "LEFT JOIN Phong p ON dp.PhongId = p.PhongId " +
                "LEFT JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId " +
                "WHERE dp.ChuTroId = ? " +
                "ORDER BY dp.ThoiGianTao DESC";

        Log.d(TAG, "Executing main query: " + query);
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            Log.d(TAG, "Query parameter: " + chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    BookingRequest booking = new BookingRequest();
                    
                    // Set basic fields with null checks
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
                    booking.setSoDatPhong(count);
                    
                    bookings.add(booking);
                    Log.d(TAG, "✅ Found booking #" + count + ": " + booking.getTenNguoiThue() + " - " + booking.getTenPhong() + " (" + booking.getTenTrangThai() + ")");
                }
                Log.d(TAG, "✅ Total records processed: " + count);
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error: " + e.getMessage(), e);
            
            // Enhanced debugging
            try {
                // Check total DatPhong records
                PreparedStatement countStmt = connection.prepareStatement("SELECT COUNT(*) as total FROM DatPhong");
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    Log.d(TAG, "📊 Total DatPhong records in database: " + countRs.getInt("total"));
                }
                countRs.close();
                countStmt.close();
                
                // Check records for this landlord
                PreparedStatement landlordStmt = connection.prepareStatement(
                    "SELECT COUNT(*) as total FROM DatPhong WHERE ChuTroId = ?");
                landlordStmt.setString(1, chuTroId);
                ResultSet landlordRs = landlordStmt.executeQuery();
                if (landlordRs.next()) {
                    Log.d(TAG, "📊 DatPhong records for ChuTroId " + chuTroId + ": " + landlordRs.getInt("total"));
                }
                landlordRs.close();
                landlordStmt.close();
                
                // Check if tables exist
                PreparedStatement tableStmt = connection.prepareStatement(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('DatPhong', 'NguoiDung', 'HoSoNguoiDung', 'Phong', 'TrangThaiDatPhong')");
                ResultSet tableRs = tableStmt.executeQuery();
                Log.d(TAG, "📋 Available tables:");
                while (tableRs.next()) {
                    Log.d(TAG, "  - " + tableRs.getString("TABLE_NAME"));
                }
                tableRs.close();
                tableStmt.close();
                
            } catch (SQLException debugE) {
                Log.e(TAG, "❌ Debug query error: " + debugE.getMessage(), debugE);
            }
        }
        
        Log.d(TAG, "📈 Final result size: " + bookings.size());
        Log.d(TAG, "=== END DEBUGGING ===");
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
            Log.e(TAG, "Error updating booking status", e);
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
            Log.e(TAG, "Error getting status ID", e);
        }
        
        return -1;
    }
}