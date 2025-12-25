# Test SQL Server connection với password đúng
Write-Host "=== TESTING SQL SERVER CONNECTION ===" -ForegroundColor Green

$serverIP = "192.168.0.117"
$port = "1433"
$username = "sa"
$password = "27012005"  # Password đúng từ DatabaseConnector
$database = "QuanLyPhongTro"

try {
    Write-Host "Testing connection to: $serverIP,$port" -ForegroundColor Yellow
    Write-Host "Username: $username" -ForegroundColor Yellow
    Write-Host "Database: $database" -ForegroundColor Yellow
    
    # Try using sqlcmd
    $result = & sqlcmd -S "$serverIP,$port" -U $username -P $password -d $database -Q "SELECT COUNT(*) as TotalBienLai FROM BienLai; SELECT COUNT(*) as TotalDatPhong FROM DatPhong;"
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "SUCCESS: Connection established!" -ForegroundColor Green
        Write-Host "Query results:" -ForegroundColor Cyan
        Write-Host $result
    }
    else {
        Write-Host "FAILED: sqlcmd returned exit code $LASTEXITCODE" -ForegroundColor Red
    }
}
catch {
    Write-Host "ERROR: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "=== TEST COMPLETED ===" -ForegroundColor Green