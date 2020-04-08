package com.platon.aton.entity;

import com.platon.aton.utils.BigIntegerUtil;

/**
 * @author ziv
 * date On 2020-03-31
 */
public class EstimateGasResult implements Nullable {

    /**
     * 预估的gas
     */
    private String gasLimit;
    /**
     * gasPrice 单位von
     */
    private String gasPrice;
    /**
     * from地址对应的自由账户余额  单位von
     */
    private String free;
    /**
     * from地址对应的锁仓账户余额  单位von
     */
    private String lock;
    /**
     * 最小委托数量
     */
    private String minDelegation;
    /**
     * 区块最大gasLimit
     */
    private String blockGasLimit;
    /**
     * nonce值
     */
    private String nonce;

    public static EstimateGasResult getNullInstance() {
        return NullEstimateGasResult.getInstance();
    }

    public EstimateGasResult() {
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(String gasLimit) {
        this.gasLimit = gasLimit;
    }

    public String getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(String gasPrice) {
        this.gasPrice = gasPrice;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getLock() {
        return lock;
    }

    public void setLock(String lock) {
        this.lock = lock;
    }

    public String getMinDelegation() {
        return minDelegation;
    }

    public void setMinDelegation(String minDelegation) {
        this.minDelegation = minDelegation;
    }

    public String getBlockGasLimit() {
        return blockGasLimit;
    }

    public void setBlockGasLimit(String blockGasLimit) {
        this.blockGasLimit = blockGasLimit;
    }

    public GasProvider getGasProvider() {
        return new GasProvider(gasLimit, gasPrice);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public String getFeeAmount() {
        return BigIntegerUtil.mul(gasLimit, gasPrice);
    }
}
