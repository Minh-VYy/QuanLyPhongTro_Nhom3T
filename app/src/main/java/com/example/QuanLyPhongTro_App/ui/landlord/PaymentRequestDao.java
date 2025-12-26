package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PaymentRequestDao {
    private static final String TAG = "PaymentRequestDao";

    /**
     * Lấy danh sách yêu cầu thanh toán của chủ trọ
     */
    public List<PaymentRequest> getPaymentRequestsByLandlord(Connection connection, String chuTroId) {
        List<PaymentRequest> payments = new ArrayList<>();
        
        Log.d(TAG, "=== DEBUGGING PAYMENT REQUESTS ===");
        Log.d(TAG, "Input ChuTroId: " + chuTroId);
        
        // Check if there are any DatPhong records for this landlord first
        String checkQuery = "SELECT COUNT(*) as total FROM DatPhong WHERE ChuTroId = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setString(1, chuTroId);
            try (ResultSet checkRs = checkStmt.executeQuery()) {
                if (checkRs.next()) {
                    int datPhongCount = checkRs.getInt("total");
                    Log.d(TAG, "DatPhong records for ChuTroId: " + datPhongCount);
                    
                    if (datPhongCount == 0) {
                        Log.d(TAG, "No DatPhong records found for ChuTroId: " + chuTroId);
                        return payments; // Return empty list
                    }
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Check query error: " + e.getMessage(), e);
            return payments;
        }

        String query = "SELECT " +
                "bl.BienLaiId, bl.DatPhongId, bl.SoTien, bl.NguoiTai, bl.TapTinId, bl.DaXacNhan, bl.ThoiGianTai, " +
                "dp.NguoiThueId, dp.ChuTroId, " +
                "ISNULL(dp.Loai, 'Thanh toán tiền thuê') as Loai, " +
                "ISNULL(hs.HoTen, 'Người thuê') as TenNguoiThue, " +
                "ISNULL(p.TieuDe, 'Phòng trọ') as TenPhong " +
                "FROM BienLai bl " +
                "INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId " +
                "LEFT JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId " +
                "LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId " +
                "LEFT JOIN Phong p ON dp.PhongId = p.PhongId " +
                "WHERE dp.ChuTroId = ? " +
                "ORDER BY bl.ThoiGianTai DESC";

        Log.d(TAG, "Executing payment query: " + query);
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            Log.d(TAG, "Query parameter: " + chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    PaymentRequest payment = new PaymentRequest();
                    
                    // Set basic fields with null safety
                    payment.setBienLaiId(rs.getString("BienLaiId"));
                    payment.setDatPhongId(rs.getString("DatPhongId"));
                    payment.setNguoiThueId(rs.getString("NguoiThueId"));
                    payment.setChuTroId(rs.getString("ChuTroId"));
                    payment.setTenNguoiThue(rs.getString("TenNguoiThue"));
                    payment.setTenPhong(rs.getString("TenPhong"));
                    payment.setSoTien(rs.getLong("SoTien"));
                    payment.setLoaiThanhToan(rs.getString("Loai"));
                    payment.setTapTinId(rs.getString("TapTinId"));
                    payment.setNgayTao(rs.getTimestamp("ThoiGianTai"));
                    
                    // Convert DaXacNhan (BIT) to status text
                    boolean daXacNhan = rs.getBoolean("DaXacNhan");
                    payment.setTrangThai(daXacNhan ? "DaXacNhan" : "ChoXacNhan");
                    
                    // Set default note if needed
                    payment.setGhiChu("Yêu cầu thanh toán " + payment.getLoaiThanhToan());
                    
                    payments.add(payment);
                    Log.d(TAG, "✅ Found payment #" + count + ": " + payment.getTenNguoiThue() + " - " + payment.getFormattedAmount() + " (" + payment.getTrangThai() + ")");
                }
                Log.d(TAG, "✅ Total payment records processed: " + count);
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error: " + e.getMessage(), e);
            
            // Enhanced debugging for payments
            try {
                // Check total BienLai records
                PreparedStatement countStmt = connection.prepareStatement("SELECT COUNT(*) as total FROM BienLai");
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    Log.d(TAG, "📊 Total BienLai records in database: " + countRs.getInt("total"));
                }
                countRs.close();
                countStmt.close();
                
                // Check BienLai with DatPhong JOIN for this landlord
                PreparedStatement joinStmt = connection.prepareStatement(
                    "SELECT COUNT(*) as total FROM BienLai bl " +
                    "INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId " +
                    "WHERE dp.ChuTroId = ?");
                joinStmt.setString(1, chuTroId);
                ResultSet joinRs = joinStmt.executeQuery();
                if (joinRs.next()) {
                    Log.d(TAG, "📊 BienLai records for ChuTroId " + chuTroId + ": " + joinRs.getInt("total"));
                }
                joinRs.close();
                joinStmt.close();
                
                // Check table structure
                PreparedStatement tableStmt = connection.prepareStatement(
                    "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('BienLai', 'DatPhong')");
                ResultSet tableRs = tableStmt.executeQuery();
                Log.d(TAG, "📋 Available payment-related tables:");
                while (tableRs.next()) {
                    Log.d(TAG, "  - " + tableRs.getString("TABLE_NAME"));
                }
                tableRs.close();
                tableStmt.close();
                
            } catch (SQLException debugE) {
                Log.e(TAG, "❌ Debug query error: " + debugE.getMessage(), debugE);
            }
        }
        
        Log.d(TAG, "📈 Final payment result size: " + payments.size());
        Log.d(TAG, "=== END PAYMENT DEBUGGING ===");
        return payments;
    }

    /**
     * Cập nhật trạng thái thanh toán
     */
    public boolean updatePaymentStatus(Connection connection, String bienLaiId, String newStatus) {
        Log.d(TAG, "Updating payment status: " + bienLaiId + " -> " + newStatus);
        
        // Chuyển đổi status text sang boolean
        boolean daXacNhan = "DaXacNhan".equals(newStatus);
        
        String query = "UPDATE BienLai SET DaXacNhan = ? WHERE BienLaiId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setBoolean(1, daXacNhan);
            stmt.setString(2, bienLaiId);
            
            int rowsUpdated = stmt.executeUpdate();
            Log.d(TAG, "Rows updated: " + rowsUpdated);
            
            return rowsUpdated > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating payment status: " + e.getMessage(), e);
            return false;
        }
    }
}