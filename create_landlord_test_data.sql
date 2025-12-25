-- Script tạo dữ liệu test cho chủ trọ
-- Chạy script này để tạo NhaTro và Phong cho chủ trọ hiện tại

USE QuanLyPhongTro;
GO

-- Lấy thông tin chủ trọ hiện tại (thay đổi email này theo tài khoản đăng nhập)
DECLARE @ChuTroEmail NVARCHAR(255) = 'admin@example.com'; -- Thay đổi email này
DECLARE @ChuTroId UNIQUEIDENTIFIER;

-- Tìm ChuTroId từ email
SELECT @ChuTroId = NguoiDungId 
FROM NguoiDung 
WHERE Email = @ChuTroEmail;

IF @ChuTroId IS NULL
BEGIN
    PRINT 'Không tìm thấy chủ trọ với email: ' + @ChuTroEmail;
    PRINT 'Vui lòng thay đổi @ChuTroEmail trong script này';
    RETURN;
END

PRINT 'Tạo dữ liệu test cho ChuTroId: ' + CAST(@ChuTroId AS NVARCHAR(50));

-- Tạo QuanHuyen và Phuong nếu chưa có
IF NOT EXISTS (SELECT 1 FROM QuanHuyen WHERE Ten = N'Quận 1')
BEGIN
    INSERT INTO QuanHuyen (Ten) VALUES (N'Quận 1');
END

IF NOT EXISTS (SELECT 1 FROM QuanHuyen WHERE Ten = N'Quận 3')
BEGIN
    INSERT INTO QuanHuyen (Ten) VALUES (N'Quận 3');
END

DECLARE @QuanHuyen1Id INT, @QuanHuyen3Id INT;
SELECT @QuanHuyen1Id = QuanHuyenId FROM QuanHuyen WHERE Ten = N'Quận 1';
SELECT @QuanHuyen3Id = QuanHuyenId FROM QuanHuyen WHERE Ten = N'Quận 3';

-- Tạo Phuong
IF NOT EXISTS (SELECT 1 FROM Phuong WHERE Ten = N'Phường Bến Nghé')
BEGIN
    INSERT INTO Phuong (Ten, QuanHuyenId) VALUES (N'Phường Bến Nghé', @QuanHuyen1Id);
END

IF NOT EXISTS (SELECT 1 FROM Phuong WHERE Ten = N'Phường Võ Thị Sáu')
BEGIN
    INSERT INTO Phuong (Ten, QuanHuyenId) VALUES (N'Phường Võ Thị Sáu', @QuanHuyen3Id);
END

DECLARE @Phuong1Id INT, @Phuong2Id INT;
SELECT @Phuong1Id = PhuongId FROM Phuong WHERE Ten = N'Phường Bến Nghé';
SELECT @Phuong2Id = PhuongId FROM Phuong WHERE Ten = N'Phường Võ Thị Sáu';

-- Xóa dữ liệu cũ của chủ trọ này (nếu có)
DELETE FROM Phong WHERE NhaTroId IN (SELECT NhaTroId FROM NhaTro WHERE ChuTroId = @ChuTroId);
DELETE FROM NhaTro WHERE ChuTroId = @ChuTroId;

-- Tạo NhaTro 1
DECLARE @NhaTro1Id UNIQUEIDENTIFIER = NEWID();
INSERT INTO NhaTro (
    NhaTroId, ChuTroId, TieuDe, DiaChi, 
    QuanHuyenId, PhuongId, MoTa, 
    IsDuyet, CreatedAt
) VALUES (
    @NhaTro1Id, @ChuTroId, 
    N'Nhà trọ Sunshine - Quận 1', 
    N'123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM',
    @QuanHuyen1Id, @Phuong1Id,
    N'Nhà trọ cao cấp, gần trung tâm thành phố, đầy đủ tiện nghi',
    1, SYSDATETIMEOFFSET()
);

-- Tạo NhaTro 2
DECLARE @NhaTro2Id UNIQUEIDENTIFIER = NEWID();
INSERT INTO NhaTro (
    NhaTroId, ChuTroId, TieuDe, DiaChi, 
    QuanHuyenId, PhuongId, MoTa, 
    IsDuyet, CreatedAt
) VALUES (
    @NhaTro2Id, @ChuTroId, 
    N'Nhà trọ Green House - Quận 3', 
    N'456 Đường Võ Văn Tần, Phường Võ Thị Sáu, Quận 3, TP.HCM',
    @QuanHuyen3Id, @Phuong2Id,
    N'Nhà trọ giá rẻ, phù hợp sinh viên, gần trường đại học',
    1, SYSDATETIMEOFFSET()
);

-- Tạo Phong cho NhaTro 1 (Nhà trọ cao cấp)
INSERT INTO Phong (
    PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TienCoc,
    SoNguoiToiDa, TrangThai, IsDuyet, IsBiKhoa, IsDeleted, CreatedAt
) VALUES 
-- Phòng đã thuê
(NEWID(), @NhaTro1Id, N'Phòng 101 - Studio cao cấp', 25.0, 8000000, 16000000, 2, N'Đã thuê', 1, 0, 0, SYSDATETIMEOFFSET()),
(NEWID(), @NhaTro1Id, N'Phòng 102 - 1 phòng ngủ', 30.0, 10000000, 20000000, 2, N'Đã thuê', 1, 0, 0, SYSDATETIMEOFFSET()),

-- Phòng còn trống
(NEWID(), @NhaTro1Id, N'Phòng 103 - Studio view đẹp', 28.0, 9000000, 18000000, 2, N'Còn trống', 1, 0, 0, SYSDATETIMEOFFSET()),
(NEWID(), @NhaTro1Id, N'Phòng 201 - 1 phòng ngủ lớn', 35.0, 12000000, 24000000, 3, N'Còn trống', 1, 0, 0, SYSDATETIMEOFFSET()),
(NEWID(), @NhaTro1Id, N'Phòng 202 - Penthouse', 45.0, 15000000, 30000000, 4, N'Còn trống', 1, 0, 0, SYSDATETIMEOFFSET()),

-- Phòng chờ duyệt
(NEWID(), @NhaTro1Id, N'Phòng 203 - Mới xây', 32.0, 11000000, 22000000, 2, N'Còn trống', 0, 0, 0, SYSDATETIMEOFFSET());

-- Tạo Phong cho NhaTro 2 (Nhà trọ sinh viên)
INSERT INTO Phong (
    PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TienCoc,
    SoNguoiToiDa, TrangThai, IsDuyet, IsBiKhoa, IsDeleted, CreatedAt
) VALUES 
-- Phòng đã thuê
(NEWID(), @NhaTro2Id, N'Phòng A01 - Sinh viên', 15.0, 3000000, 3000000, 2, N'Đã thuê', 1, 0, 0, SYSDATETIMEOFFSET()),
(NEWID(), @NhaTro2Id, N'Phòng A02 - Có gác lửng', 18.0, 3500000, 3500000, 2, N'Đã thuê', 1, 0, 0, SYSDATETIMEOFFSET()),

-- Phòng còn trống
(NEWID(), @NhaTro2Id, N'Phòng A03 - Giá rẻ', 16.0, 3200000, 3200000, 2, N'Còn trống', 1, 0, 0, SYSDATETIMEOFFSET()),
(NEWID(), @NhaTro2Id, N'Phòng B01 - Tầng 2', 17.0, 3300000, 3300000, 2, N'Còn trống', 1, 0, 0, SYSDATETIMEOFFSET()),
(NEWID(), @NhaTro2Id, N'Phòng B02 - Ban công', 20.0, 4000000, 4000000, 3, N'Còn trống', 1, 0, 0, SYSDATETIMEOFFSET()),

-- Phòng bị khóa
(NEWID(), @NhaTro2Id, N'Phòng B03 - Đang sửa chữa', 16.0, 3200000, 3200000, 2, N'Còn trống', 1, 1, 0, SYSDATETIMEOFFSET());

PRINT 'Đã tạo thành công:';
PRINT '- 2 NhaTro';
PRINT '- 12 Phong (6 phòng mỗi nhà trọ)';
PRINT '- Trạng thái: 4 đã thuê, 6 còn trống, 1 chờ duyệt, 1 bị khóa';

-- Kiểm tra kết quả
SELECT 
    nt.TieuDe AS NhaTro,
    COUNT(p.PhongId) AS SoPhong,
    SUM(CASE WHEN p.TrangThai = N'Đã thuê' THEN 1 ELSE 0 END) AS DaThue,
    SUM(CASE WHEN p.TrangThai = N'Còn trống' AND p.IsDuyet = 1 AND p.IsBiKhoa = 0 THEN 1 ELSE 0 END) AS ConTrong,
    SUM(CASE WHEN p.IsDuyet = 0 THEN 1 ELSE 0 END) AS ChoDuyet,
    SUM(CASE WHEN p.IsBiKhoa = 1 THEN 1 ELSE 0 END) AS BiKhoa
FROM NhaTro nt
LEFT JOIN Phong p ON nt.NhaTroId = p.NhaTroId AND p.IsDeleted = 0
WHERE nt.ChuTroId = @ChuTroId
GROUP BY nt.TieuDe;

PRINT 'Script hoàn thành!';