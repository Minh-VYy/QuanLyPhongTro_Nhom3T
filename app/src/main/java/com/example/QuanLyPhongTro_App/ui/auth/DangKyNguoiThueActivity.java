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
import com.example.QuanLyPhongTro_App.utils.AccountManager;
import com.example.QuanLyPhongTro_App.utils.SessionManager;


public class DangKyNguoiThueActivity extends AppCompatActivity {

    private EditText hoTenThue;
    private EditText emailThue;
    private EditText sdtThue;
    private EditText diaChiThue;
    private EditText matKhauDangKyThue;
    private EditText xacNhanMatKhauThue;
    private CheckBox dongYDieuKhoanThue;
    private Button btnDangKyThue;
    private TextView chuyenChuTroDangKyThue;
    private TextView dangNhapThue;
    private AccountManager accountManager;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_register);

        // Khởi tạo AccountManager và SessionManager
        accountManager = new AccountManager(this);
        sessionManager = new SessionManager(this);

        // Ẩn ActionBar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Ánh xạ từ layout
        hoTenThue = findViewById(R.id.ho_ten_thue);
        emailThue = findViewById(R.id.email_thue);
        sdtThue = findViewById(R.id.sdt_thue);
        diaChiThue = findViewById(R.id.dia_chi_thue);
        matKhauDangKyThue = findViewById(R.id.mat_khau_dang_ky_thue);
        xacNhanMatKhauThue = findViewById(R.id.xac_nhan_mat_khau_thue);
        dongYDieuKhoanThue = findViewById(R.id.dong_y_dieu_khoan_thue);
        btnDangKyThue = findViewById(R.id.btn_dang_ky_thue);
        chuyenChuTroDangKyThue = findViewById(R.id.chuyen_chu_tro_dang_ky_thue);
        dangNhapThue = findViewById(R.id.dang_nhap_thue);

        // Nút ĐĂNG KÝ → Kiểm tra dữ liệu và xử lý đăng ký
        btnDangKyThue.setOnClickListener(v -> xuLyDangKy());

        // Chuyển sang màn hình đăng ký dành cho CHỦ TRỌ
        chuyenChuTroDangKyThue.setOnClickListener(v -> {
            Intent intent = new Intent(DangKyNguoiThueActivity.this, DangKyChuTroActivity.class);
            startActivity(intent);
            finish();
        });

        // Chuyển về màn hình ĐĂNG NHẬP (nếu người dùng đã có tài khoản)
        dangNhapThue.setOnClickListener(v -> {
            Intent intent = new Intent(DangKyNguoiThueActivity.this, LoginActivity.class);
            intent.putExtra("targetRole", "tenant");
            startActivity(intent);
            finish();
        });
    }

    //Xử lý logic đăng ký người thuê
    private void xuLyDangKy() {
        String hoTen = hoTenThue.getText().toString().trim();
        String email = emailThue.getText().toString().trim();
        String sdt = sdtThue.getText().toString().trim();
        String diaChi = diaChiThue.getText().toString().trim();
        String matKhau = matKhauDangKyThue.getText().toString().trim();
        String xacNhanMatKhau = xacNhanMatKhauThue.getText().toString().trim();
        boolean dongY = dongYDieuKhoanThue.isChecked();

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

        if (diaChi.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập địa chỉ", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "Vui lòng đồng ý với Điều khoản dịch vụ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        btnDangKyThue.setEnabled(false);
        btnDangKyThue.setText("Đang đăng ký...");

        // VaiTroId: 3 = NguoiThue (tenant)
        accountManager.registerAPI(email, matKhau, hoTen, sdt, 3, new AccountManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                android.util.Log.d("DangKyNguoiThueActivity", "API register success");
                runOnUiThread(() -> {
                    Toast.makeText(DangKyNguoiThueActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                    // Lưu session
                    sessionManager.createLoginSession(email, hoTen, email, "tenant");
                    sessionManager.setLandlordStatus(false);
                    sessionManager.setDisplayRole("tenant");

                    // Chuyển về màn hình chính của người thuê
                    Intent intent = new Intent(DangKyNguoiThueActivity.this, com.example.QuanLyPhongTro_App.ui.tenant.MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                });
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("DangKyNguoiThueActivity", "❌ API register failed: " + error);
                runOnUiThread(() -> {
                    btnDangKyThue.setEnabled(true);
                    btnDangKyThue.setText("Đăng Ký");
                    Toast.makeText(DangKyNguoiThueActivity.this, "Đăng ký thất bại: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
