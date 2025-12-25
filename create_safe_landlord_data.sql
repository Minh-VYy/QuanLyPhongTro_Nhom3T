-- Script an toàn tạo dữ liệu chủ trọ (không dùng cột không tồn tại)
USE QuanLyPhongTro;
GO

-- THAY ĐỔI EMAIL NÀY
DECLARE @ChuTroEmail NVARCHAR(255) = 'admin@example.com';

DECLARE @ChuTroId UNIQUEIDENTIFIER;

-- Tìm ChuTroId
SELECT @ChuTroId = NguoiDungId FROM NguoiDung WHERE Email = @ChuTroEmail;

IF @ChuTroId IS NULL
BEGIN
    PRINT 'KHÔNG TÌM THẤY EMAIL: ' + @ChuTroEmail;
    RETURN;
END

PRINT 'Tạo dữ liệu cho: ' + @ChuTroEmail;

-- Xóa dữ liệu cũ (an toàn)
DELETE FROM Phong WHERE NhaTroId IN (SELECT NhaTroId FROM NhaTro WHERE ChuTroId = @ChuTroId);
DELETE FROM NhaTro WHERE ChuTroId = @ChuTroId;

-- Tạo QuanHuyen nếu chưa có
IF NOT EXISTS (SELECT 1 FROM QuanHuyen WHERE Ten = N'Quận 1')
    INSERT INTO QuanHuyen (Ten) VALUES (N'Quận 1');

DECLARE @QuanHuyenId INT;
SELECT @QuanHuyenId = QuanHuyenId FROM QuanHuyen WHERE Ten = N'Quận 1';

-- Tạo Phuong nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phuong WHERE Ten = N'Phường 1' AND QuanHuyenId = @QuanHuyenId)
    INSERT INTO Phuong (Ten, QuanHuyenId) VALUES (N'Phường 1', @QuanHuyenId);

DECLARE @PhuongId INT;
SELECT @PhuongId = PhuongId FROM Phuong WHERE Ten = N'Phường 1' AND QuanHuyenId = @QuanHuyenId;

-- Tạo NhaTro 1
DECLARE @NhaTro1Id UNIQUEIDENTIFIER = NEWID();
INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, QuanHuyenId, PhuongId, CreatedAt)
VALUES (@NhaTro1Id, @ChuTroId, N'Nhà trọ Sunshine', N'123 Lê Lợi, Q1', @QuanHuyenId, @PhuongId, GETDATE());

-- Tạo NhaTro 2  
DECLARE @NhaTro2Id UNIQUEIDENTIFIER = NEWID();
INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, QuanHuyenId, PhuongId, CreatedAt)
VALUES (@NhaTro2Id, @ChuTroId, N'Nhà trọ Green', N'456 Võ Văn Tần, Q3', @QuanHuyenId, @PhuongId, GETDATE());

-- Tạo Phong (chỉ dùng cột cơ bản)
-- NhaTro 1: 6 phòng
INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TienCoc, SoNguoiToiDa, TrangThai, CreatedAt)
VALUES 
(NEWID(), @NhaTro1Id, N'Phòng 101', 25, 8000000, 16000000, 2, N'Đã thuê', GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 102', 30, 10000000, 20000000, 2, N'Đã thuê', GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 103', 28, 9000000, 18000000, 2, N'Còn trống', GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 201', 35, 12000000, 24000000, 3, N'Còn trống', GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 202', 45, 15000000, 30000000, 4, N'Còn trống', GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 203', 32, 11000000, 22000000, 2, N'Còn trống', GETDATE());

-- NhaTro 2: 6 phòng
INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TienCoc, SoNguoiToiDa, TrangThai, CreatedAt)
VALUES 
(NEWID(), @NhaTro2Id, N'Phòng A01', 15, 3000000, 3000000, 2, N'Đã thuê', GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng A02', 18, 3500000, 3500000, 2, N'Đã thuê', GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng A03', 16, 3200000, 3200000, 2, N'Còn trống', GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng B01', 17, 3300000, 3300000, 2, N'Còn trống', GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng B02', 20, 4000000, 4000000, 3, N'Còn trống', GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng B03', 16, 3200000, 3200000, 2, N'Còn trống', GETDATE());

-- Kiểm tra kết quả
SELECT 
    nt.TieuDe AS NhaTro,
    COUNT(p.PhongId) AS SoPhong
FROM NhaTro nt
LEFT JOIN Phong p ON nt.NhaTroId = p.NhaTroId
WHERE nt.ChuTroId = @ChuTroId
GROUP BY nt.TieuDe;

SELECT 
    'Tổng số phòng' AS ThongKe,
    COUNT(*) AS SoLuong
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @ChuTroId;

PRINT 'HOÀN THÀNH! Đã tạo 2 nhà trọ và 12 phòng.';