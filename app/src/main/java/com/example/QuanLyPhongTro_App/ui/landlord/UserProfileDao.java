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
                "hs.HoTen, hs.NgaySinh, hs.GioiTinh, hs.DiaChi, hs.CCCD, hs.NgayCapCCCD, " +
                "hs.NoiCapCCCD, hs.AnhDaiDien, hs.AnhCCCDMatTruoc, hs.AnhCCCDMatSau, " +
                "hs.TenNganHang, hs.SoTaiKhoan, hs.TenChuTaiKhoan, " +
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
                    
                    // Thông tin chi tiết từ HoSoNguoiDung
                    profile.setHoTen(rs.getString("HoTen"));
                    profile.setNgaySinh(rs.getDate("NgaySinh"));
                    profile.setGioiTinh(rs.getString("GioiTinh"));
                    profile.setDiaChi(rs.getString("DiaChi"));
                    profile.setCccd(rs.getString("CCCD"));
                    profile.setNgayCapCCCD(rs.getDate("NgayCapCCCD"));
                    profile.setNoiCapCCCD(rs.getString("NoiCapCCCD"));
                    profile.setAnhDaiDien(rs.getString("AnhDaiDien"));
                    profile.setAnhCCCDMatTruoc(rs.getString("AnhCCCDMatTruoc"));
                    profile.setAnhCCCDMatSau(rs.getString("AnhCCCDMatSau"));
                    
                    // Thông tin ngân hàng
                    profile.setTenNganHang(rs.getString("TenNganHang"));
                    profile.setSoTaiKhoan(rs.getString("SoTaiKhoan"));
                    profile.setTenChuTaiKhoan(rs.getString("TenChuTaiKhoan"));
                    
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
                    "Email = ?, DienThoai = ?, UpdatedAt = GETDATE() " +
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
                        "HoTen = ?, NgaySinh = ?, GioiTinh = ?, DiaChi = ?, " +
                        "CCCD = ?, NgayCapCCCD = ?, NoiCapCCCD = ?, " +
                        "TenNganHang = ?, SoTaiKhoan = ?, TenChuTaiKhoan = ? " +
                        "WHERE NguoiDungId = ?";
                        
                try (PreparedStatement stmt = connection.prepareStatement(updateHoSoQuery)) {
                    stmt.setString(1, profile.getHoTen());
                    stmt.setDate(2, profile.getNgaySinh() != null ? new java.sql.Date(profile.getNgaySinh().getTime()) : null);
                    stmt.setString(3, profile.getGioiTinh());
                    stmt.setString(4, profile.getDiaChi());
                    stmt.setString(5, profile.getCccd());
                    stmt.setDate(6, profile.getNgayCapCCCD() != null ? new java.sql.Date(profile.getNgayCapCCCD().getTime()) : null);
                    stmt.setString(7, profile.getNoiCapCCCD());
                    stmt.setString(8, profile.getTenNganHang());
                    stmt.setString(9, profile.getSoTaiKhoan());
                    stmt.setString(10, profile.getTenChuTaiKhoan());
                    stmt.setString(11, profile.getNguoiDungId());
                    
                    int rowsUpdated = stmt.executeUpdate();
                    Log.d(TAG, "📝 HoSoNguoiDung updated: " + rowsUpdated + " rows");
                }
            } else {
                // Tạo mới HoSoNguoiDung
                String insertHoSoQuery = "INSERT INTO HoSoNguoiDung " +
                        "(NguoiDungId, HoTen, NgaySinh, GioiTinh, DiaChi, CCCD, NgayCapCCCD, NoiCapCCCD, " +
                        "TenNganHang, SoTaiKhoan, TenChuTaiKhoan) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        
                try (PreparedStatement stmt = connection.prepareStatement(insertHoSoQuery)) {
                    stmt.setString(1, profile.getNguoiDungId());
                    stmt.setString(2, profile.getHoTen());
                    stmt.setDate(3, profile.getNgaySinh() != null ? new java.sql.Date(profile.getNgaySinh().getTime()) : null);
                    stmt.setString(4, profile.getGioiTinh());
                    stmt.setString(5, profile.getDiaChi());
                    stmt.setString(6, profile.getCccd());
                    stmt.setDate(7, profile.getNgayCapCCCD() != null ? new java.sql.Date(profile.getNgayCapCCCD().getTime()) : null);
                    stmt.setString(8, profile.getNoiCapCCCD());
                    stmt.setString(9, profile.getTenNganHang());
                    stmt.setString(10, profile.getSoTaiKhoan());
                    stmt.setString(11, profile.getTenChuTaiKhoan());
                    
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
        
        // Thông tin từ HoSoNguoiDung
        private String hoTen;
        private Date ngaySinh;
        private String gioiTinh;
        private String diaChi;
        private String cccd;
        private Date ngayCapCCCD;
        private String noiCapCCCD;
        private String anhDaiDien;
        private String anhCCCDMatTruoc;
        private String anhCCCDMatSau;
        private String tenNganHang;
        private String soTaiKhoan;
        private String tenChuTaiKhoan;
        
        // Constructors
        public UserProfile() {}
        
        // Getters and Setters
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
        
        public String getHoTen() { return hoTen; }
        public void setHoTen(String hoTen) { this.hoTen = hoTen; }
        
        public Date getNgaySinh() { return ngaySinh; }
        public void setNgaySinh(Date ngaySinh) { this.ngaySinh = ngaySinh; }
        
        public String getGioiTinh() { return gioiTinh; }
        public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }
        
        public String getDiaChi() { return diaChi; }
        public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
        
        public String getCccd() { return cccd; }
        public void setCccd(String cccd) { this.cccd = cccd; }
        
        public Date getNgayCapCCCD() { return ngayCapCCCD; }
        public void setNgayCapCCCD(Date ngayCapCCCD) { this.ngayCapCCCD = ngayCapCCCD; }
        
        public String getNoiCapCCCD() { return noiCapCCCD; }
        public void setNoiCapCCCD(String noiCapCCCD) { this.noiCapCCCD = noiCapCCCD; }
        
        public String getAnhDaiDien() { return anhDaiDien; }
        public void setAnhDaiDien(String anhDaiDien) { this.anhDaiDien = anhDaiDien; }
        
        public String getAnhCCCDMatTruoc() { return anhCCCDMatTruoc; }
        public void setAnhCCCDMatTruoc(String anhCCCDMatTruoc) { this.anhCCCDMatTruoc = anhCCCDMatTruoc; }
        
        public String getAnhCCCDMatSau() { return anhCCCDMatSau; }
        public void setAnhCCCDMatSau(String anhCCCDMatSau) { this.anhCCCDMatSau = anhCCCDMatSau; }
        
        public String getTenNganHang() { return tenNganHang; }
        public void setTenNganHang(String tenNganHang) { this.tenNganHang = tenNganHang; }
        
        public String getSoTaiKhoan() { return soTaiKhoan; }
        public void setSoTaiKhoan(String soTaiKhoan) { this.soTaiKhoan = soTaiKhoan; }
        
        public String getTenChuTaiKhoan() { return tenChuTaiKhoan; }
        public void setTenChuTaiKhoan(String tenChuTaiKhoan) { this.tenChuTaiKhoan = tenChuTaiKhoan; }
        
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
                   diaChi != null && !diaChi.trim().isEmpty();
        }
    }
}