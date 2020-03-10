package com.platon.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.platon.wallet.R;

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
    private String nodeId;


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
     * 接受委托
     */
    private String delegateSum;

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
    private String delegatedRatePA;

    /**
     * 是否为链初始化时内置的候选人
     */
    private boolean isInit;
    /**
     * 是否共识中
     */
    private boolean isConsensus;

    /**
     * 委托奖励比例 乘以10000
     */
    private String delegatedRewardPer;
    /**
     * 节点累计的奖励  单位von   1LAT(ETH)=1000000000000000000von(wei)
     */
    private String cumulativeReward;
    /**
     * 当前周期委托年化率和上个周期委托年化率比较 0: 相对  1: 大于  -1：小于
     */
    private String delegatedRatePATrend;

    public VerifyNodeDetail() {

    }

    protected VerifyNodeDetail(Parcel in) {
        deposit = in.readString();
        name = in.readString();
        website = in.readString();
        intro = in.readString();
        url = in.readString();
        nodeId = in.readString();
        nodeStatus = in.readString();
        punishNumber = in.readInt();
        delegateSum = in.readString();
        delegate = in.readString();
        blockOutNumber = in.readInt();
        blockRate = in.readString();
        delegatedRatePA = in.readString();
        isInit = in.readByte() != 0;
        isConsensus = in.readByte() != 0;
        delegatedRewardPer = in.readString();
        cumulativeReward = in.readString();
        delegatedRatePATrend = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deposit);
        dest.writeString(name);
        dest.writeString(website);
        dest.writeString(intro);
        dest.writeString(url);
        dest.writeString(nodeId);
        dest.writeString(nodeStatus);
        dest.writeInt(punishNumber);
        dest.writeString(delegateSum);
        dest.writeString(delegate);
        dest.writeInt(blockOutNumber);
        dest.writeString(blockRate);
        dest.writeString(delegatedRatePA);
        dest.writeByte((byte) (isInit ? 1 : 0));
        dest.writeByte((byte) (isConsensus ? 1 : 0));
        dest.writeString(delegatedRewardPer);
        dest.writeString(cumulativeReward);
        dest.writeString(delegatedRatePATrend);
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

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
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

    public String getDelegateSum() {
        return delegateSum;
    }

    public void setDelegateSum(String delegateSum) {
        this.delegateSum = delegateSum;
    }

    public String getDelegatedRatePA() {
        return delegatedRatePA;
    }

    public void setDelegatedRatePA(String delegatedRatePA) {
        this.delegatedRatePA = delegatedRatePA;
    }

    public String getDelegatedRewardPer() {
        return delegatedRewardPer;
    }

    public void setDelegatedRewardPer(String delegatedRewardPer) {
        this.delegatedRewardPer = delegatedRewardPer;
    }

    public String getCumulativeReward() {
        return cumulativeReward;
    }

    public void setCumulativeReward(String cumulativeReward) {
        this.cumulativeReward = cumulativeReward;
    }

    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
    }

    public boolean isConsensus() {
        return isConsensus;
    }

    public void setConsensus(boolean consensus) {
        isConsensus = consensus;
    }

    public String getDelegatedRatePATrend() {
        return delegatedRatePATrend;
    }

    public void setDelegatedRatePATrend(String delegatedRatePATrend) {
        this.delegatedRatePATrend = delegatedRatePATrend;
    }

    public int getNodeStatusDescRes() {

        switch (nodeStatus) {
            case NodeStatus.ACTIVE:
                return isConsensus ? R.string.validators_verifying : R.string.validators_active;
            case NodeStatus.CANDIDATE:
                return R.string.validators_state_candidate;
            case NodeStatus.EXITING:
                return R.string.validators_state_exiting;
            default:
                return R.string.validators_state_exited;
        }
    }

    public boolean isDelegatedRatePATrendRose() {
        return "1".equals(delegatedRatePATrend);
    }

    public boolean isShowDelegatedRatePATrend() {
        return !"0".equals(delegatedRatePATrend);
    }
}


