package com.example.QuanLyPhongTro_App.ui.tenant;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.DatPhongDao;
import com.example.QuanLyPhongTro_App.data.dao.PhongDao;
import com.example.QuanLyPhongTro_App.data.model.DatPhong;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.List;
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
    private Room currentRoom;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_booking_create);

        // Khởi tạo SessionManager
        sessionManager = new SessionManager(this);

        // Lấy Room từ Intent
        currentRoom = (Room) getIntent().getSerializableExtra("room");

        initViews();
        setupToolbar();
        setupListeners();
        loadRoomData();
        loadUserData();
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
        LinearLayout toolbarLayout = findViewById(R.id.toolbar);
        ImageView btnBack = toolbarLayout.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
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
        if (currentRoom != null) {
            roomTitle.setText(currentRoom.getTitle());
            roomPrice.setText(currentRoom.getFormattedPrice());
            roomAddress.setText(currentRoom.getLocation());

            // Load image nếu có
            if (currentRoom.getImageResId() != 0) {
                roomThumbnail.setImageResource(currentRoom.getImageResId());
            }
        } else {
            // Fallback nếu không có room data
            roomTitle.setText("Phòng trọ cao cấp");
            roomPrice.setText("2.5 triệu/tháng");
            roomAddress.setText("Quận 1, TP.HCM");
        }
    }

    /**
     * Tự động điền thông tin người dùng đã đăng nhập
     */
    private void loadUserData() {
        if (sessionManager.isLoggedIn()) {
            String userName = sessionManager.getUserName();
            String userEmail = sessionManager.getUserEmail();

            if (userName != null && !userName.isEmpty()) {
                inputFullName.setText(userName);
            }

            // TODO: Load phone từ database user profile
            // Tạm thời để trống cho người dùng nhập
        }
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

        if (phone.length() < 10) {
            inputPhone.setError("Số điện thoại không hợp lệ");
            inputPhone.requestFocus();
            return;
        }

        if (selectedTimeSlot.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn khung giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        // Kiểm tra đăng nhập
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt lịch", Toast.LENGTH_SHORT).show();
            return;
        }

        // Format date
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String bookingDate = sdf.format(selectedDate.getTime());

        // Lưu booking vào database
        saveBookingToDatabase(fullName, phone, note, allowCall, bookingDate);
    }

    private void saveBookingToDatabase(String fullName, String phone, String note, 
                                       boolean allowCall, String bookingDate) {
        // Hiển thị loading
        btnConfirmBooking.setEnabled(false);
        btnConfirmBooking.setText("Đang xử lý...");

        new Thread(() -> {
            Connection conn = null;
            boolean success = false;
            String errorMessage = "";

            try {
                // Kết nối database
                conn = DatabaseHelper.getConnection();
                
                // Tạo đối tượng DatPhong
                DatPhong datPhong = new DatPhong();
                
                // Lấy PhongId từ currentRoom
                String phongId = null;
                String chuTroId = null;
                
                if (currentRoom != null && currentRoom.getPhongId() != null) {
                    phongId = currentRoom.getPhongId();
                    
                    // Lấy ChuTroId từ database dựa trên PhongId
                    String getChuTroQuery = "SELECT nt.ChuTroId FROM Phong p " +
                                          "INNER JOIN NhaTro nt ON p.NhaTroId = nt.NhaTroId " +
                                          "WHERE p.PhongId = ?";
                    
                    try (PreparedStatement stmt = conn.prepareStatement(getChuTroQuery)) {
                        stmt.setString(1, phongId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                chuTroId = rs.getString("ChuTroId");
                            }
                        }
                    }
                    
                    if (chuTroId == null) {
                        final String finalError = "Không tìm thấy thông tin chủ trọ. Vui lòng thử lại.";
                        runOnUiThread(() -> {
                            Toast.makeText(this, finalError, Toast.LENGTH_LONG).show();
                            btnConfirmBooking.setEnabled(true);
                            btnConfirmBooking.setText("Xác nhận đặt lịch");
                        });
                        return;
                    }
                } else {
                    final String finalError = "Không tìm thấy thông tin phòng. Vui lòng chọn phòng từ danh sách.";
                    runOnUiThread(() -> {
                        Toast.makeText(this, finalError, Toast.LENGTH_LONG).show();
                        btnConfirmBooking.setEnabled(true);
                        btnConfirmBooking.setText("Xác nhận đặt lịch");
                    });
                    return;
                }
                
                datPhong.setPhongId(phongId);
                datPhong.setNguoiThueId(sessionManager.getUserId());
                datPhong.setChuTroId(chuTroId); // *** QUAN TRỌNG: Set ChuTroId ***
                datPhong.setLoai("Xem phòng"); // Loại đặt lịch
                
                // Set thời gian bắt đầu (ngày + khung giờ)
                Calendar startTime = (Calendar) selectedDate.clone();
                if (selectedTimeSlot.contains("Sáng")) {
                    startTime.set(Calendar.HOUR_OF_DAY, 8);
                } else if (selectedTimeSlot.contains("Chiều")) {
                    startTime.set(Calendar.HOUR_OF_DAY, 13);
                } else if (selectedTimeSlot.contains("Tối")) {
                    startTime.set(Calendar.HOUR_OF_DAY, 18);
                }
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.SECOND, 0);
                
                datPhong.setBatDau(startTime.getTime());
                
                // Set thời gian kết thúc (2 giờ sau)
                Calendar endTime = (Calendar) startTime.clone();
                endTime.add(Calendar.HOUR_OF_DAY, 2);
                datPhong.setKetThuc(endTime.getTime());
                
                // Trạng thái: 1 = Chờ duyệt
                datPhong.setTrangThaiId(1);
                
                // Ghi chú (bao gồm thông tin liên hệ)
                String fullNote = "Họ tên: " + fullName + "\n" +
                                 "SĐT: " + phone + "\n" +
                                 "Khung giờ: " + selectedTimeSlot + "\n" +
                                 "Cho phép gọi: " + (allowCall ? "Có" : "Không");
                if (note != null && !note.isEmpty()) {
                    fullNote += "\nGhi ch��: " + note;
                }
                datPhong.setGhiChu(fullNote);
                
                // Lưu vào database
                DatPhongDao dao = new DatPhongDao();
                success = dao.createDatPhong(conn, datPhong);
                
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "Lỗi kết nối: " + e.getMessage();
            } finally {
                if (conn != null) {
                    DatabaseHelper.releaseConnection(conn);
                }
            }

            // Cập nhật UI
            final boolean finalSuccess = success;
            final String finalErrorMessage = errorMessage;
            
            runOnUiThread(() -> {
                btnConfirmBooking.setEnabled(true);
                btnConfirmBooking.setText("Xác nhận đặt lịch");

                if (finalSuccess) {
                    String message = "Đặt lịch xem phòng thành công!\n" +
                            "Ngày: " + bookingDate + "\n" +
                            "Khung giờ: " + selectedTimeSlot + "\n" +
                            "Chủ trọ sẽ liên hệ với bạn sớm";

                    new android.app.AlertDialog.Builder(this)
                            .setTitle("Đặt lịch thành công")
                            .setMessage(message)
                            .setPositiveButton("OK", (dialog, which) -> {
                                setResult(RESULT_OK);
                                finish();
                            })
                            .setCancelable(false)
                            .show();
                } else {
                    Toast.makeText(this, 
                        "Đặt lịch thất bại: " + (finalErrorMessage.isEmpty() ? "Vui lòng thử lại" : finalErrorMessage), 
                        Toast.LENGTH_LONG).show();
                }
            });
        }).start();
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

