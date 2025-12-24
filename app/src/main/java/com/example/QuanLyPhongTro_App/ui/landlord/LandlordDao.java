package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LandlordDao {

    /**
     * FINAL VERSION: Checks credentials and retrieves user details including the ID.
     * @param connection The database connection.
     * @param email The user's email.
     * @param passwordHash The user's password hash.
     * @return A Landlord object if login is successful, null otherwise.
     */
    public Landlord login(Connection connection, String email, String passwordHash) {
        Log.d("LandlordDao", "Attempting login for email: " + email);

        String sql = "SELECT u.NguoiDungId, u.DienThoai, h.HoTen " +
                     "FROM dbo.NguoiDung u " +
                     "LEFT JOIN dbo.HoSoNguoiDung h ON u.NguoiDungId = h.NguoiDungId " +
                     "WHERE u.Email = ? AND u.PasswordHash = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, passwordHash);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    Log.d("LandlordDao", "Login SUCCESS for email: " + email);
                    String nguoiDungId = resultSet.getString("NguoiDungId");
                    String hoTen = resultSet.getString("HoTen");
                    String dienThoai = resultSet.getString("DienThoai");
                    return new Landlord(nguoiDungId, email, passwordHash, hoTen, dienThoai);
                } else {
                    Log.w("LandlordDao", "Login FAILED for email: " + email + ". No user found with matching credentials.");
                    return null;
                }
            }
        } catch (SQLException e) {
            Log.e("LandlordDao", "SQLException during login for " + email, e);
            return null;
        }
    }

    /**
     * Registers a new landlord in the database.
     * @param connection The database connection.
     * @param landlord The Landlord object containing registration info.
     * @return A string indicating the result: "SUCCESS", "USER_EXISTS", or "ERROR".
     */
    public String register(Connection connection, Landlord landlord) {
        String email = landlord.getEmail();
        String checkUserSql = "SELECT 1 FROM dbo.NguoiDung WHERE Email = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkUserSql)) {
            checkStmt.setString(1, email);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    return "USER_EXISTS";
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR";
        }

        try {
            connection.setAutoCommit(false);
            int chuTroRoleId = -1;
            String roleSql = "SELECT VaiTroId FROM dbo.VaiTro WHERE TenVaiTro = N'ChuTro'";
            try (PreparedStatement roleStmt = connection.prepareStatement(roleSql);
                 ResultSet rs = roleStmt.executeQuery()) {
                if (rs.next()) {
                    chuTroRoleId = rs.getInt(1);
                } else {
                    connection.rollback();
                    return "ERROR";
                }
            }

            UUID newUserGuid = UUID.fromString(landlord.getNguoiDungId()); // Use provided ID if available
            String insertUserSql = "INSERT INTO dbo.NguoiDung (NguoiDungId, Email, DienThoai, PasswordHash, VaiTroId, IsEmailXacThuc) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertUserStmt = connection.prepareStatement(insertUserSql)) {
                insertUserStmt.setObject(1, newUserGuid);
                insertUserStmt.setString(2, landlord.getEmail());
                insertUserStmt.setString(3, landlord.getDienThoai());
                insertUserStmt.setString(4, landlord.getPasswordHash());
                insertUserStmt.setInt(5, chuTroRoleId);
                insertUserStmt.setBoolean(6, false);
                insertUserStmt.executeUpdate();
            }

            String insertProfileSql = "INSERT INTO dbo.HoSoNguoiDung (NguoiDungId, HoTen) VALUES (?, ?)";
            try (PreparedStatement insertProfileStmt = connection.prepareStatement(insertProfileSql)) {
                insertProfileStmt.setObject(1, newUserGuid);
                insertProfileStmt.setString(2, landlord.getHoTen());
                insertProfileStmt.executeUpdate();
            }

            String insertRoleLinkSql = "INSERT INTO dbo.NguoiDungVaiTro (NguoiDungId, VaiTroId) VALUES (?, ?)";
            try (PreparedStatement insertRoleLinkStmt = connection.prepareStatement(insertRoleLinkSql)) {
                insertRoleLinkStmt.setObject(1, newUserGuid);
                insertRoleLinkStmt.setInt(2, chuTroRoleId);
                insertRoleLinkStmt.executeUpdate();
            }

            connection.commit();
            return "SUCCESS";

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return "ERROR";
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
