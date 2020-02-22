package com.juzix.wallet.entity;

import android.support.annotation.IntDef;

/**
 * @author ziv
 * date On 2020-02-22
 */
@IntDef({
        RPCErrorCode.SUCCESS,
        RPCErrorCode.CONNECT_TIMEOUT,
        RPCErrorCode.SOCKET_TIMEOUT
})
public @interface RPCErrorCode {

    /**
     * 成功
     */
    int SUCCESS = 0;

    /**
     * 连接超时
     */
    int CONNECT_TIMEOUT = 1;

    /**
     * 响应
     */
    int SOCKET_TIMEOUT = 2;
}
