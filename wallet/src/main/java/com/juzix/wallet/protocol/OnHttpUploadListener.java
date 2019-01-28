package com.juzix.wallet.protocol;


import com.juzix.wallet.protocol.base.IHttpContext;

/**
 * 文件上传监听器
 */
public interface OnHttpUploadListener extends IHttpContext {
    void onProgress(int what, int progress);

    void onSuccess(int what);

    void onCancel(int what);

    void onFail(int what, RespException exception);
}
