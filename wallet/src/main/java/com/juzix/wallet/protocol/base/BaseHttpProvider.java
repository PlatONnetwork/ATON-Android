package com.juzix.wallet.protocol.base;


import android.util.Log;

import com.juzix.wallet.protocol.OnHttpProviderCallback;
import com.juzix.wallet.protocol.RespException;
import com.juzix.wallet.protocol.resp.BaseResp;

import java.util.Map;


public abstract class BaseHttpProvider<U, V, P extends BaseResp> implements IHttpProvider, IHttpRequest<U>, IHttpResponse<V>, IHttpCallback<V> {

    private final static String TAG = BaseHttpProvider.class.getSimpleName();
    private final static String RESPONCE = "[RESPONCE]";
    private final static String REQUEST = "[REQUEST]";

    protected OnHttpProviderCallback<P> mProviderCallback;
    private int mWhat = DEFAULT_WHAT;

    public BaseHttpProvider(OnHttpProviderCallback<P> callback) {
        mProviderCallback = callback;
    }

    public void what(int what) {
        mWhat = what;
    }

    /**
     * 发送数据
     */
    public void start() {
        IHttpLoader loader = getHttpLoader();
        if (loader != null) {
            loader.attachLoader(mWhat, this, this, this, this);
            if (getHeaders() != null) {
                Log.e(TAG, REQUEST + getHeaders().get("Cmd") + ":" + requestBody());
            }
            loader.load();
        }
    }

    @Override
    public String getURL() {
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return null;
    }

    @Override
    public boolean supportCache() {
        return false;
    }

    @Override
    public boolean supportCookie() {
        return false;
    }

    @Override
    public int responseType() {
        return IHttpResponse.JSON;
    }

    @Override
    public V responseBody() {
        return null;
    }

    @Override
    public int requestMethod() {
        return POST;
    }

    @Override
    public int requestType() {
        return IHttpRequest.JSON;
    }

    @Override
    public U requestBody() {
        return null;
    }

    @Override
    public void onSuccess(int what, V resp) {
        onParse(what, resp);
    }

    @Override
    public void onFail(int what, RespException exp) {
        if (mProviderCallback != null) {
            if (getHeaders() != null && exp != null) {
                Log.e(TAG, RESPONCE + getHeaders().get("Cmd") + ":" + exp.toString());
            }
            mProviderCallback.onFail(what, exp);
        }
    }

    protected void setResult(int what, P body) {
        if (mProviderCallback != null) {
            mProviderCallback.onSuccess(what, body);
        }
    }

    protected abstract IHttpLoader getHttpLoader();

    protected abstract void onParse(int what, V resp);
}
