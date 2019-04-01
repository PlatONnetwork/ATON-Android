package com.juzix.wallet.entity;

/**
 * @author matrixelement
 */
public class BatchVoteTransactionEntity {

    /**
     * 交易hash
     */
    private String TransactionHash;
    /**
     * 候选人Id
     */
    private String candidateId;
    /**
     * 投票人钱包地址
     */
    private String owner;
    /**
     * 此次交易投票获得的收益，单位Energon
     */
    private String earnings;
    /**
     * Unix时间戳，毫秒级,交易时间
     */
    private String transactiontime;
    /**
     * 当时的购票价格，单位Energon
     */
    private String deposit;
    /**
     * 总票数
     */
    private String totalTicketNum;
    /**
     * 有效票
     */
    private String validNum;
    /**
     * 区域
     */
    private RegionEntity regionEntity;
    /**
     * 节点名称
     */
    private String nodeName;

    private double voteStaked;

    public BatchVoteTransactionEntity() {

    }

    private BatchVoteTransactionEntity(Builder builder) {
        setTransactionHash(builder.TransactionHash);
        setCandidateId(builder.candidateId);
        setOwner(builder.owner);
        setEarnings(builder.earnings);
        setTransactiontime(builder.transactiontime);
        setDeposit(builder.deposit);
        setTotalTicketNum(builder.totalTicketNum);
        setValidNum(builder.validNum);
        setRegionEntity(builder.regionEntity);
        setNodeName(builder.nodeName);
        setVoteStaked(builder.voteStaked);
    }

    public String getTransactionHash() {
        return TransactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        TransactionHash = transactionHash;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getEarnings() {
        return earnings;
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getTransactiontime() {
        return transactiontime;
    }

    public void setTransactiontime(String transactiontime) {
        this.transactiontime = transactiontime;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
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

    public RegionEntity getRegionEntity() {
        return regionEntity;
    }

    public void setRegionEntity(RegionEntity regionEntity) {
        this.regionEntity = regionEntity;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public double getVoteStaked() {
        return voteStaked;
    }

    public void setVoteStaked(double voteStaked) {
        this.voteStaked = voteStaked;
    }


    public static final class Builder {
        private String TransactionHash;
        private String candidateId;
        private String owner;
        private String earnings;
        private String transactiontime;
        private String deposit;
        private String totalTicketNum;
        private String validNum;
        private RegionEntity regionEntity;
        private String nodeName;
        private double voteStaked;

        public Builder() {
        }

        public Builder TransactionHash(String val) {
            TransactionHash = val;
            return this;
        }

        public Builder candidateId(String val) {
            candidateId = val;
            return this;
        }

        public Builder owner(String val) {
            owner = val;
            return this;
        }

        public Builder earnings(String val) {
            earnings = val;
            return this;
        }

        public Builder transactiontime(String val) {
            transactiontime = val;
            return this;
        }

        public Builder deposit(String val) {
            deposit = val;
            return this;
        }

        public Builder totalTicketNum(String val) {
            totalTicketNum = val;
            return this;
        }

        public Builder validNum(String val) {
            validNum = val;
            return this;
        }

        public Builder regionEntity(RegionEntity val) {
            regionEntity = val;
            return this;
        }

        public Builder nodeName(String val) {
            nodeName = val;
            return this;
        }

        public Builder voteStaked(double val) {
            voteStaked = val;
            return this;
        }

        public BatchVoteTransactionEntity build() {
            return new BatchVoteTransactionEntity(this);
        }
    }
}
