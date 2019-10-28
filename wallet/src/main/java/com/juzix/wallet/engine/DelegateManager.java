package com.juzix.wallet.engine;

import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionReceipt;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.event.EventPublisher;

import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.StakingAmountType;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


public class DelegateManager {
    private static class InstanceHolder {
        private static volatile DelegateManager INSTANCE = new DelegateManager();
    }

    public static DelegateManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Observable<Transaction> delegate(Credentials credentials, String to, String amount, String nodeId, String nodeName, String feeAmount, String transactionType, StakingAmountType stakingAmountType, GasProvider gasProvider) { //这里新修改，传入GasProvider

        return Single
                .fromCallable(new Callable<PlatonSendTransaction>() {
                    @Override
                    public PlatonSendTransaction call() throws Exception {
                        Web3j web3j = Web3jManager.getInstance().getWeb3j();
                        String chainId = NodeManager.getInstance().getChainId();
                        DelegateContract delegateContract = DelegateContract.load(web3j, credentials, NumberParserUtils.parseLong(chainId));
                        return delegateContract.delegateReturnTransaction(nodeId, stakingAmountType, Convert.toVon(amount, Convert.Unit.LAT).toBigInteger(), gasProvider).send();
                    }
                })
                .filter(new Predicate<PlatonSendTransaction>() {
                    @Override
                    public boolean test(PlatonSendTransaction platonSendTransaction) throws Exception {
                        return !TextUtils.isEmpty(platonSendTransaction.getTransactionHash());
                    }
                })
                .switchIfEmpty(new SingleSource<PlatonSendTransaction>() {
                    @Override
                    public void subscribe(SingleObserver<? super PlatonSendTransaction> observer) {
                        observer.onError(new Throwable());
                    }
                })
                .flatMap(new Function<PlatonSendTransaction, SingleSource<Transaction>>() {
                    @Override
                    public SingleSource<Transaction> apply(PlatonSendTransaction platonSendTransaction) throws Exception {
                        return insertTransaction(credentials, platonSendTransaction, to, amount, nodeId, nodeName, feeAmount, transactionType);
                    }
                })
                .toObservable();
    }

    private Single<Transaction> insertTransaction(Credentials credentials, PlatonSendTransaction platonSendTransaction, String to, String amount, String nodeId, String nodeName, String feeAmount, String transactionType) {

        return Single.just(new Transaction.Builder()
                .from(credentials.getAddress())
                .to(to)
                .timestamp(System.currentTimeMillis())
                .txType(transactionType)
                .value(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString())
                .actualTxCost(Convert.toVon(feeAmount, Convert.Unit.LAT).toBigInteger().toString())
                .unDelegation(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString())
                .nodeName(nodeName)
                .nodeId(nodeId)
                .chainId(NodeManager.getInstance().getChainId())
                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                .hash(platonSendTransaction.getTransactionHash())
                .build())
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return TransactionDao.insertTransaction(transaction.toTransactionEntity());
                    }
                })
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        TransactionManager.getInstance().getTransactionByLoop(transaction);
                    }
                })
                .toSingle();
    }

    public Observable<Transaction> withdraw(Credentials credentials, String to, String nodeId, String nodeName, String feeAmount, String stakingBlockNum, String amount, String transactionType, GasProvider GasProvider) {

        return Single.fromCallable(new Callable<PlatonSendTransaction>() {
            @Override
            public PlatonSendTransaction call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                String chainId = NodeManager.getInstance().getChainId();
                DelegateContract delegateContract = DelegateContract.load(web3j, credentials, NumberParserUtils.parseLong(chainId));
                return delegateContract.unDelegateReturnTransaction(nodeId, new BigInteger(stakingBlockNum), Convert.toVon(amount, Convert.Unit.LAT).toBigInteger(), GasProvider).send();
            }
        })
                .filter(new Predicate<PlatonSendTransaction>() {
                    @Override
                    public boolean test(PlatonSendTransaction platonSendTransaction) throws Exception {
                        return !TextUtils.isEmpty(platonSendTransaction.getTransactionHash());
                    }
                })
                .switchIfEmpty(new SingleSource<PlatonSendTransaction>() {
                    @Override
                    public void subscribe(SingleObserver<? super PlatonSendTransaction> observer) {
                        observer.onError(new Throwable());
                    }
                })
                .flatMap(new Function<PlatonSendTransaction, SingleSource<Transaction>>() {
                    @Override
                    public SingleSource<Transaction> apply(PlatonSendTransaction platonSendTransaction) throws Exception {
                        return insertTransaction(credentials, platonSendTransaction, to, amount, nodeId, nodeName, feeAmount, transactionType);
                    }
                })
                .toObservable();

    }

    /**
     * 获取gasprice
     */
    public Single<BigInteger> getGasPrice() {
        return Single.fromCallable(new Callable<BigInteger>() {
            @Override
            public BigInteger call() throws Exception {
                return Web3jManager.getInstance().getWeb3j().platonGasPrice().send().getGasPrice();
            }
        }).onErrorReturnItem(DefaultGasProvider.GAS_PRICE);
    }


}
