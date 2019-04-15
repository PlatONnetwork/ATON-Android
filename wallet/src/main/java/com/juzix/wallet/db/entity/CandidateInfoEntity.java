package com.juzix.wallet.db.entity;

import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.utils.JSONUtil;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CandidateInfoEntity extends RealmObject {

    /**
     * 质押金额 (单位：ADP)
     */
    private String deposit;
    /**
     * 质押金更新的最新块高
     */
    private long blockNumber;

    /**
     * 质押金退款地址
     */
    private String owner;

    /**
     * 所在区块交易索引
     */
    private int txIndex;

    /**
     * 节点Id(公钥)
     */
    @PrimaryKey
    private String candidateId;

    /**
     * 最新质押交易的发送方
     */
    private String from;

    /**
     * 出块奖励佣金比，以10000为基数(eg：5%，则fee=500)
     */
    private int fee;
    /**
     * 节点主机ip
     */
    private String host;
    /**
     * 节点PORT
     */
    private String port;
    /**
     * 幸运票所在交易Hash
     */
    private String txHash;

    private String extra;

    private String nodeAddress;

    private String candidateName;

    public CandidateInfoEntity() {

    }

    public CandidateInfoEntity(Builder builder) {
        this.deposit = builder.deposit;
        this.blockNumber = builder.blockNumber;
        this.owner = builder.owner;
        this.txIndex = builder.txIndex;
        this.candidateId = builder.candidateId;
        this.from = builder.from;
        this.fee = builder.fee;
        this.host = builder.host;
        this.port = builder.port;
        this.txHash = builder.txHash;
        this.extra = builder.extra;
        this.nodeAddress = builder.nodeAddress;
        this.candidateName = builder.candidateName;
    }


    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(int txIndex) {
        this.txIndex = txIndex;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }



    public CandidateEntity buildCandidateEntity() {
        return JSONUtil.parseObject(JSONUtil.toJSONString(this),CandidateEntity.class);
    }

    public static final class Builder {
        private String deposit;
        private long blockNumber;
        private String owner;
        private int txIndex;
        private String candidateId;
        private String from;
        private int fee;
        private String host;
        private String port;
        private String txHash;
        private String extra;
        private String nodeAddress;
        private String candidateName;

        public Builder setDeposit(String deposit) {
            this.deposit = deposit;
            return this;
        }

        public Builder setBlockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
            return this;
        }

        public Builder setOwner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder setTxIndex(int txIndex) {
            this.txIndex = txIndex;
            return this;
        }

        public Builder setCandidateId(String candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder setFee(int fee) {
            this.fee = fee;
            return this;
        }

        public Builder setHost(String host) {
            this.host = host;
            return this;
        }

        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        public Builder setTxHash(String txHash) {
            this.txHash = txHash;
            return this;
        }

        public Builder setExtra(String extra) {
            this.extra = extra;
            return this;
        }

        public Builder setNodeAddress(String nodeAddress) {
            this.nodeAddress = nodeAddress;
            return this;
        }

        public Builder setCandidateName(String candidateName) {
            this.candidateName = candidateName;
            return this;
        }

        public CandidateInfoEntity build() {
            return new CandidateInfoEntity(this);
        }
    }
}
