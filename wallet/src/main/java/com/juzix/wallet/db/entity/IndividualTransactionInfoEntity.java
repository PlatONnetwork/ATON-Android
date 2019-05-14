package com.juzix.wallet.db.entity;

import com.juzix.wallet.entity.IndividualTransactionEntity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class IndividualTransactionInfoEntity extends RealmObject implements Cloneable {

    @PrimaryKey
    private String uuid;
    /**
     * 交易hash
     */
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
     * 转账备注(去掉)
     */
    private String memo;
    /**
     * 当前交易区块
     */
    private long blockNumber;
    /**
     * 交易状态，改为txReceiptStatus
     */
    private boolean completed;
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

    public IndividualTransactionInfoEntity() {

    }

    private IndividualTransactionInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setHash(builder.hash);
        setCreateTime(builder.createTime);
        setWalletName(builder.walletName);
        setFrom(builder.from);
        setTo(builder.to);
        setMemo(builder.memo);
        setCompleted(builder.completed);
        setValue(builder.value);
        setNodeAddress(builder.nodeAddress);
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public static final class Builder {
        private String uuid;
        private String hash;
        private long createTime;
        private String walletName;
        private String from;
        private String to;
        private String memo;
        private long blockNumber;
        private boolean completed;
        private double value;
        private String nodeAddress;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder hash(String val) {
            hash = val;
            return this;
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder walletName(String val) {
            walletName = val;
            return this;
        }

        public Builder from(String val) {
            from = val;
            return this;
        }

        public Builder to(String val) {
            to = val;
            return this;
        }

        public Builder memo(String val) {
            memo = val;
            return this;
        }

        public Builder blockNumber(long val) {
            blockNumber = val;
            return this;
        }

        public Builder completed(boolean val) {
            completed = val;
            return this;
        }

        public Builder value(double val) {
            value = val;
            return this;
        }

        public Builder nodeAddress(String val) {
            nodeAddress = val;
            return this;
        }

        public IndividualTransactionInfoEntity build() {
            return new IndividualTransactionInfoEntity(this);
        }
    }

    public IndividualTransactionEntity buildIndividualTransactionEntity() {
        return new IndividualTransactionEntity.Builder(uuid, createTime, walletName)
                .blockNumber(blockNumber)
                .fromAddress(from)
                .toAddress(to)
                .hash(hash)
                .memo(memo)
                .value(value)
                .completed(completed)
                .nodeAddress(nodeAddress)
                .build();
    }
}
