package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Same as ApiOrArrayResponseDeserializer but understands that some backends use lowercase
 * fields (data/message) instead of PascalCase (Data/Message).
 */
public class ApiOrArrayResponseStringDeserializer implements JsonDeserializer<ApiOrArrayResponse<?>> {

    @Override
    public ApiOrArrayResponse<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (!(typeOfT instanceof ParameterizedType)) {
            throw new JsonParseException("ApiOrArrayResponse must be parameterized");
        }

        Type dtoType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        Type listType = com.google.gson.reflect.TypeToken.getParameterized(java.util.List.class, dtoType).getType();

        if (json == null || json.isJsonNull()) {
            return ApiOrArrayResponse.failure("Empty response");
        }

        if (json.isJsonArray()) {
            java.util.List<?> list = context.deserialize(json, listType);
            //noinspection unchecked
            return ApiOrArrayResponse.success((java.util.List) list);
        }

        if (!json.isJsonObject()) {
            throw new JsonParseException("Expected JSON array or object");
        }

        // Read as object and map fields manually for maximum tolerance.
        com.google.gson.JsonObject obj = json.getAsJsonObject();

        boolean success = obj.has("success") && obj.get("success").getAsBoolean();

        JsonElement dataEl = null;
        if (obj.has("Data")) dataEl = obj.get("Data");
        else if (obj.has("data")) dataEl = obj.get("data");

        java.util.List<?> list = dataEl != null && dataEl.isJsonArray()
                ? context.deserialize(dataEl, listType)
                : java.util.Collections.emptyList();

        String message = null;
        if (obj.has("Message")) message = obj.get("Message").isJsonNull() ? null : obj.get("Message").getAsString();
        else if (obj.has("message")) message = obj.get("message").isJsonNull() ? null : obj.get("message").getAsString();

        ApiOrArrayResponse<Object> r = new ApiOrArrayResponse<>();
        r.success = success;
        //noinspection unchecked
        r.data = (java.util.List<Object>) list;
        r.message = message;
        return r;
    }
}

