package com.juzix.wallet.protocol.base;



import com.juzix.wallet.protocol.HttpConfigure;
import com.juzix.wallet.protocol.OnHttpProviderCallback;
import com.juzix.wallet.protocol.URLConfigure;
import com.juzix.wallet.protocol.resp.BaseResp;

import java.util.HashMap;
import java.util.Map;

public abstract class BasePostJsonProvider<P extends BaseResp> extends BaseHttpProvider<String, String, P> {

    public BasePostJsonProvider(OnHttpProviderCallback<P> callback) {
        super(callback);
    }

    @Override
    public int requestMethod() {
        return POST;
    }

    @Override
    public int responseType() {
        return IHttpResponse.JSON;
    }

    @Override
    public int requestType() {
        return IHttpRequest.JSON;
    }

    @Override
    public String requestBody() {
        return getJson();
    }

    @Override
    public String getURL() {
        return URLConfigure.URI;
    }

    @Override
    public Map<String, String> getHeaders() {
        String cmd = getCMD();
        if (cmd == null || "".equals(cmd)) {
            return null;
        }
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Cmd", cmd);
        return headers;
    }

    @Override
    public IHttpLoader getHttpLoader() {
        return HttpConfigure.getLoader();
    }

    @Override
    public void onParse(int what, String resp) {
        onResponse(what, resp);
    }

    protected abstract String getJson();

    protected abstract String getCMD();

    protected abstract void onResponse(int what, String resp);

}
