package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.QuanLyPhongTro_App.R;

import java.io.IOException;

public class EditTin extends AppCompatActivity {

    private static final int REQ_PICK_IMAGE = 1001;

    private ImageButton btnBack;
    private EditText edtTieuDe, edtGia, edtMoTa;
    private LinearLayout areaPickImage;
    private TextView tvPickHint;
    private ImageView imgPreview;
    private Button btnSave;
    private CheckBox cbAc, cbWc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_edit_tin);

        //ánh xạ đến layout
        btnBack = findViewById(R.id.btn_back_edit);
        edtTieuDe = findViewById(R.id.edt_tieude);
        edtGia = findViewById(R.id.edt_gia);
        edtMoTa = findViewById(R.id.edt_mota);
        areaPickImage = findViewById(R.id.area_pick_image);
        tvPickHint = findViewById(R.id.tv_pick_hint);
        imgPreview = findViewById(R.id.img_preview);
        btnSave = findViewById(R.id.btn_save_tin);
        cbAc = findViewById(R.id.cb_ac);
        cbWc = findViewById(R.id.cb_wc);

        btnBack.setOnClickListener(v -> finish());

        areaPickImage.setOnClickListener(v -> {
            // Mở gallery chọn ảnh
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, REQ_PICK_IMAGE);
        });

        btnSave.setOnClickListener(v -> {
            String t = edtTieuDe.getText().toString().trim();
            String g = edtGia.getText().toString().trim();
            String m = edtMoTa.getText().toString().trim();

            if (t.isEmpty() || g.isEmpty()) {
                Toast.makeText(EditTin.this, "Vui lòng nhập tiêu đề và giá", Toast.LENGTH_SHORT).show();
                return;
            }

            // Ở đây bạn sẽ upload ảnh + gửi dữ liệu tới server. Tạm hiện Toast và finish.
            Toast.makeText(EditTin.this, "Đã lưu tin: " + t, Toast.LENGTH_LONG).show();
            setResult(Activity.RESULT_OK);
            finish();
        });
    }

    //Hàm chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    imgPreview.setImageBitmap(bmp);
                    imgPreview.setVisibility(View.VISIBLE);
                    tvPickHint.setVisibility(View.GONE);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Không thể đọc ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
