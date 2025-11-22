package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.BottomNavigationHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BookingListActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private BookingPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_booking_list);

        initViews();
        setupToolbar();
        setupViewPager();
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void setupToolbar() {
        LinearLayout toolbarLayout = findViewById(R.id.toolbar);
        ImageView btnBack = toolbarLayout.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationHelper.setupBottomNavigation(this, "booking");
    }

    private void setupViewPager() {
        pagerAdapter = new BookingPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Sắp tới");
                            break;
                        case 1:
                            tab.setText("Đã xem / Đã huỷ");
                            break;
                    }
                }
        ).attach();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupBottomNavigation();
    }
}
