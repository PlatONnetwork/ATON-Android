package com.juzix.wallet.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author matrixelement
 */
public class TicketEntity implements Cloneable, Parcelable {
    /**
     * 正常
     */
    public static final int NORMAL   = 1;
    /**
     * 被选中
     */
    public static final int SELECTED = 2;
    /**
     * 过期
     */
    public static final int EXPIRED  = 3;
    /**
     * 掉榜
     */
    public static final int OFF      = 4;

    private String uuid;
    /**
     * 票Id
     */
    private String ticketId;

    /**
     * 票的所属者
     */
    private String owner;

    /**
     * 购票时的票价
     */
    private String deposit;

    /**
     * 候选人Id（节点Id）
     */
    private String candidateId;

    /**
     * 购票时的块高
     */
    private long blockNumber;

    /**
     *选票状态（1->正常，2->被选中，3->过期，4->掉榜）
     */
    private int state;

    /**
     * 票被释放时的块高
     */
    private long rBlockNumber;

    public TicketEntity() {
    }

    protected TicketEntity(Parcel in) {
        setUuid(in.readString());
        setTicketId(in.readString());
        setOwner(in.readString());
        setDeposit(in.readString());
        setCandidateId(in.readString());
        setBlockNumber(in.readLong());
        setState(in.readInt());
        setRBlockNumber(in.readLong());
    }

    private TicketEntity(Builder builder) {
        setUuid(builder.uuid);
        setTicketId(builder.ticketId);
        setOwner(builder.owner);
        setDeposit(builder.deposit);
        setCandidateId(builder.candidateId);
        setBlockNumber(builder.blockNumber);
        setState(builder.state);
        setRBlockNumber(builder.rBlockNumber);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getUuid());
        dest.writeString(getTicketId());
        dest.writeString(getOwner());
        dest.writeString(getDeposit());
        dest.writeString(getCandidateId());
        dest.writeLong(getBlockNumber());
        dest.writeInt(getState());
        dest.writeLong(getRBlockNumber());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TicketEntity> CREATOR = new Creator<TicketEntity>() {
        @Override
        public TicketEntity createFromParcel(Parcel in) {
            return new TicketEntity(in);
        }

        @Override
        public TicketEntity[] newArray(int size) {
            return new TicketEntity[size];
        }
    };

    @Override
    public TicketEntity clone() {
        TicketEntity transactionEntity = null;
        try {
            transactionEntity = (TicketEntity) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return transactionEntity;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }

    public String getCandidateId() {
        return candidateId;
    }

    public void setCandidateId(String candidateId) {
        this.candidateId = candidateId;
    }

    public long getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(long blockNumber) {
        this.blockNumber = blockNumber;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public long getRBlockNumber() {
        return rBlockNumber;
    }

    public void setRBlockNumber(long rBlockNumber) {
        this.rBlockNumber = rBlockNumber;
    }

    public static final class Builder {

        private String uuid;
        private String ticketId;
        private String owner;
        private String deposit;
        private String candidateId;
        private long   blockNumber;
        private int    state;
        private long   rBlockNumber;

        public Builder() {
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder ticketId(String ticketId) {
            this.ticketId = ticketId;
            return this;
        }

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder deposit(String deposit) {
            this.deposit = deposit;
            return this;
        }

        public Builder candidateId(String candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public Builder blockNumber(long blockNumber) {
            this.blockNumber = blockNumber;
            return this;
        }

        public Builder state(int state) {
            this.state = state;
            return this;
        }

        public Builder rBlockNumber(long rBlockNumber) {
            this.rBlockNumber = rBlockNumber;
            return this;
        }

        public TicketEntity build() {
            return new TicketEntity(this);
        }
    }
}
