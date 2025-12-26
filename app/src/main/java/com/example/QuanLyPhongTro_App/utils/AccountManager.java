package com.example.QuanLyPhongTro_App.utils;

import android.content.Context;
import com.example.QuanLyPhongTro_App.data.request.LoginRequest;
import com.example.QuanLyPhongTro_App.data.request.RegisterRequest;
import com.example.QuanLyPhongTro_App.data.response.LoginResponse;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AccountManager - Quản lý đăng nhập/đăng ký qua API
 * Không sử dụng local accounts nữa (đã xóa)
 */
public class AccountManager {
    private Context context;

    public AccountManager(Context context) {
        this.context = context;
    }

    /**
     * Gọi API login
     * @param email Email người dùng
     * @param password Password
     * @param callback Callback khi hoàn thành
     */
    public void loginAPI(String email, String password, AuthCallback callback) {
        try {
            android.util.Log.d("AccountManager", "=== LOGIN API CALL ===");
            android.util.Log.d("AccountManager", "Email: " + email);
            android.util.Log.d("AccountManager", "Password: " + password);
            android.util.Log.d("AccountManager", "Password length: " + password.length());
            android.util.Log.d("AccountManager", "URL: http://18.140.64.80:5000/api/nguoidung/login");
            android.util.Log.d("AccountManager", "Request Body: {\"Email\":\"" + email + "\",\"Password\":\"" + password + "\"}");

            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            LoginRequest request = new LoginRequest(email, password);

            apiService.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    android.util.Log.d("AccountManager", "--- LOGIN RESPONSE ---");
                    android.util.Log.d("AccountManager", "Response Code: " + response.code());
                    android.util.Log.d("AccountManager", "Is Successful: " + response.isSuccessful());
                    android.util.Log.d("AccountManager", "Message: " + response.message());

                    if (response.body() != null && response.body().getToken() != null) {
                        android.util.Log.d("AccountManager", "Token received: " + response.body().getToken().substring(0, Math.min(50, response.body().getToken().length())) + "...");
                    }

                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();
                        if (token != null && !token.isEmpty()) {
                            android.util.Log.d("AccountManager", "✅ LOGIN SUCCESS");

                            // Extract role from JWT token
                            String roleString = JwtTokenParser.getRoleFromToken(token);
                            String userId = JwtTokenParser.getUserIdFromToken(token);

                            android.util.Log.d("AccountManager", "Role from JWT: " + roleString);
                            android.util.Log.d("AccountManager", "User ID from JWT: " + userId);

                            SessionManager sessionManager = new SessionManager(context);
                            sessionManager.saveToken(token);

                            // ⚡ CRITICAL: Get userId AFTER saveToken, which extracts from JWT
                            String extractedUserId = sessionManager.getUserId();

                            // Save user info
                            String email = response.body().email != null ? response.body().email : "";
                            String hoTen = response.body().hoTen != null ? response.body().hoTen : email;

                            // ✅ Prefer explicit UserId from login response if backend provides it
                            String userIdFromResponse = response.body().userId;
                            if (userIdFromResponse != null) userIdFromResponse = userIdFromResponse.trim();

                            // Parse from JWT (after JwtTokenParser fix)
                            String userIdFromJwt = JwtTokenParser.getUserIdFromToken(token);
                            if (userIdFromJwt != null) userIdFromJwt = userIdFromJwt.trim();

                            // Priority: response.userId -> jwt -> (as last resort) email
                            String finalUserId = null;
                            if (userIdFromResponse != null && !userIdFromResponse.isEmpty()) {
                                finalUserId = userIdFromResponse;
                            } else if (userIdFromJwt != null && !userIdFromJwt.isEmpty()) {
                                finalUserId = userIdFromJwt;
                            } else {
                                // last resort (should not happen in production)
                                finalUserId = (email != null && !email.isEmpty()) ? email : "user@example.com";
                            }

                            android.util.Log.d("AccountManager", "UserId from response: " + userIdFromResponse);
                            android.util.Log.d("AccountManager", "UserId from JWT: " + userIdFromJwt);
                            android.util.Log.d("AccountManager", "Final userId to save: " + finalUserId);

                            sessionManager.createLoginSession(
                                finalUserId,
                                hoTen,
                                email,
                                roleString
                            );
                            sessionManager.setDisplayRole(roleString);

                            // Also ensure token saved (and SessionManager.saveToken will store userId/role too)
                            sessionManager.saveToken(token);

                            ApiClient.setToken(token);

                            // ⚡ CRITICAL FIX: If userId still not a valid GUID, call /api/nguoidung/me
                            if (extractedUserId == null || extractedUserId.isEmpty() || extractedUserId.equals(email)) {
                                android.util.Log.w("AccountManager", "⚠️ userId not extracted from JWT or = email");
                                android.util.Log.w("AccountManager", "Calling /api/nguoidung/me to get real GUID...");

                                // Call /me API to get userId
                                ApiService api = ApiClient.getRetrofit().create(ApiService.class);
                                api.getUserProfile().enqueue(new Callback<GenericResponse<Object>>() {
                                    @Override
                                    public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> meResponse) {
                                        if (meResponse.isSuccessful() && meResponse.body() != null && meResponse.body().data != null) {
                                            try {
                                                com.google.gson.Gson gson = new com.google.gson.Gson();
                                                java.util.Map<String, Object> userData = gson.fromJson(
                                                    gson.toJson(meResponse.body().data),
                                                    java.util.Map.class
                                                );

                                                Object userIdObj = userData.get("nguoiDungId");
                                                if (userIdObj == null) userIdObj = userData.get("NguoiDungId"); // Try PascalCase

                                                if (userIdObj != null) {
                                                    String userIdFromApi = userIdObj.toString();
                                                    android.util.Log.d("AccountManager", "✅ Got userId from /me API: " + userIdFromApi);

                                                    // Manually save userId to SharedPreferences
                                                    android.content.SharedPreferences.Editor editor = context
                                                        .getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                                                        .edit();
                                                    editor.putString("userId", userIdFromApi);
                                                    editor.apply();

                                                    android.util.Log.d("AccountManager", "✅ Manually saved userId to session");
                                                } else {
                                                    android.util.Log.e("AccountManager", "❌ nguoiDungId not found in /me response");
                                                }
                                            } catch (Exception e) {
                                                android.util.Log.e("AccountManager", "❌ Error parsing /me response: " + e.getMessage());
                                                e.printStackTrace();
                                            }
                                        } else {
                                            android.util.Log.e("AccountManager", "❌ /me API failed: " + meResponse.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                                        android.util.Log.e("AccountManager", "❌ /me API network error: " + t.getMessage());
                                    }
                                });
                            } else {
                                android.util.Log.d("AccountManager", "✅ userId already extracted from JWT: " + extractedUserId);
                            }

                            // Pass token as message (backward compatible)
                            callback.onSuccess(token);
                        } else {
                            android.util.Log.e("AccountManager", "❌ Token is null or empty in response");
                            callback.onError("Backend returned empty token");
                        }
                    } else {
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                                android.util.Log.e("AccountManager", "Error response body: " + errorBody);
                            }
                        } catch (Exception e) {
                            errorBody = "Unable to parse error body";
                        }

                        // Phân biệt các lỗi HTTP
                        String errorMsg = "";
                        if (response.code() == 401) {
                            android.util.Log.e("AccountManager", "❌ 401 UNAUTHORIZED");
                            android.util.Log.e("AccountManager", "=====================================");
                            android.util.Log.e("AccountManager", "NGUYÊN NHÂN CÓ THỂ:");
                            android.util.Log.e("AccountManager", "1. Email: " + email + " không tồn tại trên backend");
                            android.util.Log.e("AccountManager", "2. Password: " + password + " không khớp với PasswordHash");
                            android.util.Log.e("AccountManager", "3. Backend yêu cầu PasswordHash (hashed) chứ không phải plain password");
                            android.util.Log.e("AccountManager", "4. Endpoint login có require authorization khác");
                            android.util.Log.e("AccountManager", "=====================================");
                            errorMsg = "❌ Lỗi 401: Xác thực thất bại\n\nKiểm tra:\n- Email có tồn tại?\n- Password đúng?\n- Backend có hash password?";
                        } else if (response.code() == 400) {
                            android.util.Log.e("AccountManager", "❌ 400 BAD REQUEST");
                            android.util.Log.e("AccountManager", "Dữ liệu gửi không đúng định dạng hoặc validate fail");
                            errorMsg = "❌ Lỗi 400: Dữ liệu không hợp lệ";
                        } else if (response.code() == 403) {
                            android.util.Log.e("AccountManager", "❌ 403 FORBIDDEN");
                            errorMsg = "❌ Lỗi 403: Không có quyền truy cập";
                        } else if (response.code() == 500) {
                            android.util.Log.e("AccountManager", "❌ 500 SERVER ERROR");
                            errorMsg = "❌ Lỗi 500: Server lỗi nội bộ";
                        } else {
                            errorMsg = "❌ Login failed (" + response.code() + "): " + response.message();
                        }

                        android.util.Log.e("AccountManager", "Error body: " + errorBody);
                        callback.onError(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    android.util.Log.e("AccountManager", "❌ LOGIN API NETWORK ERROR");
                    android.util.Log.e("AccountManager", "Error: " + t.getMessage());
                    android.util.Log.e("AccountManager", "Cause: " + (t.getCause() != null ? t.getCause().getMessage() : "No cause"));
                    t.printStackTrace();
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            android.util.Log.e("AccountManager", "❌ Exception in loginAPI: " + e.getMessage());
            e.printStackTrace();
            callback.onError("Exception: " + e.getMessage());
        }
    }

    /**
     * Gọi API register
     * @param email Email
     * @param password Password
     * @param hoTen Họ tên
     * @param dienThoai Điện thoại
     * @param vaiTroId Role ID (2 = landlord, 3 = tenant)
     * @param callback Callback khi hoàn thành
     */
    public void registerAPI(String email, String password, String hoTen, String dienThoai,
                           int vaiTroId, AuthCallback callback) {
        try {
            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            RegisterRequest request = new RegisterRequest(email, password, dienThoai, hoTen, vaiTroId);

            apiService.register(request).enqueue(new Callback<GenericResponse<Object>>() {
                @Override
                public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                    android.util.Log.d("AccountManager", "Register response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null && response.body().success) {
                        android.util.Log.d("AccountManager", "Register successful");
                        callback.onSuccess("Register successful");
                    } else {
                        String errorMsg = response.body() != null && response.body().message != null
                            ? response.body().message
                            : (response.message() != null ? response.message() : "Register failed");
                        android.util.Log.e("AccountManager", "Register failed: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                    android.util.Log.e("AccountManager", "Register API error: " + t.getMessage());
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            android.util.Log.e("AccountManager", "Exception in registerAPI: " + e.getMessage());
            callback.onError("Exception: " + e.getMessage());
        }
    }

    /**
     * Callback interface cho API calls
     */
    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }
}
