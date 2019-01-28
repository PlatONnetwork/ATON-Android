package com.juzix.wallet.protocol.base;

import com.juzix.wallet.protocol.OnHttpDownloadListener;
import com.juzix.wallet.protocol.OnHttpUploadListener;

public interface IHttpLoader<T> {

    void attachLoader(
            int what,
            IHttpProvider provider,
            IHttpRequest request,
            IHttpResponse response,
            IHttpCallback<T> listener);

    void load();

    void setOnHttpDownloadListener(OnHttpDownloadListener listener);

    void setOnHttpUploadListener(OnHttpUploadListener listener);
}
