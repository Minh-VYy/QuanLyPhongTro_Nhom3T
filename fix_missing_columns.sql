-- Script kiểm tra và thêm các cột thiếu trong bảng Phong
USE QuanLyPhongTro;
GO

PRINT '========================================';
PRINT 'KIỂM TRA VÀ THÊM CỘT THIẾU TRONG BẢNG PHONG';
PRINT '========================================';
PRINT '';

-- 1. Kiểm tra và thêm cột MoTa
IF COL_LENGTH('dbo.Phong', 'MoTa') IS NULL
BEGIN
    PRINT '⚠️ Cột MoTa không tồn tại. Đang thêm...';
    ALTER TABLE dbo.Phong ADD MoTa NVARCHAR(MAX) NULL;
    PRINT '✅ Đã thêm cột MoTa';
END
ELSE
BEGIN
    PRINT '✅ Cột MoTa đã tồn tại';
END
PRINT '';

-- 2. Kiểm tra và thêm cột IsDeleted
IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NULL
BEGIN
    PRINT '⚠️ Cột IsDeleted không tồn tại. Đang thêm...';
    ALTER TABLE dbo.Phong ADD IsDeleted BIT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột IsDeleted';
END
ELSE
BEGIN
    PRINT '✅ Cột IsDeleted đã tồn tại';
END
PRINT '';

-- 3. Kiểm tra và thêm cột DiemTrungBinh
IF COL_LENGTH('dbo.Phong', 'DiemTrungBinh') IS NULL
BEGIN
    PRINT '⚠️ Cột DiemTrungBinh không tồn tại. Đang thêm...';
    ALTER TABLE dbo.Phong ADD DiemTrungBinh FLOAT NULL;
    PRINT '✅ Đã thêm cột DiemTrungBinh';
END
ELSE
BEGIN
    PRINT '✅ Cột DiemTrungBinh đã tồn tại';
END
PRINT '';

-- 4. Kiểm tra và thêm cột SoLuongDanhGia
IF COL_LENGTH('dbo.Phong', 'SoLuongDanhGia') IS NULL
BEGIN
    PRINT '⚠️ Cột SoLuongDanhGia không tồn tại. Đang thêm...';
    ALTER TABLE dbo.Phong ADD SoLuongDanhGia INT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột SoLuongDanhGia';
END
ELSE
BEGIN
    PRINT '✅ Cột SoLuongDanhGia đã tồn tại';
END
PRINT '';

-- 5. Kiểm tra và thêm cột IsDuyet
IF COL_LENGTH('dbo.Phong', 'IsDuyet') IS NULL
BEGIN
    PRINT '⚠️ Cột IsDuyet không tồn tại. Đang thêm...';
    ALTER TABLE dbo.Phong ADD IsDuyet BIT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột IsDuyet';
END
ELSE
BEGIN
    PRINT '✅ Cột IsDuyet đã tồn tại';
END
PRINT '';

-- 6. Kiểm tra và thêm cột IsBiKhoa
IF COL_LENGTH('dbo.Phong', 'IsBiKhoa') IS NULL
BEGIN
    PRINT '⚠️ Cột IsBiKhoa không tồn tại. Đang thêm...';
    ALTER TABLE dbo.Phong ADD IsBiKhoa BIT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột IsBiKhoa';
END
ELSE
BEGIN
    PRINT '✅ Cột IsBiKhoa đã tồn tại';
END
PRINT '';

-- 7. Hiển thị tất cả các cột hiện có trong bảng Phong
PRINT '========================================';
PRINT 'CÁC CỘT HIỆN CÓ TRONG BẢNG PHONG:';
PRINT '========================================';
SELECT 
    COLUMN_NAME AS [Tên Cột],
    DATA_TYPE AS [Kiểu Dữ Liệu],
    CHARACTER_MAXIMUM_LENGTH AS [Độ Dài],
    IS_NULLABLE AS [Cho Phép NULL]
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_NAME = 'Phong'
ORDER BY ORDINAL_POSITION;
PRINT '';

-- 8. Cập nhật giá trị mặc định cho các cột mới
PRINT '========================================';
PRINT 'CẬP NHẬT GIÁ TRỊ MẶC ĐỊNH:';
PRINT '========================================';

-- Set IsDeleted = 0 cho tất cả phòng
UPDATE Phong SET IsDeleted = 0 WHERE IsDeleted IS NULL;
PRINT '✅ Đã set IsDeleted = 0';

-- Set IsDuyet = 1 cho tất cả phòng (để test)
UPDATE Phong SET IsDuyet = 1 WHERE IsDuyet = 0 OR IsDuyet IS NULL;
PRINT '✅ Đã set IsDuyet = 1';

-- Set IsBiKhoa = 0 cho tất cả phòng
UPDATE Phong SET IsBiKhoa = 0 WHERE IsBiKhoa = 1 OR IsBiKhoa IS NULL;
PRINT '✅ Đã set IsBiKhoa = 0';

-- Set SoLuongDanhGia = 0 nếu NULL
UPDATE Phong SET SoLuongDanhGia = 0 WHERE SoLuongDanhGia IS NULL;
PRINT '✅ Đã set SoLuongDanhGia = 0';

PRINT '';
PRINT '========================================';
PRINT 'HOÀN TẤT!';
PRINT '========================================';
PRINT '';

-- Kiểm tra số phòng available
DECLARE @CountAvailable INT;
SELECT @CountAvailable = COUNT(*) 
FROM Phong 
WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;

PRINT 'Số phòng sẵn sàng hiển thị: ' + CAST(@CountAvailable AS VARCHAR(10));

IF @CountAvailable > 0
BEGIN
    PRINT '✅ App sẽ load được dữ liệu!';
    PRINT '';
    PRINT 'Danh sách 3 phòng đầu tiên:';
    SELECT TOP 3
        PhongId,
        TieuDe,
        GiaTien,
        DienTich,
        TrangThai,
        IsDuyet,
        IsBiKhoa,
        IsDeleted,
        CASE 
            WHEN MoTa IS NULL THEN N'(Chưa có mô tả)'
            WHEN LEN(MoTa) > 50 THEN LEFT(MoTa, 50) + '...'
            ELSE MoTa
        END AS MoTa
    FROM Phong
    WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0
    ORDER BY CreatedAt DESC;
END
ELSE
BEGIN
    PRINT '⚠️ Không có phòng nào! Cần thêm dữ liệu mẫu.';
END
PRINT '';
