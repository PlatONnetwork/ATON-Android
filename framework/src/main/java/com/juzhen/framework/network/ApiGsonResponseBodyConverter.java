/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.juzhen.framework.network;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.juzhen.framework.app.log.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
    对返回进行解密
 */
final class ApiGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    private static final String TAG = "ApiService/Logging/con";
    private final Gson gson;
    private final TypeAdapter<T> adapter;
    private final RequestInfo requestInfo;

    ApiGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter, RequestInfo requestInfo) {
        this.gson = gson;
        this.adapter = adapter;
        this.requestInfo = requestInfo;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        RequestInfo.EncryptionType encryptType = requestInfo.getEncryptType();
        if(encryptType == RequestInfo.EncryptionType.TYPE_NONE) {
            JsonReader jsonReader = gson.newJsonReader(value.charStream());
            try {
                return adapter.read(jsonReader);
            } finally {
                value.close();
            }
        } else {
            String jsonString = EncryptionManager.getInstance().decrypt(requestInfo, value.string());
            Log.debug(TAG, requestInfo.getRealUrl() + "\nRAW: " + jsonString);
            return adapter.fromJson(jsonString);
        }
    }
}
