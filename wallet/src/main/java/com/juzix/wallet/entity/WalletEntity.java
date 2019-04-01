package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

public class WalletEntity implements Parcelable {

    /**
     * 唯一识别码，与Keystore的id一致
     */
    protected String uuid;
    /**
     * 钱包名称
     */
    protected String name;
    /**
     * 普通钱包即钱包地址，共享钱包即合约地址
     */
    protected String address;
    /**
     * 创建时间
     */
    protected long createTime;
    /**
     * 更新时间
     */
    protected long updateTime;
    /**
     * 钱包余额
     */
    protected double balance;
    /**
     * 钱包头图
     */
    protected String avatar;

    public WalletEntity() {
    }

    protected WalletEntity(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        address = in.readString();
        createTime = in.readLong();
        updateTime = in.readLong();
        balance = in.readDouble();
        avatar = in.readString();
    }

    public static final Creator<WalletEntity> CREATOR = new Creator<WalletEntity>() {
        @Override
        public WalletEntity createFromParcel(Parcel in) {
            return new WalletEntity(in);
        }

        @Override
        public WalletEntity[] newArray(int size) {
            return new WalletEntity[size];
        }
    };

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(uuid) ? 0 : uuid.hashCode();
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public WalletEntity updateBalance(double balance) {
        this.balance = balance;
        return this;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return this.name;
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

    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getAddressWithoutPrefix() {
        if (!TextUtils.isEmpty(address)) {
            if (address.startsWith("0x")) {
                return address.replaceFirst("0x", "");
            }

            return address;
        }

        return null;
    }

    /**
     * 获取钱包地址
     *
     * @return
     */
    public String getPrefixAddress() {
        try {
            if (TextUtils.isEmpty(address)) {
                return "";
            }
            if (address.toLowerCase().startsWith("0x")) {
                return address;
            }
            return "0x" + address;
        } catch (Exception exp) {
            exp.printStackTrace();
            return "";
        }
    }

    @Override
    public String toString() {
        return "WalletEntity{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", balance=" + balance +
                ", avatar='" + avatar + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeDouble(balance);
        dest.writeString(avatar);
    }


}
