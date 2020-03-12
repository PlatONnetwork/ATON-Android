package com.platon.framework.network;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

/**
 * @author ziv
 */
public final class ApiRequestBody extends RequestBody {

    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final RequestBody delegate;
    private final Map<String, Object> bodyMap;
    // 未加密的ApiRequestBody对象
    private ApiRequestBody rawBody;

    public static ApiRequestBody create(Map<String, Object> bodyMap) {
        return new ApiRequestBody(bodyMap);
    }

    public static ApiRequestBody create(Map<String, Object> bodyMap, ApiRequestBody rawRequestBody) {
        ApiRequestBody apiRequestBody = new ApiRequestBody(bodyMap);
        apiRequestBody.rawBody = rawRequestBody;
        return apiRequestBody;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {
        private Map<String, Object> map;

        private Builder() {
            map = new HashMap<>();
        }

        public Builder put(String key, Object value) {
            map.put(key, value);
            return this;
        }

        public ApiRequestBody build() {
            return ApiRequestBody.create(map);
        }
    }

    private ApiRequestBody(Map<String, Object> bodyMap) {
        this.bodyMap = bodyMap;
        Gson gson = new Gson();
        delegate = RequestBody.create(MEDIA_TYPE_JSON, gson.toJson(bodyMap));
    }

    @Nullable
    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        delegate.writeTo(sink);
    }

    public Map<String, Object> getBodyMap() {
        return bodyMap;
    }

    public ApiRequestBody getRawRequestBody() {
        return rawBody;
    }
}
