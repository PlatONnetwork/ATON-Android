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
     * 转账备注
     */
    private String memo;
    /**
     * 当前交易区块
     */
    private long blockNumber;
    /**
     * 交易完成
     */
    private boolean completed;

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
                .build();
    }
}
