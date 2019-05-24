package com.juzix.wallet.engine;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.NumericUtil;

import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.PlatOnTypeEncoder;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.rlp.RlpEncoder;
import org.web3j.rlp.RlpList;
import org.web3j.rlp.RlpString;
import org.web3j.rlp.RlpType;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
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
        walletEntity.setBalance(Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress()));
        return walletEntity;
    }

    public String sendTransaction(String privateKey, String from, String toAddress, BigDecimal amount, long gasPrice, long gasLimit) {

        BigInteger GAS_PRICE = BigInteger.valueOf(gasPrice);
        BigInteger GAS_LIMIT = BigInteger.valueOf(gasLimit);

        Credentials credentials = Credentials.create(privateKey);

        try {

            List<RlpType> result = new ArrayList<>();
            result.add(RlpString.create(Numeric.hexStringToByteArray(PlatOnTypeEncoder.encode(new Int64(0)))));
            String txType = Hex.toHexString(RlpEncoder.encode(new RlpList(result)));

            RawTransaction rawTransaction = RawTransaction.createTransaction(Web3jManager.getInstance().getNonce(from), GAS_PRICE, GAS_LIMIT, toAddress, amount.toBigInteger(),
                    txType);

            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
            String hexValue = Numeric.toHexString(signedMessage);

            EthSendTransaction transaction = Web3jManager.getInstance().getWeb3j().ethSendRawTransaction(hexValue).send();

            return transaction.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Single<Transaction> sendTransaction(String privateKey, String fromAddress, String toAddress, String walletName, BigDecimal transferAmount, BigDecimal feeAmount, long gasPrice, long gasLimit) {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String transactionHash = sendTransaction(privateKey, fromAddress, toAddress, transferAmount, NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)), gasLimit);
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
                        .txType(TransactionType.TRANSFER.getTxTypeName())
                        .createTime(System.currentTimeMillis())
                        .txReceiptStatus(String.valueOf(TransactionStatus.PENDING.ordinal()))
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
    @SuppressLint("CheckResult")
    public void getTransactionByLoop(Transaction trans) {
        Flowable.interval(Constants.Common.TRANSACTION_STATUS_LOOP_TIME, TimeUnit.MILLISECONDS)
                .map(new Function<Long, Optional<org.web3j.protocol.core.methods.response.Transaction>>() {
                    @Override
                    public Optional<org.web3j.protocol.core.methods.response.Transaction> apply(Long aLong) throws Exception {
                        org.web3j.protocol.core.methods.response.Transaction transaction = Web3jManager.getInstance().getTransactionByHash(trans.getHash());
                        return new Optional<org.web3j.protocol.core.methods.response.Transaction>(transaction);
                    }
                })
                .takeUntil(new Predicate<Optional<org.web3j.protocol.core.methods.response.Transaction>>() {
                    @Override
                    public boolean test(Optional<org.web3j.protocol.core.methods.response.Transaction> optional) throws Exception {
                        return getTransactionStatus(optional) == TransactionStatus.SUCCESSED;
                    }
                })
                .map(new Function<Optional<org.web3j.protocol.core.methods.response.Transaction>, Transaction>() {
                    @Override
                    public Transaction apply(Optional<org.web3j.protocol.core.methods.response.Transaction> optional) throws Exception {
                        org.web3j.protocol.core.methods.response.Transaction transaction = optional.get();
                        double actualTxCost = BigDecimalUtil.mul(transaction.getGas().doubleValue(), transaction.getGasPrice().doubleValue());
                        Transaction t = trans.clone();
                        t.setTxReceiptStatus(String.valueOf(TransactionStatus.SUCCESSED.ordinal()));
                        t.setActualTxCost(String.valueOf(actualTxCost));
                        return t;
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        return transaction.getTxReceiptStatus() == TransactionStatus.SUCCESSED;
                    }
                })
                .filter(new Predicate<Transaction>() {
                    @Override
                    public boolean test(Transaction transaction) throws Exception {
                        //删除数据里的数据
                        return TransactionDao.deleteTransaction(transaction.getHash());
                    }
                })
                .doOnNext(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        Log.e(TAG, "getIndividualTransactionByLoop 轮询交易成功" + Thread.currentThread().getName());
                    }
                });
    }

    private TransactionStatus getTransactionStatus(Optional<org.web3j.protocol.core.methods.response.Transaction> optional) {
        if (optional.isEmpty()) {
            return TransactionStatus.FAILED;
        } else {
            Log.e(TAG, "getTransactionStatus 轮询交易");
            org.web3j.protocol.core.methods.response.Transaction transaction = optional.get();
            long latestBlockNumber = Web3jManager.getInstance().getLatestBlockNumber();
            long blockNumber = NumericUtil.decodeQuantity(transaction.getBlockNumberRaw(), BigInteger.ZERO).longValue();
            return blockNumber > 0 && latestBlockNumber - blockNumber >= 1 ? TransactionStatus.SUCCESSED : TransactionStatus.PENDING;
        }
    }
}
