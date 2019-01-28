package com.juzix.wallet.protocol.base;


import com.juzix.wallet.protocol.HttpConfigure;
import com.juzix.wallet.protocol.OnHttpProviderCallback;
import com.juzix.wallet.protocol.OnHttpUploadListener;
import com.juzix.wallet.protocol.resp.BaseResp;

import java.util.Map;

public abstract class BaseUploadProvider<T extends BaseResp> extends BaseHttpProvider<Map<String, String>, String, T> {

    private IHttpLoader mLoader;

    /**
     * 上传资源
     * @param callback
     */
    public BaseUploadProvider(OnHttpProviderCallback<T> callback) {
        super(callback);
        mLoader = HttpConfigure.getLoader();
    }

    protected void setOnHttpUploadListener(OnHttpUploadListener listener) {
        mLoader.setOnHttpUploadListener(listener);
    }

    @Override
    public IHttpLoader getHttpLoader() {
        return mLoader;
    }

    @Override
    public int requestMethod() {
        return POST;
    }

    @Override
    public int requestType() {
        return IHttpRequest.FILE;
    }

    @Override
    public int responseType() {
        return IHttpResponse.JSON;
    }

    @Override
    public Map<String, String> requestBody() {
        return getParams();
    }

    @Override
    protected void onParse(int what, String resp) {
        onResponse(what, resp);
    }

    protected abstract void onResponse(int what, String resp);

    protected abstract Map<String, String> getParams();
}
