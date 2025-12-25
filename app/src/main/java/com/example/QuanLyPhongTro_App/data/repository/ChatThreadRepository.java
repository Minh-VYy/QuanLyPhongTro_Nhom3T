package com.example.QuanLyPhongTro_App.data.repository;

import android.util.Log;
import com.example.QuanLyPhongTro_App.data.response.ChatThreadDto;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * Chat Thread Repository - fetch threads from API
 */
public class ChatThreadRepository {
    private static final String TAG = "ChatThreadRepository";
    private ApiService apiService;

    public ChatThreadRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    /**
     * Get chat threads for current user (tenant or landlord)
     * Uses /api/chat/contacts endpoint
     */
    public void getChatThreads(String userId, ThreadsCallback callback) {
        try {
            // Add null check and logging
            if (userId == null || userId.isEmpty()) {
                Log.e(TAG, "❌ ERROR: userId is NULL or EMPTY!");
                callback.onError("userId is null or empty. Please login again.");
                return;
            }

            Log.d(TAG, "Fetching chat contacts for userId: " + userId);

            apiService.getChatContacts(userId).enqueue(new Callback<List<Object>>() {
                @Override
                public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                    Log.d(TAG, "=============== RESPONSE DEBUG ===============");
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Request URL: " + call.request().url());
                    Log.d(TAG, "Response body is null: " + (response.body() == null));

                    if (response.isSuccessful() && response.body() != null) {
                        // API returns raw JSON array, not wrapped in GenericResponse
                        List<Object> contactsData = response.body();

                        Log.d(TAG, "✅ Response successful!");
                        Log.d(TAG, "📋 contactsData size: " + contactsData.size());
                        Log.d(TAG, "📋 contactsData type: " + contactsData.getClass().getName());

                        // Log entire response as JSON
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        String fullResponseJson = gson.toJson(contactsData);
                        Log.d(TAG, "📋 Full Response JSON:\n" + fullResponseJson);

                        List<ChatThreadDto> threads = convertContactsToThreads(contactsData);

                        Log.d(TAG, "✅ Got " + threads.size() + " threads");
                        callback.onSuccess(threads);
                    } else {
                        String errorMsg = response.message() != null ? response.message() : "Unknown error";
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Error reading error body: " + e.getMessage());
                        }

                        Log.e(TAG, "Response not successful. Code: " + response.code() + ", Message: " + errorMsg);
                        Log.e(TAG, "Error body: " + errorBody);
                        callback.onError("Failed to fetch contacts: " + errorMsg + " (Code: " + response.code() + ")");
                    }
                }

                @Override
                public void onFailure(Call<List<Object>> call, Throwable t) {
                    Log.e(TAG, "❌ Network error: " + t.getMessage());
                    Log.e(TAG, "Exception: ", t);
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Exception in getChatThreads: " + e.getMessage());
            e.printStackTrace();
            callback.onError("Exception: " + e.getMessage());
        }
    }

    /**
     * Convert API ContactResponse list to ChatThreadDto
     * API returns: { userId, userName, lastMessage, lastMessageTime, unreadCount }
     * userId = the OTHER user's ID (the person we're chatting with)
     */
    private List<ChatThreadDto> convertContactsToThreads(List<Object> contactsData) {
        List<ChatThreadDto> threads = new ArrayList<>();

        if (contactsData == null) {
            Log.e(TAG, "❌ contactsData is NULL!");
            return threads;
        }

        Log.d(TAG, "📋 Total contacts received: " + contactsData.size());

        com.google.gson.Gson gson = new com.google.gson.Gson();

        for (int i = 0; i < contactsData.size(); i++) {
            Object contact = contactsData.get(i);
            try {
                // ✅ BETTER APPROACH: Deserialize directly to ChatThreadDto
                // This ensures proper mapping according to @SerializedName annotations
                String rawJson = gson.toJson(contact);
                Log.d(TAG, "--- Contact #" + i + " Raw JSON ---");
                Log.d(TAG, rawJson);

                // Deserialize to ContactResponse-like object first, then map to ChatThreadDto
                java.util.Map<String, Object> contactMap = gson.fromJson(
                    rawJson,
                    java.util.Map.class
                );

                ChatThreadDto thread = new ChatThreadDto();

                // ✅ FIXED: API returns PascalCase: UserId, UserName, etc.
                // Log all keys to debug
                Log.d(TAG, "   Full contactMap keys: " + contactMap.keySet());

                Object userIdObj = contactMap.get("UserId");
                String otherUserId = "";

                if (userIdObj != null) {
                    otherUserId = userIdObj.toString().trim();  // ✅ Trim whitespace
                    Log.d(TAG, "   - UserId: " + otherUserId);
                } else {
                    Log.w(TAG, "   - UserId: NULL");
                }

                Log.d(TAG, "   UserId field type: " + (userIdObj != null ? userIdObj.getClass().getSimpleName() : "NULL"));
                Log.d(TAG, "   UserId value: '" + otherUserId + "'");
                Log.d(TAG, "   UserId isEmpty: " + otherUserId.isEmpty());

                if (otherUserId.isEmpty()) {
                    Log.w(TAG, "⚠️  WARNING: UserId is EMPTY for contact #" + i);
                    for (String key : contactMap.keySet()) {
                        Log.d(TAG, "     - " + key + ": " + contactMap.get(key));
                    }
                    continue;  // ✅ Skip contacts with empty UserId
                }

                String otherUserName = (String) contactMap.getOrDefault("UserName", "Unknown");
                thread.setThreadId(otherUserId);
                thread.setOtherUserId(otherUserId);        // ✅ NEW: Set generic otherUserId
                thread.setLandlordId(otherUserId);  // Always set for proper intent passing
                thread.setTenantId(otherUserId);
                thread.setLandlordName(otherUserName);
                thread.setTenantName(otherUserName);

                // Message info
                thread.setLastMessage((String) contactMap.getOrDefault("LastMessage", ""));

                Object lastMsgTime = contactMap.get("LastMessageTime");
                if (lastMsgTime != null) {
                    thread.setLastMessageTime(lastMsgTime.toString());
                }

                // Unread count
                Object unreadCount = contactMap.get("UnreadCount");
                if (unreadCount != null) {
                    try {
                        thread.setUnreadCount(((Number) unreadCount).intValue());
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing UnreadCount: " + e.getMessage());
                    }
                }

                Log.d(TAG, "✅ Converted contact #" + i + ": threadId=" + otherUserId + ", name=" + otherUserName);
                threads.add(thread);
            } catch (Exception e) {
                Log.e(TAG, "❌ Error converting contact #" + i + ": " + e.getMessage());
                e.printStackTrace();
            }
        }

        return threads;
    }

    // ==================== CALLBACKS ====================

    public interface ThreadsCallback {
        void onSuccess(List<ChatThreadDto> threads);
        void onError(String error);
    }
}

