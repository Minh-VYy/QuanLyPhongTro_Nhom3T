package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.PhongDao;
import com.example.QuanLyPhongTro_App.data.model.Phong;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class TestDatabaseActivity extends AppCompatActivity {
    private static final String TAG = "TestDatabaseActivity";
    private TextView tvResult;
    private Button btnTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_test);

        tvResult = findViewById(R.id.tv_result);
        btnTest = findViewById(R.id.btn_test);

        btnTest.setOnClickListener(v -> testDatabase());
    }

    private void testDatabase() {
        new TestTask().execute();
    }

    private class TestTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            StringBuilder result = new StringBuilder();
            Connection conn = null;
            
            try {
                result.append("Testing database connection...\n");
                Log.d(TAG, "Testing database connection");
                
                conn = DatabaseHelper.getConnection();
                result.append("✅ Connection successful!\n");
                Log.d(TAG, "Connection successful");
                
                // Test 1: Simple count
                PreparedStatement stmt1 = conn.prepareStatement("SELECT COUNT(*) as total FROM Phong");
                ResultSet rs1 = stmt1.executeQuery();
                if (rs1.next()) {
                    result.append("Total Phong: ").append(rs1.getInt("total")).append("\n");
                }
                rs1.close();
                stmt1.close();
                
                // Test 2: Count with conditions
                PreparedStatement stmt2 = conn.prepareStatement("SELECT COUNT(*) as available FROM Phong WHERE IsDuyet=1 AND IsBiKhoa=0 AND IsDeleted=0");
                ResultSet rs2 = stmt2.executeQuery();
                if (rs2.next()) {
                    result.append("Available Phong: ").append(rs2.getInt("available")).append("\n");
                }
                rs2.close();
                stmt2.close();
                
                // Test 3: Count with NhaTro JOIN
                PreparedStatement stmt3 = conn.prepareStatement("SELECT COUNT(*) as withNhaTro FROM Phong p INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId WHERE p.IsDuyet=1 AND p.IsBiKhoa=0 AND p.IsDeleted=0");
                ResultSet rs3 = stmt3.executeQuery();
                if (rs3.next()) {
                    result.append("With NhaTro JOIN: ").append(rs3.getInt("withNhaTro")).append("\n");
                }
                rs3.close();
                stmt3.close();
                
                // Test 4: Try PhongDao
                PhongDao dao = new PhongDao();
                List<Phong> rooms = dao.getAllPhongAvailable(conn);
                
                result.append("PhongDao result: ").append(rooms != null ? rooms.size() : "null").append(" rooms\n");
                
                if (rooms != null && !rooms.isEmpty()) {
                    result.append("First room: ").append(rooms.get(0).getTieuDe()).append("\n");
                    result.append("Price: ").append(rooms.get(0).getGiaTien()).append(" VND\n");
                } else {
                    result.append("No rooms returned by PhongDao\n");
                }
                
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
            Toast.makeText(TestDatabaseActivity.this, "Test completed", Toast.LENGTH_SHORT).show();
        }
    }
}