package com.juzix.wallet.engine;


import java.math.BigInteger;

public class TicketDto {

    /**
     * 票Id
     */
    private String TicketId;

    /**
     * 票的所属者
     */
    private String Owner;

    /**
     * 购票时的票价
     */
    private BigInteger Deposit;

    /**
     * 候选人Id（节点Id）
     */
    private String CandidateId;

    /**
     * 购票时的块高
     */
    private BigInteger BlockNumber;

    /**
     *选票状态（1->正常，2->被选中，3->过期，4->掉榜）
     */
    private int State;

    /**
     * 票被释放时的块高
     */
    private BigInteger RBlockNumber;

    public String getTicketId() {
        return TicketId;
    }

    public void setTicketId(String ticketId) {
        TicketId = ticketId;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public BigInteger getDeposit() {
        return Deposit;
    }

    public void setDeposit(BigInteger deposit) {
        Deposit = deposit;
    }

    public String getCandidateId() {
        return CandidateId;
    }

    public void setCandidateId(String candidateId) {
        CandidateId = candidateId;
    }

    public BigInteger getBlockNumber() {
        return BlockNumber;
    }

    public void setBlockNumber(BigInteger blockNumber) {
        BlockNumber = blockNumber;
    }

    public int getState() {
        return State;
    }

    public void setState(int state) {
        State = state;
    }

    public BigInteger getRBlockNumber() {
        return RBlockNumber;
    }

    public void setRBlockNumber(BigInteger RBlockNumber) {
        this.RBlockNumber = RBlockNumber;
    }
}