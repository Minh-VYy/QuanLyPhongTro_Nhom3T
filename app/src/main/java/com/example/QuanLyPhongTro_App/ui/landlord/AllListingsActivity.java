package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.data.repository.LandlordRoomRepository;
import com.example.QuanLyPhongTro_App.data.repository.RoomRepository;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class AllListingsActivity extends AppCompatActivity {
    private static final String TAG = "AllListingsActivity";

    private LinearLayout layoutEmptyState;
    private TextView tvListingCount;
    private ImageView btnBack;
    private Spinner spinnerFilter;
    private Button btnCreateFirstListing;
    private RecyclerView rvAllListings;
    private ProgressBar progressBar;
    
    private SessionManager sessionManager;
    private LandlordRoomRepository landlordRoomRepository;
    private List<Phong> allPhongList = new ArrayList<>();
    private AllListingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_listings);

        sessionManager = new SessionManager(this);
        landlordRoomRepository = new LandlordRoomRepository();

        initViews();
        setupFilter();
        setupRecyclerView();
        setupBottomNavigation();
        
        // Load dữ liệu
        loadAllListings();
    }

    private void initViews() {
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        tvListingCount = findViewById(R.id.tv_listing_count);
        btnBack = findViewById(R.id.btn_back);
        spinnerFilter = findViewById(R.id.spinner_filter);
        btnCreateFirstListing = findViewById(R.id.btn_create_first_listing);
        rvAllListings = findViewById(R.id.rv_all_listings);
        progressBar = findViewById(R.id.progressBar);

        btnBack.setOnClickListener(v -> finish());

        btnCreateFirstListing.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditTin.class);
            startActivity(intent);
        });
    }

    private void setupFilter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.listing_filter_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        rvAllListings.setLayoutManager(new LinearLayoutManager(this));
        
        adapter = new AllListingsAdapter(allPhongList, new AllListingsAdapter.OnListingActionListener() {
            @Override
            public void onEditClick(Phong phong) {
                editListing(phong);
            }

            @Override
            public void onDeleteClick(Phong phong) {
                deleteListing(phong);
            }

            @Override
            public void onToggleActive(Phong phong, boolean isActive) {
                toggleListingActive(phong, isActive);
            }
        });
        
        rvAllListings.setAdapter(adapter);
    }

    private void loadAllListings() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        String chuTroId = sessionManager.getUserId();
        if (chuTroId == null || chuTroId.trim().isEmpty()) {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            showEmptyState();
            Toast.makeText(this, "Không xác định được tài khoản chủ trọ", Toast.LENGTH_LONG).show();
            return;
        }

        landlordRoomRepository.getMyRooms(chuTroId, new LandlordRoomRepository.ListRoomsCallback() {
            @Override
            public void onSuccess(List<RoomRepository.RoomDto> rooms) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);

                    allPhongList.clear();
                    for (RoomRepository.RoomDto dto : rooms) {
                        Phong p = new Phong();
                        p.setPhongId(dto.getPhongId());
                        p.setTieuDe(dto.getTieuDe());
                        p.setMoTa(dto.getMoTa());
                        p.setGiaTien(dto.getGiaTien());
                        p.setTrangThai(dto.getTrangThai());
                        // Approx: active if status is not "Không hoạt động"
                        boolean isActive = dto.getTrangThai() == null || !"Không hoạt động".equalsIgnoreCase(dto.getTrangThai());
                        p.setBiKhoa(!isActive);
                        p.setDuyet(true);
                        allPhongList.add(p);
                    }

                    adapter.notifyDataSetChanged();
                    updateListingCount(allPhongList.size());

                    if (allPhongList.isEmpty()) {
                        showEmptyState();
                    } else {
                        showListings();
                    }
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    showEmptyState();
                    Toast.makeText(AllListingsActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void updateListingCount(int count) {
        String countText = count + " tin";
        tvListingCount.setText(countText);
    }

    private void showEmptyState() {
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
        if (rvAllListings != null) {
            rvAllListings.setVisibility(View.GONE);
        }
    }

    private void showListings() {
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(View.GONE);
        }
        if (rvAllListings != null) {
            rvAllListings.setVisibility(View.VISIBLE);
        }
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }

    /**
     * Chỉnh sửa tin đăng
     */
    private void editListing(Phong phong) {
        Intent intent = new Intent(this, EditTin.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("phongId", phong.getPhongId());
        intent.putExtra("title", phong.getTieuDe());
        intent.putExtra("price", (double)phong.getGiaTien());
        intent.putExtra("description", phong.getMoTa() != null ? phong.getMoTa() : "");
        startActivityForResult(intent, 1001);
    }

    /**
     * Xóa tin đăng
     */
    private void deleteListing(Phong phong) {
        new AlertDialog.Builder(this)
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa tin đăng \"" + phong.getTieuDe() + "\"?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    String chuTroId = sessionManager.getUserId();
                    if (chuTroId == null) {
                        Toast.makeText(this, "Không xác định được chủ trọ", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
                    landlordRoomRepository.deleteRoom(phong.getPhongId(), chuTroId, new LandlordRoomRepository.SimpleCallback() {
                        @Override
                        public void onSuccess() {
                            runOnUiThread(() -> {
                                if (progressBar != null) progressBar.setVisibility(View.GONE);
                                Toast.makeText(AllListingsActivity.this, "✅ Đã xóa: " + phong.getTieuDe(), Toast.LENGTH_SHORT).show();
                                loadAllListings();
                            });
                        }

                        @Override
                        public void onError(String message) {
                            runOnUiThread(() -> {
                                if (progressBar != null) progressBar.setVisibility(View.GONE);
                                Toast.makeText(AllListingsActivity.this, message, Toast.LENGTH_LONG).show();
                            });
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    /**
     * Toggle trạng thái hoạt động
     */
    private void toggleListingActive(Phong phong, boolean isActive) {
        String chuTroId = sessionManager.getUserId();
        if (chuTroId == null) {
            Toast.makeText(this, "Không xác định được chủ trọ", Toast.LENGTH_LONG).show();
            return;
        }

        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        landlordRoomRepository.toggleActive(phong.getPhongId(), isActive, chuTroId, new LandlordRoomRepository.SimpleCallback() {
            @Override
            public void onSuccess() {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    String status = isActive ? "kích hoạt" : "tắt";
                    Toast.makeText(AllListingsActivity.this, "✅ Đã " + status + ": " + phong.getTieuDe(), Toast.LENGTH_SHORT).show();
                    loadAllListings();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    if (progressBar != null) progressBar.setVisibility(View.GONE);
                    Toast.makeText(AllListingsActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            // Refresh danh sách sau khi edit
            loadAllListings();
        }
    }

    // ==================== ADAPTER ====================

    static class AllListingsAdapter extends RecyclerView.Adapter<AllListingsAdapter.ViewHolder> {
        private List<Phong> phongList;
        private final OnListingActionListener actionListener;

        interface OnListingActionListener {
            void onEditClick(Phong phong);
            void onDeleteClick(Phong phong);
            void onToggleActive(Phong phong, boolean isActive);
        }

        AllListingsAdapter(List<Phong> phongList, OnListingActionListener actionListener) {
            this.phongList = phongList;
            this.actionListener = actionListener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_all_listing, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Phong phong = phongList.get(position);

            holder.tvTitle.setText(phong.getTieuDe());
            
            // Format giá tiền
            DecimalFormat formatter = new DecimalFormat("#,###");
            String priceText = formatter.format(phong.getGiaTien()) + " VNĐ/tháng";
            holder.tvPrice.setText(priceText);
            
            // Xác định trạng thái hiển thị
            String displayStatus;
            if (!phong.isDuyet()) {
                displayStatus = "Chờ duyệt";
            } else if (phong.isBiKhoa()) {
                displayStatus = "Bị khóa";
            } else {
                displayStatus = phong.getTrangThai() != null ? phong.getTrangThai() : "Chưa xác định";
            }
            holder.tvStatus.setText(displayStatus);
            
            // Xác định trạng thái active - Mặc định bật cho phòng không bị khóa
            // (Bỏ điều kiện isDuyet vì phòng đã tự động duyệt khi tạo)
            boolean isActive = !phong.isBiKhoa();
            holder.swActive.setChecked(isActive);

            // Đổi màu trạng thái
            int colorRes = R.color.black;
            if ("Còn trống".equals(displayStatus)) colorRes = R.color.success;
            else if ("Đã thuê".equals(displayStatus)) colorRes = R.color.error;
            else if ("Chờ duyệt".equals(displayStatus)) colorRes = R.color.warning;
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(colorRes));

            // Switch listener
            holder.swActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (actionListener != null) {
                    actionListener.onToggleActive(phong, isChecked);
                }
            });

            // Edit button listener
            holder.btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditClick(phong);
                }
            });

            // Delete button listener
            holder.btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteClick(phong);
                }
            });
        }

        @Override
        public int getItemCount() {
            return phongList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvStatus;
            Switch swActive;
            ImageButton btnEdit, btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvPrice = itemView.findViewById(R.id.tv_price);
                tvStatus = itemView.findViewById(R.id.tv_status);
                swActive = itemView.findViewById(R.id.switch_active);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}
