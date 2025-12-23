package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Context;
import android.content.Intent;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tenant_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.notificationTitle.setText(notification.getTitle());
        holder.notificationMessage.setText(notification.getMessage());
        holder.notificationTime.setText(notification.getTime());

        int iconResId = R.drawable.ic_calendar;
        switch (notification.getIconType()) {
            case "calendar":
                iconResId = R.drawable.ic_calendar;
                break;
            case "home":
                iconResId = R.drawable.ic_home;
                break;
            case "message":
                iconResId = R.drawable.ic_message;
                break;
        }
        holder.notificationIcon.setImageResource(iconResId);

        holder.unreadDot.setVisibility(notification.isUnread() ? View.VISIBLE : View.GONE);

        if (notification.isUnread()) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.light_gray));
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(android.R.color.white));
        }

        holder.itemView.setOnClickListener(v -> {
            notification.setUnread(false);
            notifyItemChanged(position);

            // Kiểm tra loại thông báo
            if ("message".equals(notification.getIconType())) {
                Intent intent = new Intent(context, MessageDetailActivity.class);
                // Giả sử tiêu đề thông báo là tên người gửi
                intent.putExtra("USER_NAME", notification.getTitle());
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Đã đọc: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Xử lý điều hướng cho các loại thông báo khác
            }
        });
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView notificationIcon;
        TextView notificationTitle, notificationMessage, notificationTime;
        View unreadDot;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationIcon = itemView.findViewById(R.id.notificationIcon);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationTime = itemView.findViewById(R.id.notificationTime);
            unreadDot = itemView.findViewById(R.id.unreadDot);
        }
    }
}
