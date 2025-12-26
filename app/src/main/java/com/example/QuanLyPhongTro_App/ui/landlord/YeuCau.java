package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.repository.LandlordBookingRepository;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YeuCau extends AppCompatActivity {

    private static final String TAG = "YeuCau";

    private TextView btnDatLich;
    private TextView btnThanhToan;
    private RecyclerView rvBookings;
    private RecyclerView rvPayments;
    private View tabIndicator;

    private SessionManager sessionManager;
    private LandlordBookingRepository landlordBookingRepository;

    private final List<BookingRequest> bookingRequests = new ArrayList<>();
    private RecyclerView.Adapter<?> bookingsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_request);

        sessionManager = new SessionManager(this);
        landlordBookingRepository = new LandlordBookingRepository();

        btnDatLich = findViewById(R.id.btn_tab_datlich);
        btnThanhToan = findViewById(R.id.btn_tab_thanhtoan);
        tabIndicator = findViewById(R.id.tab_indicator);
        rvBookings = findViewById(R.id.rv_bookings);
        rvPayments = findViewById(R.id.rv_payments);

        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        BookingsAdapter adapter = new com.example.QuanLyPhongTro_App.ui.landlord.BookingsAdapter(bookingRequests, this);
        adapter.setOnBookingActionListener(new BookingsAdapter.OnBookingActionListener() {
            @Override
            public void onAccept(BookingRequest request) {
                confirmAndUpdateStatus(request, 2); // 2 = accepted
            }

            @Override
            public void onReject(BookingRequest request) {
                confirmAndUpdateStatus(request, 3); // 3 = rejected/cancelled
            }
        });
        bookingsAdapter = adapter;
        rvBookings.setAdapter(bookingsAdapter);

        // Payments tab not implemented yet (keep hidden)
        rvPayments.setLayoutManager(new LinearLayoutManager(this));

        btnDatLich.setOnClickListener(v -> showTab("datlich"));
        btnThanhToan.setOnClickListener(v -> showTab("thanhtoan"));

        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");

        // default tab
        String defaultTab = getIntent().getStringExtra("defaultTab");
        showTab(defaultTab != null && !defaultTab.isEmpty() ? defaultTab : "datlich");

        loadBookingRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "requests");
        // Ensure UI is in sync with server when coming back.
        loadBookingRequests();
    }

    private void showTab(String tab) {
        int primaryColor = ContextCompat.getColor(this, R.color.primary);
        int mutedColor = ContextCompat.getColor(this, R.color.muted);

        btnDatLich.setTextColor(mutedColor);
        btnThanhToan.setTextColor(mutedColor);
        tabIndicator.setBackgroundColor(primaryColor);

        rvBookings.setVisibility(View.GONE);
        rvPayments.setVisibility(View.GONE);

        if ("thanhtoan".equals(tab)) {
            btnThanhToan.setTextColor(primaryColor);
            rvPayments.setVisibility(View.VISIBLE);
            Toast.makeText(this, "ℹ️ Thanh toán chưa triển khai", Toast.LENGTH_SHORT).show();
        } else {
            btnDatLich.setTextColor(primaryColor);
            rvBookings.setVisibility(View.VISIBLE);
        }
    }

    private void loadBookingRequests() {
        String landlordId = sessionManager.getUserId();
        if (landlordId == null || landlordId.trim().isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin chủ trọ", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Loading booking requests for landlord: " + landlordId);

        landlordBookingRepository.getBookingRequests(landlordId, new LandlordBookingRepository.ListCallback() {
            @Override
            public void onSuccess(List<Map<String, Object>> rawItems) {
                runOnUiThread(() -> {
                    bookingRequests.clear();
                    if (rawItems != null) {
                        for (Map<String, Object> item : rawItems) {
                            bookingRequests.add(mapBooking(item, landlordId));
                        }
                    }
                    bookingsAdapter.notifyDataSetChanged();

                    if (bookingRequests.isEmpty()) {
                        Toast.makeText(YeuCau.this, "ℹ️ Hiện chưa có yêu cầu đặt lịch", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(YeuCau.this, "❌ Lỗi tải yêu cầu: " + message, Toast.LENGTH_LONG).show());
            }
        });
    }

    private BookingRequest mapBooking(Map<String, Object> item, String landlordId) {
        BookingRequest br = new BookingRequest();

        // API may return PascalCase (DatPhongId) or camelCase (datPhongId)
        String datPhongId = firstNonEmpty(
                asString(item.get("DatPhongId"), null),
                asString(item.get("datPhongId"), null),
                asString(item.get("id"), null),
                asString(item.get("Id"), null)
        );
        br.setDatPhongId(datPhongId);

        br.setPhongId(firstNonEmpty(
                asString(item.get("PhongId"), null),
                asString(item.get("phongId"), null)
        ));
        br.setNguoiThueId(firstNonEmpty(
                asString(item.get("NguoiThueId"), null),
                asString(item.get("nguoiThueId"), null)
        ));
        br.setChuTroId(firstNonEmpty(
                asString(item.get("ChuTroId"), null),
                asString(item.get("chuTroId"), landlordId),
                landlordId
        ));

        br.setTenNguoiThue(firstNonEmpty(
                asString(item.get("TenNguoiThue"), null),
                asString(item.get("tenNguoiThue"), null),
                asString(item.get("HoTenNguoiThue"), ""),
                asString(item.get("hoTenNguoiThue"), "")
        ));
        br.setTenPhong(firstNonEmpty(
                asString(item.get("TenPhong"), null),
                asString(item.get("tenPhong"), null),
                asString(item.get("TieuDe"), ""),
                asString(item.get("tieuDe"), "")
        ));

        br.setLoai(firstNonEmpty(
                asString(item.get("Loai"), null),
                asString(item.get("loai"), "Xem phòng")
        ));
        br.setGhiChu(firstNonEmpty(
                asString(item.get("GhiChu"), null),
                asString(item.get("ghiChu"), "")
        ));

        // Status can come as int (TrangThaiId/statusId) or string (TrangThai/status)
        Object trangThaiObj = firstNonEmptyObj(
                item.get("TrangThaiId"),
                item.get("trangThaiId"),
                item.get("Status"),
                item.get("status"),
                item.get("TrangThai"),
                item.get("trangThai")
        );
        Integer trangThaiId = asInt(trangThaiObj, 1);
        br.setTrangThaiId(trangThaiId);

        String tenTrangThai = firstNonEmpty(
                asString(item.get("TenTrangThai"), null),
                asString(item.get("tenTrangThai"), null)
        );
        if (tenTrangThai == null || tenTrangThai.trim().isEmpty()) {
            tenTrangThai = mapStatusName(trangThaiId);
        }
        br.setTenTrangThai(tenTrangThai);

        Log.d(TAG, "Mapped booking: datPhongId=" + datPhongId + " phongId=" + br.getPhongId() + " status=" + trangThaiId);
        return br;
    }

    /** Returns the first non-empty string among the inputs (trimmed). */
    private static String firstNonEmpty(String... vals) {
        if (vals == null) return null;
        for (String s : vals) {
            if (s == null) continue;
            if (!s.trim().isEmpty()) return s;
        }
        return null;
    }

    /** Returns the first non-null Object among the inputs. */
    private static Object firstNonEmptyObj(Object... vals) {
        if (vals == null) return null;
        for (Object v : vals) {
            if (v != null) return v;
        }
        return null;
    }

    private static String mapStatusName(int id) {
        switch (id) {
            case 2:
                return "DaXacNhan";
            case 3:
                return "DaHuy";
            case 1:
            default:
                return "ChoXacNhan";
        }
    }

    private static String asString(Object o, String fallback) {
        if (o == null) return fallback;
        return String.valueOf(o);
    }

    private static Integer asInt(Object o, int fallback) {
        if (o == null) return fallback;
        if (o instanceof Number) return ((Number) o).intValue();
        try {
            return Integer.parseInt(String.valueOf(o));
        } catch (Exception ignored) {
            return fallback;
        }
    }

    private void confirmAndUpdateStatus(BookingRequest request, int newStatus) {
        if (request == null) {
            Toast.makeText(this, "Lỗi: Yêu cầu không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        // If already processed, don't allow actions.
        Integer currentStatus = request.getTrangThaiId();
        if (currentStatus != null && currentStatus != 1) {
            Toast.makeText(this, "Yêu cầu đã được xử lý trước đó", Toast.LENGTH_SHORT).show();
            return;
        }

        String phong = request.getTenPhong() != null ? request.getTenPhong() : "";
        String nguoiThue = request.getTenNguoiThue() != null ? request.getTenNguoiThue() : "";

        String title = newStatus == 2 ? "Xác nhận chấp nhận" : "Xác nhận từ chối";
        String actionText = newStatus == 2 ? "chấp nhận" : "từ chối";
        String message = "Bạn có chắc muốn " + actionText + " yêu cầu này?";
        if (!nguoiThue.trim().isEmpty() || !phong.trim().isEmpty()) {
            message += "\n\n" + (nguoiThue.trim().isEmpty() ? "" : ("Người thuê: " + nguoiThue + "\n"))
                    + (phong.trim().isEmpty() ? "" : ("Phòng: " + phong));
        }

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("Hủy", (d, w) -> d.dismiss())
                .setPositiveButton("Xác nhận", (d, w) -> updateRequestStatus(request, newStatus))
                .show();
    }

    private void updateRequestStatus(BookingRequest request, int newStatus) {
        if (request == null || request.getDatPhongId() == null || request.getDatPhongId().trim().isEmpty()) {
            Toast.makeText(this, "Lỗi: Thiếu DatPhongId", Toast.LENGTH_SHORT).show();
            return;
        }

        String landlordId = sessionManager.getUserId();
        String bookingId = request.getDatPhongId();

        Toast.makeText(this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
        landlordBookingRepository.updateBookingStatus(bookingId, newStatus, landlordId, new LandlordBookingRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    // Optimistic local update
                    request.setTrangThaiId(newStatus);
                    request.setTenTrangThai(mapStatusName(newStatus));

                    // If this screen is meant to show only pending requests, remove it immediately.
                    // This prevents stale buttons from still showing until the next refresh.
                    bookingRequests.remove(request);
                    bookingsAdapter.notifyDataSetChanged();

                    Toast.makeText(YeuCau.this,
                            newStatus == 2 ? "✅ Đã chấp nhận yêu cầu" : "✅ Đã từ chối yêu cầu",
                            Toast.LENGTH_SHORT).show();

                    // Re-fetch from API to ensure UI matches backend (and catch cases where update didn't persist).
                    loadBookingRequests();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> Toast.makeText(YeuCau.this, "❌ Cập nhật thất bại: " + message, Toast.LENGTH_LONG).show());
            }
        });
    }
}
