-- Kiểm tra hành vi xóa tin đăng
USE QuanLyPhongTro;
GO

PRINT '=== KIỂM TRA HÀNH VI XÓA TIN ĐĂNG ===';
PRINT '';

-- 1. Kiểm tra tổng số phòng của chủ trọ demo
DECLARE @ChuTroId UNIQUEIDENTIFIER = '44444444-4444-4444-4444-444444444444';

PRINT '1. Tổng số phòng của chủ trọ demo:';
SELECT 
    COUNT(*) as TongSoPhong,
    SUM(CASE WHEN IsDeleted = 0 THEN 1 ELSE 0 END) as PhongChuaXoa,
    SUM(CASE WHEN IsDeleted = 1 THEN 1 ELSE 0 END) as PhongDaXoa
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE nt.ChuTroId = @ChuTroId;

PRINT '';
PRINT '2. Chi tiết phòng chưa xóa:';
SELECT 
    p.PhongId,
    p.TieuDe,
    p.GiaTien,
    p.TrangThai,
    p.IsDeleted,
    p.CreatedAt
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0
ORDER BY p.CreatedAt DESC;

PRINT '';
PRINT '3. Chi tiết phòng đã xóa (nếu có):';
SELECT 
    p.PhongId,
    p.TieuDe,
    p.GiaTien,
    p.TrangThai,
    p.IsDeleted,
    p.UpdatedAt as NgayXoa
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 1
ORDER BY p.UpdatedAt DESC;

PRINT '';
PRINT '4. Query mà app sử dụng để load phòng chủ trọ:';
SELECT 
    p.PhongId, 
    p.TieuDe, 
    p.DienTich, 
    p.GiaTien, 
    p.TienCoc,
    p.SoNguoiToiDa, 
    p.TrangThai, 
    p.DiemTrungBinh, 
    p.SoLuongDanhGia,
    p.IsDuyet, 
    p.IsBiKhoa,
    nt.DiaChi
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0 
ORDER BY p.CreatedAt DESC;

PRINT '';
PRINT '=== KẾT THÚC KIỂM TRA ===';