-- Tạo dữ liệu test cho yêu cầu đặt lịch và thanh toán - CORRECTED VERSION v2
USE QuanLyPhongTro;
GO

PRINT '=== TẠO DỮ LIỆU TEST YÊU CẦU ĐẶT LỊCH VÀ THANH TOÁN ===';
PRINT '';

-- Sử dụng ChuTroId từ session (thay đổi theo user đang đăng nhập)
-- Hoặc lấy ChuTroId đầu tiên có sẵn
DECLARE @ChuTroId UNIQUEIDENTIFIER;

-- Thử lấy ChuTroId từ bảng NguoiDung với VaiTro = 'ChuTro'
SELECT TOP 1 @ChuTroId = nd.NguoiDungId 
FROM NguoiDung nd 
INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId 
WHERE vt.TenVaiTro = 'ChuTro';

-- Nếu không có, tạo ChuTroId test
IF @ChuTroId IS NULL
BEGIN
    SET @ChuTroId = '44444444-4444-4444-4444-444444444444';
    PRINT '⚠️ Sử dụng ChuTroId test: ' + CAST(@ChuTroId AS VARCHAR(50));
END
ELSE
BEGIN
    PRINT '✅ Sử dụng ChuTroId từ database: ' + CAST(@ChuTroId AS VARCHAR(50));
END

PRINT '1. Kiểm tra và tạo dữ liệu cơ bản...';

-- Tạo VaiTro nếu chưa có (VaiTroId là INT)
DECLARE @VaiTroNguoiThue INT, @VaiTroChuTro INT;

IF NOT EXISTS (SELECT 1 FROM VaiTro WHERE TenVaiTro = 'NguoiThue')
BEGIN
    INSERT INTO VaiTro (TenVaiTro) VALUES ('NguoiThue');
    PRINT '✅ Đã tạo VaiTro NguoiThue';
END

IF NOT EXISTS (SELECT 1 FROM VaiTro WHERE TenVaiTro = 'ChuTro')
BEGIN
    INSERT INTO VaiTro (TenVaiTro) VALUES ('ChuTro');
    PRINT '✅ Đã tạo VaiTro ChuTro';
END

-- Lấy VaiTroId (INT)
SELECT @VaiTroNguoiThue = VaiTroId FROM VaiTro WHERE TenVaiTro = 'NguoiThue';
SELECT @VaiTroChuTro = VaiTroId FROM VaiTro WHERE TenVaiTro = 'ChuTro';

-- Tạo NguoiThue test nếu chưa có
DECLARE @NguoiThue1 UNIQUEIDENTIFIER = NEWID();
DECLARE @NguoiThue2 UNIQUEIDENTIFIER = NEWID();
DECLARE @NguoiThue3 UNIQUEIDENTIFIER = NEWID();

-- Kiểm tra và tạo NguoiDung cho NguoiThue
IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE Email = 'nguoithue1@test.com')
BEGIN
    INSERT INTO NguoiDung (NguoiDungId, Email, VaiTroId)
    VALUES (@NguoiThue1, 'nguoithue1@test.com', @VaiTroNguoiThue);
    
    INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh)
    VALUES (@NguoiThue1, 'Nguyễn Văn A', '1995-01-15');
    PRINT '✅ Đã tạo NguoiThue 1: Nguyễn Văn A';
END
ELSE
BEGIN
    SELECT @NguoiThue1 = NguoiDungId FROM NguoiDung WHERE Email = 'nguoithue1@test.com';
    PRINT '✓ NguoiThue 1 đã tồn tại';
END

IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE Email = 'nguoithue2@test.com')
BEGIN
    INSERT INTO NguoiDung (NguoiDungId, Email, VaiTroId)
    VALUES (@NguoiThue2, 'nguoithue2@test.com', @VaiTroNguoiThue);
    
    INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh)
    VALUES (@NguoiThue2, 'Trần Thị B', '1992-05-20');
    PRINT '✅ Đã tạo NguoiThue 2: Trần Thị B';
END
ELSE
BEGIN
    SELECT @NguoiThue2 = NguoiDungId FROM NguoiDung WHERE Email = 'nguoithue2@test.com';
    PRINT '✓ NguoiThue 2 đã tồn tại';
END

IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE Email = 'nguoithue3@test.com')
BEGIN
    INSERT INTO NguoiDung (NguoiDungId, Email, VaiTroId)
    VALUES (@NguoiThue3, 'nguoithue3@test.com', @VaiTroNguoiThue);
    
    INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh)
    VALUES (@NguoiThue3, 'Lê Văn C', '1990-08-10');
    PRINT '✅ Đã tạo NguoiThue 3: Lê Văn C';
END
ELSE
BEGIN
    SELECT @NguoiThue3 = NguoiDungId FROM NguoiDung WHERE Email = 'nguoithue3@test.com';
    PRINT '✓ NguoiThue 3 đã tồn tại';
END

-- Tạo ChuTro nếu chưa có
IF NOT EXISTS (SELECT 1 FROM NguoiDung WHERE NguoiDungId = @ChuTroId)
BEGIN
    INSERT INTO NguoiDung (NguoiDungId, Email, VaiTroId)
    VALUES (@ChuTroId, 'chutro@test.com', @VaiTroChuTro);
    
    INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh)
    VALUES (@ChuTroId, 'Chủ Trọ Test', '1980-01-01');
    PRINT '✅ Đã tạo ChuTro test';
END

-- Tạo NhaTro và Phong test
DECLARE @NhaTroId UNIQUEIDENTIFIER;
SELECT TOP 1 @NhaTroId = NhaTroId FROM NhaTro WHERE ChuTroId = @ChuTroId;

IF @NhaTroId IS NULL
BEGIN
    SET @NhaTroId = NEWID();
    INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi)
    VALUES (@NhaTroId, @ChuTroId, 'Nhà trọ test', '123 Đường Test, TP.HCM');
    PRINT '✅ Đã tạo NhaTro test';
END
ELSE
BEGIN
    PRINT '✓ NhaTro đã tồn tại';
END

-- Tạo Phong test
DECLARE @Phong1 UNIQUEIDENTIFIER, @Phong2 UNIQUEIDENTIFIER, @Phong3 UNIQUEIDENTIFIER;

SELECT TOP 1 @Phong1 = PhongId FROM Phong WHERE NhaTroId = @NhaTroId AND TieuDe LIKE '%101%';
IF @Phong1 IS NULL
BEGIN
    SET @Phong1 = NEWID();
    INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TrangThai)
    VALUES (@Phong1, @NhaTroId, 'Phòng 101 - Quận 1', 25, 3500000, 'Còn trống');
    PRINT '✅ Đã tạo Phòng 101';
END

SELECT TOP 1 @Phong2 = PhongId FROM Phong WHERE NhaTroId = @NhaTroId AND TieuDe LIKE '%205%';
IF @Phong2 IS NULL
BEGIN
    SET @Phong2 = NEWID();
    INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TrangThai)
    VALUES (@Phong2, @NhaTroId, 'Phòng 205 - Quận 7', 30, 4200000, 'Còn trống');
    PRINT '✅ Đã tạo Phòng 205';
END

SELECT TOP 1 @Phong3 = PhongId FROM Phong WHERE NhaTroId = @NhaTroId AND TieuDe LIKE '%302%';
IF @Phong3 IS NULL
BEGIN
    SET @Phong3 = NEWID();
    INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TrangThai)
    VALUES (@Phong3, @NhaTroId, 'Phòng 302 - Quận 3', 35, 5000000, 'Còn trống');
    PRINT '✅ Đã tạo Phòng 302';
END

PRINT '2. Tạo TrangThaiDatPhong nếu chưa có...';

-- Tạo TrangThaiDatPhong (TrangThaiId là INT, auto-increment)
IF NOT EXISTS (SELECT 1 FROM TrangThaiDatPhong WHERE TenTrangThai = 'ChoXacNhan')
BEGIN
    INSERT INTO TrangThaiDatPhong (TenTrangThai) VALUES ('ChoXacNhan');
    PRINT '✅ Đã tạo TrangThai ChoXacNhan';
END

IF NOT EXISTS (SELECT 1 FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaXacNhan')
BEGIN
    INSERT INTO TrangThaiDatPhong (TenTrangThai) VALUES ('DaXacNhan');
    PRINT '✅ Đã tạo TrangThai DaXacNhan';
END

IF NOT EXISTS (SELECT 1 FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaHuy')
BEGIN
    INSERT INTO TrangThaiDatPhong (TenTrangThai) VALUES ('DaHuy');
    PRINT '✅ Đã tạo TrangThai DaHuy';
END

-- Lấy TrangThaiId (INT)
DECLARE @StatusChoXacNhan INT, @StatusDaXacNhan INT, @StatusDaHuy INT;
SELECT @StatusChoXacNhan = TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'ChoXacNhan';
SELECT @StatusDaXacNhan = TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaXacNhan';
SELECT @StatusDaHuy = TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaHuy';

PRINT '3. Tạo dữ liệu DatPhong (Yêu cầu đặt lịch)...';

-- Xóa dữ liệu test cũ
DELETE FROM BienLai WHERE DatPhongId IN (
    SELECT DatPhongId FROM DatPhong WHERE ChuTroId = @ChuTroId AND GhiChu LIKE '%test%'
);
DELETE FROM DatPhong WHERE ChuTroId = @ChuTroId AND GhiChu LIKE '%test%';

-- Tạo DatPhong (Yêu cầu đặt lịch) - sử dụng cấu trúc thật
DECLARE @DatPhong1 UNIQUEIDENTIFIER = NEWID();
DECLARE @DatPhong2 UNIQUEIDENTIFIER = NEWID();
DECLARE @DatPhong3 UNIQUEIDENTIFIER = NEWID();
DECLARE @DatPhong4 UNIQUEIDENTIFIER = NEWID();

INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, Loai, BatDau, KetThuc, ThoiGianTao, TrangThaiId, GhiChu)
VALUES 
    (@DatPhong1, @Phong1, @NguoiThue1, @ChuTroId, 'Xem phong', 
     DATEADD(day, 1, SYSDATETIMEOFFSET()), DATEADD(day, 2, SYSDATETIMEOFFSET()), 
     DATEADD(hour, -2, SYSDATETIMEOFFSET()), @StatusChoXacNhan, 'Muốn xem phòng vào chiều mai - test data'),
     
    (@DatPhong2, @Phong2, @NguoiThue2, @ChuTroId, 'Thue phong', 
     DATEADD(day, 7, SYSDATETIMEOFFSET()), DATEADD(month, 12, SYSDATETIMEOFFSET()), 
     DATEADD(hour, -5, SYSDATETIMEOFFSET()), @StatusDaXacNhan, 'Đã xác nhận thuê phòng từ tuần sau - test data'),
     
    (@DatPhong3, @Phong3, @NguoiThue3, @ChuTroId, 'Xem phong', 
     DATEADD(day, 3, SYSDATETIMEOFFSET()), DATEADD(day, 4, SYSDATETIMEOFFSET()), 
     DATEADD(hour, -1, SYSDATETIMEOFFSET()), @StatusChoXacNhan, 'Cần xem phòng cuối tuần này - test data'),
     
    (@DatPhong4, @Phong1, @NguoiThue3, @ChuTroId, 'Thue phong', 
     DATEADD(day, 14, SYSDATETIMEOFFSET()), DATEADD(month, 6, SYSDATETIMEOFFSET()), 
     DATEADD(minute, -30, SYSDATETIMEOFFSET()), @StatusChoXacNhan, 'Muốn thuê phòng từ tháng sau - test data');

PRINT '✅ Đã tạo 4 DatPhong test';

PRINT '4. Tạo dữ liệu BienLai (Yêu cầu thanh toán)...';

-- Tạo BienLai (Yêu cầu thanh toán) - sử dụng cấu trúc thật
INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
VALUES 
    (NEWID(), @DatPhong2, @NguoiThue2, 4200000, 0, DATEADD(day, -2, SYSDATETIMEOFFSET())), -- Tiền thuê tháng đầu
    (NEWID(), @DatPhong2, @NguoiThue2, 5000000, 1, DATEADD(day, -5, SYSDATETIMEOFFSET())), -- Tiền cọc đã xác nhận
    (NEWID(), @DatPhong2, @NguoiThue2, 850000, 0, DATEADD(day, -1, SYSDATETIMEOFFSET())),  -- Tiền điện nước
    (NEWID(), @DatPhong4, @NguoiThue3, 3500000, 0, SYSDATETIMEOFFSET());                   -- Tiền cọc mới

PRINT '✅ Đã tạo 4 BienLai test';

PRINT '5. Kiểm tra dữ liệu đã tạo...';

-- Kiểm tra kết quả
SELECT 'DatPhong created' as TableName, COUNT(*) as RecordCount 
FROM DatPhong WHERE ChuTroId = @ChuTroId AND GhiChu LIKE '%test%';

SELECT 'BienLai created' as TableName, COUNT(*) as RecordCount 
FROM BienLai bl 
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId 
WHERE dp.ChuTroId = @ChuTroId AND dp.GhiChu LIKE '%test%';

PRINT '';
PRINT '6. Test query BookingRequestDao:';
SELECT 
    dp.DatPhongId, dp.PhongId, dp.NguoiThueId, dp.ChuTroId,
    ISNULL(dp.Loai, 'Đặt lịch xem phòng') as Loai,
    dp.BatDau, dp.KetThuc,
    ISNULL(dp.ThoiGianTao, SYSDATETIMEOFFSET()) as ThoiGianTao,
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
WHERE dp.ChuTroId = @ChuTroId
ORDER BY dp.ThoiGianTao DESC;

PRINT '';
PRINT '7. Test query PaymentRequestDao:';
SELECT 
    bl.BienLaiId, bl.DatPhongId, bl.SoTien, bl.NguoiTai, bl.TapTinId, bl.DaXacNhan, bl.ThoiGianTai,
    dp.NguoiThueId, dp.ChuTroId,
    ISNULL(dp.Loai, 'Thanh toán tiền thuê') as Loai,
    ISNULL(hs.HoTen, 'Người thuê') as TenNguoiThue,
    ISNULL(p.TieuDe, 'Phòng trọ') as TenPhong
FROM BienLai bl
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
LEFT JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId
LEFT JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId
LEFT JOIN Phong p ON dp.PhongId = p.PhongId
WHERE dp.ChuTroId = @ChuTroId
ORDER BY bl.ThoiGianTai DESC;

PRINT '';
PRINT 'ChuTroId sử dụng: ' + CAST(@ChuTroId AS VARCHAR(50));
PRINT '=== HOÀN THÀNH TẠO DỮ LIỆU TEST ===';