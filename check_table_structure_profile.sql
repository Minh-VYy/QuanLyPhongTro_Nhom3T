-- Kiểm tra cấu trúc bảng HoSoNguoiDung và NguoiDung
USE QuanLyPhongTro;

-- 1. Kiểm tra cấu trúc bảng NguoiDung
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'NguoiDung'
ORDER BY ORDINAL_POSITION;

-- 2. Kiểm tra cấu trúc bảng HoSoNguoiDung
SELECT 
    COLUMN_NAME,
    DATA_TYPE,
    IS_NULLABLE,
    COLUMN_DEFAULT
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'HoSoNguoiDung'
ORDER BY ORDINAL_POSITION;

-- 3. Kiểm tra xem bảng HoSoNguoiDung có tồn tại không
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_NAME LIKE '%HoSo%' OR TABLE_NAME LIKE '%Profile%';

-- 4. Kiểm tra tất cả bảng có liên quan đến user
SELECT TABLE_NAME 
FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_NAME LIKE '%Nguoi%' OR TABLE_NAME LIKE '%User%';

-- 5. Kiểm tra dữ liệu user chutro@test.com
SELECT * FROM NguoiDung WHERE Email = 'chutro@test.com';