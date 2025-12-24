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
    private static final String IP = "192.168.0.117";  // Fixed IP to match computer
    private static final String PORT = "1433";
    private static final String DATABASE = "QuanLyPhongTro";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "27012005";

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

        if ("landlord".equals(targetRole)) {
            new LoginTask().execute(email, password);
        } else {
            Toast.makeText(this, "Chức năng đăng nhập cho người thuê chưa được triển khai.", Toast.LENGTH_LONG).show();
        }
    }

    private class LoginTask extends AsyncTask<String, Void, Landlord> {
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
            Log.d("LoginTask", "Attempting to log in with:");
            Log.d("LoginTask", "Email: [" + email + "]");
            Log.d("LoginTask", "PasswordHash: [" + password + "] (This should match DB value)");

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
                Log.e("LoginTask", "Login Error: " + errorMsg, e);
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        Log.e("LoginTask", "Error closing connection", e);
                    }
                }
            }
            return landlord;
        }

        @Override
        protected void onPostExecute(Landlord landlord) {
            super.onPostExecute(landlord);
            // Optional: Hide the progress bar

            if (landlord != null) {
                // Login successful
                loginSuccess(landlord.getEmail(), landlord.getNguoiDungId(), landlord.getHoTen(), "landlord");
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

    private void loginSuccess(String email, String userId, String userName, String userType) {
        sessionManager.createLoginSession(userId, userName, email, userType);
        sessionManager.setLandlordStatus(true);
        sessionManager.setDisplayRole(userType);

        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, com.example.QuanLyPhongTro_App.ui.landlord.LandlordHomeActivity.class);
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
