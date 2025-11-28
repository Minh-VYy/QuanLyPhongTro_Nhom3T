package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.MockData;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.MainActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LandlordHomeActivity extends AppCompatActivity {

    private SessionManager sessionManager;
    private RecyclerView rvListings;
    private FloatingActionButton fabAddListing;
    private View roleSwitcher;
    private TextView txtRolePrimary;
    private ImageView iconRole;
    private View btnQuickAdd, btnQuickRequests, btnQuickStats;
    private ImageView btnMenu, btnMessages;
    private TextView btnViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_home);

        sessionManager = new SessionManager(this);
        
        // Kiểm tra đăng nhập - Chỉ cho phép landlord vào
        if (!sessionManager.isLoggedIn() || !"landlord".equals(sessionManager.getUserRole())) {
            Toast.makeText(this, "Vui lòng đăng nhập với tài khoản Chủ Trọ", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("targetRole", "landlord");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return;
        }

        initViews();
        setupRoleSwitcher();
        setupListings();
        setupQuickActions();
        setupBottomNavigation();
        setupFAB();
    }

    private void initViews() {
        rvListings = findViewById(R.id.rv_grid_listings);
        fabAddListing = findViewById(R.id.fab_tao_tin);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);

        // Quick action buttons
        btnQuickAdd = findViewById(R.id.btn_quick_add);
        btnQuickRequests = findViewById(R.id.btn_quick_requests);
        btnQuickStats = findViewById(R.id.btn_quick_stats);

        // Header buttons
        btnMenu = findViewById(R.id.btn_menu);
        btnMessages = findViewById(R.id.btn_messages);
        btnViewAll = findViewById(R.id.btn_view_all);
    }

    private void setupRoleSwitcher() {
        // Set landlord role display
        txtRolePrimary.setText("Chủ trọ");
        iconRole.setImageResource(R.drawable.ic_home);

        roleSwitcher.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Chọn giao diện")
                    .setItems(new String[]{"Người thuê", "Chủ trọ"}, (dialog, which) -> {
                        if (which == 0) {
                            // Switch to tenant
                            sessionManager.setDisplayRole("tenant");
                            Intent intent = new Intent(this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });
    }

    private void setupListings() {
        rvListings.setLayoutManager(new GridLayoutManager(this, 2));
        List<LandlordListing> listings = new ArrayList<>();

        // Load mock data
        List<MockData.LandlordData.ListingItem> mockListings = MockData.LandlordData.getListings();
        for (MockData.LandlordData.ListingItem item : mockListings) {
            listings.add(new LandlordListing(item.title, item.price, item.status, item.isActive));
        }

        ListingAdapter adapter = new ListingAdapter(listings, listing -> {
            // Open edit listing
            Intent intent = new Intent(this, EditTin.class);
            startActivity(intent);
        });
        rvListings.setAdapter(adapter);
    }

    private void setupQuickActions() {
        if (btnQuickAdd != null) {
            btnQuickAdd.setOnClickListener(v -> {
                Intent intent = new Intent(this, EditTin.class);
                startActivity(intent);
            });
        }

        if (btnQuickRequests != null) {
            btnQuickRequests.setOnClickListener(v -> {
                Intent intent = new Intent(this, YeuCau.class);
                startActivity(intent);
            });
        }

        if (btnQuickStats != null) {
            btnQuickStats.setOnClickListener(v -> {
                Intent intent = new Intent(this, LandlordStatsActivity.class);
                startActivity(intent);
            });
        }

        // Header buttons
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> {
                Toast.makeText(this, "Menu - Chức năng đang phát triển", Toast.LENGTH_SHORT).show();
            });
        }

        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                // Navigate to messages tab in YeuCau
                Intent intent = new Intent(this, YeuCau.class);
                intent.putExtra("defaultTab", "tinnhan");
                startActivity(intent);
            });
        }

        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(v -> {
                Toast.makeText(this, "Xem tất cả tin đăng", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void setupFAB() {
        fabAddListing.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditTin.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh bottom navigation when activity resumes
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    // Data model
    static class LandlordListing {
        String title, price, status;
        boolean isActive;

        LandlordListing(String title, String price, String status, boolean isActive) {
            this.title = title;
            this.price = price;
            this.status = status;
            this.isActive = isActive;
        }
    }

    // Adapter inner class
    interface OnListingClickListener {
        void onListingClick(LandlordListing listing);
    }

    static class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
        private final List<LandlordListing> listings;
        private final OnListingClickListener listener;

        ListingAdapter(List<LandlordListing> listings, OnListingClickListener listener) {
            this.listings = listings;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_landlord_listing, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            LandlordListing listing = listings.get(position);
            holder.tvTitle.setText(listing.title);
            holder.tvPrice.setText(listing.price);
            holder.tvStatus.setText(listing.status);
            holder.swActive.setChecked(listing.isActive);

            holder.swActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listing.isActive = isChecked;
                holder.tvStatus.setText(isChecked ? "Còn trống" : "Không hoạt động");
            });

            holder.itemView.setOnClickListener(v -> listener.onListingClick(listing));
        }

        @Override
        public int getItemCount() {
            return listings.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvStatus;
            Switch swActive;

            ViewHolder(View itemView) {
                super(itemView);
                tvTitle = itemView.findViewById(R.id.tv_title_item);
                tvPrice = itemView.findViewById(R.id.tv_price_item);
                tvStatus = itemView.findViewById(R.id.tv_status_item);
                swActive = itemView.findViewById(R.id.switch_active);
            }
        }
    }
}
