package com.juzix.wallet.entity;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.BigDecimalUtil;

public class AccountBalance {
    /**
     * 钱包地址
     */
    private String addr;

    /**
     * 自由账户余额  单位von
     */
    private String free;


    /**
     * 锁仓账户余额  单位von
     */
    private String lock;


    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }


    public String getShowFreeBalace() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(free, "1E18"));
    }

    public String getShowLockBalance() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(lock, "1E18"));
    }

}
