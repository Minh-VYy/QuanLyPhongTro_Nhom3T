package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.QuanLyPhongTro_App.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;
import java.util.List;

public class AdvancedFilterBottomSheet extends BottomSheetDialogFragment {

    private RangeSlider priceRangeSlider;
    private TextView priceRangeText, btnClearFilter;
    private Spinner areaSpinner;
    private RadioGroup distanceRadioGroup;
    private ChipGroup roomTypeChipGroup, amenitiesChipGroup;
    private Button btnCancel, btnApply;

    private FilterListener filterListener;

    // Interface to send data back to the calling activity/fragment
    public interface FilterListener {
        void onFilterApplied(Bundle filters);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Ensure that the host activity implements the listener
        try {
            filterListener = (FilterListener) getParentFragment(); // Or `(FilterListener) getActivity()`
        } catch (ClassCastException e) {
            throw new ClassCastException("Calling fragment must implement FilterListener");
        }
    }

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
            Bundle filters = new Bundle();

            // Price
            List<Float> priceValues = priceRangeSlider.getValues();
            filters.putFloat("minPrice", priceValues.get(0));
            filters.putFloat("maxPrice", priceValues.get(1));

            // Area
            if (areaSpinner.getSelectedItemPosition() > 0) {
                filters.putString("area", areaSpinner.getSelectedItem().toString());
            }

            // Distance
            int selectedDistanceId = distanceRadioGroup.getCheckedRadioButtonId();
            RadioButton selectedDistance = getView().findViewById(selectedDistanceId);
            filters.putString("distance", selectedDistance.getText().toString());

            // Room types
            ArrayList<String> roomTypes = new ArrayList<>();
            for (int id : roomTypeChipGroup.getCheckedChipIds()) {
                Chip chip = roomTypeChipGroup.findViewById(id);
                roomTypes.add(chip.getText().toString());
            }
            filters.putStringArrayList("roomTypes", roomTypes);

            // Amenities
            ArrayList<String> amenities = new ArrayList<>();
            for (int id : amenitiesChipGroup.getCheckedChipIds()) {
                Chip chip = amenitiesChipGroup.findViewById(id);
                amenities.add(chip.getText().toString());
            }
            filters.putStringArrayList("amenities", amenities);

            if (filterListener != null) {
                filterListener.onFilterApplied(filters);
            }
            Toast.makeText(requireContext(), "Đã áp dụng bộ lọc", Toast.LENGTH_SHORT).show();
            dismiss();
        });
    }

    public static AdvancedFilterBottomSheet newInstance() {
        return new AdvancedFilterBottomSheet();
    }
}