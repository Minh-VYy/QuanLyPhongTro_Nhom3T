-- Tạo tài khoản người thuê test
USE QuanLyPhongTro;
GO

-- Lấy VaiTroId cho NguoiThue
DECLARE @VaiTroNguoiThueId INT = (SELECT VaiTroId FROM VaiTro WHERE TenVaiTro = 'NguoiThue');

-- Tạo người thuê test
DECLARE @TestTenantId UNIQUEIDENTIFIER = NEWID();

-- Insert vào bảng NguoiDung
INSERT INTO NguoiDung (NguoiDungId, Email, DienThoai, PasswordHash, VaiTroId, IsKhoa, IsEmailXacThuc, CreatedAt)
VALUES (@TestTenantId, 'tenant@test.com', '0905123456', 'password123', @VaiTroNguoiThueId, 0, 1, SYSDATETIMEOFFSET());

-- Insert vào bảng HoSoNguoiDung
INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh, LoaiGiayTo, GhiChu, CreatedAt)
VALUES (@TestTenantId, N'Nguyễn Văn Test', '1995-01-01', 'CCCD: 001095000123', N'Tài khoản test cho người thuê', SYSDATETIMEOFFSET());

-- Insert vào bảng NguoiDungVaiTro
INSERT INTO NguoiDungVaiTro (NguoiDungId, VaiTroId, NgayBatDau)
VALUES (@TestTenantId, @VaiTroNguoiThueId, SYSDATETIMEOFFSET());

-- Tạo thêm một vài người thuê khác
DECLARE @TestTenant2Id UNIQUEIDENTIFIER = NEWID();
INSERT INTO NguoiDung (NguoiDungId, Email, DienThoai, PasswordHash, VaiTroId, IsKhoa, IsEmailXacThuc, CreatedAt)
VALUES (@TestTenant2Id, 'nguoithue@gmail.com', '0905987654', '123456', @VaiTroNguoiThueId, 0, 1, SYSDATETIMEOFFSET());

INSERT INTO HoSoNguoiDung (NguoiDungId, HoTen, NgaySinh, LoaiGiayTo, GhiChu, CreatedAt)
VALUES (@TestTenant2Id, N'Trần Thị Minh', '1998-05-15', 'CCCD: 001098000456', N'Sinh viên đại học', SYSDATETIMEOFFSET());

INSERT INTO NguoiDungVaiTro (NguoiDungId, VaiTroId, NgayBatDau)
VALUES (@TestTenant2Id, @VaiTroNguoiThueId, SYSDATETIMEOFFSET());

PRINT N'✅ Đã tạo tài khoản test cho người thuê:';
PRINT N'Email: tenant@test.com | Password: password123';
PRINT N'Email: nguoithue@gmail.com | Password: 123456';

-- Kiểm tra dữ liệu vừa tạo
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