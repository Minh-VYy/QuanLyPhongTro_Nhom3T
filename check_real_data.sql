-- Kiểm tra dữ liệu thật trong database
USE QuanLyPhongTro;

-- 1. Kiểm tra bảng NguoiDung (Users)
SELECT 'NguoiDung' as TableName, COUNT(*) as RecordCount FROM NguoiDung;

-- 2. Kiểm tra bảng DatPhong (Booking Requests)  
SELECT 'DatPhong' as TableName, COUNT(*) as RecordCount FROM DatPhong;

-- 3. Kiểm tra bảng BienLai (Payment Receipts)
SELECT 'BienLai' as TableName, COUNT(*) as RecordCount FROM BienLai;

-- 4. Kiểm tra bảng Phong (Rooms)
SELECT 'Phong' as TableName, COUNT(*) as RecordCount FROM Phong;

-- 5. Xem một số dữ liệu mẫu từ DatPhong
SELECT TOP 5 
    DatPhongId,
    PhongId, 
    NguoiThueId,
    ChuTroId,
    Loai,
    BatDau,
    TrangThaiId,
    GhiChu
FROM DatPhong
ORDER BY BatDau DESC;

-- 6. Xem một số dữ liệu mẫu từ BienLai
SELECT TOP 5
    BienLaiId,
    DatPhongId,
    SoTien,
    NguoiTai,
    DaXacNhan,
    ThoiGianTai
FROM BienLai
ORDER BY ThoiGianTai DESC;

-- 7. Kiểm tra có chủ trọ nào không
SELECT COUNT(*) as LandlordCount 
FROM NguoiDung 
WHERE VaiTro = 'ChuTro' OR VaiTro = 'Landlord';

-- 8. Kiểm tra có người thuê nào không  
SELECT COUNT(*) as TenantCount
FROM NguoiDung 
WHERE VaiTro = 'NguoiThue' OR VaiTro = 'Tenant';