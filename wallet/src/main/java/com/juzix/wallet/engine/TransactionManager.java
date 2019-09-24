package com.juzix.wallet.engine;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.util.LogUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.NumericUtil;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;
import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.PlatOnTypeEncoder;
import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.platon.FunctionType;
import org.web3j.platon.PlatOnFunction;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.PlatonGetBalance;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class TransactionManager {

    private final static String TAG = TransactionManager.class.getSimpleName();

    private TransactionManager() {

    }

    private static class InstanceHolder {
        private static volatile TransactionManager INSTANCE = new TransactionManager();
    }

    public static TransactionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Wallet getBalanceByAddress(Wallet walletEntity) {
//        walletEntity.setBalance(Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress()));
        return walletEntity;
    }

    public String sendTransaction(String privateKey, String from, String toAddress, BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit) {

        Credentials credentials = Credentials.create(privateKey);

        try {
            List<RlpType> result = new ArrayList<>();

            String txType = Hex.toHexString(RlpEncoder.encode(new RlpList(result)));

            RawTransaction rawTransaction = RawTransaction.createTransaction(Web3jManager.getInstance().getNonce(from), gasPrice, gasLimit, toAddress, amount.toBigInteger(),
                    txType);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);
            String hexValue = Numeric.toHexString(signedMessage);

            PlatonSendTransaction transaction = Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(hexValue).send();

            return transaction.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Single<Transaction> sendTransaction(String privateKey, String fromAddress, String toAddress, String walletName, BigDecimal transferAmount, BigDecimal feeAmount, BigInteger gasPrice, BigInteger gasLimit) {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String transactionHash = sendTransaction(privateKey, fromAddress, toAddress, transferAmount, gasPrice, gasLimit);
                if (TextUtils.isEmpty(transactionHash)) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_TRANSFER_FAILED));
                } else {
                    emitter.onSuccess(transactionHash);
                }
            }
        }).map(new Function<String, Transaction>() {
            @Override
            public Transaction apply(String hash) throws Exception {
                return new Transaction.Builder()
                        .hash(hash)
                        .from(fromAddress)
                        .to(toAddress)
                        .senderWalletName(walletName)
                        .value(transferAmount.toPlainString())
                        .chainId(NodeManager.getInstance().getChainId())
                        .txType(String.valueOf(TransactionType.TRANSFER.getTxTypeValue()))
                        .timestamp(System.currentTimeMillis())
                        .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                        .actualTxCost(feeAmount.toPlainString())
                        .build();
            }
        }).filter(new Predicate<Transaction>() {
            @Override
            public boolean test(Transaction transaction) throws Exception {
                return TransactionDao.insertTransaction(transaction.toTransactionEntity());
            }
        })
                .toSingle()
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                        getTransactionByLoop(transaction);
                    }
                });
    }


    /**
     * 通过轮询获取普通钱包的交易
     */
    public void getTransactionByLoop(Transaction transaction) {

        Flowable
                .interval(Constants.Common.TRANSACTION_STATUS_LOOP_TIME, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Transaction>() {
                    @Override
                    public Transaction apply(Long aLong) throws Exception {
                        Transaction trans = getTransactionByHash(transaction);
                        long latestBlockNumber = Web3jManager.getInstance().getLatestBlockNumber();
                        long blockNumber = trans.getBlockNumber();
                        TransactionStatus transactionStatus = blockNumber > 0 && latestBlockNumber - blockNumber >= 1 ? TransactionStatus.SUCCESSED : TransactionStatus.PENDING;
                        trans.setTxReceiptStatus(transactionStatus.ordinal());
                        return trans;
                    }
                })
                .takeUntil(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED;
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED;
                    }
                })
                .doOnNext(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        TransactionDao.deleteTransaction(transaction.getHash());
                    }
                })
                .doOnNext(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                    }
                })
                .toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe(new CustomObserver<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) {
                        LogUtils.e("getIndividualTransactionByLoop 轮询交易成功" + Thread.currentThread().getName());
                    }

                    @Override
                    public void accept(Throwable throwable) {
                        super.accept(throwable);
                        if (throwable instanceof CustomThrowable) {
                            LogUtils.e("getIndividualTransactionByLoop 轮询交易失败" + ((CustomThrowable) throwable).getDetailMsgRes());
                        } else {
                            LogUtils.e("getIndividualTransactionByLoop 轮询交易失败" + throwable.getMessage());
                        }
                    }
                });

    }

    private Transaction getTransactionByHash(final Transaction transaction) {
        final Transaction tempTransaction = transaction.clone();
        return Flowable.create(new FlowableOnSubscribe<Transaction>() {
            @Override
            public void subscribe(FlowableEmitter<Transaction> emitter) throws Exception {
                org.web3j.protocol.core.methods.response.Transaction transaction = Web3jManager.getInstance().getTransactionByHash(tempTransaction.getHash());
                if (transaction == null) {
                    emitter.onNext(tempTransaction);
                } else {
                    double actualTxCost = BigDecimalUtil.mul(transaction.getGas().doubleValue(), transaction.getGasPrice().doubleValue());
                    tempTransaction.setActualTxCost(String.valueOf(actualTxCost));
                    tempTransaction.setBlockNumber(transaction.getBlockNumber().longValue());
                    emitter.onNext(tempTransaction);
                }
            }
        }, BackpressureStrategy.BUFFER).blockingFirst();
    }
}
