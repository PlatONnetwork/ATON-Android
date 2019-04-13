package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author matrixelement
 */
public class VoteTransactionEntity extends TransactionEntity implements Cloneable, Parcelable {

    public static final int                            STATUS_PENDING = 0;
    public static final int                            STATUS_SUCCESS = 1;
    public static final int                            STATUS_FAILED  = 2;
    public static final Creator<VoteTransactionEntity> CREATOR        = new Creator<VoteTransactionEntity>() {
        @Override
        public VoteTransactionEntity createFromParcel(Parcel in) {
            return new VoteTransactionEntity(in);
        }

        @Override
        public VoteTransactionEntity[] newArray(int size) {
            return new VoteTransactionEntity[size];
        }
    };

    private int status;

    public VoteTransactionEntity() {
    }

    private VoteTransactionEntity(Builder builder) {
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
        setStatus(builder.status);
    }

    protected VoteTransactionEntity(Parcel in) {
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
        status = in.readInt();
    }

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
        dest.writeInt(status);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public VoteTransactionEntity clone() {
        VoteTransactionEntity transactionEntity = null;
        try {
            transactionEntity = (VoteTransactionEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return transactionEntity;
    }

    @Override
    public TransactionStatus getTransactionStatus() {
        if (status == STATUS_SUCCESS){
            return TransactionStatus.SUCCEED;
        }
        if (status == STATUS_FAILED){
            return TransactionStatus.FAILED;
        }
        return TransactionStatus.PENDING;
    }

    public boolean isVoter(String walletAddress){
        return walletAddress != null && walletAddress.equals(fromAddress);
    }

    public static final class Builder {
        private String uuid;
        private String hash;
        private String fromAddress;
        private String toAddress;
        private long   createTime;
        private double value;
        private long   blockNumber;
        private long   latestBlockNumber;
        private String walletName;
        private double energonPrice;
        private String memo;
        private int status;

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

        public Builder status(int va1) {
            status = va1;
            return this;
        }

        public VoteTransactionEntity build() {
            return new VoteTransactionEntity(this);
        }
    }
}
