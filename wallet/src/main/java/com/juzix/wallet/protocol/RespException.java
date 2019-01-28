package com.juzix.wallet.protocol;

public class RespException extends Exception{
    /**
     * 和服务器交互失败
     */
    public static final int SERVER_ERROR = -4;
    /**
     * 服务器请求超时
     */
    public static final int SERVER_TIMEOUT_ERROR = -5;

    public int code;

    public String msg;

    public RespException(int code, Exception exception){
        super(exception);
        this.code = code;
    }

    public RespException(int code, String msg, Exception exception) {
        super(exception);
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return msg;
    }
}
