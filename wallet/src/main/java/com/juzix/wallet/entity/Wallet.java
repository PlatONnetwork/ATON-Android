package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juzix.wallet.db.entity.WalletEntity;

public class Wallet implements Parcelable, Comparable<Wallet> {

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
     * 更新时间(更新钱包信息)
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
    /**
     * 节点地址
     */
    protected String chainId;

    public Wallet() {
    }

    protected Wallet(Parcel in) {
        uuid = in.readString();
        name = in.readString();
        address = in.readString();
        createTime = in.readLong();
        updateTime = in.readLong();
        balance = in.readDouble();
        avatar = in.readString();
        key = in.readString();
        keystorePath = in.readString();
        mnemonic = in.readString();
        chainId = in.readString();
    }

    public Wallet(Builder builder) {
        this.uuid = builder.uuid;
        this.name = builder.name;
        this.address = builder.address;
        this.createTime = builder.createTime;
        this.updateTime = builder.updateTime;
        this.balance = builder.balance;
        this.avatar = builder.avatar;
        this.key = builder.key;
        this.keystorePath = builder.keystorePath;
        this.mnemonic = builder.mnemonic;
        this.chainId = builder.chainId;
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
        dest.writeString(key);
        dest.writeString(keystorePath);
        dest.writeString(mnemonic);
        dest.writeString(chainId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Wallet> CREATOR = new Creator<Wallet>() {
        @Override
        public Wallet createFromParcel(Parcel in) {
            return new Wallet(in);
        }

        @Override
        public Wallet[] newArray(int size) {
            return new Wallet[size];
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
        if (obj instanceof Wallet) {
            Wallet entity = (Wallet) obj;
            return entity.getUuid() != null && entity.getUuid().equals(uuid);
        }
        return super.equals(obj);
    }

    public String getAvatar() {
        return avatar;
    }

    public String getExportAvatar() {
        return "icon_export_" + avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Wallet updateBalance(double balance) {
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

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeystorePath() {
        return keystorePath;
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

    /**
     * 是否需要备份，判断提交是，助记词是否为空，为空说明已经备份过了
     * @return
     */
    public boolean isNeedBackup() {
        return !TextUtils.isEmpty(mnemonic);
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "uuid='" + uuid + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", balance=" + balance +
                ", avatar='" + avatar + '\'' +
                ", chainId='" + chainId + '\'' +
                '}';
    }

    /**
     * 按照更新时间升序
     *
     * @param o
     * @return
     */
    @Override
    public int compareTo(Wallet o) {
        return Long.compare(updateTime, o.getUpdateTime());
    }

    public WalletEntity buildWalletInfoEntity() {
        return new WalletEntity.Builder()
                .uuid(getUuid())
                .keyJson(getKey())
                .name(getName())
                .address(getPrefixAddress())
                .keystorePath(getKeystorePath())
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .avatar(getAvatar())
                .mnemonic(getMnemonic())
                .chainId(getChainId()).build();
    }

    public static final class Builder {
        private String uuid;
        private String name;
        private String address;
        private long createTime;
        private long updateTime;
        private double balance;
        private String avatar;
        private String key;
        private String keystorePath;
        private String mnemonic;
        private String chainId;

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder createTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder updateTime(long updateTime) {
            this.updateTime = updateTime;
            return this;
        }

        public Builder balance(double balance) {
            this.balance = balance;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder key(String key) {
            this.key = key;
            return this;
        }

        public Builder keystorePath(String keystorePath) {
            this.keystorePath = keystorePath;
            return this;
        }

        public Builder mnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
            return this;
        }

        public Builder chainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Wallet build() {
            return new Wallet(this);
        }
    }
}
