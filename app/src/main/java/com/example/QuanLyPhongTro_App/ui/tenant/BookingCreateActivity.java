package com.example.QuanLyPhongTro_App.ui.tenant;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.QuanLyPhongTro_App.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingCreateActivity extends AppCompatActivity {

    private ImageView roomThumbnail;
    private TextView roomTitle, roomPrice, roomAddress;
    private Button btnSelectDate, btnConfirmBooking;
    private ChipGroup timeSlotChipGroup;
    private TextInputEditText inputFullName, inputPhone, inputNote;
    private SwitchMaterial switchAllowCall;

    private Calendar selectedDate;
    private String selectedTimeSlot = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_booking_create);

        initViews();
        setupToolbar();
        setupListeners();
        loadRoomData();
    }

    private void initViews() {
        roomThumbnail = findViewById(R.id.roomThumbnail);
        roomTitle = findViewById(R.id.roomTitle);
        roomPrice = findViewById(R.id.roomPrice);
        roomAddress = findViewById(R.id.roomAddress);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);
        timeSlotChipGroup = findViewById(R.id.timeSlotChipGroup);
        inputFullName = findViewById(R.id.inputFullName);
        inputPhone = findViewById(R.id.inputPhone);
        inputNote = findViewById(R.id.inputNote);
        switchAllowCall = findViewById(R.id.switchAllowCall);

        selectedDate = Calendar.getInstance();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> showDatePicker());

        timeSlotChipGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chipMorning) {
                selectedTimeSlot = "Sáng (8-12h)";
            } else if (checkedId == R.id.chipAfternoon) {
                selectedTimeSlot = "Chiều (13-17h)";
            } else if (checkedId == R.id.chipEvening) {
                selectedTimeSlot = "Tối (18-20h)";
            }
        });

        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, year, month, dayOfMonth) -> {
                selectedDate.set(year, month, dayOfMonth);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                btnSelectDate.setText(sdf.format(selectedDate.getTime()));
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );

        // Set minimum date to today
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    private void loadRoomData() {
        // TODO: Load room data from intent or database
        // For now, using placeholder data
        roomTitle.setText("Phòng trọ cao cấp");
        roomPrice.setText("2.5 triệu/tháng");
        roomAddress.setText("Quận 1, TP.HCM");
    }

    private void confirmBooking() {
        String fullName = inputFullName.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();
        String note = inputNote.getText().toString().trim();
        boolean allowCall = switchAllowCall.isChecked();

        // Validation
        if (fullName.isEmpty()) {
            inputFullName.setError("Vui lòng nhập họ tên");
            inputFullName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            inputPhone.setError("Vui lòng nhập số điện thoại");
            inputPhone.requestFocus();
            return;
        }

        if (selectedTimeSlot.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khung giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Save booking to database
        Toast.makeText(this, "Đặt lịch thành công!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

