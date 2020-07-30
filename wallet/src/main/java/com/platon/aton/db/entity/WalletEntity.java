package com.platon.aton.db.entity;


import com.platon.aton.component.widget.togglebutton.Spring;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.Bech32Address;
import com.platon.aton.entity.Wallet;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WalletEntity extends RealmObject {

    /**
     * 唯一识别码，与Keystore的id一致
     */
    @PrimaryKey
    private String uuid;
    /**
     * 是否分层钱包：默认false
     */
    private boolean isHD;
    /**
     * 钱包路径index：HD的index值，普通钱包和HD母钱包都是0
     */
    private int pathIndex;
    /**
     * 默认的排序索引, 和钱包管理顺序相同，从0开始，数据小的排前面
     */
    private int sortIndex;
    /**
     * 当前HD钱包选中的索引
     */
    private int selectedIndex;
    /**
     * 如果是HD子钱包，为HD父钱包的 uuid字段
     */
    private String parentId;
    /**
     * 钱包深度：HD目录和普通钱包为0，HD子钱包为1
     */
    private int depth;

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
     * betch32处理钱包地址
     */
    private String mainNetAddress;
    private String testNetAddress;
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
    private String chainId;
    /**
     * 是否已备份
     */
    private boolean backedUp;
    /**
     * 是否首页展示
     */
    private boolean isShow;

    public WalletEntity() {

    }

    private WalletEntity(Builder builder) {
        setUuid(builder.uuid);
        setHD(builder.isHD);
        setPathIndex(builder.pathIndex);
        setSortIndex(builder.sortIndex);
        setSelectedIndex(builder.selectedIndex);
        setParentId(builder.parentId);
        setDepth(builder.depth);
        setKeyJson(builder.keyJson);
        setName(builder.name);
        setAddress(builder.address);
        setMainNetAddress(builder.mainNetAddress);
        setTestNetAddress(builder.testNetAddress);
        setKeystorePath(builder.keystorePath);
        setCreateTime(builder.createTime);
        setUpdateTime(builder.updateTime);
        setAvatar(builder.avatar);
        setMnemonic(builder.mnemonic);
        setChainId(builder.chainId);
        setBackedUp(builder.backedUp);
        setShow(builder.isShow);
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

    public String getMainNetAddress() {
        return mainNetAddress;
    }

    public void setMainNetAddress(String mainNetAddress) {
        this.mainNetAddress = mainNetAddress;
    }

    public String getTestNetAddress() {
        return testNetAddress;
    }

    public void setTestNetAddress(String testNetAddress) {
        this.testNetAddress = testNetAddress;
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

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public boolean isBackedUp() {
        return backedUp;
    }

    public void setBackedUp(boolean backedUp) {
        this.backedUp = backedUp;
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

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    public static final class Builder {
        private String uuid;
        private boolean isHD;
        private int pathIndex;
        private int sortIndex;
        private int selectedIndex;
        private String parentId;
        private int depth;
        private String keyJson;
        private String name;
        private String address;
        private String mainNetAddress;
        private String testNetAddress;
        private String keystorePath;
        private long createTime;
        private long updateTime;
        private String avatar;
        private String mnemonic;
        private String chainId;
        private boolean backedUp;
        private boolean isShow;

        public Builder() {
        }

        public Builder uuid(String val) {
            uuid = val;
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

        public Builder mainNetAddress(String val) {
            mainNetAddress = val;
            return this;
        }

        public Builder testNetAddress(String val) {
            testNetAddress = val;
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

        public Builder chainId(String val) {
            chainId = val;
            return this;
        }

        public Builder backedUp(boolean val) {
            backedUp = val;
            return this;
        }

        public Builder isShow(boolean val) {
            isShow = val;
            return this;
        }

        public WalletEntity build() {
            return new WalletEntity(this);
        }
    }

    public Wallet buildWallet() {

        Bech32Address bech32Address = new Bech32Address(mainNetAddress,testNetAddress);
        return new Wallet.Builder()
                .uuid(uuid)
                .isHD(isHD)
                .pathIndex(pathIndex)
                .sortIndex(sortIndex)
                .selectedIndex(selectedIndex)
                .parentId(parentId)
                .depth(depth)
                .key(keyJson)
                .name(name)
                .address(address)
                .betch32Address(bech32Address)
                .keystorePath(keystorePath)
                .createTime(createTime)
                .updateTime(updateTime)
                .parentWalletName("")
                .accountBalance(new AccountBalance())
                .avatar(avatar)
                .mnemonic(mnemonic)
                .chainId(chainId)
                .backedUp(backedUp)
                .backedUpPrompt(true)
                .isShow(isShow)
                .build();
    }

    @Override
    public String toString() {
        return "WalletEntity{" +
                "uuid='" + uuid + '\'' +
                ", isHD=" + isHD +
                ", pathIndex=" + pathIndex +
                ", sortIndex=" + sortIndex +
                ", selectedIndex=" + selectedIndex +
                ", parentId='" + parentId + '\'' +
                ", depth=" + depth +
                ", keyJson='" + keyJson + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", mainNetAddress='" + mainNetAddress + '\'' +
                ", testNetAddress='" + testNetAddress + '\'' +
                ", keystorePath='" + keystorePath + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", avatar='" + avatar + '\'' +
                ", mnemonic='" + mnemonic + '\'' +
                ", chainId='" + chainId + '\'' +
                ", backedUp=" + backedUp +
                ", isShow=" + isShow +
                '}';
    }
}
