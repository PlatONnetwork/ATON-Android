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

    public OwnerInfoEntity() {

    }

    public OwnerInfoEntity(String uuid, String address, String name) {
        setUuid(uuid);
        setAddress(address);
        setName(name);
    }

    private OwnerInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setAddress(builder.address);
        setName(builder.name);
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

    public OwnerInfoEntity buildAddressEntity() {
        OwnerInfoEntity.Builder builder = new OwnerInfoEntity.Builder();
        builder.uuid(uuid);
        builder.name(name);
        builder.address(address);
        return builder.build();
    }

    @Override
    public String toString() {
        return "WalletInfoEntity{" +
                " name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }

    public static final class Builder {
        private String uuid;
        private String address;
        private String name;

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

        public OwnerInfoEntity build() {
            return new OwnerInfoEntity(this);
        }
    }
}
