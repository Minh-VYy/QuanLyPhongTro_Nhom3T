package com.example.QuanLyPhongTro_App.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.QuanLyPhongTro_App.R;

import java.util.List;

public class SuggestedRoomsAdapter extends RecyclerView.Adapter<SuggestedRoomsAdapter.ViewHolder> {

    private final List<Room> rooms;
    private final OnItemClickListener onItemClick;

    public interface OnItemClickListener {
        void onItemClick(Room room);
    }

    public SuggestedRoomsAdapter(List<Room> rooms, OnItemClickListener onItemClick) {
        this.rooms = rooms;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tenant_room_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Room room = rooms.get(position);
        holder.bind(room);
    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView roomImage;
        private final TextView roomTitle, locationText, priceText;
        private final Button detailButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            locationText = itemView.findViewById(R.id.locationText);
            priceText = itemView.findViewById(R.id.priceText);
            detailButton = itemView.findViewById(R.id.detailButton);
        }

        public void bind(Room room) {
            roomTitle.setText(room.getTitle());
            locationText.setText(room.getLocation());
            priceText.setText(room.getPrice());

            Glide.with(itemView.getContext())
                    .load(room.getImageResId())
                    .centerCrop()
                    .placeholder(R.drawable.tro)
                    .into(roomImage);

            detailButton.setOnClickListener(v -> onItemClick.onItemClick(room));
            itemView.setOnClickListener(v -> onItemClick.onItemClick(room));
        }
    }
}
