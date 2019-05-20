package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class CandidateDetailEntity implements Parcelable {

    /**
     * 节点ID
     */
    private String nodeId;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 质押金(单位:Energon)
     */
    private String deposit;
    /**
     * 质押排名
     */
    private int ranking;
    /**
     * 投票激励:小数
     */
    private String reward;
    /**
     * 机构名称
     */
    private String orgName;
    /**
     * 机构官网
     */
    private String orgWebsite;
    /**
     * 节点简介
     */
    private String intro;
    /**
     * 节点地址
     */
    private String nodeUrl;
    /**
     * 得票数
     */
    private String ticketCount;
    /**
     * 加入时间，单位-毫秒
     */
    private long joinTime;
    /**
     * 竞选状态
     *  nominees—提名节点
     *  validator-验证节点
     *  candidates—候选节点
     */
    private String nodeType;

    public CandidateDetailEntity() {
    }

    protected CandidateDetailEntity(Parcel in) {
        nodeId = in.readString();
        name = in.readString();
        deposit = in.readString();
        ranking = in.readInt();
        reward = in.readString();
        orgName = in.readString();
        orgWebsite = in.readString();
        intro = in.readString();
        nodeUrl = in.readString();
        ticketCount = in.readString();
        joinTime = in.readLong();
        nodeType = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeString(name);
        dest.writeString(deposit);
        dest.writeInt(ranking);
        dest.writeString(reward);
        dest.writeString(orgName);
        dest.writeString(orgWebsite);
        dest.writeString(intro);
        dest.writeString(nodeUrl);
        dest.writeString(ticketCount);
        dest.writeLong(joinTime);
        dest.writeString(nodeType);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CandidateDetailEntity> CREATOR = new Creator<CandidateDetailEntity>() {
        @Override
        public CandidateDetailEntity createFromParcel(Parcel in) {
            return new CandidateDetailEntity(in);
        }

        @Override
        public CandidateDetailEntity[] newArray(int size) {
            return new CandidateDetailEntity[size];
        }
    };

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgWebsite() {
        return orgWebsite;
    }

    public void setOrgWebsite(String orgWebsite) {
        this.orgWebsite = orgWebsite;
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

    public String getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(String ticketCount) {
        this.ticketCount = ticketCount;
    }

    public long getJoinTime() {
        return joinTime;
    }

    public void setJoinTime(long joinTime) {
        this.joinTime = joinTime;
    }

    public NodeType getNodeType() {
        return NodeType.getNodeTypeByName(nodeType);
    }

    public void setNodeType(String nodeType) {
        this.nodeType = nodeType;
    }
}
