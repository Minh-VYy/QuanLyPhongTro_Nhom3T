package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.regex.Pattern;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.DatabaseHelper;
import com.example.QuanLyPhongTro_App.data.dao.PhongDao;
import com.example.QuanLyPhongTro_App.data.model.Phong;
import com.example.QuanLyPhongTro_App.ui.auth.LoginActivity;
import com.example.QuanLyPhongTro_App.ui.tenant.MainActivity;
import com.example.QuanLyPhongTro_App.utils.SessionManager;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LandlordHomeActivity extends AppCompatActivity {
    private static final String TAG = "LandlordHomeActivity";

    private SessionManager sessionManager;
    private RecyclerView rvListings;
    private FloatingActionButton fabAddListing;
    private View roleSwitcher;
    private TextView txtRolePrimary;
    private ImageView iconRole;
    private View btnQuickAdd, btnQuickRequests, btnQuickStats;
    private ImageView btnMenu, btnMessages, btnFilter;
    private TextView btnViewAll;
    private ImageButton searchButton;
    private EditText searchInput;
    private FloatingActionButton fabTaoTin;
    private LinearLayout quickActionMenu;
    private boolean isMenuOpen = false;
    private ProgressBar progressBar;

    // Biến cho tìm kiếm và lọc
    private String currentStatusFilter = "all";
    private String currentActiveFilter = "all";
    private String currentKeyword = "";

    // Danh sách tin đăng
    private List<LandlordListing> allListings = new ArrayList<>();
    private List<LandlordListing> filteredListings = new ArrayList<>();
    private ListingAdapter adapter;

    // Interface cho callback
    public interface OnListingActionListener {
        void onEditClick(LandlordListing listing);
        void onDeleteClick(LandlordListing listing);
        void onToggleActive(LandlordListing listing, boolean isActive);
    }

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
        setupListings();
        setupSearchAndFilter();
        setupQuickActions();
        setupMenuActions();
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
        progressBar = findViewById(R.id.progressBar);

        // Khởi tạo các view tìm kiếm
        searchInput = findViewById(R.id.searchInput);
        searchButton = findViewById(R.id.searchButton);

        // DEBUG: Long click search button to open test activity
        searchButton.setOnLongClickListener(v -> {
            Intent intent = new Intent(LandlordHomeActivity.this, TestLandlordDatabaseActivity.class);
            startActivity(intent);
            return true;
        });
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
        fabTaoTin.setImageResource(R.drawable.ic_remove);
        quickActionMenu.setVisibility(View.VISIBLE);

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
        fabTaoTin.setImageResource(android.R.drawable.ic_input_add);

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
        findViewById(R.id.menu_add_listing).setOnClickListener(v -> {
            closeQuickActionMenu();
            startActivity(new Intent(this, EditTin.class));
        });

        findViewById(R.id.menu_edit_listing).setOnClickListener(v -> {
            closeQuickActionMenu();
            Toast.makeText(this, "Mở danh sách tin để chỉnh sửa", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AllListingsActivity.class));
        });

        findViewById(R.id.menu_delete_listing).setOnClickListener(v -> {
            closeQuickActionMenu();
            Toast.makeText(this, "Mở danh sách tin để xóa", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AllListingsActivity.class));
        });

        findViewById(R.id.menu_manage_requests).setOnClickListener(v -> {
            closeQuickActionMenu();
            Intent intent = new Intent(this, YeuCau.class);
            intent.putExtra("defaultTab", "yeucau");
            startActivity(intent);
        });

        // DEBUG: Thêm debug action vào menu
        findViewById(R.id.menu_add_listing).setOnLongClickListener(v -> {
            closeQuickActionMenu();
            Intent intent = new Intent(this, TestLandlordDatabaseActivity.class);
            startActivity(intent);
            return true;
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

    private void setupListings() {
        rvListings.setLayoutManager(new GridLayoutManager(this, 2));

        // Initialize lists
        allListings.clear();
        filteredListings.clear();

        adapter = new ListingAdapter(filteredListings, new OnListingActionListener() {
            @Override
            public void onEditClick(LandlordListing listing) {
                editListing(listing);
            }

            @Override
            public void onDeleteClick(LandlordListing listing) {
                deleteListing(listing);
            }

            @Override
            public void onToggleActive(LandlordListing listing, boolean isActive) {
                toggleListingActive(listing, isActive);
            }
        });

        rvListings.setAdapter(adapter);

        // Load dữ liệu từ database
        loadLandlordRoomsFromDatabase();
    }

    /**
     * Load danh sách phòng của chủ trọ từ database
     */
    private void loadLandlordRoomsFromDatabase() {
        new LoadLandlordPhongTask().execute();
    }

    /**
     * AsyncTask để load phòng của chủ trọ từ database
     */
    private class LoadLandlordPhongTask extends AsyncTask<Void, Void, List<Phong>> {
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
                Log.d(TAG, "=== LOADING LANDLORD ROOMS FROM DATABASE ===");
                conn = DatabaseHelper.getConnection();
                Log.d(TAG, "Database connection successful");

                // Lấy ChuTroId từ session
                String chuTroId = sessionManager.getUserId();
                Log.d(TAG, "Loading rooms for ChuTroId: " + chuTroId);

                PhongDao dao = new PhongDao();

                // Load danh sách phòng
                List<Phong> result = dao.getPhongByChuTroId(conn, chuTroId);
                Log.d(TAG, "Query result: " + (result != null ? result.size() : "null") + " rooms");

                return result;
            } catch (Exception e) {
                Log.e(TAG, "Error loading landlord rooms: " + e.getMessage(), e);
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
                // Convert Phong to LandlordListing và update adapter
                allListings.clear();
                filteredListings.clear();

                for (Phong phong : phongList) {
                    LandlordListing listing = convertPhongToLandlordListing(phong);
                    allListings.add(listing);
                    filteredListings.add(listing);
                }

                adapter.notifyDataSetChanged();

                // Cập nhật thống kê đơn giản
                updateListingCounts();

                if (phongList.isEmpty()) {
                    Toast.makeText(LandlordHomeActivity.this,
                        "Chưa có phòng nào. Hãy thêm phòng mới!",
                        Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LandlordHomeActivity.this,
                        "Đã tải " + phongList.size() + " phòng",
                        Toast.LENGTH_SHORT).show();
                }
            } else {
                if (errorMsg != null) {
                    Toast.makeText(LandlordHomeActivity.this,
                        "Lỗi kết nối: " + errorMsg,
                        Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(LandlordHomeActivity.this,
                        "Không thể tải dữ liệu phòng",
                        Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * Convert model Phong từ database sang LandlordListing (UI model)
     */
    private LandlordListing convertPhongToLandlordListing(Phong phong) {
        // Format giá tiền
        DecimalFormat formatter = new DecimalFormat("#,###");
        String priceText = formatter.format(phong.getGiaTien()) + " VNĐ/tháng";

        // Xác định trạng thái hiển thị
        String displayStatus;
        if (!phong.isDuyet()) {
            displayStatus = "Chờ duyệt";
        } else if (phong.isBiKhoa()) {
            displayStatus = "Bị khóa";
        } else {
            displayStatus = phong.getTrangThai() != null ? phong.getTrangThai() : "Chưa xác định";
        }

        // Xác định trạng thái active - Mặc định bật cho phòng không bị khóa
        // (Bỏ điều kiện isDuyet vì phòng đã tự động duyệt khi tạo)
        boolean isActive = !phong.isBiKhoa();

        return new LandlordListing(
            phong.getPhongId(),
            phong.getTieuDe(),
            priceText,
            phong.getGiaTien(),
            displayStatus,
            isActive,
            "default_room" // Tạm thời dùng ảnh mặc định
        );
    }

    private void updateListingCounts() {
        int total = allListings.size();
        int activeCount = 0;

        for (LandlordListing listing : allListings) {
            if (listing.isActive) {
                activeCount++;
            }
        }

        TextView tvTotal = findViewById(R.id.tv_total_listings);
        TextView tvActive = findViewById(R.id.tv_active_listings);

        if (tvTotal != null) tvTotal.setText(String.valueOf(total));
        if (tvActive != null) tvActive.setText(String.valueOf(activeCount));
    }

    public void afterTextChanged(Editable s) {
        currentKeyword = normalizeText(s.toString().trim());
    }


    private String normalizeText(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        return Pattern.compile("\\p{InCombiningDiacriticalMarks}+")
                .matcher(normalized)
                .replaceAll("")
                .toLowerCase();
    }

    private void setupSearchAndFilter() {
        // Tìm kiếm khi nhấn nút search
        searchButton.setOnClickListener(v -> {
            currentKeyword = normalizeText(searchInput.getText().toString().trim());
            applyAllFilters();
            hideKeyboard();
        });

        // Tìm kiếm khi nhấn Enter trên bàn phím
        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentKeyword = normalizeText(searchInput.getText().toString().trim());
                applyAllFilters();
                hideKeyboard();
                return true;
            }
            return false;
        });



        // Tìm kiếm realtime khi gõ (tùy chọn)
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                currentKeyword = s.toString().trim().toLowerCase();
            }
        });
    }


    private void applyAllFilters() {
        filteredListings.clear();

        // Áp dụng cả 3 loại filter: keyword, status, active
        for (LandlordListing listing : allListings) {
            // Kiểm tra keyword
            boolean matchesKeyword = currentKeyword.isEmpty()
                    || normalizeText(listing.title).contains(currentKeyword)
                    || normalizeText(listing.price).contains(currentKeyword)
                    || normalizeText(listing.status).contains(currentKeyword);

            // Kiểm tra status
            boolean matchesStatus = currentStatusFilter.equals("all") ||
                    listing.status.equals(currentStatusFilter);

            // Kiểm tra active
            boolean matchesActive = currentActiveFilter.equals("all") ||
                    (currentActiveFilter.equals("active") && listing.isActive) ||
                    (currentActiveFilter.equals("inactive") && !listing.isActive);

            if (matchesKeyword && matchesStatus && matchesActive) {
                filteredListings.add(listing);
            }
        }

        // Cập nhật adapter
        adapter.updateList(filteredListings);

        // Hiển thị số lượng kết quả
        showSearchResultCount();
    }

    private void showSearchResultCount() {
        if (!currentKeyword.isEmpty() ||
                !currentStatusFilter.equals("all") ||
                !currentActiveFilter.equals("all")) {

            String message = "Tìm thấy " + filteredListings.size() + " tin đăng";
            if (!currentKeyword.isEmpty()) {
                message += " với từ khóa: " + currentKeyword;
            }
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }



    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
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
            // DEBUG: Long click stats button to open test activity
            btnQuickStats.setOnLongClickListener(v -> {
                Intent intent = new Intent(this, TestLandlordDatabaseActivity.class);
                startActivity(intent);
                return true;
            });
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
            // DEBUG: Long click to open test manage phong activity
            btnViewAll.setOnLongClickListener(v -> {
                Intent intent = new Intent(this, TestManagePhongActivity.class);
                startActivity(intent);
                return true;
            });
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
        // Refresh data khi quay lại màn hình
        loadLandlordRoomsFromDatabase();
    }

    /**
     * Chỉnh sửa tin đăng
     */
    private void editListing(LandlordListing listing) {
        Intent intent = new Intent(this, EditTin.class);
        intent.putExtra("mode", "edit");
        intent.putExtra("phongId", listing.phongId);
        intent.putExtra("title", listing.title);
        intent.putExtra("price", listing.priceValue);
        intent.putExtra("description", ""); // Sẽ load từ database
        startActivityForResult(intent, 1001);
    }

    /**
     * Xóa tin đăng
     */
    private void deleteListing(LandlordListing listing) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc muốn xóa tin đăng \"" + listing.title + "\"?")
            .setPositiveButton("Xóa", (dialog, which) -> {
                new DeletePhongTask().execute(listing.phongId, listing.title);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    /**
     * Toggle trạng thái hoạt động
     */
    private void toggleListingActive(LandlordListing listing, boolean isActive) {
        new ToggleActiveTask().execute(listing.phongId, String.valueOf(isActive), listing.title);
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
                Toast.makeText(LandlordHomeActivity.this,
                    "✅ Đã xóa: " + deletedTitle,
                    Toast.LENGTH_SHORT).show();

                // Refresh danh sách
                loadLandlordRoomsFromDatabase();
            } else {
                String message = "❌ Không thể xóa tin đăng";
                if (errorMsg != null) {
                    message += "\nLỗi: " + errorMsg;
                }
                Toast.makeText(LandlordHomeActivity.this, message, Toast.LENGTH_LONG).show();
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
                Toast.makeText(LandlordHomeActivity.this,
                    "✅ Đã " + status + ": " + phongTitle,
                    Toast.LENGTH_SHORT).show();

                // Refresh danh sách
                loadLandlordRoomsFromDatabase();
            } else {
                String message = "❌ Không thể thay đổi trạng thái";
                if (errorMsg != null) {
                    message += "\nLỗi: " + errorMsg;
                }
                Toast.makeText(LandlordHomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            // Refresh danh sách sau khi edit
            loadLandlordRoomsFromDatabase();
        }
    }

    // ==================== INTERNAL CLASSES ====================

    static class LandlordListing {
        String phongId, title, price, status, imageName;
        double priceValue;
        boolean isActive;

        LandlordListing(String phongId, String title, String price, double priceValue, String status,
                        boolean isActive, String imageName) {
            this.phongId = phongId;
            this.title = title;
            this.price = price;
            this.priceValue = priceValue;
            this.status = status;
            this.isActive = isActive;
            this.imageName = imageName;
        }
    }


    interface OnListingClickListener {
        void onListingClick(LandlordListing listing);
    }

    static class ListingAdapter extends RecyclerView.Adapter<ListingAdapter.ViewHolder> {
        private List<LandlordListing> listings;
        private final OnListingActionListener actionListener;

        ListingAdapter(List<LandlordListing> listings, OnListingActionListener actionListener) {
            this.listings = listings;
            this.actionListener = actionListener;
        }

        // Thêm phương thức updateList
        public void updateList(List<LandlordListing> newList) {
            this.listings = newList;
            notifyDataSetChanged();
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

            // Switch listener
            holder.swActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                listing.isActive = isChecked;
                if (actionListener != null) {
                    actionListener.onToggleActive(listing, isChecked);
                }
            });

            // Edit button listener
            holder.btnEdit.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditClick(listing);
                }
            });

            // Delete button listener
            holder.btnDelete.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onDeleteClick(listing);
                }
            });

            // Item click listener (for viewing details)
            holder.itemView.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditClick(listing); // Mặc định click item = edit
                }
            });
        }

        @Override
        public int getItemCount() {
            return listings.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvStatus;
            Switch swActive;
            ImageView imgRoom;
            ImageButton btnEdit, btnDelete;

            ViewHolder(View itemView) {
                super(itemView);
                imgRoom = itemView.findViewById(R.id.img_room);
                tvTitle = itemView.findViewById(R.id.tv_title_item);
                tvPrice = itemView.findViewById(R.id.tv_price_item);
                tvStatus = itemView.findViewById(R.id.tv_status_item);
                swActive = itemView.findViewById(R.id.switch_active);
                btnEdit = itemView.findViewById(R.id.btn_edit);
                btnDelete = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}