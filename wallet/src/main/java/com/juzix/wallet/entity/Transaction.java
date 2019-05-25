package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.db.entity.TransactionEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.DateUtil;
import com.juzix.wallet.utils.JSONUtil;

import retrofit2.http.PUT;

public class Transaction implements Comparable<Transaction>, Parcelable, Cloneable {

    /**
     * 交易hash
     */
    private String hash;
    /**
     * 当前交易所在快高
     */
    private long blockNumber;
    /**
     * 当前交易的链id
     */
    private String chainId;
    /**
     * 交易创建时间
     */
    @JSONField(name = "timestamp")
    private long createTime;
    /**
     * 交易实际花费值(手续费)，单位：wei
     * “21168000000000”
     */
    private String actualTxCost;
    /**
     * 交易发送方
     */
    private String from;
    /**
     * 交易接收方
     */
    private String to;
    /**
     * 交易序列号
     */
    private long sequence;
    /**
     * 交易状态2 pending 1 成功 0 失败
     */
    private String txReceiptStatus;
    /**
     * 交易类型
     * transfer ：转账
     * MPCtransaction ： MPC交易
     * contractCreate ： 合约创建
     * vote ： 投票
     * transactionExecute ： 合约执行
     * authorization ： 权限
     * candidateDeposit：竞选质押
     * candidateApplyWithdraw：减持质押
     * candidateWithdraw：提取质押
     * unknown：未知
     */
    private String txType;
    /**
     * 交易金额
     */
    private String value;
    /**
     * 发送者钱包名称
     */
    private String senderWalletName;
    /**
     * {json}交易详细信息
     */
    private String txInfo;

    public Transaction() {
    }

    protected Transaction(Parcel in) {
        hash = in.readString();
        blockNumber = in.readLong();
        chainId = in.readString();
        createTime = in.readLong();
        actualTxCost = in.readString();
        from = in.readString();
        to = in.readString();
        sequence = in.readLong();
        txReceiptStatus = in.readString();
        txType = in.readString();
        value = in.readString();
        senderWalletName = in.readString();
        txInfo = in.readString();
    }

    public static final Creator<Transaction> CREATOR = new Creator<Transaction>() {
        @Override
        public Transaction createFromParcel(Parcel in) {
            return new Transaction(in);
        }

        @Override
        public Transaction[] newArray(int size) {
            return new Transaction[size];
        }
    };

    public Transaction(Builder builder) {
        this.hash = builder.hash;
        this.blockNumber = builder.blockNumber;
        this.chainId = builder.chainId;
        this.createTime = builder.createTime;
        this.actualTxCost = builder.actualTxCost;
        this.from = builder.from;
        this.to = builder.to;
        this.sequence = builder.sequence;
        this.txReceiptStatus = builder.txReceiptStatus;
        this.txType = builder.txType;
        this.value = builder.value;
        this.senderWalletName = builder.senderWalletName;
        this.txInfo = builder.txInfo;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getChainId() {
        return chainId;
    }

    public void setChainId(String chainId) {
        this.chainId = chainId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public String getShowCreateTime(){
        return DateUtil.format(createTime, DateUtil.DATETIME_FORMAT_PATTERN);
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getActualTxCost() {
        return actualTxCost;
    }

    public String getShowActualTxCost(){
        return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(actualTxCost, "1E18"));
    }

    public void setActualTxCost(String actualTxCost) {
        this.actualTxCost = actualTxCost;
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

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public TransactionStatus getTxReceiptStatus() {
        return TransactionStatus.getTransactionStatusByIndex(NumberParserUtils.parseInt(txReceiptStatus));
    }

    public void setTxReceiptStatus(String txReceiptStatus) {
        this.txReceiptStatus = txReceiptStatus;
    }

    public TransactionType getTxType() {
        return TransactionType.getTxTypeByName(txType);
    }

    public void setTxType(String txType) {
        this.txType = txType;
    }

    public String getValue() {
        return value;
    }

    public String getShowValue() {
       return NumberParserUtils.getPrettyBalance(BigDecimalUtil.div(value, "1E18"));
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSenderWalletName() {
        return senderWalletName;
    }

    public void setSenderWalletName(String senderWalletName) {
        this.senderWalletName = senderWalletName;
    }

    public String getTxInfo() {
        return txInfo;
    }

    public TransactionExtra getTransactionExtra(){
        return JSONUtil.parseObject(txInfo,TransactionExtra.class);
    }

    public void setTxInfo(String txInfo) {
        this.txInfo = txInfo;
    }

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(hash) ? 0 : hash.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Transaction) {
            Transaction transaction = (Transaction) obj;
            return !TextUtils.isEmpty(hash) && hash.equals(transaction.getHash());
        }
        return super.equals(obj);
    }

    @Override
    public int compareTo(Transaction o) {
        return Long.compare(o.sequence, sequence);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeLong(blockNumber);
        dest.writeString(chainId);
        dest.writeLong(createTime);
        dest.writeString(actualTxCost);
        dest.writeString(from);
        dest.writeString(to);
        dest.writeLong(sequence);
        dest.writeString(txReceiptStatus);
        dest.writeString(txType);
        dest.writeString(value);
        dest.writeString(senderWalletName);
        dest.writeString(txInfo);
    }

    @Override
    public Transaction clone() {
        Transaction transaction = null;
        try {
            transaction = (Transaction) super.clone();
        } catch (CloneNotSupportedException exception) {
            exception.printStackTrace();
        }
        return transaction;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "hash='" + hash + '\'' +
                ", blockNumber=" + blockNumber +
                ", chainId='" + chainId + '\'' +
                ", createTime=" + createTime +
                ", actualTxCost='" + actualTxCost + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", sequence=" + sequence +
                ", txReceiptStatus='" + txReceiptStatus + '\'' +
                ", txType='" + txType + '\'' +
                ", value='" + value + '\'' +
                ", senderWalletName='" + senderWalletName + '\'' +
                ", txInfo='" + txInfo + '\'' +
                '}';
    }

    public static final class Builder {
        private String hash;
        private long blockNumber;
        private String chainId;
        private long createTime;
        private String actualTxCost;
        private String from;
        private String to;
        private long sequence;
        private String txReceiptStatus;
        private String txType;
        private String value;
        private String senderWalletName;
        private String txInfo;

        public Builder hash(String hash) {
            this.hash = hash;
            return this;
        }

        public Builder blockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
            return this;
        }

        public Builder chainId(String chainId) {
            this.chainId = chainId;
            return this;
        }

        public Builder createTime(long createTime) {
            this.createTime = createTime;
            return this;
        }

        public Builder actualTxCost(String actualTxCost) {
            this.actualTxCost = actualTxCost;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder to(String to) {
            this.to = to;
            return this;
        }

        public Builder sequence(long sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder txReceiptStatus(String txReceiptStatus) {
            this.txReceiptStatus = txReceiptStatus;
            return this;
        }

        public Builder txType(String txType) {
            this.txType = txType;
            return this;
        }

        public Builder value(String value) {
            this.value = value;
            return this;
        }

        public Builder senderWalletName(String senderWalletName) {
            this.senderWalletName = senderWalletName;
            return this;
        }

        public Builder txInfo(String txtInfo) {
            this.txInfo = txtInfo;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    public TransactionEntity toTransactionEntity() {
        return new TransactionEntity.Builder(hash, senderWalletName, from, to, createTime)
                .setTxType(txType)
                .setTxInfo(txInfo)
                .setBlockNumber(blockNumber)
                .setChainId(chainId)
                .setTxReceiptStatus(txReceiptStatus)
                .setActualTxCost(actualTxCost)
                .setValue(NumberParserUtils.parseDouble(value))
                .build();
    }
}
