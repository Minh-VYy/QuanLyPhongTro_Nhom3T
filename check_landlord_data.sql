-- Script kiểm tra dữ liệu chủ trọ
-- Thay đổi email theo tài khoản của bạn

USE QuanLyPhongTro;
GO

DECLARE @ChuTroEmail NVARCHAR(255) = 'admin@example.com'; -- THAY ĐỔI EMAIL NÀY
DECLARE @ChuTroId UNIQUEIDENTIFIER;

SELECT @ChuTroId = NguoiDungId FROM NguoiDung WHERE Email = @ChuTroEmail;

IF @ChuTroId IS NULL
BEGIN
    PRINT 'Không tìm thấy chủ trọ với email: ' + @ChuTroEmail;
    RETURN;
END

PRINT 'Kiểm tra dữ liệu cho ChuTroId: ' + CAST(@ChuTroId AS NVARCHAR(50));
PRINT '================================================';

-- 1. Kiểm tra NhaTro
SELECT 
    'NhaTro' AS LoaiDuLieu,
    COUNT(*) AS SoLuong
FROM NhaTro 
WHERE ChuTroId = @ChuTroId;

-- 2. Kiểm tra tổng số Phong
SELECT 
    'Tổng Phong' AS LoaiDuLieu,
    COUNT(*) AS SoLuong
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @ChuTroId;

-- 3. Kiểm tra Phong theo điều kiện
SELECT 
    'Phong IsDeleted=0' AS LoaiDuLieu,
    COUNT(*) AS SoLuong
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0;

-- 4. Kiểm tra Phong hoạt động (điều kiện của app)
SELECT 
    'Phong Hoạt Động' AS LoaiDuLieu,
    COUNT(*) AS SoLuong
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @ChuTroId 
  AND p.IsDeleted = 0;

-- 5. Chi tiết từng phòng
SELECT 
    nt.TieuDe AS NhaTro,
    p.TieuDe AS TenPhong,
    p.GiaTien,
    p.TrangThai,
    p.IsDuyet,
    p.IsBiKhoa,
    p.IsDeleted,
    CASE 
        WHEN p.IsDeleted = 1 THEN 'Đã xóa'
        WHEN p.IsBiKhoa = 1 THEN 'Bị khóa'
        WHEN p.IsDuyet = 0 THEN 'Chờ duyệt'
        ELSE 'Hoạt động'
    END AS TrangThaiHienThi
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @ChuTroId
ORDER BY nt.TieuDe, p.TieuDe;

PRINT 'Kiểm tra hoàn tất!';