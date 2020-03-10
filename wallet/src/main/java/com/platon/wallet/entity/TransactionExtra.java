package com.platon.wallet.entity;

public class TransactionExtra {

    private String functionName;

    private String parameters;

    private String type;

    public TransactionExtra() {
    }

    public TransactionExtra(String functionName, String parameters, String type) {
        this.functionName = functionName;
        this.parameters = parameters;
        this.type = type;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
