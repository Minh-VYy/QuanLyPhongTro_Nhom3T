@echo off
echo === FIXING FIREWALL FOR SQL SERVER ===
echo.
echo Adding firewall rules...
netsh advfirewall firewall add rule name="SQL Server Port 1433" dir=in action=allow protocol=TCP localport=1433
netsh advfirewall firewall add rule name="SQL Server Browser" dir=in action=allow protocol=UDP localport=1434

echo.
echo Starting SQL Server Browser...
net start SQLBrowser

echo.
echo Checking firewall rules...
netsh advfirewall firewall show rule name="SQL Server Port 1433"

echo.
echo === DONE! Try your app now ===
pause