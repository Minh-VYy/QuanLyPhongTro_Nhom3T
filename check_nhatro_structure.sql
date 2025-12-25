-- Kiểm tra cấu trúc bảng NhaTro
USE QuanLyPhongTro;

SELECT 'NhaTro Table Structure' as Info;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'NhaTro'
ORDER BY ORDINAL_POSITION;

-- Xem dữ liệu mẫu
SELECT 'Sample NhaTro Data' as Info;
SELECT TOP 5 * FROM NhaTro;