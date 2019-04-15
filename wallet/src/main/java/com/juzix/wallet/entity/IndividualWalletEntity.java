package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juzix.wallet.db.entity.IndividualWalletInfoEntity;

public class IndividualWalletEntity extends WalletEntity implements Parcelable, Cloneable {

    /**
     * keystore
     */
    private String key;
    /**
     * 文件名称
     */
    private String keystorePath;

    /**
     * 助记词
     */
    private String mnemonic;

    private IndividualWalletEntity(Builder builder) {
        setUuid(builder.uuid);
        setKey(builder.key);
        setName(builder.name);
        setAddress(builder.address);
        setKeystorePath(builder.keystorePath);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setBalance(builder.balance);
        setAvatar(builder.avatar);
        setMnemonic(builder.mnemonic);
        setNodeAddress(builder.nodeAddress);
    }

    protected IndividualWalletEntity(Parcel in) {
        uuid = in.readString();
        key = in.readString();
        name = in.readString();
        address = in.readString();
        keystorePath = in.readString();
        createTime = in.readLong();
        updateTime = in.readLong();
        balance = in.readDouble();
        avatar = in.readString();
        mnemonic = in.readString();
        nodeAddress = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(key);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(keystorePath);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeDouble(balance);
        dest.writeString(avatar);
        dest.writeString(mnemonic);
        dest.writeString(nodeAddress);
    }

    public static final Creator<IndividualWalletEntity> CREATOR = new Creator<IndividualWalletEntity>() {
        @Override
        public IndividualWalletEntity createFromParcel(Parcel in) {
            return new IndividualWalletEntity(in);
        }

        @Override
        public IndividualWalletEntity[] newArray(int size) {
            return new IndividualWalletEntity[size];
        }
    };

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(uuid) ? 0 : uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == this) {
            return true;
        }

        if (obj instanceof IndividualWalletEntity) {
            IndividualWalletEntity entity = (IndividualWalletEntity) obj;
            return !TextUtils.isEmpty(entity.uuid) && entity.uuid.equals(uuid);
        }

        return false;
    }

    @Override
    public IndividualWalletEntity clone() {

        IndividualWalletEntity walletEntity = null;
        try {
            walletEntity = (IndividualWalletEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return walletEntity;
    }

    @Override
    public IndividualWalletEntity updateBalance(double balance) {
        this.balance = balance;
        return this;
    }

    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeystorePath() {
        return this.keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
    }

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public void setWalletEntity(IndividualWalletEntity walletEntity) {
        setUuid(walletEntity.uuid);
        setKey(walletEntity.key);
        setName(walletEntity.name);
        setAddress(walletEntity.address);
        setKeystorePath(walletEntity.keystorePath);
        setCreateTime(walletEntity.createTime);
        setUpdateTime(walletEntity.updateTime);
        setBalance(walletEntity.balance);
        setAvatar(walletEntity.avatar);
        setMnemonic(walletEntity.mnemonic);
        setNodeAddress(walletEntity.nodeAddress);
    }

    public IndividualWalletInfoEntity buildWalletInfoEntity() {
        IndividualWalletInfoEntity.Builder builder = new IndividualWalletInfoEntity.Builder();
        builder.uuid(getUuid());
        builder.keyJson(getKey());
        builder.name(getName());
        builder.address(getPrefixAddress());
        builder.keystorePath(getKeystorePath());
        builder.createTime(getCreateTime());
        builder.updateTime(getUpdateTime());
        builder.avatar(getAvatar());
        builder.mnemonic(getMnemonic());
        builder.nodeAddress(getNodeAddress());
        return builder.build();
    }

    public static final class Builder {
        private String uuid;
        private String key;
        private String name;
        private String address;
        private String keystorePath;
        private long createTime;
        private long updateTime;
        private double balance;
        private String avatar;
        private String mnemonic;
        private String nodeAddress;

        public Builder() {
        }

        public Builder(IndividualWalletEntity entity) {
            uuid = entity.uuid;
            key = entity.key;
            name = entity.name;
            address = entity.address;
            keystorePath = entity.keystorePath;
            createTime = entity.createTime;
            updateTime = entity.updateTime;
            balance = entity.balance;
            avatar = entity.avatar;
            nodeAddress = entity.nodeAddress;
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder key(String val) {
            key = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder keystorePath(String val) {
            keystorePath = val;
            return this;
        }

        public Builder createTime(long val) {
            createTime = val;
            return this;
        }

        public Builder updateTime(long val) {
            updateTime = val;
            return this;
        }

        public Builder balance(double val) {
            balance = val;
            return this;
        }

        public Builder avatar(String val) {
            avatar = val;
            return this;
        }

        public Builder mnemonic(String val) {
            mnemonic = val;
            return this;
        }

        public Builder nodeAddress(String val) {
            nodeAddress = val;
            return this;
        }

        public IndividualWalletEntity build() {
            return new IndividualWalletEntity(this);
        }
    }

    @Override
    public String toString() {
        return "IndividualWalletEntity{" +
                "key='" + key + '\'' +
                ", keystorePath='" + keystorePath + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", nodeAddress='" + nodeAddress + '\'' +
                ", uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", balance=" + balance +
                ", avatar='" + avatar + '\'' +
                '}';
    }
}
