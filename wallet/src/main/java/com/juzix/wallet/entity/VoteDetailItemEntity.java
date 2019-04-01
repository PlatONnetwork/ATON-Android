package com.juzix.wallet.entity;

import android.support.annotation.NonNull;

/**
 * @author matrixelement
 */
public class VoteDetailItemEntity implements Comparable<VoteDetailItemEntity> {

    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 票价
     */
    private double ticketPrice;
    /**
     * 投票锁定
     */
    private double voteStaked;
    /**
     * 投票解除
     */
    private double voteUnStaked;
    /**
     * 收益
     */
    private double profit;
    /**
     * 投票钱包地址
     */
    private String walletAddress;
    /**
     * 投票钱包名称
     */
    private String walletName;
    /**
     * 预计/过期时间
     */
    private long expireTime;
    /**
     * 候选人id
     */
    private String candidateId;
    /**
     * 交易id
     */
    private String transactionId;
    /**
     * 有效票数量
     */
    private long validVoteNum;
    /**
     * 失效票数量
     */
    private long invalidVoteNum;

    private VoteDetailItemEntity(Builder builder) {
        setCreateTime(builder.createTime);
        setTicketPrice(builder.ticketPrice);
        setVoteStaked(builder.voteStaked);
        setVoteUnStaked(builder.voteUnStaked);
        setProfit(builder.profit);
        setWalletAddress(builder.walletAddress);
        setWalletName(builder.walletName);
        setExpireTime(builder.expireTime);
        setCandidateId(builder.candidateId);
        setTransactionId(builder.transactionId);
        setValidVoteNum(builder.validVoteNum);
        setInvalidVoteNum(builder.invalidVoteNum);
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public double getVoteStaked() {
        return voteStaked;
    }

    public void setVoteStaked(double voteStaked) {
        this.voteStaked = voteStaked;
    }

    public double getVoteUnStaked() {
        return voteUnStaked;
    }

    public void setVoteUnStaked(double voteUnStaked) {
        this.voteUnStaked = voteUnStaked;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public long getValidVoteNum() {
        return validVoteNum;
    }

    public void setValidVoteNum(long validVoteNum) {
        this.validVoteNum = validVoteNum;
    }

    public long getInvalidVoteNum() {
        return invalidVoteNum;
    }

    public void setInvalidVoteNum(long invalidVoteNum) {
        this.invalidVoteNum = invalidVoteNum;
    }

    @Override
    public int compareTo(@NonNull VoteDetailItemEntity o) {
        return Long.compare(o.createTime, createTime);
    }

    public static final class Builder {
        private long createTime;
        private double ticketPrice;
        private double voteStaked;
        private double voteUnStaked;
        private double profit;
        private String walletAddress;
        private String walletName;
        private long expireTime;
        private String candidateId;
        private String transactionId;
        private long validVoteNum;
        private long invalidVoteNum;

        public Builder() {
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder ticketPrice(double val) {
            ticketPrice = val;
            return this;
        }

        public Builder voteStaked(double val) {
            voteStaked = val;
            return this;
        }

        public Builder voteUnStaked(double val) {
            voteUnStaked = val;
            return this;
        }

        public Builder profit(double val) {
            profit = val;
            return this;
        }

        public Builder walletAddress(String val) {
            walletAddress = val;
            return this;
        }

        public Builder walletName(String val) {
            walletName = val;
            return this;
        }

        public Builder expireTime(long val) {
            expireTime = val;
            return this;
        }

        public Builder candidateId(String val) {
            candidateId = val;
            return this;
        }

        public Builder transactionId(String val) {
            transactionId = val;
            return this;
        }

        public Builder validVoteNum(long val) {
            validVoteNum = val;
            return this;
        }

        public Builder invalidVoteNum(long val) {
            invalidVoteNum = val;
            return this;
        }

        public VoteDetailItemEntity build() {
            return new VoteDetailItemEntity(this);
        }
    }
}
