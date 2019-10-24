package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzix.wallet.R;
import com.juzix.wallet.utils.BigIntegerUtil;

import org.web3j.platon.PlatOnFunction;
import org.web3j.platon.PlatOnFunctionFactory;

public class TransactionAuthorizationUnDelegateData extends TransactionAuthorizationDelegateData {

    private String stakingBlockNum;

    public TransactionAuthorizationUnDelegateData() {
        super();
    }

    public String getStakingBlockNum() {
        return stakingBlockNum;
    }

    public void setStakingBlockNum(String stakingBlockNum) {
        this.stakingBlockNum = stakingBlockNum;
    }

    public TransactionAuthorizationUnDelegateData(Builder builder) {
        this.amount = builder.amount;
        this.chainId = builder.chainId;
        this.from = builder.from;
        this.to = builder.to;
        this.gasLimit = builder.gasLimit;
        this.gasPrice = builder.gasPrice;
        this.nonce = builder.nonce;
        this.nodeId = builder.nodeId;
        this.nodeName = builder.nodeName;
        this.stakingBlockNum = builder.stakingBlockNum;
    }


    public static final class Builder {
        private String amount;
        private String chainId;
        private String from;
        private String to;
        private String gasLimit;
        private String gasPrice;
        private String nonce;
        private int functionType;
        private String nodeId;
        private String nodeName;
        private String stakingBlockNum;

        public TransactionAuthorizationUnDelegateData.Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setChainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setGasLimit(String gasLimit) {
            this.gasLimit = gasLimit;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setNonce(String nonce) {
            this.nonce = nonce;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setFunctionType(int functionType) {
            this.functionType = functionType;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setNodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public TransactionAuthorizationUnDelegateData.Builder setNodeName(String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public Builder setStakingBlockNum(String stakingBlockNum) {
            this.stakingBlockNum = stakingBlockNum;
            return this;
        }

        public TransactionAuthorizationUnDelegateData build() {
            return new TransactionAuthorizationUnDelegateData(this);
        }
    }

    @JSONField(serialize = false,deserialize = false)
    @Override
    public PlatOnFunction getPlatOnFunction() {
        return PlatOnFunctionFactory.createUnDelegateFunction(nodeId, BigIntegerUtil.toBigInteger(stakingBlockNum), BigIntegerUtil.toBigInteger(amount));
    }
}
