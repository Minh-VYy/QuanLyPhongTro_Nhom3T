-- Test dữ liệu yêu cầu thật với cấu trúc đúng
USE QuanLyPhongTro;
GO

PRINT '=== TEST DỮ LIỆU YÊU CẦU THẬT ===';
PRINT '';

-- Chủ trọ demo
DECLARE @ChuTroId UNIQUEIDENTIFIER = '44444444-4444-4444-4444-444444444444';

PRINT '1. Query yêu cầu đặt lịch (BookingRequestDao):';
SELECT 
    dp.DatPhongId, dp.PhongId, dp.NguoiThueId, dp.ChuTroId,
    dp.Loai, dp.BatDau, dp.KetThuc, dp.ThoiGianTao,
    dp.TrangThaiId, dp.GhiChu,
    hs.HoTen as TenNguoiThue,
    p.TieuDe as TenPhong,
    tt.TenTrangThai
FROM DatPhong dp
INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId
INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
INNER JOIN Phong p ON dp.PhongId = p.PhongId
INNER JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId
WHERE dp.ChuTroId = @ChuTroId
ORDER BY dp.ThoiGianTao DESC;

PRINT '';
PRINT '2. Query yêu cầu thanh toán (PaymentRequestDao):';
SELECT 
    bl.BienLaiId, bl.DatPhongId, bl.SoTien, bl.NguoiTai, bl.TapTinId, bl.DaXacNhan, bl.ThoiGianTai,
    dp.NguoiThueId, dp.ChuTroId, dp.Loai,
    hs.HoTen as TenNguoiThue,
    p.TieuDe as TenPhong
FROM BienLai bl
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId
INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
INNER JOIN Phong p ON dp.PhongId = p.PhongId
WHERE dp.ChuTroId = @ChuTroId
ORDER BY bl.ThoiGianTai DESC;

PRINT '';
PRINT '3. Tổng kết:';
SELECT 
    (SELECT COUNT(*) FROM DatPhong WHERE ChuTroId = @ChuTroId) as TongYeuCauDatLich,
    (SELECT COUNT(*) FROM BienLai bl INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId WHERE dp.ChuTroId = @ChuTroId) as TongYeuCauThanhToan;

PRINT '';
PRINT 'ChuTroId test: ' + CAST(@ChuTroId AS VARCHAR(50));
PRINT '=== KẾT THÚC TEST ===';