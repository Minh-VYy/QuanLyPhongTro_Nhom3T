package com.example.QuanLyPhongTro_App.ui.tenant;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TenantDao {
    private static final String TAG = "TenantDao";

    public Tenant login(Connection connection, String email, String password) {
        String query = "SELECT nd.NguoiDungId, nd.Email, nd.DienThoai, nd.IsKhoa, nd.IsEmailXacThuc, " +
                      "hs.HoTen, hs.NgaySinh, hs.LoaiGiayTo, hs.GhiChu " +
                      "FROM NguoiDung nd " +
                      "LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId " +
                      "INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId " +
                      "WHERE nd.Email = ? AND nd.PasswordHash = ? AND vt.TenVaiTro = 'NguoiThue'";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            
            Log.d(TAG, "Executing login query for email: " + email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Check if account is locked
                    boolean isLocked = rs.getBoolean("IsKhoa");
                    if (isLocked) {
                        Log.w(TAG, "Account is locked for email: " + email);
                        return null;
                    }

                    // Create Tenant object
                    Tenant tenant = new Tenant();
                    tenant.setNguoiDungId(rs.getString("NguoiDungId"));
                    tenant.setEmail(rs.getString("Email"));
                    tenant.setDienThoai(rs.getString("DienThoai"));
                    tenant.setIsEmailXacThuc(rs.getBoolean("IsEmailXacThuc"));
                    
                    // Profile information
                    tenant.setHoTen(rs.getString("HoTen"));
                    tenant.setNgaySinh(rs.getDate("NgaySinh"));
                    tenant.setLoaiGiayTo(rs.getString("LoaiGiayTo"));
                    tenant.setGhiChu(rs.getString("GhiChu"));

                    Log.d(TAG, "Login successful for tenant: " + tenant.getHoTen());
                    return tenant;
                } else {
                    Log.w(TAG, "No tenant found with email: " + email);
                    return null;
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error during tenant login", e);
            return null;
        }
    }

    public boolean updateLastLogin(Connection connection, String nguoiDungId) {
        String query = "UPDATE NguoiDung SET UpdatedAt = SYSDATETIMEOFFSET() WHERE NguoiDungId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nguoiDungId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Log.e(TAG, "Error updating last login", e);
            return false;
        }
    }
}