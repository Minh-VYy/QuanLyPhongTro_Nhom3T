# Check SQL Server Configuration for Remote Connections
Write-Host "=== SQL Server Configuration Check ===" -ForegroundColor Green

# Check if SQL Server is running
Write-Host "`n1. SQL Server Services:" -ForegroundColor Yellow
Get-Service -Name "*SQL*" | Where-Object {$_.Status -eq "Running"} | Format-Table

# Check if port 1433 is listening
Write-Host "`n2. Port 1433 Status:" -ForegroundColor Yellow
netstat -an | findstr 1433

# Check SQL Server Browser service
Write-Host "`n3. SQL Server Browser Service:" -ForegroundColor Yellow
Get-Service -Name "SQLBrowser" -ErrorAction SilentlyContinue | Format-Table

# Test local connection to SQL Server
Write-Host "`n4. Testing local connection:" -ForegroundColor Yellow
try {
    $connectionString = "Server=localhost,1433;Database=QuanLyPhongTro;User Id=sa;Password=27012005;TrustServerCertificate=true;"
    $connection = New-Object System.Data.SqlClient.SqlConnection($connectionString)
    $connection.Open()
    Write-Host "✅ Local connection successful!" -ForegroundColor Green
    $connection.Close()
} catch {
    Write-Host "❌ Local connection failed: $($_.Exception.Message)" -ForegroundColor Red
}

# Check Windows Firewall status
Write-Host "`n5. Windows Firewall Status:" -ForegroundColor Yellow
try {
    $firewallProfiles = Get-NetFirewallProfile
    foreach ($profile in $firewallProfiles) {
        Write-Host "$($profile.Name): $($profile.Enabled)" -ForegroundColor Cyan
    }
} catch {
    Write-Host "Cannot check firewall status (need admin rights)" -ForegroundColor Red
}

Write-Host "`n=== Recommendations ===" -ForegroundColor Green
Write-Host "1. If local connection failed, check SQL Server authentication mode"
Write-Host "2. If firewall is enabled, add rule for port 1433"
Write-Host "3. Ensure SQL Server Browser service is running"
Write-Host "4. Check if TCP/IP protocol is enabled in SQL Server Configuration Manager"