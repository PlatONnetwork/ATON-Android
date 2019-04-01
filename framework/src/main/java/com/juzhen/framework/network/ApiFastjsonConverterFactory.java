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


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.support.retrofit.Retrofit2ConverterFactory;
import com.juzhen.framework.app.log.Log;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;


/**
 * @author ziv
 */
public final class ApiFastjsonConverterFactory extends Retrofit2ConverterFactory {

    private static final String TAG = "ApiService/Logging/con";

    private static final Feature[] EMPTY_SERIALIZER_FEATURES = new Feature[0];

    public static ApiFastjsonConverterFactory create() {
        return new ApiFastjsonConverterFactory();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, //
                                                            Annotation[] annotations, //
                                                            Retrofit retrofit) {
        if (getRawType(type) == ApiResponse.class) {
            RequestInfo requestInfo = getRequestInfoByAnnotations(annotations);
            return new ResponseBodyConverter<ResponseBody>(type, requestInfo);
        } else {
            return super.responseBodyConverter(type, annotations, retrofit);
        }
    }

    private final class ResponseBodyConverter<T> implements Converter<ResponseBody, T> {
        private Type type;
        private RequestInfo requestInfo;

        ResponseBodyConverter(Type type, RequestInfo requestInfo) {
            this.type = type;
            this.requestInfo = requestInfo;
        }

        @Override
        public T convert(ResponseBody value) throws IOException {
            RequestInfo.EncryptionType encryptType = requestInfo.getEncryptType();
            // 需要反序列化的内容
            String content;
            if (encryptType == RequestInfo.EncryptionType.TYPE_NONE) {
                content = value.string();
            } else {
                content = EncryptionManager.getInstance().decrypt(requestInfo, value.string());
                Log.debug(TAG, requestInfo.getRealUrl() + "\nRAW: " + content);
            }
            try {
                Feature[] features = getParserFeatures();
                T t = JSON.parseObject(content
                        , type
                        , getParserConfig()
                        , getParserFeatureValues()
                        , features != null
                                ? features
                                : EMPTY_SERIALIZER_FEATURES
                );
                Log.debug(TAG, t.toString());
                return t;
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                value.close();
            }
            return null;
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
