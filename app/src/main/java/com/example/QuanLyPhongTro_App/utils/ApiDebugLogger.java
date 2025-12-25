package com.example.QuanLyPhongTro_App.utils;

import android.util.Log;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Debug utility để log chi tiết API requests/responses
 * Giúp debug vấn đề khi API không hoạt động đúng
 */
public class ApiDebugLogger {
    private static final String TAG = "API_DEBUG";
    private static final boolean ENABLE_LOGGING = true; // Set false khi release

    /**
     * Log API request
     */
    public static void logRequest(String method, String url, Object body) {
        if (!ENABLE_LOGGING) return;

        Log.d(TAG, "════════════════════════════════════════════");
        Log.d(TAG, "📤 API REQUEST");
        Log.d(TAG, "Method: " + method);
        Log.d(TAG, "URL: " + url);

        if (body != null) {
            try {
                String jsonStr = new com.google.gson.Gson().toJson(body);
                JSONObject json = new JSONObject(jsonStr);
                Log.d(TAG, "Body: " + json.toString(2)); // Pretty print with indent
            } catch (Exception e) {
                Log.d(TAG, "Body: " + body.toString());
            }
        }
        Log.d(TAG, "════════════════════════════════════════════");
    }

    /**
     * Log API response success
     */
    public static void logResponse(int code, Object responseBody) {
        if (!ENABLE_LOGGING) return;

        Log.d(TAG, "════════════════════════════════════════════");
        Log.d(TAG, "📥 API RESPONSE");
        Log.d(TAG, "Status Code: " + code);

        if (responseBody != null) {
            try {
                String jsonStr = new com.google.gson.Gson().toJson(responseBody);

                // Try to parse as JSONObject
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    Log.d(TAG, "Response Body: " + json.toString(2));

                    // Log specific fields
                    if (json.has("success")) {
                        Log.d(TAG, "✅ Success: " + json.getBoolean("success"));
                    }
                    if (json.has("message")) {
                        Log.d(TAG, "📝 Message: " + json.getString("message"));
                    }
                    if (json.has("data")) {
                        Object data = json.get("data");
                        if (data instanceof JSONArray) {
                            JSONArray arr = (JSONArray) data;
                            Log.d(TAG, "📊 Data Count: " + arr.length());
                            if (arr.length() > 0) {
                                Log.d(TAG, "📌 First Item: " + arr.getJSONObject(0).toString(2));
                            }
                        } else {
                            Log.d(TAG, "📊 Data: " + data.toString());
                        }
                    }
                } catch (Exception e) {
                    // Try as JSONArray
                    try {
                        JSONArray json = new JSONArray(jsonStr);
                        Log.d(TAG, "Response Body (Array): " + json.toString(2));
                        Log.d(TAG, "📊 Array Count: " + json.length());
                    } catch (Exception e2) {
                        Log.d(TAG, "Response Body: " + jsonStr);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, "Response Body: " + responseBody.toString());
            }
        }
        Log.d(TAG, "════════════════════════════════════════════");
    }

    /**
     * Log API error
     */
    public static void logError(int code, String errorMessage, String errorBody) {
        if (!ENABLE_LOGGING) return;

        Log.e(TAG, "════════════════════════════════════════════");
        Log.e(TAG, "❌ API ERROR");
        Log.e(TAG, "Status Code: " + code);
        Log.e(TAG, "Error Message: " + errorMessage);

        if (errorBody != null && !errorBody.isEmpty()) {
            try {
                // Try to parse error body as JSON
                JSONObject json = new JSONObject(errorBody);
                Log.e(TAG, "Error Body: " + json.toString(2));

                // Log specific error fields
                if (json.has("message")) {
                    Log.e(TAG, "📝 Error Details: " + json.getString("message"));
                }
                if (json.has("errors")) {
                    Log.e(TAG, "🔍 Validation Errors: " + json.get("errors").toString());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error Body (Raw): " + errorBody);
            }
        }
        Log.e(TAG, "════════════════════════════════════════════");
    }

    /**
     * Log network failure
     */
    public static void logNetworkFailure(String url, Throwable error) {
        if (!ENABLE_LOGGING) return;

        Log.e(TAG, "════════════════════════════════════════════");
        Log.e(TAG, "🔌 NETWORK FAILURE");
        Log.e(TAG, "URL: " + url);
        Log.e(TAG, "Error Type: " + error.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + error.getMessage());

        if (error.getCause() != null) {
            Log.e(TAG, "Caused By: " + error.getCause().getMessage());
        }

        // Common network errors
        if (error instanceof java.net.UnknownHostException) {
            Log.e(TAG, "💡 TIP: Kiểm tra kết nối internet hoặc URL server");
        } else if (error instanceof java.net.SocketTimeoutException) {
            Log.e(TAG, "💡 TIP: Server quá lâu không phản hồi, kiểm tra server");
        } else if (error instanceof java.net.ConnectException) {
            Log.e(TAG, "💡 TIP: Không thể kết nối đến server, kiểm tra IP/port");
        }

        Log.e(TAG, "════════════════════════════════════════════");
    }

    /**
     * Log field mismatch (khi field từ API khác với expected)
     */
    public static void logFieldMismatch(String className, String expectedField, String actualField) {
        if (!ENABLE_LOGGING) return;

        Log.w(TAG, "════════════════════════════════════════════");
        Log.w(TAG, "⚠️ FIELD MISMATCH WARNING");
        Log.w(TAG, "Class: " + className);
        Log.w(TAG, "Expected Field: " + expectedField);
        Log.w(TAG, "Actual Field: " + actualField);
        Log.w(TAG, "💡 TIP: Kiểm tra tên field trong C# backend có khớp với Android không");
        Log.w(TAG, "════════════════════════════════════════════");
    }

    /**
     * Log data conversion (từ API response sang model)
     */
    public static void logDataConversion(String fromType, String toType, int count) {
        if (!ENABLE_LOGGING) return;

        Log.i(TAG, "════════════════════════════════════════════");
        Log.i(TAG, "🔄 DATA CONVERSION");
        Log.i(TAG, "From: " + fromType);
        Log.i(TAG, "To: " + toType);
        Log.i(TAG, "Count: " + count);
        Log.i(TAG, "════════════════════════════════════════════");
    }

    /**
     * Log summary (tóm tắt kết quả API call)
     */
    public static void logSummary(String operation, boolean success, String details) {
        if (!ENABLE_LOGGING) return;

        String icon = success ? "✅" : "❌";
        Log.i(TAG, "════════════════════════════════════════════");
        Log.i(TAG, icon + " " + operation.toUpperCase());
        Log.i(TAG, "Result: " + (success ? "SUCCESS" : "FAILED"));
        Log.i(TAG, "Details: " + details);
        Log.i(TAG, "════════════════════════════════════════════");
    }
}

