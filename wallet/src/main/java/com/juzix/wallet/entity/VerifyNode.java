package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class VerifyNode implements Parcelable {


    /**
     * 节点ID
     */
    private String nodeId;

    /**
     * 质押排名
     */
    private int ranking;

    /**
     * 节点名称
     */
    private String name;
    /**
     * 总押金
     */

    private String deposit;

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
     * 预计年化率
     */
    private String ratePA;

    /**
     * 处罚次数
     */
    private int punishNumber;
    /**
     * 委托次数
     */
    private int delegateNumber;
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
     * 排列序号：由区块号和交易索引拼接而成
     */
    private int sequence;


    public VerifyNode() {

    }


    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public String getRatePA() {
        return ratePA;
    }

    public void setRatePA(String ratePA) {
        this.ratePA = ratePA;
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    protected VerifyNode(Parcel in) {
        nodeId = in.readString();
        ranking = in.readInt();
        name = in.readString();
        deposit = in.readString();
        website = in.readString();
        intro = in.readString();
        url = in.readString();
        nodeUrl = in.readString();
        nodeStatus = in.readString();
        ratePA = in.readString();
        punishNumber = in.readInt();
        delegateNumber = in.readInt();
        delegate = in.readString();
        blockOutNumber = in.readInt();
        blockRate = in.readString();
        sequence = in.readInt();
    }


    public static final Creator<VerifyNode> CREATOR = new Creator<VerifyNode>() {
        @Override
        public VerifyNode createFromParcel(Parcel in) {
            return new VerifyNode(in);
        }

        @Override
        public VerifyNode[] newArray(int size) {
            return new VerifyNode[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeInt(ranking);
        dest.writeString(name);
        dest.writeString(deposit);
        dest.writeString(website);
        dest.writeString(intro);
        dest.writeString(url);
        dest.writeString(nodeUrl);
        dest.writeString(nodeStatus);
        dest.writeString(ratePA);
        dest.writeInt(punishNumber);
        dest.writeInt(delegateNumber);
        dest.writeString(delegate);
        dest.writeInt(blockOutNumber);
        dest.writeString(blockRate);
        dest.writeInt(sequence);

    }

    @Override
    public int describeContents() {
        return 0;
    }


}
