package com.example.QuanLyPhongTro_App.ui.landlord;

import java.util.Date;

/**
 * Legacy note:
 * This class originally contained direct SQL (java.sql.Connection/PreparedStatement/ResultSet)
 * to load/update user profiles.
 *
 * The app has migrated to API-based data access, so the DAO methods were removed.
 *
 * Keep this file only as a shared model holder (UserProfile) used by landlord UI screens.
 */
public class UserProfileDao {

    /**
     * Model chứa thông tin hồ sơ người dùng.
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
        public String getGioiTinh() { return "Nam"; }
        public void setGioiTinh(String gioiTinh) { /* No-op */ }

        public String getDiaChi() { return ghiChu; }
        public void setDiaChi(String diaChi) { this.ghiChu = diaChi; }

        public String getCccd() { return loaiGiayTo; }
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
            if (email != null && !email.trim().isEmpty() && email.contains("@")) {
                return email.split("@")[0];
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