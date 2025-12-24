package com.example.QuanLyPhongTro_App.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.ChatMessage;
import com.example.QuanLyPhongTro_App.data.ChatThread;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    private List<ChatThread> chatThreads;
    private String currentUserId;
    private OnChatClickListener onChatClickListener;

    public interface OnChatClickListener {
        void onChatClick(ChatThread thread);
    }

    public ChatListAdapter(List<ChatThread> chatThreads, String currentUserId, OnChatClickListener listener) {
        this.chatThreads = chatThreads;
        this.currentUserId = currentUserId;
        this.onChatClickListener = listener;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListViewHolder holder, int position) {
        ChatThread thread = chatThreads.get(position);
        holder.bind(thread, currentUserId, onChatClickListener);
    }

    @Override
    public int getItemCount() {
        return chatThreads.size();
    }

    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChatName;
        private TextView tvLastMessage;
        private TextView tvLastTime;

        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChatName = itemView.findViewById(R.id.tv_chat_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastTime = itemView.findViewById(R.id.tv_last_time);
        }

        public void bind(ChatThread thread, String currentUserId, OnChatClickListener listener) {
            // Hiển thị tên người kia
            String otherName;
            if (currentUserId.equals(thread.getTenantId())) {
                otherName = thread.getLandlordName();
            } else {
                otherName = thread.getTenantName();
            }
            tvChatName.setText(otherName);

            // Lấy tin nhắn cuối cùng
            List<ChatMessage> messages = thread.getMessages();
            if (!messages.isEmpty()) {
                ChatMessage lastMessage = messages.get(messages.size() - 1);
                tvLastMessage.setText(lastMessage.getContent());

                // Định dạng thời gian
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                String timeString = sdf.format(new Date(lastMessage.getTimestamp()));
                tvLastTime.setText(timeString);
            }

            // Click listener
            itemView.setOnClickListener(v -> listener.onChatClick(thread));
        }
    }
}
