package com.juzix.wallet.entity;

import java.util.List;

public class CandidateWrapEntity {

    /**
     * 已投票数
     */
    private long voteCount;
    /**
     * 投票率:小数
     */
    private double proportion;
    /**
     * 票价（单位Energon）
     */
    private String ticketPrice;

    private List<CandidateEntity> candidateEntityList;

    /**
     * fastJson自动解析需要提供默认构造函数
     */
    public CandidateWrapEntity() {
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(long voteCount) {
        this.voteCount = voteCount;
    }

    public double getProportion() {
        return proportion;
    }

    public void setProportion(double proportion) {
        this.proportion = proportion;
    }

    public String getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(String ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public List<CandidateEntity> getCandidateEntityList() {
        return candidateEntityList;
    }

    public void setCandidateEntityList(List<CandidateEntity> candidateEntityList) {
        this.candidateEntityList = candidateEntityList;
    }

    @Override
    public String toString() {
        return "CandidateWrapEntity{" +
                "voteCount=" + voteCount +
                ", proportion=" + proportion +
                ", ticketPrice='" + ticketPrice + '\'' +
                ", candidateEntityList=" + candidateEntityList +
                '}';
    }
}
