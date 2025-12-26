# Script đơn giản để tạo dữ liệu chủ trọ
param(
    [string]$Email = "admin@example.com"
)

Write-Host "=== TẠO DỮ LIỆU CHỦ TRỌ ===" -ForegroundColor Green
Write-Host "Email: $Email" -ForegroundColor Yellow

# Đọc script SQL và thay thế email
$SqlContent = Get-Content "create_simple_landlord_data.sql" -Raw
$SqlContent = $SqlContent -replace "DECLARE @ChuTroEmail NVARCHAR\(255\) = '[^']*';", "DECLARE @ChuTroEmail NVARCHAR(255) = '$Email';"

# Thông tin kết nối
$Server = "192.168.0.117"
$Database = "QuanLyPhongTro"
$Username = "sa"
$Password = "27012005"

try {
    # Import SQL Server module nếu có
    if (Get-Module -ListAvailable -Name SqlServer) {
        Import-Module SqlServer -ErrorAction SilentlyContinue
    }

    Write-Host "Đang kết nối SQL Server..." -ForegroundColor Yellow
    
    # Sử dụng Invoke-Sqlcmd nếu có
    if (Get-Command Invoke-Sqlcmd -ErrorAction SilentlyContinue) {
        Invoke-Sqlcmd -ServerInstance $Server -Database $Database -Username $Username -Password $Password -Query $SqlContent -TrustServerCertificate
        Write-Host "✅ Thành công! Dữ liệu đã được tạo." -ForegroundColor Green
    } else {
        # Fallback: Sử dụng .NET SqlConnection
        $ConnectionString = "Server=$Server;Database=$Database;User Id=$Username;Password=$Password;TrustServerCertificate=True;"
        $Connection = New-Object System.Data.SqlClient.SqlConnection($ConnectionString)
        $Connection.Open()
        
        $Command = New-Object System.Data.SqlClient.SqlCommand($SqlContent, $Connection)
        $Command.CommandTimeout = 60
        $Command.ExecuteNonQuery() | Out-Null
        
        $Connection.Close()
        Write-Host "✅ Thành công! Dữ liệu đã được tạo." -ForegroundColor Green
    }
    
} catch {
    Write-Host "❌ Lỗi: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Hãy thử chạy script SQL trực tiếp trong SSMS" -ForegroundColor Yellow
}

Write-Host "`n=== HƯỚNG DẪN ===" -ForegroundColor Cyan
Write-Host "1. Mở app Android" -ForegroundColor White
Write-Host "2. Đăng nhập với email: $Email" -ForegroundColor White
Write-Host "3. Chuyển sang giao diện Chủ trọ" -ForegroundColor White
Write-Host "4. Kiểm tra trang chủ - sẽ thấy 12 phòng" -ForegroundColor White