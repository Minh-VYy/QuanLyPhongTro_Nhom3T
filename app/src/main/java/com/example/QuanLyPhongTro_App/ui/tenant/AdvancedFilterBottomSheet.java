package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.QuanLyPhongTro_App.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.List;

public class AdvancedFilterBottomSheet extends BottomSheetDialogFragment {

    private RangeSlider priceRangeSlider;
    private TextView priceRangeText, btnClearFilter;
    private Spinner areaSpinner;
    private RadioGroup distanceRadioGroup;
    private ChipGroup roomTypeChipGroup, amenitiesChipGroup;
    private Button btnCancel, btnApply;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_advanced_filter, container, false);

        initViews(view);
        setupSpinner();
        setupListeners();

        return view;
    }

    private void initViews(View view) {
        priceRangeSlider = view.findViewById(R.id.priceRangeSlider);
        priceRangeText = view.findViewById(R.id.priceRangeText);
        btnClearFilter = view.findViewById(R.id.btnClearFilter);
        areaSpinner = view.findViewById(R.id.areaSpinner);
        distanceRadioGroup = view.findViewById(R.id.distanceRadioGroup);
        roomTypeChipGroup = view.findViewById(R.id.roomTypeChipGroup);
        amenitiesChipGroup = view.findViewById(R.id.amenitiesChipGroup);
        btnCancel = view.findViewById(R.id.btnCancel);
        btnApply = view.findViewById(R.id.btnApply);
    }

    private void setupSpinner() {
        String[] areas = {"Chọn quận/khu vực", "Quận 1", "Quận 2", "Quận 3", "Quận 5", "Quận 7", "Quận 10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
            android.R.layout.simple_spinner_item, areas);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(adapter);
    }

    private void setupListeners() {
        // Price range slider
        priceRangeSlider.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            priceRangeText.setText(String.format("Từ %.1f - %.1f triệu", values.get(0), values.get(1)));
        });

        // Clear filter
        btnClearFilter.setOnClickListener(v -> {
            priceRangeSlider.setValues(0.5f, 10.0f);
            areaSpinner.setSelection(0);
            distanceRadioGroup.check(R.id.distanceAny);
            roomTypeChipGroup.clearCheck();
            amenitiesChipGroup.clearCheck();
            Toast.makeText(requireContext(), "Đã xoá bộ lọc", Toast.LENGTH_SHORT).show();
        });

        // Cancel
        btnCancel.setOnClickListener(v -> dismiss());

        // Apply
        btnApply.setOnClickListener(v -> {
            // TODO: Apply filters and return to MainActivity
            Toast.makeText(requireContext(), "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    public static AdvancedFilterBottomSheet newInstance() {
        return new AdvancedFilterBottomSheet();
    }
}

