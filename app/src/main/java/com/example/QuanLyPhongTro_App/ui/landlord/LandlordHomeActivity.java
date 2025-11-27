package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.MockData;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_home);

        sessionManager = new SessionManager(this);
        
        // Auto-login as guest landlord for demo
        if (!sessionManager.isLoggedIn()) {
            sessionManager.createLoginSession("guest_landlord", "Chủ Trọ Demo", "landlord@example.com", "landlord");
            sessionManager.setLandlordStatus(true);
        }

        initViews();
        setupRoleSwitcher();
        setupListings();
        setupBottomNavigation();
        setupFAB();
    }

    private void initViews() {
        rvListings = findViewById(R.id.rv_grid_listings);
        fabAddListing = findViewById(R.id.fab_tao_tin);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);
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

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    private void setupFAB() {
        fabAddListing.setOnClickListener(v -> {
            showUtilityDialog();//hiển thị danh sách tiện ích
        });
    }

    private void showUtilityDialog() {
        UtilityDialog dialog = new UtilityDialog(this);
        dialog.show();

        // Tuỳ chọn: set kích thước cho dialog
        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
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
