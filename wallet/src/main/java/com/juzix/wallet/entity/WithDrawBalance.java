package com.juzix.wallet.entity;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.BigDecimalUtil;

public class WithDrawBalance {

    /**
     * 已解除委托  单位von
     */
    private String released;

    /**
     * 块高
     */
    private String stakingBlockNum;

    /**
     * 已委托  单位von
     */
    private String delegated;


    public WithDrawBalance() {

    }

    public String getReleased() {
        return released;
    }

    public String getShowReleased() {
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(released, "1E18"));
    }

    public void setReleased(String released) {
        this.released = released;
    }

    public String getStakingBlockNum() {
        return stakingBlockNum;
    }

    public void setStakingBlockNum(String stakingBlockNum) {
        this.stakingBlockNum = stakingBlockNum;
    }

    public String getDelegated() {
        return delegated;
    }

    public String getShowDelegated(){
        return  NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(delegated,"1E18"));
    }

    public void setDelegated(String delegated) {
        this.delegated = delegated;
    }
}
