package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;

/**
 * @author matrixelement
 */
public class IndividualTransactionEntity extends TransactionEntity implements Cloneable, Parcelable {

    /**
     * 交易完成
     */
    private boolean completed;

    public IndividualTransactionEntity() {
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isCompleted() {
        return completed;
    }

    private IndividualTransactionEntity(Builder builder) {
        setUuid(builder.uuid);
        setHash(builder.hash);
        setFromAddress(builder.fromAddress);
        setToAddress(builder.toAddress);
        setCreateTime(builder.createTime);
        setValue(builder.value);
        setBlockNumber(builder.blockNumber);
        setLatestBlockNumber(builder.latestBlockNumber);
        setWalletName(builder.walletName);
        setEnergonPrice(builder.energonPrice);
        setMemo(builder.memo);
        setCompleted(builder.completed);
        setNodeAddress(builder.nodeAddress);
    }

    protected IndividualTransactionEntity(Parcel in) {
        uuid = in.readString();
        hash = in.readString();
        fromAddress = in.readString();
        toAddress = in.readString();
        createTime = in.readLong();
        value = in.readDouble();
        blockNumber = in.readLong();
        latestBlockNumber = in.readLong();
        walletName = in.readString();
        energonPrice = in.readDouble();
        memo = in.readString();
        completed = in.readByte() != 0;
        nodeAddress = in.readString();
    }

    public static final Creator<IndividualTransactionEntity> CREATOR = new Creator<IndividualTransactionEntity>() {
        @Override
        public IndividualTransactionEntity createFromParcel(Parcel in) {
            return new IndividualTransactionEntity(in);
        }

        @Override
        public IndividualTransactionEntity[] newArray(int size) {
            return new IndividualTransactionEntity[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(hash);
        dest.writeString(fromAddress);
        dest.writeString(toAddress);
        dest.writeLong(createTime);
        dest.writeDouble(value);
        dest.writeLong(blockNumber);
        dest.writeLong(latestBlockNumber);
        dest.writeString(walletName);
        dest.writeDouble(energonPrice);
        dest.writeString(memo);
        dest.writeByte((byte) (completed ? 1 : 0));
        dest.writeString(nodeAddress);
    }

    public static final class Builder {
        private String uuid;
        private String hash;
        private String fromAddress;
        private String toAddress;
        private long createTime;
        private double value;
        private long blockNumber;
        private long latestBlockNumber;
        private String walletName;
        private double energonPrice;
        private String memo;
        private boolean completed;
        private String nodeAddress;

        public Builder(String uuid, long createTime, String walletName) {
            this.uuid = uuid;
            this.createTime = createTime;
            this.walletName = walletName;
        }

        public Builder hash(String val) {
            hash = val;
            return this;
        }

        public Builder fromAddress(String val) {
            fromAddress = val;
            return this;
        }

        public Builder toAddress(String val) {
            toAddress = val;
            return this;
        }

        public Builder value(double val) {
            value = val;
            return this;
        }

        public Builder blockNumber(long va1) {
            blockNumber = va1;
            return this;
        }

        public Builder latestBlockNumber(long va1) {
            latestBlockNumber = va1;
            return this;
        }

        public Builder energonPrice(double va1) {
            energonPrice = va1;
            return this;
        }

        public Builder memo(String va1) {
            memo = va1;
            return this;
        }

        public Builder completed(boolean val) {
            completed = val;
            return this;
        }

        public Builder nodeAddress(String val) {
            nodeAddress = val;
            return this;
        }

        public IndividualTransactionEntity build() {
            return new IndividualTransactionEntity(this);
        }
    }

    @Override
    public IndividualTransactionEntity clone() {
        IndividualTransactionEntity transactionEntity = null;
        try {
            transactionEntity = (IndividualTransactionEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return transactionEntity;
    }

    @Override
    public TransactionStatus getTransactionStatus() {
        return completed ? TransactionStatus.SUCCEED : TransactionStatus.PENDING;
    }

    public IndividualTransactionInfoEntity buildIndividualTransactionInfoEntity() {
        return new IndividualTransactionInfoEntity.Builder()
                .uuid(uuid)
                .blockNumber(blockNumber)
                .completed(completed)
                .from(fromAddress)
                .to(toAddress)
                .hash(hash)
                .createTime(createTime)
                .walletName(walletName)
                .memo(memo)
                .value(value)
                .nodeAddress(nodeAddress)
                .build();
    }
}
