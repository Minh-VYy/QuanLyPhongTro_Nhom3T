-- Kiểm tra dữ liệu đặt lịch giữa người thuê và chủ trọ
USE QuanLyPhongTro;

-- 1. Kiểm tra tất cả records trong DatPhong
SELECT 
    COUNT(*) as TotalBookings,
    COUNT(DISTINCT ChuTroId) as UniqueLandlords,
    COUNT(DISTINCT NguoiThueId) as UniqueTenants
FROM DatPhong;

-- 2. Kiểm tra dữ liệu DatPhong chi tiết
SELECT TOP 10
    DatPhongId,
    PhongId,
    NguoiThueId,
    ChuTroId,
    Loai,
    BatDau,
    KetThuc,
    ThoiGianTao,
    TrangThaiId,
    GhiChu
FROM DatPhong
ORDER BY ThoiGianTao DESC;

-- 3. Kiểm tra với ChuTroId của user chutro@test.com
DECLARE @chuTroId UNIQUEIDENTIFIER = '00000000-0000-0000-0000-000000000002';

SELECT 
    dp.DatPhongId,
    dp.PhongId,
    dp.NguoiThueId,
    dp.ChuTroId,
    dp.Loai,
    dp.BatDau,
    dp.KetThuc,
    dp.ThoiGianTao,
    dp.TrangThaiId,
    dp.GhiChu,
    hs.HoTen as TenNguoiThue,
    p.TieuDe as TenPhong
FROM DatPhong dp
LEFT JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN Phong p ON dp.PhongId = p.PhongId
WHERE dp.ChuTroId = @chuTroId
ORDER BY dp.ThoiGianTao DESC;

-- 4. Kiểm tra cấu trúc bảng DatPhong
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'DatPhong'
ORDER BY ORDINAL_POSITION;

-- 5. Tạo dữ liệu test nếu không có
IF NOT EXISTS (SELECT 1 FROM DatPhong WHERE ChuTroId = @chuTroId)
BEGIN
    -- Lấy PhongId đầu tiên của chủ trọ này
    DECLARE @phongId UNIQUEIDENTIFIER;
    DECLARE @nguoiThueId UNIQUEIDENTIFIER = '00000000-0000-0000-0000-000000000001'; -- Giả sử có user này
    
    SELECT TOP 1 @phongId = PhongId 
    FROM Phong p 
    INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
    WHERE nt.ChuTroId = @chuTroId;
    
    IF @phongId IS NOT NULL
    BEGIN
        INSERT INTO DatPhong (
            DatPhongId, PhongId, NguoiThueId, ChuTroId, 
            Loai, BatDau, KetThuc, ThoiGianTao, TrangThaiId, GhiChu
        ) VALUES (
            NEWID(), @phongId, @nguoiThueId, @chuTroId,
            N'Xem phòng', GETDATE(), DATEADD(HOUR, 2, GETDATE()),
            GETDATE(), 1, N'Yêu cầu xem phòng test'
        );
        
        PRINT 'Created test booking data';
    END
END

-- 6. Kiểm tra lại sau khi tạo dữ liệu test
SELECT COUNT(*) as BookingCount FROM DatPhong WHERE ChuTroId = @chuTroId;