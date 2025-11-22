// ⚠️ Dòng này cực kỳ quan trọng, phải khớp với cấu trúc folder của bạn
package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.QuanLyPhongTro_App.R; // Import R từ package chính của bạn

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // 1. Xử lý nút Back
        ImageView btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) { // Kiểm tra null để tránh crash nếu id sai
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        // 2. Xử lý nút Lưu
        Button btnSave = findViewById(R.id.btnSave);
        if (btnSave != null) {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(EditProfileActivity.this, "Đã lưu thay đổi!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        // 3. Xử lý nút Đổi ảnh
        View btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        if (btnChangeAvatar != null) {
            btnChangeAvatar.setOnClickListener(v -> {
                Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }
    }
}