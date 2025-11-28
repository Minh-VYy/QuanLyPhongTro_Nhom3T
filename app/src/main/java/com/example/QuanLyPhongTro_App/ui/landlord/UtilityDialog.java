package com.example.QuanLyPhongTro_App.ui.landlord;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
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

        setupRecyclerView();
    }

    private void setupRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.utilityRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Sử dụng MockData để lấy danh sách tiện ích
        List<MockData.UtilityItem> utilityItems = MockData.getLandlordUtilities();

        UtilityAdapter adapter = new UtilityAdapter(context, utilityItems);
        recyclerView.setAdapter(adapter);
    }
}
