package com.juzix.wallet.protocol;


import com.juzix.wallet.protocol.base.IHttpContext;

/**
 * 文件下载监听器
 */
public interface OnHttpDownloadListener extends IHttpContext {
    void onProgress(int what, int progress, long fileCount, long speed);

    void onSuccess(int what, String filePath);

    void onCancel(int what);

    void onFail(int what, RespException exception);
}
