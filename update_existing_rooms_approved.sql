-- Script để update tất cả phòng hiện tại thành đã duyệt (IsDuyet = 1)
-- Chạy script này để fix dữ liệu cũ

-- Kiểm tra số lượng phòng chưa duyệt
SELECT COUNT(*) as 'So phong chua duyet' FROM Phong WHERE IsDuyet = 0 AND IsDeleted = 0;

-- Update tất cả phòng chưa duyệt thành đã duyệt
UPDATE Phong 
SET IsDuyet = 1, UpdatedAt = GETDATE() 
WHERE IsDuyet = 0 AND IsDeleted = 0;

-- Kiểm tra kết quả sau khi update
SELECT COUNT(*) as 'So phong da duyet' FROM Phong WHERE IsDuyet = 1 AND IsDeleted = 0;
SELECT COUNT(*) as 'So phong chua duyet' FROM Phong WHERE IsDuyet = 0 AND IsDeleted = 0;

-- Xem một vài phòng mẫu
SELECT TOP 5 PhongId, TieuDe, IsDuyet, IsBiKhoa, TrangThai, CreatedAt 
FROM Phong 
WHERE IsDeleted = 0 
ORDER BY CreatedAt DESC;