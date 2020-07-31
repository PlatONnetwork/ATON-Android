package com.platon.aton.entity;

public class  NullTransactionWallet extends TransactionWallet implements Nullable {

    public static NullTransactionWallet getInstance() {
        return SingletonHolder.NULL_WALLET;
    }

    private final static class SingletonHolder {
        private final static NullTransactionWallet NULL_WALLET = new NullTransactionWallet();
    }

    @Override
    public boolean isNull() {
        return true;
    }


}
