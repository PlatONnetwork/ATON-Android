package com.platon.aton.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.engine.WalletManager;
import com.platon.framework.utils.LogUtils;

public class Wallet implements Parcelable, Comparable<Wallet>, Nullable, Cloneable {

    /**
     * 唯一识别码，与Keystore的id一致
     */
    protected String uuid;
    /**
     * 是否分层钱包：默认false
     */
    protected boolean isHD;
    /**
     * 钱包路径index：HD的index值，普通钱包和HD母钱包都是0
     */
    protected int pathIndex;
    /**
     * 默认的排序索引, 和钱包管理顺序相同，从0开始，数据小的排前面
     */
    protected int sortIndex;
    /**
     * 当前HD钱包选中的索引
     */
    protected int selectedIndex;
    /**
     * 如果是HD子钱包，为HD父钱包的 uuid字段
     */
    protected String parentId;
    /**
     * 钱包深度：HD目录和普通钱包为0，HD子钱包为1
     */
    protected int depth;

    /**
     * 钱包名称
     */
    protected String name;
    /**
     * 普通钱包即钱包地址，共享钱包即合约地址
     */
    protected String address;
    /**
     * Betch32处理后钱包地址
     */
    protected  Bech32Address bech32Address;
    /**
     * 创建时间
     */
    protected long createTime;
    /**
     * 更新时间(更新钱包信息),用于排序的
     */
    protected long updateTime;
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
    /**
     * 是否已经备份了
     */
    protected boolean backedUp;

    protected AccountBalance accountBalance;
    /**
     * 是否被选中
     */
    protected boolean selected;
    /**
     * 展示首页提示
     */
    protected boolean backedUpPrompt;
    /**
     * 关联母钱包名称
     */
    protected String parentWalletName;
    /**
     * 是否首页展示
     */
    protected boolean isShow;

    public Wallet() {

    }


    protected Wallet(Parcel in) {
        uuid = in.readString();
        isHD = in.readByte() != 0;
        pathIndex = in.readInt();
        sortIndex = in.readInt();
        selectedIndex = in.readInt();
        parentId = in.readString();
        depth = in.readInt();
        name = in.readString();
        address = in.readString();
        bech32Address = in.readParcelable(Bech32Address.class.getClassLoader());
        createTime = in.readLong();
        updateTime = in.readLong();
        avatar = in.readString();
        key = in.readString();
        keystorePath = in.readString();
        mnemonic = in.readString();
        chainId = in.readString();
        backedUp = in.readByte() != 0;
        accountBalance = in.readParcelable(AccountBalance.class.getClassLoader());
        selected = in.readByte() != 0;
        backedUpPrompt = in.readByte() != 0;
        parentWalletName = in.readString();
        isShow = in.readByte() != 0;
    }

    public Wallet(Builder builder) {
        this.uuid = builder.uuid;
        this.isHD = builder.isHD;
        this.pathIndex = builder.pathIndex;
        this.sortIndex = builder.sortIndex;
        this.selectedIndex = builder.selectedIndex;
        this.parentId = builder.parentId;
        this.depth = builder.depth;
        this.name = builder.name;
        this.address = builder.address;
        this.bech32Address = builder.bech32Address;
        this.createTime = builder.createTime;
        this.updateTime = builder.updateTime;
        this.avatar = builder.avatar;
        this.key = builder.key;
        this.keystorePath = builder.keystorePath;
        this.mnemonic = builder.mnemonic;
        this.chainId = builder.chainId;
        this.backedUp = builder.backedUp;
        this.accountBalance = builder.accountBalance;
        this.selected = builder.selected;
        this.backedUpPrompt = builder.backedUpPrompt;
        this.parentWalletName = builder.parentWalletName;
        this.isShow = builder.isShow;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeByte((byte) (isHD ? 1 : 0));
        dest.writeInt(pathIndex);
        dest.writeInt(sortIndex);
        dest.writeInt(selectedIndex);
        dest.writeString(parentId);
        dest.writeInt(depth);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeParcelable(bech32Address, flags);
        dest.writeLong(createTime);
        dest.writeLong(updateTime);
        dest.writeString(avatar);
        dest.writeString(key);
        dest.writeString(keystorePath);
        dest.writeString(mnemonic);
        dest.writeString(chainId);
        dest.writeByte((byte) (backedUp ? 1 : 0));
        dest.writeParcelable(accountBalance, flags);
        dest.writeByte((byte) (selected ? 1 : 0));
        dest.writeByte((byte) (backedUpPrompt ? 1 : 0));
        dest.writeByte((byte) (isShow ? 1 : 0));
        dest.writeString(parentWalletName);
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

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public boolean isHD() {
        return isHD;
    }

    public void setHD(boolean HD) {
        isHD = HD;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(int pathIndex) {
        this.pathIndex = pathIndex;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
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

    public Bech32Address getBech32Address() {
        return bech32Address;
    }

    public void setBech32Address(Bech32Address bech32Address) {
        this.bech32Address = bech32Address;
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

    public AccountBalance getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(AccountBalance accountBalance) {
        this.accountBalance = accountBalance;
    }

    public String getFreeBalance() {
        return accountBalance == null ? "0" : accountBalance.getFree();
    }

    public String getLockBalance() {
        return accountBalance == null ? "0" : accountBalance.getLock();
    }

    public boolean isBackedUp() {
        return backedUp;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getParentWalletName() {
        return parentWalletName;
    }

    public void setParentWalletName(String parentWalletName) {
        this.parentWalletName = parentWalletName;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    /**
     * 是否可以备份
     *
     * @return
     */
    public boolean isBackedUpEnabled() {
        return !TextUtils.isEmpty(mnemonic);
    }

    /**
     * 是否需要备份,未备份并且助记词不为空
     *
     * @return
     */
    public boolean isBackedUpNeeded() {
        return !TextUtils.isEmpty(mnemonic) && !isBackedUp();
    }

    /**
     * 是否可以删除
     *
     * @return
     */
    public boolean isDeletedEnabled() {
        return isBackedUp();
    }

    public void setBackedUp(boolean backedUp) {
        this.backedUp = backedUp;
    }

    public boolean isBackedUpPrompt() {
        return backedUpPrompt;
    }

    public void setBackedUpPrompt(boolean backedUpPrompt) {
        this.backedUpPrompt = backedUpPrompt;
    }

    /**
     * 展示备份的提示
     *
     * @return
     */
    public boolean showBackedUpPrompt() {
        return backedUpPrompt && isBackedUpNeeded();
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
             if(getBech32Address() == null){
                 return "";
             }
            if(WalletManager.getInstance().isMainNetWalletAddress()){
                return getBech32Address().getMainnet();
            }else{
                return getBech32Address().getTestnet();
            }


          /*  if (TextUtils.isEmpty(address)) {
                return "";
            }
            if (address.toLowerCase().startsWith("0x")) {
                return address;
            }
            return "0x" + address;*/
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return "";
        }
    }

    /**
     * 获取原始0x钱包地址
     * @return
     */
    public String getOriginalAddress(){
        try {
           if (TextUtils.isEmpty(address)) {
                return "";
            }
            if (address.toLowerCase().startsWith("0x")) {
                return address;
            }
            return "0x" + address;
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return "";
        }
    }

    @Override
    public String toString() {
        return "Wallet{" +
                "uuid='" + uuid + '\'' +
                ", isHD=" + isHD +
                ", pathIndex=" + pathIndex +
                ", sortIndex=" + sortIndex +
                ", selectedIndex=" + selectedIndex +
                ", parentId='" + parentId + '\'' +
                ", depth=" + depth +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", bech32Address=" + bech32Address +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", avatar='" + avatar + '\'' +
                ", key='" + key + '\'' +
                ", keystorePath='" + keystorePath + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", chainId='" + chainId + '\'' +
                ", backedUp=" + backedUp +
                ", accountBalance=" + accountBalance +
                ", selected=" + selected +
                ", backedUpPrompt=" + backedUpPrompt +
                ", parentWalletName='" + parentWalletName + '\'' +
                ", isShow=" + isShow +
                '}';
    }

    /**
     * 按照更新时间升序
     * @param o
     * @return
     */
    @Override
    public int compareTo(Wallet o) {
        if (updateTime != 0) {
            return Long.compare(updateTime, o.getUpdateTime());
        } else {
            return Long.compare(createTime, o.getCreateTime());
        }
    }


    public WalletEntity buildWalletInfoEntity() {
        return new WalletEntity.Builder()
                .uuid(getUuid())
                .isHD(isHD())
                .pathIndex(getPathIndex())
                .sortIndex(getSortIndex())
                .selectedIndex(getSelectedIndex())
                .parentId(getParentId())
                .depth(getDepth())
                .keyJson(getKey())
                .name(getName())
                .address(getOriginalAddress())
                .mainNetAddress(getBech32Address() != null ? getBech32Address().getMainnet() : "")
                .testNetAddress(getBech32Address() != null ? getBech32Address().getTestnet() : "")
                .keystorePath(getKeystorePath())
                .createTime(getCreateTime())
                .updateTime(getUpdateTime())
                .avatar(getAvatar())
                .mnemonic(getMnemonic())
                .chainId(getChainId())
                .backedUp(isBackedUp())
                .isShow(isShow())
                .build();
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public static Wallet getNullInstance() {
        return NullWallet.getInstance();
    }

    @Override
    public Wallet clone() {
        Wallet wallet = null;
        try {
            wallet = (Wallet) super.clone();
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            wallet = NullWallet.getInstance();
        }
        return wallet;
    }

    /**
     * 是否是观察者钱包
     *
     * @return
     */
    public boolean isObservedWallet() {
        return TextUtils.isEmpty(key) && depth == 0;
    }


    public static final class Builder {
        private String uuid;
        private boolean isHD;
        private int pathIndex;
        private int sortIndex;
        private int selectedIndex;
        private String parentId;
        private int depth;
        private String name;
        private String address;
        private Bech32Address bech32Address;
        private long createTime;
        private long updateTime;
        private String avatar;
        private String key;
        private String keystorePath;
        private String mnemonic;
        private String chainId;
        protected boolean backedUp;
        private AccountBalance accountBalance;
        private boolean selected;
        private boolean backedUpPrompt;
        private boolean isShow;
        private String parentWalletName;

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder isHD(boolean val){
            isHD = val;
            return this;
        }

        public Builder pathIndex(int val){
            pathIndex = val;
            return this;
        }

        public Builder sortIndex(int val){
            sortIndex = val;
            return this;
        }

        public Builder selectedIndex(int val){
            selectedIndex = val;
            return this;
        }

        public Builder parentId(String val){
            parentId = val;
            return this;
        }

        public Builder depth(int val){
            depth = val;
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

        public Builder betch32Address(Bech32Address bech32Address) {
            this.bech32Address = bech32Address;
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

        public Builder backedUp(boolean backedUp) {
            this.backedUp = backedUp;
            return this;
        }

        public Builder accountBalance(AccountBalance accountBalance) {
            this.accountBalance = accountBalance;
            return this;
        }

        public Builder selected(boolean selected) {
            this.selected = selected;
            return this;
        }

        public Builder backedUpPrompt(boolean backedUpPrompt) {
            this.backedUpPrompt = backedUpPrompt;
            return this;
        }

        public Builder parentWalletName(String parentWalletName) {
            this.parentWalletName = parentWalletName;
            return this;
        }

        public Builder isShow(boolean isShow) {
            this.isShow = isShow;
            return this;
        }


        public Wallet build() {
            return new Wallet(this);
        }
    }
}
