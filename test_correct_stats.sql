-- Test thống kê với cấu trúc database đúng
USE QuanLyPhongTro;

DECLARE @LandlordId UNIQUEIDENTIFIER = '00000000-0000-0000-0000-000000000002'; -- chutro@test.com

SELECT '=== THỐNG KÊ CHỦ TRỌ (CORRECTED) ===' as Info;
SELECT 'LandlordId: ' + CAST(@LandlordId as VARCHAR(50)) as LandlordInfo;

-- 1. Thống kê phòng (qua NhaTro)
SELECT '1. THỐNG KÊ PHÒNG' as Section;
SELECT 
    COUNT(*) as TotalRooms,
    SUM(CASE WHEN p.TrangThai = 'con_trong' THEN 1 ELSE 0 END) as VacantRooms,
    SUM(CASE WHEN p.TrangThai = 'da_thue' THEN 1 ELSE 0 END) as OccupiedRooms,
    SUM(CASE WHEN p.TrangThai NOT IN ('con_trong', 'da_thue') THEN 1 ELSE 0 END) as OtherStatus
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @LandlordId;

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

-- 4. Kiểm tra nhà trọ của chủ trọ này
SELECT '4. NHÀ TRỌ CỦA CHỦ TRỌ' as Section;
SELECT 
    nt.NhaTroId,
    nt.TieuDe,
    nt.DiaChi,
    COUNT(p.PhongId) as SoPhong
FROM NhaTro nt
LEFT JOIN Phong p ON nt.NhaTroId = p.NhaTroId
WHERE nt.ChuTroId = @LandlordId
GROUP BY nt.NhaTroId, nt.TieuDe, nt.DiaChi;

-- 5. Chi tiết trạng thái phòng
SELECT '5. CHI TIẾT TRẠNG THÁI PHÒNG' as Section;
SELECT 
    p.TrangThai,
    COUNT(*) as SoLuong
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @LandlordId
GROUP BY p.TrangThai;