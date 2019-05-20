package com.juzix.wallet.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class CandidateWrapEntity {

    /**
     * 已投票数
     */
    private long voteCount;
    /**
     * 投票率:小数
     */
    private long totalCount;
    /**
     * 票价（单位Energon）
     */
    private String ticketPrice;

    @JSONField(name = "list")
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

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
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
                ", totalCount=" + totalCount +
                ", ticketPrice='" + ticketPrice + '\'' +
                ", candidateEntityList=" + candidateEntityList +
                '}';
    }
}
