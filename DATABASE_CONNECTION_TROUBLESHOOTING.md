# Hướng dẫn khắc phục lỗi kết nối Database

## 1. Kiểm tra cấu hình SQL Server

### Bật SQL Server Browser Service:
```
1. Mở SQL Server Configuration Manager
2. Tìm "SQL Server Browser" trong Services
3. Đảm bảo nó đang chạy (Running)
4. Set Startup Type = Automatic
```

### Bật TCP/IP Protocol:
```
1. Mở SQL Server Configuration Manager
2. Vào SQL Server Network Configuration > Protocols for MSSQLSERVER
3. Enable TCP/IP protocol
4. Right-click TCP/IP > Properties > IP Addresses tab
5. Tìm IPAll section ở cuối:
   - TCP Dynamic Ports: để trống
   - TCP Port: 1433
6. Restart SQL Server service
```

### Kiểm tra Windows Firewall:
```
1. Mở Windows Defender Firewall
2. Click "Allow an app or feature through Windows Defender Firewall"
3. Tìm và check:
   - SQL Server
   - SQL Server Browser
4. Hoặc tạo Inbound Rule cho port 1433
```

## 2. Kiểm tra SQL Server Authentication

### Bật Mixed Mode Authentication:
```
1. Mở SQL Server Management Studio (SSMS)
2. Right-click server name > Properties
3. Vào Security tab
4. Chọn "SQL Server and Windows Authentication mode"
5. Restart SQL Server service
```

### Kiểm tra tài khoản SA:
```sql
-- Trong SSMS, chạy query này:
ALTER LOGIN sa ENABLE;
ALTER LOGIN sa WITH PASSWORD = '27012005';
```

## 3. Test kết nối từ máy khác

### Sử dụng telnet:
```cmd
telnet 192.168.0.117 1433
```
Nếu kết nối thành công, màn hình sẽ trống (không báo lỗi)

### Sử dụng SSMS từ máy khác:
```
Server name: 192.168.0.117,1433
Authentication: SQL Server Authentication
Login: sa
Password: 27012005
```

## 4. Kiểm tra trong Android App

### Chạy app và test:
1. Mở app
2. Long press vào nút "Filter" ở màn hình chính
3. Sẽ mở màn hình Database Test
4. Nhấn "Test Kết Nối"
5. Xem log trong Android Studio Logcat với filter "DatabaseConnector"

### Các lỗi thường gặp:

**"Connection refused":**
- SQL Server không chạy
- Port 1433 bị block
- IP address sai

**"Login failed for user 'sa'":**
- Sai password
- Tài khoản sa bị disable
- Mixed mode authentication chưa bật

**"Network error IOException":**
- Firewall block kết nối
- Không có mạng
- SQL Server Browser service không chạy

## 5. Cấu hình mạng

### Đảm bảo cùng mạng:
```cmd
# Trên máy SQL Server, chạy:
ipconfig

# Trên Android (qua ADB):
adb shell ip addr show wlan0
```

### Test ping:
```cmd
# Từ máy Android đến SQL Server:
ping 192.168.0.117
```

## 6. Alternative: Sử dụng SQL Server Express

Nếu vẫn không kết nối được, thử cài SQL Server Express:
1. Download SQL Server Express
2. Trong quá trình cài đặt, chọn "Mixed Mode"
3. Set password cho SA
4. Bật TCP/IP trong Configuration Manager

## 7. Logs để kiểm tra

### SQL Server Error Log:
```
SQL Server Management Studio > Management > SQL Server Logs > Current
```

### Android Logcat:
```
Filter: DatabaseConnector
Hoặc: System.out
```

## 8. Test Connection String

Thử các connection string khác nhau:

```java
// Option 1: Hiện tại
"jdbc:jtds:sqlserver://192.168.0.117:1433/QuanLyPhongTro"

// Option 2: Với instance name
"jdbc:jtds:sqlserver://192.168.0.117:1433/QuanLyPhongTro;instance=SQLEXPRESS"

// Option 3: Với domain
"jdbc:jtds:sqlserver://192.168.0.117:1433/QuanLyPhongTro;domain="
```