package com.example.QuanLyPhongTro_App.data.repository;

import com.example.QuanLyPhongTro_App.data.request.UpdateBookingStatusRequest;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.data.response.MyBookingsResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * BookingRepository - API wrapper for tenant booking operations.
 */
public class BookingRepository {

    private final ApiService apiService;

    public BookingRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public interface BookingDetailCallback {
        void onSuccess(MyBookingsResponse.MyBookingDto booking);
        void onNotFound();
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    /**
     * Fallback implementation: backend may not have /api/datphong/{id}.
     * We call /api/datphong/my-bookings then filter client-side.
     */
    public void getBookingDetailById(String bookingId, BookingDetailCallback callback) {
        apiService.getMyBookingsTyped().enqueue(new Callback<MyBookingsResponse>() {
            @Override
            public void onResponse(Call<MyBookingsResponse> call, Response<MyBookingsResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    callback.onError("Failed to load booking: " + response.code());
                    return;
                }
                MyBookingsResponse body = response.body();
                if (!body.success) {
                    callback.onError(body.message != null ? body.message : "Failed to load booking");
                    return;
                }
                List<MyBookingsResponse.MyBookingDto> data = body.data != null ? body.data : new ArrayList<>();
                for (MyBookingsResponse.MyBookingDto dto : data) {
                    if (dto != null && bookingId != null && bookingId.equals(dto.datPhongId)) {
                        callback.onSuccess(dto);
                        return;
                    }
                }
                callback.onNotFound();
            }

            @Override
            public void onFailure(Call<MyBookingsResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Cancel booking by updating status to 4 (cancelled).
     * Requires backend support for PUT /api/datphong/{id}/status
     */
    public void cancelBooking(String bookingId, SimpleCallback callback) {
        apiService.updateBookingStatus(bookingId, new UpdateBookingStatusRequest(4)).enqueue(new Callback<GenericResponse<Object>>() {
            @Override
            public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse<Object> body = response.body();
                    if (body.success) {
                        callback.onSuccess();
                    } else {
                        callback.onError(body.message != null ? body.message : "Cancel failed");
                    }
                    return;
                }
                callback.onError("Cancel failed: " + response.code());
            }

            @Override
            public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}

