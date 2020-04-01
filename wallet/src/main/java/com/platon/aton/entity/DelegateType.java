package com.platon.aton.entity;


import org.web3j.platon.StakingAmountType;

public class DelegateType {

    private StakingAmountType stakingAmountType;
    private String amount;


    public DelegateType(StakingAmountType stakingAmountType, String amount) {
        this.stakingAmountType = stakingAmountType;
        this.amount = amount;
    }

    public StakingAmountType getStakingAmountType() {
        return stakingAmountType;
    }

    public void setStakingAmountType(StakingAmountType stakingAmountType) {
        this.stakingAmountType = stakingAmountType;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
