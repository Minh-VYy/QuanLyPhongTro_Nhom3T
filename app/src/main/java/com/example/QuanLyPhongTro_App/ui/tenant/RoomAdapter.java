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

/**
 * Adapter cho danh sách phòng trọ
 * ĐÃ CẬP NHẬT: Hiển thị giá từ database một cách đơn giản
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
        private TextView ratingBadge;  // Thêm badge đánh giá
        private Button detailButton;

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
            // ========== HIỂN THỊ HÌNH ẢNH ==========
            // Ưu tiên URL từ server, nếu không có thì dùng resource local
            if (room.getImageUrl() != null && !room.getImageUrl().isEmpty()) {
                // TODO: Dùng Glide hoặc Picasso để load ảnh từ URL
                // Glide.with(itemView.getContext()).load(room.getImageUrl()).into(roomImage);

                // Tạm thời dùng ảnh mặc định
                roomImage.setImageResource(R.drawable.tro);
            } else if (room.getImageResId() != 0) {
                roomImage.setImageResource(room.getImageResId());
            } else {
                roomImage.setImageResource(R.drawable.tro);
            }

            // ========== HIỂN THỊ TIÊU ĐỀ ==========
            roomTitle.setText(room.getTitle());

            // ========== HIỂN THỊ GIÁ - ĐƠN GIẢN HÓA ==========
            // Chỉ cần gọi method getFormattedPrice() - đã format sẵn từ model
            // Không cần xử lý gì thêm, database trả về giá là số, method này tự động format
            priceText.setText(room.getFormattedPrice());

            // ========== HIỂN THỊ VỊ TRÍ ==========
            locationText.setText(room.getLocation());

            // ========== HIỂN THỊ ĐÁNH GIÁ (NẾU CÓ) ==========
            if (room.hasRoomRating()) {
                // Phòng có đánh giá -> hiển thị badge
                ratingBadge.setVisibility(View.VISIBLE);
                ratingBadge.setText("⭐ " + String.format("%.1f", room.getRoomRating()));
            } else {
                // Chưa có đánh giá -> ẩn badge
                ratingBadge.setVisibility(View.GONE);
            }

            // ========== XỬ LÝ SỰ KIỆN CLICK ==========
            detailButton.setOnClickListener(v -> listener.onRoomClick(room));
            itemView.setOnClickListener(v -> listener.onRoomClick(room));
        }
    }

    /**
     * Cập nhật danh sách phòng mới (VD: sau khi lấy từ database)
     */
    public void updateRoomList(ArrayList<Room> newRoomList) {
        this.roomList = newRoomList;
        notifyDataSetChanged();
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
