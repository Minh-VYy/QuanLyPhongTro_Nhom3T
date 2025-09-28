USE master;
GO

IF DB_ID('TimTroApp') IS NOT NULL
BEGIN
    ALTER DATABASE TimTroApp SET SINGLE_USER WITH ROLLBACK IMMEDIATE;
    DROP DATABASE TimTroApp;
END
GO

CREATE DATABASE TimTroApp;
GO
USE TimTroApp;
GO

---------------------------------------------------
-- Xóa các đối tượng nếu tồn tại
---------------------------------------------------
IF OBJECT_ID(N'dbo.[vw_ChiTietPhong]', 'V') IS NOT NULL DROP VIEW dbo.[vw_ChiTietPhong];
IF OBJECT_ID(N'dbo.[trg_CapNhatTrangThaiPhong]', 'TR') IS NOT NULL DROP TRIGGER dbo.[trg_CapNhatTrangThaiPhong];
IF OBJECT_ID(N'dbo.[sp_TimPhong]', 'P') IS NOT NULL DROP PROCEDURE dbo.[sp_TimPhong];
IF OBJECT_ID(N'dbo.[sp_ThemPhong]', 'P') IS NOT NULL DROP PROCEDURE dbo.[sp_ThemPhong];
IF OBJECT_ID(N'dbo.[sp_DatPhong]', 'P') IS NOT NULL DROP PROCEDURE dbo.[sp_DatPhong];

IF OBJECT_ID(N'dbo.[DatPhong]', 'U') IS NOT NULL DROP TABLE dbo.[DatPhong];
IF OBJECT_ID(N'dbo.[YeuThich]', 'U') IS NOT NULL DROP TABLE dbo.[YeuThich];
IF OBJECT_ID(N'dbo.[DanhGia]', 'U') IS NOT NULL DROP TABLE dbo.[DanhGia];
IF OBJECT_ID(N'dbo.[HinhAnhPhong]', 'U') IS NOT NULL DROP TABLE dbo.[HinhAnhPhong];
IF OBJECT_ID(N'dbo.[PhongTienIch]', 'U') IS NOT NULL DROP TABLE dbo.[PhongTienIch];
IF OBJECT_ID(N'dbo.[TienIch]', 'U') IS NOT NULL DROP TABLE dbo.[TienIch];
IF OBJECT_ID(N'dbo.[PhongTro]', 'U') IS NOT NULL DROP TABLE dbo.[PhongTro];
IF OBJECT_ID(N'dbo.[DiaChi]', 'U') IS NOT NULL DROP TABLE dbo.[DiaChi];
IF OBJECT_ID(N'dbo.[NguoiDung]', 'U') IS NOT NULL DROP TABLE dbo.[NguoiDung];

---------------------------------------------------
-- Bảng Người dùng
---------------------------------------------------
CREATE TABLE [NguoiDung] (
    [MaNguoiDung] INT IDENTITY PRIMARY KEY,
    [HoTen] NVARCHAR(100) NOT NULL,
    [Email] NVARCHAR(100) UNIQUE NOT NULL,
    [SoDienThoai] NVARCHAR(20),
    [MatKhauHash] NVARCHAR(255) NOT NULL,
    [LoaiNguoiDung] NVARCHAR(20) CHECK ([LoaiNguoiDung] IN (N'Chủ trọ',N'Người thuê')),
    [NgayTao] DATETIME DEFAULT GETDATE()
);

---------------------------------------------------
-- Bảng Địa chỉ
---------------------------------------------------
CREATE TABLE [DiaChi] (
    [MaDiaChi] INT IDENTITY PRIMARY KEY,
    [Tinh] NVARCHAR(100),
    [QuanHuyen] NVARCHAR(100),
    [PhuongXa] NVARCHAR(100),
    [Duong] NVARCHAR(200),
    [ViDo] DECIMAL(9,6),
    [KinhDo] DECIMAL(9,6)
);

---------------------------------------------------
-- Bảng Phòng trọ
---------------------------------------------------
CREATE TABLE [PhongTro] (
    [MaPhong] INT IDENTITY PRIMARY KEY,
    [MaChuTro] INT NOT NULL FOREIGN KEY REFERENCES [NguoiDung]([MaNguoiDung]),
    [MaDiaChi] INT NOT NULL FOREIGN KEY REFERENCES [DiaChi]([MaDiaChi]),
    [TieuDe] NVARCHAR(200) NOT NULL,
    [MoTa] NVARCHAR(MAX),
    [Gia] DECIMAL(18,2) NOT NULL CHECK ([Gia] > 0),
    [DienTich] DECIMAL(10,2) CHECK ([DienTich] > 0),
    [SoNguoiToiDa] INT CHECK ([SoNguoiToiDa] > 0),
    [TrangThai] NVARCHAR(20) DEFAULT N'Còn trống' CHECK ([TrangThai] IN (N'Còn trống',N'Đã thuê')),
    [NgayTao] DATETIME DEFAULT GETDATE()
);

---------------------------------------------------
-- Bảng Tiện ích
---------------------------------------------------
CREATE TABLE [TienIch] (
    [MaTienIch] INT IDENTITY PRIMARY KEY,
    [TenTienIch] NVARCHAR(100) UNIQUE NOT NULL
);

-- Liên kết Phòng - Tiện ích
CREATE TABLE [PhongTienIch] (
    [MaPhong] INT FOREIGN KEY REFERENCES [PhongTro]([MaPhong]),
    [MaTienIch] INT FOREIGN KEY REFERENCES [TienIch]([MaTienIch]),
    PRIMARY KEY ([MaPhong], [MaTienIch])
);

---------------------------------------------------
-- Bảng Hình ảnh phòng
---------------------------------------------------
CREATE TABLE [HinhAnhPhong] (
    [MaHinh] INT IDENTITY PRIMARY KEY,
    [MaPhong] INT FOREIGN KEY REFERENCES [PhongTro]([MaPhong]),
    [DuongDanAnh] NVARCHAR(500) NOT NULL
);

---------------------------------------------------
-- Bảng Đánh giá
---------------------------------------------------
CREATE TABLE [DanhGia] (
    [MaDanhGia] INT IDENTITY PRIMARY KEY,
    [MaPhong] INT FOREIGN KEY REFERENCES [PhongTro]([MaPhong]),
    [MaNguoiDung] INT FOREIGN KEY REFERENCES [NguoiDung]([MaNguoiDung]),
    [SoSao] INT CHECK ([SoSao] BETWEEN 1 AND 5),
    [BinhLuan] NVARCHAR(MAX),
    [NgayTao] DATETIME DEFAULT GETDATE()
);

---------------------------------------------------
-- Bảng Yêu thích
---------------------------------------------------
CREATE TABLE [YeuThich] (
    [MaNguoiDung] INT FOREIGN KEY REFERENCES [NguoiDung]([MaNguoiDung]),
    [MaPhong] INT FOREIGN KEY REFERENCES [PhongTro]([MaPhong]),
    [NgayTao] DATETIME DEFAULT GETDATE(),
    PRIMARY KEY ([MaNguoiDung], [MaPhong])
);

---------------------------------------------------
-- Bảng Đặt phòng
---------------------------------------------------
CREATE TABLE [DatPhong] (
    [MaDatPhong] INT IDENTITY PRIMARY KEY,
    [MaPhong] INT FOREIGN KEY REFERENCES [PhongTro]([MaPhong]),
    [MaNguoiDung] INT FOREIGN KEY REFERENCES [NguoiDung]([MaNguoiDung]),
    [NgayDat] DATETIME DEFAULT GETDATE(),
    [TrangThai] NVARCHAR(20) DEFAULT N'Chờ duyệt' CHECK ([TrangThai] IN (N'Chờ duyệt',N'Xác nhận',N'Hủy'))
);

---------------------------------------------------
-- Indexes
---------------------------------------------------
CREATE INDEX IX_PhongTro_Gia ON [PhongTro]([Gia]);
CREATE INDEX IX_PhongTro_TrangThai ON [PhongTro]([TrangThai]);
CREATE INDEX IX_DiaChi_QuanHuyen ON [DiaChi]([QuanHuyen]);
GO

---------------------------------------------------
-- View
---------------------------------------------------
CREATE VIEW [vw_ChiTietPhong] AS
SELECT p.[MaPhong], p.[TieuDe], p.[Gia], p.[DienTich], p.[TrangThai],
       d.[Tinh], d.[QuanHuyen], d.[PhuongXa], d.[Duong],
       n.[HoTen] AS [ChuTro], n.[SoDienThoai] AS [LienHe]
FROM [PhongTro] p
JOIN [DiaChi] d ON p.[MaDiaChi] = d.[MaDiaChi]
JOIN [NguoiDung] n ON p.[MaChuTro] = n.[MaNguoiDung];
GO

---------------------------------------------------
-- Trigger
---------------------------------------------------
CREATE TRIGGER [trg_CapNhatTrangThaiPhong]
ON [DatPhong]
AFTER UPDATE
AS
BEGIN
    IF EXISTS (SELECT 1 FROM inserted WHERE [TrangThai] = N'Xác nhận')
    BEGIN
        UPDATE [PhongTro]
        SET [TrangThai] = N'Đã thuê'
        WHERE [MaPhong] IN (SELECT [MaPhong] FROM inserted WHERE [TrangThai] = N'Xác nhận');
    END
END;
GO

---------------------------------------------------
-- Stored Procedure: Tìm phòng
---------------------------------------------------
CREATE PROCEDURE [sp_TimPhong]
    @QuanHuyen NVARCHAR(100) = NULL,
    @GiaMin DECIMAL(18,2) = NULL,
    @GiaMax DECIMAL(18,2) = NULL,
    @TienIch NVARCHAR(100) = NULL
AS
BEGIN
    SELECT DISTINCT p.[MaPhong], p.[TieuDe], p.[Gia], p.[DienTich], p.[TrangThai], d.[QuanHuyen]
    FROM [PhongTro] p
    JOIN [DiaChi] d ON p.[MaDiaChi] = d.[MaDiaChi]
    LEFT JOIN [PhongTienIch] pt ON p.[MaPhong] = pt.[MaPhong]
    LEFT JOIN [TienIch] t ON pt.[MaTienIch] = t.[MaTienIch]
    WHERE (@QuanHuyen IS NULL OR d.[QuanHuyen] = @QuanHuyen)
      AND (@GiaMin IS NULL OR p.[Gia] >= @GiaMin)
      AND (@GiaMax IS NULL OR p.[Gia] <= @GiaMax)
      AND (@TienIch IS NULL OR t.[TenTienIch] = @TienIch)
      AND p.[TrangThai] = N'Còn trống';
END;
GO

---------------------------------------------------
-- Stored Procedure: Thêm phòng mới
---------------------------------------------------
CREATE PROCEDURE [sp_ThemPhong]
    @MaChuTro INT,
    @MaDiaChi INT,
    @TieuDe NVARCHAR(200),
    @MoTa NVARCHAR(MAX),
    @Gia DECIMAL(18,2),
    @DienTich DECIMAL(10,2),
    @SoNguoiToiDa INT
AS
BEGIN
    INSERT INTO [PhongTro] ([MaChuTro], [MaDiaChi], [TieuDe], [MoTa], [Gia], [DienTich], [SoNguoiToiDa])
    VALUES (@MaChuTro, @MaDiaChi, @TieuDe, @MoTa, @Gia, @DienTich, @SoNguoiToiDa);
END;
GO

---------------------------------------------------
-- Stored Procedure: Đặt phòng
---------------------------------------------------
CREATE PROCEDURE [sp_DatPhong]
    @MaPhong INT,
    @MaNguoiDung INT
AS
BEGIN
    INSERT INTO [DatPhong] ([MaPhong], [MaNguoiDung], [TrangThai])
    VALUES (@MaPhong, @MaNguoiDung, N'Chờ duyệt');
END;
GO