package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class VerifyNodeDetail implements Parcelable {

    /**
     * 总押金
     */

    private String deposit;


    /**
     * 节点名称
     */
    private String name;

    /**
     * 机构官网
     */
    private String website;

    /**
     * 节点简介
     */
    private String intro;


    /**
     * 节点头像
     */
    private String url;


    /**
     * 节点地址
     */
    private String nodeUrl;


    /**
     * 竞选状态
     * Active —— 活跃中
     * Candidate —— 候选中
     * Exiting —— 退出中
     * Exited —— 已退出
     */
    private String nodeStatus;


    /**
     * 处罚次数
     */
    private int punishNumber;
    /**
     * 委托次数
     */
    private int delegateNumber;

    /**
     * 接受委托
     */
    private int delegateSum;

    /**
     * 委托者
     */
    private String delegate;

    /**
     * 出块数
     */
    private int blockOutNumber;

    /**
     * 出块率
     */
    private String blockRate;

    /**
     * 预计年化率
     */
    private String ratePA;


    protected VerifyNodeDetail(Parcel in) {
        deposit = in.readString();
        name = in.readString();
        website = in.readString();
        intro = in.readString();
        url = in.readString();
        nodeUrl = in.readString();
        nodeStatus = in.readString();
        punishNumber = in.readInt();
        delegateNumber = in.readInt();
        delegateSum = in.readInt();
        delegate = in.readString();
        blockOutNumber = in.readInt();
        blockRate = in.readString();
        ratePA = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deposit);
        dest.writeString(name);
        dest.writeString(website);
        dest.writeString(intro);
        dest.writeString(url);
        dest.writeString(nodeUrl);
        dest.writeString(nodeStatus);
        dest.writeInt(punishNumber);
        dest.writeInt(delegateNumber);
        dest.writeInt(delegateSum);
        dest.writeString(delegate);
        dest.writeInt(blockOutNumber);
        dest.writeString(blockRate);
        dest.writeString(ratePA);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VerifyNodeDetail> CREATOR = new Creator<VerifyNodeDetail>() {
        @Override
        public VerifyNodeDetail createFromParcel(Parcel in) {
            return new VerifyNodeDetail(in);
        }

        @Override
        public VerifyNodeDetail[] newArray(int size) {
            return new VerifyNodeDetail[size];
        }
    };

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPunishNumber() {
        return punishNumber;
    }

    public void setPunishNumber(int punishNumber) {
        this.punishNumber = punishNumber;
    }

    public int getDelegateNumber() {
        return delegateNumber;
    }

    public void setDelegateNumber(int delegateNumber) {
        this.delegateNumber = delegateNumber;
    }

    public String getDelegate() {
        return delegate;
    }

    public void setDelegate(String delegate) {
        this.delegate = delegate;
    }

    public int getBlockOutNumber() {
        return blockOutNumber;
    }

    public void setBlockOutNumber(int blockOutNumber) {
        this.blockOutNumber = blockOutNumber;
    }

    public String getBlockRate() {
        return blockRate;
    }

    public void setBlockRate(String blockRate) {
        this.blockRate = blockRate;
    }

    public int getDelegateSum() {
        return delegateSum;
    }

    public void setDelegateSum(int delegateSum) {
        this.delegateSum = delegateSum;
    }

    public String getRatePA() {
        return ratePA;
    }

    public void setRatePA(String ratePA) {
        this.ratePA = ratePA;
    }
}


