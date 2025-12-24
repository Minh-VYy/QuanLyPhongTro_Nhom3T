package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.MockData;

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

        // Set vị trí và kích thước cho dialog
        setDialogPosition();

        // Xử lý nút đóng
        setupCloseButton();

        setupRecyclerView();
    }

    private void setDialogPosition() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());

            // Set chiều rộng cố định, chiều cao full màn hình
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

            // Hiển thị từ bên phải
            layoutParams.gravity = Gravity.END;

            // Background trong suốt
            window.setBackgroundDrawableResource(android.R.color.transparent);

            // KHÔNG CẦN ANIMATION
            // window.setWindowAnimations(R.style.DialogAnimation);

            window.setAttributes(layoutParams);
        }
    }

    private void setupCloseButton() {
        ImageView btnClose = findViewById(R.id.btn_close);
        btnClose.setOnClickListener(v -> {
            dismiss(); // Đóng dialog khi click nút X
        });
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.utilityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Sử dụng MockData để lấy danh sách tiện ích (chỉ 4 mục)
        List<MockData.UtilityItem> utilityItems = MockData.getLandlordUtilities();

        UtilityAdapter adapter = new UtilityAdapter(context, utilityItems, this);
        recyclerView.setAdapter(adapter);
    }
}
