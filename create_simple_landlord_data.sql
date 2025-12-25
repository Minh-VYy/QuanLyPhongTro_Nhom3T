-- Script đơn giản tạo dữ liệu test cho chủ trọ
-- Thay đổi @ChuTroEmail theo email đăng nhập của bạn

USE QuanLyPhongTro;
GO

-- THAY ĐỔI EMAIL NÀY THEO TÀI KHOẢN CỦA BẠN
DECLARE @ChuTroEmail NVARCHAR(255) = 'admin@example.com';

DECLARE @ChuTroId UNIQUEIDENTIFIER;

-- Tìm ChuTroId từ email
SELECT @ChuTroId = NguoiDungId 
FROM NguoiDung 
WHERE Email = @ChuTroEmail;

IF @ChuTroId IS NULL
BEGIN
    PRINT 'KHÔNG TÌM THẤY CHỦ TRỌ VỚI EMAIL: ' + @ChuTroEmail;
    PRINT 'VUI LÒNG THAY ĐỔI @ChuTroEmail TRONG SCRIPT';
    RETURN;
END

PRINT 'Tạo dữ liệu cho ChuTroId: ' + CAST(@ChuTroId AS NVARCHAR(50));

-- Xóa dữ liệu cũ
DELETE FROM Phong WHERE NhaTroId IN (SELECT NhaTroId FROM NhaTro WHERE ChuTroId = @ChuTroId);
DELETE FROM NhaTro WHERE ChuTroId = @ChuTroId;

-- Tạo QuanHuyen nếu chưa có
IF NOT EXISTS (SELECT 1 FROM QuanHuyen WHERE Ten = N'Quận 1')
    INSERT INTO QuanHuyen (Ten) VALUES (N'Quận 1');

DECLARE @QuanHuyenId INT;
SELECT @QuanHuyenId = QuanHuyenId FROM QuanHuyen WHERE Ten = N'Quận 1';

-- Tạo Phuong nếu chưa có
IF NOT EXISTS (SELECT 1 FROM Phuong WHERE Ten = N'Phường Bến Nghé')
    INSERT INTO Phuong (Ten, QuanHuyenId) VALUES (N'Phường Bến Nghé', @QuanHuyenId);

DECLARE @PhuongId INT;
SELECT @PhuongId = PhuongId FROM Phuong WHERE Ten = N'Phường Bến Nghé';

-- Tạo NhaTro 1
DECLARE @NhaTro1Id UNIQUEIDENTIFIER = NEWID();
INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, QuanHuyenId, PhuongId, MoTa, IsDuyet, CreatedAt)
VALUES (@NhaTro1Id, @ChuTroId, N'Nhà trọ Sunshine', N'123 Lê Lợi, Q1, TP.HCM', @QuanHuyenId, @PhuongId, N'Nhà trọ cao cấp', 1, GETDATE());

-- Tạo NhaTro 2
DECLARE @NhaTro2Id UNIQUEIDENTIFIER = NEWID();
INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, QuanHuyenId, PhuongId, MoTa, IsDuyet, CreatedAt)
VALUES (@NhaTro2Id, @ChuTroId, N'Nhà trọ Green House', N'456 Võ Văn Tần, Q3, TP.HCM', @QuanHuyenId, @PhuongId, N'Nhà trọ sinh viên', 1, GETDATE());

-- Tạo Phong cho NhaTro 1 (6 phòng)
INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TienCoc, SoNguoiToiDa, TrangThai, IsDuyet, IsBiKhoa, IsDeleted, CreatedAt)
VALUES 
(NEWID(), @NhaTro1Id, N'Phòng 101 - Studio', 25, 8000000, 16000000, 2, N'Đã thuê', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 102 - 1PN', 30, 10000000, 20000000, 2, N'Đã thuê', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 103 - View đẹp', 28, 9000000, 18000000, 2, N'Còn trống', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 201 - Lớn', 35, 12000000, 24000000, 3, N'Còn trống', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 202 - Penthouse', 45, 15000000, 30000000, 4, N'Còn trống', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro1Id, N'Phòng 203 - Mới', 32, 11000000, 22000000, 2, N'Còn trống', 0, 0, 0, GETDATE());

-- Tạo Phong cho NhaTro 2 (6 phòng)
INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TienCoc, SoNguoiToiDa, TrangThai, IsDuyet, IsBiKhoa, IsDeleted, CreatedAt)
VALUES 
(NEWID(), @NhaTro2Id, N'Phòng A01 - SV', 15, 3000000, 3000000, 2, N'Đã thuê', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng A02 - Gác lửng', 18, 3500000, 3500000, 2, N'Đã thuê', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng A03 - Giá rẻ', 16, 3200000, 3200000, 2, N'Còn trống', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng B01 - Tầng 2', 17, 3300000, 3300000, 2, N'Còn trống', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng B02 - Ban công', 20, 4000000, 4000000, 3, N'Còn trống', 1, 0, 0, GETDATE()),
(NEWID(), @NhaTro2Id, N'Phòng B03 - Sửa chữa', 16, 3200000, 3200000, 2, N'Còn trống', 1, 1, 0, GETDATE());

PRINT 'HOÀN THÀNH! Đã tạo:';
PRINT '- 2 NhaTro';
PRINT '- 12 Phong';

-- Kiểm tra kết quả
SELECT 
    'Tổng số phòng' AS ThongKe,
    COUNT(*) AS SoLuong
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0

UNION ALL

SELECT 
    'Phòng hoạt động' AS ThongKe,
    COUNT(*) AS SoLuong
FROM Phong p
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0 AND p.IsDuyet = 1 AND p.IsBiKhoa = 0;

PRINT 'Hãy mở app và kiểm tra trang chủ chủ trọ!';