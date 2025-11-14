package com.example.QuanLyPhongTro_App.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.ArrayList;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private ArrayList<Room> roomList;
    private OnRoomClickListener onRoomClickListener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(ArrayList<Room> roomList, OnRoomClickListener onRoomClickListener) {
        this.roomList = roomList;
        this.onRoomClickListener = onRoomClickListener;
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tenant_room_grid, parent, false);
        return new RoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        Room room = roomList.get(position);
        holder.bind(room, onRoomClickListener);
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private ImageView roomImage;
        private TextView roomTitle;
        private TextView priceText;
        private TextView locationText;
        private Button detailButton;

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            priceText = itemView.findViewById(R.id.priceText);
            locationText = itemView.findViewById(R.id.locationText);
            detailButton = itemView.findViewById(R.id.detailButton);
        }

        public void bind(Room room, OnRoomClickListener listener) {
            roomImage.setImageResource(room.getImageResId());
            roomTitle.setText(room.getTitle());
            priceText.setText(room.getPrice());
            locationText.setText(room.getLocation());

            detailButton.setOnClickListener(v -> listener.onRoomClick(room));
            itemView.setOnClickListener(v -> listener.onRoomClick(room));
        }
    }
}
