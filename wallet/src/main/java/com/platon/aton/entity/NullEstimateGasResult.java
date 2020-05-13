package com.platon.aton.entity;

import com.platon.aton.engine.AppConfigManager;

import java.math.BigInteger;

/**
 * @author ziv
 * date On 2020-03-31
 */
public class NullEstimateGasResult extends EstimateGasResult {

    private NullEstimateGasResult() {
    }

    public static NullEstimateGasResult getInstance() {
        return new NullEstimateGasResult();
    }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public String getFree() {
        return BigInteger.ZERO.toString(10);
    }

    @Override
    public String getLock() {
        return BigInteger.ZERO.toString(10);
    }

    @Override
    public String getMinDelegation() {
        return AppConfigManager.getInstance().getMinDelegation();
    }
}
