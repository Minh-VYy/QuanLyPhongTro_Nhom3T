package com.example.QuanLyPhongTro_App.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.utils.FileUrlResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter cho danh sách phòng trọ
 * TỐI ƯU: Sử dụng Glide để load ảnh, DiffUtil để update hiệu quả
 */
public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> {

    private ArrayList<Room> roomList;
    private OnRoomClickListener onRoomClickListener;

    public interface OnRoomClickListener {
        void onRoomClick(Room room);
    }

    public RoomAdapter(ArrayList<Room> roomList, OnRoomClickListener onRoomClickListener) {
        this.roomList = roomList;
        this.onRoomClickListener = onRoomClickListener;
        setHasStableIds(true);  // TỐI ƯU
    }
    
    @Override
    public long getItemId(int position) {
        return roomList.get(position).getId();  // TỐI ƯU: Stable IDs
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
    
    /**
     * TỐI ƯU: Update danh sách với DiffUtil
     */
    public void updateRoomList(ArrayList<Room> newRoomList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return roomList.size();
            }

            @Override
            public int getNewListSize() {
                return newRoomList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return roomList.get(oldItemPosition).getId() == newRoomList.get(newItemPosition).getId();
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                Room oldRoom = roomList.get(oldItemPosition);
                Room newRoom = newRoomList.get(newItemPosition);
                return oldRoom.getTitle().equals(newRoom.getTitle()) 
                    && oldRoom.getPriceValue() == newRoom.getPriceValue();
            }
        });
        
        this.roomList = newRoomList;
        diffResult.dispatchUpdatesTo(this);
    }

    public static class RoomViewHolder extends RecyclerView.ViewHolder {
        private ImageView roomImage;
        private TextView roomTitle;
        private TextView priceText;
        private TextView locationText;
        private TextView ratingBadge;
        private Button detailButton;
        
        // TỐI ƯU: Glide RequestOptions
        private static final RequestOptions glideOptions = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache cả original và resized
                .placeholder(R.drawable.tro)  // Ảnh placeholder
                .error(R.drawable.tro)  // Ảnh khi lỗi
                .centerCrop();

        public RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            roomImage = itemView.findViewById(R.id.roomImage);
            roomTitle = itemView.findViewById(R.id.roomTitle);
            priceText = itemView.findViewById(R.id.priceText);
            locationText = itemView.findViewById(R.id.locationText);
            ratingBadge = itemView.findViewById(R.id.ratingBadge);
            detailButton = itemView.findViewById(R.id.detailButton);
        }

        public void bind(Room room, OnRoomClickListener listener) {
            // ========== HIỂN THỊ HÌNH ẢNH - TỐI ƯU VỚI GLIDE ==========
            String raw = room.getImageUrl();
            String resolvedUrl = FileUrlResolver.resolve(raw);

            if (resolvedUrl != null && !resolvedUrl.isEmpty()) {
                Glide.with(itemView.getContext())
                    .load(resolvedUrl)
                    .apply(glideOptions)
                    .into(roomImage);
            } else if (room.getImageResId() != 0) {
                Glide.with(itemView.getContext())
                    .load(room.getImageResId())
                    .apply(glideOptions)
                    .into(roomImage);
            } else {
                roomImage.setImageResource(R.drawable.tro);
            }

            // ========== HIỂN THỊ TIÊU ĐỀ ==========
            roomTitle.setText(room.getTitle());

            // ========== HIỂN THỊ GIÁ ==========
            priceText.setText(room.getFormattedPrice());

            // ========== HIỂN THỊ VỊ TRÍ ==========
            locationText.setText(room.getLocation());

            // ========== HIỂN THỊ ĐÁNH GIÁ ==========
            if (room.hasRoomRating()) {
                ratingBadge.setVisibility(View.VISIBLE);
                ratingBadge.setText("⭐ " + String.format("%.1f", room.getRoomRating()));
            } else {
                ratingBadge.setVisibility(View.GONE);
            }

            // ========== XỬ LÝ SỰ KIỆN CLICK ==========
            detailButton.setOnClickListener(v -> listener.onRoomClick(room));
            itemView.setOnClickListener(v -> listener.onRoomClick(room));
        }
    }

    /**
     * Thêm phòng mới vào danh sách
     */
    public void addRoom(Room room) {
        this.roomList.add(room);
        notifyItemInserted(roomList.size() - 1);
    }

    /**
     * Xóa phòng khỏi danh sách
     */
    public void removeRoom(int position) {
        if (position >= 0 && position < roomList.size()) {
            this.roomList.remove(position);
            notifyItemRemoved(position);
        }
    }
}
