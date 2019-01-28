package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;

/**
 * @author matrixelement
 */
public class IndividualTransactionEntity extends TransactionEntity implements Cloneable, Parcelable {

    public IndividualTransactionEntity() {
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
        if (blockNumber == 0) {
            return TransactionStatus.PENDING;
        } else {
            long signedBlockNumber = latestBlockNumber - blockNumber;
            if (signedBlockNumber >= 1) {
                return TransactionStatus.SUCCEED;
            } else {
                return TransactionStatus.PENDING;
            }
        }
    }

    public IndividualTransactionInfoEntity buildIndividualTransactionInfoEntity() {

        IndividualTransactionInfoEntity individualTransactionInfoEntity = new IndividualTransactionInfoEntity();
        individualTransactionInfoEntity.setUuid(uuid);
        individualTransactionInfoEntity.setBlockNumber(blockNumber);
        individualTransactionInfoEntity.setCreateTime(createTime);
        individualTransactionInfoEntity.setFrom(fromAddress);
        individualTransactionInfoEntity.setTo(toAddress);
        individualTransactionInfoEntity.setHash(hash);
        individualTransactionInfoEntity.setMemo(memo);
        individualTransactionInfoEntity.setWalletName(walletName);

        return individualTransactionInfoEntity;
    }
}
