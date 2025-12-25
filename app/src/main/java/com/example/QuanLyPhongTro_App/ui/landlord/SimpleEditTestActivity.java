package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;

public class SimpleEditTestActivity extends AppCompatActivity {
    private static final String TAG = "SimpleEditTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            Log.d(TAG, "=== SIMPLE EDIT TEST ACTIVITY ===");
            
            // Tạo layout đơn giản bằng code
            setContentView(R.layout.activity_simple_edit_test);
            
            // Lấy dữ liệu từ Intent
            Intent intent = getIntent();
            String mode = intent.getStringExtra("mode");
            String phongId = intent.getStringExtra("phongId");
            String title = intent.getStringExtra("title");
            double price = intent.getDoubleExtra("price", 0);
            
            Log.d(TAG, "Mode: " + mode);
            Log.d(TAG, "PhongId: " + phongId);
            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Price: " + price);
            
            // Hiển thị thông tin
            TextView tvInfo = findViewById(R.id.tv_info);
            EditText edtTitle = findViewById(R.id.edt_title);
            EditText edtPrice = findViewById(R.id.edt_price);
            Button btnSave = findViewById(R.id.btn_save);
            Button btnBack = findViewById(R.id.btn_back);
            
            tvInfo.setText("Mode: " + mode + "\nPhongId: " + phongId);
            
            if (title != null) {
                edtTitle.setText(title);
            }
            if (price > 0) {
                edtPrice.setText(String.valueOf((long)price));
            }
            
            btnBack.setOnClickListener(v -> finish());
            
            btnSave.setOnClickListener(v -> {
                String newTitle = edtTitle.getText().toString();
                String newPrice = edtPrice.getText().toString();
                
                Toast.makeText(this, 
                    "Sẽ lưu: " + newTitle + " - " + newPrice + " VNĐ", 
                    Toast.LENGTH_LONG).show();
                
                // Trả kết quả về
                setResult(RESULT_OK);
                finish();
            });
            
            Toast.makeText(this, "✅ Test activity loaded successfully!", Toast.LENGTH_SHORT).show();
            
        } catch (Exception e) {
            Log.e(TAG, "Error in SimpleEditTestActivity: " + e.getMessage(), e);
            Toast.makeText(this, "❌ Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }
}