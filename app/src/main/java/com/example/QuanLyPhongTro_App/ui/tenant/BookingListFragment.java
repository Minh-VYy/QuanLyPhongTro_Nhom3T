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
        List<Booking> bookings = new ArrayList<>();

        if ("pending".equals(bookingType)) {
            bookings.addAll(com.example.QuanLyPhongTro_App.data.MockData.getPendingBookings());
        } else if ("confirmed".equals(bookingType)) {
            bookings.addAll(com.example.QuanLyPhongTro_App.data.MockData.getConfirmedBookings());
        } else if ("completed".equals(bookingType)) {
            bookings.addAll(com.example.QuanLyPhongTro_App.data.MockData.getCompletedBookings());
        } else if ("cancelled".equals(bookingType)) {
            bookings.addAll(com.example.QuanLyPhongTro_App.data.MockData.getCancelledBookings());
        } else {
            // "all" - load all bookings
            bookings.addAll(com.example.QuanLyPhongTro_App.data.MockData.getPendingBookings());
            bookings.addAll(com.example.QuanLyPhongTro_App.data.MockData.getConfirmedBookings());
        }


        return bookings;
    }
}

