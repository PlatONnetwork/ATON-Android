package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.db.entity.VerifyNodeEntity;

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
     * 节点头像
     */
    private String url;

    /**
     * 预计年化率
     */
    private String ratePA;


    /**
     * 竞选状态
     * Active —— 活跃中
     * Candidate —— 候选中
     */
    private String nodeStatus;

    /**
     * 是否为链初始化时内置的候选人
     */
    private boolean isInit;


    public VerifyNode() {

    }

    public VerifyNode(String nodeId, int ranking, String name, String deposit, String url, String ratePA, String nodeStatus, boolean isInit) {
        this.nodeId = nodeId;
        this.ranking = ranking;
        this.name = name;
        this.deposit = deposit;
        this.url = url;
        this.ratePA = ratePA;
        this.nodeStatus = nodeStatus;
        this.isInit = isInit;
    }

    public VerifyNode(Builder builder) {
        this.nodeId = builder.nodeId;
        this.url = builder.url;
        this.deposit = builder.deposit;
        this.nodeStatus = builder.nodeStatus;
        this.ranking = builder.ranking;
        this.name = builder.name;
        this.ratePA = builder.ratePA;
        this.isInit = builder.isInit;
    }


    public boolean isInit() {
        return isInit;
    }

    public void setInit(boolean init) {
        isInit = init;
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

    public String getRatePA() {
        return ratePA;
    }

    public int showRate() {
        return NumberParserUtils.parseInt(ratePA);
    }

    public void setRatePA(String ratePA) {
        this.ratePA = ratePA;
    }


    protected VerifyNode(Parcel in) {
        nodeId = in.readString();
        ranking = in.readInt();
        name = in.readString();
        deposit = in.readString();
        url = in.readString();
        nodeStatus = in.readString();
        ratePA = in.readString();
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
        dest.writeString(url);
        dest.writeString(nodeStatus);
        dest.writeString(ratePA);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final class Builder {
        private String nodeId;
        private int ranking;
        private String name;
        private String deposit;
        private String url;
        private String ratePA;
        private String nodeStatus;
        private boolean isInit;

        public Builder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder ranking(int ranking) {
            this.ranking = ranking;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder deposit(String deposit) {
            this.deposit = deposit;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder ratePA(String ratePA) {
            this.ratePA = ratePA;
            return this;
        }

        public Builder nodeStatus(String nodeStatus) {
            this.nodeStatus = nodeStatus;
            return this;
        }

        public Builder isInit(boolean isInit) {
            this.isInit = isInit;
            return this;
        }

        public VerifyNode build() {
            return new VerifyNode(this);
        }

    }

    public VerifyNodeEntity toVerifyNodeEntity() {
        return new VerifyNodeEntity.Builder()
                .deposit(deposit)
                .name(name)
                .nodeId(nodeId)
                .ranking(ranking)
                .ratePA(NumberParserUtils.parseLong(ratePA))
                .url(url)
                .nodeStatus(nodeStatus)
                .isInit(isInit)
                .build();
    }


}
