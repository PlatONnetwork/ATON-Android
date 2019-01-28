package com.juzix.wallet.db.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author matrixelement
 */
public class TicketInfoEntity extends RealmObject implements Cloneable {
    @PrimaryKey
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

    /**
     * 单次投票id
     */
    private String transactionId;

    public TicketInfoEntity(){

    }

    private TicketInfoEntity(Builder builder) {
        setUuid(builder.uuid);
        setTicketId(builder.ticketId);
        setOwner(builder.owner);
        setDeposit(builder.deposit);
        setCandidateId(builder.candidateId);
        setBlockNumber(builder.blockNumber);
        setState(builder.state);
        setRBlockNumber(builder.rBlockNumber);
        setTransactionId(builder.transactionId);
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
        private String transactionId;

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

        public Builder transactionUuid(String transactionUuid) {
            this.transactionId = transactionUuid;
            return this;
        }

        public TicketInfoEntity build() {
            return new TicketInfoEntity(this);
        }
    }
}
