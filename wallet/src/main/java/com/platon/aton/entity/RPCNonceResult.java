package com.platon.aton.entity;

import java.math.BigInteger;

/**
 * @author ziv
 * date On 2020-03-03
 */
public class RPCNonceResult {

    private @RPCErrorCode
    int errCode;

    private BigInteger nonce;

    public RPCNonceResult(int errCode, BigInteger nonce) {
        this.errCode = errCode;
        this.nonce = nonce;
    }

    public boolean isSuccessful() {
        return errCode == RPCErrorCode.SUCCESS;
    }

    public int getErrCode() {
        return errCode;
    }

    public void setErrCode(int errCode) {
        this.errCode = errCode;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }
}
