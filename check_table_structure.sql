-- Kiểm tra cấu trúc các bảng chính
USE QuanLyPhongTro;

-- 1. Kiểm tra cấu trúc bảng NguoiDung
SELECT 'NguoiDung Structure' as Info;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'NguoiDung'
ORDER BY ORDINAL_POSITION;

-- 2. Kiểm tra cấu trúc bảng DatPhong  
SELECT 'DatPhong Structure' as Info;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'DatPhong'
ORDER BY ORDINAL_POSITION;

-- 3. Kiểm tra cấu trúc bảng BienLai
SELECT 'BienLai Structure' as Info;
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'BienLai'
ORDER BY ORDINAL_POSITION;

-- 4. Đếm số lượng records
SELECT 'Record Counts' as Info;
SELECT 
    (SELECT COUNT(*) FROM NguoiDung) as NguoiDung_Count,
    (SELECT COUNT(*) FROM DatPhong) as DatPhong_Count,
    (SELECT COUNT(*) FROM BienLai) as BienLai_Count,
    (SELECT COUNT(*) FROM Phong) as Phong_Count;