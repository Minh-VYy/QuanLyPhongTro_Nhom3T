package com.example.QuanLyPhongTro_App.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.response.ChatThreadDto;
import com.example.QuanLyPhongTro_App.utils.UserCache;

import java.util.List;

/**
 * Adapter for displaying chat threads from API
 */
public class ChatThreadListAdapter extends RecyclerView.Adapter<ChatThreadListAdapter.ThreadViewHolder> {

    private List<ChatThreadDto> threads;
    private OnThreadClickListener onThreadClickListener;
    private String currentUserId;  // ✅ Add to know who current user is

    public interface OnThreadClickListener {
        void onThreadClick(ChatThreadDto thread);
    }

    public ChatThreadListAdapter(List<ChatThreadDto> threads, OnThreadClickListener listener) {
        this(threads, listener, null);
    }

    // ✅ NEW: Constructor with currentUserId
    public ChatThreadListAdapter(List<ChatThreadDto> threads, OnThreadClickListener listener, String currentUserId) {
        this.threads = threads;
        this.onThreadClickListener = listener;
        this.currentUserId = currentUserId;
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
        holder.bind(thread, onThreadClickListener, currentUserId);
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

        public void bind(ChatThreadDto thread, OnThreadClickListener listener, String currentUserId) {
            // ✅ FIX: Determine who "other user" is based on current user
            String otherName = null;

            // If currentUserId is not set, use old logic (fallback)
            if (currentUserId == null || currentUserId.isEmpty()) {
                otherName = thread.getTenantName() != null ? thread.getTenantName() : thread.getLandlordName();
            } else {
                // Trim all IDs for comparison
                String currId = currentUserId.trim();
                String tenantId = thread.getTenantId() != null ? thread.getTenantId().trim() : "";
                String landlordId = thread.getLandlordId() != null ? thread.getLandlordId().trim() : "";

                // If current user is tenant, show landlord name
                if (currId.equals(tenantId)) {
                    otherName = thread.getLandlordName();
                }
                // If current user is landlord, show tenant name
                else if (currId.equals(landlordId)) {
                    otherName = thread.getTenantName();
                }
                // Fallback: use otherUserId to lookup in cache
                else if (thread.getOtherUserId() != null && !thread.getOtherUserId().isEmpty()) {
                    otherName = UserCache.getUserName(thread.getOtherUserId().trim());
                    if (otherName == null || otherName.isEmpty()) {
                        otherName = thread.getOtherUserId();
                    }
                }
                // Last resort
                if (otherName == null || otherName.isEmpty()) {
                    otherName = thread.getTenantName() != null ? thread.getTenantName() : thread.getLandlordName();
                }
            }

            tvChatName.setText(otherName != null ? otherName : "Unknown");

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

