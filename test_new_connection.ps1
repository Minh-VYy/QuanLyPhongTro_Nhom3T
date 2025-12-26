# Test kết nối với IP mới
Write-Host "=== TESTING NEW IP CONNECTION ===" -ForegroundColor Green

$serverIP = "192.168.0.117"
$port = "1433"
$username = "sa"
$password = "27012005"
$database = "QuanLyPhongTro"

try {
    Write-Host "Testing connection to: $serverIP,$port" -ForegroundColor Yellow
    
    # Test basic connection
    $result = & sqlcmd -S "$serverIP,$port" -U $username -P $password -d $database -Q "SELECT COUNT(*) as TotalPhong FROM Phong; SELECT COUNT(*) as TotalBienLai FROM BienLai;"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "SUCCESS: Connection established!" -ForegroundColor Green
        Write-Host "Database content:" -ForegroundColor Cyan
        Write-Host $result
        
        # Test specific query for rooms
        Write-Host "`nTesting room query..." -ForegroundColor Yellow
        $roomResult = & sqlcmd -S "$serverIP,$port" -U $username -P $password -d $database -Q "SELECT TOP 5 PhongId, TieuDe, GiaThue FROM Phong;"
        Write-Host "Room query result:" -ForegroundColor Cyan
        Write-Host $roomResult
    }
    else {
        Write-Host "FAILED: sqlcmd returned exit code $LASTEXITCODE" -ForegroundColor Red
    }
}
catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "=== TEST COMPLETED ===" -ForegroundColor Green