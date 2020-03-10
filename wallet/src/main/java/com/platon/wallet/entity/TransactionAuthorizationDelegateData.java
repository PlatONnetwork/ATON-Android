package com.platon.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.platon.wallet.utils.BigIntegerUtil;

import org.web3j.platon.PlatOnFunction;
import org.web3j.platon.PlatOnFunctionFactory;
import org.web3j.platon.StakingAmountType;

public class TransactionAuthorizationDelegateData extends TransactionAuthorizationBaseData {

    protected String nodeId;

    /**
     * @see StakingAmountType
     */
    protected int stakingAmountType;

    protected String nodeName;

    public TransactionAuthorizationDelegateData() {
        super();
    }

    public TransactionAuthorizationDelegateData(Builder builder) {
        this.amount = builder.amount;
        this.chainId = builder.chainId;
        this.from = builder.from;
        this.to = builder.to;
        this.gasLimit = builder.gasLimit;
        this.gasPrice = builder.gasPrice;
        this.nonce = builder.nonce;
        this.nodeId = builder.nodeId;
        this.stakingAmountType = builder.stakingAmountType;
        this.nodeName = builder.nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }


    public static final class Builder {
        private String amount;
        private String chainId;
        private String from;
        private String to;
        private String gasLimit;
        private String gasPrice;
        private String nonce;
        private String nodeId;
        protected int stakingAmountType;
        private String nodeName;

        public TransactionAuthorizationDelegateData.Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public TransactionAuthorizationDelegateData.Builder setChainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public TransactionAuthorizationDelegateData.Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public TransactionAuthorizationDelegateData.Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public TransactionAuthorizationDelegateData.Builder setGasLimit(String gasLimit) {
            this.gasLimit = gasLimit;
            return this;
        }

        public TransactionAuthorizationDelegateData.Builder setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public TransactionAuthorizationDelegateData.Builder setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public Builder setNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public Builder setStakingAmountType(int stakingAmountType) {
            this.stakingAmountType = stakingAmountType;
            return this;
        }

        public Builder setNodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public TransactionAuthorizationDelegateData build() {
            return new TransactionAuthorizationDelegateData(this);
        }
    }

    @JSONField(serialize = false,deserialize = false)
    @Override
    public PlatOnFunction getPlatOnFunction() {
        return PlatOnFunctionFactory.createDelegateFunction(nodeId, StakingAmountType.getStakingAmountType(stakingAmountType), BigIntegerUtil.toBigInteger(amount));
    }
}
