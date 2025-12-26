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
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.data.repository.BookingCache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        // Load dữ liệu từ API
        loadBookingsFromApi();

        return view;
    }

    private boolean hasToken() {
        String token = sessionManager.getToken();
        return token != null && !token.trim().isEmpty();
    }

    private void loadBookingsFromApi() {
        if (!hasToken()) {
            showEmptyView("Vui lòng đăng nhập để xem lịch hẹn");
            return;
        }

        ApiClient.setToken(sessionManager.getToken());
        showLoading(true);

        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        loadBookingsTolerant(api);
    }

    private void loadBookingsTolerant(ApiService api) {
        api.getMyBookingsTolerant().enqueue(new Callback<com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponse<com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse.MyBookingDto>>() {
            @Override
            public void onResponse(@NonNull Call<com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponse<com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse.MyBookingDto>> call,
                                   @NonNull Response<com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponse<com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse.MyBookingDto>> response) {
                if (!isAdded()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    String err = "";
                    try {
                        okhttp3.ResponseBody eb = response.errorBody();
                        if (eb != null) {
                            try {
                                err = eb.string();
                            } finally {
                                eb.close();
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    Log.w(TAG, "API my-bookings(tolerant) failed http=" + response.code() + " err=" + err);

                    // 500 là backend bug -> đừng spam gọi thêm endpoint khác, chỉ show lỗi rõ
                    if (response.code() >= 500) {
                        showServerError(err);
                        return;
                    }

                    // nếu không phải 500 thì fallback typed
                    loadBookingsTyped(api);
                    return;
                }

                com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponse<com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse.MyBookingDto> body = response.body();
                if (!body.success) {
                    Log.w(TAG, "API my-bookings(tolerant) !success: " + body.message);
                    showLoading(false);
                    showEmptyView(getEmptyMessage() + "\n\nNhấn để thử lại");
                    if (emptyView != null) {
                        emptyView.setOnClickListener(v -> loadBookingsFromApi());
                    }
                    return;
                }

                List<com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse.MyBookingDto> items = body.data != null ? body.data : new ArrayList<>();
                List<Booking> bookingList = mapBookings(items);

                showLoading(false);
                if (bookingList.isEmpty()) {
                    showEmptyView(getEmptyMessage());
                } else {
                    showRecyclerView();
                    adapter = new BookingAdapter(getContext(), bookingList);
                    adapter.setOnBookingActionListener(() -> loadBookingsFromApi());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<com.example.QuanLyPhongTro_App.data.response.ApiOrArrayResponse<com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse.MyBookingDto>> call,
                                  @NonNull Throwable t) {
                Log.e(TAG, "Error loading bookings from API (tolerant)", t);
                if (!isAdded()) return;
                showLoading(false);
                showEmptyView("Không thể tải dữ liệu\n\nNhấn để thử lại");
                if (emptyView != null) {
                    emptyView.setOnClickListener(v -> loadBookingsFromApi());
                }
                Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBookingsTyped(ApiService api) {
        api.getMyBookingsTyped().enqueue(new Callback<MyBookingsResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyBookingsResponse> call, @NonNull Response<MyBookingsResponse> response) {
                if (!isAdded()) return;

                if (!response.isSuccessful() || response.body() == null) {
                    String err = "";
                    try {
                        okhttp3.ResponseBody eb = response.errorBody();
                        if (eb != null) {
                            try {
                                err = eb.string();
                            } finally {
                                eb.close();
                            }
                        }
                    } catch (Exception ignored) {
                    }

                    Log.w(TAG, "API my-bookings(typed) failed http=" + response.code() + " err=" + err);
                    if (response.code() >= 500) {
                        showServerError(err);
                        return;
                    }
                    showLoading(false);
                    showEmptyView("Không thể tải dữ liệu\n\nNhấn để thử lại");
                    if (emptyView != null) {
                        emptyView.setOnClickListener(v -> loadBookingsFromApi());
                    }
                    return;
                }

                MyBookingsResponse body = response.body();
                if (!body.success) {
                    showLoading(false);
                    showEmptyView(getEmptyMessage() + "\n\nNhấn để thử lại");
                    if (emptyView != null) {
                        emptyView.setOnClickListener(v -> loadBookingsFromApi());
                    }
                    return;
                }

                List<MyBookingsResponse.MyBookingDto> items = body.data != null ? body.data : new ArrayList<>();
                List<Booking> bookingList = mapBookings(items);

                showLoading(false);
                if (bookingList.isEmpty()) {
                    showEmptyView(getEmptyMessage());
                } else {
                    showRecyclerView();
                    adapter = new BookingAdapter(getContext(), bookingList);
                    adapter.setOnBookingActionListener(() -> loadBookingsFromApi());
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MyBookingsResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "API my-bookings(typed) onFailure", t);
                if (!isAdded()) return;
                showLoading(false);
                showEmptyView("Không thể tải dữ liệu\n\nNhấn để thử lại");
                if (emptyView != null) {
                    emptyView.setOnClickListener(v -> loadBookingsFromApi());
                }
            }
        });
    }

    private List<Booking> mapBookings(List<MyBookingsResponse.MyBookingDto> items) {
        List<Booking> bookingList = new ArrayList<>();

        int total = 0, pending = 0, confirmed = 0, completed = 0, cancelled = 0;

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        for (MyBookingsResponse.MyBookingDto dto : items) {
            total++;
            int trangThaiId = dto.trangThaiId != null ? dto.trangThaiId : 1;
            String status = getStatusFromTrangThaiId(trangThaiId);

            switch (status) {
                case "pending": pending++; break;
                case "confirmed": confirmed++; break;
                case "completed": completed++; break;
                case "cancelled": cancelled++; break;
            }

            if (!shouldShowBooking(status)) continue;

            java.util.Date startDate = safeParseDate(dto.batDau);
            String date = startDate != null ? dateFormat.format(startDate) : "";
            String time = startDate != null ? timeFormat.format(startDate) : "";
            String timeSlot = getTimeSlotFromHour(startDate);

            long price = dto.giaPhong != null ? dto.giaPhong : 0L;

            Booking booking = new Booking(
                    dto.datPhongId,
                    dto.tenPhong != null ? dto.tenPhong : "Phòng trọ",
                    formatPrice(price),
                    date,
                    (timeSlot.isEmpty() ? "" : (timeSlot + " (" + time + ")")),
                    status,
                    dto.tenNguoiThue != null ? dto.tenNguoiThue : "",
                    dto.diaChiPhong != null ? dto.diaChiPhong : ""
            );

            bookingList.add(booking);
        }

        Log.d(TAG, "mapBookings type=" + bookingType + " total=" + total +
                " pending=" + pending + " confirmed=" + confirmed +
                " completed=" + completed + " cancelled=" + cancelled +
                " -> shown=" + bookingList.size());

        return bookingList;
    }

    private java.util.Date safeParseDate(String iso) {
        if (iso == null || iso.trim().isEmpty()) return null;
        try {
            // try with timezone
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

    private boolean shouldShowBooking(String status) {
        if (bookingType == null) return true;

        switch (bookingType) {
            case "upcoming":
                return status.equals("pending") || status.equals("confirmed");
            case "past":
                // history = completed + cancelled
                return status.equals("completed") || status.equals("cancelled");
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
            return String.format(Locale.getDefault(), "%.1f triệu/tháng", millions);
        } else if (price >= 1000) {
            double thousands = price / 1000.0;
            return String.format(Locale.getDefault(), "%.0f nghìn/tháng", thousands);
        }
        return price + " đ/tháng";
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
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
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void showServerError(String err) {
        // ✅ fallback: show cached bookings if any
        List<Booking> cached = new BookingCache(requireContext()).getFiltered(bookingType);
        Log.w(TAG, "showServerError http500. cachedCount=" + (cached != null ? cached.size() : -1) + " type=" + bookingType);

        if (cached != null && !cached.isEmpty()) {
            showLoading(false);

            // show list
            if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
            adapter = new BookingAdapter(getContext(), cached);
            adapter.setOnBookingActionListener(this::loadBookingsFromApi);
            recyclerView.setAdapter(adapter);

            // ⚠️ show banner ABOVE list (do not call showRecyclerView() because it hides emptyView)
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("(Offline) Server lỗi 500 khi tải lịch hẹn. Đang hiển thị lịch đã lưu trên máy.\n\nNhấn để thử tải lại");
                emptyView.setOnClickListener(v -> loadBookingsFromApi());
            }
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            return;
        }

        // no cache -> normal error
        showLoading(false);
        String msg = "Không tải được lịch hẹn (server lỗi 500).";
        if (err != null && !err.trim().isEmpty()) {
            msg += "\n" + err;
        }
        showEmptyView(msg + "\n\nNhấn để thử lại");
        if (emptyView != null) {
            emptyView.setOnClickListener(v -> loadBookingsFromApi());
        }
    }

    private String getEmptyMessage() {
        if (bookingType == null) {
            return "Chưa có lịch hẹn";
        }
        switch (bookingType) {
            case "upcoming":
                return "Chưa có lịch hẹn sắp tới";
            case "past":
                return "Chưa có lịch hẹn lịch sử";
            case "completed":
                return "Chưa có lịch hẹn đã xem";
            case "cancelled":
                return "Chưa có lịch hẹn đã hủy";
            default:
                return "Chưa có lịch hẹn";
        }
    }
}
