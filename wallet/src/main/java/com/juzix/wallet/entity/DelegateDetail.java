package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.juzix.wallet.db.entity.DelegateAddressEntity;
import com.juzix.wallet.db.entity.WalletEntity;

import java.util.Objects;

public class DelegateDetail implements Parcelable {
    /**
     * 投票节点Id（节点地址 ）
     */
    private String noadeId;
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

    /**
     * 赎回中委托
     */
    private double redeem;
    /**
     * 已锁定委托
     */
    private double locked;

    /**
     * 未锁定委托
     */
    private double unLocked;

    /**
     * 已解除委托
     */
    private double released;
    /**
     * 排列序号：由区块号和交易索引拼接而成
     */
    private int sequence;


    public DelegateDetail() {

    }

    public String getNoadeId() {
        return noadeId;
    }

    public void setNoadeId(String noadeId) {
        this.noadeId = noadeId;
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

    public double getRedeem() {
        return redeem;
    }

    public void setRedeem(double redeem) {
        this.redeem = redeem;
    }

    public double getLocked() {
        return locked;
    }

    public void setLocked(double locked) {
        this.locked = locked;
    }

    public double getUnLocked() {
        return unLocked;
    }

    public void setUnLocked(double unLocked) {
        this.unLocked = unLocked;
    }

    public double getReleased() {
        return released;
    }

    public void setReleased(double released) {
        this.released = released;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    protected DelegateDetail(Parcel in) {
        noadeId = in.readString();
        nodeName = in.readString();
        website = in.readString();
        nodeStatus = in.readString();
        url = in.readString();
        redeem = in.readDouble();
        locked = in.readDouble();
        released = in.readDouble();
        unLocked = in.readDouble();
        sequence = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(noadeId);
        dest.writeString(nodeName);
        dest.writeString(nodeStatus);
        dest.writeString(website);
        dest.writeString(url);
        dest.writeDouble(redeem);
        dest.writeDouble(locked);
        dest.writeDouble(released);
        dest.writeDouble(unLocked);
        dest.writeInt(sequence);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DelegateDetail that = (DelegateDetail) o;
        return Objects.equals(noadeId, that.noadeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noadeId);
    }

}
