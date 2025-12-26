package com.example.QuanLyPhongTro_App.ui.landlord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.sql.Connection;

public class TestManagePhongActivity extends AppCompatActivity {
    private static final String TAG = "TestManagePhong";
    
    private EditText edtPhongId, edtTitle, edtPrice;
    private Button btnTestUpdate, btnTestDelete, btnTestToggle, btnTestOwnership;
    private TextView tvResults;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_manage_phong);
        
        sessionManager = new SessionManager(this);
        
        initViews();
        setupButtons();
        
        // Load sample data
        loadSamplePhongId();
    }
    
    private void initViews() {
        edtPhongId = findViewById(R.id.edt_phong_id);
        edtTitle = findViewById(R.id.edt_title);
        edtPrice = findViewById(R.id.edt_price);
        btnTestUpdate = findViewById(R.id.btn_test_update);
        btnTestDelete = findViewById(R.id.btn_test_delete);
        btnTestToggle = findViewById(R.id.btn_test_toggle);
        btnTestOwnership = findViewById(R.id.btn_test_ownership);
        tvResults = findViewById(R.id.tv_results);
    }
    
    private void setupButtons() {
        btnTestUpdate.setOnClickListener(v -> testUpdate());
        btnTestDelete.setOnClickListener(v -> testDelete());
        btnTestToggle.setOnClickListener(v -> testToggle());
        btnTestOwnership.setOnClickListener(v -> testOwnership());
    }
    
    private void loadSamplePhongId() {
        new LoadSamplePhongTask().execute();
    }
    
    private void testUpdate() {
        String phongId = edtPhongId.getText().toString().trim();
        String title = edtTitle.getText().toString().trim();
        String priceStr = edtPrice.getText().toString().trim();
        
        if (phongId.isEmpty() || title.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            long price = Long.parseLong(priceStr);
            new TestUpdateTask().execute(phongId, title, String.valueOf(price));
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
        }
    }
    
    private void testDelete() {
        String phongId = edtPhongId.getText().toString().trim();
        if (phongId.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập PhongId", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new TestDeleteTask().execute(phongId);
    }
    
    private void testToggle() {
        String phongId = edtPhongId.getText().toString().trim();
        if (phongId.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập PhongId", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new TestToggleTask().execute(phongId);
    }
    
    private void testOwnership() {
        String phongId = edtPhongId.getText().toString().trim();
        if (phongId.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập PhongId", Toast.LENGTH_SHORT).show();
            return;
        }
        
        new TestOwnershipTask().execute(phongId);
    }
    
    private void appendResult(String result) {
        runOnUiThread(() -> {
            String current = tvResults.getText().toString();
            tvResults.setText(current + "\n" + result);
        });
    }
    
    // AsyncTask classes
    private class LoadSamplePhongTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                String chuTroId = sessionManager.getUserId();
                
                // Lấy PhongId đầu tiên của chủ trọ
                String query = "SELECT TOP 1 p.PhongId, p.TieuDe, p.GiaTien " +
                              "FROM Phong p INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                              "WHERE nt.ChuTroId = ? AND p.IsDeleted = 0";
                              
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, chuTroId);
                    try (java.sql.ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("PhongId") + "|" + 
                                   rs.getString("TieuDe") + "|" + 
                                   rs.getLong("GiaTien");
                        }
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading sample phong: " + e.getMessage(), e);
                return "ERROR: " + e.getMessage();
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
            return null;
        }
        
        @Override
        protected void onPostExecute(String result) {
            if (result != null && !result.startsWith("ERROR")) {
                String[] parts = result.split("\\|");
                if (parts.length >= 3) {
                    edtPhongId.setText(parts[0]);
                    edtTitle.setText(parts[1]);
                    edtPrice.setText(parts[2]);
                    appendResult("✅ Loaded sample data: " + parts[1]);
                }
            } else {
                appendResult("❌ No sample data found: " + result);
            }
        }
    }
    
    private class TestUpdateTask extends AsyncTask<String, Void, Boolean> {
        private String errorMsg = null;
        
        @Override
        protected Boolean doInBackground(String... params) {
            String phongId = params[0];
            String title = params[1];
            String priceStr = params[2];
            
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                String chuTroId = sessionManager.getUserId();
                long price = Long.parseLong(priceStr);
                
                Log.d(TAG, "Testing update - PhongId: " + phongId + ", ChuTroId: " + chuTroId);
                
                ManagePhongDao dao = new ManagePhongDao();
                return dao.updatePhong(conn, phongId, chuTroId, title, price, "Test update");
                
            } catch (Exception e) {
                Log.e(TAG, "Error testing update: " + e.getMessage(), e);
                errorMsg = e.getMessage();
                return false;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                appendResult("✅ UPDATE SUCCESS");
            } else {
                appendResult("❌ UPDATE FAILED: " + errorMsg);
            }
        }
    }
    
    private class TestDeleteTask extends AsyncTask<String, Void, Boolean> {
        private String errorMsg = null;
        
        @Override
        protected Boolean doInBackground(String... params) {
            String phongId = params[0];
            
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                String chuTroId = sessionManager.getUserId();
                
                Log.d(TAG, "Testing delete - PhongId: " + phongId + ", ChuTroId: " + chuTroId);
                
                ManagePhongDao dao = new ManagePhongDao();
                return dao.deletePhong(conn, phongId, chuTroId);
                
            } catch (Exception e) {
                Log.e(TAG, "Error testing delete: " + e.getMessage(), e);
                errorMsg = e.getMessage();
                return false;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                appendResult("✅ DELETE SUCCESS");
            } else {
                appendResult("❌ DELETE FAILED: " + errorMsg);
            }
        }
    }
    
    private class TestToggleTask extends AsyncTask<String, Void, Boolean> {
        private String errorMsg = null;
        
        @Override
        protected Boolean doInBackground(String... params) {
            String phongId = params[0];
            
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                String chuTroId = sessionManager.getUserId();
                
                Log.d(TAG, "Testing toggle - PhongId: " + phongId + ", ChuTroId: " + chuTroId);
                
                ManagePhongDao dao = new ManagePhongDao();
                return dao.togglePhongActive(conn, phongId, chuTroId, true);
                
            } catch (Exception e) {
                Log.e(TAG, "Error testing toggle: " + e.getMessage(), e);
                errorMsg = e.getMessage();
                return false;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }
        
        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                appendResult("✅ TOGGLE SUCCESS");
            } else {
                appendResult("❌ TOGGLE FAILED: " + errorMsg);
            }
        }
    }
    
    private class TestOwnershipTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String phongId = params[0];
            
            Connection conn = null;
            try {
                conn = DatabaseHelper.getConnection();
                String chuTroId = sessionManager.getUserId();
                
                String query = "SELECT COUNT(*) as count FROM Phong p " +
                              "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                              "WHERE p.PhongId = ? AND nt.ChuTroId = ?";
                              
                try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, phongId);
                    stmt.setString(2, chuTroId);
                    
                    try (java.sql.ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt("count");
                            return "Ownership check: " + count + " (PhongId: " + phongId + ", ChuTroId: " + chuTroId + ")";
                        }
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error testing ownership: " + e.getMessage(), e);
                return "ERROR: " + e.getMessage();
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
            return "No result";
        }
        
        @Override
        protected void onPostExecute(String result) {
            appendResult("🔍 " + result);
        }
    }
}