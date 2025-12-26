-- Kiểm tra dữ liệu profile của user chutro@test.com
USE QuanLyPhongTro;

-- 1. Kiểm tra thông tin cơ bản trong NguoiDung
SELECT 
    NguoiDungId,
    Email,
    DienThoai,
    VaiTroId,
    IsKhoa,
    IsEmailXacThuc,
    CreatedAt,
    UpdatedAt
FROM NguoiDung 
WHERE Email = 'chutro@test.com';

-- 2. Kiểm tra thông tin chi tiết trong HoSoNguoiDung
SELECT 
    hs.NguoiDungId,
    hs.HoTen,
    hs.NgaySinh,
    hs.GioiTinh,
    hs.DiaChi,
    hs.CCCD,
    hs.NgayCapCCCD,
    hs.NoiCapCCCD,
    hs.AnhDaiDien,
    hs.TenNganHang,
    hs.SoTaiKhoan,
    hs.TenChuTaiKhoan
FROM HoSoNguoiDung hs
INNER JOIN NguoiDung nd ON hs.NguoiDungId = nd.NguoiDungId
WHERE nd.Email = 'chutro@test.com';

-- 3. Kiểm tra JOIN query như trong UserProfileDao
SELECT 
    nd.NguoiDungId, nd.Email, nd.DienThoai, nd.VaiTroId, nd.IsKhoa, nd.IsEmailXacThuc,
    nd.CreatedAt, nd.UpdatedAt,
    hs.HoTen, hs.NgaySinh, hs.GioiTinh, hs.DiaChi, hs.CCCD, hs.NgayCapCCCD,
    hs.NoiCapCCCD, hs.AnhDaiDien, hs.AnhCCCDMatTruoc, hs.AnhCCCDMatSau,
    hs.TenNganHang, hs.SoTaiKhoan, hs.TenChuTaiKhoan,
    vt.TenVaiTro
FROM NguoiDung nd
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE nd.Email = 'chutro@test.com';

-- 4. Kiểm tra tất cả users có VaiTroId = 2 (landlord)
SELECT 
    nd.NguoiDungId,
    nd.Email,
    hs.HoTen,
    vt.TenVaiTro
FROM NguoiDung nd
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE nd.VaiTroId = 2;

-- 5. Kiểm tra cấu trúc bảng VaiTro
SELECT * FROM VaiTro;