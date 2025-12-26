-- Tạo dữ liệu demo cho yêu cầu thanh toán
USE QuanLyPhongTro;

-- Lấy thông tin cần thiết
DECLARE @ChuTro1 UNIQUEIDENTIFIER = (SELECT TOP 1 NguoiDungId FROM NguoiDung nd INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId WHERE vt.TenVaiTro = 'ChuTro');
DECLARE @NguoiThue1 UNIQUEIDENTIFIER = (SELECT TOP 1 NguoiDungId FROM NguoiDung nd INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId WHERE vt.TenVaiTro = 'NguoiThue');
DECLARE @Phong1 UNIQUEIDENTIFIER = (SELECT TOP 1 PhongId FROM Phong);
DECLARE @TrangThaiChoXacNhan INT = (SELECT TOP 1 TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'ChoXacNhan');

PRINT 'ChuTro1: ' + CAST(@ChuTro1 AS NVARCHAR(50));
PRINT 'NguoiThue1: ' + CAST(@NguoiThue1 AS NVARCHAR(50));
PRINT 'Phong1: ' + CAST(@Phong1 AS NVARCHAR(50));

-- Tạo DatPhong demo nếu chưa có
DECLARE @DatPhong1 UNIQUEIDENTIFIER = NEWID();
DECLARE @DatPhong2 UNIQUEIDENTIFIER = NEWID();
DECLARE @DatPhong3 UNIQUEIDENTIFIER = NEWID();
DECLARE @DatPhong4 UNIQUEIDENTIFIER = NEWID();

-- DatPhong 1: Tiền thuê tháng 12
IF NOT EXISTS (SELECT 1 FROM DatPhong WHERE DatPhongId = @DatPhong1)
BEGIN
    INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, TrangThaiId, Loai, GhiChu, BatDau, KetThuc)
    VALUES (@DatPhong1, @Phong1, @NguoiThue1, @ChuTro1, @TrangThaiChoXacNhan, N'Tiền thuê tháng 12', N'Thanh toán tiền thuê tháng 12/2024', GETDATE(), DATEADD(month, 1, GETDATE()));
    PRINT 'Created DatPhong 1';
END

-- DatPhong 2: Tiền cọc
IF NOT EXISTS (SELECT 1 FROM DatPhong WHERE DatPhongId = @DatPhong2)
BEGIN
    INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, TrangThaiId, Loai, GhiChu, BatDau, KetThuc)
    VALUES (@DatPhong2, @Phong1, @NguoiThue1, @ChuTro1, @TrangThaiChoXacNhan, N'Tiền cọc phòng', N'Cọc thuê phòng trước khi vào ở', GETDATE(), DATEADD(month, 1, GETDATE()));
    PRINT 'Created DatPhong 2';
END

-- DatPhong 3: Tiền điện nước
IF NOT EXISTS (SELECT 1 FROM DatPhong WHERE DatPhongId = @DatPhong3)
BEGIN
    INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, TrangThaiId, Loai, GhiChu, BatDau, KetThuc)
    VALUES (@DatPhong3, @Phong1, @NguoiThue1, @ChuTro1, @TrangThaiChoXacNhan, N'Tiền điện nước', N'Thanh toán tiền điện nước tháng 12', GETDATE(), DATEADD(month, 1, GETDATE()));
    PRINT 'Created DatPhong 3';
END

-- DatPhong 4: Phí dịch vụ
IF NOT EXISTS (SELECT 1 FROM DatPhong WHERE DatPhongId = @DatPhong4)
BEGIN
    INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, TrangThaiId, Loai, GhiChu, BatDau, KetThuc)
    VALUES (@DatPhong4, @Phong1, @NguoiThue1, @ChuTro1, @TrangThaiChoXacNhan, N'Phí dịch vụ', N'Phí quản lý và dịch vụ chung cư', GETDATE(), DATEADD(month, 1, GETDATE()));
    PRINT 'Created DatPhong 4';
END

-- Tạo BienLai demo
DECLARE @BienLai1 UNIQUEIDENTIFIER = NEWID();
DECLARE @BienLai2 UNIQUEIDENTIFIER = NEWID();
DECLARE @BienLai3 UNIQUEIDENTIFIER = NEWID();
DECLARE @BienLai4 UNIQUEIDENTIFIER = NEWID();

-- BienLai 1: Chờ xác nhận - Tiền thuê 3.500.000đ
IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai1)
BEGIN
    INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
    VALUES (@BienLai1, @DatPhong1, @NguoiThue1, 3500000, 0, GETDATE());
    PRINT 'Created BienLai 1: 3.500.000đ - Chờ xác nhận';
END

-- BienLai 2: Đã xác nhận - Tiền cọc 5.000.000đ
IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai2)
BEGIN
    INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
    VALUES (@BienLai2, @DatPhong2, @NguoiThue1, 5000000, 1, DATEADD(day, -1, GETDATE()));
    PRINT 'Created BienLai 2: 5.000.000đ - Đã xác nhận';
END

-- BienLai 3: Chờ xác nhận - Tiền điện nước 800.000đ
IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai3)
BEGIN
    INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
    VALUES (@BienLai3, @DatPhong3, @NguoiThue1, 800000, 0, DATEADD(hour, -2, GETDATE()));
    PRINT 'Created BienLai 3: 800.000đ - Chờ xác nhận';
END

-- BienLai 4: Chờ xác nhận - Phí dịch vụ 300.000đ
IF NOT EXISTS (SELECT 1 FROM BienLai WHERE BienLaiId = @BienLai4)
BEGIN
    INSERT INTO BienLai (BienLaiId, DatPhongId, NguoiTai, SoTien, DaXacNhan, ThoiGianTai)
    VALUES (@BienLai4, @DatPhong4, @NguoiThue1, 300000, 0, DATEADD(hour, -1, GETDATE()));
    PRINT 'Created BienLai 4: 300.000đ - Chờ xác nhận';
END

-- Kiểm tra kết quả
SELECT 'DEMO DATA CREATED' as Status;

SELECT 
    bl.BienLaiId, 
    bl.SoTien, 
    bl.DaXacNhan,
    bl.ThoiGianTai,
    dp.Loai,
    dp.ChuTroId,
    hs.HoTen as TenNguoiThue,
    p.TieuDe as TenPhong
FROM BienLai bl 
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId 
INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId 
INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId 
INNER JOIN Phong p ON dp.PhongId = p.PhongId 
WHERE dp.ChuTroId = @ChuTro1
ORDER BY bl.ThoiGianTai DESC;