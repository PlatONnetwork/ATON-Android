package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import java.text.NumberFormat;

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
     * 节点头像
     */
    private String url;

    /**
     * 预计年化率
     */
    private String delegatedRatePA;
    /**
     * 竞选状态
     * Active —— 活跃中
     * Candidate —— 候选中
     */
    private @NodeStatus
    String nodeStatus;

    /**
     * 是否为链初始化时内置的候选人
     */
    private boolean isInit;

    /**
     * 是否共识中
     */
    private boolean isConsensus;

    /**
     * 总押金
     */
    private String delegateSum;
    /**
     * 委托者数
     */
    private String delegate;

    public VerifyNode() {

    }

    protected VerifyNode(Parcel in) {
        nodeId = in.readString();
        ranking = in.readInt();
        name = in.readString();
        url = in.readString();
        delegatedRatePA = in.readString();
        nodeStatus = in.readString();
        isInit = in.readByte() != 0;
        isConsensus = in.readByte() != 0;
        delegateSum = in.readString();
        delegate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nodeId);
        dest.writeInt(ranking);
        dest.writeString(name);
        dest.writeString(url);
        dest.writeString(delegatedRatePA);
        dest.writeString(nodeStatus);
        dest.writeByte((byte) (isInit ? 1 : 0));
        dest.writeByte((byte) (isConsensus ? 1 : 0));
        dest.writeString(delegateSum);
        dest.writeString(delegate);
    }

    @Override
    public int describeContents() {
        return 0;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDelegatedRatePA() {
        return delegatedRatePA;
    }

    public void setDelegatedRatePA(String delegatedRatePA) {
        this.delegatedRatePA = delegatedRatePA;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
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

    public String getDelegateSum() {
        return delegateSum;
    }

    public void setDelegateSum(String delegateSum) {
        this.delegateSum = delegateSum;
    }

    public String getDelegate() {
        return delegate;
    }

    public void setDelegate(String delegate) {
        this.delegate = delegate;
    }

    public int getNodeStatusDescRes() {

        switch (nodeStatus) {
            case NodeStatus.ACTIVE:
                return isConsensus ? R.string.validators_verifying : R.string.validators_active;
            case NodeStatus.CANDIDATE:
                return R.string.validators_candidate;
            case NodeStatus.EXITING:
                return R.string.validators_state_exiting;
            case NodeStatus.EXITED:
                return R.string.validators_state_exited;
            default:
                return -1;
        }
    }

    public String getShowDelegatedRatePA() {
        //todo 待重构
        return isInit ? null : NumberFormat.getInstance().format(((NumberParserUtils.parseDouble(delegatedRatePA)) / 100)) + "%";
    }
}
