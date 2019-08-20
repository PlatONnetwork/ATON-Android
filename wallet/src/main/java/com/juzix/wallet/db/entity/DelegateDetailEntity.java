package com.juzix.wallet.db.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DelegateDetailEntity extends RealmObject {
    /**
     * 投票节点Id  (节点地址)
     */
    @PrimaryKey
    private String nodeId;
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 最新的质押交易块高
     */
    private String stakingBlockNum;



    public DelegateDetailEntity() {

    }

    public DelegateDetailEntity(Builder builder) {
        setAddress(builder.address);
        setStakingBlockNum(builder.stakingBlockNum);
        setNodeId(builder.nodeId);
    }


    public String getStakingBlockNum() {
        return stakingBlockNum;
    }

    public void setStakingBlockNum(String stakingBlockNum) {
        this.stakingBlockNum = stakingBlockNum;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public static final class Builder {
        private String address;
        private String stakingBlockNum;
        private String nodeId;


        public Builder() {

        }

        public Builder address(String address) {
            address = address;
            return this;
        }

        public Builder stakingBlockNum(String stakingBlockNum) {
            this.stakingBlockNum = stakingBlockNum;
            return this;
        }

        public Builder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public DelegateDetailEntity build() {
            return new DelegateDetailEntity(this);
        }
    }

}
