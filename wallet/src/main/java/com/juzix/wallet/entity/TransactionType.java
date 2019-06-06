package com.juzix.wallet.entity;

import com.juzix.wallet.R;

import java.util.HashMap;
import java.util.Map;

public enum TransactionType {

    TRANSFER("transfer", R.string.transfer), MPCTRANSACTION("MPCtransaction", R.string.mpc_transaction), CONTRACTCREATE("contractCreate", R.string.contract_create), VOTETICKET("voteTicket", R.string.vote_ticket), TRANSACTIONEXECUTE("transactionExecute", R.string.transaction_execute), CANDIDATEDEPOSIT("candidateDeposit", R.string.candidate_deposit), CANDIDATEAPPLYWITHDRAW("candidateApplyWithdraw", R.string.candidate_apply_withdraw), CANDIDATEWITHDRAW("candidateWithdraw", R.string.candidate_withdraw), UNKNOWN("unknown", R.string.unknown);

    private String name;
    private int descRes;

    TransactionType(String name, int descRes) {
        this.name = name;
        this.descRes = descRes;
    }

    private static Map<String, TransactionType> map = new HashMap<>();

    static {
        for (TransactionType status : values()) {
            map.put(status.name, status);
        }
    }

    public static TransactionType getTxTypeByName(String name) {
        return map.get(name) == null ? TransactionType.UNKNOWN : map.get(name);
    }

    public int getTxTypeDescRes() {
        return descRes;
    }

    public String getTxTypeName() {
        return name;
    }

}
