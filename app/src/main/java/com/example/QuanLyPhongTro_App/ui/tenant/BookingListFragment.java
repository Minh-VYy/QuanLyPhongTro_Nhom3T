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

        adapter = new BookingAdapter(getContext(), getBookingList());
        recyclerView.setAdapter(adapter);

        return view;
    }

    private List<Booking> getBookingList() {
        // TODO: Load from database based on bookingType
        List<Booking> bookings = new ArrayList<>();

        if ("upcoming".equals(bookingType)) {
            // Add sample upcoming bookings
            bookings.add(new Booking("Phòng trọ cao cấp", "2.5 triệu", "21/08/2024 - Sáng", "Quận 1, TP.HCM", "pending"));
            bookings.add(new Booking("Phòng trọ giá rẻ", "1.8 triệu", "22/08/2024 - Chiều", "Quận 3, TP.HCM", "confirmed"));
        } else {
            // Add sample past bookings
            bookings.add(new Booking("Phòng gần trường", "2.0 triệu", "15/08/2024 - Sáng", "Quận 5, TP.HCM", "completed"));
            bookings.add(new Booking("Phòng VIP", "3.5 triệu", "10/08/2024 - Tối", "Quận 7, TP.HCM", "cancelled"));
        }

        return bookings;
    }
}

