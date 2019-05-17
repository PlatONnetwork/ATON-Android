package com.juzix.wallet.engine;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class BaseUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        return chain.proceed(chain.request().newBuilder()
                .url(NodeManager.getInstance().getCurNode().getHttpUrl())
                .build());
    }
}
