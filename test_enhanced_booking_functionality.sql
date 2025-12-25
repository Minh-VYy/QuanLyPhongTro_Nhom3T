-- Test Script for Enhanced Booking Functionality
-- This script verifies the database structure supports the enhanced updateBooking features

USE QuanLyPhongTro;

-- 1. Check if TrangThaiDatPhong table has the expected statuses
SELECT 'Checking TrangThaiDatPhong statuses...' as Step;
SELECT TrangThaiId, TenTrangThai 
FROM TrangThaiDatPhong 
WHERE TenTrangThai IN ('ChoXacNhan', 'DaXacNhan', 'DaHuy')
ORDER BY TrangThaiId;

-- 2. Check if we have test booking data for the landlord
SELECT 'Checking DatPhong records for test landlord...' as Step;
SELECT COUNT(*) as TotalBookings
FROM DatPhong 
WHERE ChuTroId = '00000000-0000-0000-0000-000000000002';

-- 3. Show sample booking data with all required fields
SELECT 'Sample booking data with status info...' as Step;
SELECT TOP 3
    dp.DatPhongId,
    dp.PhongId,
    dp.NguoiThueId,
    dp.ChuTroId,
    dp.Loai,
    dp.BatDau,
    dp.TrangThaiId,
    tt.TenTrangThai,
    dp.GhiChu,
    ISNULL(hs.HoTen, 'Unknown Tenant') as TenNguoiThue,
    ISNULL(p.TieuDe, 'Unknown Room') as TenPhong
FROM DatPhong dp
LEFT JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId
LEFT JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN Phong p ON dp.PhongId = p.PhongId
WHERE dp.ChuTroId = '00000000-0000-0000-0000-000000000002'
ORDER BY dp.ThoiGianTao DESC;

-- 4. Test ownership verification query (used by verifyBookingOwnership method)
SELECT 'Testing ownership verification query...' as Step;
SELECT 
    DatPhongId,
    ChuTroId,
    CASE 
        WHEN ChuTroId = '00000000-0000-0000-0000-000000000002' THEN 'OWNED'
        ELSE 'NOT_OWNED'
    END as OwnershipStatus
FROM DatPhong 
WHERE DatPhongId IN (
    SELECT TOP 2 DatPhongId 
    FROM DatPhong 
    WHERE ChuTroId = '00000000-0000-0000-0000-000000000002'
);

-- 5. Test status update query (used by updateBookingStatus method)
SELECT 'Testing status update capability...' as Step;
-- This is a dry run - we'll show what would be updated but not actually update
SELECT 
    dp.DatPhongId,
    dp.TrangThaiId as CurrentStatusId,
    tt.TenTrangThai as CurrentStatus,
    'Would update to status ID 2 (DaXacNhan)' as UpdateAction
FROM DatPhong dp
LEFT JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId
WHERE dp.ChuTroId = '00000000-0000-0000-0000-000000000002'
  AND dp.TrangThaiId = 1; -- Only ChoXacNhan bookings can be updated

-- 6. Check if we have different status types for testing
SELECT 'Status distribution for test data...' as Step;
SELECT 
    tt.TenTrangThai,
    COUNT(*) as Count
FROM DatPhong dp
INNER JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId
WHERE dp.ChuTroId = '00000000-0000-0000-0000-000000000002'
GROUP BY tt.TenTrangThai
ORDER BY COUNT(*) DESC;

-- 7. Verify the enhanced query used by BookingRequestDao works correctly
SELECT 'Testing enhanced BookingRequestDao query...' as Step;
SELECT 
    dp.DatPhongId,
    ISNULL(dp.Loai, 'Đặt lịch xem phòng') as Loai,
    dp.BatDau,
    dp.KetThuc,
    ISNULL(dp.ThoiGianTao, GETDATE()) as ThoiGianTao,
    ISNULL(dp.TrangThaiId, 1) as TrangThaiId,
    ISNULL(dp.GhiChu, '') as GhiChu,
    ISNULL(hs.HoTen, 'Người thuê') as TenNguoiThue,
    ISNULL(p.TieuDe, 'Phòng trọ') as TenPhong,
    ISNULL(tt.TenTrangThai, 'ChoXacNhan') as TenTrangThai
FROM DatPhong dp
LEFT JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN Phong p ON dp.PhongId = p.PhongId
LEFT JOIN TrangThaiDatPhong tt ON dp.TrangThaiId = tt.TrangThaiId
WHERE dp.ChuTroId = '00000000-0000-0000-0000-000000000002'
ORDER BY dp.ThoiGianTao DESC;

SELECT 'Enhanced booking functionality test completed!' as Result;
SELECT 'Ready to test in Android app with confirmation dialogs, button states, and error handling.' as NextStep;