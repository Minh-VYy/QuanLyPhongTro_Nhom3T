# Script để tạo dữ liệu thanh toán test
Write-Host "=== CREATING PAYMENT TEST DATA ===" -ForegroundColor Green

$serverIP = "192.168.0.117"
$port = "1433"
$username = "sa"
$password = "123456"
$database = "QuanLyPhongTro"

# Thử các cách kết nối khác nhau
$connectionStrings = @(
    "Server=$serverIP,$port;Database=$database;User Id=$username;Password=$password;TrustServerCertificate=true;",
    "Server=$serverIP;Database=$database;User Id=$username;Password=$password;TrustServerCertificate=true;",
    "Data Source=$serverIP,$port;Initial Catalog=$database;User ID=$username;Password=$password;TrustServerCertificate=true;"
)

foreach ($connStr in $connectionStrings) {
    try {
        Write-Host "Trying connection: $connStr" -ForegroundColor Yellow
        
        # Load SQL Server module if available
        if (Get-Module -ListAvailable -Name SqlServer) {
            Import-Module SqlServer -ErrorAction SilentlyContinue
        }
        
        # Try using Invoke-Sqlcmd if available
        if (Get-Command Invoke-Sqlcmd -ErrorAction SilentlyContinue) {
            Write-Host "Using Invoke-Sqlcmd..." -ForegroundColor Cyan
            $result = Invoke-Sqlcmd -ConnectionString $connStr -InputFile "create_payment_test_data.sql" -ErrorAction Stop
            Write-Host "SUCCESS: Payment test data created!" -ForegroundColor Green
            $result | Format-Table
            break
        }
        else {
            # Fallback to sqlcmd.exe
            Write-Host "Using sqlcmd.exe..." -ForegroundColor Cyan
            $result = & sqlcmd -S "$serverIP,$port" -U $username -P $password -d $database -i "create_payment_test_data.sql"
            if ($LASTEXITCODE -eq 0) {
                Write-Host "SUCCESS: Payment test data created!" -ForegroundColor Green
                Write-Host $result
                break
            }
            else {
                Write-Host "sqlcmd failed with exit code: $LASTEXITCODE" -ForegroundColor Red
            }
        }
    }
    catch {
        Write-Host "Connection failed: $($_.Exception.Message)" -ForegroundColor Red
        continue
    }
}

Write-Host "=== SCRIPT COMPLETED ===" -ForegroundColor Green