package com.example.QuanLyPhongTro_App.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.MainActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, txtGotoRegister, txtTargetRole;
    private SessionManager sessionManager;
    private String targetRole = "tenant"; // Mặc định là người thuê

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        // Lấy targetRole từ Intent (nếu có)
        if (getIntent().hasExtra("targetRole")) {
            targetRole = getIntent().getStringExtra("targetRole");
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupListeners();
        updateRoleUI();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edt_email_login);
        edtPassword = findViewById(R.id.edt_password_login);
        btnLogin = findViewById(R.id.btn_login);
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtGotoRegister = findViewById(R.id.txt_goto_register);
        txtTargetRole = findViewById(R.id.txt_target_role);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> handleLogin());

        txtForgotPassword.setOnClickListener(v ->
                Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        );

        txtGotoRegister.setOnClickListener(v -> showRegisterRoleDialog());
    }

    private void handleLogin() {
        String emailPhone = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(emailPhone)) {
            Toast.makeText(this, "Vui lòng nhập email hoặc số điện thoại", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 8) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 8 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Gọi API đăng nhập thực tế
        // Database sẽ trả về thông tin user và role (tenant/landlord)

        // DEMO ACCOUNT:
        // Người thuê: tenant@demo.com / password123
        // Chủ trọ: landlord@demo.com / password123

        if (emailPhone.equals("tenant@demo.com") && password.equals("password123")) {
            loginSuccess(emailPhone, "tenant_001", "Nguyễn Văn A", "tenant");
        } else if (emailPhone.equals("landlord@demo.com") && password.equals("password123")) {
            loginSuccess(emailPhone, "landlord_001", "Trần Thị B", "landlord");
        } else {
            // Giả lập đăng nhập theo targetRole cho demo
            String userName = targetRole.equals("landlord") ? "Chủ Trọ Demo" : "Người Thuê Demo";
            String userId = targetRole.equals("landlord") ? "landlord_demo" : "tenant_demo";
            loginSuccess(emailPhone, userId, userName, targetRole);
        }
    }

    private void loginSuccess(String email, String userId, String userName, String userType) {
        // Lưu session - database sẽ xác định role
        sessionManager.createLoginSession(userId, userName, email, userType);
        boolean isLandlord = "landlord".equals(userType);
        sessionManager.setLandlordStatus(isLandlord);
        sessionManager.setDisplayRole(userType);

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

        Intent intent;
        if (isLandlord) {
            intent = new Intent(this, com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void showRegisterRoleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn Vai Trò Đăng Ký");
        builder.setItems(new String[]{"Người Thuê Trọ", "Chủ Trọ / Người Cho Thuê"}, (dialog, which) -> {
            if (which == 0) {
                startActivity(new Intent(LoginActivity.this, DangKyNguoiThueActivity.class));
            } else {
                startActivity(new Intent(LoginActivity.this, DangKyChuTroActivity.class));
            }
        });
        builder.show();
    }

    /**
     * Cập nhật UI hiển thị role đang đăng nhập
     */
    private void updateRoleUI() {
        if (txtTargetRole != null) {
            String roleText = targetRole.equals("landlord") ? "Đăng nhập với vai trò: Chủ Trọ" : "Đăng nhập với vai trò: Người Thuê";
            txtTargetRole.setText(roleText);
        }
    }
}
