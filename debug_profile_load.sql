-- Debug profile loading issue
USE QuanLyPhongTro;

-- 1. Kiểm tra user login
SELECT 
    NguoiDungId,
    Email,
    DienThoai,
    VaiTroId,
    PasswordHash
FROM NguoiDung 
WHERE Email = 'chutro@test.com' AND PasswordHash = '12345678';

-- 2. Kiểm tra với userId string format
DECLARE @userIdString VARCHAR(50) = '00000000-0000-0000-0000-000000000002';

SELECT 
    nd.NguoiDungId, 
    nd.Email, 
    nd.DienThoai, 
    nd.VaiTroId, 
    nd.IsKhoa, 
    nd.IsEmailXacThuc,
    nd.CreatedAt, 
    nd.UpdatedAt,
    hs.HoTen, 
    hs.NgaySinh, 
    hs.LoaiGiayTo, 
    hs.GhiChu,
    vt.TenVaiTro
FROM NguoiDung nd
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE nd.NguoiDungId = CAST(@userIdString AS UNIQUEIDENTIFIER);

-- 3. Kiểm tra với string comparison
SELECT 
    nd.NguoiDungId, 
    nd.Email, 
    nd.DienThoai, 
    nd.VaiTroId, 
    nd.IsKhoa, 
    nd.IsEmailXacThuc,
    nd.CreatedAt, 
    nd.UpdatedAt,
    hs.HoTen, 
    hs.NgaySinh, 
    hs.LoaiGiayTo, 
    hs.GhiChu,
    vt.TenVaiTro
FROM NguoiDung nd
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE CAST(nd.NguoiDungId AS VARCHAR(50)) = @userIdString;

-- 4. Kiểm tra tất cả VaiTro
SELECT * FROM VaiTro;