package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.juzix.wallet.R;
import com.juzix.wallet.utils.JSONUtil;

import org.web3j.utils.Numeric;

/**
 * @author matrixelement
 */
public class CandidateEntity implements Cloneable, Parcelable {

    private final static long UPDATE_REGION_TIME_MILLS = 60 * 1000;

    /**
     * 质押金额 (单位：ADP)
     */
    @JSONField(name = "Deposit")
    private String deposit;

    /**
     * 质押金更新的最新块高
     */
    @JSONField(name = "BlockNumber")
    private long blockNumber;

    /**
     * 质押金退款地址
     */
    @JSONField(name = "Owner")
    private String owner;

    /**
     * 所在区块交易索引
     */
    @JSONField(name = "TxIndex")
    private int txIndex;

    /**
     * 节点Id(公钥)
     */
    @JSONField(name = "CandidateId")
    private String candidateId;

    /**
     * 最新质押交易的发送方
     */
    @JSONField(name = "From")
    private String from;

    /**
     * 出块奖励佣金比，以10000为基数(eg：5%，则fee=500)
     */
    @JSONField(name = "Fee")
    private int fee;
    /**
     * 节点主机ip
     */
    @JSONField(name = "Host")
    private String host;
    /**
     * 节点PORT
     */
    @JSONField(name = "Port")
    private String port;
    /**
     * 幸运票所在交易Hash
     */
    @JSONField(name = "TxHash")
    private String txHash;
    /**
     * 投票数
     */
    private long votedNum;
    /**
     * 状态
     */
    private CandidateStatus status;
    /**
     * 质押排名
     */
    private int stakedRanking;

    @JSONField(name = "Extra")
    private String extra;

    private RegionEntity regionEntity;

    public CandidateEntity() {
    }

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
        setTxHash(builder.txHash);
        setExtra(builder.extra);
        setVotedNum(builder.votedNum);
        setStatus(builder.status);
        setStakedRanking(builder.stakedRanking);
        setRegionEntity(builder.regionEntity);
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
        setTxHash(in.readString());
        setExtra(in.readString());
        setVotedNum(in.readLong());
        setStatus(in.readParcelable(CandidateStatus.class.getClassLoader()));
        setStakedRanking(in.readInt());
        setRegionEntity(in.readParcelable(RegionEntity.class.getClassLoader()));
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
        dest.writeString(getTxHash());
        dest.writeString(getExtra());
        dest.writeLong(getVotedNum());
        dest.writeParcelable(status, flags);
        dest.writeInt(getStakedRanking());
        dest.writeParcelable(getRegionEntity(), flags);
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

    public String getCandidateIdWithPrefix() {
        if (TextUtils.isEmpty(candidateId)) {
            return candidateId;
        }
        return Numeric.prependHexPrefix(candidateId);
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

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public long getVotedNum() {
        return votedNum;
    }

    public void setVotedNum(long votedNum) {
        this.votedNum = votedNum;
    }

    public CandidateStatus getStatus() {
        return status;
    }

    public void setStatus(CandidateStatus status) {
        this.status = status;
    }

    public int getStakedRanking() {
        return stakedRanking == 0 ? 1 : stakedRanking;
    }

    public void setStakedRanking(int stakedRanking) {
        this.stakedRanking = stakedRanking;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getTxHash() {
        return txHash;
    }

    public void setTxHash(String txHash) {
        this.txHash = txHash;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public RegionEntity getRegionEntity() {
        return regionEntity;
    }

    public void setRegionEntity(RegionEntity regionEntity) {
        this.regionEntity = regionEntity;
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

    @Override
    public int hashCode() {
        return TextUtils.isEmpty(candidateId) ? 0 : candidateId.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof CandidateEntity) {
            CandidateEntity candidateEntity = (CandidateEntity) obj;
            return !TextUtils.isEmpty(candidateEntity.getCandidateId()) && candidateEntity.getCandidateId().equals(candidateId);

        }
        return super.equals(obj);
    }

    public static final class Builder {
        private String deposit;
        private long blockNumber;
        private String owner;
        private int txIndex;
        private String candidateId;
        private String from;
        private int fee;
        private String host;
        private String port;
        private String txHash;
        private String extra;
        private long votedNum;
        private CandidateStatus status;
        private int stakedRanking;
        private RegionEntity regionEntity;

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

        public Builder txHash(String txHash) {
            this.txHash = txHash;
            return this;
        }

        public Builder extra(String extra) {
            this.extra = extra;
            return this;
        }

        public Builder votedNum(long votedNum) {
            this.votedNum = votedNum;
            return this;
        }

        public Builder status(CandidateStatus status) {
            this.status = status;
            return this;
        }

        public Builder stakedRanking(int stakedRanking) {
            this.stakedRanking = stakedRanking;
            return this;
        }

        public Builder regionEntity(RegionEntity regionEntity) {
            this.regionEntity = regionEntity;
            return this;
        }

        public CandidateEntity build() {
            return new CandidateEntity(this);
        }
    }

    /**
     * 是否是无效的地址
     *
     * @return
     */
    public boolean isInvalidHost() {

        if (TextUtils.isEmpty(host) || regionEntity == null) {
            return true;
        }

        if (TextUtils.isEmpty(regionEntity.getCountryEn()) || TextUtils.isEmpty(regionEntity.getCountryZh())) {
            return true;
        }

        if (System.currentTimeMillis() - regionEntity.getUpdateTime() >= UPDATE_REGION_TIME_MILLS) {
            return true;
        }

        return false;
    }

    public CandidateExtraEntity getCandidateExtraEntity() {
        return JSONUtil.parseObject(extra, CandidateExtraEntity.class);
    }

    public String getCandidateName() {
        CandidateExtraEntity candidateExtraEntity = getCandidateExtraEntity();
        return candidateExtraEntity != null ? candidateExtraEntity.getNodeName() : "";

    }

    public enum CandidateStatus implements Parcelable {

        STATUS_CANDIDATE {
            @Override
            public int getStatusDescRes() {
                return R.string.candidate;
            }
        }, STATUS_RESERVE {
            @Override
            public int getStatusDescRes() {
                return R.string.alternative;
            }
        }, STATUS_VERIFY {
            @Override
            public int getStatusDescRes() {
                return R.string.validator;
            }
        };

        public static final Creator<CandidateStatus> CREATOR = new Creator<CandidateStatus>() {
            @Override
            public CandidateStatus createFromParcel(Parcel in) {
                return CandidateStatus.values()[in.readInt()];
            }

            @Override
            public CandidateStatus[] newArray(int size) {
                return new CandidateStatus[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(ordinal());
        }

        public abstract int getStatusDescRes();
    }
}
