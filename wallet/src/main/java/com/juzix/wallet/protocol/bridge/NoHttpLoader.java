package com.juzix.wallet.protocol.bridge;

import com.juzhen.framework.network.BasicBinary;
import com.juzhen.framework.network.FileBinary;
import com.juzhen.framework.network.NoHttp;
import com.juzhen.framework.network.RequestMethod;
import com.juzhen.framework.network.download.DownloadRequest;
import com.juzhen.framework.network.rest.CacheMode;
import com.juzhen.framework.network.rest.Request;
import com.juzix.wallet.protocol.OnHttpDownloadListener;
import com.juzix.wallet.protocol.OnHttpUploadListener;
import com.juzix.wallet.protocol.base.IHttpCallback;
import com.juzix.wallet.protocol.base.IHttpLoader;
import com.juzix.wallet.protocol.base.IHttpProvider;
import com.juzix.wallet.protocol.base.IHttpRequest;
import com.juzix.wallet.protocol.base.IHttpResponse;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class NoHttpLoader<T> implements IHttpLoader<T> {

    private int                    mWhat;
    private IHttpProvider          mProvider;
    private IHttpRequest           mRequest;
    private IHttpResponse          mResponse;
    private IHttpCallback<T>       mCallback;
    private OnHttpDownloadListener mOnDownloadListener;
    private OnHttpUploadListener   mOnUploadListener;

    @Override
    public void attachLoader(int what, IHttpProvider provider, IHttpRequest request, IHttpResponse response,
                             IHttpCallback<T> callback) {
        mWhat = what;
        mProvider = provider;
        mRequest = request;
        mResponse = response;
        mCallback = callback;
    }

    @Override
    public void load() {
        if (mProvider == null || mRequest == null || mResponse == null) {
            return;
        }

        int requestMethod = mProvider.requestMethod();
        int requestType   = mRequest.requestType();
        int responseType  = mResponse.responseType();

        if (requestMethod == IHttpProvider.POST && requestType == IHttpRequest.JSON && responseType == IHttpResponse.JSON) {
            postJson((String) mRequest.requestBody());
        } else if (requestMethod == IHttpProvider.POST && requestType == IHttpRequest.FILE && responseType == IHttpResponse.JSON) {
            HashMap<String, String> params = (HashMap<String, String>) mRequest.requestBody();
            postFile(params);
        } else if (requestMethod == IHttpProvider.GET && responseType == IHttpResponse.FILE) {
            HashMap<String, String> params = (HashMap<String, String>) mRequest.requestBody();
            download(params.get("filepath"), params.get("filename"));
        }
    }

    @Override
    public void setOnHttpDownloadListener(OnHttpDownloadListener listener) {
        mOnDownloadListener = listener;
    }

    @Override
    public void setOnHttpUploadListener(OnHttpUploadListener listener) {
        mOnUploadListener = listener;
    }

    private Request<String> createRequest(String url, String key, RequestMethod method) {
        // 创建请求对象。
        Request<String> request = NoHttp.createStringRequest(url, method);
        if (url != null && !"".equals(url) && key != null && !"".equals(key)) {
            // 这里的key是缓存数据的主键，默认是url，使用的时候要保证全局唯一，否则会被其他相同url数据覆盖。
            request.setCacheKey(url + File.separator + key);
            //默认就是DEFAULT，所以这里可以不用设置，DEFAULT代表走Http标准协议。
            request.setCacheMode(CacheMode.DEFAULT);
        }
        // 单个请求的超时时间，不指定就会使用全局配置。
        // 设置连接超时。
        //request.setConnectTimeout(60 * 1000);
        // 设置读取超时时间，也就是服务器的响应超时。
        //request.setReadTimeout(60 * 1000);
        // 请求头，是否要添加头，添加什么头，要看开发者服务器端的要求。
        setHeader(request, mProvider.getHeaders());
        // 设置一个tag, 在请求完(失败/成功)时原封不动返回; 多数情况下不需要。
        request.setTag(new Object());
        // 设置取消标志。
        request.setCancelSign(mWhat);

        return request;
    }

    private void postJson(String json) {

        // 创建请求对象。
        Request<String> request = createRequest(mProvider.getURL(), json, RequestMethod.POST);
        // 添加请求参数。
        request.setDefineRequestBodyForJson(json);

        NoHttpManager.getInstance().addRequest(mWhat, request, new OnResponseListenerProxy(mCallback));

    }

    private void postFile(Map<String, String> params) {

        String filePath = params.get("filePath");
        String fileName = params.get("fileName");
        String fileSize = params.get("fileSize");

        BasicBinary binary = new FileBinary(new File(filePath));
        if (mOnUploadListener != null) {
            binary.setUploadListener(mWhat, new OnUploadListenerProxy(mOnUploadListener));
        }
        // 创建请求对象。
        Request<String> request = createRequest(mProvider.getURL(), "", RequestMethod.POST);
        // 添加普通参数。
//        request.add("user", "yolanda");
        // 添加1个文件
        request.add("file", binary);
        request.add("fileName", fileName);
        request.add("fileSize", fileSize);
        request.add("policy", "public");

        NoHttpManager.getInstance().addRequest(mWhat, request, new OnResponseListenerProxy(mCallback));
    }

    private void download(String filepath, String filename) {

        DownloadRequest request = new DownloadRequest(mProvider.getURL(),
                RequestMethod.GET,
                filepath,
                filename,
                true, true);

        NoHttpManager.getInstance().addDownload(mWhat, request, new OnDownloadListenerProxy(mOnDownloadListener));
    }


    private void setHeader(Request<String> request, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                request.addHeader(entry.getKey(), entry.getValue());
            }
        }
    }

}
