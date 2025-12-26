-- Kiểm tra cấu trúc bảng Phong
USE QuanLyPhongTro;

SELECT 'Phong Table Structure' as Info;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'Phong'
ORDER BY ORDINAL_POSITION;

-- Xem một số dữ liệu mẫu
SELECT 'Sample Phong Data' as Info;
SELECT TOP 5 * FROM Phong;