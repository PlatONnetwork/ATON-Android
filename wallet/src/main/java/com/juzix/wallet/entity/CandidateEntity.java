package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author matrixelement
 */
public class CandidateEntity implements Cloneable, Parcelable {

    public static final int STATUS_CANDIDATE = 1;
    public static final int STATUS_RESERVE = 2;

    /**
     * 质押金额 (单位：ADP)
     */
    private String deposit;

    /**
     * 质押金更新的最新块高
     */
    private long blockNumber;

    /**
     * 质押金退款地址
     */
    private String owner;

    /**
     * 所在区块交易索引
     */
    private int txIndex;

    /**
     * 节点Id(公钥)
     */
    private String candidateId;

    /**
     * 最新质押交易的发送方
     */
    private String from;

    /**
     * 出块奖励佣金比，以10000为基数(eg：5%，则fee=500)
     */
    private int fee;

    /**
     *  节点IP
     */
    private String host;

    /**
     *  节点PORT
     */
    private String port;

    /**
     * 附加数据(有长度限制，限制值待定)
     */
    private String extra;
    /**
     * ICON
     */
    private String avatar;
    /**
     * 区域
     */
    private String region;
    /**
     * 投票数
     */
    private long votedNum;
    /**
     * 状态
     */
    private int status;
    /**
     * 质押排名
     */
    private int stakedRanking;

    private CandidateExtraEntity candidateExtraEntity;

    private CandidateEntity(Builder builder) {
        setDeposit(builder.deposit);
        setBlockNumber(builder.blockNumber);
        setOwner(builder.owner);
        setTxIndex(builder.txIndex);
        setCandidateId(builder.candidateId);
        setFrom(builder.from);
        setFee(builder.fee);
        setHost(builder.host);
        setPort(builder.port);
        setExtra(builder.extra);
        setAvatar(builder.avatar);
        setRegion(builder.region);
        setVotedNum(builder.votedNum);
        setStatus(builder.status);
        setStakedRanking(builder.stakedRanking);
        setCandidateExtraEntity(builder.candidateExtraEntity);
    }

    protected CandidateEntity(Parcel in) {
        setDeposit(in.readString());
        setBlockNumber(in.readLong());
        setOwner(in.readString());
        setTxIndex(in.readInt());
        setCandidateId(in.readString());
        setFrom(in.readString());
        setFee(in.readInt());
        setHost(in.readString());
        setPort(in.readString());
        setExtra(in.readString());
        setAvatar(in.readString());
        setRegion(in.readString());
        setVotedNum(in.readLong());
        setStatus(in.readInt());
        setStakedRanking(in.readInt());
        setCandidateExtraEntity(in.readParcelable(CandidateExtraEntity.class.getClassLoader()));
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getDeposit());
        dest.writeLong(getBlockNumber());
        dest.writeString(getOwner());
        dest.writeInt(getTxIndex());
        dest.writeString(getCandidateId());
        dest.writeString(getFrom());
        dest.writeInt(getFee());
        dest.writeString(getHost());
        dest.writeString(getPort());
        dest.writeString(getExtra());
        dest.writeString(getAvatar());
        dest.writeString(getRegion());
        dest.writeLong(getVotedNum());
        dest.writeInt(getStatus());
        dest.writeInt(getStakedRanking());
        dest.writeParcelable(getCandidateExtraEntity(), flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CandidateEntity> CREATOR = new Creator<CandidateEntity>() {
        @Override
        public CandidateEntity createFromParcel(Parcel in) {
            return new CandidateEntity(in);
        }

        @Override
        public CandidateEntity[] newArray(int size) {
            return new CandidateEntity[size];
        }
    };

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getTxIndex() {
        return txIndex;
    }

    public void setTxIndex(int txIndex) {
        this.txIndex = txIndex;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public int getFee() {
        return fee;
    }

    public void setFee(int fee) {
        this.fee = fee;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public long getVotedNum() {
        return votedNum;
    }

    public void setVotedNum(long votedNum) {
        this.votedNum = votedNum;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStakedRanking() {
        return stakedRanking;
    }

    public void setStakedRanking(int stakedRanking) {
        this.stakedRanking = stakedRanking;
    }

    public CandidateExtraEntity getCandidateExtraEntity() {
        return candidateExtraEntity;
    }

    public void setCandidateExtraEntity(CandidateExtraEntity candidateExtraEntity) {
        this.candidateExtraEntity = candidateExtraEntity;
    }

    @Override
    public CandidateEntity clone() {
        CandidateEntity candidateEntity = null;
        try {
            candidateEntity = (CandidateEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return candidateEntity;
    }

    public static final class Builder {
        private String deposit;
        private long   blockNumber;
        private String owner;
        private int    txIndex;
        private String candidateId;
        private String from;
        private int    fee;
        private String host;
        private String port;
        private String extra;
        private String avatar;
        private String region;
        private long votedNum;
        private int status;
        private int stakedRanking;
        private CandidateExtraEntity candidateExtraEntity;

        public Builder() {
        }

        public Builder deposit(String deposit) {
            this.deposit = deposit;
            return this;
        }

        public Builder blockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder txIndex(int txIndex) {
            this.txIndex = txIndex;
            return this;
        }

        public Builder candidateId(String candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public Builder from(String from) {
            this.from = from;
            return this;
        }

        public Builder fee(int fee) {
            this.fee = fee;
            return this;
        }

        public Builder host(String host) {
            this.host = host;
            return this;
        }

        public Builder port(String port) {
            this.port = port;
            return this;
        }

        public Builder extra(String extra) {
            this.extra = extra;
            return this;
        }

        public Builder avatar(String avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder region(String region) {
            this.region = region;
            return this;
        }

        public Builder votedNum(long votedNum) {
            this.votedNum = votedNum;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder stakedRanking(int stakedRanking) {
            this.stakedRanking = stakedRanking;
            return this;
        }

        public Builder candidateExtraEntity(CandidateExtraEntity candidateExtraEntity) {
            this.candidateExtraEntity = candidateExtraEntity;
            return this;
        }

        public CandidateEntity build() {
            return new CandidateEntity(this);
        }
    }
}
