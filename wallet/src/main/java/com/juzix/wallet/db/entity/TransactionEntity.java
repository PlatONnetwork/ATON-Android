package com.juzix.wallet.db.entity;

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
     * 节点地址
     */
    private String nodeAddress;
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
    private int txType;
    /**
     * 交易状态 1 成功 0 失败
     */
    private int txReceiptStatus;
    /**
     * // 排列序号：由区块号和交易索引拼接而成
     */
    private long sequence;
    /**
     * 交易接收者类型（to是合约还是账户）contract合约、 account账户
     */
    private String receiveType;
    /**
     * 链id
     */
    private String chainId;
    /**
     * 交易实际花费值(手续费)，单位：wei
     * "21168000000000"
     */
    private String actualTxCost;

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
        this.nodeAddress = builder.nodeAddress;
        this.txType = builder.txType;
        this.txReceiptStatus = builder.txReceiptStatus;
        this.sequence = builder.sequence;
        this.receiveType = builder.receiveType;
        this.chainId = builder.chainId;
        this.actualTxCost = builder.actualTxCost;
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

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public int getTxType() {
        return txType;
    }

    public void setTxType(int txType) {
        this.txType = txType;
    }

    public int getTxReceiptStatus() {
        return txReceiptStatus;
    }

    public void setTxReceiptStatus(int txReceiptStatus) {
        this.txReceiptStatus = txReceiptStatus;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
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

    static final class Builder {
        private String hash;
        private long createTime;
        private String walletName;
        private String from;
        private String to;
        private long blockNumber;
        private double value;
        private String nodeAddress;
        private int txType;
        private int txReceiptStatus;
        private long sequence;
        private String receiveType;
        private String chainId;
        private String actualTxCost;

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

        public Builder setNodeAddress(String nodeAddress) {
            this.nodeAddress = nodeAddress;
            return this;
        }

        public Builder setTxType(int txType) {
            this.txType = txType;
            return this;
        }

        public Builder setTxReceiptStatus(int txReceiptStatus) {
            this.txReceiptStatus = txReceiptStatus;
            return this;
        }

        public Builder setSequence(long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder setReceiveType(String receiveType) {
            this.receiveType = receiveType;
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

        public TransactionEntity build() {
            return new TransactionEntity(this);
        }
    }
}
