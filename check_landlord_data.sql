-- Kiểm tra dữ liệu của chủ trọ cụ thể
USE QuanLyPhongTro;

-- 1. Xem tất cả NguoiDung để tìm chủ trọ
SELECT TOP 10
    NguoiDungId,
    Email,
    DienThoai,
    VaiTroId,
    IsKhoa
FROM NguoiDung
ORDER BY CreatedAt DESC;

-- 2. Kiểm tra VaiTro table
SELECT * FROM VaiTro;

-- 3. Xem DatPhong với thông tin chi tiết
SELECT TOP 10
    dp.DatPhongId,
    dp.PhongId,
    dp.NguoiThueId,
    dp.ChuTroId,
    dp.Loai,
    dp.BatDau,
    dp.TrangThaiId,
    dp.GhiChu,
    tt.TenTrangThai
FROM DatPhong dp
LEFT JOIN TrangThai tt ON dp.TrangThaiId = tt.TrangThaiId
ORDER BY dp.ThoiGianTao DESC;

-- 4. Kiểm tra có chủ trọ nào có DatPhong không
SELECT DISTINCT ChuTroId, COUNT(*) as BookingCount
FROM DatPhong 
WHERE ChuTroId IS NOT NULL
GROUP BY ChuTroId
ORDER BY BookingCount DESC;