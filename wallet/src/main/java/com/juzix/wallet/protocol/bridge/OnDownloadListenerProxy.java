package com.juzix.wallet.protocol.bridge;

import com.juzhen.framework.network.Headers;
import com.juzhen.framework.network.download.DownloadListener;
import com.juzix.wallet.protocol.OnHttpDownloadListener;
import com.juzix.wallet.protocol.RespException;


class OnDownloadListenerProxy implements DownloadListener {
    private OnHttpDownloadListener mListener;

    public OnDownloadListenerProxy(OnHttpDownloadListener listener) {
        mListener = listener;
    }

    @Override
    public void onDownloadError(int what, Exception exception) {
        if (exception instanceof org.apache.http.conn.ConnectTimeoutException || exception instanceof java.net.SocketTimeoutException) {
            mListener.onFail(what, new RespException(RespException.SERVER_TIMEOUT_ERROR, exception));
        } else {
            mListener.onFail(what, new RespException(RespException.SERVER_ERROR, exception));
        }
    }

    @Override
    public void onStart(int what, boolean isResume, long rangeSize, Headers responseHeaders, long allCount) {

    }

    @Override
    public void onProgress(int what, int progress, long fileCount, long speed) {
        if (mListener != null) {
            mListener.onProgress(what, progress, fileCount, speed);
        }
    }

    @Override
    public void onFinish(int what, String filePath) {
        if (mListener != null) {
            mListener.onSuccess(what, filePath);
        }
    }

    @Override
    public void onCancel(int what) {
        if (mListener != null) {
            mListener.onCancel(what);
        }
    }
}
