package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.utils.BigDecimalUtil;

/**
 * @author matrixelement
 */
public class BatchVoteTransactionEntity implements Parcelable {

    /**
     * 交易hash
     */
    private String TransactionHash;
    /**
     * 候选人Id
     */
    private String candidateId;
    /**
     * 投票人钱包地址
     */
    private String owner;
    /**
     * 此次交易投票获得的收益，单位Energon
     */
    private String earnings;
    /**
     * 交易时间(2019-04-13 11:11:03)
     */
    private String transactiontime;
    /**
     * 当时的购票价格，单位Energon
     */
    private String deposit;
    /**
     * 总票数
     */
    private String totalTicketNum;
    /**
     * 有效票
     */
    private String validNum;
    /**
     * 区域
     */
    private RegionEntity regionEntity;
    /**
     * 节点名称
     */
    private String nodeName;

    public BatchVoteTransactionEntity() {

    }

    private BatchVoteTransactionEntity(Builder builder) {
        setTransactionHash(builder.TransactionHash);
        setCandidateId(builder.candidateId);
        setOwner(builder.owner);
        setEarnings(builder.earnings);
        setTransactiontime(builder.transactiontime);
        setDeposit(builder.deposit);
        setTotalTicketNum(builder.totalTicketNum);
        setValidNum(builder.validNum);
        setRegionEntity(builder.regionEntity);
        setNodeName(builder.nodeName);
    }

    protected BatchVoteTransactionEntity(Parcel in) {
        TransactionHash = in.readString();
        candidateId = in.readString();
        owner = in.readString();
        earnings = in.readString();
        transactiontime = in.readString();
        deposit = in.readString();
        totalTicketNum = in.readString();
        validNum = in.readString();
        regionEntity = in.readParcelable(RegionEntity.class.getClassLoader());
        nodeName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(TransactionHash);
        dest.writeString(candidateId);
        dest.writeString(owner);
        dest.writeString(earnings);
        dest.writeString(transactiontime);
        dest.writeString(deposit);
        dest.writeString(totalTicketNum);
        dest.writeString(validNum);
        dest.writeParcelable(regionEntity, flags);
        dest.writeString(nodeName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BatchVoteTransactionEntity> CREATOR = new Creator<BatchVoteTransactionEntity>() {
        @Override
        public BatchVoteTransactionEntity createFromParcel(Parcel in) {
            return new BatchVoteTransactionEntity(in);
        }

        @Override
        public BatchVoteTransactionEntity[] newArray(int size) {
            return new BatchVoteTransactionEntity[size];
        }
    };

    public String getFormatCandidateId() {
        if (TextUtils.isEmpty(candidateId) || candidateId.startsWith("0x")) {
            return candidateId;
        }
        return "0x".concat(candidateId);
    }

    public String getTransactionHash() {
        return TransactionHash;
    }

    public void setTransactionHash(String transactionHash) {
        TransactionHash = transactionHash;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getEarnings() {
        return earnings;
    }

    public String getShowEarnings() {
        return NumberParserUtils.getPrettyNumber(BigDecimalUtil.div(earnings, "1E18"), 4);
    }

    public void setEarnings(String earnings) {
        this.earnings = earnings;
    }

    public String getTransactiontime() {
        return transactiontime;
    }

    public void setTransactiontime(String transactiontime) {
        this.transactiontime = transactiontime;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getTotalTicketNum() {
        return totalTicketNum;
    }

    public void setTotalTicketNum(String totalTicketNum) {
        this.totalTicketNum = totalTicketNum;
    }

    public String getValidNum() {
        return validNum;
    }

    public String getInvalidVoteNum() {
        return String.valueOf(BigDecimalUtil.sub(totalTicketNum, validNum));
    }

    public void setValidNum(String validNum) {
        this.validNum = validNum;
    }

    public RegionEntity getRegionEntity() {
        return regionEntity;
    }

    public void setRegionEntity(RegionEntity regionEntity) {
        this.regionEntity = regionEntity;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public double getVoteStaked() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(validNum, deposit).doubleValue(), 1E18);
    }

    public double getVoteUnStaked() {
        return BigDecimalUtil.div(BigDecimalUtil.mul(getInvalidVoteNum(), deposit).doubleValue(), 1E18);
    }

    public static final class Builder {
        private String TransactionHash;
        private String candidateId;
        private String owner;
        private String earnings;
        private String transactiontime;
        private String deposit;
        private String totalTicketNum;
        private String validNum;
        private RegionEntity regionEntity;
        private String nodeName;

        public Builder() {
        }

        public Builder TransactionHash(String val) {
            TransactionHash = val;
            return this;
        }

        public Builder candidateId(String val) {
            candidateId = val;
            return this;
        }

        public Builder owner(String val) {
            owner = val;
            return this;
        }

        public Builder earnings(String val) {
            earnings = val;
            return this;
        }

        public Builder transactiontime(String val) {
            transactiontime = val;
            return this;
        }

        public Builder deposit(String val) {
            deposit = val;
            return this;
        }

        public Builder totalTicketNum(String val) {
            totalTicketNum = val;
            return this;
        }

        public Builder validNum(String val) {
            validNum = val;
            return this;
        }

        public Builder regionEntity(RegionEntity val) {
            regionEntity = val;
            return this;
        }

        public Builder nodeName(String val) {
            nodeName = val;
            return this;
        }

        public BatchVoteTransactionEntity build() {
            return new BatchVoteTransactionEntity(this);
        }
    }
}
