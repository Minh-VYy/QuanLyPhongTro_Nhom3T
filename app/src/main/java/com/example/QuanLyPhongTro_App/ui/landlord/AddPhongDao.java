package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AddPhongDao {
    private static final String TAG = "AddPhongDao";

    /**
     * Thêm phòng mới vào database
     */
    public boolean addPhong(Connection conn, String chuTroId, String tieuDe, long giaTien, String moTa) {
        try {
            // Lấy NhaTroId đầu tiên của chủ trọ (hoặc tạo mới nếu chưa có)
            String nhaTroId = getOrCreateNhaTroId(conn, chuTroId);
            if (nhaTroId == null) {
                Log.e(TAG, "Cannot get or create NhaTroId for ChuTroId: " + chuTroId);
                return false;
            }

            // Tạo PhongId mới
            String phongId = UUID.randomUUID().toString();

            // Insert phòng mới - Tự động duyệt khi đăng tin
            String insertQuery = "INSERT INTO Phong (PhongId, NhaTroId, TieuDe, GiaTien, TienCoc, " +
                               "DienTich, SoNguoiToiDa, TrangThai, DiemTrungBinh, SoLuongDanhGia, IsDuyet, CreatedAt) " +
                               "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, GETDATE())";

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, phongId);
                stmt.setString(2, nhaTroId);
                stmt.setString(3, tieuDe);
                stmt.setLong(4, giaTien);
                stmt.setLong(5, giaTien); // TienCoc = GiaTien (mặc định)
                stmt.setDouble(6, 25.0); // DienTich mặc định 25m2
                stmt.setInt(7, 2); // SoNguoiToiDa mặc định 2 người
                stmt.setString(8, "Còn trống"); // TrangThai mặc định
                stmt.setFloat(9, 0.0f); // DiemTrungBinh mặc định 0
                stmt.setInt(10, 0); // SoLuongDanhGia mặc định 0
                // IsDuyet = 1 được set trực tiếp trong query (tự động duyệt)

                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    Log.d(TAG, "✅ Successfully added and auto-approved phong: " + tieuDe + " with ID: " + phongId);
                    return true;
                } else {
                    Log.e(TAG, "❌ Failed to add phong: " + tieuDe);
                    return false;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "❌ SQL Error adding phong: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy NhaTroId đầu tiên của chủ trọ, hoặc tạo mới nếu chưa có
     */
    private String getOrCreateNhaTroId(Connection conn, String chuTroId) {
        try {
            // Kiểm tra xem chủ trọ đã có nhà trọ nào chưa
            String selectQuery = "SELECT TOP 1 NhaTroId FROM NhaTro WHERE ChuTroId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setString(1, chuTroId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String existingId = rs.getString("NhaTroId");
                        Log.d(TAG, "Found existing NhaTroId: " + existingId);
                        return existingId;
                    }
                }
            }

            // Nếu chưa có nhà trọ, tạo mới
            Log.d(TAG, "No existing NhaTro found, creating new one for ChuTroId: " + chuTroId);
            return createDefaultNhaTro(conn, chuTroId);

        } catch (SQLException e) {
            Log.e(TAG, "Error getting or creating NhaTroId: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Tạo nhà trọ mặc định cho chủ trọ
     */
    private String createDefaultNhaTro(Connection conn, String chuTroId) {
        try {
            String nhaTroId = UUID.randomUUID().toString();
            
            // Lấy QuanHuyenId và PhuongId (tạo nếu chưa có)
            int quanHuyenId = getOrCreateQuanHuyen(conn, "Quận 1");
            int phuongId = getOrCreatePhuong(conn, "Phường 1", quanHuyenId);

            String insertQuery = "INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, " +
                               "QuanHuyenId, PhuongId, CreatedAt) " +
                               "VALUES (?, ?, ?, ?, ?, ?, GETDATE())";

            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, nhaTroId);
                stmt.setString(2, chuTroId);
                stmt.setString(3, "Nhà trọ của tôi");
                stmt.setString(4, "Địa chỉ nhà trọ");
                stmt.setInt(5, quanHuyenId);
                stmt.setInt(6, phuongId);

                int result = stmt.executeUpdate();
                if (result > 0) {
                    Log.d(TAG, "✅ Created default NhaTro with ID: " + nhaTroId);
                    return nhaTroId;
                } else {
                    Log.e(TAG, "❌ Failed to create default NhaTro");
                    return null;
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Error creating default NhaTro: " + e.getMessage(), e);
            return null;
        }
    }

    /**
     * Lấy hoặc tạo QuanHuyen
     */
    private int getOrCreateQuanHuyen(Connection conn, String tenQuan) {
        try {
            // Kiểm tra đã có chưa
            String selectQuery = "SELECT QuanHuyenId FROM QuanHuyen WHERE Ten = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setString(1, tenQuan);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("QuanHuyenId");
                    }
                }
            }

            // Tạo mới nếu chưa có
            String insertQuery = "INSERT INTO QuanHuyen (Ten) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, tenQuan);
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Error with QuanHuyen: " + e.getMessage(), e);
        }
        return 1; // Fallback
    }

    /**
     * Lấy hoặc tạo Phuong
     */
    private int getOrCreatePhuong(Connection conn, String tenPhuong, int quanHuyenId) {
        try {
            // Kiểm tra đã có chưa
            String selectQuery = "SELECT PhuongId FROM Phuong WHERE Ten = ? AND QuanHuyenId = ?";
            try (PreparedStatement stmt = conn.prepareStatement(selectQuery)) {
                stmt.setString(1, tenPhuong);
                stmt.setInt(2, quanHuyenId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("PhuongId");
                    }
                }
            }

            // Tạo mới nếu chưa có
            String insertQuery = "INSERT INTO Phuong (Ten, QuanHuyenId) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, tenPhuong);
                stmt.setInt(2, quanHuyenId);
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            Log.e(TAG, "Error with Phuong: " + e.getMessage(), e);
        }
        return 1; // Fallback
    }
}