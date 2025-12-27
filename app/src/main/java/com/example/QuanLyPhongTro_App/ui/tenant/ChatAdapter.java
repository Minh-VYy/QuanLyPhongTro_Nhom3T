package com.example.QuanLyPhongTro_App.ui.tenant;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.QuanLyPhongTro_App.R;
import com.example.QuanLyPhongTro_App.data.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessage> messages;
    private String currentUserId;

    public ChatAdapter(List<ChatMessage> messages, String currentUserId) {
        this.messages = messages;
        // ✅ CRITICAL: Trim currentUserId to avoid whitespace comparison issues
        this.currentUserId = currentUserId != null ? currentUserId.trim() : "";
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messages.get(position);

        String msgSenderId = message.getSenderId() != null ? message.getSenderId().trim() : "";
        String currUserId = currentUserId != null ? currentUserId.trim() : "";

        // ✅ GUID so sánh không phân biệt hoa/thường
        String msgSenderNorm = msgSenderId.toLowerCase(Locale.US);
        String currUserNorm = currUserId.toLowerCase(Locale.US);

        boolean isSentById = !currUserNorm.isEmpty() && msgSenderNorm.equals(currUserNorm);

        boolean isSent;
        if (!currUserNorm.isEmpty()) {
            isSent = isSentById;
        } else {
            isSent = !message.isFromLandlord();
        }

        if (position < 10) {
            android.util.Log.d("ChatAdapter", "Message " + position
                    + ": msgSender='" + msgSenderId + "' current='" + currUserId
                    + "' senderNorm='" + msgSenderNorm + "' currentNorm='" + currUserNorm
                    + "' => isSent=" + isSent);
        }

        return isSent ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;

        if (viewType == VIEW_TYPE_SENT) {
            view = inflater.inflate(R.layout.item_chat_message_sent, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_chat_message_received, parent, false);
        }

        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.bind(message);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Update messages from API
     */
    public void updateMessages(List<ChatMessage> newMessages) {
        this.messages.clear();
        this.messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    /**
     * Add a single message (for optimistic updates)
     */
    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        notifyItemInserted(this.messages.size() - 1);
    }

    /**
     * Inner class for message view holder
     */
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessageContent;
        private TextView tvMessageTime;
        private TextView tvSenderName;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageContent = itemView.findViewById(R.id.tv_message_content);
            tvMessageTime = itemView.findViewById(R.id.tv_message_time);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name);
        }

        public void bind(ChatMessage message) {
            tvMessageContent.setText(message.getContent());

            // Format thời gian
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String timeString = sdf.format(new Date(message.getTimestamp()));
            tvMessageTime.setText(timeString);

            // ✅ Theo yêu cầu: KHÔNG hiển thị userId/tên trong bubble.
            // Chỉ hiển thị tên ở tiêu đề (tv_chat_header) trong ChatActivity.
            if (tvSenderName != null) {
                tvSenderName.setVisibility(View.GONE);
            }
        }
    }
}
