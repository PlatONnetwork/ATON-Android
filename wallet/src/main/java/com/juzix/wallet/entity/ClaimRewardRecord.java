package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class ClaimRewardRecord {

    private String walletAvatar;

    private String walletName;

    private String address;

    private String totalReward;

    private long timestamp;

    private long sequence;

    @JSONField(name = "item")
    private List<ClaimReward> claimRewardList;

    private boolean expanded;

    public ClaimRewardRecord() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(String totalReward) {
        this.totalReward = totalReward;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<ClaimReward> getClaimRewardList() {
        return claimRewardList;
    }

    public void setClaimRewardList(List<ClaimReward> claimRewardList) {
        this.claimRewardList = claimRewardList;
    }

    public String getWalletAvatar() {
        return walletAvatar;
    }

    public void setWalletAvatar(String walletAvatar) {
        this.walletAvatar = walletAvatar;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
