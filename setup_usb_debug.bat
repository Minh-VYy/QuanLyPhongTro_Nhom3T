@echo off
echo === Setting up USB debugging for SQL Server connection ===
echo.

echo 1. Finding ADB path...
set ADB_PATH=""
if exist "%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe" (
    set ADB_PATH="%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe"
    echo Found ADB at: %ADB_PATH%
) else if exist "%USERPROFILE%\AppData\Local\Android\Sdk\platform-tools\adb.exe" (
    set ADB_PATH="%USERPROFILE%\AppData\Local\Android\Sdk\platform-tools\adb.exe"
    echo Found ADB at: %ADB_PATH%
) else (
    echo ADB not found in default locations
    echo Please add Android SDK platform-tools to PATH
    echo Or run this manually: adb forward tcp:1433 tcp:1433
    pause
    exit /b 1
)

echo.
echo 2. Setting up port forwarding...
%ADB_PATH% forward tcp:1433 tcp:1433

echo.
echo 3. Checking connected devices...
%ADB_PATH% devices

echo.
echo === Setup complete! ===
echo Now your Android app can connect to SQL Server using:
echo IP: 127.0.0.1 or localhost
echo Port: 1433
echo.
echo To remove port forwarding later, run:
echo adb forward --remove tcp:1433
echo.
pause