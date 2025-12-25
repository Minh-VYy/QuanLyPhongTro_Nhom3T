package com.example.QuanLyPhongTro_App.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.response.ChatThreadDto;

import java.util.List;

/**
 * Adapter for displaying chat threads from API
 */
public class ChatThreadListAdapter extends RecyclerView.Adapter<ChatThreadListAdapter.ThreadViewHolder> {

    private List<ChatThreadDto> threads;
    private OnThreadClickListener onThreadClickListener;

    public interface OnThreadClickListener {
        void onThreadClick(ChatThreadDto thread);
    }

    public ChatThreadListAdapter(List<ChatThreadDto> threads, OnThreadClickListener listener) {
        this.threads = threads;
        this.onThreadClickListener = listener;
    }

    @NonNull
    @Override
    public ThreadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_list, parent, false);
        return new ThreadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ThreadViewHolder holder, int position) {
        ChatThreadDto thread = threads.get(position);
        holder.bind(thread, onThreadClickListener);
    }

    @Override
    public int getItemCount() {
        return threads.size();
    }

    public static class ThreadViewHolder extends RecyclerView.ViewHolder {
        private TextView tvChatName;
        private TextView tvLastMessage;
        private TextView tvLastTime;

        public ThreadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChatName = itemView.findViewById(R.id.tv_chat_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            tvLastTime = itemView.findViewById(R.id.tv_last_time);
        }

        public void bind(ChatThreadDto thread, OnThreadClickListener listener) {
            // Display tenant name for landlord view (or landlord name for tenant view)
            String otherName = thread.getTenantName() != null ? thread.getTenantName() : thread.getLandlordName();
            tvChatName.setText(otherName);

            // Last message
            if (thread.getLastMessage() != null) {
                tvLastMessage.setText(thread.getLastMessage());
            } else {
                tvLastMessage.setText("No messages yet");
            }

            // Last message time
            if (thread.getLastMessageTime() != null) {
                tvLastTime.setText(thread.getLastMessageTime());
            }


            // Click listener
            itemView.setOnClickListener(v -> listener.onThreadClick(thread));
        }
    }
}

