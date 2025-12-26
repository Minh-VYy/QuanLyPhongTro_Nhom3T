-- Kiểm tra nhanh dữ liệu chủ trọ
USE QuanLyPhongTro;

-- Thay email này
DECLARE @Email NVARCHAR(255) = 'admin@example.com';

SELECT 
    nd.Email,
    COUNT(DISTINCT nt.NhaTroId) AS SoNhaTro,
    COUNT(p.PhongId) AS TongPhong
FROM NguoiDung nd
LEFT JOIN NhaTro nt ON nd.NguoiDungId = nt.ChuTroId  
LEFT JOIN Phong p ON nt.NhaTroId = p.NhaTroId
WHERE nd.Email = @Email
GROUP BY nd.Email;

-- Chi tiết phòng
SELECT 
    nt.TieuDe AS NhaTro,
    p.TieuDe AS Phong,
    p.GiaTien,
    p.TrangThai
FROM NguoiDung nd
JOIN NhaTro nt ON nd.NguoiDungId = nt.ChuTroId
JOIN Phong p ON nt.NhaTroId = p.NhaTroId  
WHERE nd.Email = @Email
ORDER BY nt.TieuDe, p.TieuDe;