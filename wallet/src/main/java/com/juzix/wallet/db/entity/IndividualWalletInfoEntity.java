package com.juzix.wallet.db.entity;


import com.juzix.wallet.entity.IndividualWalletEntity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class IndividualWalletInfoEntity extends RealmObject {

    /**
     * 唯一识别码，与Keystore的id一致
     */
    @PrimaryKey
    private String uuid;
    /**
     * keystore
     */
    private String keyJson;
    /**
     * 钱包名称
     */
    private String name;
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 文件名称
     */
    private String keystorePath;
    /**
     * 创建时间
     */
    private long createTime;
    /**
     * 更新时间
     */
    private long updateTime;
    /**
     * 钱包头图
     */
    private String avatar;
    /**
     * 助记词
     */
    private String mnemonic;
    /**
     * 节点地址
     */
    private String nodeAddress;

    public IndividualWalletInfoEntity() {

    }

    private IndividualWalletInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setKeyJson(builder.keyJson);
        setName(builder.name);
        setAddress(builder.address);
        setKeystorePath(builder.keystorePath);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setAvatar(builder.avatar);
        setMnemonic(builder.mnemonic);
        setNodeAddress(builder.nodeAddress);
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getKeyJson() {
        return keyJson;
    }

    public void setKeyJson(String keyJson) {
        this.keyJson = keyJson;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKeystorePath() {
        return keystorePath;
    }

    public void setKeystorePath(String keystorePath) {
        this.keystorePath = keystorePath;
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

    public String getMnemonic() {
        return mnemonic;
    }

    public void setMnemonic(String mnemonic) {
        this.mnemonic = mnemonic;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public static final class Builder {
        private String uuid;
        private String keyJson;
        private String name;
        public String address;
        private String keystorePath;
        private long createTime;
        private long updateTime;
        private String avatar;
        private String mnemonic;
        private String nodeAddress;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder keyJson(String val) {
            keyJson = val;
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

        public IndividualWalletInfoEntity build() {
            return new IndividualWalletInfoEntity(this);
        }
    }

    public IndividualWalletEntity buildWalletEntity() {
        IndividualWalletEntity.Builder builder = new IndividualWalletEntity.Builder();
        builder.uuid(uuid);
        builder.key(keyJson);
        builder.name(name);
        builder.address(address);
        builder.keystorePath(keystorePath);
        builder.createTime(createTime);
        builder.updateTime(updateTime);
        builder.avatar(avatar);
        builder.mnemonic(mnemonic);
        builder.nodeAddress(nodeAddress);
        return builder.build();
    }

    @Override
    public String toString() {
        return "IndividualWalletInfoEntity{" +
                "uuid='" + uuid + '\'' +
                ", keyJson='" + keyJson + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", keystorePath='" + keystorePath + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", avatar='" + avatar + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", nodeAddress='" + nodeAddress + '\'' +
                '}';
    }
}
