package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.juzix.wallet.R;
import com.juzix.wallet.engine.TransactionManager;
import com.juzix.wallet.utils.BigDecimalUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.FunctionType;
import org.web3j.platon.PlatOnFunction;
import org.web3j.platon.PlatOnFunctionFactory;

public class TransactionAuthorizationBaseData implements Parcelable {

    protected String amount;

    protected String chainId;

    protected String from;

    protected String to;

    protected String gasLimit;

    protected String gasPrice;

    protected String nonce;

    public TransactionAuthorizationBaseData() {

    }

    protected TransactionAuthorizationBaseData(Parcel in) {
        amount = in.readString();
        chainId = in.readString();
        from = in.readString();
        to = in.readString();
        gasLimit = in.readString();
        gasPrice = in.readString();
        nonce = in.readString();
    }

    public TransactionAuthorizationBaseData(Builder builder) {
        this.amount = builder.amount;
        this.chainId = builder.chainId;
        this.from = builder.from;
        this.to = builder.to;
        this.gasLimit = builder.gasLimit;
        this.gasPrice = builder.gasPrice;
        this.nonce = builder.nonce;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(chainId);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeString(gasLimit);
        dest.writeString(gasPrice);
        dest.writeString(nonce);
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

    public static final class Builder {
        private String amount;
        private String chainId;
        private String from;
        private String to;
        private String gasLimit;
        private String gasPrice;
        private String nonce;

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

        public TransactionAuthorizationBaseData build() {
            return new TransactionAuthorizationBaseData(this);
        }
    }

    public PlatOnFunction getPlatOnFunction() {
        return PlatOnFunctionFactory.createTransferFunction();
    }

    public String getGasUsed() {
        return BigDecimalUtil.mul(gasPrice, gasLimit).toPlainString();
    }


}
