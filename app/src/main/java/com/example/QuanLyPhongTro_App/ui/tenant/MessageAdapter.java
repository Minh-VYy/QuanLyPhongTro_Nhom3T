package com.example.QuanLyPhongTro_App.ui.tenant;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
// Import lớp Message model của bạn
// import com.example.QuanLyPhongTro_App.model.Message;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final Context context;
    private final List<Message> messageList; // Thay Message bằng tên model của bạn

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ánh xạ layout item_landlord_message.xml
        View view = LayoutInflater.from(context).inflate(R.layout.item_landlord_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lệnh Intent để chuyển Activity
                Intent intent = new Intent(context, MessageDetailActivity.class);

                // Gửi ID hoặc đối tượng Message model qua Intent
                intent.putExtra("message_id", message.getId());

                // Bắt đầu Activity mới
                context.startActivity(intent);
            }
        });
        // Gán dữ liệu (Sử dụng ID từ item_landlord_message.xml)
        holder.tvAvatarInitial.setText(message.getRequesterName().substring(0, 1));
        holder.tvMsgName.setText(message.getRequesterName());
        holder.tvMsgTime.setText(message.getSentTime());
        holder.tvMsgPreview.setText(message.getPreview());

        // 1. CHỨC NĂNG XEM CHI TIẾT (CLICK)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, MessageDetailActivity.class);
            // Giả sử Message model có phương thức getId()
            intent.putExtra("message_id", message.getId());
            context.startActivity(intent);
        });

        // 2. CHỨC NĂNG XÓA (LONG CLICK) - Nếu không dùng Swipe-to-Dismiss
        holder.itemView.setOnLongClickListener(v -> {
            // Triển khai dialog xác nhận xóa tại đây
            // hoặc gọi một interface để Activity/Fragment xử lý
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Lớp ViewHolder
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarInitial, tvMsgName, tvMsgTime, tvMsgPreview;
        RelativeLayout itemContainer; // ID message_item_container từ XML

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarInitial = itemView.findViewById(R.id.tv_avatar_initial);
            tvMsgName = itemView.findViewById(R.id.tv_msg_name);
            tvMsgTime = itemView.findViewById(R.id.tv_msg_time);
            tvMsgPreview = itemView.findViewById(R.id.tv_msg_preview);
            itemContainer = itemView.findViewById(R.id.message_item_container);
        }
    }

    // Hàm hỗ trợ xóa tin nhắn (nếu dùng Swipe-to-Dismiss)
    public void deleteMessage(int position) {
        // messageList.remove(position);
        // notifyItemRemoved(position);
        // *** LOGIC XÓA API/DB NÊN ĐƯỢC GỌI Ở ĐÂY HOẶC TRONG ACTIVITY ***
    }
}