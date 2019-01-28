package com.juzix.wallet.protocol.bridge;

import com.juzhen.framework.network.rest.OnResponseListener;
import com.juzhen.framework.network.rest.Response;
import com.juzix.wallet.protocol.RespException;
import com.juzix.wallet.protocol.base.IHttpCallback;

class OnResponseListenerProxy<T> implements OnResponseListener<T> {

    private final static String TAG = OnResponseListenerProxy.class.getSimpleName();

    private IHttpCallback<T> mListener;

    public OnResponseListenerProxy(IHttpCallback<T> listener) {
        mListener = listener;
    }

    @Override
    public void onStart(int what) {

    }

    @Override
    public void onSucceed(int what, Response<T> response) {
        if (mListener != null) {
            mListener.onSuccess(what, response.get());
        }
    }

    @Override
    public void onFailed(int what, Response<T> response) {

        Exception exception = response.getException();

        if (exception instanceof org.apache.http.conn.ConnectTimeoutException
                || exception instanceof java.net.SocketTimeoutException
                || exception instanceof com.juzhen.framework.network.error.TimeoutError) {
            mListener.onFail(what, new RespException(RespException.SERVER_TIMEOUT_ERROR, exception));
        } else {
            mListener.onFail(what, new RespException(RespException.SERVER_ERROR, exception));
        }
    }

    @Override
    public void onFinish(int what) {

    }
}
