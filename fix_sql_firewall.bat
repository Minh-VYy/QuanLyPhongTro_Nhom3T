@echo off
echo === Fixing SQL Server Firewall Issues ===
echo.

echo 1. Adding firewall rules for SQL Server...
netsh advfirewall firewall add rule name="SQL Server" dir=in action=allow protocol=TCP localport=1433
netsh advfirewall firewall add rule name="SQL Server Browser" dir=in action=allow protocol=UDP localport=1434

echo.
echo 2. Starting SQL Server Browser service...
net start SQLBrowser
sc config SQLBrowser start= auto

echo.
echo 3. Checking firewall rules...
netsh advfirewall firewall show rule name="SQL Server"

echo.
echo 4. Checking services...
sc query SQLBrowser
sc query MSSQLSERVER

echo.
echo === Done! Try connecting from your app now ===
pause