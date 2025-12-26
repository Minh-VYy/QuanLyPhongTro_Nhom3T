package com.example.QuanLyPhongTro_App.ui.tenant;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.data.request.CreateBookingRequest;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.example.QuanLyPhongTro_App.utils.IsoDateTime;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingCreateActivity extends AppCompatActivity {

    public static final String EXTRA_ROOM_ID = "roomId";
    public static final String EXTRA_ROOM = "room";

    private static final String TAG = "BookingCreateActivity";

    private ImageView roomThumbnail;
    private TextView roomTitle, roomPrice, roomAddress;
    private Button btnSelectDate, btnConfirmBooking;
    private com.google.android.material.chip.ChipGroup timeSlotChipGroup;
    private TextInputEditText inputFullName, inputPhone, inputNote;
    private SwitchMaterial switchAllowCall;

    private Calendar selectedDate;
    private String selectedTimeSlot = "";
    private Room currentRoom;
    private SessionManager sessionManager;
    private String roomId;

    private boolean isResolvingChuTroId = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_booking_create);

        sessionManager = new SessionManager(this);

        // 1) Ưu tiên lấy roomId truyền riêng
        roomId = getIntent().getStringExtra(EXTRA_ROOM_ID);

        // 2) Fallback: lấy Room serialized (để hiển thị nhanh nếu có)
        currentRoom = (Room) getIntent().getSerializableExtra(EXTRA_ROOM);
        if ((roomId == null || roomId.trim().isEmpty()) && currentRoom != null) {
            roomId = currentRoom.getPhongId();
        }

        android.util.Log.d(TAG, "=== START BookingCreateActivity ===");
        android.util.Log.d(TAG, "roomId(extra)=" + roomId);
        android.util.Log.d(TAG, "currentRoom=" + currentRoom);

        initViews();
        setupToolbar();
        setupListeners();

        // Hiển thị tạm dữ liệu truyền qua (nếu có)
        loadRoomData();
        loadUserData();

        // Nếu có roomId thật thì gọi API lấy chi tiết đầy đủ để hiển thị đúng
        if (roomId != null && !roomId.trim().isEmpty()) {
            fetchRoomDetail(roomId);
        } else {
            Toast.makeText(this,
                    "Không nhận được ID phòng (PhongId).\nVui lòng quay lại và mở phòng từ danh sách chuẩn.",
                    Toast.LENGTH_LONG).show();
        }
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

            // ✅ fix: nếu giá = 0 thì hiển thị phù hợp thay vì 0đ/tháng (thường do Room truyền từ màn trước thiếu dữ liệu)
            if (currentRoom.getPriceValue() <= 0) {
                roomPrice.setText("Đang cập nhật giá");
            } else {
                roomPrice.setText(currentRoom.getFormattedPrice());
            }

            String addr = currentRoom.getLocation();
            if (addr == null || addr.trim().isEmpty()) addr = currentRoom.getAddress();
            roomAddress.setText(addr != null ? addr : "");

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

    private boolean hasValidLoginForBooking() {
        String token = sessionManager.getToken();
        String userId = sessionManager.getUserId();
        return token != null && !token.trim().isEmpty() && isUuid(userId);
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

        // Kiểm tra đăng nhập (✅ theo token + GUID, không theo flag isLoggedIn)
        if (!hasValidLoginForBooking()) {
            // cố gắng re-extract userId từ token (JWT)
            String token = sessionManager.getToken();
            if (token != null && !token.trim().isEmpty()) {
                sessionManager.saveToken(token);
            }
        }
        if (!hasValidLoginForBooking()) {
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
        btnConfirmBooking.setEnabled(false);
        btnConfirmBooking.setText("Đang xử lý...");

        // ✅ đảm bảo userId trong session là GUID trước khi gửi API
        if (!isUuid(sessionManager.getUserId())) {
            String token = sessionManager.getToken();
            if (token != null && !token.trim().isEmpty()) {
                sessionManager.saveToken(token);
            }
        }

        android.util.Log.d("BookingCreateActivity", "=== STARTING BOOKING CREATION ===");
        android.util.Log.d("BookingCreateActivity", "Raw Room.phongId: " + (currentRoom != null ? currentRoom.getPhongId() : "NULL"));
        android.util.Log.d("BookingCreateActivity", "User ID: " + sessionManager.getUserId());
        android.util.Log.d("BookingCreateActivity", "Token: " + (sessionManager.getToken() != null ? "EXISTS" : "NULL"));

        final String phongId = resolvePhongIdFromRoom(currentRoom);
        if (phongId == null || phongId.trim().isEmpty()) {
            android.util.Log.e("BookingCreateActivity", "ERROR: Could not resolve PhongId from Room");
            Toast.makeText(this,
                    "Không tìm thấy ID phòng (PhongId).\n" +
                            "Hãy mở phòng từ danh sách/chi tiết phòng rồi thử lại.",
                    Toast.LENGTH_LONG).show();
            btnConfirmBooking.setEnabled(true);
            btnConfirmBooking.setText("Xác nhận đặt lịch");
            return;
        }

        if (sessionManager.getToken() == null || sessionManager.getToken().isEmpty()) {
            android.util.Log.e("BookingCreateActivity", "ERROR: No token available");
            Toast.makeText(this, "Bạn cần đăng nhập lại để đặt lịch.", Toast.LENGTH_LONG).show();
            btnConfirmBooking.setEnabled(true);
            btnConfirmBooking.setText("Xác nhận đặt lịch");
            return;
        }

        // ✅ Swagger yêu cầu ChuTroId bắt buộc -> ưu tiên lấy từ Intent extra (RoomDetailActivity truyền sang)
        String chuTroId = null;
        try {
            String extraChuTroId = getIntent().getStringExtra("chuTroId");
            if (isUuid(extraChuTroId)) {
                chuTroId = extraChuTroId;
            }
        } catch (Exception ignored) {
        }

        if (chuTroId == null) {
            chuTroId = resolveChuTroIdFromRoom(currentRoom);
        }

        if (chuTroId == null || chuTroId.trim().isEmpty()) {
            android.util.Log.w("BookingCreateActivity", "ChuTroId missing. Fetching from API room detail...");
            fetchChuTroIdAndCreateBooking(fullName, phone, note, allowCall, bookingDate, phongId);
            return;
        }

        createBookingWithLandlordId(fullName, phone, note, allowCall, bookingDate, chuTroId, phongId);
    }

    private void fetchChuTroIdAndCreateBooking(String fullName, String phone, String note,
                                              boolean allowCall, String bookingDate, String phongId) {
        // Prevent duplicate requests when user taps multiple times
        if (isResolvingChuTroId) {
            android.util.Log.w(TAG, "fetchChuTroId already in progress - ignoring duplicate call");
            return;
        }
        isResolvingChuTroId = true;

        try {
            ApiClient.setToken(sessionManager.getToken());
            ApiService api = ApiClient.getRetrofit().create(ApiService.class);
            api.getRoomDetail(phongId).enqueue(new Callback<GenericResponse<Object>>() {
                @Override
                public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                    isResolvingChuTroId = false;

                    if (!response.isSuccessful() || response.body() == null || response.body().data == null) {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception ignored) {
                        }

                        android.util.Log.e(TAG, "fetchChuTroId getRoomDetail failed http=" + response.code() + " body=" + errorBody);
                        btnConfirmBooking.setEnabled(true);
                        btnConfirmBooking.setText("Xác nhận đặt lịch");
                        Toast.makeText(BookingCreateActivity.this,
                                "Không lấy được thông tin phòng để suy ra chủ trọ (ChuTroId).\n" +
                                        "HTTP " + response.code() + (errorBody.isEmpty() ? "" : ("\n" + errorBody)),
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    // parse chuTroId from raw map
                    String chuTroId = null;
                    try {
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        java.util.Map<String, Object> map = gson.fromJson(gson.toJson(response.body().data), java.util.Map.class);
                        Object v = map.get("ChuTroId");
                        if (v == null) v = map.get("chuTroId");
                        if (v == null) v = map.get("LandlordId");
                        if (v == null) v = map.get("landlordId");
                        if (v != null) chuTroId = String.valueOf(v);
                    } catch (Exception ignored) {
                    }

                    if (chuTroId == null || chuTroId.trim().isEmpty()) {
                        btnConfirmBooking.setEnabled(true);
                        btnConfirmBooking.setText("Xác nhận đặt lịch");
                        Toast.makeText(BookingCreateActivity.this,
                                "Backend chưa trả ChuTroId trong API chi tiết phòng.\n" +
                                        "Vui lòng cập nhật backend trả ChuTroId (UUID) để đặt lịch.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    createBookingWithLandlordId(fullName, phone, note, allowCall, bookingDate, chuTroId, phongId);
                }

                @Override
                public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                    isResolvingChuTroId = false;

                    android.util.Log.e(TAG, "fetchChuTroId getRoomDetail onFailure: " + t.getMessage(), t);
                    btnConfirmBooking.setEnabled(true);
                    btnConfirmBooking.setText("Xác nhận đặt lịch");
                    Toast.makeText(BookingCreateActivity.this,
                            "Lỗi kết nối khi lấy ChuTroId: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            isResolvingChuTroId = false;

            android.util.Log.e(TAG, "fetchChuTroIdAndCreateBooking error: " + e.getMessage(), e);
            btnConfirmBooking.setEnabled(true);
            btnConfirmBooking.setText("Xác nhận đặt lịch");
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Extract a UUID-like PhongId from Room.
     * Prefer the explicit field Room.phongId; fallback to parsing from other strings.
     */
    private String resolvePhongIdFromRoom(Room room) {
        if (room == null) return null;

        // 1) Preferred: explicit PhongId field
        String phongId = room.getPhongId();
        if (isUuid(phongId)) return phongId;

        // 2) Fallback: sometimes callers accidentally put the UUID into other fields (title/location/address)
        String[] candidates = new String[]{
                room.getTitle(),
                room.getLocation(),
                room.getAddress(),
                room.getDescription()
        };
        for (String c : candidates) {
            String extracted = extractFirstUuid(c);
            if (extracted != null) return extracted;
        }

        return null;
    }

    /**
     * Best-effort: some repos later add chuTroId UUID field to Room.
     * We keep reflection-based extraction so app works across variants without breaking compilation.
     */
    private String resolveChuTroIdFromRoom(Room room) {
        if (room == null) return null;

        try {
            java.lang.reflect.Method m = room.getClass().getMethod("getChuTroId");
            Object v = m.invoke(room);
            if (v instanceof String && isUuid((String) v)) return (String) v;
        } catch (Exception ignored) {
        }

        try {
            java.lang.reflect.Method m = room.getClass().getMethod("getLandlordUuidId");
            Object v = m.invoke(room);
            if (v instanceof String && isUuid((String) v)) return (String) v;
        } catch (Exception ignored) {
        }

        return null;
    }

    private boolean isUuid(String s) {
        if (s == null) return false;
        return s.matches("(?i)^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$");
    }

    private String extractFirstUuid(String s) {
        if (s == null) return null;
        java.util.regex.Matcher matcher = java.util.regex.Pattern
                .compile("(?i)([0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12})")
                .matcher(s);
        if (matcher.find()) return matcher.group(1);
        return null;
    }

    private void createBookingWithLandlordId(String fullName, String phone, String note,
                                            boolean allowCall, String bookingDate, String chuTroId, String phongId) {
        android.util.Log.d("BookingCreateActivity", "=== CREATING BOOKING ===");
        android.util.Log.d("BookingCreateActivity", "PhongId: " + phongId);
        android.util.Log.d("BookingCreateActivity", "ChuTroId: " + chuTroId);
        android.util.Log.d("BookingCreateActivity", "FullName: " + fullName);
        android.util.Log.d("BookingCreateActivity", "Phone: " + phone);
        android.util.Log.d("BookingCreateActivity", "TimeSlot: " + selectedTimeSlot);

        // Build time window
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

        Calendar endTime = (Calendar) startTime.clone();
        endTime.add(Calendar.HOUR_OF_DAY, 2);

        String fullNote = "Họ tên: " + fullName + "\n" +
                "SĐT: " + phone + "\n" +
                "Khung giờ: " + selectedTimeSlot + "\n" +
                "Cho phép gọi: " + (allowCall ? "Có" : "Không");
        if (note != null && !note.isEmpty()) {
            fullNote += "\nGhi chú: " + note;
        }

        String startTimeStr = IsoDateTime.formatLocal(startTime.getTime());
        String endTimeStr = IsoDateTime.formatLocal(endTime.getTime());

        android.util.Log.d("BookingCreateActivity", "Start time: " + startTimeStr);
        android.util.Log.d("BookingCreateActivity", "End time: " + endTimeStr);
        android.util.Log.d("BookingCreateActivity", "Note: " + fullNote);

        CreateBookingRequest request = new CreateBookingRequest(
                phongId,
                chuTroId,
                "Xem phòng",
                startTimeStr,
                endTimeStr,
                fullNote
        );

        // Call API
        ApiClient.setToken(sessionManager.getToken());
        ApiService api = ApiClient.getRetrofit().create(ApiService.class);

        android.util.Log.d("BookingCreateActivity", "Sending booking request to API...");

        api.createBookingRaw(request).enqueue(new Callback<okhttp3.ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Response<okhttp3.ResponseBody> response) {
                btnConfirmBooking.setEnabled(true);
                btnConfirmBooking.setText("Xác nhận đặt lịch");

                android.util.Log.d("BookingCreateActivity", "=== BOOKING API RESPONSE (RAW) ===");
                android.util.Log.d("BookingCreateActivity", "Response code: " + response.code());
                android.util.Log.d("BookingCreateActivity", "Response successful: " + response.isSuccessful());

                String raw = "";
                try {
                    okhttp3.ResponseBody rb = response.body();
                    if (rb != null) {
                        try {
                            raw = rb.string();
                        } finally {
                            rb.close();
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.e("BookingCreateActivity", "Error reading response body: " + e.getMessage(), e);
                }

                if (raw == null) raw = "";
                android.util.Log.d("BookingCreateActivity", "Raw response JSON: " + raw);

                if (!response.isSuccessful()) {
                    Toast.makeText(BookingCreateActivity.this,
                            "Đặt lịch thất bại (HTTP " + response.code() + ")\n" + (raw.trim().isEmpty() ? "" : raw),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                // Parse success/message from arbitrary JSON
                boolean success = false;
                String message = null;
                String createdBookingId = null;

                try {
                    com.google.gson.JsonElement je = com.google.gson.JsonParser.parseString(raw);
                    if (je != null && je.isJsonObject()) {
                        com.google.gson.JsonObject jo = je.getAsJsonObject();

                        // ✅ Case 1: backend returns DatPhong object directly (no wrapper)
                        com.google.gson.JsonElement idEl = jo.get("DatPhongId");
                        if (idEl == null) idEl = jo.get("datPhongId");
                        if (idEl != null && idEl.isJsonPrimitive()) {
                            createdBookingId = idEl.getAsString();
                            if (createdBookingId != null && !createdBookingId.trim().isEmpty()) {
                                success = true;
                            }
                        }

                        // ✅ Case 2: backend returns wrapper success/message
                        if (!success) {
                            com.google.gson.JsonElement s = jo.get("success");
                            if (s == null) s = jo.get("Success");
                            if (s == null) s = jo.get("succeeded");
                            if (s == null) s = jo.get("Succeeded");
                            if (s != null && s.isJsonPrimitive()) {
                                try { success = s.getAsBoolean(); } catch (Exception ignored) {}
                            }
                        }

                        // message keys
                        com.google.gson.JsonElement m = jo.get("message");
                        if (m == null) m = jo.get("Message");
                        if (m == null) m = jo.get("detail");
                        if (m == null) m = jo.get("Detail");
                        if (m != null && m.isJsonPrimitive()) {
                            message = m.getAsString();
                        }

                        // errors keys (ASP.NET validation)
                        if ((message == null || message.trim().isEmpty())) {
                            com.google.gson.JsonElement errs = jo.get("errors");
                            if (errs == null) errs = jo.get("Errors");
                            if (errs != null) {
                                message = errs.toString();
                            }
                        }
                    }
                } catch (Exception e) {
                    android.util.Log.w("BookingCreateActivity", "Cannot parse booking response JSON, will fallback", e);
                    // if can't parse, assume success when body empty
                    success = raw.trim().isEmpty();
                }

                if (!success) {
                    if (message == null || message.trim().isEmpty()) {
                        message = raw.trim().isEmpty() ? "Vui lòng thử lại" : raw;
                    }
                    Toast.makeText(BookingCreateActivity.this, "Đặt lịch thất bại: " + message, Toast.LENGTH_LONG).show();
                    return;
                }

                android.util.Log.d("BookingCreateActivity", "✅ BOOKING SUCCESS! (RAW) id=" + createdBookingId);

                // ✅ Cache booking local để vẫn xem được lịch hẹn khi backend /my-bookings bị lỗi 500
                try {
                    com.example.QuanLyPhongTro_App.data.repository.BookingCache bc =
                            new com.example.QuanLyPhongTro_App.data.repository.BookingCache(BookingCreateActivity.this);
                    int before = bc.getAll().size();

                    String cacheId = createdBookingId;
                    if (cacheId == null || cacheId.trim().isEmpty()) {
                        cacheId = null;
                    }

                    String title = (currentRoom != null && currentRoom.getTitle() != null) ? currentRoom.getTitle() : "Phòng trọ";
                    String priceText = (currentRoom != null && currentRoom.getPriceValue() > 0) ? currentRoom.getFormattedPrice() : "";
                    String location = (currentRoom != null && currentRoom.getLocation() != null) ? currentRoom.getLocation() : "";

                    Booking cached = new Booking(
                            cacheId != null ? cacheId : ("local-" + System.currentTimeMillis()),
                            title,
                            priceText,
                            bookingDate,
                            selectedTimeSlot,
                            "pending",
                            fullName,
                            location
                    );

                    bc.addOrUpdate(cached);
                    int after = bc.getAll().size();
                    android.util.Log.d("BookingCreateActivity", "✅ Cached booking locally id=" + cached.getId() + " before=" + before + " after=" + after);
                } catch (Exception e) {
                    android.util.Log.w("BookingCreateActivity", "Cannot cache booking locally", e);
                }

                String msg = "Đặt lịch xem phòng thành công!\n" +
                        "Ngày: " + bookingDate + "\n" +
                        "Khung giờ: " + selectedTimeSlot + "\n" +
                        "Chủ trọ sẽ liên hệ với bạn sớm";

                new android.app.AlertDialog.Builder(BookingCreateActivity.this)
                        .setTitle("Đặt lịch thành công")
                        .setMessage(msg)
                        .setPositiveButton("OK", (dialog, which) -> {
                            setResult(RESULT_OK);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onFailure(@NonNull Call<okhttp3.ResponseBody> call, @NonNull Throwable t) {
                android.util.Log.e("BookingCreateActivity", "=== BOOKING API FAILURE (RAW) ===", t);

                btnConfirmBooking.setEnabled(true);
                btnConfirmBooking.setText("Xác nhận đặt lịch");

                Toast.makeText(BookingCreateActivity.this,
                        "Lỗi kết nối: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Gọi API lấy chi tiết phòng để màn đặt lịch hiển thị đúng dữ liệu
     */
    private void fetchRoomDetail(String phongId) {
        try {
            // ApiClient chỉ có getRetrofit()
            ApiService api = ApiClient.getRetrofit().create(ApiService.class);
            api.getRoomDetail(phongId).enqueue(new Callback<GenericResponse<Object>>() {
                @Override
                public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        android.util.Log.e(TAG, "getRoomDetail failed: http=" + response.code());
                        return;
                    }

                    // GenericResponse dùng field public 'data' (SerializedName: "Data")
                    Object data = response.body().data;
                    if (data == null) {
                        android.util.Log.w(TAG, "getRoomDetail: data is null");
                        return;
                    }

                    Room mapped = mapRoomFromApiData(data);
                    if (mapped != null) {
                        // bảo toàn phongId
                        mapped.setPhongId(phongId);
                        currentRoom = mapped;

                        // ✅ set lại address/location từ Phong model (nếu có)
                        String addr = null;
                        try {
                            java.lang.reflect.Method m = data.getClass().getMethod("getDiaChiNhaTro");
                            Object val = m.invoke(data);
                            if (val != null) addr = String.valueOf(val);
                        } catch (Exception ignore) {
                        }
                        if (addr != null && !addr.trim().isEmpty()) {
                            currentRoom.setAddress(addr);
                            currentRoom.setLocation(addr);
                        }

                        loadRoomData();
                    } else {
                        android.util.Log.w(TAG, "getRoomDetail: cannot map room from response");
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                    android.util.Log.e(TAG, "getRoomDetail onFailure: " + t.getMessage(), t);
                }
            });
        } catch (Exception e) {
            android.util.Log.e(TAG, "fetchRoomDetail error: " + e.getMessage(), e);
        }
    }

    /**
     * Map dữ liệu trả về từ /api/phong/{id} (thường là LinkedTreeMap) sang Room.
     * Chỉ map những field cần để hiển thị trên màn đặt lịch.
     */
    private Room mapRoomFromApiData(Object data) {
        try {
            Gson gson = new Gson();
            String json = gson.toJson(data);
            Phong phong = gson.fromJson(json, Phong.class);
            if (phong == null) return null;

            android.util.Log.d(TAG, "mapRoomFromApiData phong.giaTien=" + phong.getGiaTien());

            Room room = new Room();
            room.setPhongId(phong.getPhongId());
            room.setTitle(phong.getTieuDe());

            // ✅ giaTien là long, set vào priceValue (double)
            room.setPriceValue((double) phong.getGiaTien());

            // ✅ address/location ưu tiên field join sẵn trong Phong model
            if (phong.getDiaChiNhaTro() != null && !phong.getDiaChiNhaTro().trim().isEmpty()) {
                room.setAddress(phong.getDiaChiNhaTro());
                room.setLocation(phong.getDiaChiNhaTro());
            }
            String qh = null;
            try {
                java.lang.reflect.Method m = phong.getClass().getMethod("getTenQuanHuyen");
                Object val = m.invoke(phong);
                if (val != null) qh = String.valueOf(val);
            } catch (Exception ignore) {}
            String ph = null;
            try {
                java.lang.reflect.Method m = phong.getClass().getMethod("getTenPhuong");
                Object val = m.invoke(phong);
                if (val != null) ph = String.valueOf(val);
            } catch (Exception ignore) {}

            if ((qh != null && !qh.trim().isEmpty()) || (ph != null && !ph.trim().isEmpty())) {
                String loc = ((ph != null && !ph.trim().isEmpty()) ? ph : "") +
                        (((ph != null && !ph.trim().isEmpty()) && (qh != null && !qh.trim().isEmpty())) ? ", " : "") +
                        ((qh != null && !qh.trim().isEmpty()) ? qh : "");
                room.setLocation(loc);
            }

            // ✅ mô tả
            try {
                java.lang.reflect.Method m = phong.getClass().getMethod("getMoTa");
                Object val = m.invoke(phong);
                if (val != null) room.setDescription(String.valueOf(val));
            } catch (Exception ignore) {}

            android.util.Log.d(TAG, "mapRoomFromApiData room.priceValue=" + room.getPriceValue());
            return room;
        } catch (Exception e) {
            android.util.Log.e(TAG, "mapRoomFromApiData error: " + e.getMessage(), e);

            // fallback cực kỳ tolerant nếu backend trả map
            if (data instanceof LinkedTreeMap) {
                try {
                    LinkedTreeMap<?, ?> map = (LinkedTreeMap<?, ?>) data;
                    Room room = new Room();
                    Object title = map.get("tieuDe");
                    if (title == null) title = map.get("TieuDe");
                    if (title != null) room.setTitle(String.valueOf(title));
                    Object giaTien = map.get("giaTien");
                    if (giaTien == null) giaTien = map.get("GiaTien");
                    if (giaTien instanceof Number) room.setPriceValue(((Number) giaTien).doubleValue());
                    Object diaChi = map.get("diaChiNhaTro");
                    if (diaChi == null) diaChi = map.get("DiaChiNhaTro");
                    if (diaChi != null) room.setAddress(String.valueOf(diaChi));
                    return room;
                } catch (Exception ignore) {
                    return null;
                }
            }
            return null;
        }
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
