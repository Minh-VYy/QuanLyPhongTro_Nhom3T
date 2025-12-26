-- Test chức năng chỉnh sửa tin đăng đơn giản
USE QuanLyPhongTro;
GO

PRINT '=== TEST CHỈNH SỬA TIN ĐĂNG ===';
PRINT '';

-- Lấy thông tin phòng để test
DECLARE @ChuTroId UNIQUEIDENTIFIER = '44444444-4444-4444-4444-444444444444';

PRINT '1. Danh sách 5 phòng đầu tiên để test:';
SELECT TOP 5
    p.PhongId,
    p.TieuDe,
    p.GiaTien,
    p.MoTa,
    p.TrangThai,
    p.IsDeleted
FROM Phong p 
INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId 
WHERE nt.ChuTroId = @ChuTroId AND p.IsDeleted = 0
ORDER BY p.CreatedAt DESC;

PRINT '';
PRINT '2. Hướng dẫn test:';
PRINT '- Mở app với tài khoản chủ trọ';
PRINT '- Vào FAB -> "Xóa tin" -> Danh sách tin đăng';
PRINT '- Nhấn nút Edit (xanh) trên tin đăng';
PRINT '- Kiểm tra: Có mở EditTin với dữ liệu đã điền không?';
PRINT '- Sửa tiêu đề và giá';
PRINT '- Nhấn "Cập nhật tin đăng"';
PRINT '- Kiểm tra: Có lưu thành công không?';

PRINT '';
PRINT '=== SẴN SÀNG TEST ===';