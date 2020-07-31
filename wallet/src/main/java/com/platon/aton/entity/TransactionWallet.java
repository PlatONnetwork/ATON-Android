package com.platon.aton.entity;

import java.util.List;

public class TransactionWallet {

    private Wallet wallet;
    private List<Wallet> subWallets;//母钱包的子钱包组

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public List<Wallet> getSubWallets() {
        return subWallets;
    }

    public void setSubWallets(List<Wallet> subWallets) {
        this.subWallets = subWallets;
    }


    public static String NULL_TRANSACTIONWALLET_UUID = "All";
    public static NullTransactionWallet getNullInstance() {

        NullTransactionWallet nullTransactionWallet = new NullTransactionWallet();
        Wallet wallet = new Wallet();
        wallet.setUuid(NULL_TRANSACTIONWALLET_UUID);
        wallet.setName("所有钱包");
        nullTransactionWallet.setWallet(wallet);

        return nullTransactionWallet;
    }

    @Override
    public String toString() {
        return "TransactionWallet{" +
                "wallet=" + wallet +
                ", subWallets=" + subWallets +
                '}';
    }
}
