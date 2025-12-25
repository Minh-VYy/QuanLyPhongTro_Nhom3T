package com.example.QuanLyPhongTro_App.ui.landlord;

import android.util.Log;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class UserProfileDao {
    private static final String TAG = "UserProfileDao";

    /**
     * Lấy thông tin hồ sơ người dùng từ database
     */
    public UserProfile getUserProfile(Connection connection, String userId) {
        UserProfile profile = null;
        
        Log.d(TAG, "=== LOADING USER PROFILE ===");
        Log.d(TAG, "UserId: " + userId);
        
        String query = "SELECT " +
                "nd.NguoiDungId, nd.Email, nd.DienThoai, nd.VaiTroId, nd.IsKhoa, nd.IsEmailXacThuc, " +
                "nd.CreatedAt, nd.UpdatedAt, " +
                "hs.HoTen, hs.NgaySinh, hs.LoaiGiayTo, hs.GhiChu, " +
                "vt.TenVaiTro " +
                "FROM NguoiDung nd " +
                "LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId " +
                "LEFT JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId " +
                "WHERE nd.NguoiDungId = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    profile = new UserProfile();
                    
                    // Thông tin cơ bản từ NguoiDung
                    profile.setNguoiDungId(rs.getString("NguoiDungId"));
                    profile.setEmail(rs.getString("Email"));
                    profile.setDienThoai(rs.getString("DienThoai"));
                    profile.setVaiTroId(rs.getInt("VaiTroId"));
                    profile.setTenVaiTro(rs.getString("TenVaiTro"));
                    profile.setIsKhoa(rs.getBoolean("IsKhoa"));
                    profile.setIsEmailXacThuc(rs.getBoolean("IsEmailXacThuc"));
                    profile.setCreatedAt(rs.getTimestamp("CreatedAt"));
                    profile.setUpdatedAt(rs.getTimestamp("UpdatedAt"));
                    
                    // Thông tin chi tiết từ HoSoNguoiDung (chỉ có những cột thật sự tồn tại)
                    profile.setHoTen(rs.getString("HoTen"));
                    profile.setNgaySinh(rs.getDate("NgaySinh"));
                    profile.setLoaiGiayTo(rs.getString("LoaiGiayTo"));
                    profile.setGhiChu(rs.getString("GhiChu"));
                    
                    Log.d(TAG, "✅ Profile loaded successfully for: " + profile.getHoTen());
                    Log.d(TAG, "📧 Email: " + profile.getEmail());
                    Log.d(TAG, "📱 Phone: " + profile.getDienThoai());
                    Log.d(TAG, "👤 Role: " + profile.getTenVaiTro());
                    
                } else {
                    Log.w(TAG, "⚠️ No profile found for userId: " + userId);
                }
            }
        } catch (SQLException e) {
            Log.e(TAG, "❌ Error loading user profile: " + e.getMessage(), e);
        }
        
        return profile;
    }
    
    /**
     * Cập nhật thông tin hồ sơ người dùng
     */
    public boolean updateUserProfile(Connection connection, UserProfile profile) {
        Log.d(TAG, "=== UPDATING USER PROFILE ===");
        Log.d(TAG, "UserId: " + profile.getNguoiDungId());
        
        boolean success = false;
        
        try {
            // Bắt đầu transaction
            connection.setAutoCommit(false);
            
            // 1. Cập nhật bảng NguoiDung
            String updateNguoiDungQuery = "UPDATE NguoiDung SET " +
                    "Email = ?, DienThoai = ?, UpdatedAt = SYSDATETIMEOFFSET() " +
                    "WHERE NguoiDungId = ?";
                    
            try (PreparedStatement stmt = connection.prepareStatement(updateNguoiDungQuery)) {
                stmt.setString(1, profile.getEmail());
                stmt.setString(2, profile.getDienThoai());
                stmt.setString(3, profile.getNguoiDungId());
                
                int rowsUpdated = stmt.executeUpdate();
                Log.d(TAG, "📝 NguoiDung updated: " + rowsUpdated + " rows");
            }
            
            // 2. Cập nhật hoặc tạo mới HoSoNguoiDung
            String checkHoSoQuery = "SELECT COUNT(*) FROM HoSoNguoiDung WHERE NguoiDungId = ?";
            boolean hoSoExists = false;
            
            try (PreparedStatement stmt = connection.prepareStatement(checkHoSoQuery)) {
                stmt.setString(1, profile.getNguoiDungId());
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        hoSoExists = rs.getInt(1) > 0;
                    }
                }
            }
            
            if (hoSoExists) {
                // Cập nhật HoSoNguoiDung
                String updateHoSoQuery = "UPDATE HoSoNguoiDung SET " +
                        "HoTen = ?, NgaySinh = ?, LoaiGiayTo = ?, GhiChu = ? " +
                        "WHERE NguoiDungId = ?";
                        
                try (PreparedStatement stmt = connection.prepareStatement(updateHoSoQuery)) {
                    stmt.setString(1, profile.getHoTen());
                    stmt.setDate(2, profile.getNgaySinh() != null ? new java.sql.Date(profile.getNgaySinh().getTime()) : null);
                    stmt.setString(3, profile.getLoaiGiayTo());
                    stmt.setString(4, profile.getGhiChu());
                    stmt.setString(5, profile.getNguoiDungId());
                    
                    int rowsUpdated = stmt.executeUpdate();
                    Log.d(TAG, "📝 HoSoNguoiDung updated: " + rowsUpdated + " rows");
                }
            } else {
                // Tạo mới HoSoNguoiDung
                String insertHoSoQuery = "INSERT INTO HoSoNguoiDung " +
                        "(NguoiDungId, HoTen, NgaySinh, LoaiGiayTo, GhiChu, CreatedAt) " +
                        "VALUES (?, ?, ?, ?, ?, SYSDATETIMEOFFSET())";
                        
                try (PreparedStatement stmt = connection.prepareStatement(insertHoSoQuery)) {
                    stmt.setString(1, profile.getNguoiDungId());
                    stmt.setString(2, profile.getHoTen());
                    stmt.setDate(3, profile.getNgaySinh() != null ? new java.sql.Date(profile.getNgaySinh().getTime()) : null);
                    stmt.setString(4, profile.getLoaiGiayTo());
                    stmt.setString(5, profile.getGhiChu());
                    
                    int rowsInserted = stmt.executeUpdate();
                    Log.d(TAG, "📝 HoSoNguoiDung inserted: " + rowsInserted + " rows");
                }
            }
            
            // Commit transaction
            connection.commit();
            success = true;
            Log.d(TAG, "✅ Profile updated successfully");
            
        } catch (SQLException e) {
            Log.e(TAG, "❌ Error updating profile: " + e.getMessage(), e);
            try {
                connection.rollback();
                Log.d(TAG, "🔄 Transaction rolled back");
            } catch (SQLException rollbackEx) {
                Log.e(TAG, "❌ Error rolling back transaction", rollbackEx);
            }
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                Log.e(TAG, "Error resetting auto-commit", e);
            }
        }
        
        return success;
    }
    
    /**
     * Class chứa thông tin hồ sơ người dùng
     */
    public static class UserProfile {
        // Thông tin từ NguoiDung
        private String nguoiDungId;
        private String email;
        private String dienThoai;
        private int vaiTroId;
        private String tenVaiTro;
        private boolean isKhoa;
        private boolean isEmailXacThuc;
        private Date createdAt;
        private Date updatedAt;
        
        // Thông tin từ HoSoNguoiDung (chỉ những cột thật sự tồn tại)
        private String hoTen;
        private Date ngaySinh;
        private String loaiGiayTo;
        private String ghiChu;
        
        // Constructors
        public UserProfile() {}
        
        // Getters and Setters cho NguoiDung
        public String getNguoiDungId() { return nguoiDungId; }
        public void setNguoiDungId(String nguoiDungId) { this.nguoiDungId = nguoiDungId; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getDienThoai() { return dienThoai; }
        public void setDienThoai(String dienThoai) { this.dienThoai = dienThoai; }
        
        public int getVaiTroId() { return vaiTroId; }
        public void setVaiTroId(int vaiTroId) { this.vaiTroId = vaiTroId; }
        
        public String getTenVaiTro() { return tenVaiTro; }
        public void setTenVaiTro(String tenVaiTro) { this.tenVaiTro = tenVaiTro; }
        
        public boolean isKhoa() { return isKhoa; }
        public void setIsKhoa(boolean isKhoa) { this.isKhoa = isKhoa; }
        
        public boolean isEmailXacThuc() { return isEmailXacThuc; }
        public void setIsEmailXacThuc(boolean isEmailXacThuc) { this.isEmailXacThuc = isEmailXacThuc; }
        
        public Date getCreatedAt() { return createdAt; }
        public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
        
        public Date getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(Date updatedAt) { this.updatedAt = updatedAt; }
        
        // Getters and Setters cho HoSoNguoiDung
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        
        public Date getNgaySinh() { return ngaySinh; }
        public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
        
        public String getLoaiGiayTo() { return loaiGiayTo; }
        public void setLoaiGiayTo(String loaiGiayTo) { this.loaiGiayTo = loaiGiayTo; }
        
        public String getGhiChu() { return ghiChu; }
        public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
        
        // Dummy getters/setters for compatibility with existing UI code
        public String getGioiTinh() { return "Nam"; } // Default value
        public void setGioiTinh(String gioiTinh) { /* No-op */ }
        
        public String getDiaChi() { return ghiChu; } // Use GhiChu as address for now
        public void setDiaChi(String diaChi) { this.ghiChu = diaChi; } // Store in GhiChu
        
        public String getCccd() { return loaiGiayTo; } // Use LoaiGiayTo as ID document
        public void setCccd(String cccd) { this.loaiGiayTo = cccd; }
        
        public Date getNgayCapCCCD() { return null; }
        public void setNgayCapCCCD(Date ngayCapCCCD) { /* No-op */ }
        
        public String getNoiCapCCCD() { return null; }
        public void setNoiCapCCCD(String noiCapCCCD) { /* No-op */ }
        
        public String getAnhDaiDien() { return null; }
        public void setAnhDaiDien(String anhDaiDien) { /* No-op */ }
        
        public String getAnhCCCDMatTruoc() { return null; }
        public void setAnhCCCDMatTruoc(String anhCCCDMatTruoc) { /* No-op */ }
        
        public String getAnhCCCDMatSau() { return null; }
        public void setAnhCCCDMatSau(String anhCCCDMatSau) { /* No-op */ }
        
        public String getTenNganHang() { return null; }
        public void setTenNganHang(String tenNganHang) { /* No-op */ }
        
        public String getSoTaiKhoan() { return null; }
        public void setSoTaiKhoan(String soTaiKhoan) { /* No-op */ }
        
        public String getTenChuTaiKhoan() { return null; }
        public void setTenChuTaiKhoan(String tenChuTaiKhoan) { /* No-op */ }
        
        // Helper methods
        public String getDisplayName() {
            if (hoTen != null && !hoTen.trim().isEmpty()) {
                return hoTen;
            }
            if (email != null && !email.trim().isEmpty()) {
                return email.split("@")[0]; // Lấy phần trước @ của email
            }
            return "Người dùng";
        }
        
        public String getFormattedBirthDate() {
            if (ngaySinh != null) {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                return sdf.format(ngaySinh);
            }
            return "";
        }
        
        public boolean hasCompleteProfile() {
            return hoTen != null && !hoTen.trim().isEmpty() &&
                   dienThoai != null && !dienThoai.trim().isEmpty() &&
                   email != null && !email.trim().isEmpty();
        }
    }
}