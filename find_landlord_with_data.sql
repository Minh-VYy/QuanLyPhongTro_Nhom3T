-- Tìm thông tin đăng nhập của chủ trọ có dữ liệu
USE QuanLyPhongTro;

-- Tìm chủ trọ có nhiều booking nhất
SELECT 
    nd.NguoiDungId,
    nd.Email,
    nd.DienThoai,
    'Password: 27012005' as SuggestedPassword,
    COUNT(dp.DatPhongId) as BookingCount
FROM NguoiDung nd
JOIN DatPhong dp ON nd.NguoiDungId = dp.ChuTroId
WHERE nd.VaiTroId = 2  -- ChuTro
GROUP BY nd.NguoiDungId, nd.Email, nd.DienThoai
ORDER BY BookingCount DESC;

-- Xem chi tiết booking của chủ trọ có nhiều dữ liệu nhất
SELECT TOP 5
    dp.DatPhongId,
    dp.Loai,
    dp.BatDau,
    dp.TrangThaiId,
    dp.GhiChu,
    nd_tenant.Email as TenantEmail
FROM DatPhong dp
LEFT JOIN NguoiDung nd_tenant ON dp.NguoiThueId = nd_tenant.NguoiDungId
WHERE dp.ChuTroId = '00000000-0000-0000-0000-000000000002'
ORDER BY dp.ThoiGianTao DESC;