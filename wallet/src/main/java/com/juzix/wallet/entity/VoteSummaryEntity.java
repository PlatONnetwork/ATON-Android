package com.juzix.wallet.entity;

/**
 * @author matrixelement
 */
public class VoteSummaryEntity {

    private String voteSummaryDesc;

    private String voteSummaryValue;

    public VoteSummaryEntity(String voteSummaryDesc, String voteSummaryValue) {
        this.voteSummaryDesc = voteSummaryDesc;
        this.voteSummaryValue = voteSummaryValue;
    }

    public String getVoteSummaryDesc() {
        return voteSummaryDesc;
    }

    public void setVoteSummaryDesc(String voteSummaryDesc) {
        this.voteSummaryDesc = voteSummaryDesc;
    }

    public String getVoteSummaryValue() {
        return voteSummaryValue;
    }

    public void setVoteSummaryValue(String voteSummaryValue) {
        this.voteSummaryValue = voteSummaryValue;
    }

}
