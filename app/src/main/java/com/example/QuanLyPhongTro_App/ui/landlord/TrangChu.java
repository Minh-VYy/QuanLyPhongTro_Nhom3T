package com.example.QuanLyPhongTro_App.ui.landlord; // đổi theo package của bạn

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TrangChu extends AppCompatActivity {

    private EditText edtTimKiem;
    private Button btnTabTinDang, btnTabDanhGia, btnTabGioiThieu;
    private ScrollView scrollTinDang, scrollDanhGia, scrollGioiThieu;
    private RecyclerView rvGridListings;
    private FloatingActionButton fabTaoTin;
    private LinearLayout navHome, navRequests, navStats, navProfile;
    private Button btnSuaTin;
    private ImageButton btnHelp, btnMessages, btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landlord_home);

        // Ánh xạ
        edtTimKiem = findViewById(R.id.edt_tim_kiem);
        btnTabTinDang = findViewById(R.id.btn_tab_tindang);
        btnTabDanhGia = findViewById(R.id.btn_tab_danhgia);
        btnTabGioiThieu = findViewById(R.id.btn_tab_gioithieu);

        scrollTinDang = findViewById(R.id.scroll_tin_dang);
        scrollDanhGia = findViewById(R.id.scroll_danh_gia);
        scrollGioiThieu = findViewById(R.id.scroll_gioi_thieu);

        rvGridListings = findViewById(R.id.rv_grid_listings);
        fabTaoTin = findViewById(R.id.fab_tao_tin);

        navHome = findViewById(R.id.nav_home);
        navRequests = findViewById(R.id.nav_requests);
        navStats = findViewById(R.id.nav_stats);
        navProfile = findViewById(R.id.nav_profile);

        btnHelp = findViewById(R.id.btn_help);
        btnMessages = findViewById(R.id.btn_messages);
        btnBack = findViewById(R.id.btn_back);
        btnSuaTin = findViewById(R.id.btn_sua_tin);

        // Tab
        btnTabTinDang.setOnClickListener(v -> hienThiTab("tindang"));
        btnTabDanhGia.setOnClickListener(v -> hienThiTab("danhgia"));
        btnTabGioiThieu.setOnClickListener(v -> hienThiTab("gioithieu"));
        hienThiTab("tindang");

        // RecyclerView grid 2 columns
        rvGridListings.setLayoutManager(new GridLayoutManager(this, 2));
        List<Tin> data = new ArrayList<>();
        data.add(new Tin("Phòng trọ gần ĐH Bách Khoa", "2.500.000 đ", "Còn trống", true));
        data.add(new Tin("Chung cư mini full nội thất", "3.200.000 đ", "Đã thuê", true));
        data.add(new Tin("Phòng sinh viên giá rẻ", "1.800.000 đ", "Chờ xử lý", false));
        ListingsAdapter adapter = new ListingsAdapter(data);
        rvGridListings.setAdapter(adapter);

        // FAB -> mở EditTin
        fabTaoTin.setOnClickListener(v -> {
            Intent it = new Intent(TrangChu.this, EditTin.class);
            startActivity(it);
        });

        // header actions
        btnHelp.setOnClickListener(v -> {
            Intent it = new Intent(TrangChu.this, TroGiup.class);
            startActivity(it);
        });

        btnMessages.setOnClickListener(v -> {
            Intent it = new Intent(TrangChu.this, YeuCau.class);
            startActivity(it);
        });

        btnBack.setOnClickListener(v -> finish());

        // bottom nav
        navRequests.setOnClickListener(v -> {
            Intent it = new Intent(TrangChu.this, YeuCau.class);
            startActivity(it);
        });

        navHome.setOnClickListener(v -> hienThiTab("tindang"));

        // Sửa tin (nút ở featured)
        btnSuaTin.setOnClickListener(v -> {
            Intent it = new Intent(TrangChu.this, EditTin.class);
            startActivity(it);
        });
    }

    private void hienThiTab(String tab) {
        int mauChinh = ContextCompat.getColor(this, R.color.primary);
        int mauKhong = Color.parseColor("#6B7280");

        btnTabTinDang.setTextColor(mauKhong);
        btnTabDanhGia.setTextColor(mauKhong);
        btnTabGioiThieu.setTextColor(mauKhong);

        scrollTinDang.setVisibility(View.GONE);
        scrollDanhGia.setVisibility(View.GONE);
        scrollGioiThieu.setVisibility(View.GONE);

        switch (tab) {
            case "tindang":
                scrollTinDang.setVisibility(View.VISIBLE);
                btnTabTinDang.setTextColor(mauChinh);
                break;
            case "danhgia":
                scrollDanhGia.setVisibility(View.VISIBLE);
                btnTabDanhGia.setTextColor(mauChinh);
                break;
            case "gioithieu":
                scrollGioiThieu.setVisibility(View.VISIBLE);
                btnTabGioiThieu.setTextColor(mauChinh);
                break;
        }
    }

    // DATA model
    static class Tin {
        String tieuDe, gia, trangThai;
        boolean isActive;
        Tin(String t, String g, String tt, boolean a) { tieuDe = t; gia = g; trangThai = tt; isActive = a; }
    }

    // ADAPTER
    static class ListingsAdapter extends RecyclerView.Adapter<ListingsAdapter.VH> {
        private final List<Tin> items;
        ListingsAdapter(List<Tin> list) { items = list; }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvTitle, tvPrice, tvStatus;
            Switch swActive;
            VH(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tv_title_item);
                tvPrice = v.findViewById(R.id.tv_price_item);
                tvStatus = v.findViewById(R.id.tv_status_item);
                swActive = v.findViewById(R.id.switch_active);
            }
        }

        @Override
        public VH onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(R.layout.item_landlord_listing, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(VH holder, int position) {
            Tin t = items.get(position);
            holder.tvTitle.setText(t.tieuDe);
            holder.tvPrice.setText(t.gia);
            holder.tvStatus.setText(t.trangThai);
            holder.swActive.setChecked(t.isActive);

            holder.swActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                t.isActive = isChecked;
                holder.tvStatus.setText(isChecked ? "Còn trống" : "Không hoạt động");
            });

            holder.itemView.setOnClickListener(v -> {
                // Ví dụ: mở EditTin để chỉnh
                android.content.Context ctx = v.getContext();
                Intent it = new Intent(ctx, EditTin.class);
                ctx.startActivity(it);
            });
        }

        @Override
        public int getItemCount() { return items.size(); }
    }
}
