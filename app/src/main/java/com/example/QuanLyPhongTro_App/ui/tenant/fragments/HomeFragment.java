package com.example.QuanLyPhongTro_App.ui.tenant.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.ui.tenant.AdvancedFilterBottomSheet;
import com.example.QuanLyPhongTro_App.ui.tenant.Room;
import com.example.QuanLyPhongTro_App.ui.tenant.RoomAdapter;
import com.example.QuanLyPhongTro_App.ui.tenant.RoomDetailActivity;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView roomRecyclerView;
    private RoomAdapter roomAdapter;
    private ArrayList<Room> roomList;
    private Button btnFilter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initViews(view);
        initRoomList();
        setupRoomRecyclerView();
        setupFilterButton();

        return view;
    }

    private void initViews(View view) {
        roomRecyclerView = view.findViewById(R.id.roomRecyclerView);
        btnFilter = view.findViewById(R.id.btnFilter);
    }

    private void initRoomList() {
        roomList = new ArrayList<>();
        roomList.add(new Room("Phòng trọ đẹp, gần ĐH Bách Khoa", "2.5 triệu/tháng", "Quận 10, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Chung cư mini full nội thất", "3.2 triệu/tháng", "Quận 1, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Phòng trọ mới xây", "1.8 triệu/tháng", "Quận Tân Bình, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Studio cao cấp", "4.5 triệu/tháng", "Quận 3, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Phòng trọ giá rẻ", "1.5 triệu/tháng", "Quận Bình Thạnh, TP.HCM", R.drawable.tro));
        roomList.add(new Room("Nhà nguyên căn", "5.0 triệu/tháng", "Quận 7, TP.HCM", R.drawable.tro));
    }

    private void setupRoomRecyclerView() {
        roomAdapter = new RoomAdapter(roomList, room -> {
            Intent intent = new Intent(getActivity(), RoomDetailActivity.class);
            intent.putExtra("room", room);
            startActivity(intent);
        });
        roomRecyclerView.setAdapter(roomAdapter);
    }

    private void setupFilterButton() {
        btnFilter.setOnClickListener(v -> {
            AdvancedFilterBottomSheet filterSheet = AdvancedFilterBottomSheet.newInstance();
            filterSheet.show(getParentFragmentManager(), "AdvancedFilter");
        });
    }
}

