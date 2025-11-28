package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.MockData;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;
import java.util.List;

public class AllListingsActivity extends AppCompatActivity {

    private RecyclerView rvAllListings;
    private LinearLayout layoutEmptyState;
    private TextView tvListingCount;
    private ImageView btnBack;
    private Spinner spinnerFilter;
    private Button btnCreateFirstListing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_listings);

        initViews();
        setupFilter();
        setupListings();
        setupBottomNavigation();
    }

    private void initViews() {
        rvAllListings = findViewById(R.id.rv_all_listings);
        layoutEmptyState = findViewById(R.id.layout_empty_state); // ĐÃ THAY ĐỔI
        tvListingCount = findViewById(R.id.tv_listing_count);
        btnBack = findViewById(R.id.btn_back);
        spinnerFilter = findViewById(R.id.spinner_filter);
        btnCreateFirstListing = findViewById(R.id.btn_create_first_listing);

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> finish());

        // Xử lý nút tạo tin đầu tiên
        btnCreateFirstListing.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditTin.class);
            startActivity(intent);
        });
    }

    private void setupFilter() {
        // Tạo adapter cho spinner filter
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.listing_filter_options,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFilter.setAdapter(adapter);
    }

    private void setupListings() {
        // Lấy danh sách tin đăng từ MockData
        List<MockData.LandlordData.ListingItem> listings = MockData.LandlordData.getListings();

        // Cập nhật số lượng tin
        updateListingCount(listings.size());

        if (listings.isEmpty()) {
            showEmptyState();
        } else {
            showListings(listings);
        }
    }

    private void updateListingCount(int count) {
        String countText = count + " tin";
        tvListingCount.setText(countText);
    }

    private void showEmptyState() {
        layoutEmptyState.setVisibility(View.VISIBLE); // SỬ DỤNG layout_empty_state
        rvAllListings.setVisibility(View.GONE);
    }

    private void showListings(List<MockData.LandlordData.ListingItem> listings) {
        layoutEmptyState.setVisibility(View.GONE); // SỬ DỤNG layout_empty_state
        rvAllListings.setVisibility(View.VISIBLE);

        rvAllListings.setLayoutManager(new LinearLayoutManager(this));
        AllListingsAdapter adapter = new AllListingsAdapter(listings, listing -> {
            // Mở trang chỉnh sửa khi click vào tin đăng
            Intent intent = new Intent(this, EditTin.class);
            startActivity(intent);
        });
        rvAllListings.setAdapter(adapter);
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh dữ liệu khi quay lại từ EditTin
        setupListings();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }
}