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
package com.platon.framework.network;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.POST;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;

/**
 * @author ziv
 */
public final class ApiGsonConverterFactory extends Converter.Factory {

    private GsonConverterFactory mGsonConverterFactory;
    private Gson mGson;

    public static ApiGsonConverterFactory create() {
        return new ApiGsonConverterFactory(GsonConverterFactory.create());
    }

    private ApiGsonConverterFactory(GsonConverterFactory gsonConverterFactory) {
        mGson = new Gson();
        mGsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type,
            Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return mGsonConverterFactory.requestBodyConverter(type,
                parameterAnnotations, methodAnnotations, retrofit);
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        if (getRawType(type) == ApiResponse.class) {
            RequestInfo requestInfo = getRequestInfoByAnnotations(annotations);
            TypeAdapter<?> adapter = mGson.getAdapter(TypeToken.get(type));
            return new ApiGsonResponseBodyConverter<>(mGson, adapter, requestInfo);
        } else {
            return mGsonConverterFactory.responseBodyConverter(type, annotations, retrofit);
        }
    }

    private RequestInfo getRequestInfoByAnnotations(Annotation[] annotations) {
        String annotatedValue;
        for (Annotation annotation : annotations) {
            Object clazz = annotation.annotationType();
            if (clazz == POST.class) {
                annotatedValue = ((POST) annotation).value();
            } else if (clazz == GET.class) {
                annotatedValue = ((GET) annotation).value();
            } else if (clazz == PUT.class) {
                annotatedValue = ((PUT) annotation).value();
            } else if (clazz == DELETE.class) {
                annotatedValue = ((DELETE) annotation).value();
            } else {
                continue;
            }
            if (annotatedValue != null) {
                return RequestInfo.create(annotatedValue);
            }
        }
        return null;
    }

}
