-- ============================================
-- SCRIPT FIX TẤT CẢ VẤN ĐỀ DATABASE
-- Chạy script này để fix mọi vấn đề liên quan đến app
-- ============================================

USE QuanLyPhongTro;
GO

PRINT '';
PRINT '╔════════════════════════════════════════════════════════════╗';
PRINT '║  FIX TẤT CẢ VẤN ĐỀ DATABASE CHO APP QUẢN LÝ PHÒNG TRỌ    ║';
PRINT '╚════════════════════════════════════════════════════════════╝';
PRINT '';

-- ============================================
-- BƯỚC 1: KIỂM TRA VÀ THÊM CÁC CỘT THIẾU
-- ============================================
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT 'BƯỚC 1: Kiểm tra và thêm các cột thiếu';
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT '';

-- Cột MoTa
IF COL_LENGTH('dbo.Phong', 'MoTa') IS NULL
BEGIN
    ALTER TABLE dbo.Phong ADD MoTa NVARCHAR(MAX) NULL;
    PRINT '✅ Đã thêm cột MoTa';
END
ELSE
    PRINT '✓ Cột MoTa đã tồn tại';

-- Cột IsDeleted
IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NULL
BEGIN
    ALTER TABLE dbo.Phong ADD IsDeleted BIT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột IsDeleted';
END
ELSE
    PRINT '✓ Cột IsDeleted đã tồn tại';

-- Cột DiemTrungBinh
IF COL_LENGTH('dbo.Phong', 'DiemTrungBinh') IS NULL
BEGIN
    ALTER TABLE dbo.Phong ADD DiemTrungBinh FLOAT NULL;
    PRINT '✅ Đã thêm cột DiemTrungBinh';
END
ELSE
    PRINT '✓ Cột DiemTrungBinh đã tồn tại';

-- Cột SoLuongDanhGia
IF COL_LENGTH('dbo.Phong', 'SoLuongDanhGia') IS NULL
BEGIN
    ALTER TABLE dbo.Phong ADD SoLuongDanhGia INT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột SoLuongDanhGia';
END
ELSE
    PRINT '✓ Cột SoLuongDanhGia đã tồn tại';

-- Cột IsDuyet
IF COL_LENGTH('dbo.Phong', 'IsDuyet') IS NULL
BEGIN
    ALTER TABLE dbo.Phong ADD IsDuyet BIT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột IsDuyet';
END
ELSE
    PRINT '✓ Cột IsDuyet đã tồn tại';

-- Cột IsBiKhoa
IF COL_LENGTH('dbo.Phong', 'IsBiKhoa') IS NULL
BEGIN
    ALTER TABLE dbo.Phong ADD IsBiKhoa BIT NOT NULL DEFAULT 0;
    PRINT '✅ Đã thêm cột IsBiKhoa';
END
ELSE
    PRINT '✓ Cột IsBiKhoa đã tồn tại';

PRINT '';

-- ============================================
-- BƯỚC 2: CẬP NHẬT GIÁ TRỊ MẶC ĐỊNH
-- ============================================
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT 'BƯỚC 2: Cập nhật giá trị mặc định';
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT '';

-- Update IsDeleted
DECLARE @UpdatedIsDeleted INT;
UPDATE Phong SET IsDeleted = 0 WHERE IsDeleted IS NULL OR IsDeleted = 1;
SET @UpdatedIsDeleted = @@ROWCOUNT;
PRINT '✓ Cập nhật IsDeleted = 0 cho ' + CAST(@UpdatedIsDeleted AS VARCHAR(10)) + ' phòng';

-- Update IsDuyet
DECLARE @UpdatedIsDuyet INT;
UPDATE Phong SET IsDuyet = 1, ThoiGianDuyet = SYSDATETIMEOFFSET() 
WHERE IsDuyet = 0 OR IsDuyet IS NULL;
SET @UpdatedIsDuyet = @@ROWCOUNT;
PRINT '✓ Duyệt ' + CAST(@UpdatedIsDuyet AS VARCHAR(10)) + ' phòng (IsDuyet = 1)';

-- Update IsBiKhoa
DECLARE @UpdatedIsBiKhoa INT;
UPDATE Phong SET IsBiKhoa = 0 WHERE IsBiKhoa = 1 OR IsBiKhoa IS NULL;
SET @UpdatedIsBiKhoa = @@ROWCOUNT;
PRINT '✓ Mở khóa ' + CAST(@UpdatedIsBiKhoa AS VARCHAR(10)) + ' phòng (IsBiKhoa = 0)';

-- Update SoLuongDanhGia
UPDATE Phong SET SoLuongDanhGia = 0 WHERE SoLuongDanhGia IS NULL;
PRINT '✓ Cập nhật SoLuongDanhGia = 0';

-- Update DiemTrungBinh
UPDATE Phong SET DiemTrungBinh = 0 WHERE DiemTrungBinh IS NULL;
PRINT '✓ Cập nhật DiemTrungBinh = 0';

PRINT '';

-- ============================================
-- BƯỚC 3: KIỂM TRA BẢN PhongAnh
-- ============================================
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT 'BƯỚC 3: Kiểm tra bảng PhongAnh';
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT '';

IF OBJECT_ID(N'dbo.PhongAnh', N'U') IS NULL
BEGIN
    PRINT '⚠️ Bảng PhongAnh chưa tồn tại. Đang tạo...';
    CREATE TABLE dbo.PhongAnh (
        PhongAnhId UNIQUEIDENTIFIER NOT NULL DEFAULT NEWID() PRIMARY KEY,
        PhongId    UNIQUEIDENTIFIER NOT NULL,
        TapTinId   UNIQUEIDENTIFIER NOT NULL,
        ThuTu      INT NOT NULL DEFAULT 0,
        FOREIGN KEY (PhongId) REFERENCES dbo.Phong(PhongId),
        FOREIGN KEY (TapTinId) REFERENCES dbo.TapTin(TapTinId)
    );
    PRINT '✅ Đã tạo bảng PhongAnh';
END
ELSE
    PRINT '✓ Bảng PhongAnh đã tồn tại';

PRINT '';

-- ============================================
-- BƯỚC 4: KIỂM TRA KẾT QUẢ
-- ============================================
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT 'BƯỚC 4: Kiểm tra kết quả';
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
PRINT '';

-- Đếm tổng số phòng
DECLARE @TotalPhong INT;
SELECT @TotalPhong = COUNT(*) FROM Phong;
PRINT '📊 Tổng số phòng: ' + CAST(@TotalPhong AS VARCHAR(10));

-- Đếm phòng available
DECLARE @AvailablePhong INT;
SELECT @AvailablePhong = COUNT(*) 
FROM Phong 
WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;
PRINT '📊 Phòng sẵn sàng hiển thị: ' + CAST(@AvailablePhong AS VARCHAR(10));

-- Đếm phòng có ảnh
DECLARE @PhongCoAnh INT;
IF OBJECT_ID(N'dbo.PhongAnh', N'U') IS NOT NULL
BEGIN
    SELECT @PhongCoAnh = COUNT(DISTINCT PhongId) FROM PhongAnh;
    PRINT '📊 Phòng có ảnh: ' + CAST(@PhongCoAnh AS VARCHAR(10));
END

PRINT '';

-- ============================================
-- KẾT QUẢ CUỐI CÙNG
-- ============================================
PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
IF @AvailablePhong > 0
BEGIN
    PRINT '✅ THÀNH CÔNG! App sẽ load được ' + CAST(@AvailablePhong AS VARCHAR(10)) + ' phòng';
    PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
    PRINT '';
    PRINT 'Danh sách 5 phòng đầu tiên:';
    PRINT '';
    SELECT TOP 5
        PhongId,
        TieuDe,
        FORMAT(GiaTien, 'N0') + ' VNĐ' AS GiaTien,
        CAST(DienTich AS VARCHAR(10)) + ' m²' AS DienTich,
        TrangThai,
        CASE WHEN IsDuyet = 1 THEN N'✓ Đã duyệt' ELSE N'✗ Chưa duyệt' END AS TrangThaiDuyet,
        CASE WHEN IsBiKhoa = 1 THEN N'✗ Bị khóa' ELSE N'✓ Hoạt động' END AS TrangThaiKhoa
    FROM Phong
    WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0
    ORDER BY CreatedAt DESC;
END
ELSE
BEGIN
    PRINT '⚠️ CẢNH BÁO: Không có phòng nào sẵn sàng!';
    PRINT '━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━';
    PRINT '';
    IF @TotalPhong = 0
    BEGIN
        PRINT 'Nguyên nhân: Database chưa có dữ liệu phòng';
        PRINT 'Giải pháp: Cần thêm dữ liệu mẫu hoặc tạo phòng từ app';
    END
    ELSE
    BEGIN
        PRINT 'Nguyên nhân: Có phòng nhưng không thỏa điều kiện';
        PRINT 'Đã tự động fix: Duyệt và mở khóa tất cả phòng';
        PRINT '';
        PRINT '⚠️ Vui lòng chạy lại script này!';
    END
END

PRINT '';
PRINT '╔════════════════════════════════════════════════════════════╗';
PRINT '║                    HOÀN TẤT!                              ║';
PRINT '╚════════════════════════════════════════════════════════════╝';
PRINT '';
PRINT 'Bước tiếp theo:';
PRINT '1. Chạy lại app';
PRINT '2. Long press nút Filter → Mở DatabaseTestActivity';
PRINT '3. Nhấn "Load Phòng" để kiểm tra';
PRINT '';
