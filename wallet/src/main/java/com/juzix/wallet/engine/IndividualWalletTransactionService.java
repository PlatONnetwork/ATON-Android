//package com.juzix.wallet.engine;
//
//import com.juzix.wallet.db.entity.IndividualTransactionInfoEntity;
//import com.juzix.wallet.db.sqlite.IndividualTransactionInfoDao;
//import com.juzix.wallet.entity.IndividualTransactionEntity;
//import com.juzix.wallet.entity.IndividualWalletEntity;
//
//import org.reactivestreams.Publisher;
//
//import java.math.BigInteger;
//import java.util.List;
//import java.util.concurrent.Callable;
//
//import io.reactivex.Flowable;
//import io.reactivex.Single;
//import io.reactivex.functions.Function;
//import io.reactivex.functions.Predicate;
//import io.reactivex.schedulers.Schedulers;
//
///**
// * @author matrixelement
// */
//public class IndividualWalletTransactionService implements IIndividualWalletTransactionService {
//
//    @Override
//    public Single<IndividualWalletEntity> getBalance(IndividualWalletEntity walletEntity) {
//        return Single.fromCallable(new Callable<IndividualWalletEntity>() {
//            @Override
//            public IndividualWalletEntity call() throws Exception {
//                return IndividualWalletTransactionManager.getInstance().getBalanceByAddress(walletEntity);
//            }
//        }).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Flowable<IndividualTransactionEntity> getTransactionByHash(String hash, long createTime, String walletName, String memo) {
//        return Flowable.fromCallable(new Callable<IndividualTransactionEntity>() {
//            @Override
//            public IndividualTransactionEntity call() throws Exception {
//                return IndividualWalletTransactionManager.getInstance().getTransactionByHash(hash, createTime, walletName, memo);
//            }
//        }).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Flowable<String> sendTransaction(String privateKey, String ownAddress, String toAddress, String amount, String memo, long gasPrice, long gasLimit) {
//        return Flowable.fromCallable(new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                return IndividualWalletTransactionManager.getInstance().sendTransaction(privateKey, ownAddress, toAddress, amount, memo, gasPrice, gasLimit);
//            }
//        }).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Flowable<IndividualTransactionInfoEntity> getTransactionList() {
//        return Flowable.fromIterable(IndividualTransactionInfoDao.getInstance().getTransactionList()).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Flowable<IndividualTransactionInfoEntity> getTransactionList(String address) {
//        return Flowable.fromIterable(IndividualTransactionInfoDao.getInstance().getTransactionList(address)).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Flowable<IndividualTransactionInfoEntity> getTransactionListByWalletAddress(String creatorAddress) {
//        return Flowable.fromIterable(IndividualTransactionInfoDao.getInstance().getTransactionListByWalletAddress(creatorAddress)).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Flowable<IndividualTransactionEntity> getLatestBlockNumber(IndividualTransactionEntity transactionEntity) {
//        return Flowable.fromCallable(new Callable<IndividualTransactionEntity>() {
//            @Override
//            public IndividualTransactionEntity call() throws Exception {
//                return IndividualWalletTransactionManager.getInstance().getLatestBlockNumber(transactionEntity);
//            }
//        }).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Flowable<IndividualWalletEntity> getDefaultWallet(String address) {
//        return Flowable.fromCallable(new Callable<List<IndividualWalletEntity>>() {
//            @Override
//            public List<IndividualWalletEntity> call() throws Exception {
//                return IndividualWalletManager.getInstance().getWalletList();
//            }
//        }).filter(new Predicate<List<IndividualWalletEntity>>() {
//            @Override
//            public boolean test(List<IndividualWalletEntity> walletEntityList) throws Exception {
//                return walletEntityList != null && !walletEntityList.isEmpty();
//            }
//        }).flatMap(new Function<List<IndividualWalletEntity>, Publisher<?>>() {
//            @Override
//            public Publisher<?> apply(List<IndividualWalletEntity> walletEntityList) throws Exception {
//                return Flowable.fromIterable(walletEntityList);
//            }
//        }).filter(new Predicate<Object>() {
//
//            @Override
//            public boolean test(Object walletEntity) throws Exception {
//                return walletEntity != null && ((IndividualWalletEntity) walletEntity).getPrefixAddress().equals(address);
//            }
//        }).map(new Function<Object, IndividualWalletEntity>() {
//
//            @Override
//            public IndividualWalletEntity apply(Object o) throws Exception {
//                return IndividualWalletTransactionManager.getInstance().getBalanceByAddress((IndividualWalletEntity) o);
//            }
//        }).subscribeOn(Schedulers.io());
//    }
//
//    @Override
//    public Single<BigInteger> getEstimateGas(String from, String to, String memo) {
//        return Single.fromCallable(new Callable<BigInteger>() {
//            @Override
//            public BigInteger call() throws Exception {
//                return Web3jManager.getInstance().getEstimateGas(from, to, memo);
//            }
//        }).subscribeOn(Schedulers.io());
//    }
//
//
//}
