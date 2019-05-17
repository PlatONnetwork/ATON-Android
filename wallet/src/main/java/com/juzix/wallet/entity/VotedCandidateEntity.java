package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;

public class VotedCandidateEntity {

    /**
     * 节点ID
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 国家代码
     */
    private String countryCode;
    /**
     * 国家英文名称
     */
    private String countryEnName;
    /**
     * 国家中文名称
     */
    private String countryCnName;
    /**
     * 国家拼音名称，中文环境下，区域进行排序
     */
    private String countrySpellName;
    /**
     * 有效票数
     */
    private String validNum;
    /**
     * 总票数
     */
    private String totalTicketNum;
    /**
     * 投票锁定,单位Energon
     */
    private String locked;
    /**
     * 投票解除锁定
     */
    private String unLocked;
    /**
     * 投票收益,单位Energon
     */
    private String earnings;
    /**
     * 最新投票时间，单位-毫秒
     */
    private String transactionTime;
    /**
     * 当时的购票价格，单位Energon
     */
    private String deposit;
    /**
     * 投票人钱包地址
     */
    private String owner;

    public VotedCandidateEntity() {
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryEnName() {
        return countryEnName;
    }

    public void setCountryEnName(String countryEnName) {
        this.countryEnName = countryEnName;
    }

    public String getCountryCnName() {
        return countryCnName;
    }

    public void setCountryCnName(String countryCnName) {
        this.countryCnName = countryCnName;
    }

    public String getCountrySpellName() {
        return countrySpellName;
    }

    public void setCountrySpellName(String countrySpellName) {
        this.countrySpellName = countrySpellName;
    }

    public String getValidNum() {
        return validNum;
    }

    public void setValidNum(String validNum) {
        this.validNum = validNum;
    }

    public String getTotalTicketNum() {
        return totalTicketNum;
    }

    public void setTotalTicketNum(String totalTicketNum) {
        this.totalTicketNum = totalTicketNum;
    }

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

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
