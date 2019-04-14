package com.juzix.wallet.db.entity;

import android.text.TextUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class OwnerInfoEntity extends RealmObject {

    @PrimaryKey
    private String uuid;
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 钱包名称
     */
    private String name;

    /**
     * 节点地址
     */
    private String nodeAddress;

    public OwnerInfoEntity() {

    }

    public OwnerInfoEntity(String uuid, String address, String name, String nodeAddress) {
        setUuid(uuid);
        setAddress(address);
        setName(name);
        setNodeAddress(nodeAddress);
    }

    private OwnerInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setAddress(builder.address);
        setName(builder.name);
        setNodeAddress(builder.nodeAddress);
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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setNodeAddress(String nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    /**
     * 获取钱包地址
     *
     * @return
     */
    public String getPrefixAddress() {
        try {
            if (TextUtils.isEmpty(address)) {
                return null;
            }
            if (address.toLowerCase().startsWith("0x")) {
                return address;
            }
            return "0x" + address;
        } catch (Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }

    public void setAddress(String address) {
        if (address.toLowerCase().startsWith("0x")) {
            this.address = address;
        } else {
            this.address = "0x" + address;
        }
    }

    @Override
    public String toString() {
        return "OwnerInfoEntity{" +
                "uuid='" + uuid + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", nodeAddress='" + nodeAddress + '\'' +
                '}';
    }

    public static final class Builder {
        private String uuid;
        private String address;
        private String name;
        private String nodeAddress;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
            return this;
        }

        public Builder address(String val) {
            address = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder nodeAddress(String val) {
            nodeAddress = val;
            return this;
        }

        public OwnerInfoEntity build() {
            return new OwnerInfoEntity(this);
        }
    }
}
