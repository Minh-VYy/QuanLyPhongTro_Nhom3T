package com.example.QuanLyPhongTro_App.data.repository;

import android.util.Log;
import com.example.QuanLyPhongTro_App.data.model.ChatMessage;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Chat Repository - gọi API để gửi/nhận message
 */
public class ChatRepository {
    private static final String TAG = "ChatRepository";
    private ApiService apiService;

    public ChatRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    /**
     * Gửi message qua API
     * @param fromUserId Current user's ID (người gửi)
     * @param toUserId Recipient's ID (người nhận)
     * @param noiDung Message content
     * @param callback Callback for success/failure
     */
    public void sendMessage(String fromUserId, String toUserId, String noiDung, ChatCallback callback) {
        try {
            Log.d(TAG, "Sending message from: " + fromUserId + " to: " + toUserId + ", content: " + noiDung);

            // Validate user IDs
            if (fromUserId == null || fromUserId.isEmpty()) {
                Log.e(TAG, "❌ fromUserId is null or empty!");
                callback.onError("Lỗi: Không xác định người gửi");
                return;
            }

            if (toUserId == null || toUserId.isEmpty()) {
                Log.e(TAG, "❌ toUserId is null or empty!");
                callback.onError("Lỗi: Không xác định người nhận");
                return;
            }

            // Create request body matching C# SendChatMessageRequest
            // C# expects: { FromUserId, ToUserId, Content, MessageType }
            Map<String, Object> messageRequest = new HashMap<>();
            messageRequest.put("FromUserId", fromUserId);
            messageRequest.put("ToUserId", toUserId);
            messageRequest.put("Content", noiDung);
            messageRequest.put("MessageType", "text");

            Log.d(TAG, "Request body: " + new Gson().toJson(messageRequest));

            // 🔍 DEBUG: Log request
            com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logRequest(
                "POST",
                "/api/Chat/send",  // ⚠️ Note: Capital C
                messageRequest
            );

            apiService.sendMessage(messageRequest).enqueue(new Callback<GenericResponse<Object>>() {
                @Override
                public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                    Log.d(TAG, "Send message response code: " + response.code());

                    // 🔍 DEBUG: Log response
                    if (response.body() != null) {
                        com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logResponse(
                            response.code(),
                            response.body()
                        );
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().success) {
                            Log.d(TAG, "✅ Message sent successfully");
                            callback.onSuccess("Message sent");
                        } else {
                            String error = response.body().message != null ? response.body().message : "Send failed";
                            Log.e(TAG, "❌ Send failed: " + error);
                            callback.onError(error);
                        }
                    } else {
                        String error = "HTTP " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                error += ": " + response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }

                        // 🔍 DEBUG: Log error
                        com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logError(
                            response.code(),
                            response.message(),
                            error
                        );

                        Log.e(TAG, "❌ Send failed: " + error);
                        callback.onError(error);
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                    Log.e(TAG, "❌ Send message network error: " + t.getMessage(), t);

                    // 🔍 DEBUG: Log network failure
                    com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logNetworkFailure(
                        "/api/Chat/send",  // ⚠️ Note: Capital C
                        t
                    );

                    callback.onError("Lỗi kết nối: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception: " + e.getMessage(), e);
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Lấy lịch sử chat với user khác qua API
     * @param currentUserId Current user's ID
     * @param otherUserId Other user's ID to get conversation with
     * @param callback Callback for success/failure
     */
    public void getMessageHistory(String currentUserId, String otherUserId, HistoryCallback callback) {
        try {
            if (otherUserId == null || otherUserId.isEmpty()) {
                Log.e(TAG, "❌ otherUserId is null or empty");
                callback.onError("ID người dùng không hợp lệ");
                return;
            }

            Log.d(TAG, "Getting message history with: " + otherUserId);

            // API expects user1 and user2 parameters
            apiService.getMessageHistory(currentUserId, otherUserId, 1, 50).enqueue(new Callback<List<Object>>() {
                @Override
                public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                    Log.d(TAG, "Message history response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            // API returns raw JSON array directly
                            List<Object> messages = response.body();

                            // Convert to ChatMessage list
                            Gson gson = new Gson();
                            java.util.List<ChatMessage> chatMessages = new java.util.ArrayList<>();
                            if (messages != null) {
                                for (Object msg : messages) {
                                    try {
                                        ChatMessage chatMsg = gson.fromJson(gson.toJson(msg), ChatMessage.class);
                                        chatMessages.add(chatMsg);
                                    } catch (Exception e) {
                                        Log.e(TAG, "Error parsing message: " + e.getMessage());
                                    }
                                }
                            }

                            Log.d(TAG, "✅ Got " + chatMessages.size() + " messages");
                            callback.onSuccess(chatMessages);
                        } catch (Exception e) {
                            Log.e(TAG, "❌ Error processing history: " + e.getMessage(), e);
                            callback.onError("Lỗi xử lý dữ liệu: " + e.getMessage());
                        }
                    } else {
                        String error = "HTTP " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                error += ": " + response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }
                        Log.e(TAG, "❌ Get history failed: " + error);
                        callback.onError(error);
                    }
                }

                @Override
                public void onFailure(Call<List<Object>> call, Throwable t) {
                    Log.e(TAG, "❌ Get history network error: " + t.getMessage(), t);
                    callback.onError("Lỗi kết nối: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception: " + e.getMessage(), e);
            callback.onError("Lỗi: " + e.getMessage());
        }
    }

    /**
     * Đánh dấu message đã đọc
     */
    public void markAsRead(String otherUserId, ReadCallback callback) {
        try {
            Log.d(TAG, "Marking messages as read for: " + otherUserId);

            apiService.markAllMessagesAsRead(otherUserId, otherUserId).enqueue(new Callback<GenericResponse<Object>>() {
                @Override
                public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "✅ Messages marked as read");
                        callback.onSuccess();
                    } else {
                        Log.e(TAG, "❌ Mark as read failed");
                        callback.onError("Failed to mark as read");
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                    Log.e(TAG, "❌ Mark as read error: " + t.getMessage());
                    callback.onError(t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception: " + e.getMessage());
            callback.onError(e.getMessage());
        }
    }

    // ==================== CALLBACKS ====================

    public interface ChatCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface HistoryCallback {
        void onSuccess(List<ChatMessage> messages);
        void onError(String error);
    }

    public interface ReadCallback {
        void onSuccess();
        void onError(String error);
    }
}

