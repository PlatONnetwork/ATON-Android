package com.juzix.wallet.entity;

public class WithDrawBalance {
//    /**
//     * 赎回中委托  单位von   1LAT(ETH)=1000000000000000000von(wei)
//     */
//    private String redeem;
    /**
     * 已锁定委托  单位von
     */
    private String locked;
    /**
     * 未锁定委托  单位von
     */
    private String unLocked;
    /**
     * 已解除委托  单位von
     */
    private String released;


    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getUnLocked() {
        return unLocked;
    }

    public void setUnLocked(String unLocked) {
        this.unLocked = unLocked;
    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }
}
