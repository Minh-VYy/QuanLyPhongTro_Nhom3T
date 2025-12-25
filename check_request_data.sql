-- Kiểm tra dữ liệu yêu cầu đặt lịch và thanh toán
USE QuanLyPhongTro;
GO

PRINT '=== KIỂM TRA DỮ LIỆU YÊU CẦU ===';
PRINT '';

-- Kiểm tra chủ trọ demo
DECLARE @ChuTroId UNIQUEIDENTIFIER = '44444444-4444-4444-4444-444444444444';

PRINT '1. Thông tin chủ trọ:';
SELECT ChuTroId, TenDangNhap, HoTen FROM ChuTro WHERE ChuTroId = @ChuTroId;

PRINT '';
PRINT '2. Kiểm tra bảng DatPhong (Yêu cầu đặt lịch):';
SELECT COUNT(*) as TongSoDatPhong FROM DatPhong;

SELECT TOP 5 
    dp.DatPhongId,
    dp.NguoiThueId,
    dp.PhongId,
    dp.TrangThaiDatPhongId,
    dp.NgayDat,
    dp.GhiChu,
    tsdp.TenTrangThai
FROM DatPhong dp
LEFT JOIN TrangThaiDatPhong tsdp ON dp.TrangThaiDatPhongId = tsdp.TrangThaiDatPhongId
ORDER BY dp.NgayDat DESC;

PRINT '';
PRINT '3. Kiểm tra bảng BienLai (Yêu cầu thanh toán):';
SELECT COUNT(*) as TongSoBienLai FROM BienLai;

SELECT TOP 5
    bl.BienLaiId,
    bl.PhongId,
    bl.SoTien,
    bl.TrangThai,
    bl.NgayTao,
    bl.MoTa
FROM BienLai bl
ORDER BY bl.NgayTao DESC;

PRINT '';
PRINT '4. Query mà app sử dụng cho yêu cầu đặt lịch:';
-- Query từ BookingRequestDao
SELECT 
    dp.DatPhongId,
    nt.TenNguoiThue,
    p.TieuDe as TenPhong,
    tsdp.TenTrangThai,
    dp.TrangThaiDatPhongId,
    'Đặt lịch xem phòng' as Loai,
    dp.GhiChu,
    dp.NgayDat
FROM DatPhong dp
INNER JOIN Phong p ON dp.PhongId = p.PhongId
INNER JOIN NhaTro nt_house ON p.NhaTroId = nt_house.NhaTroId
INNER JOIN NguoiThue nt ON dp.NguoiThueId = nt.NguoiThueId
LEFT JOIN TrangThaiDatPhong tsdp ON dp.TrangThaiDatPhongId = tsdp.TrangThaiDatPhongId
WHERE nt_house.ChuTroId = @ChuTroId
ORDER BY dp.NgayDat DESC;

PRINT '';
PRINT '5. Query mà app sử dụng cho yêu cầu thanh toán:';
-- Query từ PaymentRequestDao
SELECT 
    bl.BienLaiId,
    bl.SoTien,
    nt.TenNguoiThue,
    p.TieuDe as TenPhong,
    bl.TrangThai,
    bl.NgayTao,
    bl.MoTa
FROM BienLai bl
INNER JOIN Phong p ON bl.PhongId = p.PhongId
INNER JOIN NhaTro nt_house ON p.NhaTroId = nt_house.NhaTroId
INNER JOIN NguoiThue nt ON bl.NguoiThueId = nt.NguoiThueId
WHERE nt_house.ChuTroId = @ChuTroId
ORDER BY bl.NgayTao DESC;

PRINT '';
PRINT '=== KẾT THÚC KIỂM TRA ===';