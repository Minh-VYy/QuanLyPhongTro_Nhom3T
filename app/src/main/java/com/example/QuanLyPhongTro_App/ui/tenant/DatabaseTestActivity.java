package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.DatPhongDao;
import com.example.QuanLyPhongTro_App.data.dao.PhongDao;
import com.example.QuanLyPhongTro_App.data.dao.YeuCauHoTroDao;
import com.example.QuanLyPhongTro_App.data.model.DatPhong;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.data.model.YeuCauHoTro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

/**
 * Activity để test kết nối database và các DAO
 */
public class DatabaseTestActivity extends AppCompatActivity {
    private static final String TAG = "DatabaseTestActivity";
    
    private TextView txtResult;
    private ProgressBar progressBar;
    private Button btnTestConnection, btnTestPhong, btnTestDatPhong, btnTestYeuCau;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);
        
        initViews();
        setupListeners();
    }

    private void initViews() {
        txtResult = findViewById(R.id.txtResult);
        progressBar = findViewById(R.id.progressBar);
        btnTestConnection = findViewById(R.id.btnTestConnection);
        btnTestPhong = findViewById(R.id.btnTestPhong);
        btnTestDatPhong = findViewById(R.id.btnTestDatPhong);
        btnTestYeuCau = findViewById(R.id.btnTestYeuCau);
    }

    private void setupListeners() {
        btnTestConnection.setOnClickListener(v -> new TestConnectionTask().execute());
        btnTestPhong.setOnClickListener(v -> new TestPhongTask().execute());
        btnTestDatPhong.setOnClickListener(v -> new TestDatPhongTask().execute());
        btnTestYeuCau.setOnClickListener(v -> new TestYeuCauTask().execute());
    }

    private void showResult(String result) {
        txtResult.setText(result);
    }

    // Test kết nối database
    private class TestConnectionTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            showResult("Đang kiểm tra kết nối...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                if (conn != null && !conn.isClosed()) {
                    String dbName = conn.getCatalog();
                    return "✅ Kết nối thành công!\n\nDatabase: " + dbName;
                }
                return "❌ Kết nối thất bại!";
            } catch (Exception e) {
                return "❌ Lỗi: " + e.getMessage();
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            showResult(result);
        }
    }

    // Test load phòng
    private class TestPhongTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            showResult("Đang load phòng...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Connection conn = null;
            try {
                Log.d(TAG, "Starting TestPhongTask...");
                conn = DatabaseHelper.getConnection();
                
                // Test 1: Đếm tổng số phòng
                String countQuery = "SELECT COUNT(*) as Total FROM Phong";
                PreparedStatement countStmt = conn.prepareStatement(countQuery);
                ResultSet countRs = countStmt.executeQuery();
                int totalPhong = 0;
                if (countRs.next()) {
                    totalPhong = countRs.getInt("Total");
                }
                countRs.close();
                countStmt.close();
                
                Log.d(TAG, "Total phòng in database: " + totalPhong);
                
                // Test 2: Đếm phòng đã duyệt
                String approvedQuery = "SELECT COUNT(*) as Total FROM Phong WHERE IsDuyet = 1 AND IsBiKhoa = 0 AND IsDeleted = 0";
                PreparedStatement approvedStmt = conn.prepareStatement(approvedQuery);
                ResultSet approvedRs = approvedStmt.executeQuery();
                int approvedPhong = 0;
                if (approvedRs.next()) {
                    approvedPhong = approvedRs.getInt("Total");
                }
                approvedRs.close();
                approvedStmt.close();
                
                Log.d(TAG, "Approved phòng: " + approvedPhong);
                
                // Test 3: Load phòng bằng DAO
                PhongDao dao = new PhongDao();
                List<Phong> list = dao.getAllPhongAvailable(conn);
                
                if (list != null && !list.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("✅ Load thành công!\n\n");
                    sb.append("📊 Thống kê:\n");
                    sb.append("• Tổng phòng: ").append(totalPhong).append("\n");
                    sb.append("• Phòng đã duyệt: ").append(approvedPhong).append("\n");
                    sb.append("• Phòng load được: ").append(list.size()).append("\n\n");
                    
                    // Hiển thị 3 phòng đầu tiên
                    int count = Math.min(3, list.size());
                    sb.append("📍 Danh sách phòng:\n\n");
                    for (int i = 0; i < count; i++) {
                        Phong p = list.get(i);
                        sb.append((i+1)).append(". ").append(p.getTieuDe()).append("\n");
                        sb.append("   💰 ").append(String.format("%,d", p.getGiaTien())).append(" VNĐ\n");
                        sb.append("   📐 ").append(p.getDienTich()).append(" m²\n");
                        sb.append("   📍 ").append(p.getDiaChiNhaTro()).append("\n");
                        
                        if (p.getDanhSachAnhUrl() != null && !p.getDanhSachAnhUrl().isEmpty()) {
                            sb.append("   🖼️ Có ảnh\n");
                        }
                        
                        sb.append("\n");
                    }
                    
                    if (list.size() > 3) {
                        sb.append("... và ").append(list.size() - 3).append(" phòng khác\n");
                    }
                    
                    return sb.toString();
                } else {
                    StringBuilder sb = new StringBuilder();
                    sb.append("⚠️ Không load được phòng!\n\n");
                    sb.append("📊 Thống kê:\n");
                    sb.append("• Tổng phòng: ").append(totalPhong).append("\n");
                    sb.append("• Phòng đã duyệt: ").append(approvedPhong).append("\n\n");
                    
                    if (totalPhong == 0) {
                        sb.append("❌ Database chưa có dữ liệu!\n");
                        sb.append("Hãy chạy script SQL để insert dữ liệu mẫu.");
                    } else if (approvedPhong == 0) {
                        sb.append("❌ Không có phòng nào đã duyệt!\n");
                        sb.append("Cần set IsDuyet=1, IsBiKhoa=0, IsDeleted=0");
                    } else {
                        sb.append("❌ Lỗi khi load dữ liệu!\n");
                        sb.append("Xem Logcat với tag 'PhongDao' để biết chi tiết.");
                    }
                    
                    return sb.toString();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in TestPhongTask: " + e.getMessage(), e);
                return "❌ Lỗi: " + e.getMessage() + "\n\nXem Logcat để biết chi tiết.";
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            showResult(result);
        }
    }

    // Test load đặt phòng
    private class TestDatPhongTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            showResult("Đang load đặt phòng...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                DatPhongDao dao = new DatPhongDao();
                
                // Test với user ID mẫu (thay bằng user thật khi test)
                String testUserId = "11111111-1111-1111-1111-111111111111";
                List<DatPhong> list = dao.getDatPhongByNguoiThue(conn, testUserId);
                
                if (list != null && !list.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("✅ Load thành công ").append(list.size()).append(" đặt phòng\n\n");
                    
                    for (DatPhong dp : list) {
                        sb.append("🏠 ").append(dp.getTenPhong()).append("\n");
                        sb.append("📅 ").append(dp.getBatDau()).append("\n");
                        sb.append("📊 ").append(dp.getTenTrangThai()).append("\n\n");
                    }
                    
                    return sb.toString();
                }
                return "ℹ️ Chưa có đặt phòng nào";
            } catch (Exception e) {
                return "❌ Lỗi: " + e.getMessage();
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            showResult(result);
        }
    }

    // Test load yêu cầu hỗ trợ
    private class TestYeuCauTask extends AsyncTask<Void, Void, String> {
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            showResult("Đang load yêu cầu hỗ trợ...");
        }

        @Override
        protected String doInBackground(Void... voids) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                YeuCauHoTroDao dao = new YeuCauHoTroDao();
                
                // Test với user ID mẫu
                String testUserId = "11111111-1111-1111-1111-111111111111";
                List<YeuCauHoTro> list = dao.getYeuCauByNguoiDung(conn, testUserId);
                
                if (list != null && !list.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("✅ Load thành công ").append(list.size()).append(" yêu cầu\n\n");
                    
                    for (YeuCauHoTro yc : list) {
                        sb.append("🎫 ").append(yc.getTieuDe()).append("\n");
                        sb.append("📂 ").append(yc.getTenLoaiHoTro()).append("\n");
                        sb.append("📊 ").append(yc.getTrangThai()).append("\n\n");
                    }
                    
                    return sb.toString();
                }
                return "ℹ️ Chưa có yêu cầu nào";
            } catch (Exception e) {
                return "❌ Lỗi: " + e.getMessage();
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            showResult(result);
        }
    }
}
