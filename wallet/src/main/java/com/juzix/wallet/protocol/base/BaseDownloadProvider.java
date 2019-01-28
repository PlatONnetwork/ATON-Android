package com.juzix.wallet.protocol.base;

import com.juzix.wallet.protocol.HttpConfigure;
import com.juzix.wallet.protocol.OnHttpDownloadListener;

import java.util.HashMap;
import java.util.Map;

public class BaseDownloadProvider extends BaseHttpProvider {

    private String      mUrl;
    private String      mPath;
    private String      mFilename;
    private IHttpLoader mLoader;

    /**
     * 下载资源
     *
     * @param downUrl  下载地址
     * @param path     保存路径
     * @param filename 保存文件名
     * @param listener 下载回调
     */
    public BaseDownloadProvider(String downUrl, String path, String filename,
                                OnHttpDownloadListener listener) {
        super(null);
        mUrl = downUrl;
        mPath = path;
        mFilename = filename;
        mLoader = HttpConfigure.getLoader();
        mLoader.setOnHttpDownloadListener(listener);
    }

    @Override
    public IHttpLoader getHttpLoader() {
        return mLoader;
    }

    @Override
    protected void onParse(int what, Object resp) {

    }

    @Override
    public String getURL() {
        return mUrl;
    }

    @Override
    public int requestMethod() {
        return GET;
    }

    @Override
    public int requestType() {
        return STRING;
    }

    @Override
    public int responseType() {
        return IHttpResponse.FILE;
    }

    @Override
    public Map<String, String> requestBody() {
        HashMap<String, String> params = new HashMap<>();
        params.put("filepath", mPath);
        params.put("filename", mFilename);
        return params;
    }
}
