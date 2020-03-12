package com.platon.aton.entity;

public class TransactionReceipt {

    private int status;

    private String hash;

    private String totalReward;

    private String blockNumber;

    public TransactionReceipt(int status, String hash) {
        this.status = status;
        this.hash = hash;
    }

    public TransactionReceipt() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(String totalReward) {
        this.totalReward = totalReward;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }
}
