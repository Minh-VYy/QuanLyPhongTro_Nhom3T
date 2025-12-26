package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.repository.BookingRepository;
import com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class BookingDetailActivity extends AppCompatActivity {

    private static final String TAG = "BookingDetail";
    public static final String EXTRA_BOOKING_ID = "booking_id";

    private TextView tvStatus, tvRoomName, tvRoomPrice, tvRoomAddress;
    private TextView tvBookingDate, tvBookingTime, tvContactInfo, tvNote, tvCreatedTime;
    private LinearLayout layoutContactInfo, layoutNote, layoutButtons;
    private Button btnCancel, btnContact;

    private String bookingId;
    private BookingRepository bookingRepository;
    private SessionManager sessionManager;
    private MyBookingsResponse.MyBookingDto bookingDto;
    private String landlordPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_booking_detail);
            Log.d(TAG, "=== BOOKING DETAIL ACTIVITY ===");

            bookingId = getIntent().getStringExtra(EXTRA_BOOKING_ID);
            Log.d(TAG, "Booking ID: " + bookingId);

            if (bookingId == null || bookingId.isEmpty()) {
                Log.e(TAG, "Booking ID is null or empty");
                Toast.makeText(this, "Không tìm thấy thông tin đặt lịch", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            sessionManager = new SessionManager(this);
            bookingRepository = new BookingRepository();

            initViews();
            setupToolbar();
            loadBookingDetail();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            tvStatus = findViewById(R.id.tvStatus);
            tvRoomName = findViewById(R.id.tvRoomName);
            tvRoomPrice = findViewById(R.id.tvRoomPrice);
            tvRoomAddress = findViewById(R.id.tvRoomAddress);
            tvBookingDate = findViewById(R.id.tvBookingDate);
            tvBookingTime = findViewById(R.id.tvBookingTime);
            tvContactInfo = findViewById(R.id.tvContactInfo);
            tvNote = findViewById(R.id.tvNote);
            tvCreatedTime = findViewById(R.id.tvCreatedTime);
            layoutContactInfo = findViewById(R.id.layoutContactInfo);
            layoutNote = findViewById(R.id.layoutNote);
            layoutButtons = findViewById(R.id.layoutButtons);
            btnCancel = findViewById(R.id.btnCancel);
            btnContact = findViewById(R.id.btnContact);

            btnCancel.setOnClickListener(v -> showCancelDialog());
            btnContact.setOnClickListener(v -> contactLandlord());

            Log.d(TAG, "✅ All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing views: " + e.getMessage(), e);
            throw e;
        }
    }

    private void setupToolbar() {
        try {
            LinearLayout toolbar = findViewById(R.id.toolbar);
            TextView tvTitle = toolbar.findViewById(R.id.tvTitle);
            ImageView btnBack = toolbar.findViewById(R.id.btnBack);

            tvTitle.setText("Chi tiết đặt lịch");
            btnBack.setOnClickListener(v -> finish());

            Log.d(TAG, "✅ Toolbar setup successfully");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting up toolbar: " + e.getMessage(), e);
            throw e;
        }
    }

    private void loadBookingDetail() {
        Log.d(TAG, "Loading booking detail for ID: " + bookingId);

        if (!sessionManager.isLoggedIn() || sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        ApiClient.setToken(sessionManager.getToken());

        bookingRepository.getBookingDetailById(bookingId, new BookingRepository.BookingDetailCallback() {
            @Override
            public void onSuccess(MyBookingsResponse.MyBookingDto booking) {
                bookingDto = booking;
                runOnUiThread(() -> displayBookingInfo());
            }

            @Override
            public void onNotFound() {
                runOnUiThread(() -> {
                    Toast.makeText(BookingDetailActivity.this, "Không tìm thấy thông tin đặt lịch", Toast.LENGTH_SHORT).show();
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(BookingDetailActivity.this, "Lỗi tải dữ liệu: " + message, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void displayBookingInfo() {
        try {
            if (bookingDto == null) {
                Toast.makeText(this, "Không có dữ liệu đặt lịch", Toast.LENGTH_SHORT).show();
                return;
            }

            Integer trangThaiIdObj = bookingDto.trangThaiId;
            int trangThaiId = trangThaiIdObj != null ? trangThaiIdObj : 1;

            String status = getStatusText(trangThaiId);
            int statusColor = getStatusColor(trangThaiId);
            tvStatus.setText(status);
            tvStatus.setTextColor(getResources().getColor(statusColor));

            tvRoomName.setText(bookingDto.tenPhong != null ? bookingDto.tenPhong : "Phòng trọ");
            long giaPhong = bookingDto.giaPhong != null ? bookingDto.giaPhong : 0L;
            tvRoomPrice.setText(formatPrice(giaPhong));
            tvRoomAddress.setText(bookingDto.diaChiPhong != null ? bookingDto.diaChiPhong : "");

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            java.util.Date batDau = safeParseDate(bookingDto.batDau);
            if (batDau != null) {
                tvBookingDate.setText(dateFormat.format(batDau));
                String timeSlot = getTimeSlot(batDau);
                String time = timeFormat.format(batDau);
                tvBookingTime.setText(timeSlot + " (" + time + ")");
            }

            // Notes
            if (bookingDto.ghiChu != null && !bookingDto.ghiChu.isEmpty()) {
                String[] lines = bookingDto.ghiChu.split("\n");
                StringBuilder contactInfo = new StringBuilder();
                StringBuilder noteInfo = new StringBuilder();

                for (String line : lines) {
                    if (line.startsWith("Họ tên:") || line.startsWith("SĐT:") ||
                            line.startsWith("Khung giờ:") || line.startsWith("Cho phép gọi:")) {
                        contactInfo.append(line).append("\n");
                    } else if (line.startsWith("Ghi chú:")) {
                        noteInfo.append(line.substring(8).trim());
                    }
                }

                if (contactInfo.length() > 0) {
                    tvContactInfo.setText(contactInfo.toString().trim());
                    layoutContactInfo.setVisibility(View.VISIBLE);
                }

                if (noteInfo.length() > 0) {
                    tvNote.setText(noteInfo.toString());
                    layoutNote.setVisibility(View.VISIBLE);
                }
            }

            java.util.Date created = safeParseDate(bookingDto.thoiGianTao);
            if (created != null) {
                SimpleDateFormat createdFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                tvCreatedTime.setText(createdFormat.format(created));
            }

            if (trangThaiId == 1 || trangThaiId == 2) {
                layoutButtons.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
            } else {
                btnCancel.setVisibility(View.GONE);
                if (trangThaiId == 3 || trangThaiId == 4) {
                    layoutButtons.setVisibility(View.GONE);
                }
            }

            // Backend currently doesn't provide landlord phone in booking DTO
            landlordPhone = null;
            btnContact.setEnabled(false);
            btnContact.setAlpha(0.5f);

        } catch (Exception e) {
            Log.e(TAG, "Error displaying booking info: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi hiển thị thông tin: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private java.util.Date safeParseDate(String iso) {
        if (iso == null || iso.trim().isEmpty()) return null;
        try {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US);
            return sdf1.parse(iso.replace("Z", ""));
        } catch (Exception ignore) {
        }
        try {
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            return sdf2.parse(iso.replace("Z", ""));
        } catch (Exception e) {
            return null;
        }
    }

    private String getStatusText(int trangThaiId) {
        switch (trangThaiId) {
            case 1:
                return "Chờ duyệt";
            case 2:
                return "Đã xác nhận";
            case 3:
                return "Đã xem";
            case 4:
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }

    private int getStatusColor(int trangThaiId) {
        switch (trangThaiId) {
            case 1:
                return R.color.warning;
            case 2:
                return R.color.success;
            case 3:
                return R.color.text_secondary;
            case 4:
                return R.color.error;
            default:
                return R.color.text_secondary;
        }
    }

    private String getTimeSlot(java.util.Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        if (hour >= 8 && hour < 12) {
            return "Sáng (8-12h)";
        } else if (hour >= 13 && hour < 17) {
            return "Chiều (13-17h)";
        } else if (hour >= 18 && hour <= 20) {
            return "Tối (18-20h)";
        }
        return "";
    }

    private String formatPrice(long price) {
        if (price >= 1000000) {
            double millions = price / 1000000.0;
            return String.format("%.1f triệu/tháng", millions);
        } else if (price >= 1000) {
            double thousands = price / 1000.0;
            return String.format("%.0f nghìn/tháng", thousands);
        }
        return price + " đ/tháng";
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy lịch hẹn")
                .setMessage("Bạn có chắc chắn muốn hủy lịch hẹn này?")
                .setPositiveButton("Hủy lịch", (dialog, which) -> cancelBooking())
                .setNegativeButton("Không", null)
                .show();
    }

    private void cancelBooking() {
        if (!sessionManager.isLoggedIn() || sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiClient.setToken(sessionManager.getToken());

        bookingRepository.cancelBooking(bookingId, new BookingRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    Toast.makeText(BookingDetailActivity.this, "Đã hủy lịch hẹn", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(BookingDetailActivity.this, "Không thể hủy lịch hẹn: " + message, Toast.LENGTH_LONG).show());
            }
        });
    }

    private void contactLandlord() {
        if (landlordPhone != null && !landlordPhone.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Liên hệ chủ trọ")
                    .setMessage("Số điện thoại: " + landlordPhone)
                    .setPositiveButton("Gọi điện", (dialog, which) -> {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + landlordPhone));
                        startActivity(intent);
                    })
                    .setNegativeButton("Đóng", null)
                    .show();
        } else {
            Toast.makeText(this, "Không tìm thấy số điện thoại chủ trọ", Toast.LENGTH_SHORT).show();
        }
    }
}
