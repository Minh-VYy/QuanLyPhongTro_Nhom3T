package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/**
 * A tolerant wrapper to support inconsistent backend responses that sometimes return:
 *  - a wrapped object: { success: true, Data: [ ... ], Message: ... }
 *  - OR a plain array: [ ... ]
 *
 * Use with a custom Gson deserializer (see ApiOrArrayResponseDeserializer).
 */
public class ApiOrArrayResponse<T> {

    @SerializedName("success")
    public boolean success;

    @SerializedName("Data")
    public List<T> data;

    @SerializedName(value = "Message", alternate = {"message"})
    public String message;

    public static <T> ApiOrArrayResponse<T> success(List<T> data) {
        ApiOrArrayResponse<T> r = new ApiOrArrayResponse<>();
        r.success = true;
        r.data = data;
        r.message = null;
        return r;
    }

    public static <T> ApiOrArrayResponse<T> failure(String message) {
        ApiOrArrayResponse<T> r = new ApiOrArrayResponse<>();
        r.success = false;
        r.data = Collections.emptyList();
        r.message = message;
        return r;
    }

    // NOTE: No extra helpers required; deserializer handles array vs object.
}
