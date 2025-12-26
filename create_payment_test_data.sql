-- Tạo dữ liệu test cho BienLai (yêu cầu thanh toán)
USE QuanLyPhongTro;

-- Lấy thông tin ChuTro và NguoiThue có sẵn
DECLARE @ChuTroId UNIQUEIDENTIFIER;
DECLARE @NguoiThueId UNIQUEIDENTIFIER;
DECLARE @PhongId UNIQUEIDENTIFIER;
DECLARE @DatPhongId UNIQUEIDENTIFIER;

-- Tìm ChuTro đầu tiên
SELECT TOP 1 @ChuTroId = nd.NguoiDungId 
FROM NguoiDung nd 
INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId 
WHERE vt.TenVaiTro = 'ChuTro';

-- Tìm NguoiThue đầu tiên  
SELECT TOP 1 @NguoiThueId = nd.NguoiDungId 
FROM NguoiDung nd 
INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId 
WHERE vt.TenVaiTro = 'NguoiThue';

-- Tìm Phong đầu tiên
SELECT TOP 1 @PhongId = PhongId FROM Phong;

PRINT 'ChuTroId: ' + CAST(@ChuTroId AS NVARCHAR(50));
PRINT 'NguoiThueId: ' + CAST(@NguoiThueId AS NVARCHAR(50));
PRINT 'PhongId: ' + CAST(@PhongId AS NVARCHAR(50));

-- Tạo DatPhong nếu chưa có
IF @ChuTroId IS NOT NULL AND @NguoiThueId IS NOT NULL AND @PhongId IS NOT NULL
BEGIN
    -- Kiểm tra xem đã có DatPhong chưa
    SELECT TOP 1 @DatPhongId = DatPhongId 
    FROM DatPhong 
    WHERE ChuTroId = @ChuTroId AND NguoiThueId = @NguoiThueId AND PhongId = @PhongId;
    
    -- Nếu chưa có thì tạo mới
    IF @DatPhongId IS NULL
    BEGIN
        SET @DatPhongId = NEWID();
        
        INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, BatDau, KetThuc, Loai, TrangThaiId, GhiChu)
        VALUES (
            @DatPhongId, 
            @PhongId, 
            @NguoiThueId, 
            @ChuTroId, 
            DATEADD(day, -30, GETDATE()), 
            DATEADD(day, 335, GETDATE()), 
            N'Thuê phòng dài hạn',
            (SELECT TOP 1 TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaXacNhan'),
            N'Đặt phòng test cho thanh toán'
        );
        
        PRINT 'Created DatPhong: ' + CAST(@DatPhongId AS NVARCHAR(50));
    END
    ELSE
    BEGIN
        PRINT 'Using existing DatPhong: ' + CAST(@DatPhongId AS NVARCHAR(50));
    END
    
    -- Tạo các BienLai test
    DECLARE @BienLai1 UNIQUEIDENTIFIER = NEWID();
    DECLARE @BienLai2 UNIQUEIDENTIFIER = NEWID();
    DECLARE @BienLai3 UNIQUEIDENTIFIER = NEWID();
    DECLARE @BienLai4 UNIQUEIDENTIFIER = NEWID();
    
    -- BienLai 1: Chờ xác nhận - Tiền thuê tháng 12
    IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai1)
    BEGIN
        INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
        VALUES (@BienLai1, @DatPhongId, @NguoiThueId, 3500000, 0, DATEADD(day, -5, GETDATE()));
        PRINT 'Created BienLai 1: Tiền thuê tháng 12 - 3,500,000đ (Chờ xác nhận)';
    END
    
    -- BienLai 2: Đã xác nhận - Tiền cọc
    IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai2)
    BEGIN
        INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
        VALUES (@BienLai2, @DatPhongId, @NguoiThueId, 5000000, 1, DATEADD(day, -10, GETDATE()));
        PRINT 'Created BienLai 2: Tiền cọc - 5,000,000đ (Đã xác nhận)';
    END
    
    -- BienLai 3: Chờ xác nhận - Tiền điện nước
    IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai3)
    BEGIN
        INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
        VALUES (@BienLai3, @DatPhongId, @NguoiThueId, 850000, 0, DATEADD(day, -2, GETDATE()));
        PRINT 'Created BienLai 3: Tiền điện nước - 850,000đ (Chờ xác nhận)';
    END
    
    -- BienLai 4: Chờ xác nhận - Tiền thuê tháng 11
    IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai4)
    BEGIN
        INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
        VALUES (@BienLai4, @DatPhongId, @NguoiThueId, 3500000, 0, DATEADD(day, -35, GETDATE()));
        PRINT 'Created BienLai 4: Tiền thuê tháng 11 - 3,500,000đ (Chờ xác nhận)';
    END
    
    PRINT 'Successfully created test payment data!';
    
    -- Hiển thị kết quả
    SELECT 
        bl.BienLaiId,
        bl.SoTien,
        bl.DaXacNhan,
        bl.ThoiGianTai,
        dp.Loai,
        hs.HoTen as TenNguoiThue,
        p.TieuDe as TenPhong
    FROM BienLai bl
    INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
    INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId
    INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
    INNER JOIN Phong p ON dp.PhongId = p.PhongId
    WHERE dp.ChuTroId = @ChuTroId
    ORDER BY bl.ThoiGianTai DESC;
    
END
ELSE
BEGIN
    PRINT 'ERROR: Missing required data (ChuTro, NguoiThue, or Phong)';
    
    SELECT 'ChuTro count' as Info, COUNT(*) as Count 
    FROM NguoiDung nd INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId 
    WHERE vt.TenVaiTro = 'ChuTro';
    
    SELECT 'NguoiThue count' as Info, COUNT(*) as Count 
    FROM NguoiDung nd INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId 
    WHERE vt.TenVaiTro = 'NguoiThue';
    
    SELECT 'Phong count' as Info, COUNT(*) as Count FROM Phong;
END