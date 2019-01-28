package com.juzix.wallet.protocol;

import com.juzix.wallet.protocol.base.IHttpContext;
import com.juzix.wallet.protocol.resp.BaseResp;

/**
 * 请求网络后回调接口
 *
 * @param <T>获取数据类型
 */
public interface OnHttpProviderCallback<T extends BaseResp> extends IHttpContext {

    void onSuccess(int what, T t);

    void onFail(int what, RespException exception);

}
