package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;


public class TroGiup extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvXemThem;
    private LinearLayout rowChat, rowCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_help);

        // Ánh xạ các view từ layout
        btnBack = findViewById(R.id.btn_back_trogiup);
        tvXemThem = findViewById(R.id.tv_xem_them);
        rowChat = findViewById(R.id.row_chat);
        rowCall = findViewById(R.id.row_call);

        // --- XỬ LÝ NÚT BACK (QUAY LẠI) ---
        btnBack.setOnClickListener(v -> {
            // Kết thúc Activity hiện tại để quay về màn hình trước đó
            finish();
        });

        // Xử lý sự kiện bấm "Xem thêm"
        tvXemThem.setOnClickListener(v ->
                Toast.makeText(this, "Mở danh sách câu hỏi đầy đủ (chưa implement)", Toast.LENGTH_SHORT).show()
        );

        // Xử lý sự kiện bấm vào "Trò chuyện ngay"
        rowChat.setOnClickListener(v -> {
            // Ví dụ mở một activity chat (chưa có), tạm show toast
            Toast.makeText(this, "Mở trò chuyện (chưa implement)", Toast.LENGTH_SHORT).show();
        });

        // Xử lý sự kiện bấm vào "Gọi tổng đài"
        rowCall.setOnClickListener(v -> {
            // Chuyển sang màn hình quay số với số điện thoại định sẵn
            String phone = "tel:0905123456";
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse(phone));
            startActivity(intent);
        });
    }
}
