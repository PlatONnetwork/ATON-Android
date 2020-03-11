package com.platon.aton.db.entity;

import com.platon.aton.entity.Transaction;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class TransactionEntity extends RealmObject implements Cloneable {

    /**
     * 交易hash
     */
    @PrimaryKey
    private String hash;
    /**
     * 交易创建时间
     */
    private long createTime;
    /**
     * 发生交易的钱包名称
     */
    private String walletName;
    /**
     * 发送地址
     */
    private String from;
    /**
     * 接收地址
     */
    private String to;
    /**
     * 当前交易区块
     */
    private long blockNumber;
    /**
     * 交易金额
     */
    protected double value;
    /**
     * 交易类型 transfer ：转账
     * MPCtransaction ： MPC交易
     * contractCreate ： 合约创建
     * vote ： 投票
     * transactionExecute ： 合约执行
     * authorization ： 权限
     * candidateDeposit：竞选质押
     * candidateApplyWithdraw：减持质押
     * candidateWithdraw：提取质押
     * unknown：未知
     */
    private String txType;
    /**
     * 交易状态 1 成功 0 失败
     */
    private int txReceiptStatus;
    /**
     * 链id
     */
    private String chainId;
    /**
     * 交易实际花费值(手续费)，单位：wei
     * "21168000000000"
     */
    private String actualTxCost;
    /**
     * {json}交易详细信息
     */
    private String txInfo;

    private String nodeId;

    private String nodeName;

    private String totalReward;

    private String unDelegation;

    public TransactionEntity() {
    }

    public TransactionEntity(Builder builder) {
        this.hash = builder.hash;
        this.createTime = builder.createTime;
        this.walletName = builder.walletName;
        this.from = builder.from;
        this.to = builder.to;
        this.blockNumber = builder.blockNumber;
        this.value = builder.value;
        this.txType = builder.txType;
        this.txReceiptStatus = builder.txReceiptStatus;
        this.chainId = builder.chainId;
        this.actualTxCost = builder.actualTxCost;
        this.txInfo = builder.txInfo;
        this.nodeId = builder.nodeId;
        this.nodeName = builder.nodeName;
        this.totalReward = builder.totalReward;
        this.unDelegation = builder.unDelegation;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getWalletName() {
        return walletName;
    }

    public void setWalletName(String walletName) {
        this.walletName = walletName;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getTxType() {
        return txType;
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public int getTxReceiptStatus() {
        return txReceiptStatus;
    }

    public void setTxReceiptStatus(int txReceiptStatus) {
        this.txReceiptStatus = txReceiptStatus;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getActualTxCost() {
        return actualTxCost;
    }

    public void setActualTxCost(String actualTxCost) {
        this.actualTxCost = actualTxCost;
    }

    public String getTxInfo() {
        return txInfo;
    }

    public void setTxInfo(String txInfo) {
        this.txInfo = txInfo;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getTotalReward() {
        return totalReward;
    }

    public void setTotalReward(String totalReward) {
        this.totalReward = totalReward;
    }

    public String getUnDelegation() {
        return unDelegation;
    }

    public void setUnDelegation(String unDelegation) {
        this.unDelegation = unDelegation;
    }

    public static final class Builder {
        private String hash;
        private long createTime;
        private String walletName;
        private String from;
        private String to;
        private long blockNumber;
        private double value;
        private String txType;
        private int txReceiptStatus;
        private String chainId;
        private String actualTxCost;
        private String txInfo;
        private String nodeId;
        private String nodeName;
        private String totalReward;
        private String unDelegation;

        public Builder(String hash, String walletName, String from, String to, long createTime) {
            this.hash = hash;
            this.walletName = walletName;
            this.from = from;
            this.to = to;
            this.createTime = createTime;
        }

        public Builder setBlockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
            return this;
        }

        public Builder setValue(double value) {
            this.value = value;
            return this;
        }

        public Builder setTxType(String txType) {
            this.txType = txType;
            return this;
        }

        public Builder setTxReceiptStatus(int txReceiptStatus) {
            this.txReceiptStatus = txReceiptStatus;
            return this;
        }

        public Builder setChainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder setActualTxCost(String actualTxCost) {
            this.actualTxCost = actualTxCost;
            return this;
        }

        public Builder setTxInfo(String txInfo) {
            this.txInfo = txInfo;
            return this;
        }

        public Builder setNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder setNodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder setTotalReward(String totalReward) {
            this.totalReward = totalReward;
            return this;
        }

        public Builder setUnDelegation(String unDelegation) {
            this.unDelegation = unDelegation;
            return this;
        }


        public TransactionEntity build() {
            return new TransactionEntity(this);
        }
    }

    public Transaction toTransaction() {
        return new Transaction.Builder()
                .hash(hash)
                .timestamp(getCreateTime())
                .txType(txType)
                .txReceiptStatus(txReceiptStatus)
                .blockNumber(blockNumber)
                .senderWalletName(walletName)
                .chainId(chainId)
                .from(from)
                .to(to)
                .value(String.valueOf(value))
                .actualTxCost(actualTxCost)
                .txInfo(txInfo)
                .nodeId(nodeId)
                .nodeName(nodeName)
                .totalReward(totalReward)
                .unDelegation(unDelegation)
                .build();
    }
}
