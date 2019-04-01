package com.juzhen.framework.network;

import java.io.IOException;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * 加密拦截器，负责对请求的加密
 *
 * @author ziv
 */
public class EncryptInterceptor implements Interceptor {

    private static final String TAG = "ApiService/EncryptInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request origin = chain.request();
        RequestInfo reqInfo = RequestInfo.create(origin.url().toString());
        RequestBody encryptedBody = encryptBody(reqInfo, origin.body());
        Request request = origin.newBuilder()
                .url(reqInfo.getRealUrl())
                .method(origin.method(), encryptedBody)
                .build();
        //Log.debug(TAG, "intercept: " + origin.url() + " ==> " + request.url() + ", body= " + origin.body());
        return chain.proceed(request);
    }

    private RequestBody encryptBody(RequestInfo reqInfo, RequestBody body) {
        if (!(body instanceof ApiRequestBody)) {
            return body;
        }
        ApiRequestBody requestBody = (ApiRequestBody) body;
        Map<String, Object> encryptedBodyMap = EncryptionManager.getInstance().encrypt(reqInfo, requestBody.getBodyMap());
        //Log.debug(TAG, "encryptBody" + new Gson().toJson(encryptedBodyMap));
        return ApiRequestBody.create(encryptedBodyMap, requestBody);
    }
}
