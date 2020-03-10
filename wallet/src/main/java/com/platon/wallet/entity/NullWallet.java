package com.platon.wallet.entity;

class NullWallet extends Wallet implements Nullable {

    public static NullWallet getInstance() {
        return SingletonHolder.NULL_WALLET;
    }

    private final static class SingletonHolder {
        private final static NullWallet NULL_WALLET = new NullWallet();
    }

    @Override
    public boolean isNull() {
        return true;
    }


}
