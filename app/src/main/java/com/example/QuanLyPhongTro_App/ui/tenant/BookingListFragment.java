package com.example.QuanLyPhongTro_App.ui.tenant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.ArrayList;
import java.util.List;

public class BookingListFragment extends Fragment {

    private static final String ARG_TYPE = "type";
    private String bookingType;
    private RecyclerView recyclerView;
    private BookingAdapter adapter;

    public static BookingListFragment newInstance(String type) {
        BookingListFragment fragment = new BookingListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookingType = getArguments().getString(ARG_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booking_list, container, false);

        recyclerView = view.findViewById(R.id.bookingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Sử dụng một danh sách trống vì MockData đã bị xóa
        adapter = new BookingAdapter(getContext(), getBookingList());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Booking> getBookingList() {
        // Trả về một danh sách trống vì không còn sử dụng MockData
        return new ArrayList<>();
    }
}
