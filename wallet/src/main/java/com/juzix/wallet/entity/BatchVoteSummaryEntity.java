package com.juzix.wallet.entity;

/**
 * @author matrixelement
 */
public class BatchVoteSummaryEntity {

    /**
     * 投票锁定,单位Energon
     */
    private String locked;
    /**
     * 投票收益,单位Energon
     */
    private String earnings;
    /**
     * 总票数
     */
    private String totalTicketNum;
    /**
     * 有效票数
     */
    private String validNum;

    public BatchVoteSummaryEntity() {
    }

    public BatchVoteSummaryEntity(String locked, String earnings, String totalTicketNum, String validNum) {
        this.locked = locked;
        this.earnings = earnings;
        this.totalTicketNum = totalTicketNum;
        this.validNum = validNum;
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getTotalTicketNum() {
        return totalTicketNum;
    }

    public void setTotalTicketNum(String totalTicketNum) {
        this.totalTicketNum = totalTicketNum;
    }

    public String getValidNum() {
        return validNum;
    }

    public void setValidNum(String validNum) {
        this.validNum = validNum;
    }
}
