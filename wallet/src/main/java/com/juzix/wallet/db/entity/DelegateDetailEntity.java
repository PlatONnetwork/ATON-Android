package com.juzix.wallet.db.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class DelegateDetailEntity extends RealmObject {
    /**
     * 投票节点Id  (节点地址)
     */
    private String nodeId;
    /**
     * 钱包地址
     */
    private String address;
    /**
     * 最新委托交易块高
     */
    private String delegationBlockNum;



    public DelegateDetailEntity() {

    }

    public DelegateDetailEntity(Builder builder) {
        setAddress(builder.address);
        setDelegationBlockNum(builder.delegationBlockNum);
        setNodeId(builder.nodeId);
    }


    public String getDelegationBlockNum() {
        return delegationBlockNum;
    }

    public void setDelegationBlockNum(String delegationBlockNum) {
        this.delegationBlockNum = delegationBlockNum;
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
        private String delegationBlockNum;
        private String nodeId;


        public Builder() {

        }

        public Builder address(String address) {
            address = address;
            return this;
        }

        public Builder delegationBlockNum(String delegationBlockNum) {
            this.delegationBlockNum = delegationBlockNum;
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
