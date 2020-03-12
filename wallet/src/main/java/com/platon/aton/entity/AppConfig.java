package com.platon.aton.entity;

public class AppConfig {

    private String minGasPrice;

    private String minDelegation;

    private String timeout;

    public AppConfig() {
    }

    public AppConfig(String minGasPrice, String minDelegation, String timeout) {
        this.minGasPrice = minGasPrice;
        this.minDelegation = minDelegation;
        this.timeout = timeout;
    }

    public String getMinGasPrice() {
        return minGasPrice;
    }

    public void setMinGasPrice(String minGasPrice) {
        this.minGasPrice = minGasPrice;
    }

    public String getMinDelegation() {
        return minDelegation;
    }

    public void setMinDelegation(String minDelegation) {
        this.minDelegation = minDelegation;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "minGasPrice='" + minGasPrice + '\'' +
                ", minDelegation='" + minDelegation + '\'' +
                ", timeout='" + timeout + '\'' +
                '}';
    }
}
