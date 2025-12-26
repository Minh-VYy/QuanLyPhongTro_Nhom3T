package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.repository.LandlordRoomRepository;
import com.example.QuanLyPhongTro_App.data.request.LandlordUpsertRoomRequest;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;

import java.io.IOException;

public class EditTin extends AppCompatActivity {
    private static final String TAG = "EditTin";
    private static final int REQ_PICK_IMAGE = 1001;
    private static final int REQ_PERMISSION_STORAGE = 1002;

    private ImageButton btnBack;
    private EditText edtTieuDe, edtGia, edtMoTa;
    private MaterialCardView areaPickImage;
    private TextView tvPickHint;
    private ImageView imgPreview;
    private Button btnSave;
    private Chip cbAc, cbWc;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    private LandlordRoomRepository landlordRoomRepository;

    // Edit mode variables
    private boolean isEditMode = false;
    private String editPhongId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_landlord_edit_tin);

            sessionManager = new SessionManager(this);
            landlordRoomRepository = new LandlordRoomRepository();
            ApiClient.setToken(sessionManager.getToken());

            // Kiểm tra chế độ edit
            Intent intent = getIntent();
            if (intent != null) {
                String mode = intent.getStringExtra("mode");
                if ("edit".equals(mode)) {
                    isEditMode = true;
                    editPhongId = intent.getStringExtra("phongId");

                    Log.d(TAG, "Edit mode activated for PhongId: " + editPhongId);

                    // Cập nhật title
                    TextView tvTitle = findViewById(R.id.tv_title_edit);
                    if (tvTitle != null) {
                        tvTitle.setText("Chỉnh sửa tin đăng");
                    }
                }
            }

            // Khởi tạo views
            initViews();

            // Load dữ liệu để edit nếu cần
            if (isEditMode && editPhongId != null) {
                loadDataForEdit();
            }

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void initViews() {
        try {
            //ánh xạ đến layout
            btnBack = findViewById(R.id.btn_back_edit);
            edtTieuDe = findViewById(R.id.edt_tieude);
            edtGia = findViewById(R.id.edt_gia);
            edtMoTa = findViewById(R.id.edt_mota);
            areaPickImage = findViewById(R.id.area_pick_image);
            tvPickHint = findViewById(R.id.tv_pick_hint);
            imgPreview = findViewById(R.id.img_preview);
            btnSave = findViewById(R.id.btn_save_tin);
            cbAc = findViewById(R.id.cb_ac);
            cbWc = findViewById(R.id.cb_wc);
            progressBar = findViewById(R.id.progressBar);

            // Null checks để tránh crash
            if (btnBack == null || edtTieuDe == null || edtGia == null || edtMoTa == null ||
                    areaPickImage == null || tvPickHint == null || imgPreview == null ||
                    btnSave == null || cbAc == null || cbWc == null) {
                Toast.makeText(this, "Lỗi: Không thể tải giao diện", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            btnBack.setOnClickListener(v -> finish());

            areaPickImage.setOnClickListener(v -> {
                // Kiểm tra quyền trước khi mở gallery
                if (checkStoragePermission()) {
                    openImagePicker();
                } else {
                    requestStoragePermission();
                }
            });

            btnSave.setOnClickListener(v -> savePhongToDatabase());

        } catch (Exception e) {
            Log.e(TAG, "Error in initViews: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khởi tạo giao diện: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    /**
     * Load dữ liệu để edit
     */
    private void loadDataForEdit() {
        try {
            if (editPhongId == null || editPhongId.isEmpty()) {
                Log.e(TAG, "EditPhongId is null or empty");
                Toast.makeText(this, "Lỗi: Không có ID phòng để chỉnh sửa", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            Log.d(TAG, "Loading data for edit - PhongId: " + editPhongId);

            // Load từ Intent trước (nhanh hơn) - chỉ để hiển thị tạm
            Intent intent = getIntent();
            if (intent != null) {
                String title = intent.getStringExtra("title");
                double price = intent.getDoubleExtra("price", 0);
                String description = intent.getStringExtra("description");

                Log.d(TAG, "Intent data - Title: " + title + ", Price: " + price);

                if (title != null && !title.isEmpty() && edtTieuDe != null) {
                    edtTieuDe.setText(title);
                }
                if (price > 0 && edtGia != null) {
                    edtGia.setText(String.valueOf((long) price));
                }
                if (description != null && !description.isEmpty() && edtMoTa != null) {
                    edtMoTa.setText(description);
                }
            }

            // Cập nhật button text ngay lập tức
            if (btnSave != null) {
                btnSave.setText("Cập nhật tin đăng");
            }

            // Không load từ database nữa để tránh crash - chỉ dùng dữ liệu từ Intent
            Toast.makeText(this, "✅ Đã tải dữ liệu để chỉnh sửa", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error in loadDataForEdit: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi load dữ liệu: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Kiểm tra quyền đọc storage
     */
    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ sử dụng READ_MEDIA_IMAGES
            return ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            // Android 12 trở xuống sử dụng READ_EXTERNAL_STORAGE
            return ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    /**
     * Yêu cầu quyền đọc storage
     */
    private void requestStoragePermission() {
        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[]{android.Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        ActivityCompat.requestPermissions(this, permissions, REQ_PERMISSION_STORAGE);
    }

    /**
     * Mở image picker
     */
    private void openImagePicker() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

            // Kiểm tra có app nào handle được intent này không
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, REQ_PICK_IMAGE);
            } else {
                // Fallback: sử dụng Intent.ACTION_GET_CONTENT
                Intent fallbackIntent = new Intent(Intent.ACTION_GET_CONTENT);
                fallbackIntent.setType("image/*");
                fallbackIntent.addCategory(Intent.CATEGORY_OPENABLE);

                if (fallbackIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(Intent.createChooser(fallbackIntent, "Chọn ảnh"), REQ_PICK_IMAGE);
                } else {
                    Toast.makeText(this, "Không tìm thấy ứng dụng để chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening image picker: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi mở thư viện ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQ_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, mở image picker
                openImagePicker();
            } else {
                // Quyền bị từ chối
                Toast.makeText(this, "Cần quyền truy cập ảnh để chọn hình", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Lưu phòng vào database
     */
    private void savePhongToDatabase() {
        String tieude = edtTieuDe.getText().toString().trim();
        String giaStr = edtGia.getText().toString().trim();
        String mota = edtMoTa.getText().toString().trim();

        // Validate input
        if (tieude.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tiêu đề", Toast.LENGTH_SHORT).show();
            edtTieuDe.requestFocus();
            return;
        }

        if (giaStr.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập giá", Toast.LENGTH_SHORT).show();
            edtGia.requestFocus();
            return;
        }

        long giaTien;
        try {
            giaTien = Long.parseLong(giaStr);
            if (giaTien <= 0) {
                Toast.makeText(this, "Giá phải lớn hơn 0", Toast.LENGTH_SHORT).show();
                edtGia.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            edtGia.requestFocus();
            return;
        }

        // Kiểm tra đăng nhập
        if (!sessionManager.isLoggedIn() || !"landlord".equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Vui lòng đăng nhập với tài khoản chủ trọ", Toast.LENGTH_LONG).show();
            return;
        }

        // Resolve NhaTroId (required by backend CreatePhongRequest)
        resolveMyHouseIdAndSave(tieude, giaTien, mota);
    }

    private void resolveMyHouseIdAndSave(String tieude, long giaTien, String mota) {
        btnSave.setEnabled(false);
        btnSave.setText(isEditMode ? "Đang cập nhật..." : "Đang lưu...");
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        ApiService api = ApiClient.getRetrofit().create(ApiService.class);
        api.getMyHouses().enqueue(new retrofit2.Callback<java.util.List<Object>>() {
            @Override
            public void onResponse(retrofit2.Call<java.util.List<Object>> call, retrofit2.Response<java.util.List<Object>> response) {
                if (!response.isSuccessful() || response.body() == null || response.body().isEmpty()) {
                    runOnUiThread(() -> {
                        restoreUi();
                        Toast.makeText(EditTin.this, "❌ Không tìm thấy nhà trọ của bạn (my-houses). Code: " + response.code(), Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                // pick first house (simple assumption)
                String nhaTroId = null;
                try {
                    com.google.gson.Gson gson = new com.google.gson.Gson();
                    java.util.Map<String, Object> map = gson.fromJson(gson.toJson(response.body().get(0)), java.util.Map.class);
                    Object id = map.get("NhaTroId");
                    if (id == null) id = map.get("nhaTroId");
                    if (id == null) id = map.get("Id");
                    if (id == null) id = map.get("id");
                    if (id != null) nhaTroId = id.toString();
                } catch (Exception ignore) {}

                if (nhaTroId == null || nhaTroId.trim().isEmpty()) {
                    String finalNhaTroId = nhaTroId;
                    runOnUiThread(() -> {
                        restoreUi();
                        Toast.makeText(EditTin.this, "❌ Không đọc được NhaTroId từ my-houses", Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                LandlordUpsertRoomRequest request = new LandlordUpsertRoomRequest(nhaTroId, tieude, giaTien, mota);

                if (isEditMode && editPhongId != null) {
                    landlordRoomRepository.updateRoom(editPhongId, request, new LandlordRoomRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                restoreUi();
                                Toast.makeText(EditTin.this, "✅ Đã cập nhật tin: " + tieude, Toast.LENGTH_LONG).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> {
                                restoreUi();
                                Toast.makeText(EditTin.this, "❌ Không thể cập nhật tin\nLỗi: " + message, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                } else {
                    landlordRoomRepository.createRoom(request, new LandlordRoomRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                restoreUi();
                                Toast.makeText(EditTin.this, "✅ Đã lưu tin: " + tieude, Toast.LENGTH_LONG).show();
                                setResult(Activity.RESULT_OK);
                                finish();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> {
                                restoreUi();
                                Toast.makeText(EditTin.this, "❌ Không thể lưu tin\nLỗi: " + message, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                }
            }

            @Override
            public void onFailure(retrofit2.Call<java.util.List<Object>> call, Throwable t) {
                runOnUiThread(() -> {
                    restoreUi();
                    Toast.makeText(EditTin.this, "❌ Lỗi mạng khi lấy my-houses: " + t.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void restoreUi() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        btnSave.setEnabled(true);
        btnSave.setText("Lưu và đăng tin");
    }

    //Hàm chọn ảnh
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    Log.d(TAG, "Selected image URI: " + uri.toString());

                    // Sử dụng phương thức an toàn hơn để load ảnh
                    Bitmap bmp;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        // Android 9+ sử dụng ImageDecoder
                        bmp = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                    } else {
                        // Android 8 trở xuống sử dụng MediaStore
                        bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    }

                    // Resize ảnh nếu quá lớn để tránh OutOfMemoryError
                    Bitmap resizedBmp = resizeImageIfNeeded(bmp);

                    // Hiển thị ảnh
                    imgPreview.setImageBitmap(resizedBmp);
                    imgPreview.setVisibility(View.VISIBLE);
                    tvPickHint.setVisibility(View.GONE);

                    // Cleanup bitmap gốc nếu đã resize
                    if (resizedBmp != bmp) {
                        bmp.recycle();
                    }

                    Toast.makeText(this, "✅ Đã chọn ảnh thành công", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    Log.e(TAG, "Error loading image: " + e.getMessage(), e);
                    Toast.makeText(this, "❌ Không thể đọc ảnh: " + e.getMessage(), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e(TAG, "Unexpected error loading image: " + e.getMessage(), e);
                    Toast.makeText(this, "❌ Lỗi không xác định khi tải ảnh", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "❌ Không thể lấy ảnh đã chọn", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == REQ_PICK_IMAGE) {
            // User cancelled or error occurred
            Log.d(TAG, "Image picker cancelled or failed. ResultCode: " + resultCode);
        }
    }

    /**
     * Resize ảnh nếu quá lớn để tránh OutOfMemoryError
     */
    private Bitmap resizeImageIfNeeded(Bitmap original) {
        final int MAX_WIDTH = 1024;
        final int MAX_HEIGHT = 1024;

        int width = original.getWidth();
        int height = original.getHeight();

        // Kiểm tra xem có cần resize không
        if (width <= MAX_WIDTH && height <= MAX_HEIGHT) {
            return original;
        }

        // Tính tỷ lệ resize
        float ratio = Math.min((float) MAX_WIDTH / width, (float) MAX_HEIGHT / height);
        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        Log.d(TAG, "Resizing image from " + width + "x" + height + " to " + newWidth + "x" + newHeight);

        return Bitmap.createScaledBitmap(original, newWidth, newHeight, true);
    }
}

