package com.platon.aton.entity;

import com.platon.aton.utils.BigIntegerUtil;

import org.web3j.tx.gas.ContractGasProvider;

public class GasProvider {

    private String gasLimit;

    private String gasPrice;

    public GasProvider() {
    }

    public GasProvider(String gasLimit, String gasPrice) {
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
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

    @Override
    public String toString() {
        return "GasProvider{" +
                "gasLimit='" + gasLimit + '\'' +
                ", gasPrice='" + gasPrice + '\'' +
                '}';
    }

    public org.web3j.tx.gas.GasProvider toSdkGasProvider() {
        return new ContractGasProvider(BigIntegerUtil.toBigInteger(gasPrice), BigIntegerUtil.toBigInteger(gasLimit));
    }
}
