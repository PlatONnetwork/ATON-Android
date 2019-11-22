package com.juzix.wallet.db.entity;

import com.juzix.wallet.entity.VerifyNode;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class VerifyNodeEntity extends RealmObject {
    /**
     * 节点ID
     */
    @PrimaryKey
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
    private long ratePA;


    /**
     * 竞选状态
     * Active —— 活跃中
     * Candidate —— 候选中
     * Exiting —— 退出中
     * Exited —— 已退出
     */
    private String nodeStatus;

    /**
     * 是否为链初始化时内置的候选人
     */
    private boolean isInit;

    public VerifyNodeEntity() {

    }

    public VerifyNodeEntity(Builder builder) {
        this.name = builder.name;
        this.deposit = builder.deposit;
        this.nodeId = builder.nodeId;
        this.nodeStatus = builder.nodeStatus;
        this.ranking = builder.ranking;
        this.ratePA = builder.ratePA;
        this.url = builder.url;
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

    public long getRatePA() {
        return ratePA;
    }

    public void setRatePA(long ratePA) {
        this.ratePA = ratePA;
    }

    public String getNodeStatus() {
        return nodeStatus;
    }

    public void setNodeStatus(String nodeStatus) {
        this.nodeStatus = nodeStatus;
    }

    public static final class Builder {
        private String nodeId;
        private int ranking;
        private String name;
        private String deposit;
        private String url;
        private long ratePA;
        private String nodeStatus;
        private boolean isInit;

        public Builder() {

        }

        public Builder nodeId(String val) {
            this.nodeId = val;
            return this;
        }

        public Builder ranking(int val) {
            this.ranking = val;
            return this;

        }

        public Builder name(String val) {
            this.name = val;
            return this;
        }

        public Builder deposit(String val) {
            this.deposit = val;
            return this;
        }

        public Builder url(String val) {
            this.url = val;
            return this;
        }

        public Builder ratePA(long val) {
            this.ratePA = val;
            return this;
        }

        public Builder nodeStatus(String val) {
            this.nodeStatus = val;
            return this;
        }

        public Builder isInit(boolean isInit) {
            this.isInit = isInit;
            return this;
        }

        public VerifyNodeEntity build() {
            return new VerifyNodeEntity(this);
        }


    }

    public VerifyNode buildVerifyNodeEntity() {
        return new VerifyNode.Builder()
                .name(name)
                .deposit(deposit)
                .nodeId(nodeId)
                .nodeStatus(nodeStatus)
                .ranking(ranking)
                .ratePA(String.valueOf(ratePA))
                .url(url)
                .isInit(isInit)
                .build();
    }


}
