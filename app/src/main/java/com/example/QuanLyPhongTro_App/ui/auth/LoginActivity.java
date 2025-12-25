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
import com.example.QuanLyPhongTro_App.utils.AccountManager;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, txtGotoRegister, txtTargetRole;
    private SessionManager sessionManager;
    private AccountManager accountManager;
    private String targetRole = "tenant"; // Mặc định là người thuê

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sessionManager = new SessionManager(this);
        accountManager = new AccountManager(this);

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

        android.util.Log.d("LoginActivity", "Password length: " + password.length());

        if (password.length() < 6) {
            Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show loading
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang xác thực...");

        // Call API login ONLY - NO FALLBACK
        android.util.Log.d("LoginActivity", "Calling API login with email: " + emailPhone);
        accountManager.loginAPI(emailPhone, password, new AccountManager.AuthCallback() {
            @Override
            public void onSuccess(String message) {
                android.util.Log.d("LoginActivity", "✅ API login success: " + message);
                // API login successful, get user info from session
                SessionManager sm = new SessionManager(LoginActivity.this);
                String token = sm.getToken();
                if (token != null && !token.isEmpty()) {
                    android.util.Log.d("LoginActivity", "Token saved: " + token.substring(0, Math.min(20, token.length())) + "...");

                    // Get user info from session (saved in AccountManager.loginAPI)
                    String userId = sm.getUserId();
                    String userName = sm.getUserName();
                    String userEmail = sm.getUserEmail();
                    String userRole = sm.getUserRole();

                    android.util.Log.d("LoginActivity", "User role from session: " + userRole);

                    // Login successful with correct role
                    runOnUiThread(() -> loginSuccess(userEmail, userId, userName, userRole));
                } else {
                    runOnUiThread(() -> {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("Đăng Nhập");
                        Toast.makeText(LoginActivity.this, "❌ Lỗi: Token không được lưu", Toast.LENGTH_SHORT).show();
                    });
                }
            }

            @Override
            public void onError(String error) {
                android.util.Log.e("LoginActivity", "❌ API login FAILED: " + error);
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    btnLogin.setText("Đăng Nhập");
                    // Show full error message from API
                    Toast.makeText(LoginActivity.this, "❌ Đăng nhập thất bại:\n" + error, Toast.LENGTH_LONG).show();
                });
            }
        });
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
