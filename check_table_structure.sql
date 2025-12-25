-- Kiểm tra cấu trúc bảng Phong và NhaTro
USE QuanLyPhongTro;
GO

PRINT 'Cấu trúc bảng Phong:';
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'Phong'
ORDER BY ORDINAL_POSITION;

PRINT '';
PRINT 'Cấu trúc bảng NhaTro:';
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'NhaTro'
ORDER BY ORDINAL_POSITION;

PRINT '';
PRINT 'Dữ liệu mẫu từ bảng Phong (5 dòng đầu):';
SELECT TOP 5 * FROM Phong;

PRINT '';
PRINT 'Dữ liệu mẫu từ bảng NhaTro (5 dòng đầu):';
SELECT TOP 5 * FROM NhaTro;