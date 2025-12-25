package com.example.QuanLyPhongTro_App.ui.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.QuanLyPhongTro_App.R;
import java.util.List;

/**
 * TỐI ƯU: Sử dụng DiffUtil để update hiệu quả
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<ChatMessage> messages;
    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_BOT = 2;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
        setHasStableIds(true);  // TỐI ƯU
    }
    
    @Override
    public long getItemId(int position) {
        return position;  // TỐI ƯU: Stable IDs
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isUser() ? VIEW_TYPE_USER : VIEW_TYPE_BOT;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = viewType == VIEW_TYPE_USER ? 
                R.layout.item_chat_user : R.layout.item_chat_bot;
        View view = LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }
    
    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            // TỐI ƯU: Partial update nếu cần
            holder.bind(messages.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    
    /**
     * TỐI ƯU: Update danh sách với DiffUtil
     */
    public void updateMessages(List<ChatMessage> newMessages) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return messages.size();
            }

            @Override
            public int getNewListSize() {
                return newMessages.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return messages.get(oldItemPosition).equals(newMessages.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                ChatMessage oldMsg = messages.get(oldItemPosition);
                ChatMessage newMsg = newMessages.get(newItemPosition);
                return oldMsg.getMessage().equals(newMsg.getMessage()) 
                    && oldMsg.isUser() == newMsg.isUser();
            }
        });
        
        messages = newMessages;
        diffResult.dispatchUpdatesTo(this);
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;

        ChatViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
        }

        void bind(ChatMessage message) {
            tvMessage.setText(message.getMessage());
        }
    }
}
