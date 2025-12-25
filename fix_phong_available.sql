-- Script fix nhanh để có phòng hiển thị trong app
USE QuanLyPhongTro;
GO

PRINT '========================================';
PRINT 'FIX PHÒNG ĐỂ HIỂN THỊ TRONG APP';
PRINT '========================================';
PRINT '';

-- Bước 1: Thêm cột IsDeleted nếu chưa có
IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NULL
BEGIN
    PRINT 'Bước 1: Thêm cột IsDeleted...';
    ALTER TABLE Phong ADD IsDeleted BIT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột IsDeleted';
    PRINT '';
END
ELSE
BEGIN
    PRINT 'Bước 1: Cột IsDeleted đã tồn tại ✅';
    PRINT '';
END

-- Bước 2: Đảm bảo tất cả phòng có IsDeleted = 0
PRINT 'Bước 2: Cập nhật IsDeleted = 0 cho tất cả phòng...';
UPDATE Phong SET IsDeleted = 0 WHERE IsDeleted IS NULL OR IsDeleted = 1;
PRINT '✅ Đã cập nhật';
PRINT '';

-- Bước 3: Duyệt tất cả phòng (set IsDuyet = 1)
PRINT 'Bước 3: Duyệt tất cả phòng...';
DECLARE @CountDuyet INT;
SELECT @CountDuyet = COUNT(*) FROM Phong WHERE IsDuyet = 0;

IF @CountDuyet > 0
BEGIN
    UPDATE Phong SET IsDuyet = 1, NguoiDuyet = NULL, ThoiGianDuyet = SYSDATETIMEOFFSET()
    WHERE IsDuyet = 0;
    PRINT '✅ Đã duyệt ' + CAST(@CountDuyet AS VARCHAR(10)) + ' phòng';
END
ELSE
BEGIN
    PRINT '✅ Tất cả phòng đã được duyệt';
END
PRINT '';

-- Bước 4: Mở khóa tất cả phòng (set IsBiKhoa = 0)
PRINT 'Bước 4: Mở khóa tất cả phòng...';
DECLARE @CountKhoa INT;
SELECT @CountKhoa = COUNT(*) FROM Phong WHERE IsBiKhoa = 1;

IF @CountKhoa > 0
BEGIN
    UPDATE Phong SET IsBiKhoa = 0 WHERE IsBiKhoa = 1;
    PRINT '✅ Đã mở khóa ' + CAST(@CountKhoa AS VARCHAR(10)) + ' phòng';
END
ELSE
BEGIN
    PRINT '✅ Không có phòng nào bị khóa';
END
PRINT '';

-- Bước 5: Kiểm tra kết quả
PRINT '========================================';
PRINT 'KẾT QUẢ SAU KHI FIX:';
PRINT '========================================';
PRINT '';

DECLARE @CountAvailable INT;
SELECT @CountAvailable = COUNT(*) 
FROM Phong 
WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;

PRINT 'Số phòng sẵn sàng hiển thị: ' + CAST(@CountAvailable AS VARCHAR(10));
PRINT '';

IF @CountAvailable > 0
BEGIN
    PRINT '✅ THÀNH CÔNG! App sẽ load được ' + CAST(@CountAvailable AS VARCHAR(10)) + ' phòng';
    PRINT '';
    PRINT 'Danh sách 5 phòng đầu tiên:';
    SELECT TOP 5
        PhongId,
        TieuDe,
        GiaTien,
        TrangThai,
        IsDuyet,
        IsBiKhoa,
        IsDeleted
    FROM Phong
    WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0
    ORDER BY CreatedAt DESC;
END
ELSE
BEGIN
    PRINT '⚠️ CẢNH BÁO: Vẫn không có phòng nào!';
    PRINT '';
    PRINT 'Nguyên nhân có thể:';
    PRINT '1. Database chưa có dữ liệu phòng';
    PRINT '2. Cần chạy script seed data để tạo phòng mẫu';
    PRINT '';
    
    DECLARE @CountTotal INT;
    SELECT @CountTotal = COUNT(*) FROM Phong;
    PRINT 'Tổng số phòng trong database: ' + CAST(@CountTotal AS VARCHAR(10));
END
PRINT '';
PRINT '========================================';
