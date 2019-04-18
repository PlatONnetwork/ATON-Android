package com.juzix.wallet.entity;

import android.support.annotation.NonNull;

import com.juzix.wallet.utils.DateUtil;

/**
 * @author matrixelement
 */
public class VoteDetailItemEntity implements Comparable<VoteDetailItemEntity> {

    private static final long EXPIRE_BLOCKNUMBER = 1536000000;
    /**
     * 创建时间
     */
    private String createTime;
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
    private String profit;
    /**
     * 投票钱包地址
     */
    private String walletAddress;
    /**
     * 投票钱包名称
     */
    private String walletName;
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
    private double validVoteNum;
    /**
     * 失效票数量
     */
    private double invalidVoteNum;

    private VoteDetailItemEntity(Builder builder) {
        setCreateTime(builder.createTime);
        setTicketPrice(builder.ticketPrice);
        setVoteStaked(builder.voteStaked);
        setVoteUnStaked(builder.voteUnStaked);
        setProfit(builder.profit);
        setWalletAddress(builder.walletAddress);
        setWalletName(builder.walletName);
        setCandidateId(builder.candidateId);
        setTransactionId(builder.transactionId);
        setValidVoteNum(builder.validVoteNum);
        setInvalidVoteNum(builder.invalidVoteNum);
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
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

    public String getProfit() {
        return profit;
    }

    public void setProfit(String profit) {
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

    public String getExpireTime() {
        return DateUtil.format(DateUtil.parse(createTime, DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND) + EXPIRE_BLOCKNUMBER, DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND);
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

    public double getValidVoteNum() {
        return validVoteNum;
    }

    public void setValidVoteNum(double validVoteNum) {
        this.validVoteNum = validVoteNum;
    }

    public double getInvalidVoteNum() {
        return invalidVoteNum;
    }

    public void setInvalidVoteNum(double invalidVoteNum) {
        this.invalidVoteNum = invalidVoteNum;
    }

    @Override
    public int compareTo(@NonNull VoteDetailItemEntity o) {
        return Long.compare(DateUtil.parse(o.createTime, DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND), DateUtil.parse(createTime, DateUtil.DATETIME_FORMAT_PATTERN_WITH_SECOND));
    }

    public static final class Builder {
        private String createTime;
        private double ticketPrice;
        private double voteStaked;
        private double voteUnStaked;
        private String profit;
        private String walletAddress;
        private String walletName;
        private String candidateId;
        private String transactionId;
        private double validVoteNum;
        private double invalidVoteNum;

        public Builder() {
        }

        public Builder createTime(String val) {
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

        public Builder profit(String val) {
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

        public Builder candidateId(String val) {
            candidateId = val;
            return this;
        }

        public Builder transactionId(String val) {
            transactionId = val;
            return this;
        }

        public Builder validVoteNum(double val) {
            validVoteNum = val;
            return this;
        }

        public Builder invalidVoteNum(double val) {
            invalidVoteNum = val;
            return this;
        }

        public VoteDetailItemEntity build() {
            return new VoteDetailItemEntity(this);
        }
    }
}
