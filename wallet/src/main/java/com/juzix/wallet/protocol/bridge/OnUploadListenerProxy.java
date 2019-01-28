package com.juzix.wallet.protocol.bridge;

import com.juzhen.framework.network.OnUploadListener;
import com.juzix.wallet.protocol.OnHttpUploadListener;
import com.juzix.wallet.protocol.RespException;


class OnUploadListenerProxy implements OnUploadListener {
    private OnHttpUploadListener mListener;

    public OnUploadListenerProxy(OnHttpUploadListener listener) {
        mListener = listener;
    }

    @Override
    public void onStart(int what) {

    }

    @Override
    public void onCancel(int what) {
        if (mListener != null) {
            mListener.onCancel(what);
        }
    }

    @Override
    public void onProgress(int what, int progress) {
        if (mListener != null) {
            mListener.onProgress(what, progress);
        }
    }

    @Override
    public void onFinish(int what) {
        if (mListener != null) {
            mListener.onSuccess(what);
        }
    }

    @Override
    public void onError(int what, Exception exception) {
        if (exception instanceof org.apache.http.conn.ConnectTimeoutException || exception instanceof java.net.SocketTimeoutException) {
            mListener.onFail(what, new RespException(RespException.SERVER_TIMEOUT_ERROR, exception));
        } else {
            mListener.onFail(what, new RespException(RespException.SERVER_ERROR, exception));
        }
    }
}
