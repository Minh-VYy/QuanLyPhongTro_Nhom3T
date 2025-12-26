package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatsDao {
    private static final String TAG = "StatsDao";

    /**
     * Lấy thống kê tổng quan cho chủ trọ
     */
    public LandlordStats getLandlordStats(Connection connection, String landlordId) {
        LandlordStats stats = new LandlordStats();
        
        Log.d(TAG, "=== LOADING LANDLORD STATISTICS ===");
        Log.d(TAG, "LandlordId: " + landlordId);
        
        try {
            // 1. Thống kê phòng
            loadRoomStats(connection, landlordId, stats);
            
            // 2. Thống kê booking
            loadBookingStats(connection, landlordId, stats);
            
            // 3. Thống kê doanh thu
            loadRevenueStats(connection, landlordId, stats);
            
            Log.d(TAG, "✅ Statistics loaded successfully");
            
        } catch (SQLException e) {
            Log.e(TAG, "❌ Error loading statistics: " + e.getMessage(), e);
        }
        
        return stats;
    }
    
    /**
     * Thống kê phòng
     */
    private void loadRoomStats(Connection connection, String landlordId, LandlordStats stats) throws SQLException {
        String query = "SELECT " +
                "COUNT(*) as TotalRooms, " +
                "SUM(CASE WHEN p.TrangThai = 'con_trong' THEN 1 ELSE 0 END) as VacantRooms, " +
                "SUM(CASE WHEN p.TrangThai = 'da_thue' THEN 1 ELSE 0 END) as OccupiedRooms " +
                "FROM Phong p " +
                "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                "WHERE nt.ChuTroId = ?";
                
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, landlordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalRooms(rs.getInt("TotalRooms"));
                    stats.setVacantRooms(rs.getInt("VacantRooms"));
                    stats.setOccupiedRooms(rs.getInt("OccupiedRooms"));
                    
                    // Tính phòng hoạt động (tổng - không hoạt động)
                    stats.setActiveRooms(stats.getTotalRooms());
                    stats.setInactiveRooms(0);
                    
                    Log.d(TAG, "📊 Room Stats - Total: " + stats.getTotalRooms() + 
                          ", Occupied: " + stats.getOccupiedRooms() + 
                          ", Vacant: " + stats.getVacantRooms());
                }
            }
        }
    }
    
    /**
     * Thống kê booking
     */
    private void loadBookingStats(Connection connection, String landlordId, LandlordStats stats) throws SQLException {
        String query = "SELECT " +
                "COUNT(*) as TotalBookings, " +
                "SUM(CASE WHEN TrangThaiId = 1 THEN 1 ELSE 0 END) as PendingRequests, " +
                "SUM(CASE WHEN TrangThaiId = 2 THEN 1 ELSE 0 END) as ApprovedBookings, " +
                "SUM(CASE WHEN TrangThaiId = 3 THEN 1 ELSE 0 END) as RejectedBookings " +
                "FROM DatPhong WHERE ChuTroId = ?";
                
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, landlordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalBookings(rs.getInt("TotalBookings"));
                    stats.setPendingRequests(rs.getInt("PendingRequests"));
                    stats.setApprovedBookings(rs.getInt("ApprovedBookings"));
                    stats.setRejectedBookings(rs.getInt("RejectedBookings"));
                    
                    Log.d(TAG, "📋 Booking Stats - Total: " + stats.getTotalBookings() + 
                          ", Pending: " + stats.getPendingRequests() + 
                          ", Approved: " + stats.getApprovedBookings() + 
                          ", Rejected: " + stats.getRejectedBookings());
                }
            }
        }
    }
    
    /**
     * Thống kê doanh thu
     */
    private void loadRevenueStats(Connection connection, String landlordId, LandlordStats stats) throws SQLException {
        // Doanh thu tháng hiện tại
        String monthlyQuery = "SELECT ISNULL(SUM(bl.SoTien), 0) as MonthlyRevenue " +
                "FROM BienLai bl " +
                "INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId " +
                "WHERE dp.ChuTroId = ? " +
                "AND bl.DaXacNhan = 1 " +
                "AND MONTH(bl.ThoiGianTai) = MONTH(GETDATE()) " +
                "AND YEAR(bl.ThoiGianTai) = YEAR(GETDATE())";
                
        try (PreparedStatement stmt = connection.prepareStatement(monthlyQuery)) {
            stmt.setString(1, landlordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setMonthlyRevenue(rs.getLong("MonthlyRevenue"));
                    Log.d(TAG, "💰 Monthly Revenue: " + stats.getMonthlyRevenue());
                }
            }
        }
        
        // Tổng doanh thu
        String totalQuery = "SELECT ISNULL(SUM(bl.SoTien), 0) as TotalRevenue " +
                "FROM BienLai bl " +
                "INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId " +
                "WHERE dp.ChuTroId = ? AND bl.DaXacNhan = 1";
                
        try (PreparedStatement stmt = connection.prepareStatement(totalQuery)) {
            stmt.setString(1, landlordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalRevenue(rs.getLong("TotalRevenue"));
                    Log.d(TAG, "💎 Total Revenue: " + stats.getTotalRevenue());
                }
            }
        }
        
        // Số lượng thanh toán
        String paymentCountQuery = "SELECT " +
                "COUNT(*) as TotalPayments, " +
                "SUM(CASE WHEN DaXacNhan = 1 THEN 1 ELSE 0 END) as ConfirmedPayments, " +
                "SUM(CASE WHEN DaXacNhan = 0 THEN 1 ELSE 0 END) as PendingPayments " +
                "FROM BienLai bl " +
                "INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId " +
                "WHERE dp.ChuTroId = ?";
                
        try (PreparedStatement stmt = connection.prepareStatement(paymentCountQuery)) {
            stmt.setString(1, landlordId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.setTotalPayments(rs.getInt("TotalPayments"));
                    stats.setConfirmedPayments(rs.getInt("ConfirmedPayments"));
                    stats.setPendingPayments(rs.getInt("PendingPayments"));
                    
                    Log.d(TAG, "💳 Payment Stats - Total: " + stats.getTotalPayments() + 
                          ", Confirmed: " + stats.getConfirmedPayments() + 
                          ", Pending: " + stats.getPendingPayments());
                }
            }
        }
    }
    
    /**
     * Class chứa dữ liệu thống kê
     */
    public static class LandlordStats {
        // Thống kê phòng
        private int totalRooms = 0;
        private int activeRooms = 0;
        private int inactiveRooms = 0;
        private int occupiedRooms = 0;
        private int vacantRooms = 0;
        
        // Thống kê booking
        private int totalBookings = 0;
        private int pendingRequests = 0;
        private int approvedBookings = 0;
        private int rejectedBookings = 0;
        
        // Thống kê doanh thu
        private long monthlyRevenue = 0;
        private long totalRevenue = 0;
        private int totalPayments = 0;
        private int confirmedPayments = 0;
        private int pendingPayments = 0;
        
        // Getters and Setters
        public int getTotalRooms() { return totalRooms; }
        public void setTotalRooms(int totalRooms) { this.totalRooms = totalRooms; }
        
        public int getActiveRooms() { return activeRooms; }
        public void setActiveRooms(int activeRooms) { this.activeRooms = activeRooms; }
        
        public int getInactiveRooms() { return inactiveRooms; }
        public void setInactiveRooms(int inactiveRooms) { this.inactiveRooms = inactiveRooms; }
        
        public int getOccupiedRooms() { return occupiedRooms; }
        public void setOccupiedRooms(int occupiedRooms) { this.occupiedRooms = occupiedRooms; }
        
        public int getVacantRooms() { return vacantRooms; }
        public void setVacantRooms(int vacantRooms) { this.vacantRooms = vacantRooms; }
        
        public int getTotalBookings() { return totalBookings; }
        public void setTotalBookings(int totalBookings) { this.totalBookings = totalBookings; }
        
        public int getPendingRequests() { return pendingRequests; }
        public void setPendingRequests(int pendingRequests) { this.pendingRequests = pendingRequests; }
        
        public int getApprovedBookings() { return approvedBookings; }
        public void setApprovedBookings(int approvedBookings) { this.approvedBookings = approvedBookings; }
        
        public int getRejectedBookings() { return rejectedBookings; }
        public void setRejectedBookings(int rejectedBookings) { this.rejectedBookings = rejectedBookings; }
        
        public long getMonthlyRevenue() { return monthlyRevenue; }
        public void setMonthlyRevenue(long monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
        
        public long getTotalRevenue() { return totalRevenue; }
        public void setTotalRevenue(long totalRevenue) { this.totalRevenue = totalRevenue; }
        
        public int getTotalPayments() { return totalPayments; }
        public void setTotalPayments(int totalPayments) { this.totalPayments = totalPayments; }
        
        public int getConfirmedPayments() { return confirmedPayments; }
        public void setConfirmedPayments(int confirmedPayments) { this.confirmedPayments = confirmedPayments; }
        
        public int getPendingPayments() { return pendingPayments; }
        public void setPendingPayments(int pendingPayments) { this.pendingPayments = pendingPayments; }
        
        // Helper methods
        public String getFormattedMonthlyRevenue() {
            return String.format("%,d đ", monthlyRevenue);
        }
        
        public String getFormattedTotalRevenue() {
            return String.format("%,d đ", totalRevenue);
        }
        
        public double getOccupancyRate() {
            if (totalRooms == 0) return 0.0;
            return (double) occupiedRooms / totalRooms * 100;
        }
        
        public double getApprovalRate() {
            if (totalBookings == 0) return 0.0;
            return (double) approvedBookings / totalBookings * 100;
        }
    }
}