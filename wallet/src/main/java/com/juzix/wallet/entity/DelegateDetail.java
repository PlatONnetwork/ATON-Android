package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juzix.wallet.db.entity.DelegateDetailEntity;

import java.util.Objects;

public class DelegateDetail implements Parcelable {
    /**
     * 投票节点Id（节点地址 ）
     */
    private String nodeId;

//    /**
//     * 最新的质押交易块高
//     */
//    private String stakingBlockNum;
    /**
     * 节点名称
     */
    private String nodeName;
    /**
     * 机构官网（链接）
     */
    private String website;
    /**
     * 节点头像
     */
    private String url;
    /**
     * 竞选状态:
     * Active —— 活跃中
     * Candidate —— 候选中
     * Exiting —— 退出中
     * Exited —— 已退出
     */
    private String nodeStatus;

//    /**
//     * 赎回中委托
//     */
//    private String redeem;
//    /**
//     * 已锁定委托
//     */
//    private String locked;
//
//    /**
//     * 未锁定委托
//     */
//    private String unLocked;

    /**
     * 已解除委托
     */
    private String released;

    private String walletAddress;

//    /**
//     * 最新委托交易块高 后面新加的
//     */
//    private String delegationBlockNum;

    /**
     *是否为链初始化时内置的候选人
     * 0.7.3 新增字段
     */
    private boolean isInit;

    /**
     * 已委托  单位von
     */
    private String delegated;

    public DelegateDetail() {

    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public String getDelegated() {
        return delegated;
    }

    public void setDelegated(String delegated) {
        this.delegated = delegated;
    }

    //    public String getRedeem() {
//        return redeem;
//    }
//
//    public void setRedeem(String redeem) {
//        this.redeem = redeem;
//    }
//
//    public String getLocked() {
//        return locked;
//    }
//
//    public void setLocked(String locked) {
//        this.locked = locked;
//    }
//
//    public String getUnLocked() {
//        return unLocked;
//    }
//
//    public void setUnLocked(String unLocked) {
//        this.unLocked = unLocked;
//    }

    public String getReleased() {
        return released;
    }

    public void setReleased(String released) {
        this.released = released;
    }

//    public String getStakingBlockNum() {
//        return stakingBlockNum;
//    }
//
//    public void setStakingBlockNum(String stakingBlockNum) {
//        this.stakingBlockNum = stakingBlockNum;
//    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

//    public String getDelegationBlockNum() {
//        return delegationBlockNum;
//    }
//
//    public void setDelegationBlockNum(String delegationBlockNum) {
//        this.delegationBlockNum = delegationBlockNum;
//    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    protected DelegateDetail(Parcel in) {
        nodeId = in.readString();
        nodeName = in.readString();
        website = in.readString();
        nodeStatus = in.readString();
        url = in.readString();
//        redeem = in.readString();
//        locked = in.readString();
        released = in.readString();
//        unLocked = in.readString();
//        stakingBlockNum = in.readString();
        walletAddress = in.readString();
//        delegationBlockNum = in.readString();
        delegated =in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(nodeName);
        dest.writeString(nodeStatus);
        dest.writeString(website);
        dest.writeString(url);
//        dest.writeString(redeem);
//        dest.writeString(locked);
        dest.writeString(released);
//        dest.writeString(unLocked);
//        dest.writeString(stakingBlockNum);
        dest.writeString(walletAddress);
        dest.writeString(delegated);
//        dest.writeString(delegationBlockNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DelegateDetail> CREATOR = new Creator<DelegateDetail>() {
        @Override
        public DelegateDetail createFromParcel(Parcel in) {
            return new DelegateDetail(in);
        }

        @Override
        public DelegateDetail[] newArray(int size) {
            return new DelegateDetail[size];
        }
    };

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        DelegateDetail that = (DelegateDetail) o;
//        return this.stakingBlockNum == that.stakingBlockNum;
//    }
//
//    @Override
//    public int hashCode() {
//        return TextUtils.isEmpty(stakingBlockNum) ? 0 : stakingBlockNum.hashCode();
//    }

    public DelegateDetailEntity toDelegateDetailEntity(){
        DelegateDetailEntity entity = new DelegateDetailEntity();
        entity.setAddress(walletAddress);
        entity.setNodeId(nodeId);
//        entity.setDelegationBlockNum(delegationBlockNum);
        return entity;
    }

}
