//package com.juzix.wallet.engine;
//
//import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
//import com.juzix.wallet.entity.IndividualTransactionEntity;
//import com.juzix.wallet.entity.IndividualWalletEntity;
//
//import java.math.BigInteger;
//
//import io.reactivex.Flowable;
//import io.reactivex.Single;
//
///**
// * @author matrixelement
// */
//public interface IIndividualWalletTransactionService {
//
//    Single<IndividualWalletEntity> getBalance(IndividualWalletEntity walletEntity);
//
//    Flowable<IndividualTransactionEntity> getTransactionByHash(String hash, long createTime, String walletName, String memo);
//
//    Flowable<String> sendTransaction(String privateKey, String ownAddress, String toAddress, String amount, String memo, long gasPrice, long gasLimit);
//
//    Flowable<IndividualTransactionInfoEntity> getTransactionList();
//
//    Flowable<IndividualTransactionInfoEntity> getTransactionList(String address);
//
//    Flowable<IndividualTransactionInfoEntity> getTransactionListByWalletAddress(String walletAddress);
//
//    Flowable<IndividualTransactionEntity> getLatestBlockNumber(IndividualTransactionEntity transactionEntity);
//
//    Flowable<IndividualWalletEntity> getDefaultWallet(String address);
//
//    Single<BigInteger> getEstimateGas(String from, String to, String memo);
//}
