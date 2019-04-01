package com.juzhen.framework.network;

public interface ApiCallback<T> {

    /**
     * errorCode = 0,即表示成功
     */
    void onSuccess(T result);

    /**
     * errorCode != 0, 或http请求不成功
     */
    void onFailure(ApiResponse errorResponse);
}
