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
            // ✅ Trim all IDs to remove whitespace
            if (fromUserId != null) fromUserId = fromUserId.trim();
            if (toUserId != null) toUserId = toUserId.trim();

            Log.d(TAG, "=== SENDING MESSAGE ===");
            Log.d(TAG, "From: '" + fromUserId + "' (length: " + (fromUserId != null ? fromUserId.length() : "null") + ")");
            Log.d(TAG, "To: '" + toUserId + "' (length: " + (toUserId != null ? toUserId.length() : "null") + ")");
            Log.d(TAG, "Content: '" + noiDung + "'");

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

            // ✅ FIX: Ensure no special characters or newlines in IDs
            fromUserId = fromUserId.replaceAll("[^a-zA-Z0-9-]", "");
            toUserId = toUserId.replaceAll("[^a-zA-Z0-9-]", "");

            if (fromUserId.isEmpty() || toUserId.isEmpty()) {
                Log.e(TAG, "❌ IDs invalid after cleaning!");
                callback.onError("Lỗi: ID không hợp lệ");
                return;
            }

            // Create request body matching C# SendChatMessageRequest
            // C# expects: { FromUserId, ToUserId, Content, MessageType }
            Map<String, Object> messageRequest = new HashMap<>();
            messageRequest.put("FromUserId", fromUserId);
            messageRequest.put("ToUserId", toUserId);
            messageRequest.put("Content", noiDung);
            messageRequest.put("MessageType", "text");

            String requestJson = new Gson().toJson(messageRequest);
            Log.d(TAG, "Request body: " + requestJson);
            Log.d(TAG, "Request size: " + requestJson.length() + " bytes");

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
                        Log.d(TAG, "Response body: " + new Gson().toJson(response.body()));
                    } else {
                        Log.d(TAG, "⚠️ Response body is null");
                    }

                    // ✅ FIX: Check HTTP status code first (isSuccessful = 2xx)
                    // Backend returns 200 OK even for successful sends
                    if (response.isSuccessful() && response.code() >= 200 && response.code() < 300) {
                        Log.d(TAG, "✅✅✅ Message sent successfully (HTTP " + response.code() + ") ✅✅✅");
                        callback.onSuccess("Message sent");
                    } else if (response.isSuccessful()) {
                        // Still 2xx but maybe not exactly what we expected
                        Log.d(TAG, "✅ Got 2xx response: " + response.code());
                        callback.onSuccess("Message sent");
                    } else {
                        String error = "HTTP " + response.code();
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                                error += ": " + errorBody;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body", e);
                        }

                        // 🔍 DEBUG: Log error chi tiết
                        Log.e(TAG, "❌❌❌ Send failed - Code: " + response.code()
                            + ", Message: " + response.message()
                            + ", Body: " + errorBody);

                        com.example.QuanLyPhongTro_App.utils.ApiDebugLogger.logError(
                            response.code(),
                            response.message(),
                            error
                        );

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
                    Log.d(TAG, "📥 Message history response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            // API returns raw JSON array directly
                            List<Object> messages = response.body();
                            Log.d(TAG, "📥 API returned " + messages.size() + " objects");

                            // Convert to ChatMessage list using proper model
                            Gson gson = new Gson();
                            java.util.List<com.example.QuanLyPhongTro_App.data.model.ChatMessage> chatMessages = new java.util.ArrayList<>();

                            if (messages != null) {
                                for (Object msg : messages) {
                                    try {
                                        // Convert to proper model: com.example.QuanLyPhongTro_App.data.model.ChatMessage
                                        com.example.QuanLyPhongTro_App.data.model.ChatMessage chatMsg = gson.fromJson(
                                            gson.toJson(msg),
                                            com.example.QuanLyPhongTro_App.data.model.ChatMessage.class
                                        );

                                        if (chatMsg != null && chatMsg.noiDung != null) {
                                            Log.d(TAG, "✏️ Parsed: from=" + chatMsg.fromUser + ", content=" + chatMsg.noiDung);
                                            chatMessages.add(chatMsg);
                                        } else {
                                            Log.w(TAG, "⚠️ Skipping null message or null content");
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "❌ Error parsing message: " + e.getMessage(), e);
                                    }
                                }
                            }

                            Log.d(TAG, "✅ Successfully converted " + chatMessages.size() + " messages");
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
        void onSuccess(List<com.example.QuanLyPhongTro_App.data.model.ChatMessage> messages);
        void onError(String error);
    }

    public interface ReadCallback {
        void onSuccess();
        void onError(String error);
    }
}

