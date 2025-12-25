package com.example.QuanLyPhongTro_App.ui.landlord;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

/**
 * Màn hình chính cho Chủ trọ (Landlord)
 * Hiển thị danh sách tin đăng và các chức năng quản lý.
 */
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
    private FloatingActionButton fabTaoTin;
    private LinearLayout quickActionMenu;
    private boolean isMenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_home);

        sessionManager = new SessionManager(this);
        
        // Kiểm tra quyền truy cập
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
        setupListings(); // Load danh sách tin đăng từ MockData
        setupQuickActions();
        setupBottomNavigation();
        setupFAB();
        setupMessagesButton();
    }


    private void initViews() {
        rvListings = findViewById(R.id.rv_grid_listings);
        roleSwitcher = findViewById(R.id.roleSwitcher);
        txtRolePrimary = roleSwitcher.findViewById(R.id.txtRolePrimary);
        iconRole = roleSwitcher.findViewById(R.id.iconRole);

        btnQuickAdd = findViewById(R.id.btn_quick_add);
        btnQuickRequests = findViewById(R.id.btn_quick_requests);
        btnQuickStats = findViewById(R.id.btn_quick_stats);

        btnMenu = findViewById(R.id.btn_menu);
        btnMessages = findViewById(R.id.btn_messages);
        btnViewAll = findViewById(R.id.btn_view_all);
        fabTaoTin = findViewById(R.id.fab_tao_tin);
        quickActionMenu = findViewById(R.id.quick_action_menu);
    }

    private void setupMessagesButton() {
        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                Intent intent = new Intent(LandlordHomeActivity.this, LandlordChatListActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupFAB() {
        fabTaoTin.setOnClickListener(v -> {
            if (isMenuOpen) {
                closeQuickActionMenu();
            } else {
                openQuickActionMenu();
            }
        });
    }

    private void openQuickActionMenu() {
        isMenuOpen = true;

        // Thay đổi icon FAB thành dấu trừ
        fabTaoTin.setImageResource(R.drawable.ic_remove);

        // Hiển thị menu
        quickActionMenu.setVisibility(View.VISIBLE);

        // Animation hiện menu từ dưới lên
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(quickActionMenu, "scaleX", 0.8f, 1f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(quickActionMenu, "scaleY", 0.8f, 1f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(quickActionMenu, "alpha", 0f, 1f);

        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(250);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.start();
    }

    private void closeQuickActionMenu() {
        isMenuOpen = false;

        // Thay đổi icon FAB thành dấu cộng
        fabTaoTin.setImageResource(android.R.drawable.ic_input_add);

        // Animation ẩn menu
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(quickActionMenu, "scaleX", 1f, 0.8f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(quickActionMenu, "scaleY", 1f, 0.8f);
        ObjectAnimator alpha = ObjectAnimator.ofFloat(quickActionMenu, "alpha", 1f, 0f);

        animatorSet.playTogether(scaleX, scaleY, alpha);
        animatorSet.setDuration(200);
        animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                quickActionMenu.setVisibility(View.GONE);
            }
        });
        animatorSet.start();
    }

    private void setupMenuActions() {
        // Thêm tin mới
        findViewById(R.id.menu_add_listing).setOnClickListener(v -> {
            closeQuickActionMenu();
            startActivity(new Intent(this, EditTin.class));
        });

        // Chỉnh sửa tin
        findViewById(R.id.menu_edit_listing).setOnClickListener(v -> {
            closeQuickActionMenu();
            // Mở activity chỉnh sửa hoặc hiển thị dialog chọn tin để sửa
            Toast.makeText(this, "Mở danh sách tin để chỉnh sửa", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AllListingsActivity.class));
        });

        // Xóa tin
        findViewById(R.id.menu_delete_listing).setOnClickListener(v -> {
            closeQuickActionMenu();
            // Mở activity xóa tin
            Toast.makeText(this, "Mở danh sách tin để xóa", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AllListingsActivity.class));
        });

        // Quản lý yêu cầu
        findViewById(R.id.menu_manage_requests).setOnClickListener(v -> {
            closeQuickActionMenu();
            Intent intent = new Intent(this, YeuCau.class);
            intent.putExtra("defaultTab", "yeucau");
            startActivity(intent);
        });
    }


    private void setupRoleSwitcher() {
        txtRolePrimary.setText("Chủ trọ");
        iconRole.setImageResource(R.drawable.ic_home);

        roleSwitcher.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Chọn giao diện")
                    .setItems(new String[]{"Người thuê", "Chủ trọ"}, (dialog, which) -> {
                        if (which == 0) {
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

    /**
     * Lấy dữ liệu tin đăng từ MockData và hiển thị lên RecyclerView
     */
    private void setupListings() {
        rvListings.setLayoutManager(new GridLayoutManager(this, 2));

        List<LandlordListing> listings = new ArrayList<>();

        // Lấy dữ liệu từ MockData (đã cập nhật)
        List<MockData.LandlordData.ListingItem> mockListings = MockData.LandlordData.getListings();

        // Map dữ liệu từ MockData sang model của Activity này (nếu cần thiết)
        for (MockData.LandlordData.ListingItem item : mockListings) {
            listings.add(new LandlordListing(
                    item.title,
                    item.price,
                    item.status,
                    item.isActive
            ));
        }

        ListingAdapter adapter = new ListingAdapter(listings, listing -> {
            Intent intent = new Intent(this, EditTin.class);
            startActivity(intent);
        });

        rvListings.setAdapter(adapter);
    }

    private void setupQuickActions() {
        if (btnQuickAdd != null) {
            btnQuickAdd.setOnClickListener(v -> startActivity(new Intent(this, EditTin.class)));
        }
        if (btnQuickRequests != null) {
            btnQuickRequests.setOnClickListener(v -> startActivity(new Intent(this, YeuCau.class)));
        }
        if (btnQuickStats != null) {
            btnQuickStats.setOnClickListener(v -> startActivity(new Intent(this, LandlordStatsActivity.class)));
        }
        if (btnMenu != null) {
            btnMenu.setOnClickListener(v -> Toast.makeText(this, "Menu", Toast.LENGTH_SHORT).show());
        }
        if (btnMessages != null) {
            btnMessages.setOnClickListener(v -> {
                Intent intent = new Intent(this, YeuCau.class);
                intent.putExtra("defaultTab", "tinnhan");
                startActivity(intent);
            });
        }
        if (btnViewAll != null) {
            btnViewAll.setOnClickListener(v -> startActivity(new Intent(this, AllListingsActivity.class)));
        }
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "home");
    }



    private void showUtilityDialog() {
        // Utility dialog removed - show toast instead
        Toast.makeText(this, "Tính năng đang phát triển", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "home");
    }

    // ==================== INTERNAL CLASSES ====================

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

            // Đổi màu trạng thái
            int colorRes = R.color.black;
            if ("Còn trống".equals(listing.status)) colorRes = R.color.success;
            else if ("Đã thuê".equals(listing.status)) colorRes = R.color.error;
            else if ("Chờ xử lý".equals(listing.status)) colorRes = R.color.warning;
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(colorRes));

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
