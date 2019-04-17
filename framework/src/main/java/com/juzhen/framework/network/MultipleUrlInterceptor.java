package com.juzhen.framework.network;


import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author matrixelement
 */
public class MultipleUrlInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request origin = chain.request();

        List<String> headerValues = origin.headers("name");

        Request request;

        if (headerValues != null && !headerValues.isEmpty()) {

            HttpUrl newBaseUrl;

            String headerValue = headerValues.get(0);

            if (RequestInfo.URL_IP.equals(headerValue)) {
                newBaseUrl = HttpUrl.parse(RequestInfo.getUrl(RequestInfo.URL_IP));
            } else {
                newBaseUrl = HttpUrl.parse(RequestInfo.getUrl(RequestInfo.DEFAULT_URL_KEY));
            }
            request = origin.newBuilder()
                    .url(newBaseUrl)
                    .method(origin.method(), origin.body())
                    .build();
        } else {
            request = origin.newBuilder()
                    .url(origin.url())
                    .method(origin.method(), origin.body())
                    .build();
        }

        return chain.proceed(request);
    }
}
