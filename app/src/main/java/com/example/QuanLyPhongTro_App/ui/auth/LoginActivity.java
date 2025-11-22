// app/src/main/java/com/example/QuanLyPhongTro_App/ui/auth/LoginActivity.java
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
    private TextView txtForgotPassword, txtGotoRegister;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        edtEmail = findViewById(R.id.edt_email_login);
        edtPassword = findViewById(R.id.edt_password_login);
        btnLogin = findViewById(R.id.btn_login);
        txtForgotPassword = findViewById(R.id.txt_forgot_password);
        txtGotoRegister = findViewById(R.id.txt_goto_register);
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

                // Password complexity validation should be performed server-side.
        // The client only checks for minimum length for user experience.
        // TODO: Gọi API đăng nhập thực tế
        // Database sẽ trả về thông tin user và role (tenant/landlord)
        // Giả lập đăng nhập thành công
                loginSuccess(emailPhone, "user123", "Nguyễn Văn A", "tenant");
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
}
