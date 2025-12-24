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
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.LandlordBottomNavigationHelper;

public class AllListingsActivity extends AppCompatActivity {

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
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        tvListingCount = findViewById(R.id.tv_listing_count);
        btnBack = findViewById(R.id.btn_back);
        spinnerFilter = findViewById(R.id.spinner_filter);
        btnCreateFirstListing = findViewById(R.id.btn_create_first_listing);

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

    private void setupListings() {
        // Không làm gì cả, luôn hiển thị trạng thái trống
        updateListingCount(0);
        showEmptyState();
    }

    private void updateListingCount(int count) {
        String countText = count + " tin";
        tvListingCount.setText(countText);
    }

    private void showEmptyState() {
        if (layoutEmptyState != null) {
            layoutEmptyState.setVisibility(View.VISIBLE);
        }
        RecyclerView rvAllListings = findViewById(R.id.rv_all_listings);
        if (rvAllListings != null) {
            rvAllListings.setVisibility(View.GONE);
        }
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupListings();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }
}
