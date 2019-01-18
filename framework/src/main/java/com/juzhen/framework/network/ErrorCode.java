package com.juzhen.framework.network;


public class ErrorCode {
    /**
     * 公共部分
     */
    // 成功
    public static final int SUCC = 0;

    // 没有连接到服务器
    public static final int NO_CONNECT = -1;

    // 系统内部错误
    public static final int SYSTEM_ERROR = -2;

    // 和服务器交互失败
    public static final int SERVER_ERROR = -3;

    // 用户没有在线登录
    public static final int NOT_ONLINE_LOGIN = -4;

    // 服务器请求超时
    public static final int SERVER_TIMEOUT_ERROR = -5;

    // 服务器崩溃，系统繁忙
    public static final int SERVER_BUSY_ERROR = 6331;

    /**
     * 消息部分
     */
    public static final int CONTENT_UTF8_DEOCDE_ERROR = -9400;
    public static final int PARSE_RSP_JSON_FAILED = -9401;
    public static final int NEW_RSP_FAILED = -9402;

    /**
     * 用户部分
     */
    // 密码错误次数超过最大限制
    public static final int USER_EXCEED_PWD_ERRORTIMES = -100;
    // 用户不存在（离线登录）
    public static final int USER_NOT_EXISTS = -101;
    // 密码错误（离线登录）
    public static final int USER_PWD_ERROR = -102;
    // 超出离线登录的最大时间（离线登录）
    public static final int USER_EXCEED_LAST_ONLINELOGIN__TIME = -103;

    public static final int USER_KICKED = 2001;

    /**
     * xmpp部分
     */
    //连接XMPP失败
    public static final int XMPP_NO_CONNECT = -1;
    //登陆XMPP成功
    public static final int XMPP_CONNECT_SUCC = 0;
    //断开XMPP失败
    public static final int XMPP_DISCONNECT_FAILED = -1;
    //断开XMPP成功
    public static final int XMPP_DISCONNECT_SUCC = 0;

    /**
     * 支付部分
     */
    //线上百果园果币余额不足
    public static final int PAY_COIN_BALANCE_NOT_ENOUGH = 6112;
    //支付宝取消支付
    public static final int ALIPAY_CANCEL = -1;
    //微信取消支付
    public static final int WX_PAY_CANCEL = -2;

    //微信未安装
    public static final int WX_NOT_INSTALLED = -5;

    public final static class LoginErrorCode {

        public static final int SYSTEM_OPERATION_EXCEPTION = -10003;

        public static final int SYSTEM_CONNECT_FAILED = -10004;

        public static final int VERIFICATION_CODE_INCORRECT = 30007;

        public static final int USER_ACCOUNT_EXCEPTION = 55306;

        public static final int INTEGRATION_REGISTER_FAILED = 68000;

        public static final int LOGIN_TIME_RESTRICTION = 55303;

        public static final int PHONE_NUMBER_FORMAT_EXCEPTION = 30008;

    }


}
