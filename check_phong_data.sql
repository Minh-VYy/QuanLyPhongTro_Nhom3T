-- Script kiểm tra dữ liệu phòng trong database
USE QuanLyPhongTro;
GO

PRINT '========================================';
PRINT 'KIỂM TRA DỮ LIỆU PHÒNG';
PRINT '========================================';
PRINT '';

-- 1. Kiểm tra cột IsDeleted có tồn tại không
PRINT '1. Kiểm tra cột IsDeleted:';
IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NULL
BEGIN
    PRINT '   ❌ Cột IsDeleted KHÔNG tồn tại!';
    PRINT '   → Cần chạy lại script QuanLyPhongTro.sql để thêm cột này';
END
ELSE
BEGIN
    PRINT '   ✅ Cột IsDeleted đã tồn tại';
END
PRINT '';

-- 2. Đếm tổng số phòng
PRINT '2. Tổng số phòng trong database:';
SELECT COUNT(*) AS TongSoPhong FROM Phong;
PRINT '';

-- 3. Đếm phòng theo trạng thái
PRINT '3. Phân loại phòng theo trạng thái:';
SELECT 
    IsDuyet,
    IsBiKhoa,
    CASE 
        WHEN COL_LENGTH('dbo.Phong', 'IsDeleted') IS NOT NULL 
        THEN CAST(IsDeleted AS VARCHAR(10))
        ELSE 'N/A'
    END AS IsDeleted,
    COUNT(*) AS SoLuong
FROM Phong
GROUP BY IsDuyet, IsBiKhoa, 
    CASE 
        WHEN COL_LENGTH('dbo.Phong', 'IsDeleted') IS NOT NULL 
        THEN CAST(IsDeleted AS VARCHAR(10))
        ELSE 'N/A'
    END
ORDER BY IsDuyet DESC, IsBiKhoa, IsDeleted;
PRINT '';

-- 4. Đếm phòng available (điều kiện của app)
PRINT '4. Số phòng thỏa điều kiện hiển thị (IsDuyet=1, IsBiKhoa=0, IsDeleted=0):';
IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NOT NULL
BEGIN
    SELECT COUNT(*) AS SoPhongAvailable 
    FROM Phong 
    WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;
END
ELSE
BEGIN
    SELECT COUNT(*) AS SoPhongAvailable 
    FROM Phong 
    WHERE IsDuyet = 1 AND IsBiKhoa = 0;
END
PRINT '';

-- 5. Hiển thị 5 phòng mẫu (nếu có)
PRINT '5. Danh sách 5 phòng đầu tiên:';
SELECT TOP 5
    PhongId,
    TieuDe,
    GiaTien,
    TrangThai,
    IsDuyet,
    IsBiKhoa,
    CASE 
        WHEN COL_LENGTH('dbo.Phong', 'IsDeleted') IS NOT NULL 
        THEN CAST(IsDeleted AS VARCHAR(10))
        ELSE 'N/A'
    END AS IsDeleted,
    CreatedAt
FROM Phong
ORDER BY CreatedAt DESC;
PRINT '';

-- 6. Gợi ý fix nếu không có phòng available
PRINT '========================================';
PRINT 'HƯỚNG DẪN FIX (nếu cần):';
PRINT '========================================';
PRINT '';

DECLARE @CountAvailable INT;
IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NOT NULL
BEGIN
    SELECT @CountAvailable = COUNT(*) 
    FROM Phong 
    WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;
END
ELSE
BEGIN
    SELECT @CountAvailable = COUNT(*) 
    FROM Phong 
    WHERE IsDuyet = 1 AND IsBiKhoa = 0;
END

IF @CountAvailable = 0
BEGIN
    PRINT 'Không có phòng nào thỏa điều kiện hiển thị!';
    PRINT '';
    PRINT 'Để fix, chạy các lệnh sau:';
    PRINT '';
    
    -- Kiểm tra nếu thiếu cột IsDeleted
    IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NULL
    BEGIN
        PRINT '-- Bước 1: Thêm cột IsDeleted';
        PRINT 'ALTER TABLE Phong ADD IsDeleted BIT NOT NULL DEFAULT 0;';
        PRINT 'UPDATE Phong SET IsDeleted = 0 WHERE IsDeleted IS NULL;';
        PRINT '';
    END
    
    -- Kiểm tra nếu có phòng nhưng chưa duyệt
    DECLARE @CountTotal INT;
    SELECT @CountTotal = COUNT(*) FROM Phong;
    
    IF @CountTotal > 0
    BEGIN
        PRINT '-- Bước 2: Duyệt tất cả phòng hiện có';
        PRINT 'UPDATE Phong SET IsDuyet = 1, IsBiKhoa = 0 WHERE IsDuyet = 0 OR IsBiKhoa = 1;';
        PRINT '';
    END
    ELSE
    BEGIN
        PRINT '-- Không có phòng nào trong database!';
        PRINT '-- Cần thêm dữ liệu mẫu hoặc tạo phòng mới từ app';
        PRINT '';
    END
END
ELSE
BEGIN
    PRINT '✅ Có ' + CAST(@CountAvailable AS VARCHAR(10)) + ' phòng sẵn sàng hiển thị!';
    PRINT '✅ App sẽ load được dữ liệu';
END
PRINT '';
PRINT '========================================';
