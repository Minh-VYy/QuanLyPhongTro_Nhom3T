package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.DatPhongDao;
import com.example.QuanLyPhongTro_App.data.model.DatPhong;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class BookingListFragment extends Fragment {

    private static final String TAG = "BookingListFragment";
    private static final String ARG_TYPE = "type";
    
    private String bookingType;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private SessionManager sessionManager;

    public static BookingListFragment newInstance(String type) {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingType = getArguments().getString(ARG_TYPE);
        }
        sessionManager = new SessionManager(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

        recyclerView = view.findViewById(R.id.bookingRecyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.emptyView);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo adapter với danh sách trống
        adapter = new BookingAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Load dữ liệu từ database
        loadBookingsFromDatabase();

        return view;
    }

    private void loadBookingsFromDatabase() {
        if (!sessionManager.isLoggedIn()) {
            showEmptyView("Vui lòng đăng nhập để xem lịch hẹn");
            return;
        }

        String userId = sessionManager.getUserId();
        Log.d(TAG, "=== LOAD BOOKINGS ===");
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Booking Type: " + bookingType);
        
        if (userId == null || userId.isEmpty()) {
            showEmptyView("Không tìm thấy thông tin người dùng");
            return;
        }

        // Hiển thị loading
        showLoading(true);

        new Thread(() -> {
            Connection conn = null;
            List<Booking> bookingList = new ArrayList<>();
            String errorMessage = "";

            try {
                // Kết nối database
                conn = DatabaseHelper.getConnection();
                Log.d(TAG, "✅ Connected to database");
                
                // Lấy danh sách đặt phòng
                DatPhongDao dao = new DatPhongDao();
                List<DatPhong> datPhongList = dao.getDatPhongByNguoiThue(conn, userId);
                Log.d(TAG, "Loaded " + datPhongList.size() + " bookings from database");
                
                // Chuyển đổi DatPhong sang Booking
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                
                for (DatPhong dp : datPhongList) {
                    // Lọc theo loại (Sắp tới / Đã xem / Đã hủy)
                    String status = getStatusFromTrangThaiId(dp.getTrangThaiId());
                    Log.d(TAG, "Booking: " + dp.getDatPhongId() + " - Status: " + status + " - TrangThaiId: " + dp.getTrangThaiId());
                    
                    if (shouldShowBooking(status)) {
                        String date = dp.getBatDau() != null ? dateFormat.format(dp.getBatDau()) : "";
                        String time = dp.getBatDau() != null ? timeFormat.format(dp.getBatDau()) : "";
                        String timeSlot = getTimeSlotFromHour(dp.getBatDau());
                        
                        Booking booking = new Booking(
                            dp.getDatPhongId(),
                            dp.getTenPhong() != null ? dp.getTenPhong() : "Phòng trọ",
                            formatPrice(dp.getGiaPhong()),
                            date,
                            timeSlot + " (" + time + ")",
                            status,
                            dp.getTenNguoiThue() != null ? dp.getTenNguoiThue() : "",
                            dp.getDiaChiPhong() != null ? dp.getDiaChiPhong() : ""
                        );
                        
                        bookingList.add(booking);
                        Log.d(TAG, "✅ Added booking to list: " + booking.getTitle());
                    } else {
                        Log.d(TAG, "❌ Booking filtered out (not matching type)");
                    }
                }
                
                Log.d(TAG, "Final booking list size: " + bookingList.size() + " for type: " + bookingType);
                
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "Lỗi kết nối: " + e.getMessage();
                Log.e(TAG, "Error loading bookings: " + errorMessage, e);
            } finally {
                if (conn != null) {
                    DatabaseHelper.releaseConnection(conn);
                }
            }

            // Cập nhật UI
            final List<Booking> finalList = bookingList;
            final String finalError = errorMessage;
            
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    showLoading(false);
                    
                    if (!finalError.isEmpty()) {
                        Toast.makeText(getContext(), finalError, Toast.LENGTH_SHORT).show();
                        showEmptyView("Không thể tải dữ liệu");
                    } else if (finalList.isEmpty()) {
                        showEmptyView(getEmptyMessage());
                    } else {
                        showRecyclerView();
                        adapter = new BookingAdapter(getContext(), finalList);
                        adapter.setOnBookingActionListener(() -> {
                            // Reload lại danh sách khi có booking bị hủy
                            loadBookingsFromDatabase();
                        });
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }).start();
    }

    private boolean shouldShowBooking(String status) {
        if (bookingType == null) return true;
        
        switch (bookingType) {
            case "upcoming":
                return status.equals("pending") || status.equals("confirmed");
            case "completed":
                return status.equals("completed");
            case "cancelled":
                return status.equals("cancelled");
            default:
                return true;
        }
    }

    private String getStatusFromTrangThaiId(int trangThaiId) {
        switch (trangThaiId) {
            case 1: return "pending";      // Chờ duyệt
            case 2: return "confirmed";    // Đã xác nhận
            case 3: return "completed";    // Đã xem
            case 4: return "cancelled";    // Đã hủy
            default: return "pending";
        }
    }

    private String getTimeSlotFromHour(java.util.Date date) {
        if (date == null) return "";
        
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

    private String getEmptyMessage() {
        if (bookingType == null) return "Chưa có lịch hẹn nào";
        
        switch (bookingType) {
            case "upcoming":
                return "Chưa có lịch hẹn sắp tới";
            case "completed":
                return "Chưa có lịch hẹn đã xem";
            case "cancelled":
                return "Chưa có lịch hẹn đã hủy";
            default:
                return "Chưa có lịch hẹn nào";
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    private void showEmptyView(String message) {
        if (emptyView != null) {
            emptyView.setText(message);
            emptyView.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void showRecyclerView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload dữ liệu khi quay lại fragment
        loadBookingsFromDatabase();
    }
}
