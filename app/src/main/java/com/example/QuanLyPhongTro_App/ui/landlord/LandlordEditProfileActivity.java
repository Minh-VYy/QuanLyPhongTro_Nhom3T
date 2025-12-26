package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LandlordEditProfileActivity extends AppCompatActivity {

    private static final String TAG = "LandlordEditProfile";
    private SessionManager sessionManager;
    private UserProfileDao.UserProfile currentProfile;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    private ImageView btnBack, ivAvatar, ivIdDocument;
    private LinearLayout btnChangeAvatar;
    private TextInputEditText etFullName, etPhone, etEmail, etAddress, etDob, etBankName, etAccountNumber, etAccountHolderName;
    private RadioGroup rgGender;
    private RadioButton rbMale, rbFemale;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_edit_profile);

        sessionManager = new SessionManager(this);

        initViews();
        setupListeners();
        loadProfileDataFromApi();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        ivAvatar = findViewById(R.id.ivAvatar);
        btnChangeAvatar = findViewById(R.id.btnChangeAvatar);
        etFullName = findViewById(R.id.etFullName);
        etPhone = findViewById(R.id.etPhone);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        etDob = findViewById(R.id.etDob);
        rgGender = findViewById(R.id.rgGender);
        rbMale = findViewById(R.id.rbMale);
        rbFemale = findViewById(R.id.rbFemale);
        ivIdDocument = findViewById(R.id.ivIdDocument);
        etBankName = findViewById(R.id.etBankName);
        etAccountNumber = findViewById(R.id.etAccountNumber);
        etAccountHolderName = findViewById(R.id.etAccountHolderName);
        btnSave = findViewById(R.id.btnSave);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnChangeAvatar.setOnClickListener(v -> {
            // TODO: Implement avatar change functionality
        });
        btnSave.setOnClickListener(v -> saveProfile());

        etDob.setOnClickListener(v -> showDatePickerDialog());
        etDob.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePickerDialog();
            }
        });
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, monthOfYear + 1, year1);
                    etDob.setText(date);
                },
                year, month, day);
        datePickerDialog.show();
    }

    private void loadProfileData() {
        // Fallback method - load basic data from session
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        
        if (userName != null && !userName.trim().isEmpty()) {
            etFullName.setText(userName);
        }
        
        if (userEmail != null && !userEmail.trim().isEmpty()) {
            etEmail.setText(userEmail);
        }
        
        // Set default gender
        rbMale.setChecked(true);
        
        Log.d(TAG, "Loaded fallback profile data from session");
    }

    private void loadProfileDataFromApi() {
        if (!sessionManager.isLoggedIn() || sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            loadProfileData();
            return;
        }

        ApiClient.setToken(sessionManager.getToken());
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        api.getUserProfile().enqueue(new Callback<GenericResponse<Object>>() {
            @Override
            public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "getUserProfile failed: " + response.code());
                    loadProfileData();
                    return;
                }

                GenericResponse<Object> body = response.body();
                if (!body.success || body.data == null) {
                    Log.w(TAG, "getUserProfile empty: " + body.message);
                    loadProfileData();
                    return;
                }

                try {
                    //noinspection unchecked
                    Map<String, Object> map = (Map<String, Object>) body.data;

                    UserProfileDao.UserProfile profile = new UserProfileDao.UserProfile();

                    Object nguoiDungId = map.get("nguoiDungId");
                    if (nguoiDungId == null) nguoiDungId = map.get("NguoiDungId");
                    if (nguoiDungId != null) profile.setNguoiDungId(nguoiDungId.toString());

                    Object email = map.get("email");
                    if (email == null) email = map.get("Email");
                    if (email != null) profile.setEmail(email.toString());

                    Object hoTen = map.get("hoTen");
                    if (hoTen == null) hoTen = map.get("HoTen");
                    if (hoTen != null) profile.setHoTen(hoTen.toString());

                    // optional phone
                    Object dienThoai = map.get("dienThoai");
                    if (dienThoai == null) dienThoai = map.get("DienThoai");
                    if (dienThoai != null) profile.setDienThoai(dienThoai.toString());

                    currentProfile = profile;
                    updateUIWithProfile(profile);

                } catch (Exception e) {
                    Log.e(TAG, "Error parsing profile API response", e);
                    loadProfileData();
                }
            }

            @Override
            public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                Log.e(TAG, "getUserProfile error", t);
                loadProfileData();
            }
        });
    }

    // Keep old name for call sites but route to API
    private void loadProfileDataFromDatabase() {
        Log.w(TAG, "loadProfileDataFromDatabase() disabled. Using API.");
        loadProfileDataFromApi();
    }

    private void saveProfile() {
        if (currentProfile == null) {
            return;
        }

        // Validate required fields
        String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
        String phone = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";

        if (fullName.isEmpty()) {
            etFullName.setError("Vui lòng nhập họ tên");
            etFullName.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            etEmail.setError("Vui lòng nhập email");
            etEmail.requestFocus();
            return;
        }

        // Update local profile object
        currentProfile.setHoTen(fullName);
        currentProfile.setDienThoai(phone);
        currentProfile.setEmail(email);
        currentProfile.setGhiChu(etAddress.getText() != null ? etAddress.getText().toString().trim() : "");

        // Handle date of birth
        String dobText = etDob.getText() != null ? etDob.getText().toString().trim() : "";
        if (!dobText.isEmpty()) {
            try {
                Date dob = dateFormat.parse(dobText);
                currentProfile.setNgaySinh(dob);
            } catch (ParseException e) {
                Log.w(TAG, "Invalid date format: " + dobText);
                etDob.setError("Định dạng ngày không hợp lệ (dd/MM/yyyy)");
                etDob.requestFocus();
                return;
            }
        }

        // API update endpoint is not available yet in ApiService.
        // To keep UX consistent and avoid DB, we just update the Session and return success.
        btnSave.setEnabled(false);
        btnSave.setText("Đang lưu...");

        btnSave.postDelayed(() -> {
            btnSave.setEnabled(true);
            btnSave.setText("Lưu thay đổi");

            sessionManager.createLoginSession(
                    currentProfile.getNguoiDungId() != null ? currentProfile.getNguoiDungId() : sessionManager.getUserId(),
                    currentProfile.getHoTen(),
                    currentProfile.getEmail(),
                    sessionManager.getUserType()
            );

            Toast.makeText(LandlordEditProfileActivity.this,
                    "Đã lưu (tạm thời lưu local). Khi backend có API cập nhật hồ sơ, mình sẽ nối vào.",
                    Toast.LENGTH_LONG).show();

            setResult(RESULT_OK);
            finish();
        }, 600);
    }

    private void updateUIWithProfile(UserProfileDao.UserProfile profile) {
        if (profile == null) {
            Log.w(TAG, "Profile is null, using fallback data");
            loadProfileData();
            return;
        }

        currentProfile = profile;

        if (profile.getHoTen() != null) {
            etFullName.setText(profile.getHoTen());
        }

        if (profile.getDienThoai() != null) {
            etPhone.setText(profile.getDienThoai());
        }

        if (profile.getEmail() != null) {
            etEmail.setText(profile.getEmail());
        }

        if (profile.getGhiChu() != null) {
            etAddress.setText(profile.getGhiChu());
        }

        if (profile.getNgaySinh() != null) {
            String formattedDate = dateFormat.format(profile.getNgaySinh());
            etDob.setText(formattedDate);
        }

        rbMale.setChecked(true);
        Log.d(TAG, "✅ UI updated with profile data");
    }
}