package com.juzix.wallet.protocol;

/**
 * 业务响应代理专门处理
 *
 * @author ziv
 */
public class BusinessResponceProxy {

    private final static class InnerClass {
        private final static BusinessResponceProxy BUSINESS_RESPONCE_PROXY = new BusinessResponceProxy();
    }

    public static BusinessResponceProxy getInstance() {
        return InnerClass.BUSINESS_RESPONCE_PROXY;
    }

}
