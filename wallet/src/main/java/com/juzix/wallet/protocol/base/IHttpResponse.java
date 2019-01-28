package com.juzix.wallet.protocol.base;

public interface IHttpResponse<T> {
    int JSON = 2101;
    int FILE = 2102;

    /**
     * 获取内容内型
     */
    int responseType();

    /**
     * 获取内容
     */
    T responseBody();
}
