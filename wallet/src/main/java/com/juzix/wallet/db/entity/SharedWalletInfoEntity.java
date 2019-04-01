package com.juzix.wallet.db.entity;

import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.ArrayList;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class SharedWalletInfoEntity extends RealmObject {
    //唯一识别码
    @PrimaryKey
    private String uuid;
    /**
     * 钱包名称
     */
    private String name;
    /**
     * 关联的普通钱地址
     */
    private String walletAddress;
    /**
     * 合约地址
     */
    private String contractAddress;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 共享钱包所有人
     */
    private RealmList<OwnerInfoEntity> owner;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 所需签名数
     */
    private int requiredSignNumber;
    /**
     * 钱包头图
     */
    private String avatar;

    public SharedWalletInfoEntity() {
    }

    private SharedWalletInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setName(builder.name);
        setWalletAddress(builder.walletAddress);
        setContractAddress(builder.contractAddress);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setOwnerArrayList(builder.owner);
        setRequiredSignNumber(builder.requiredSignNumber);
        setAvatar(builder.avatar);
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public RealmList<OwnerInfoEntity> getOwner() {
        return owner;
    }

    public void setOwner(RealmList<OwnerInfoEntity> owner) {
        this.owner = owner;
    }

    public ArrayList<OwnerInfoEntity> getOwnerArrayList() {
        ArrayList<OwnerInfoEntity> addressInfoEntities = new ArrayList<>();
        if (this.owner == null) {
            return addressInfoEntities;
        }
        for (OwnerInfoEntity infoEntity : this.owner) {
            addressInfoEntities.add(infoEntity);
        }
        return addressInfoEntities;
    }

    public void setOwnerArrayList(ArrayList<OwnerInfoEntity> owner) {
        if (owner == null) {
            return;
        }
        this.owner = new RealmList<>();
        for (OwnerInfoEntity addressInfoEntity : owner) {
            this.owner.add(addressInfoEntity);
        }
    }

    public int getRequiredSignNumber() {
        return requiredSignNumber;
    }

    public void setRequiredSignNumber(int requiredSignNumber) {
        this.requiredSignNumber = requiredSignNumber;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public SharedWalletEntity buildWalletEntity() {
        SharedWalletEntity.Builder builder = new SharedWalletEntity.Builder();
        builder.uuid(uuid);
        builder.name(name);
        builder.walletAddress(contractAddress);
        builder.creatorAddress(walletAddress);
        builder.createTime(createTime);
        builder.updateTime(updateTime);
        builder.owner(buildAddressEntityList());
        builder.requiredSignNumber(requiredSignNumber);
        builder.avatar(avatar);
        builder.finished(true);
        return builder.build();
    }

    public ArrayList<OwnerEntity> buildAddressEntityList() {
        ArrayList<OwnerEntity> addressEntityArrayList = new ArrayList<>();
        for (OwnerInfoEntity entity : owner) {
            addressEntityArrayList.add(new OwnerEntity(entity.getUuid(), entity.getName(), entity.getAddress()));
        }
        return addressEntityArrayList;
    }

    @Override
    public String toString() {
        return "SharedWalletInfoEntity{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", walletAddress='" + walletAddress + '\'' +
                ", contractAddress='" + contractAddress + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", owner=" + owner +
                ", requiredSignNumber=" + requiredSignNumber +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public static final class Builder {
        private String uuid;
        private String name;
        private String walletAddress;
        private String contractAddress;
        private long createTime;
        private long updateTime;
        private ArrayList<OwnerInfoEntity> owner;
        private int requiredSignNumber;
        private String avatar;
        private String linkWalletAddress;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder walletAddress(String val) {
            walletAddress = val;
            return this;
        }

        public Builder contractAddress(String val) {
            contractAddress = val;
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

        public Builder owner(ArrayList<OwnerInfoEntity> val) {
            owner = val;
            return this;
        }

        public Builder requiredSignNumber(int val) {
            requiredSignNumber = val;
            return this;
        }

        public Builder avatar(String val) {
            avatar = val;
            return this;
        }

        public Builder linkWalletAddress(String val) {
            linkWalletAddress = val;
            return this;
        }

        public SharedWalletInfoEntity build() {
            return new SharedWalletInfoEntity(this);
        }
    }
}
