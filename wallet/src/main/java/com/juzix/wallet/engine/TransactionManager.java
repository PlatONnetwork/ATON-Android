package com.juzix.wallet.engine;

import android.text.TextUtils;
import android.util.Pair;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.entity.RPCErrorCode;
import com.juzix.wallet.entity.RPCTransactionResult;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionReceipt;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.platon.PlatOnFunction;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.tx.PlatOnContract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
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

    private volatile Map<String, Pair<Long, Disposable>> mDisposableMap = new LinkedHashMap<>();

    private TransactionManager() {

    }

    private static class InstanceHolder {
        private static volatile TransactionManager INSTANCE = new TransactionManager();
    }

    public static TransactionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Wallet getBalanceByAddress(Wallet walletEntity) {
        return walletEntity;
    }

    public Disposable removeTaskByHash(String hash) {
        Pair<Long, Disposable> pair = mDisposableMap.remove(hash);
        if (pair != null) {
            return pair.second;
        }
        return null;
    }

    /**
     * 是否允许发送交易，与上笔pending中交易间隔超过五分钟
     *
     * @return
     */
    public long getSendTransactionTimeInterval(long currentTime) {

        Set<Map.Entry<String, Pair<Long, Disposable>>> entrySet = mDisposableMap.entrySet();

        return Constants.Common.TRANSACTION_SEND_INTERVAL - (currentTime - entrySet.iterator().next().getValue().first);

    }

    /**
     * 是否允许发送交易，与上笔pending中交易间隔超过五分钟
     *
     * @return
     */
    public boolean isAllowSendTransaction(long currentTime) {

        Set<Map.Entry<String, Pair<Long, Disposable>>> entrySet = mDisposableMap.entrySet();

        return entrySet.isEmpty() || currentTime - entrySet.iterator().next().getValue().first > Constants.Common.TRANSACTION_SEND_INTERVAL;
    }


    public void putTask(String hash, Pair<Long, Disposable> pair) {
        if (!mDisposableMap.containsKey(hash)) {
            mDisposableMap.put(hash, pair);
        }
    }

    public void cancelTaskByHash(String hash) {
        Disposable disposable = removeTaskByHash(hash);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public RPCTransactionResult sendTransaction(String privateKey, String from, String toAddress, BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit) {

        Credentials credentials = Credentials.create(privateKey);

        BigInteger nonce = Web3jManager.getInstance().getNonce(from);

        if (Web3jManager.NONE_NONCE.equals(nonce)) {
            return new RPCTransactionResult(RPCErrorCode.CONNECT_TIMEOUT);
        }

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, amount.toBigInteger(),
                "");

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);

        return getTransactionResult(Numeric.toHexString(signedMessage));
    }

    public RPCTransactionResult sendTransaction(PlatOnContract platOnContract, Credentials credentials, PlatOnFunction platOnFunction) throws IOException {

        String signedMessage = signedTransaction(platOnContract, credentials, platOnFunction.getGasProvider().getGasPrice(), platOnFunction.getGasProvider().getGasLimit(), platOnContract.getContractAddress(), platOnFunction.getEncodeData(), BigInteger.ZERO);

        return getTransactionResult(signedMessage);

    }

    private String signedTransaction(PlatOnContract platOnContract, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String to,
                                     String data, BigInteger value) throws IOException {

        RawTransactionManager rawTransactionManager = (RawTransactionManager) platOnContract.getTransactionManager();

        BigInteger nonce = Web3jManager.getInstance().getNonce(credentials.getAddress());

        return rawTransactionManager.signedTransaction(RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                data));

    }


    /**
     * 获取交易hash，交易读超时的话就使用本地hash
     *
     * @param hexValue
     * @return
     */
    private RPCTransactionResult getTransactionResult(String hexValue) {
        RPCTransactionResult rpcTransactionResult = null;
        try {
            String hash = Web3jManager.getInstance().getWeb3j().platonSendRawTransaction(hexValue).send().getTransactionHash();
            rpcTransactionResult = new RPCTransactionResult(RPCErrorCode.SUCCESS, hash);
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            rpcTransactionResult = new RPCTransactionResult(RPCErrorCode.SOCKET_TIMEOUT, Hash.sha3(hexValue));
        } catch (ClientConnectionException e) {
            rpcTransactionResult = new RPCTransactionResult(RPCErrorCode.CONNECT_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rpcTransactionResult;
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
                RPCTransactionResult transactionResult = sendTransaction(privateKey, fromAddress, toAddress, transferAmount, gasPrice, gasLimit);
                if (TextUtils.isEmpty(transactionResult.getHash())) {
                    emitter.onError(new CustomThrowable(transactionResult.getErrCode()));
                } else {
                    emitter.onSuccess(transactionResult.getHash());
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
                        putTask(transaction.getHash(), new Pair<>(transaction.getTimestamp(), getTransactionByLoop(transaction)));
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
                        if (System.currentTimeMillis() - transaction.getTimestamp() >= NumberParserUtils.parseLong(AppConfigManager.getInstance().getTimeout())) {
                            tempTransaction.setTxReceiptStatus(TransactionStatus.TIMEOUT.ordinal());
                        } else {
                            TransactionReceipt transactionReceipt = getTransactionReceipt(tempTransaction.getHash());
                            tempTransaction.setTxReceiptStatus(transactionReceipt.getStatus());
                            tempTransaction.setTotalReward(transactionReceipt.getTotalReward());
                            if (tempTransaction.getTxType() == TransactionType.UNDELEGATE) {
                                tempTransaction.setValue(BigDecimalUtil.add(transaction.getUnDelegation(), transactionReceipt.getTotalReward()).toPlainString());
                            }
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
                        removeTaskByHash(transaction.getHash());
                        if (transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED) {
                            TransactionDao.deleteTransaction(transaction.getHash());
                        } else if (transaction.getTxReceiptStatus() == TransactionStatus.TIMEOUT) {
                            TransactionDao.insertTransaction(transaction.toTransactionEntity());
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

    }
}
