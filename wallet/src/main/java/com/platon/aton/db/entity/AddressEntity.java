package com.platon.aton.db.entity;

import android.text.TextUtils;

import com.platon.framework.utils.LogUtils;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class AddressEntity extends RealmObject {

    /**
     * 钱包地址
     */
    @PrimaryKey
    private String uuid;

    private String address;
    /**
     * 钱包名称
     */
    private String name;
    /**
     * 钱包头像
     */
    private String avatar;

    public AddressEntity() {

    }

    public AddressEntity(String uuid, String address, String name, String avatar) {
        setUuid(uuid);
        setAddress(address);
        setName(name);
        setAvatar(avatar);
    }

    private AddressEntity(Builder builder) {
        setUuid(builder.uuid);
        setAddress(builder.address);
        setName(builder.name);
        setAvatar(builder.avatar);
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
            return null;
        }
    }

    public void setAddress(String address) {
       /* if (address.toLowerCase().startsWith("0x")) {
            this.address = address;
        } else {
            this.address = "0x" + address;
        }*/
        this.address = address;
    }

    @Override
    public String toString() {
        return "AddressEntity{" +
                "uuid='" + uuid + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    public static final class Builder {
        private String uuid;
        private String address;
        private String name;
        private String avatar;

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

        public Builder avatar(String val) {
            avatar = val;
            return this;
        }

        public AddressEntity build() {
            return new AddressEntity(this);
        }
    }
}
