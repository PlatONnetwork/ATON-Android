package com.juzix.wallet.engine;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.util.LogUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomObserver;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionReceipt;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.NumericUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusBuilder;
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
import org.web3j.crypto.TransactionDecoder;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * @author matrixelement
 */
public class TransactionManager {

    private final static int MAX_PENDING_TIME = 24 * 60 * 60 * 1000;

    private volatile Map<String, Disposable> mDisposableMap = new HashMap<>();

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

    public Disposable getTaskByHash(String hash) {
        return mDisposableMap.get(hash);
    }

    public Disposable removeTaskByHash(String hash) {
        LogUtils.e("removeTaskByHash:    " + hash);
        return mDisposableMap.remove(hash);
    }

    public void putTask(String hash, Disposable disposable) {
        mDisposableMap.put(hash, disposable);
        LogUtils.e("putTask:    " + hash);
    }

    public void cancelTaskByHash(String hash) {
        Disposable disposable = removeTaskByHash(hash);
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public String sendTransaction(String privateKey, String from, String toAddress, BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit) {

        Credentials credentials = Credentials.create(privateKey);

        try {

            RawTransaction rawTransaction = RawTransaction.createTransaction(Web3jManager.getInstance().getNonce(from), gasPrice, gasLimit, toAddress, amount.toBigInteger(),
                    "");

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);
            String hexValue = Numeric.toHexString(signedMessage);

            PlatonSendTransaction transaction = Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(hexValue).send();

            return transaction.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendTransaction(String signedMessage) {

        try {
            PlatonSendTransaction transaction = Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(signedMessage).send();
            return transaction.getTransactionHash();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public PlatonSendTransaction sendTransactionReturnPlatonSendTransaction(String signedMessage) {
        try {
            return Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(signedMessage).send();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String signTransaction(Credentials credentials, String data, String toAddress, BigDecimal amount, BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit) {

        try {
            RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, amount.toBigInteger(),
                    data);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);

            return Numeric.toHexString(signedMessage);
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
                        putTask(transaction.getHash(), getTransactionByLoop(transaction));
                    }
                });
    }

    /**
     * 通过轮询获取普通钱包的交易
     */
    public Disposable getTransactionByLoop(Transaction transaction) {

        return Flowable
                .interval(Constants.Common.TRANSACTION_STATUS_LOOP_TIME, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Transaction>() {
                    @Override
                    public Transaction apply(Long aLong) throws Exception {
                        Transaction tempTransaction = transaction.clone();
                        //如果pending时间超过4小时，则删除
                        if (System.currentTimeMillis() - transaction.getTimestamp() >= MAX_PENDING_TIME) {
                            tempTransaction.setTxReceiptStatus(TransactionStatus.TIMEOUT.ordinal());
                        } else {
                            TransactionReceipt transactionReceipt = getTransactionReceipt(tempTransaction.getHash());
                            tempTransaction.setTxReceiptStatus(transactionReceipt.getStatus());
                        }
                        return tempTransaction;
                    }
                })
                .takeUntil(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() != TransactionStatus.PENDING;
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() != TransactionStatus.PENDING;
                    }
                })
                .doOnNext(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        cancelTaskByHash(transaction.getHash());
                        if (transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED) {
                            TransactionDao.deleteTransaction(transaction.getHash());
                            LogUtils.e("getIndividualTransactionByLoop 轮询交易成功" + transaction.toString());
                        }
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                    }
                })
                .toObservable()
                .subscribeOn(Schedulers.io())
                .subscribe();

    }

    private TransactionReceipt getTransactionReceipt(String hash) {

        return ServerUtils
                .getCommonApi()
                .getTransactionsStatus(ApiRequestBody.newBuilder()
                        .put("hash", Arrays.asList(hash))
                        .build())
                .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse != null && apiResponseResponse.isSuccessful();
                    }
                })
                .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        List<TransactionReceipt> transactionReceiptList = apiResponseResponse.body().getData();
                        return transactionReceiptList != null && !transactionReceiptList.isEmpty();
                    }
                })
                .map(new Function<Response<ApiResponse<List<TransactionReceipt>>>, TransactionReceipt>() {
                    @Override
                    public TransactionReceipt apply(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse.body().getData().get(0);
                    }
                })
                .defaultIfEmpty(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
                .onErrorReturnItem(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
                .toSingle()
                .blockingGet();


    private TransactionReceipt getTransactionReceipt(String hash) {

        return ServerUtils
                .getCommonApi()
                .getTransactionsStatus(ApiRequestBody.newBuilder()
                        .put("hash", Arrays.asList(hash))
                        .build())
                .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse != null && apiResponseResponse.isSuccessful();
                    }
                })
                .filter(new Predicate<Response<ApiResponse<List<TransactionReceipt>>>>() {
                    @Override
                    public boolean test(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        List<TransactionReceipt> transactionReceiptList = apiResponseResponse.body().getData();
                        return transactionReceiptList != null && !transactionReceiptList.isEmpty();
                    }
                })
                .map(new Function<Response<ApiResponse<List<TransactionReceipt>>>, TransactionReceipt>() {
                    @Override
                    public TransactionReceipt apply(Response<ApiResponse<List<TransactionReceipt>>> apiResponseResponse) throws Exception {
                        return apiResponseResponse.body().getData().get(0);
                    }
                })
                .defaultIfEmpty(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
                .onErrorReturnItem(new TransactionReceipt(TransactionStatus.PENDING.ordinal(), hash))
                .toSingle()
                .blockingGet();


    }
}
