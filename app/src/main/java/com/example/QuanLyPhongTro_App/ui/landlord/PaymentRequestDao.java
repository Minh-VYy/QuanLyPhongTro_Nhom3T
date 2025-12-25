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
        
        String query = "SELECT " +
                "bl.BienLaiId, bl.DatPhongId, bl.SoTien, bl.NguoiTai, bl.TapTinId, bl.DaXacNhan, bl.ThoiGianTai, " +
                "dp.NguoiThueId, dp.ChuTroId, dp.Loai, " +
                "hs.HoTen as TenNguoiThue, " +
                "p.TieuDe as TenPhong " +
                "FROM BienLai bl " +
                "INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId " +
                "INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId " +
                "INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId " +
                "INNER JOIN Phong p ON dp.PhongId = p.PhongId " +
                "WHERE dp.ChuTroId = ? " +
                "ORDER BY bl.ThoiGianTai DESC";

        Log.d(TAG, "Executing query: " + query);
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, chuTroId);
            Log.d(TAG, "Query parameter: " + chuTroId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    PaymentRequest payment = new PaymentRequest();
                    payment.setBienLaiId(rs.getString("BienLaiId"));
                    payment.setDatPhongId(rs.getString("DatPhongId"));
                    payment.setNguoiThueId(rs.getString("NguoiThueId"));
                    payment.setChuTroId(rs.getString("ChuTroId"));
                    payment.setTenNguoiThue(rs.getString("TenNguoiThue"));
                    payment.setTenPhong(rs.getString("TenPhong"));
                    payment.setSoTien(rs.getLong("SoTien"));
                    payment.setLoaiThanhToan(rs.getString("Loai")); // Loại từ DatPhong
                    payment.setTapTinId(rs.getString("TapTinId"));
                    payment.setNgayTao(rs.getTimestamp("ThoiGianTai"));
                    
                    // Chuyển đổi trạng thái từ DaXacNhan (BIT) sang text
                    boolean daXacNhan = rs.getBoolean("DaXacNhan");
                    if (daXacNhan) {
                        payment.setTrangThai("DaXacNhan");
                    } else {
                        payment.setTrangThai("ChoXacNhan");
                    }
                    
                    payments.add(payment);
                    Log.d(TAG, "Found payment #" + count + ": " + payment.getTenNguoiThue() + " - " + payment.getFormattedAmount() + " - " + payment.getTrangThai());
                }
                Log.d(TAG, "Total records processed: " + count);
            }
        } catch (SQLException e) {
            Log.e(TAG, "SQL Error: " + e.getMessage(), e);
            
            try {
                // Debug query - kiểm tra dữ liệu có trong database không
                PreparedStatement countStmt = connection.prepareStatement("SELECT COUNT(*) as total FROM BienLai");
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    Log.d(TAG, "Total BienLai records: " + countRs.getInt("total"));
                }
                countRs.close();
                countStmt.close();
                
                // Kiểm tra DatPhong cho ChuTroId này
                PreparedStatement datPhongStmt = connection.prepareStatement("SELECT COUNT(*) as total FROM DatPhong WHERE ChuTroId = ?");
                datPhongStmt.setString(1, chuTroId);
                ResultSet datPhongRs = datPhongStmt.executeQuery();
                if (datPhongRs.next()) {
                    Log.d(TAG, "DatPhong records for ChuTroId " + chuTroId + ": " + datPhongRs.getInt("total"));
                }
                datPhongRs.close();
                datPhongStmt.close();
                
            } catch (SQLException debugE) {
                Log.e(TAG, "Debug query error: " + debugE.getMessage(), debugE);
            }
        }
        
        Log.d(TAG, "Final result size: " + payments.size());
        Log.d(TAG, "=== END DEBUGGING ===");
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