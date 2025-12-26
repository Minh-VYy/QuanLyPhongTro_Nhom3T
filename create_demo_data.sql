-- Tạo dữ liệu demo cho landlord 00000000-0000-0000-0000-000000000002
USE QuanLyPhongTro;
GO

DECLARE @LandlordDemoId UNIQUEIDENTIFIER = '00000000-0000-0000-0000-000000000002';
DECLARE @TenantDemoId UNIQUEIDENTIFIER = '00000000-0000-0000-0000-000000000001';

-- Tạo nhà trọ demo
DECLARE @NhaTroId UNIQUEIDENTIFIER = NEWID();
INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, IsHoatDong)
VALUES (@NhaTroId, @LandlordDemoId, N'Nhà Trọ Demo - Quận 1', N'123 Nguyễn Văn Linh, Quận 1, TP.HCM', 1);

-- Tạo phòng trọ demo
DECLARE @PhongId1 UNIQUEIDENTIFIER = NEWID();
DECLARE @PhongId2 UNIQUEIDENTIFIER = NEWID();

INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, IsDuyet, IsBiKhoa)
VALUES 
(@PhongId1, @NhaTroId, N'Phòng 101 - Quận 1', 25, 3500000, 1, 0),
(@PhongId2, @NhaTroId, N'Phòng 102 - Quận 1', 30, 4000000, 1, 0);

-- Lấy ID trạng thái
DECLARE @StatusChoXacNhan INT = (SELECT TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'ChoXacNhan');
DECLARE @StatusDaXacNhan INT = (SELECT TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaXacNhan');
DECLARE @StatusDaHuy INT = (SELECT TrangThaiId FROM TrangThaiDatPhong WHERE TenTrangThai = 'DaHuy');

-- Tạo yêu cầu đặt phòng demo
INSERT INTO DatPhong (DatPhongId, PhongId, NguoiThueId, ChuTroId, Loai, BatDau, KetThuc, ThoiGianTao, TrangThaiId, GhiChu)
VALUES 
(NEWID(), @PhongId1, @TenantDemoId, @LandlordDemoId, 'XemPhong', 
 DATEADD(DAY, 1, SYSDATETIMEOFFSET()), NULL, SYSDATETIMEOFFSET(), @StatusChoXacNhan, 
 N'Tôi muốn xem phòng vào chiều mai lúc 2h'),

(NEWID(), @PhongId2, @TenantDemoId, @LandlordDemoId, 'ThuePhong', 
 DATEADD(DAY, 3, SYSDATETIMEOFFSET()), NULL, DATEADD(HOUR, -2, SYSDATETIMEOFFSET()), @StatusDaXacNhan, 
 N'Đã xác nhận thuê phòng từ tháng sau'),

(NEWID(), @PhongId1, @TenantDemoId, @LandlordDemoId, 'XemPhong', 
 DATEADD(DAY, -1, SYSDATETIMEOFFSET()), NULL, DATEADD(DAY, -3, SYSDATETIMEOFFSET()), @StatusDaHuy, 
 N'Lịch hẹn đã bị hủy do bận việc');

PRINT N'✅ Đã tạo dữ liệu demo cho landlord: ' + CAST(@LandlordDemoId AS NVARCHAR(50));
PRINT N'   - 1 nhà trọ: Nhà Trọ Demo - Quận 1';
PRINT N'   - 2 phòng: Phòng 101, Phòng 102';
PRINT N'   - 3 yêu cầu đặt phòng với các trạng thái khác nhau';