package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.juzix.wallet.R;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.BigIntegerUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.FunctionType;
import org.web3j.platon.PlatOnFunction;
import org.web3j.platon.PlatOnFunctionFactory;
import org.web3j.platon.StakingAmountType;

public class TransactionAuthorizationBaseData implements Parcelable {

    protected int functionType;

    protected String amount;

    protected String chainId;

    protected String from;

    protected String to;

    protected String gasLimit;

    protected String gasPrice;

    protected String nonce;

    protected String nodeId;

    /**
     * @see StakingAmountType
     */
    protected int stakingAmountType;

    protected String nodeName;

    protected String stakingBlockNum;

    public TransactionAuthorizationBaseData() {

    }

    protected TransactionAuthorizationBaseData(Parcel in) {
        functionType = in.readInt();
        amount = in.readString();
        chainId = in.readString();
        from = in.readString();
        to = in.readString();
        gasLimit = in.readString();
        gasPrice = in.readString();
        nonce = in.readString();
        nodeId = in.readString();
        stakingAmountType = in.readInt();
        nodeName = in.readString();
        stakingBlockNum = in.readString();
    }

    public TransactionAuthorizationBaseData(Builder builder) {
        this.functionType = builder.functionType;
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
        this.stakingBlockNum = builder.stakingBlockNum;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(functionType);
        dest.writeString(amount);
        dest.writeString(chainId);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(gasLimit);
        dest.writeString(gasPrice);
        dest.writeString(nonce);
        dest.writeString(nodeId);
        dest.writeInt(stakingAmountType);
        dest.writeString(nodeName);
        dest.writeString(stakingBlockNum);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TransactionAuthorizationBaseData> CREATOR = new Creator<TransactionAuthorizationBaseData>() {
        @Override
        public TransactionAuthorizationBaseData createFromParcel(Parcel in) {
            return new TransactionAuthorizationBaseData(in);
        }

        @Override
        public TransactionAuthorizationBaseData[] newArray(int size) {
            return new TransactionAuthorizationBaseData[size];
        }
    };

    public int getFunctionType() {
        return functionType;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public int getStakingAmountType() {
        return stakingAmountType;
    }

    public void setStakingAmountType(int stakingAmountType) {
        this.stakingAmountType = stakingAmountType;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public String getStakingBlockNum() {
        return stakingBlockNum;
    }

    public void setStakingBlockNum(String stakingBlockNum) {
        this.stakingBlockNum = stakingBlockNum;
    }

    public static final class Builder {
        private int functionType;
        private String amount;
        private String chainId;
        private String from;
        private String to;
        private String gasLimit;
        private String gasPrice;
        private String nonce;
        private String nodeId;
        private int stakingAmountType;
        private String nodeName;
        protected String stakingBlockNum;

        public Builder(int functionType) {
            this.functionType = functionType;
        }

        public Builder setAmount(String amount) {
            this.amount = amount;
            return this;
        }

        public Builder setChainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder setFrom(String from) {
            this.from = from;
            return this;
        }

        public Builder setTo(String to) {
            this.to = to;
            return this;
        }

        public Builder setGasLimit(String gasLimit) {
            this.gasLimit = gasLimit;
            return this;
        }

        public Builder setGasPrice(String gasPrice) {
            this.gasPrice = gasPrice;
            return this;
        }

        public Builder setNonce(String nonce) {
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

        public Builder setStakingBlockNum(String stakingBlockNum) {
            this.stakingBlockNum = stakingBlockNum;
            return this;
        }

        public TransactionAuthorizationBaseData build() {
            return new TransactionAuthorizationBaseData(this);
        }
    }

    public PlatOnFunction getPlatOnFunction() {
        if (functionType == FunctionType.DELEGATE_FUNC_TYPE) {
            return PlatOnFunctionFactory.createDelegateFunction(nodeId, StakingAmountType.getStakingAmountType(stakingAmountType), BigIntegerUtil.toBigInteger(amount));
        } else if (functionType == FunctionType.WITHDREW_DELEGATE_FUNC_TYPE) {
            return PlatOnFunctionFactory.createUnDelegateFunction(nodeId, BigIntegerUtil.toBigInteger(stakingBlockNum), BigIntegerUtil.toBigInteger(amount));
        } else {
            return PlatOnFunctionFactory.createTransferFunction();
        }
    }

    public String getGasUsed() {
        return BigDecimalUtil.mul(gasPrice, gasLimit).toPlainString();
    }


}
