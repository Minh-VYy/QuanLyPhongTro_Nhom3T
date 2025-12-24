package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.ArrayList;
import java.util.List;

public class UtilityDialog extends Dialog {
    private Context context;

    public UtilityDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_landlord_utility);

        setDialogPosition();
        setupCloseButton();
        setupRecyclerView();
    }

    private void setDialogPosition() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.END;
            window.setBackgroundDrawableResource(android.R.color.transparent);
            window.setAttributes(layoutParams);
        }
    }

    private void setupCloseButton() {
        ImageView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> dismiss());
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.utilityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Tạo lại danh sách tiện ích ngay tại đây
        List<UtilityAdapter.UtilityItem> utilityItems = new ArrayList<>();
        utilityItems.add(new UtilityAdapter.UtilityItem(R.drawable.ic_add, "Thêm tin trọ", "Đăng tin cho thuê phòng trọ mới", EditTin.class));
        utilityItems.add(new UtilityAdapter.UtilityItem(R.drawable.ic_list, "Danh sách tin đăng", "Xem và quản lý tin đăng", AllListingsActivity.class));

        UtilityAdapter adapter = new UtilityAdapter(context, utilityItems, this);
        recyclerView.setAdapter(adapter);
    }
}
