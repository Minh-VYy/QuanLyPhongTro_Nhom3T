package com.example.QuanLyPhongTro_App.ui.landlord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TestLandlordDatabaseActivity extends AppCompatActivity {
    private static final String TAG = "TestLandlordDB";
    private TextView tvResult;
    private Button btnTest;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        sessionManager = new SessionManager(this);
        tvResult = findViewById(R.id.tv_result);
        btnTest = findViewById(R.id.btn_test);

        btnTest.setText("Test Landlord DB");
        btnTest.setOnClickListener(v -> testLandlordDatabase());
        
        // Thêm nút tạo dữ liệu test
        Button btnCreateData = new Button(this);
        btnCreateData.setText("Tạo dữ liệu test");
        btnCreateData.setOnClickListener(v -> createTestData());
        
        // Thêm nút vào layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.tv_result).getParent();
        layout.addView(btnCreateData, 1);
    }

    private void testLandlordDatabase() {
        new TestLandlordTask().execute();
    }
    
    private void createTestData() {
        new CreateTestDataTask().execute();
    }

    private class TestLandlordTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            Connection conn = null;
            
            try {
                result.append("Testing landlord database connection...\n");
                Log.d(TAG, "Testing landlord database connection");
                
                conn = DatabaseHelper.getConnection();
                result.append("✅ Connection successful!\n");
                Log.d(TAG, "Connection successful");
                
                // Lấy thông tin user hiện tại
                String userId = sessionManager.getUserId();
                String userRole = sessionManager.getUserRole();
                result.append("Current User ID: ").append(userId).append("\n");
                result.append("Current User Role: ").append(userRole).append("\n");
                
                if (userId == null || !userRole.equals("landlord")) {
                    result.append("❌ Not logged in as landlord\n");
                    return result.toString();
                }
                
                // Test 1: Count total NhaTro for this landlord
                PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) as total FROM NhaTro WHERE ChuTroId = ?");
                stmt1.setString(1, userId);
                ResultSet rs1 = stmt1.executeQuery();
                if (rs1.next()) {
                    result.append("Total NhaTro: ").append(rs1.getInt("total")).append("\n");
                }
                rs1.close();
                stmt1.close();
                
                // Test 2: Count total Phong for this landlord
                PreparedStatement stmt2 = conn.prepareStatement(
                    "SELECT COUNT(*) as total FROM Phong p " +
                    "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                    "WHERE nt.ChuTroId = ? AND p.IsDeleted = 0"
                );
                stmt2.setString(1, userId);
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    result.append("Total Phong: ").append(rs2.getInt("total")).append("\n");
                }
                rs2.close();
                stmt2.close();
                
                // Test 3: Count active Phong
                PreparedStatement stmt3 = conn.prepareStatement(
                    "SELECT COUNT(*) as active FROM Phong p " +
                    "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                    "WHERE nt.ChuTroId = ? AND p.IsDeleted = 0 AND p.IsDuyet = 1 AND p.IsBiKhoa = 0"
                );
                stmt3.setString(1, userId);
                ResultSet rs3 = stmt3.executeQuery();
                if (rs3.next()) {
                    result.append("Active Phong: ").append(rs3.getInt("active")).append("\n");
                }
                rs3.close();
                stmt3.close();
                
                // Test 4: Try LandlordPhongDao
                LandlordPhongDao dao = new LandlordPhongDao();
                List<Phong> rooms = dao.getPhongByChuTroId(conn, userId);
                
                result.append("LandlordPhongDao result: ").append(rooms != null ? rooms.size() : "null").append(" rooms\n");
                
                if (rooms != null && !rooms.isEmpty()) {
                    result.append("First room: ").append(rooms.get(0).getTieuDe()).append("\n");
                    result.append("Price: ").append(rooms.get(0).getGiaTien()).append(" VND\n");
                    result.append("Status: ").append(rooms.get(0).getTrangThai()).append("\n");
                    result.append("Is Duyet: ").append(rooms.get(0).isDuyet()).append("\n");
                    result.append("Is Bi Khoa: ").append(rooms.get(0).isBiKhoa()).append("\n");
                } else {
                    result.append("No rooms returned by LandlordPhongDao\n");
                }
                
                // Test 5: Get stats
                LandlordPhongDao.PhongStats stats = dao.getPhongStats(conn, userId);
                result.append("Stats - Total: ").append(stats.totalPhong)
                      .append(", Active: ").append(stats.activePhong)
                      .append(", Available: ").append(stats.availablePhong)
                      .append(", Rented: ").append(stats.rentedPhong)
                      .append(", Pending: ").append(stats.pendingPhong).append("\n");
                
                Log.d(TAG, "Test completed: " + (rooms != null ? rooms.size() : "null") + " rooms");
                
            } catch (Exception e) {
                result.append("❌ Error: ").append(e.getMessage()).append("\n");
                result.append("Stack trace: ").append(Log.getStackTraceString(e)).append("\n");
                Log.e(TAG, "Test failed", e);
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
            
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            tvResult.setText(result);
            Toast.makeText(TestLandlordDatabaseActivity.this, "Landlord test completed", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * AsyncTask để tạo dữ liệu test cho chủ trọ
     */
    private class CreateTestDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            Connection conn = null;
            
            try {
                result.append("Creating test data for landlord...\n");
                Log.d(TAG, "Creating test data for landlord");
                
                conn = DatabaseHelper.getConnection();
                result.append("✅ Connection successful!\n");
                
                String userId = sessionManager.getUserId();
                String userRole = sessionManager.getUserRole();
                
                if (userId == null || !userRole.equals("landlord")) {
                    result.append("❌ Not logged in as landlord\n");
                    return result.toString();
                }
                
                result.append("Creating data for ChuTroId: ").append(userId).append("\n");
                
                // Xóa dữ liệu cũ
                PreparedStatement deletePhong = conn.prepareStatement(
                    "DELETE FROM Phong WHERE NhaTroId IN (SELECT NhaTroId FROM NhaTro WHERE ChuTroId = ?)"
                );
                deletePhong.setString(1, userId);
                deletePhong.executeUpdate();
                deletePhong.close();
                
                PreparedStatement deleteNhaTro = conn.prepareStatement("DELETE FROM NhaTro WHERE ChuTroId = ?");
                deleteNhaTro.setString(1, userId);
                deleteNhaTro.executeUpdate();
                deleteNhaTro.close();
                
                result.append("Deleted old data\n");
                
                // Tạo QuanHuyen và Phuong nếu chưa có
                createLocationData(conn);
                
                // Lấy ID của QuanHuyen và Phuong
                PreparedStatement getQuan1 = conn.prepareStatement("SELECT QuanHuyenId FROM QuanHuyen WHERE Ten = N'Quận 1'");
                ResultSet rs1 = getQuan1.executeQuery();
                int quanHuyen1Id = 0;
                if (rs1.next()) quanHuyen1Id = rs1.getInt("QuanHuyenId");
                rs1.close();
                getQuan1.close();
                
                PreparedStatement getPhuong1 = conn.prepareStatement("SELECT PhuongId FROM Phuong WHERE Ten = N'Phường Bến Nghé'");
                ResultSet rs2 = getPhuong1.executeQuery();
                int phuong1Id = 0;
                if (rs2.next()) phuong1Id = rs2.getInt("PhuongId");
                rs2.close();
                getPhuong1.close();
                
                // Tạo NhaTro 1
                String nhaTro1Id = java.util.UUID.randomUUID().toString();
                PreparedStatement insertNhaTro1 = conn.prepareStatement(
                    "INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, QuanHuyenId, PhuongId, MoTa, IsDuyet, CreatedAt) " +
                    "VALUES (?, ?, N'Nhà trọ Sunshine - Quận 1', N'123 Đường Lê Lợi, Phường Bến Nghé, Quận 1, TP.HCM', ?, ?, N'Nhà trọ cao cấp, gần trung tâm thành phố', 1, SYSDATETIMEOFFSET())"
                );
                insertNhaTro1.setString(1, nhaTro1Id);
                insertNhaTro1.setString(2, userId);
                insertNhaTro1.setInt(3, quanHuyen1Id);
                insertNhaTro1.setInt(4, phuong1Id);
                insertNhaTro1.executeUpdate();
                insertNhaTro1.close();
                
                result.append("Created NhaTro 1\n");
                
                // Tạo NhaTro 2
                String nhaTro2Id = java.util.UUID.randomUUID().toString();
                PreparedStatement insertNhaTro2 = conn.prepareStatement(
                    "INSERT INTO NhaTro (NhaTroId, ChuTroId, TieuDe, DiaChi, QuanHuyenId, PhuongId, MoTa, IsDuyet, CreatedAt) " +
                    "VALUES (?, ?, N'Nhà trọ Green House - Quận 3', N'456 Đường Võ Văn Tần, Phường Võ Thị Sáu, Quận 3, TP.HCM', ?, ?, N'Nhà trọ giá rẻ, phù hợp sinh viên', 1, SYSDATETIMEOFFSET())"
                );
                insertNhaTro2.setString(1, nhaTro2Id);
                insertNhaTro2.setString(2, userId);
                insertNhaTro2.setInt(3, quanHuyen1Id); // Tạm dùng quận 1
                insertNhaTro2.setInt(4, phuong1Id);   // Tạm dùng phường 1
                insertNhaTro2.executeUpdate();
                insertNhaTro2.close();
                
                result.append("Created NhaTro 2\n");
                
                // Tạo Phong cho NhaTro 1
                createPhongForNhaTro(conn, nhaTro1Id, "cao cấp");
                result.append("Created 6 phòng for NhaTro 1\n");
                
                // Tạo Phong cho NhaTro 2
                createPhongForNhaTro(conn, nhaTro2Id, "sinh viên");
                result.append("Created 6 phòng for NhaTro 2\n");
                
                result.append("✅ Test data created successfully!\n");
                result.append("Total: 2 NhaTro, 12 Phong\n");
                
            } catch (Exception e) {
                result.append("❌ Error: ").append(e.getMessage()).append("\n");
                Log.e(TAG, "Error creating test data", e);
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
            
            return result.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            tvResult.setText(result);
            Toast.makeText(TestLandlordDatabaseActivity.this, "Test data creation completed", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void createLocationData(Connection conn) throws Exception {
        // Tạo QuanHuyen
        PreparedStatement insertQuan = conn.prepareStatement(
            "IF NOT EXISTS (SELECT 1 FROM QuanHuyen WHERE Ten = N'Quận 1') " +
            "INSERT INTO QuanHuyen (Ten) VALUES (N'Quận 1')"
        );
        insertQuan.executeUpdate();
        insertQuan.close();
        
        // Lấy QuanHuyenId
        PreparedStatement getQuan = conn.prepareStatement("SELECT QuanHuyenId FROM QuanHuyen WHERE Ten = N'Quận 1'");
        ResultSet rs = getQuan.executeQuery();
        int quanHuyenId = 0;
        if (rs.next()) quanHuyenId = rs.getInt("QuanHuyenId");
        rs.close();
        getQuan.close();
        
        // Tạo Phuong
        PreparedStatement insertPhuong = conn.prepareStatement(
            "IF NOT EXISTS (SELECT 1 FROM Phuong WHERE Ten = N'Phường Bến Nghé') " +
            "INSERT INTO Phuong (Ten, QuanHuyenId) VALUES (N'Phường Bến Nghé', ?)"
        );
        insertPhuong.setInt(1, quanHuyenId);
        insertPhuong.executeUpdate();
        insertPhuong.close();
    }
    
    private void createPhongForNhaTro(Connection conn, String nhaTroId, String type) throws Exception {
        if ("cao cấp".equals(type)) {
            // Phòng cao cấp
            createPhong(conn, nhaTroId, "N'Phòng 101 - Studio cao cấp'", 25.0, 8000000, 16000000, 2, "N'Đã thuê'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng 102 - 1 phòng ngủ'", 30.0, 10000000, 20000000, 2, "N'Đã thuê'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng 103 - Studio view đẹp'", 28.0, 9000000, 18000000, 2, "N'Còn trống'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng 201 - 1 phòng ngủ lớn'", 35.0, 12000000, 24000000, 3, "N'Còn trống'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng 202 - Penthouse'", 45.0, 15000000, 30000000, 4, "N'Còn trống'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng 203 - Mới xây'", 32.0, 11000000, 22000000, 2, "N'Còn trống'", 0, 0);
        } else {
            // Phòng sinh viên
            createPhong(conn, nhaTroId, "N'Phòng A01 - Sinh viên'", 15.0, 3000000, 3000000, 2, "N'Đã thuê'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng A02 - Có gác lửng'", 18.0, 3500000, 3500000, 2, "N'Đã thuê'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng A03 - Giá rẻ'", 16.0, 3200000, 3200000, 2, "N'Còn trống'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng B01 - Tầng 2'", 17.0, 3300000, 3300000, 2, "N'Còn trống'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng B02 - Ban công'", 20.0, 4000000, 4000000, 3, "N'Còn trống'", 1, 0);
            createPhong(conn, nhaTroId, "N'Phòng B03 - Đang sửa chữa'", 16.0, 3200000, 3200000, 2, "N'Còn trống'", 1, 1);
        }
    }
    
    private void createPhong(Connection conn, String nhaTroId, String tieuDe, double dienTich, 
                           long giaTien, long tienCoc, int soNguoi, String trangThai, 
                           int isDuyet, int isBiKhoa) throws Exception {
        PreparedStatement stmt = conn.prepareStatement(
            "INSERT INTO Phong (PhongId, NhaTroId, TieuDe, DienTich, GiaTien, TienCoc, SoNguoiToiDa, TrangThai, IsDuyet, IsBiKhoa, IsDeleted, CreatedAt) " +
            "VALUES (NEWID(), ?, " + tieuDe + ", ?, ?, ?, ?, " + trangThai + ", ?, ?, 0, SYSDATETIMEOFFSET())"
        );
        stmt.setString(1, nhaTroId);
        stmt.setDouble(2, dienTich);
        stmt.setLong(3, giaTien);
        stmt.setLong(4, tienCoc);
        stmt.setInt(5, soNguoi);
        stmt.setInt(6, isDuyet);
        stmt.setInt(7, isBiKhoa);
        stmt.executeUpdate();
        stmt.close();
    }
}