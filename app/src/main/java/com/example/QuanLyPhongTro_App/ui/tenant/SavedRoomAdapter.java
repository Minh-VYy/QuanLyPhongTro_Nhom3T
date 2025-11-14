package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;

import java.util.List;

public class SavedRoomAdapter extends RecyclerView.Adapter<SavedRoomAdapter.SavedRoomViewHolder> {

    private Context context;
    private List<Room> roomList;

    public SavedRoomAdapter(Context context, List<Room> roomList) {
        this.context = context;
        this.roomList = roomList;
    }

    @NonNull
    @Override
    public SavedRoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tenant_saved_room, parent, false);
        return new SavedRoomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRoomViewHolder holder, int position) {
        Room room = roomList.get(position);

        holder.roomTitle.setText(room.getTitle());
        holder.roomPrice.setText(room.getPrice());
        holder.roomAddress.setText(room.getAddress());
        holder.roomRating.setText(room.getRating());

        // Heart icon - already saved
        holder.iconHeart.setOnClickListener(v -> {
            // Remove from saved
            roomList.remove(position);
            notifyItemRemoved(position);
            Toast.makeText(context, "Đã bỏ lưu", Toast.LENGTH_SHORT).show();
        });

        // Navigate to detail
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(context, "Xem chi tiết: " + room.getTitle(), Toast.LENGTH_SHORT).show();
            // TODO: Navigate to RoomDetailActivity
        });
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    static class SavedRoomViewHolder extends RecyclerView.ViewHolder {
        ImageView roomImage, iconHeart, iconArrow;
        TextView roomTitle, roomPrice, roomAddress, roomRating;

        public SavedRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            roomPrice = itemView.findViewById(R.id.roomPrice);
            roomAddress = itemView.findViewById(R.id.roomAddress);
            roomRating = itemView.findViewById(R.id.roomRating);
            iconHeart = itemView.findViewById(R.id.iconHeart);
            iconArrow = itemView.findViewById(R.id.iconArrow);
        }
    }
}

