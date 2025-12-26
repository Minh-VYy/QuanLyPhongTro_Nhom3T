# Script PowerShell để tạo dữ liệu test cho chủ trọ
# Chạy script này để tạo NhaTro và Phong cho chủ trọ

param(
    [string]$Email = "admin@example.com"  # Email của chủ trọ cần tạo dữ liệu
)

Write-Host "=== TẠO DỮ LIỆU TEST CHO CHỦ TRỌ ===" -ForegroundColor Green
Write-Host "Email chủ trọ: $Email" -ForegroundColor Yellow

# Thông tin kết nối database
$ServerName = "192.168.0.117"
$DatabaseName = "QuanLyPhongTro"
$Username = "sa"
$Password = "27012005"

# Đọc và cập nhật script SQL
$SqlScript = Get-Content "create_landlord_test_data.sql" -Raw
$SqlScript = $SqlScript -replace "@ChuTroEmail NVARCHAR\(255\) = '[^']*'", "@ChuTroEmail NVARCHAR(255) = '$Email'"

Write-Host "Đang kết nối đến SQL Server..." -ForegroundColor Yellow

try {
    # Tạo connection string
    $ConnectionString = "Server=$ServerName;Database=$DatabaseName;User Id=$Username;Password=$Password;TrustServerCertificate=True;"
    
    # Tạo connection
    $Connection = New-Object System.Data.SqlClient.SqlConnection($ConnectionString)
    $Connection.Open()
    
    Write-Host "✅ Kết nối thành công!" -ForegroundColor Green
    
    # Tạo command
    $Command = New-Object System.Data.SqlClient.SqlCommand($SqlScript, $Connection)
    $Command.CommandTimeout = 60
    
    Write-Host "Đang thực thi script SQL..." -ForegroundColor Yellow
    
    # Thực thi script
    $Result = $Command.ExecuteNonQuery()
    
    Write-Host "✅ Script thực thi thành công!" -ForegroundColor Green
    Write-Host "Số dòng bị ảnh hưởng: $Result" -ForegroundColor Cyan
    
    # Kiểm tra kết quả
    $CheckQuery = @"
SELECT 
    nd.Email,
    COUNT(DISTINCT nt.NhaTroId) AS SoNhaTro,
    COUNT(p.PhongId) AS TongSoPhong,
    SUM(CASE WHEN p.IsDuyet = 1 AND p.IsBiKhoa = 0 THEN 1 ELSE 0 END) AS PhongHoatDong
FROM NguoiDung nd
LEFT JOIN NhaTro nt ON nd.NguoiDungId = nt.ChuTroId
LEFT JOIN Phong p ON nt.NhaTroId = p.NhaTroId AND p.IsDeleted = 0
WHERE nd.Email = '$Email'
GROUP BY nd.Email
"@
    
    $CheckCommand = New-Object System.Data.SqlClient.SqlCommand($CheckQuery, $Connection)
    $Reader = $CheckCommand.ExecuteReader()
    
    if ($Reader.Read()) {
        Write-Host "`n=== KẾT QUẢ ===" -ForegroundColor Green
        Write-Host "Email: $($Reader['Email'])" -ForegroundColor White
        Write-Host "Số nhà trọ: $($Reader['SoNhaTro'])" -ForegroundColor White
        Write-Host "Tổng số phòng: $($Reader['TongSoPhong'])" -ForegroundColor White
        Write-Host "Phòng hoạt động: $($Reader['PhongHoatDong'])" -ForegroundColor White
    } else {
        Write-Host "❌ Không tìm thấy dữ liệu cho email: $Email" -ForegroundColor Red
    }
    
    $Reader.Close()
    
} catch {
    Write-Host "❌ Lỗi: $($_.Exception.Message)" -ForegroundColor Red
} finally {
    if ($Connection.State -eq 'Open') {
        $Connection.Close()
        Write-Host "Đã đóng kết nối database." -ForegroundColor Yellow
    }
}

Write-Host "`n=== HƯỚNG DẪN ===" -ForegroundColor Cyan
Write-Host "1. Mở app Android và đăng nhập với email: $Email" -ForegroundColor White
Write-Host "2. Chuyển sang giao diện Chủ trọ" -ForegroundColor White
Write-Host "3. Kiểm tra trang chủ - sẽ hiển thị 12 phòng" -ForegroundColor White
Write-Host "4. Nếu cần debug: Long click nút Search để mở Test Activity" -ForegroundColor White

Write-Host "`nScript hoàn thành!" -ForegroundColor Green