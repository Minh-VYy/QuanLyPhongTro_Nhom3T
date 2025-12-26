package com.example.QuanLyPhongTro_App.data.repository;

import com.example.QuanLyPhongTro_App.data.request.UpdateBookingStatusByLandlordRequest;
import com.example.QuanLyPhongTro_App.data.response.GenericResponse;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LandlordBookingRepository - landlord view of booking requests.
 * Uses API; no direct DB.
 */
public class LandlordBookingRepository {

    private final ApiService apiService;

    public LandlordBookingRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public interface ListCallback {
        void onSuccess(List<Map<String, Object>> rawItems);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    public void getBookingRequests(String chuTroId, ListCallback callback) {
        // Swagger primary: no chuTroId param; server uses JWT to detect landlord.
        apiService.getLandlordBookingRequestsV2().enqueue(new Callback<GenericResponse<List<Object>>>() {
            @Override
            public void onResponse(Call<GenericResponse<List<Object>>> call, Response<GenericResponse<List<Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    GenericResponse<List<Object>> body = response.body();
                    if (!body.success) {
                        callback.onError(body.message != null ? body.message : "Failed to load requests");
                        return;
                    }

                    List<Object> data = body.data != null ? body.data : new ArrayList<>();
                    List<Map<String, Object>> out = new ArrayList<>();
                    for (Object o : data) {
                        if (o instanceof Map) {
                            //noinspection unchecked
                            out.add((Map<String, Object>) o);
                        }
                    }
                    callback.onSuccess(out);
                    return;
                }

                // If backend returns a raw JSON array (unwrapped), Retrofit will fail to parse into GenericResponse.
                // In that case response.body() is null but onResponse is still called.
                // Try raw-array endpoint.
                apiService.getLandlordBookingRequestsV2Raw().enqueue(new Callback<List<Object>>() {
                    @Override
                    public void onResponse(Call<List<Object>> callRaw, Response<List<Object>> responseRaw) {
                        if (responseRaw.isSuccessful() && responseRaw.body() != null) {
                            List<Object> dataRaw = responseRaw.body();
                            List<Map<String, Object>> outRaw = new ArrayList<>();
                            for (Object o : dataRaw) {
                                if (o instanceof Map) {
                                    //noinspection unchecked
                                    outRaw.add((Map<String, Object>) o);
                                }
                            }
                            callback.onSuccess(outRaw);
                            return;
                        }
                        // Optional fallback to legacy endpoints (older backend builds)
                        apiService.getLandlordBookingRequests(chuTroId).enqueue(new Callback<GenericResponse<List<Object>>>() {
                            @Override
                            public void onResponse(Call<GenericResponse<List<Object>>> call2, Response<GenericResponse<List<Object>>> response2) {
                                if (!response2.isSuccessful() || response2.body() == null) {
                                    callback.onError("Failed to load requests: " + response2.code());
                                    return;
                                }

                                GenericResponse<List<Object>> body2 = response2.body();
                                if (!body2.success) {
                                    callback.onError(body2.message != null ? body2.message : "Failed to load requests");
                                    return;
                                }

                                List<Object> data2 = body2.data != null ? body2.data : new ArrayList<>();
                                List<Map<String, Object>> out2 = new ArrayList<>();
                                for (Object o : data2) {
                                    if (o instanceof Map) {
                                        //noinspection unchecked
                                        out2.add((Map<String, Object>) o);
                                    }
                                }
                                callback.onSuccess(out2);
                            }

                            @Override
                            public void onFailure(Call<GenericResponse<List<Object>>> call2, Throwable t2) {
                                callback.onError("Network error: " + t2.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Object>> callRaw, Throwable tRaw) {
                        // Optional fallback
                        apiService.getLandlordBookingRequests(chuTroId).enqueue(new Callback<GenericResponse<List<Object>>>() {
                            @Override
                            public void onResponse(Call<GenericResponse<List<Object>>> call2, Response<GenericResponse<List<Object>>> response2) {
                                if (!response2.isSuccessful() || response2.body() == null) {
                                    callback.onError("Failed to load requests: " + response2.code());
                                    return;
                                }

                                GenericResponse<List<Object>> body2 = response2.body();
                                if (!body2.success) {
                                    callback.onError(body2.message != null ? body2.message : "Failed to load requests");
                                    return;
                                }

                                List<Object> data2 = body2.data != null ? body2.data : new ArrayList<>();
                                List<Map<String, Object>> out2 = new ArrayList<>();
                                for (Object o : data2) {
                                    if (o instanceof Map) {
                                        //noinspection unchecked
                                        out2.add((Map<String, Object>) o);
                                    }
                                }
                                callback.onSuccess(out2);
                            }

                            @Override
                            public void onFailure(Call<GenericResponse<List<Object>>> call2, Throwable t2) {
                                callback.onError("Network error: " + tRaw.getMessage());
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(Call<GenericResponse<List<Object>>> call, Throwable t) {
                // Primary wrapped call failed (likely JsonSyntaxException due to raw array). Try raw first.
                apiService.getLandlordBookingRequestsV2Raw().enqueue(new Callback<List<Object>>() {
                    @Override
                    public void onResponse(Call<List<Object>> callRaw, Response<List<Object>> responseRaw) {
                        if (responseRaw.isSuccessful() && responseRaw.body() != null) {
                            List<Object> dataRaw = responseRaw.body();
                            List<Map<String, Object>> outRaw = new ArrayList<>();
                            for (Object o : dataRaw) {
                                if (o instanceof Map) {
                                    //noinspection unchecked
                                    outRaw.add((Map<String, Object>) o);
                                }
                            }
                            callback.onSuccess(outRaw);
                            return;
                        }
                        // Fallback legacy
                        apiService.getLandlordBookingRequests(chuTroId).enqueue(new Callback<GenericResponse<List<Object>>>() {
                            @Override
                            public void onResponse(Call<GenericResponse<List<Object>>> call2, Response<GenericResponse<List<Object>>> response2) {
                                if (!response2.isSuccessful() || response2.body() == null) {
                                    callback.onError("Failed to load requests: " + response2.code());
                                    return;
                                }
                                GenericResponse<List<Object>> body2 = response2.body();
                                if (!body2.success) {
                                    callback.onError(body2.message != null ? body2.message : "Failed to load requests");
                                    return;
                                }
                                List<Object> data2 = body2.data != null ? body2.data : new ArrayList<>();
                                List<Map<String, Object>> out2 = new ArrayList<>();
                                for (Object o : data2) {
                                    if (o instanceof Map) {
                                        //noinspection unchecked
                                        out2.add((Map<String, Object>) o);
                                    }
                                }
                                callback.onSuccess(out2);
                            }

                            @Override
                            public void onFailure(Call<GenericResponse<List<Object>>> call2, Throwable t2) {
                                callback.onError("Network error: " + t.getMessage());
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<List<Object>> callRaw, Throwable tRaw) {
                        // Fallback legacy
                        apiService.getLandlordBookingRequests(chuTroId).enqueue(new Callback<GenericResponse<List<Object>>>() {
                            @Override
                            public void onResponse(Call<GenericResponse<List<Object>>> call2, Response<GenericResponse<List<Object>>> response2) {
                                if (!response2.isSuccessful() || response2.body() == null) {
                                    callback.onError("Failed to load requests: " + response2.code());
                                    return;
                                }
                                GenericResponse<List<Object>> body2 = response2.body();
                                if (!body2.success) {
                                    callback.onError(body2.message != null ? body2.message : "Failed to load requests");
                                    return;
                                }
                                List<Object> data2 = body2.data != null ? body2.data : new ArrayList<>();
                                List<Map<String, Object>> out2 = new ArrayList<>();
                                for (Object o : data2) {
                                    if (o instanceof Map) {
                                        //noinspection unchecked
                                        out2.add((Map<String, Object>) o);
                                    }
                                }
                                callback.onSuccess(out2);
                            }

                            @Override
                            public void onFailure(Call<GenericResponse<List<Object>>> call2, Throwable t2) {
                                callback.onError("Network error: " + tRaw.getMessage());
                            }
                        });
                    }
                });
            }
        });
    }

    public void updateBookingStatus(String bookingId, int trangThaiId, String chuTroId, SimpleCallback callback) {
        // Swagger primary: PUT /api/DatPhong/status/{id}?status=
        apiService.updateLandlordBookingStatusV2(bookingId, trangThaiId).enqueue(new Callback<GenericResponse<Object>>() {
            @Override
            public void onResponse(Call<GenericResponse<Object>> call, Response<GenericResponse<Object>> response) {
                // Many backend builds return 200 with empty body or plain text.
                // Treat any HTTP 2xx as success unless we have an explicit {success:false}.
                if (response.isSuccessful()) {
                    GenericResponse<Object> body = response.body();
                    if (body == null) {
                        callback.onSuccess();
                        return;
                    }
                    if (body.success) {
                        callback.onSuccess();
                    } else {
                        callback.onError(body.message != null ? body.message : "Update failed");
                    }
                    return;
                }

                // Not successful -> try raw endpoint to capture servers that don't wrap responses
                apiService.updateLandlordBookingStatusV2Raw(bookingId, trangThaiId).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> callRaw, Response<Object> responseRaw) {
                        if (responseRaw.isSuccessful()) {
                            callback.onSuccess();
                            return;
                        }

                        // Fallback to legacy body-based endpoint (older backend builds)
                        apiService.updateLandlordBookingStatus(bookingId, new UpdateBookingStatusByLandlordRequest(trangThaiId, chuTroId))
                                .enqueue(new Callback<GenericResponse<Object>>() {
                                    @Override
                                    public void onResponse(Call<GenericResponse<Object>> call2, Response<GenericResponse<Object>> response2) {
                                        if (response2.isSuccessful()) {
                                            GenericResponse<Object> body2 = response2.body();
                                            if (body2 == null || body2.success) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onError(body2.message != null ? body2.message : "Update failed");
                                            }
                                            return;
                                        }

                                        String msg = "Update failed: " + response2.code();
                                        try {
                                            if (response2.errorBody() != null) {
                                                msg += " " + response2.errorBody().string();
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        callback.onError(msg);
                                    }

                                    @Override
                                    public void onFailure(Call<GenericResponse<Object>> call2, Throwable t2) {
                                        callback.onError("Network error: " + t2.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Object> callRaw, Throwable tRaw) {
                        // Fallback to legacy
                        apiService.updateLandlordBookingStatus(bookingId, new UpdateBookingStatusByLandlordRequest(trangThaiId, chuTroId))
                                .enqueue(new Callback<GenericResponse<Object>>() {
                                    @Override
                                    public void onResponse(Call<GenericResponse<Object>> call2, Response<GenericResponse<Object>> response2) {
                                        if (response2.isSuccessful()) {
                                            GenericResponse<Object> body2 = response2.body();
                                            if (body2 == null || body2.success) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onError(body2.message != null ? body2.message : "Update failed");
                                            }
                                            return;
                                        }

                                        String msg = "Update failed: " + response2.code();
                                        try {
                                            if (response2.errorBody() != null) {
                                                msg += " " + response2.errorBody().string();
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        callback.onError(msg);
                                    }

                                    @Override
                                    public void onFailure(Call<GenericResponse<Object>> call2, Throwable t2) {
                                        callback.onError("Network error: " + tRaw.getMessage());
                                    }
                                });
                    }
                });
            }

            @Override
            public void onFailure(Call<GenericResponse<Object>> call, Throwable t) {
                // Try raw version first
                apiService.updateLandlordBookingStatusV2Raw(bookingId, trangThaiId).enqueue(new Callback<Object>() {
                    @Override
                    public void onResponse(Call<Object> callRaw, Response<Object> responseRaw) {
                        if (responseRaw.isSuccessful()) {
                            callback.onSuccess();
                            return;
                        }
                        // Fallback to legacy endpoint
                        apiService.updateLandlordBookingStatus(bookingId, new UpdateBookingStatusByLandlordRequest(trangThaiId, chuTroId))
                                .enqueue(new Callback<GenericResponse<Object>>() {
                                    @Override
                                    public void onResponse(Call<GenericResponse<Object>> call2, Response<GenericResponse<Object>> response2) {
                                        if (response2.isSuccessful()) {
                                            GenericResponse<Object> body2 = response2.body();
                                            if (body2 == null || body2.success) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onError(body2.message != null ? body2.message : "Update failed");
                                            }
                                            return;
                                        }

                                        String msg = "Update failed: " + response2.code();
                                        try {
                                            if (response2.errorBody() != null) {
                                                msg += " " + response2.errorBody().string();
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        callback.onError(msg);
                                    }

                                    @Override
                                    public void onFailure(Call<GenericResponse<Object>> call2, Throwable t2) {
                                        callback.onError("Network error: " + t.getMessage());
                                    }
                                });
                    }

                    @Override
                    public void onFailure(Call<Object> callRaw, Throwable tRaw) {
                        // Fallback to legacy endpoint
                        apiService.updateLandlordBookingStatus(bookingId, new UpdateBookingStatusByLandlordRequest(trangThaiId, chuTroId))
                                .enqueue(new Callback<GenericResponse<Object>>() {
                                    @Override
                                    public void onResponse(Call<GenericResponse<Object>> call2, Response<GenericResponse<Object>> response2) {
                                        if (response2.isSuccessful()) {
                                            GenericResponse<Object> body2 = response2.body();
                                            if (body2 == null || body2.success) {
                                                callback.onSuccess();
                                            } else {
                                                callback.onError(body2.message != null ? body2.message : "Update failed");
                                            }
                                            return;
                                        }

                                        String msg = "Update failed: " + response2.code();
                                        try {
                                            if (response2.errorBody() != null) {
                                                msg += " " + response2.errorBody().string();
                                            }
                                        } catch (Exception ignored) {
                                        }
                                        callback.onError(msg);
                                    }

                                    @Override
                                    public void onFailure(Call<GenericResponse<Object>> call2, Throwable t2) {
                                        callback.onError("Network error: " + tRaw.getMessage());
                                    }
                                });
                    }
                });
            }
        });
    }
}
