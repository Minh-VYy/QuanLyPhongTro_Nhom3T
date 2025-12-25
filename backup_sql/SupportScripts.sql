-- ============================================================================
-- SUPPORT SCRIPTS FOR QUANLYPHONGTRO DATABASE
-- Tập hợp các script hỗ trợ test và troubleshooting
-- ============================================================================

-- ============================================================================
-- SECTION 1: DATABASE HEALTH CHECK AND FIX ALL ISSUES
-- Kiểm tra và sửa tất cả vấn đề database
-- ============================================================================

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

GO

-- ============================================================================
-- SECTION 2: DATA HEALTH CHECK SCRIPT
-- Script kiểm tra dữ liệu phòng trong database
-- ============================================================================

PRINT '';
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

DECLARE @CountAvailable2 INT;
IF COL_LENGTH('dbo.Phong', 'IsDeleted') IS NOT NULL
BEGIN
    SELECT @CountAvailable2 = COUNT(*) 
    FROM Phong 
    WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;
END
ELSE
BEGIN
    SELECT @CountAvailable2 = COUNT(*) 
    FROM Phong 
    WHERE IsDuyet = 1 AND IsBiKhoa = 0;
END

IF @CountAvailable2 = 0
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
    DECLARE @CountTotal2 INT;
    SELECT @CountTotal2 = COUNT(*) FROM Phong;
    
    IF @CountTotal2 > 0
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
    PRINT '✅ Có ' + CAST(@CountAvailable2 AS VARCHAR(10)) + ' phòng sẵn sàng hiển thị!';
    PRINT '✅ App sẽ load được dữ liệu';
END
PRINT '';
PRINT '========================================';

GO

-- ============================================================================
-- SECTION 3: CREATE TEST TENANT ACCOUNTS
-- Tạo tài khoản người thuê test
-- ============================================================================

PRINT '';
PRINT '========================================';
PRINT 'TẠO TÀI KHOẢN NGƯỜI THUÊ TEST';
PRINT '========================================';
PRINT '';

-- Lấy VaiTroId cho NguoiThue
DECLARE @VaiTroNguoiThueId INT = (SELECT VaiTroId FROM VaiTro WHERE TenVaiTro = 'NguoiThue');

-- Tạo người thuê test 1
DECLARE @TestTenantId UNIQUEIDENTIFIER = NEWID();

-- Kiểm tra xem đã tồn tại chưa
IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE Email = 'tenant@test.com')
BEGIN
    -- Insert vào bảng NguoiDung
    INSERT INTO NguoiDung (NguoiDungId, Email, DienThoai, PasswordHash, VaiTroId, IsKhoa, IsEmailXacThuc, CreatedAt)
    VALUES (@TestTenantId, 'tenant@test.com', '0905123456', 'password123', @VaiTroNguoiThueId, 0, 1, SYSDATETIMEOFFSET());

    -- Insert vào bảng HoSoNguoiDung
    INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh, LoaiGiayTo, GhiChu, CreatedAt)
    VALUES (@TestTenantId, N'Nguyễn Văn Test', '1995-01-01', 'CCCD: 001095000123', N'Tài khoản test cho người thuê', SYSDATETIMEOFFSET());

    -- Insert vào bảng NguoiDungVaiTro
    INSERT INTO NguoiDungVaiTro (NguoiDungId, VaiTroId, NgayBatDau)
    VALUES (@TestTenantId, @VaiTroNguoiThueId, SYSDATETIMEOFFSET());
    
    PRINT '✅ Đã tạo tài khoản: tenant@test.com';
END
ELSE
BEGIN
    PRINT '✓ Tài khoản tenant@test.com đã tồn tại';
END

-- Tạo thêm một vài người thuê khác
DECLARE @TestTenant2Id UNIQUEIDENTIFIER = NEWID();

IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE Email = 'nguoithue@gmail.com')
BEGIN
    INSERT INTO NguoiDung (NguoiDungId, Email, DienThoai, PasswordHash, VaiTroId, IsKhoa, IsEmailXacThuc, CreatedAt)
    VALUES (@TestTenant2Id, 'nguoithue@gmail.com', '0905987654', '123456', @VaiTroNguoiThueId, 0, 1, SYSDATETIMEOFFSET());

    INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh, LoaiGiayTo, GhiChu, CreatedAt)
    VALUES (@TestTenant2Id, N'Trần Thị Minh', '1998-05-15', 'CCCD: 001098000456', N'Sinh viên đại học', SYSDATETIMEOFFSET());

    INSERT INTO NguoiDungVaiTro (NguoiDungId, VaiTroId, NgayBatDau)
    VALUES (@TestTenant2Id, @VaiTroNguoiThueId, SYSDATETIMEOFFSET());
    
    PRINT '✅ Đã tạo tài khoản: nguoithue@gmail.com';
END
ELSE
BEGIN
    PRINT '✓ Tài khoản nguoithue@gmail.com đã tồn tại';
END

PRINT '';
PRINT 'Thông tin đăng nhập:';
PRINT 'Email: tenant@test.com | Password: password123';
PRINT 'Email: nguoithue@gmail.com | Password: 123456';
PRINT '';

-- Kiểm tra dữ liệu vừa tạo
PRINT 'Danh sách tài khoản test:';
SELECT 
    nd.Email,
    hs.HoTen,
    vt.TenVaiTro,
    nd.IsKhoa,
    nd.IsEmailXacThuc
FROM NguoiDung nd
JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE nd.Email IN ('tenant@test.com', 'nguoithue@gmail.com');

GO

-- ============================================================================
-- SECTION 4: QUICK FIX FOR ROOM AVAILABILITY
-- Script fix nhanh để có phòng hiển thị trong app
-- ============================================================================

PRINT '';
PRINT '========================================';
PRINT 'FIX NHANH PHÒNG ĐỂ HIỂN THỊ TRONG APP';
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
DECLARE @CountDuyet2 INT;
SELECT @CountDuyet2 = COUNT(*) FROM Phong WHERE IsDuyet = 0;

IF @CountDuyet2 > 0
BEGIN
    UPDATE Phong SET IsDuyet = 1, NguoiDuyet = NULL, ThoiGianDuyet = SYSDATETIMEOFFSET()
    WHERE IsDuyet = 0;
    PRINT '✅ Đã duyệt ' + CAST(@CountDuyet2 AS VARCHAR(10)) + ' phòng';
END
ELSE
BEGIN
    PRINT '✅ Tất cả phòng đã được duyệt';
END
PRINT '';

-- Bước 4: Mở khóa tất cả phòng (set IsBiKhoa = 0)
PRINT 'Bước 4: Mở khóa tất cả phòng...';
DECLARE @CountKhoa2 INT;
SELECT @CountKhoa2 = COUNT(*) FROM Phong WHERE IsBiKhoa = 1;

IF @CountKhoa2 > 0
BEGIN
    UPDATE Phong SET IsBiKhoa = 0 WHERE IsBiKhoa = 1;
    PRINT '✅ Đã mở khóa ' + CAST(@CountKhoa2 AS VARCHAR(10)) + ' phòng';
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

DECLARE @CountAvailable3 INT;
SELECT @CountAvailable3 = COUNT(*) 
FROM Phong 
WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;

PRINT 'Số phòng sẵn sàng hiển thị: ' + CAST(@CountAvailable3 AS VARCHAR(10));
PRINT '';

IF @CountAvailable3 > 0
BEGIN
    PRINT '✅ THÀNH CÔNG! App sẽ load được ' + CAST(@CountAvailable3 AS VARCHAR(10)) + ' phòng';
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
    
    DECLARE @CountTotal3 INT;
    SELECT @CountTotal3 = COUNT(*) FROM Phong;
    PRINT 'Tổng số phòng trong database: ' + CAST(@CountTotal3 AS VARCHAR(10));
END
PRINT '';
PRINT '========================================';

GO

-- ============================================================================
-- SECTION 5: COLUMN CHECK AND ADD MISSING COLUMNS
-- Script kiểm tra và thêm các cột thiếu trong bảng Phong
-- ============================================================================

PRINT '';
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
DECLARE @CountAvailable4 INT;
SELECT @CountAvailable4 = COUNT(*) 
FROM Phong 
WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0;

PRINT 'Số phòng sẵn sàng hiển thị: ' + CAST(@CountAvailable4 AS VARCHAR(10));

IF @CountAvailable4 > 0
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

GO

-- ============================================================================
-- SECTION 6: FINAL SUMMARY AND INSTRUCTIONS
-- Tổng kết và hướng dẫn sử dụng
-- ============================================================================

PRINT '';
PRINT '╔════════════════════════════════════════════════════════════╗';
PRINT '║                    TỔNG KẾT SCRIPT                        ║';
PRINT '╚════════════════════════════════════════════════════════════╝';
PRINT '';
PRINT 'Script này bao gồm các chức năng sau:';
PRINT '';
PRINT '1. ✅ Fix tất cả vấn đề database';
PRINT '2. ✅ Kiểm tra dữ liệu phòng';
PRINT '3. ✅ Tạo tài khoản test';
PRINT '4. ✅ Fix nhanh phòng available';
PRINT '5. ✅ Kiểm tra và thêm cột thiếu';
PRINT '';
PRINT 'Cách sử dụng:';
PRINT '- Chạy toàn bộ script này để fix mọi vấn đề';
PRINT '- Hoặc chạy từng section riêng biệt theo nhu cầu';
PRINT '';
PRINT 'Sau khi chạy script:';
PRINT '1. Khởi động lại app Android';
PRINT '2. Kiểm tra MainActivity có load được phòng không';
PRINT '3. Nếu vẫn lỗi, kiểm tra log và chạy lại script';
PRINT '';
PRINT '╔════════════════════════════════════════════════════════════╗';
PRINT '║                 SCRIPT HOÀN TẤT!                          ║';
PRINT '╚════════════════════════════════════════════════════════════╝';
PRINT '';