package com.example.QuanLyPhongTro_App.ui.landlord;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
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
    private TextView tvEmptyState;
    private ImageView btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_listings);

        initViews();
        setupListings();
        setupBottomNavigation();
    }

    private void initViews() {
        rvAllListings = findViewById(R.id.rv_all_listings);
        tvEmptyState = findViewById(R.id.tv_empty_state);
        btnBack = findViewById(R.id.btn_back);

        btnBack.setOnClickListener(v -> finish());
    }

    private void setupListings() {
        // Lấy danh sách tin đăng từ MockData
        List<MockData.LandlordData.ListingItem> listings = MockData.LandlordData.getListings();

        if (listings.isEmpty()) {
            tvEmptyState.setVisibility(View.VISIBLE);
            rvAllListings.setVisibility(View.GONE);
        } else {
            tvEmptyState.setVisibility(View.GONE);
            rvAllListings.setVisibility(View.VISIBLE);

            rvAllListings.setLayoutManager(new LinearLayoutManager(this));
            AllListingsAdapter adapter = new AllListingsAdapter(listings, listing -> {
                // Mở trang chỉnh sửa khi click vào tin đăng
                Intent intent = new Intent(this, EditTin.class);
                startActivity(intent);
            });
            rvAllListings.setAdapter(adapter);
        }
    }

    private void setupBottomNavigation() {
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LandlordBottomNavigationHelper.setupBottomNavigation(this, "listings");
    }
}
