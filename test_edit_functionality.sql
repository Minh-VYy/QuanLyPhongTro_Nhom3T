-- Test chức năng chỉnh sửa tin đăng
USE QuanLyPhongTro;
GO

PRINT '=== TEST CHỨC NĂNG CHỈNH SỬA TIN ĐĂNG ===';
PRINT '';

-- Lấy một phòng mẫu để test
DECLARE @ChuTroId UNIQUEIDENTIFIER = '44444444-4444-4444-4444-444444444444';
DECLARE @PhongId UNIQUEIDENTIFIER;

-- Lấy PhongId đầu tiên
SELECT TOP 1 @PhongId = p.PhongId 
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0;

PRINT '1. Thông tin phòng trước khi sửa:';
SELECT 
    p.PhongId,
    p.TieuDe,
    p.GiaTien,
    p.MoTa,
    p.UpdatedAt
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE p.PhongId = @PhongId AND nt.ChuTroId = @ChuTroId;

PRINT '';
PRINT '2. Query mà app sử dụng để load dữ liệu edit:';
SELECT 
    p.PhongId, 
    p.TieuDe, 
    p.GiaTien, 
    p.TienCoc, 
    p.DienTich,
    p.SoNguoiToiDa, 
    p.TrangThai, 
    p.DiemTrungBinh, 
    p.SoLuongDanhGia,
    p.MoTa, 
    p.IsDuyet, 
    p.IsBiKhoa,
    nt.DiaChi
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE p.PhongId = @PhongId AND nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0;

PRINT '';
PRINT 'PhongId để test: ' + CAST(@PhongId AS VARCHAR(50));
PRINT 'ChuTroId: ' + CAST(@ChuTroId AS VARCHAR(50));

PRINT '';
PRINT '=== SẴN SÀNG TEST TRÊN APP ===';
PRINT 'Hãy sử dụng PhongId trên để test chức năng edit trong app!';