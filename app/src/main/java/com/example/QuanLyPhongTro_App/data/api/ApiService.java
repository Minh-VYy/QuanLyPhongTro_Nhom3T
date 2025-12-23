package com.example.QuanLyPhongTro_App.data.api;

import com.example.QuanLyPhongTro_App.data.model.Room;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    // Ví dụ về một lệnh gọi GET để lấy danh sách các phòng trọ
    // Thay thế "rooms" bằng endpoint thực tế của bạn
    @GET("rooms")
    Call<List<Room>> getRooms();

    // Bạn có thể thêm các lệnh gọi API khác ở đây, ví dụ:
    // @GET("users/{id}")
    // Call<User> getUser(@Path("id") String userId);
    //
    // @POST("auth/login")
    // Call<LoginResponse> login(@Body LoginRequest request);
}
