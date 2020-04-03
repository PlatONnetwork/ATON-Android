package com.platon.aton.entity;

import com.platon.aton.utils.BigIntegerUtil;

import org.web3j.tx.gas.ContractGasProvider;

public class GasProvider {

    private String gasLimit;

    private String gasPrice;

    private String free;

    private String lock;

    private String minDelegation;

    private String nonce;

    private String blockGasLimit;

    public GasProvider() {
    }

    public GasProvider(String gasLimit, String gasPrice) {
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
    }

    public GasProvider(String gasLimit, String gasPrice, String free, String lock, String minDelegation, String nonce, String blockGasLimit) {
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.free = free;
        this.lock = lock;
        this.minDelegation = minDelegation;
        this.nonce = nonce;
        this.blockGasLimit = blockGasLimit;
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

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getBlockGasLimit() {
        return blockGasLimit;
    }

    public void setBlockGasLimit(String blockGasLimit) {
        this.blockGasLimit = blockGasLimit;
    }

    @Override
    public String toString() {
        return "GasProvider{" +
                "gasLimit='" + gasLimit + '\'' +
                ", gasPrice='" + gasPrice + '\'' +
                ", free='" + free + '\'' +
                ", lock='" + lock + '\'' +
                ", minDelegation='" + minDelegation + '\'' +
                ", nonce='" + nonce + '\'' +
                ", blockGasLimit='" + blockGasLimit + '\'' +
                '}';
    }

    public org.web3j.tx.gas.GasProvider toSdkGasProvider() {
        return new ContractGasProvider(BigIntegerUtil.toBigInteger(gasPrice), BigIntegerUtil.toBigInteger(gasLimit));
    }
}
