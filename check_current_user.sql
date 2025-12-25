-- Kiểm tra user hiện tại và dữ liệu của họ
USE QuanLyPhongTro;

-- 1. Tìm chủ trọ trong hệ thống
SELECT 
    nd.NguoiDungId,
    nd.Email,
    nd.DienThoai,
    vt.TenVaiTro,
    nd.IsKhoa
FROM NguoiDung nd
JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId
WHERE nd.VaiTroId = 2  -- ChuTro
ORDER BY nd.CreatedAt DESC;

-- 2. Kiểm tra DatPhong của chủ trọ này
SELECT 
    dp.DatPhongId,
    dp.PhongId,
    dp.NguoiThueId,
    dp.ChuTroId,
    dp.Loai,
    dp.BatDau,
    dp.TrangThaiId,
    dp.GhiChu
FROM DatPhong dp
WHERE dp.ChuTroId = '5E55876B-B689-4FDB-A037-EB9630836E7B'  -- ID của chủ trọ
ORDER BY dp.ThoiGianTao DESC;

-- 3. Kiểm tra BienLai liên quan đến chủ trọ này
SELECT 
    bl.BienLaiId,
    bl.DatPhongId,
    bl.SoTien,
    bl.DaXacNhan,
    bl.ThoiGianTai
FROM BienLai bl
JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
WHERE dp.ChuTroId = '5E55876B-B689-4FDB-A037-EB9630836E7B'
ORDER BY bl.ThoiGianTai DESC;

-- 4. Kiểm tra tất cả các ChuTroId trong DatPhong
SELECT DISTINCT 
    ChuTroId,
    COUNT(*) as BookingCount
FROM DatPhong 
WHERE ChuTroId IS NOT NULL
GROUP BY ChuTroId;