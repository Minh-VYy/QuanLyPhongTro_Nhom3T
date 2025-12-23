package com.example.QuanLyPhongTro_App.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.MainActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

public class DangKyChuTroActivity extends AppCompatActivity {

    private EditText hoTenChuTro;
    private EditText emailChuTro;
    private EditText sdtChuTro;
    private EditText matKhauDangKyChuTro;
    private EditText xacNhanMatKhauChuTro;
    private CheckBox dongYDieuKhoanChuTro;
    private Button btnDangKyChuTro;
    private TextView chuyenNguoiThueDangKyChuTro;
    private TextView dangNhapChuTro;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_register);

        sessionManager = new SessionManager(this);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Ánh xạ các view
        hoTenChuTro = findViewById(R.id.ho_ten_chu_tro);
        emailChuTro = findViewById(R.id.email_chu_tro);
        sdtChuTro = findViewById(R.id.sdt_chu_tro);
        matKhauDangKyChuTro = findViewById(R.id.mat_khau_dang_ky_chu_tro);
        xacNhanMatKhauChuTro = findViewById(R.id.xac_nhan_mat_khau_chu_tro);
        dongYDieuKhoanChuTro = findViewById(R.id.dong_y_dieu_khoan_chu_tro);
        btnDangKyChuTro = findViewById(R.id.btn_dang_ky_chu_tro);
        chuyenNguoiThueDangKyChuTro = findViewById(R.id.chuyen_nguoi_thue_dang_ky_chu_tro);
        dangNhapChuTro = findViewById(R.id.dang_nhap_chu_tro);

        // Xử lý nút Đăng Ký
        btnDangKyChuTro.setOnClickListener(v -> xuLyDangKy());

        // Xử lý Chuyển sang Người Thuê
        chuyenNguoiThueDangKyChuTro.setOnClickListener(v -> {
            Intent intent = new Intent(DangKyChuTroActivity.this, DangKyNguoiThueActivity.class);
            startActivity(intent);
            finish();
        });

        // Xử lý Đăng Nhập
        dangNhapChuTro.setOnClickListener(v -> {
            Intent intent = new Intent(DangKyChuTroActivity.this, LoginActivity.class);
            intent.putExtra("targetRole", "landlord");
            startActivity(intent);
            finish();
        });
    }

    // Hàm xử lý đăng ký
    private void xuLyDangKy() {
        String hoTen = hoTenChuTro.getText().toString().trim();
        String email = emailChuTro.getText().toString().trim();
        String sdt = sdtChuTro.getText().toString().trim();
        String matKhau = matKhauDangKyChuTro.getText().toString().trim();
        String xacNhanMatKhau = xacNhanMatKhauChuTro.getText().toString().trim();
        boolean dongY = dongYDieuKhoanChuTro.isChecked();

        // Kiểm tra dữ liệu
        if (hoTen.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập họ và tên", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!email.contains("@")) {
            Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sdt.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (sdt.length() < 10) {
            Toast.makeText(this, "Số điện thoại phải có ít nhất 10 chữ số", Toast.LENGTH_SHORT).show();
            return;
        }

        if (matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (matKhau.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!matKhau.equals(xacNhanMatKhau)) {
            Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!dongY) {
            Toast.makeText(this, "Vui lòng đồng ý với các điều khoản của ứng dụng", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Gửi dữ liệu lên server
        // Khi đăng ký Chủ trọ -> tự động có cả quyền Người thuê

        String userId = "landlord_" + System.currentTimeMillis();

        // Lưu session với role = "landlord" (có cả quyền tenant)
        sessionManager.createLoginSession(userId, hoTen, email, "landlord");

        Toast.makeText(this, "Đăng ký thành công! Tài khoản của bạn có thể dùng cho cả Người thuê và Chủ trọ.", Toast.LENGTH_LONG).show();

        // Chuyển về MainActivity (có thể chuyển giữa 2 role)
        Intent intent = new Intent(DangKyChuTroActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
