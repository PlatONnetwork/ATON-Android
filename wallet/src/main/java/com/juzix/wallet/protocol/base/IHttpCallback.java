package com.juzix.wallet.protocol.base;


import com.juzix.wallet.protocol.RespException;

public interface IHttpCallback<T> extends IHttpContext {
    void onSuccess(int what, T resp);

    void onFail(int what, RespException exp);
}
