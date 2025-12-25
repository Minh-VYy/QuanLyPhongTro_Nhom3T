# Test Database Connection Script
Write-Host "=== Testing Database Connection ===" -ForegroundColor Green

$serverIP = "172.26.98.234"
$port = "1433"
$database = "QuanLyPhongTro"
$username = "sa"
$password = "27012005"

Write-Host "`n1. Testing TCP Connection to $serverIP`:$port" -ForegroundColor Yellow
$tcpTest = Test-NetConnection -ComputerName $serverIP -Port $port -WarningAction SilentlyContinue
if ($tcpTest.TcpTestSucceeded) {
    Write-Host "✅ TCP Connection successful!" -ForegroundColor Green
} else {
    Write-Host "❌ TCP Connection failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n2. Testing SQL Server Connection" -ForegroundColor Yellow
try {
    $connectionString = "Server=$serverIP,$port;Database=$database;User Id=$username;Password=$password;TrustServerCertificate=true;Timeout=30;"
    $connection = New-Object System.Data.SqlClient.SqlConnection($connectionString)
    $connection.Open()
    Write-Host "✅ SQL Server connection successful!" -ForegroundColor Green
    
    # Test a simple query
    $command = $connection.CreateCommand()
    $command.CommandText = "SELECT COUNT(*) as TableCount FROM INFORMATION_SCHEMA.TABLES"
    $result = $command.ExecuteScalar()
    Write-Host "✅ Database has $result tables" -ForegroundColor Green
    
    # Test specific tables
    $command.CommandText = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME IN ('DatPhong', 'BienLai', 'NguoiDung')"
    $result = $command.ExecuteScalar()
    Write-Host "✅ Found $result required tables (DatPhong, BienLai, NguoiDung)" -ForegroundColor Green
    
    $connection.Close()
} catch {
    Write-Host "❌ SQL Server connection failed: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

Write-Host "`n3. Testing Android App Connection String" -ForegroundColor Yellow
$androidConnectionString = "jdbc:jtds:sqlserver://$serverIP`:$port/$database"
Write-Host "Android connection string: $androidConnectionString" -ForegroundColor Cyan
Write-Host "Username: $username" -ForegroundColor Cyan
Write-Host "Password: $password" -ForegroundColor Cyan

Write-Host "`n✅ All tests passed! Your Android app should be able to connect to the database." -ForegroundColor Green
Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Install the updated APK on your Android device"
Write-Host "2. Make sure your Android device is on the same WiFi network"
Write-Host "3. Test the app's database functionality"