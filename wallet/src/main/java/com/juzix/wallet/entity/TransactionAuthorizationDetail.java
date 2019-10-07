package com.juzix.wallet.entity;

public class TransactionAuthorizationDetail {

    private String amount;

    private int functionType;

    private String sender;

    private String receiver;

    private String fee;

    public TransactionAuthorizationDetail(String amount, int functionType, String sender, String receiver, String fee) {
        this.amount = amount;
        this.functionType = functionType;
        this.sender = sender;
        this.receiver = receiver;
        this.fee = fee;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public int getFunctionType() {
        return functionType;
    }

    public void setFunctionType(int functionType) {
        this.functionType = functionType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }
}
