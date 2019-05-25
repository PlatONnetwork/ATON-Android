package com.juzix.wallet.entity;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {

    TRANSFER("transfer", "转账"), MPCTRANSACTION("MPCtransaction：MPC", "MPC交易"), CONTRACTCREATE("contractCreate", "合约创建"), VOTETICKET("voteTicket", "投票"), TRANSACTIONEXECUTE("transactionExecute", "合约执行"), CANDIDATEDEPOSIT("candidateDeposit", "质押"), CANDIDATEAPPLYWITHDRAW("candidateApplyWithdraw", "减持质押"), CANDIDATEWITHDRAW("candidateWithdraw", "提取质押"), UNKNOWN("unknown", "其他");

    private String name;
    private String desc;

    TransactionType(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    private static Map<String, TransactionType> map = new HashMap<>();

    static {
        for (TransactionType status : values()) {
            map.put(status.name, status);
        }
    }

    public static TransactionType getTxTypeByName(String name) {
        return map.get(name);
    }

    public String getTxTypeDesc() {
        return desc;
    }

    public String getTxTypeName(){
        return name;
    }

}
