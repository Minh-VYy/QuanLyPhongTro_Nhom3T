package com.example.QuanLyPhongTro_App.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.QuanLyPhongTro_App.data.MockData;
import com.example.QuanLyPhongTro_App.data.request.LoginRequest;
import com.example.QuanLyPhongTro_App.data.request.RegisterRequest;
import com.example.QuanLyPhongTro_App.data.response.LoginResponse;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * AccountManager - Quản lý đăng ký và lưu trữ tài khoản người dùng
 * Sử dụng SharedPreferences để lưu trữ dữ liệu tài khoản cục bộ
 */
public class AccountManager {
    private static final String PREF_NAME = "UserAccounts";
    private static final String KEY_ACCOUNTS = "accounts";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public AccountManager(Context context) {
        this.context = context;
        this.pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = pref.edit();

        // Ensure MockData is initialized for chat persistence
        MockData.init(context);

        // Khởi tạo pre-created accounts nếu chưa có
        if (!isInitialized()) {
            android.util.Log.d("AccountManager", "Initializing pre-created accounts...");
            initializePreCreatedAccounts();
        } else {
            android.util.Log.d("AccountManager", "Accounts already initialized");
        }
    }

    /**
     * Kiểm tra xem app đã khởi tạo accounts hay chưa
     */
    private boolean isInitialized() {
        return pref.contains(KEY_ACCOUNTS);
    }

    /**
     * Khởi tạo pre-created accounts (Người thuê và Chủ trọ)
     */
    private void initializePreCreatedAccounts() {
        JSONArray accounts = new JSONArray();
        try {
            // Tenant 1
            JSONObject tenant1 = new JSONObject();
            tenant1.put("email", "tenant1@gmail.com");
            tenant1.put("password", "123456");
            tenant1.put("fullName", "Nguyễn Văn A");
            tenant1.put("phoneNumber", "0905111111");
            tenant1.put("userType", "tenant");
            accounts.put(tenant1);

            // Tenant 2
            JSONObject tenant2 = new JSONObject();
            tenant2.put("email", "tenant2@gmail.com");
            tenant2.put("password", "123456");
            tenant2.put("fullName", "Trần Thị X");
            tenant2.put("phoneNumber", "0905222222");
            tenant2.put("userType", "tenant");
            accounts.put(tenant2);

            // Tenant 3
            JSONObject tenant3 = new JSONObject();
            tenant3.put("email", "tenant3@gmail.com");
            tenant3.put("password", "123456");
            tenant3.put("fullName", "Hoàng Thị Y");
            tenant3.put("phoneNumber", "0905333333");
            tenant3.put("userType", "tenant");
            accounts.put(tenant3);

            // Landlord 1
            JSONObject landlord1 = new JSONObject();
            landlord1.put("email", "landlord1@gmail.com");
            landlord1.put("password", "123456");
            landlord1.put("fullName", "Nguyễn Văn Chủ");
            landlord1.put("phoneNumber", "0905444444");
            landlord1.put("userType", "landlord");
            accounts.put(landlord1);

            // Landlord 2
            JSONObject landlord2 = new JSONObject();
            landlord2.put("email", "landlord2@gmail.com");
            landlord2.put("password", "123456");
            landlord2.put("fullName", "Trần Thị B");
            landlord2.put("phoneNumber", "0905555555");
            landlord2.put("userType", "landlord");
            accounts.put(landlord2);

            // Landlord 3
            JSONObject landlord3 = new JSONObject();
            landlord3.put("email", "landlord3@gmail.com");
            landlord3.put("password", "123456");
            landlord3.put("fullName", "Lê Văn C");
            landlord3.put("phoneNumber", "0905666666");
            landlord3.put("userType", "landlord");
            accounts.put(landlord3);

            editor.putString(KEY_ACCOUNTS, accounts.toString());
            editor.apply();
            android.util.Log.d("AccountManager", "Pre-created accounts initialized successfully!");
            android.util.Log.d("AccountManager", "Total accounts: " + accounts.length());
        } catch (JSONException e) {
            android.util.Log.e("AccountManager", "Error initializing accounts: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đăng ký tài khoản mới
     * @return true nếu đăng ký thành công, false nếu email đã tồn tại
     */
    public boolean registerAccount(String email, String password, String fullName, String phoneNumber, String userType) {
        try {
            // Kiểm tra email đã tồn tại hay chưa
            if (getAccount(email) != null) {
                return false; // Email đã tồn tại
            }

            JSONArray accounts = getAccounts();
            JSONObject newAccount = new JSONObject();
            newAccount.put("email", email);
            newAccount.put("password", password);
            newAccount.put("fullName", fullName);
            newAccount.put("phoneNumber", phoneNumber);
            newAccount.put("userType", userType);

            accounts.put(newAccount);
            editor.putString(KEY_ACCOUNTS, accounts.toString());
            editor.apply();
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Xác thực đăng nhập
     * @return JSONObject chứa thông tin tài khoản nếu đúng, null nếu sai
     */
    public JSONObject loginAccount(String email, String password) {
        try {
            JSONObject account = getAccount(email);
            if (account != null) {
                android.util.Log.d("AccountManager", "Account found for " + email);
                String storedPassword = account.getString("password");
                android.util.Log.d("AccountManager", "Stored password: " + storedPassword + ", Input: " + password);
                if (storedPassword.equals(password)) {
                    android.util.Log.d("AccountManager", "Password match!");
                    return account;
                } else {
                    android.util.Log.d("AccountManager", "Password mismatch!");
                }
            } else {
                android.util.Log.d("AccountManager", "No account found for " + email);
            }
            return null;
        } catch (JSONException e) {
            android.util.Log.e("AccountManager", "Login error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy thông tin tài khoản bằng email
     */
    public JSONObject getAccount(String email) {
        try {
            JSONArray accounts = getAccounts();
            for (int i = 0; i < accounts.length(); i++) {
                JSONObject account = accounts.getJSONObject(i);
                if (account.getString("email").equals(email)) {
                    return account;
                }
            }
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy danh sách tất cả tài khoản
     */
    private JSONArray getAccounts() {
        try {
            String accountsJson = pref.getString(KEY_ACCOUNTS, "[]");
            return new JSONArray(accountsJson);
        } catch (JSONException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }

    /**
     * Kiểm tra email đã tồn tại hay chưa
     */
    public boolean emailExists(String email) {
        return getAccount(email) != null;
    }

    /**
     * Lấy danh sách tài khoản theo loại (tenant hoặc landlord)
     */
    public JSONArray getAccountsByType(String userType) {
        JSONArray result = new JSONArray();
        try {
            JSONArray accounts = getAccounts();
            for (int i = 0; i < accounts.length(); i++) {
                JSONObject account = accounts.getJSONObject(i);
                if (account.getString("userType").equals(userType)) {
                    result.put(account);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Xóa tất cả tài khoản (dùng cho testing)
     */
    public void clearAllAccounts() {
        editor.remove(KEY_ACCOUNTS);
        editor.apply();
    }

    // ==================== API METHODS ====================

    /**
     * Gọi API login
     * @param email Email người dùng
     * @param password Password
     * @param callback Callback khi hoàn thành
     */
    public void loginAPI(String email, String password, AuthCallback callback) {
        try {
            ApiService apiService = ApiClient.getRetrofit().create(ApiService.class);
            LoginRequest request = new LoginRequest(email, password);

            apiService.login(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    android.util.Log.d("AccountManager", "Login response code: " + response.code());

                    if (response.isSuccessful() && response.body() != null) {
                        String token = response.body().getToken();
                        if (token != null && !token.isEmpty()) {
                            android.util.Log.d("AccountManager", "Login successful, token received");
                            SessionManager sessionManager = new SessionManager(context);
                            sessionManager.saveToken(token);
                            // Set token in ApiClient for future requests
                            ApiClient.setToken(token);
                            callback.onSuccess(token);
                        } else {
                            android.util.Log.e("AccountManager", "Token is null or empty");
                            callback.onError("Token is null or empty");
                        }
                    } else {
                        String errorMsg = response.message() != null ? response.message() : "Login failed";
                        android.util.Log.e("AccountManager", "Login failed: " + errorMsg);
                        callback.onError(errorMsg);
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    android.util.Log.e("AccountManager", "Login API error: " + t.getMessage());
                    callback.onError("Network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            android.util.Log.e("AccountManager", "Exception in loginAPI: " + e.getMessage());
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
