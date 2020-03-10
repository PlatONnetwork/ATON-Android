package com.platon.wallet.entity;

/**
 * @author ziv
 * date On 2020-02-22
 */
public class RPCTransactionResult {


    private @RPCErrorCode
    int errCode;

    private String hash;

    public RPCTransactionResult(int errCode) {
        this.errCode = errCode;
    }

    public RPCTransactionResult(int errCode, String hash) {
        this.errCode = errCode;
        this.hash = hash;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
