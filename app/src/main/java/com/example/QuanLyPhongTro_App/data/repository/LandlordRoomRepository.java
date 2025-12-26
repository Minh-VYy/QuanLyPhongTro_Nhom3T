package com.example.QuanLyPhongTro_App.data.repository;

import android.util.Log;

import com.example.QuanLyPhongTro_App.data.request.LandlordUpsertRoomRequest;
import com.example.QuanLyPhongTro_App.utils.ApiClient;
import com.example.QuanLyPhongTro_App.utils.ApiService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * LandlordRoomRepository - landlord CRUD rooms through API.
 * Notes:
 * - Endpoints might need to match your backend. If backend routes differ, adjust in ApiService.
 */
public class LandlordRoomRepository {
    private static final String TAG = "LandlordRoomRepo";

    private final ApiService apiService;

    public LandlordRoomRepository() {
        this.apiService = ApiClient.getRetrofit().create(ApiService.class);
    }

    public interface ListRoomsCallback {
        void onSuccess(List<RoomRepository.RoomDto> rooms);
        void onError(String message);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onError(String message);
    }

    private static long lastFetchAtMs = 0;

    /**
     * Landlord flow (Swagger):
     * 1) GET /api/NhaTro/my-houses
     * 2) For each houseId (NhaTroId), GET /api/Phong?nhaTroId=...
     */
    public void getMyRooms(String ignoredChuTroId, ListRoomsCallback callback) {
        long now = System.currentTimeMillis();
        if (now - lastFetchAtMs < 800) {
            Log.d(TAG, "getMyRooms debounced (called too quickly)");
            // still proceed to keep UI consistent, but avoid spamming network would require caching.
        }
        lastFetchAtMs = now;

        // Step 1: load houses for current landlord
        apiService.getMyHouses().enqueue(new Callback<List<Object>>() {
            @Override
            public void onResponse(Call<List<Object>> call, Response<List<Object>> response) {
                if (!response.isSuccessful()) {
                    callback.onError("Failed to load landlord houses: " + response.code());
                    return;
                }

                List<Object> housesRaw = response.body();
                if (housesRaw == null) {
                    Log.w(TAG, "getMyHouses() returned 200 but body is null. Backend may wrap response (items/data). Update ApiService or add tolerant mapping.");
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                Log.d(TAG, "Loaded my-houses: count=" + housesRaw.size());

                // Extract NhaTroId from each item
                List<String> houseIds = extractIdsFromUnknownList(housesRaw,
                        new String[]{"NhaTroId", "nhaTroId", "NhaTroID", "nhaTroID", "Id", "id"});

                Log.d(TAG, "Extracted houseIds count=" + houseIds.size() + " ids=" + houseIds);

                if (houseIds.isEmpty()) {
                    callback.onSuccess(new ArrayList<>());
                    return;
                }

                // Step 2: load rooms for each house and merge results
                List<Object> combinedRooms = new ArrayList<>();
                final int[] remaining = {houseIds.size()};

                for (String nhaTroId : houseIds) {
                    apiService.getRoomsByHouseWrapped(nhaTroId, 1, 200).enqueue(new Callback<com.example.QuanLyPhongTro_App.data.response.GenericResponse<List<Object>>>() {
                        @Override
                        public void onResponse(Call<com.example.QuanLyPhongTro_App.data.response.GenericResponse<List<Object>>> call2,
                                               Response<com.example.QuanLyPhongTro_App.data.response.GenericResponse<List<Object>>> response2) {
                            synchronized (remaining) {
                                if (response2.isSuccessful()) {
                                    List<Object> roomsRaw = null;
                                    if (response2.body() != null) {
                                        roomsRaw = response2.body().data;
                                    }
                                    int count = roomsRaw == null ? 0 : roomsRaw.size();
                                    Log.d(TAG, "Loaded rooms for nhaTroId=" + nhaTroId + ": count=" + count);
                                    if (roomsRaw != null) combinedRooms.addAll(roomsRaw);
                                } else {
                                    String err = "";
                                    try (okhttp3.ResponseBody rb = response2.errorBody()) {
                                        if (rb != null) err = rb.string();
                                    } catch (Exception ignore) {}
                                    Log.w(TAG, "Failed to load rooms for nhaTroId=" + nhaTroId + " code=" + response2.code() + " body=" + err);
                                }

                                remaining[0]--;
                                if (remaining[0] == 0) {
                                    Log.d(TAG, "Total combined rooms=" + combinedRooms.size());
                                    callback.onSuccess(convertToRoomDtos(combinedRooms));
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<com.example.QuanLyPhongTro_App.data.response.GenericResponse<List<Object>>> call2, Throwable t2) {
                            synchronized (remaining) {
                                Log.w(TAG, "Network error loading rooms for nhaTroId=" + nhaTroId + ": " + t2.getMessage());
                                remaining[0]--;
                                if (remaining[0] == 0) {
                                    Log.d(TAG, "Total combined rooms=" + combinedRooms.size());
                                    callback.onSuccess(convertToRoomDtos(combinedRooms));
                                }
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<List<Object>> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createRoom(LandlordUpsertRoomRequest request, SimpleCallback callback) {
        apiService.createPhong(request).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    String err = "";
                    try (okhttp3.ResponseBody rb = response.errorBody()) {
                        if (rb != null) err = rb.string();
                    } catch (Exception ignore) {}
                    callback.onError("Create failed: " + response.code() + (err.isEmpty() ? "" : (" - " + err)));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void updateRoom(String phongId, LandlordUpsertRoomRequest request, SimpleCallback callback) {
        apiService.updatePhong(phongId, request).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    String err = "";
                    try (okhttp3.ResponseBody rb = response.errorBody()) {
                        if (rb != null) err = rb.string();
                    } catch (Exception ignore) {}
                    callback.onError("Update failed: " + response.code() + (err.isEmpty() ? "" : (" - " + err)));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void deleteRoom(String phongId, String ignoredChuTroId, SimpleCallback callback) {
        // Backend doesn't allow DELETE on /api/Phong/{id}. Use lock endpoint instead.
        apiService.lockPhong(phongId, true).enqueue(new Callback<Object>() {
             @Override
             public void onResponse(Call<Object> call, Response<Object> response) {
                 if (response.isSuccessful()) {
                     callback.onSuccess();
                 } else {
                     String err = "";
                     try (okhttp3.ResponseBody rb = response.errorBody()) {
                         if (rb != null) err = rb.string();
                     } catch (Exception ignore) {}
                     callback.onError("Delete (lock) failed: " + response.code() + (err.isEmpty() ? "" : (" - " + err)));
                 }
             }

             @Override
             public void onFailure(Call<Object> call, Throwable t) {
                 callback.onError("Network error: " + t.getMessage());
             }
         });
     }

    public void toggleActive(String phongId, boolean isActive, String ignoredChuTroId, SimpleCallback callback) {
        // Map toggle to lock/unlock endpoint.
        // isActive=true => unlock (isLocked=false)
        // isActive=false => lock (isLocked=true)
        apiService.lockPhong(phongId, !isActive).enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    String err = "";
                    try (okhttp3.ResponseBody rb = response.errorBody()) {
                        if (rb != null) err = rb.string();
                    } catch (Exception ignore) {}
                    callback.onError("Toggle (lock) failed: " + response.code() + (err.isEmpty() ? "" : (" - " + err)));
                }
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
     }

    private List<String> extractIdsFromUnknownList(List<Object> raw, String[] keys) {
        List<String> out = new ArrayList<>();
        Gson gson = new Gson();
        for (Object item : raw) {
            try {
                Map<String, Object> map = gson.fromJson(gson.toJson(item), Map.class);
                for (String k : keys) {
                    Object v = map.get(k);
                    if (v != null) {
                        String s = v.toString();
                        if (!s.trim().isEmpty()) {
                            out.add(s);
                            break;
                        }
                    }
                }
            } catch (Exception ignore) {}
        }
        return out;
    }

    private List<RoomRepository.RoomDto> convertToRoomDtos(List<Object> roomsData) {
        List<RoomRepository.RoomDto> rooms = new ArrayList<>();
        Gson gson = new Gson();

        for (Object obj : roomsData) {
            try {
                Map<String, Object> map = gson.fromJson(gson.toJson(obj), Map.class);
                RoomRepository.RoomDto dto = new RoomRepository.RoomDto();

                Object phongId = map.get("PhongId");
                if (phongId == null) phongId = map.get("phongId");
                if (phongId == null) phongId = map.get("Id");
                if (phongId == null) phongId = map.get("id");
                if (phongId != null) dto.setPhongId(phongId.toString());

                Object tieuDe = map.get("TieuDe");
                if (tieuDe == null) tieuDe = map.get("tieuDe");
                if (tieuDe == null) tieuDe = map.get("TenPhong");
                if (tieuDe == null) tieuDe = map.get("tenPhong");
                if (tieuDe != null) dto.setTieuDe(tieuDe.toString());

                Object moTa = map.get("MoTa");
                if (moTa == null) moTa = map.get("moTa");
                if (moTa != null) dto.setMoTa(moTa.toString());

                Object giaTien = map.get("GiaTien");
                if (giaTien == null) giaTien = map.get("giaTien");
                if (giaTien != null) {
                    try {
                        dto.setGiaTien(Long.parseLong(giaTien.toString().replace(".0", "")));
                    } catch (Exception ignore) {
                    }
                }

                // Optional status/flags
                Object trangThai = map.get("TrangThai");
                if (trangThai == null) trangThai = map.get("trangThai");
                if (trangThai != null) dto.setTrangThai(trangThai.toString());

                rooms.add(dto);
            } catch (Exception e) {
                Log.e(TAG, "Convert error: " + e.getMessage());
            }
        }
        return rooms;
    }
}
