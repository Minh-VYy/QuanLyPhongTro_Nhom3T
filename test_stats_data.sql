-- Test dữ liệu thống kê cho chủ trọ
USE QuanLyPhongTro;

DECLARE @LandlordId UNIQUEIDENTIFIER = '00000000-0000-0000-0000-000000000002'; -- chutro@test.com

SELECT '=== THỐNG KÊ CHỦ TRỌ ===' as Info;
SELECT 'LandlordId: ' + CAST(@LandlordId as VARCHAR(50)) as LandlordInfo;

-- 1. Thống kê phòng
SELECT '1. THỐNG KÊ PHÒNG' as Section;
SELECT 
    COUNT(*) as TotalRooms,
    SUM(CASE WHEN TrangThai = 1 THEN 1 ELSE 0 END) as ActiveRooms,
    SUM(CASE WHEN TrangThai = 0 THEN 1 ELSE 0 END) as InactiveRooms
FROM Phong 
WHERE ChuTroId = @LandlordId;

-- Phòng đã cho thuê
SELECT COUNT(DISTINCT p.PhongId) as OccupiedRooms
FROM Phong p
INNER JOIN DatPhong dp ON p.PhongId = dp.PhongId
WHERE p.ChuTroId = @LandlordId AND dp.TrangThaiId = 2;

-- 2. Thống kê booking
SELECT '2. THỐNG KÊ BOOKING' as Section;
SELECT 
    COUNT(*) as TotalBookings,
    SUM(CASE WHEN TrangThaiId = 1 THEN 1 ELSE 0 END) as PendingRequests,
    SUM(CASE WHEN TrangThaiId = 2 THEN 1 ELSE 0 END) as ApprovedBookings,
    SUM(CASE WHEN TrangThaiId = 3 THEN 1 ELSE 0 END) as RejectedBookings
FROM DatPhong 
WHERE ChuTroId = @LandlordId;

-- 3. Thống kê doanh thu
SELECT '3. THỐNG KÊ DOANH THU' as Section;

-- Doanh thu tháng hiện tại
SELECT ISNULL(SUM(bl.SoTien), 0) as MonthlyRevenue
FROM BienLai bl
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
WHERE dp.ChuTroId = @LandlordId
AND bl.DaXacNhan = 1
AND MONTH(bl.ThoiGianTai) = MONTH(GETDATE())
AND YEAR(bl.ThoiGianTai) = YEAR(GETDATE());

-- Tổng doanh thu
SELECT ISNULL(SUM(bl.SoTien), 0) as TotalRevenue
FROM BienLai bl
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
WHERE dp.ChuTroId = @LandlordId AND bl.DaXacNhan = 1;

-- Thống kê thanh toán
SELECT 
    COUNT(*) as TotalPayments,
    SUM(CASE WHEN DaXacNhan = 1 THEN 1 ELSE 0 END) as ConfirmedPayments,
    SUM(CASE WHEN DaXacNhan = 0 THEN 1 ELSE 0 END) as PendingPayments
FROM BienLai bl
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
WHERE dp.ChuTroId = @LandlordId;

-- 4. Chi tiết một số booking gần đây
SELECT '4. BOOKING GẦN ĐÂY' as Section;
SELECT TOP 5
    dp.DatPhongId,
    dp.Loai,
    dp.BatDau,
    dp.TrangThaiId,
    CASE dp.TrangThaiId 
        WHEN 1 THEN 'Chờ xác nhận'
        WHEN 2 THEN 'Đã xác nhận' 
        WHEN 3 THEN 'Đã hủy'
        ELSE 'Không xác định'
    END as TrangThaiText,
    dp.GhiChu
FROM DatPhong dp
WHERE dp.ChuTroId = @LandlordId
ORDER BY dp.ThoiGianTao DESC;

-- 5. Chi tiết một số thanh toán gần đây
SELECT '5. THANH TOÁN GẦN ĐÂY' as Section;
SELECT TOP 5
    bl.BienLaiId,
    bl.SoTien,
    bl.DaXacNhan,
    CASE bl.DaXacNhan 
        WHEN 1 THEN 'Đã xác nhận'
        WHEN 0 THEN 'Chờ xác nhận'
    END as TrangThaiText,
    bl.ThoiGianTai
FROM BienLai bl
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
WHERE dp.ChuTroId = @LandlordId
ORDER BY bl.ThoiGianTai DESC;