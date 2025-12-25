package com.example.QuanLyPhongTro_App.ui.auth;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.landlord.Landlord;
import com.example.QuanLyPhongTro_App.ui.landlord.LandlordDao;
import com.example.QuanLyPhongTro_App.ui.tenant.MainActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.Tenant;
import com.example.QuanLyPhongTro_App.ui.tenant.TenantDao;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edtEmail, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, txtGotoRegister, txtTargetRole;
    private SessionManager sessionManager;
    private String targetRole = "tenant";

    // --- Database Credentials --- 
    private static final String IP = "172.26.98.220";  // Fixed IP to match computer
    private static final String PORT = "1433";
    private static final String DATABASE = "QuanLyPhongTro";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "12345";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sessionManager = new SessionManager(this);
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
        txtForgotPassword.setOnClickListener(v -> Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show());
        txtGotoRegister.setOnClickListener(v -> showRegisterRoleDialog());
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Vui lòng nhập email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // Disable login button to prevent multiple clicks
        btnLogin.setEnabled(false);
        btnLogin.setText("Đang đăng nhập...");

        if ("landlord".equals(targetRole)) {
            new LandlordLoginTask().execute(email, password);
        } else {
            new TenantLoginTask().execute(email, password);
        }
    }

    private class LandlordLoginTask extends AsyncTask<String, Void, Landlord> {
        private String errorMsg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Optional: Show a progress bar
        }

        @Override
        protected Landlord doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            Connection connection = null;
            Landlord landlord = null;

            // --- IMPORTANT DEBUG LOGGING ---
            Log.d("LandlordLoginTask", "Attempting to log in with:");
            Log.d("LandlordLoginTask", "Email: [" + email + "]");
            Log.d("LandlordLoginTask", "PasswordHash: [" + password + "] (This should match DB value)");

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String url = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE;
                connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

                if (connection != null) {
                    LandlordDao landlordDao = new LandlordDao();
                    landlord = landlordDao.login(connection, email, password);
                }

            } catch (ClassNotFoundException | SQLException e) {
                errorMsg = e.getMessage();
                Log.e("LandlordLoginTask", "Login Error: " + errorMsg, e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e("LandlordLoginTask", "Error closing connection", e);
                    }
                }
            }
            return landlord;
        }

        @Override
        protected void onPostExecute(Landlord landlord) {
            super.onPostExecute(landlord);
            // Re-enable login button
            btnLogin.setEnabled(true);
            btnLogin.setText("Đăng Nhập");

            if (landlord != null) {
                // Login successful
                loginSuccessLandlord(landlord.getEmail(), landlord.getNguoiDungId(), landlord.getHoTen(), "landlord");
            } else {
                // Login failed
                if (errorMsg != null) {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + errorMsg, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private class TenantLoginTask extends AsyncTask<String, Void, Tenant> {
        private String errorMsg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Optional: Show a progress bar
        }

        @Override
        protected Tenant doInBackground(String... params) {
            String email = params[0];
            String password = params[1];
            Connection connection = null;
            Tenant tenant = null;

            Log.d("TenantLoginTask", "Attempting tenant login with:");
            Log.d("TenantLoginTask", "Email: [" + email + "]");

            try {
                Class.forName("net.sourceforge.jtds.jdbc.Driver");
                String url = "jdbc:jtds:sqlserver://" + IP + ":" + PORT + "/" + DATABASE;
                connection = DriverManager.getConnection(url, USERNAME, PASSWORD);

                if (connection != null) {
                    TenantDao tenantDao = new TenantDao();
                    tenant = tenantDao.login(connection, email, password);
                    
                    // Update last login time if successful
                    if (tenant != null) {
                        tenantDao.updateLastLogin(connection, tenant.getNguoiDungId());
                    }
                }

            } catch (ClassNotFoundException | SQLException e) {
                errorMsg = e.getMessage();
                Log.e("TenantLoginTask", "Login Error: " + errorMsg, e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e("TenantLoginTask", "Error closing connection", e);
                    }
                }
            }
            return tenant;
        }

        @Override
        protected void onPostExecute(Tenant tenant) {
            super.onPostExecute(tenant);
            // Re-enable login button
            btnLogin.setEnabled(true);
            btnLogin.setText("Đăng Nhập");

            if (tenant != null) {
                // Login successful
                loginSuccessTenant(tenant.getEmail(), tenant.getNguoiDungId(), tenant.getHoTen(), "tenant");
            } else {
                // Login failed
                if (errorMsg != null) {
                    Toast.makeText(LoginActivity.this, "Lỗi kết nối: " + errorMsg, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Tên đăng nhập hoặc mật khẩu không đúng.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void loginSuccessLandlord(String email, String userId, String userName, String userType) {
        sessionManager.createLoginSession(userId, userName, email, userType);
        sessionManager.setLandlordStatus(true);
        sessionManager.setDisplayRole(userType);

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void loginSuccessTenant(String email, String userId, String userName, String userType) {
        sessionManager.createLoginSession(userId, userName, email, userType);
        sessionManager.setLandlordStatus(false);
        sessionManager.setDisplayRole(userType);

        Toast.makeText(this, "Chào mừng " + userName + "!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);
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

    private void updateRoleUI() {
        if (txtTargetRole != null) {
            String roleText = targetRole.equals("landlord") ? "Đăng nhập với vai trò: Chủ Trọ" : "Đăng nhập với vai trò: Người Thuê";
            txtTargetRole.setText(roleText);
        }
    }
}
