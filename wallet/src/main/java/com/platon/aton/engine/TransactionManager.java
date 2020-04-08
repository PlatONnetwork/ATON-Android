package com.platon.aton.engine;

import android.text.TextUtils;

import com.platon.aton.app.CustomThrowable;
import com.platon.aton.db.sqlite.TransactionDao;
import com.platon.aton.entity.RPCErrorCode;
import com.platon.aton.entity.RPCNonceResult;
import com.platon.aton.entity.RPCTransactionResult;
import com.platon.aton.entity.SubmitTransactionData;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionReceipt;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.BigIntegerUtil;
import com.platon.aton.utils.JSONUtil;
import com.platon.aton.utils.NumberParserUtils;
import com.platon.aton.utils.SignCodeUtils;
import com.platon.framework.app.Constants;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.MapUtils;

import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Hash;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.Sign;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.platon.ErrorCode;
import org.web3j.platon.PlatOnFunction;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.tx.PlatOnContract;
import org.web3j.tx.RawTransactionManager;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
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

    private static final String UTF_8 = "UTF-8";
    private volatile Map<String, Disposable> mDisposableMap = new HashMap<>();
    private volatile Map<String, Object> mPendingMap = new HashMap<>();

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
        return mDisposableMap.remove(hash);
    }

    public void putPendingTransaction(String from, long timeStamp) {
        mPendingMap.put(buildPendingMapKey(from), timeStamp);
    }

    public long getPendingTransactionTimeStamp(String from) {
        return MapUtils.getLong(mPendingMap, buildPendingMapKey(from));
    }

    public void removePendingTransaction(String from) {
        mPendingMap.remove(buildPendingMapKey(from));
    }

    /**
     * 是否允许发送交易，与上笔pending中交易间隔超过五分钟
     *
     * @return
     */
    public long getSendTransactionTimeInterval(String from, long currentTime) {

        long timestamp = getPendingTransactionTimeStamp(from);

        return Constants.Common.TRANSACTION_SEND_INTERVAL - (currentTime - timestamp);

    }

    /**
     * 是否允许发送交易，与上笔pending中交易间隔超过五分钟
     *
     * @return
     */
    public boolean isAllowSendTransaction(String from, long currentTime) {

        long timestamp = getPendingTransactionTimeStamp(from);

        return currentTime - timestamp > Constants.Common.TRANSACTION_SEND_INTERVAL;
    }


    public void putTask(String hash, Disposable disposable) {
        if (!mDisposableMap.containsKey(hash)) {
            mDisposableMap.put(hash, disposable);
        }
    }

    public void cancelTaskByHash(String hash) {
        Disposable disposable = removeTaskByHash(hash);
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public Single<RPCTransactionResult> sendContractTransaction(PlatOnContract platOnContract, Credentials credentials, PlatOnFunction platOnFunction, String nonce) throws IOException {

        return signedTransaction(platOnContract, credentials, platOnFunction.getGasProvider().getGasPrice(), platOnFunction.getGasProvider().getGasLimit(), platOnContract.getContractAddress(), platOnFunction.getEncodeData(), BigInteger.ZERO, nonce)
                .flatMap(new Function<String, SingleSource<RPCTransactionResult>>() {
                    @Override
                    public SingleSource<RPCTransactionResult> apply(String signedMessage) throws Exception {
                        return submitTransaction(createSigned(credentials.getEcKeyPair(), signedMessage, ""), signedMessage, "");
                    }
                });
    }

    /**
     * 提交交易
     *
     * @param sign
     * @param signedMessage
     * @param remark
     * @return
     */
    public Single<RPCTransactionResult> submitTransaction(String sign, String signedMessage, String remark) {

        return ServerUtils.getCommonApi().submitSignedTransaction(ApiRequestBody.newBuilder()
                .put("data", JSONUtil.toJSONString(new SubmitTransactionData(signedMessage, remark)))
                .put("sign", sign)
                .build())
                .flatMap(new Function<Response<ApiResponse<String>>, SingleSource<RPCTransactionResult>>() {
                    @Override
                    public SingleSource<RPCTransactionResult> apply(Response<ApiResponse<String>> apiResponseResponse) {

                        return Single.create(new SingleOnSubscribe<RPCTransactionResult>() {
                            @Override
                            public void subscribe(SingleEmitter<RPCTransactionResult> emitter) {

                                if (apiResponseResponse != null) {
                                    if (apiResponseResponse.isSuccessful() && apiResponseResponse.body().getErrorCode() == ErrorCode.SUCCESS) {
                                        emitter.onSuccess(new RPCTransactionResult(RPCErrorCode.SUCCESS, apiResponseResponse.body().getData()));
                                    } else {
                                        emitter.onSuccess(new RPCTransactionResult(apiResponseResponse.body().getErrorCode()));
                                    }
                                }
                            }
                        });
                    }
                })
                .onErrorReturn(new Function<Throwable, RPCTransactionResult>() {
                    @Override
                    public RPCTransactionResult apply(Throwable throwable) {
                        if (throwable instanceof SocketTimeoutException) {
                            return new RPCTransactionResult(RPCErrorCode.SOCKET_TIMEOUT, Hash.sha3(signedMessage));
                        } else if (throwable instanceof ClientConnectionException) {
                            return new RPCTransactionResult(RPCErrorCode.CONNECT_TIMEOUT);
                        }
                        return null;
                    }
                });

    }


    public Single<BigInteger> getNonce(String from) {

        return Single
                .fromCallable(new Callable<RPCNonceResult>() {
                    @Override
                    public RPCNonceResult call() throws Exception {
                        return Web3jManager.getInstance().getNonce(from);
                    }
                })
                .flatMap(new Function<RPCNonceResult, SingleSource<BigInteger>>() {
                    @Override
                    public SingleSource<BigInteger> apply(RPCNonceResult rpcNonceResult) throws Exception {
                        return Single.create(new SingleOnSubscribe<BigInteger>() {
                            @Override
                            public void subscribe(SingleEmitter<BigInteger> emitter) throws Exception {
                                if (rpcNonceResult.isSuccessful()) {
                                    emitter.onSuccess(rpcNonceResult.getNonce());
                                } else {
                                    emitter.onError(new CustomThrowable(rpcNonceResult.getErrCode()));
                                }
                            }
                        });
                    }
                });

    }

    private Single<String> getSignedMessageSingle(ECKeyPair ecKeyPair, String from, String toAddress, BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {

        return Single
                .fromCallable(new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        return getSignedMessage(ecKeyPair, from, toAddress, amount, gasPrice, gasLimit, nonce);
                    }
                });
    }

    private String getSignedMessage(ECKeyPair ecKeyPair, String from, String toAddress, BigDecimal amount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce) {


        Credentials credentials = Credentials.create(ecKeyPair);

        RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, gasPrice, gasLimit, toAddress, amount.toBigInteger(),
                "");

        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, NumberParserUtils.parseLong(NodeManager.getInstance().getChainId()), credentials);

        return Numeric.toHexString(signedMessage);
    }

    private String createSigned(ECKeyPair ecKeyPair, String signedData, String remark) {
        byte[] signedDataByte = Numeric.hexStringToByteArray(signedData);
        byte[] remarkByte = new byte[0];
        if (!TextUtils.isEmpty(remark)) {
            try {
                remarkByte = remark.getBytes(UTF_8);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        byte[] message = new byte[signedDataByte.length + remarkByte.length];
        System.arraycopy(signedDataByte, 0, message, 0, signedDataByte.length);
        System.arraycopy(remarkByte, 0, message, signedDataByte.length, remarkByte.length);

        byte[] messageHash = Hash.sha3(message);

        //签名 Sign.signMessage(message, ecKeyPair, true) 和  Sign.signMessage(messageHash, ecKeyPair, false) 等效
        Sign.SignatureData signatureData = Sign.signMessage(messageHash, ecKeyPair, false);

        byte[] signByte = SignCodeUtils.encode(signatureData);

        //报文中sign数据， signHex等于下面打印的值
        return Numeric.toHexString(signByte);
    }

    private String getSignedData(ECKeyPair ecKeyPair, String data) {

        byte[] message = new byte[0];
        try {
            message = data.getBytes(UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        byte[] messageHash = Hash.sha3(message);

        //签名 Sign.signMessage(message, ecKeyPair, true) 和  Sign.signMessage(messageHash, ecKeyPair, false) 等效
        Sign.SignatureData signatureData = Sign.signMessage(messageHash, ecKeyPair, false);

        byte[] signByte = SignCodeUtils.encode(signatureData);

        //报文中sign数据， signHex等于下面打印的值
        return Numeric.toHexString(signByte);
    }

    private Single<String> signedTransaction(PlatOnContract platOnContract, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String to,
                                             String data, BigInteger value, String nonce) throws IOException {

        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return ((RawTransactionManager) platOnContract.getTransactionManager()).signedTransaction(RawTransaction.createTransaction(
                        BigIntegerUtil.toBigInteger(nonce),
                        gasPrice,
                        gasLimit,
                        to,
                        value,
                        data));
            }
        });
    }


    /**
     * 获取交易hash，交易读超时的话就使用本地hash
     *
     * @param hexValue
     * @return
     */
    public RPCTransactionResult getTransactionResult(String hexValue) {
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


    public String sendContractTransaction(String signedMessage) {

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
        } catch (Exception e) {
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

    public Single<Transaction> sendTransferTransaction(ECKeyPair ecKeyPair, String fromAddress, String toAddress, String walletName, BigDecimal transferAmount, BigDecimal feeAmount, BigInteger gasPrice, BigInteger gasLimit, BigInteger nonce, String remark) {

        return getSignedMessageSingle(ecKeyPair, fromAddress, toAddress, transferAmount, gasPrice, gasLimit, nonce)
                .flatMap(new Function<String, SingleSource<Transaction>>() {
                    @Override
                    public SingleSource<Transaction> apply(String signedMessage) throws Exception {
                        return submitTransaction(createSigned(ecKeyPair, signedMessage, remark), signedMessage, remark)
                                .flatMap(new Function<RPCTransactionResult, SingleSource<RPCTransactionResult>>() {
                                    @Override
                                    public SingleSource<RPCTransactionResult> apply(RPCTransactionResult transactionResult) throws Exception {
                                        return createRPCTransactionResult(transactionResult);
                                    }
                                })
                                .map(new Function<RPCTransactionResult, Transaction>() {
                                    @Override
                                    public Transaction apply(RPCTransactionResult rpcTransactionResult) throws Exception {
                                        return new Transaction.Builder()
                                                .hash(rpcTransactionResult.getHash())
                                                .from(fromAddress)
                                                .to(toAddress)
                                                .senderWalletName(walletName)
                                                .value(transferAmount.toPlainString())
                                                .chainId(NodeManager.getInstance().getChainId())
                                                .txType(String.valueOf(TransactionType.TRANSFER.getTxTypeValue()))
                                                .timestamp(System.currentTimeMillis())
                                                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                                                .actualTxCost(feeAmount.toPlainString())
                                                .remark(remark)
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
                                        putPendingTransaction(transaction.getFrom(), transaction.getTimestamp());
                                        putTask(transaction.getHash(), getTransactionByLoop(transaction));
                                    }
                                });
                    }
                });
    }

    /**
     * 通过轮询获取普通钱包的交易
     */
    public Disposable getTransactionByLoop(Transaction transaction) {

        Disposable disposable = mDisposableMap.get(transaction.getHash());

        return disposable != null ? disposable : Flowable
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
                            tempTransaction.setBlockNumber(transactionReceipt.getBlockNumber());
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
                        removePendingTransaction(transaction.getFrom());
                        //更新数据库中交易的状态
                        TransactionDao.insertTransaction(transaction.toTransactionEntity());
                        //如果是完成了的交易，则从数据库中删除
                        if (transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED || transaction.getTxReceiptStatus() == TransactionStatus.FAILED) {
                            TransactionDao.deleteTransaction(transaction.getHash());
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

    private String buildPendingMapKey(String from) {
        return from.toLowerCase() + "-" + NodeManager.getInstance().getChainId();
    }

    private Single<RPCTransactionResult> createRPCTransactionResult(RPCTransactionResult rpcTransactionResult) {
        return Single.create(new SingleOnSubscribe<RPCTransactionResult>() {
            @Override
            public void subscribe(SingleEmitter<RPCTransactionResult> emitter) throws Exception {
                if (TextUtils.isEmpty(rpcTransactionResult.getHash())) {
                    emitter.onError(new CustomThrowable(rpcTransactionResult.getErrCode()));
                } else {
                    emitter.onSuccess(rpcTransactionResult);
                }
            }
        });
    }
}
