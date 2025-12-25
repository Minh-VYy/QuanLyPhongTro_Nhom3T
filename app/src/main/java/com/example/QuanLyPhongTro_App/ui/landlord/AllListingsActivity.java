package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.PhongDao;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.example.QuanLyPhongTro_App.utils.SessionManager;

import java.sql.Connection;
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
    private List<Phong> allPhongList = new ArrayList<>();
    private AllListingsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_listings);

        sessionManager = new SessionManager(this);
        
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
        new LoadAllListingsTask().execute();
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
                new DeletePhongTask().execute(phong.getPhongId(), phong.getTieuDe());
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Toggle trạng thái hoạt động
     */
    private void toggleListingActive(Phong phong, boolean isActive) {
        new ToggleActiveTask().execute(phong.getPhongId(), String.valueOf(isActive), phong.getTieuDe());
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

    // ==================== ASYNC TASKS ====================

    /**
     * AsyncTask để load tất cả tin đăng
     */
    private class LoadAllListingsTask extends AsyncTask<Void, Void, List<Phong>> {
        private String errorMsg = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected List<Phong> doInBackground(Void... voids) {
            Connection conn = null;
            try {
                Log.d(TAG, "=== LOADING ALL LISTINGS ===");
                conn = DatabaseHelper.getConnection();
                Log.d(TAG, "Database connection successful");
                
                String chuTroId = sessionManager.getUserId();
                Log.d(TAG, "Loading all listings for ChuTroId: " + chuTroId);
                
                PhongDao dao = new PhongDao();
                List<Phong> result = dao.getPhongByChuTroId(conn, chuTroId);
                
                Log.d(TAG, "Query result: " + (result != null ? result.size() : "null") + " listings");
                return result;
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading all listings: " + e.getMessage(), e);
                errorMsg = e.getMessage();
                return null;
            } finally {
                DatabaseHelper.closeConnection(conn);
                Log.d(TAG, "Database connection closed");
            }
        }

        @Override
        protected void onPostExecute(List<Phong> phongList) {
            super.onPostExecute(phongList);
            if (progressBar != null) {
                progressBar.setVisibility(View.GONE);
            }

            if (phongList != null) {
                allPhongList.clear();
                allPhongList.addAll(phongList);
                adapter.notifyDataSetChanged();
                
                updateListingCount(phongList.size());
                
                if (phongList.isEmpty()) {
                    showEmptyState();
                } else {
                    showListings();
                    Toast.makeText(AllListingsActivity.this, 
                        "Đã tải " + phongList.size() + " tin đăng", 
                        Toast.LENGTH_SHORT).show();
                }
            } else {
                showEmptyState();
                if (errorMsg != null) {
                    Toast.makeText(AllListingsActivity.this, 
                        "Lỗi kết nối: " + errorMsg, 
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(AllListingsActivity.this, 
                        "Không thể tải danh sách tin đăng", 
                        Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * AsyncTask để xóa phòng
     */
    private class DeletePhongTask extends AsyncTask<String, Void, Boolean> {
        private String errorMsg = null;
        private String deletedTitle = null;

        @Override
        protected Boolean doInBackground(String... params) {
            String phongId = params[0];
            deletedTitle = params[1];
            
            Connection conn = null;
            try {
                Log.d(TAG, "=== DELETING PHONG ===");
                Log.d(TAG, "PhongId: " + phongId);
                
                conn = DatabaseHelper.getConnection();
                String chuTroId = sessionManager.getUserId();
                
                ManagePhongDao dao = new ManagePhongDao();
                boolean result = dao.deletePhong(conn, phongId, chuTroId);
                
                Log.d(TAG, "Delete result: " + result);
                return result;
                
            } catch (Exception e) {
                Log.e(TAG, "Error deleting phong: " + e.getMessage(), e);
                errorMsg = e.getMessage();
                return false;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                Toast.makeText(AllListingsActivity.this, 
                    "✅ Đã xóa: " + deletedTitle, 
                    Toast.LENGTH_SHORT).show();
                
                // Refresh danh sách
                loadAllListings();
            } else {
                String message = "❌ Không thể xóa tin đăng";
                if (errorMsg != null) {
                    message += "\nLỗi: " + errorMsg;
                }
                Toast.makeText(AllListingsActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * AsyncTask để toggle trạng thái hoạt động
     */
    private class ToggleActiveTask extends AsyncTask<String, Void, Boolean> {
        private String errorMsg = null;
        private String phongTitle = null;
        private boolean newActiveState = false;

        @Override
        protected Boolean doInBackground(String... params) {
            String phongId = params[0];
            newActiveState = Boolean.parseBoolean(params[1]);
            phongTitle = params[2];
            
            Connection conn = null;
            try {
                Log.d(TAG, "=== TOGGLING PHONG ACTIVE ===");
                Log.d(TAG, "PhongId: " + phongId + ", Active: " + newActiveState);
                
                conn = DatabaseHelper.getConnection();
                String chuTroId = sessionManager.getUserId();
                
                ManagePhongDao dao = new ManagePhongDao();
                boolean result = dao.togglePhongActive(conn, phongId, chuTroId, newActiveState);
                
                Log.d(TAG, "Toggle result: " + result);
                return result;
                
            } catch (Exception e) {
                Log.e(TAG, "Error toggling phong active: " + e.getMessage(), e);
                errorMsg = e.getMessage();
                return false;
            } finally {
                DatabaseHelper.closeConnection(conn);
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                String status = newActiveState ? "kích hoạt" : "tắt";
                Toast.makeText(AllListingsActivity.this, 
                    "✅ Đã " + status + ": " + phongTitle, 
                    Toast.LENGTH_SHORT).show();
                
                // Refresh danh sách
                loadAllListings();
            } else {
                String message = "❌ Không thể thay đổi trạng thái";
                if (errorMsg != null) {
                    message += "\nLỗi: " + errorMsg;
                }
                Toast.makeText(AllListingsActivity.this, message, Toast.LENGTH_LONG).show();
            }
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
            
            // Xác định trạng thái active
            boolean isActive = phong.isDuyet() && !phong.isBiKhoa();
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
