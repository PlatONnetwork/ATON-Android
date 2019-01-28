package com.juzix.wallet.protocol.base;

public interface IHttpRequest<T> {

    int JSON   = 1201;
    int STRING = 1202;
    int FILE   = 1203;

    /**
     * 发送内容类型
     */
    int requestType();

    /**
     * 发送内容
     */
    T requestBody();
}
