package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;

public class LandlordEditProfileActivity extends AppCompatActivity {

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

        initViews();
        setupListeners();
        loadProfileData();
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
        btnChangeAvatar.setOnClickListener(v -> Toast.makeText(this, "Thay đổi ảnh đại diện", Toast.LENGTH_SHORT).show());
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
        // TODO: Load actual landlord profile data from SessionManager or Database
        // For now, using placeholder data from layout (already set in XML)

        // Set initial gender (example)
        // if (landlord.getGender().equals("Nam")) {
        //    rbMale.setChecked(true);
        // } else {
        //    rbFemale.setChecked(true);
        // }
        rbMale.setChecked(true); // Default to Male for placeholder
    }

    private void saveProfile() {
        String fullName = etFullName.getText().toString();
        String phone = etPhone.getText().toString();
        String email = etEmail.getText().toString();
        String address = etAddress.getText().toString();
        String dob = etDob.getText().toString();

        int selectedGenderId = rgGender.getCheckedRadioButtonId();
        String gender = "";
        if (selectedGenderId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedGenderId);
            gender = selectedRadioButton.getText().toString();
        }

        String bankName = etBankName.getText().toString();
        String accountNumber = etAccountNumber.getText().toString();
        String accountHolderName = etAccountHolderName.getText().toString();

        // TODO: Implement actual save logic (e.g., update SessionManager, send to server)
        String message = "Lưu thay đổi hồ sơ Chủ trọ: " +
                "\nHọ tên: " + fullName +
                "\nNgày sinh: " + dob +
                "\nGiới tính: " + gender +
                "\nNgân hàng: " + bankName;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        finish();
    }
}
