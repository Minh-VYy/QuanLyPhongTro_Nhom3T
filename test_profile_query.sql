-- Test query giống như trong UserProfileDao
USE QuanLyPhongTro;

-- Test với user chutro@test.com
DECLARE @userId UNIQUEIDENTIFIER = '00000000-0000-0000-0000-000000000002';

SELECT 
    nd.NguoiDungId, nd.Email, nd.DienThoai, nd.VaiTroId, nd.IsKhoa, nd.IsEmailXacThuc,
    nd.CreatedAt, nd.UpdatedAt,
    hs.HoTen, hs.NgaySinh, hs.LoaiGiayTo, hs.GhiChu,
    vt.TenVaiTro
FROM NguoiDung nd
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE nd.NguoiDungId = @userId;

-- Kiểm tra xem có dữ liệu trong HoSoNguoiDung không
SELECT COUNT(*) as ProfileCount FROM HoSoNguoiDung WHERE NguoiDungId = @userId;

-- Nếu không có, tạo dữ liệu mẫu
IF NOT EXISTS (SELECT 1 FROM HoSoNguoiDung WHERE NguoiDungId = @userId)
BEGIN
    INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh, LoaiGiayTo, GhiChu, CreatedAt)
    VALUES (@userId, N'Chủ Trọ Test', '1990-01-01', N'CCCD', N'Địa chỉ test', SYSDATETIMEOFFSET());
    
    PRINT 'Created sample profile data';
END

-- Test lại query sau khi tạo dữ liệu
SELECT 
    nd.NguoiDungId, nd.Email, nd.DienThoai, nd.VaiTroId, nd.IsKhoa, nd.IsEmailXacThuc,
    nd.CreatedAt, nd.UpdatedAt,
    hs.HoTen, hs.NgaySinh, hs.LoaiGiayTo, hs.GhiChu,
    vt.TenVaiTro
FROM NguoiDung nd
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE nd.NguoiDungId = @userId;