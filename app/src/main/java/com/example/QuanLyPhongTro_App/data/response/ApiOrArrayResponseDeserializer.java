package com.example.QuanLyPhongTro_App.data.response;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Gson deserializer for ApiOrArrayResponse<T>.
 *
 * It can read either:
 *  - JSON array: [ ... ]
 *  - Wrapped object: { success, Data: [ ... ], Message }
 */
public class ApiOrArrayResponseDeserializer implements JsonDeserializer<ApiOrArrayResponse<?>> {

    @Override
    public ApiOrArrayResponse<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        if (!(typeOfT instanceof ParameterizedType)) {
            throw new JsonParseException("ApiOrArrayResponse must be parameterized");
        }

        // ApiOrArrayResponse<Dto>
        Type dtoType = ((ParameterizedType) typeOfT).getActualTypeArguments()[0];
        Type listType = com.google.gson.reflect.TypeToken.getParameterized(java.util.List.class, dtoType).getType();

        if (json == null || json.isJsonNull()) {
            return ApiOrArrayResponse.failure("Empty response");
        }

        try {
            if (json.isJsonArray()) {
                java.util.List<?> list = context.deserialize(json, listType);
                //noinspection unchecked
                return ApiOrArrayResponse.success((java.util.List) list);
            }

            if (json.isJsonObject()) {
                GenericResponse<?> gr = context.deserialize(
                        json,
                        com.google.gson.reflect.TypeToken.getParameterized(GenericResponse.class, listType).getType()
                );

                ApiOrArrayResponse<Object> r = new ApiOrArrayResponse<>();
                r.success = ((GenericResponse<?>) gr).success;
                //noinspection unchecked
                r.data = (java.util.List<Object>) ((GenericResponse<?>) gr).data;
                r.message = ((GenericResponse<?>) gr).message;
                return r;
            }
        } catch (RuntimeException ex) {
            throw new JsonParseException(ex);
        }

        throw new JsonParseException("Expected JSON array or object");
    }
}
