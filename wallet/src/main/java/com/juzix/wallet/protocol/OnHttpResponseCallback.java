package com.juzix.wallet.protocol;


import com.juzix.wallet.protocol.base.IHttpContext;

/**
 * 请求网络后回调接口
 *
 * @param <T>获取数据类型
 */
public interface OnHttpResponseCallback<T> extends IHttpContext {

    void onStart();

    void onSuccess(T t);

    void onFail(int code, String msg);

    void onFinish();
}
