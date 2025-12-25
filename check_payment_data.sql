-- Kiểm tra dữ liệu thanh toán trong database
USE QuanLyPhongTro;

-- 1. Kiểm tra tổng số BienLai
SELECT 'Total BienLai records' as Info, COUNT(*) as Count FROM BienLai;

-- 2. Kiểm tra dữ liệu BienLai chi tiết
SELECT TOP 10 
    bl.BienLaiId, 
    bl.DatPhongId, 
    bl.SoTien, 
    bl.DaXacNhan, 
    bl.ThoiGianTai,
    dp.ChuTroId,
    dp.NguoiThueId,
    dp.Loai
FROM BienLai bl
LEFT JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId
ORDER BY bl.ThoiGianTai DESC;

-- 3. Kiểm tra ChuTroId có trong hệ thống
SELECT 'ChuTro accounts' as Info, COUNT(*) as Count 
FROM NguoiDung nd 
INNER JOIN VaiTro vt ON nd.VaiTroId = vt.VaiTroId 
WHERE vt.TenVaiTro = 'ChuTro';

-- 4. Kiểm tra DatPhong có ChuTroId nào
SELECT DISTINCT dp.ChuTroId, COUNT(*) as DatPhongCount
FROM DatPhong dp
GROUP BY dp.ChuTroId;

-- 5. Kiểm tra query chính xác như trong app
SELECT 
    bl.BienLaiId, bl.DatPhongId, bl.SoTien, bl.NguoiTai, bl.TapTinId, bl.DaXacNhan, bl.ThoiGianTai,
    dp.NguoiThueId, dp.ChuTroId, dp.Loai,
    hs.HoTen as TenNguoiThue,
    p.TieuDe as TenPhong
FROM BienLai bl 
INNER JOIN DatPhong dp ON bl.DatPhongId = dp.DatPhongId 
INNER JOIN NguoiDung nd ON dp.NguoiThueId = nd.NguoiDungId 
INNER JOIN HoSoNguoiDung hs ON nd.NguoiDungId = hs.NguoiDungId 
INNER JOIN Phong p ON dp.PhongId = p.PhongId 
ORDER BY bl.ThoiGianTai DESC;