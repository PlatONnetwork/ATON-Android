package com.juzix.wallet.engine;

import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;

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
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
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

    public IndividualTransactionEntity getTransactionByHash(IndividualTransactionEntity individualTransactionEntity) {
//        try {
//            Transaction transaction = Web3jManager.getInstance().getTransactionByHash(individualTransactionEntity.getHash());
//            long latestBlockNumber = Web3jManager.getInstance().getLatestBlockNumber();
//            if (transaction != null) {
//                long blockNumber = NumericUtil.decodeQuantity(transaction.getBlockNumberRaw(), BigInteger.ZERO).longValue();
//                double energonPrice = BigDecimalUtil.div(BigDecimalUtil.mul(transaction.getGas().doubleValue(), transaction.getGasPrice().doubleValue()), 1E18);
//                double value = BigDecimalUtil.div(transaction.getValue().toString(), "1E18");
//                boolean completed = blockNumber > 0 && latestBlockNumber - blockNumber >= 1;
//                IndividualTransactionEntity entity = new IndividualTransactionEntity.Builder(individualTransactionEntity.getUuid(), individualTransactionEntity.getCreateTime(), individualTransactionEntity.getWalletName())
////                        .hash(individualTransactionEntity.getHash())
//                        .fromAddress(transaction.getFrom())
//                        .toAddress(transaction.getTo())
//                        .value(value)
//                        .blockNumber(blockNumber)
//                        .energonPrice(energonPrice)
//                        .latestBlockNumber(latestBlockNumber)
//                        .completed(completed)
////                        .memo(individualTransactionEntity.getMemo())
////                        .value(individualTransactionEntity.getValue())
//                        .nodeAddress(NodeManager.getInstance().getCurNodeAddress())
//                        .build();
//                return entity;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }

    public String sendIndividualTransaction(String privateKey, String from, String toAddress, String amount, long gasPrice, long gasLimit) {

        BigInteger GAS_PRICE = BigInteger.valueOf(gasPrice);
        BigInteger GAS_LIMIT = BigInteger.valueOf(gasLimit);

        Credentials credentials = Credentials.create(privateKey);
        try {
            BigInteger value = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();

            List<RlpType> result = new ArrayList<>();
            result.add(RlpString.create(Numeric.hexStringToByteArray(PlatOnTypeEncoder.encode(new Int64(0)))));
            String txType = Hex.toHexString(RlpEncoder.encode(new RlpList(result)));

            RawTransaction rawTransaction = RawTransaction.createTransaction(Web3jManager.getInstance().getNonce(from), GAS_PRICE, GAS_LIMIT, toAddress, value,
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

    public Single<IndividualTransactionEntity> sendTransaction(String privateKey, String fromAddress, String toAddress, String walletName, String transferAmount, long gasPrice, long gasLimit) {

        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String transactionHash = sendIndividualTransaction(privateKey, fromAddress, toAddress, transferAmount, NumberParserUtils.parseLong(BigDecimalUtil.parseString(gasPrice)), gasLimit);
                if (TextUtils.isEmpty(transactionHash)) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_TRANSFER_FAILED));
                } else {
                    emitter.onSuccess(transactionHash);
                }
            }
        }).flatMap(new Function<String, SingleSource<IndividualTransactionEntity>>() {
            @Override
            public SingleSource<IndividualTransactionEntity> apply(String hash) throws Exception {
                IndividualTransactionEntity individualTransactionEntity = new IndividualTransactionEntity.Builder(UUID.randomUUID().toString(), System.currentTimeMillis(), walletName)
                        .hash(hash)
                        .fromAddress(fromAddress)
                        .toAddress(toAddress)
                        .value(NumberParserUtils.parseDouble(transferAmount))
                        .nodeAddress(NodeManager.getInstance().getCurNodeAddress())
                        .build();
                return Single.fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
//                        return TransactionInfoDao.insertTransaction(individualTransactionEntity.buildIndividualTransactionInfoEntity());
                        return null;
                    }
                }).filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).map(new Function<Boolean, IndividualTransactionEntity>() {
                    @Override
                    public IndividualTransactionEntity apply(Boolean aBoolean) throws Exception {
                        return individualTransactionEntity;
                    }
                }).doOnSuccess(new Consumer<IndividualTransactionEntity>() {
                    @Override
                    public void accept(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                        EventPublisher.getInstance().sendUpdateIndividualWalletTransactionEvent(individualTransactionEntity);
                    }
                }).toSingle();
            }
        }).doOnSuccess(new Consumer<IndividualTransactionEntity>() {
            @Override
            public void accept(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                getIndividualTransactionByLoop(individualTransactionEntity);
            }
        });
    }

    /**
     * 通过轮询获取普通钱包的交易
     */
    public void getIndividualTransactionByLoop(IndividualTransactionEntity transactionEntity) {
        Flowable.interval(Constants.Common.REFRESH_TIME, TimeUnit.MILLISECONDS)
                .map(new Function<Long, IndividualTransactionEntity>() {
                    @Override
                    public IndividualTransactionEntity apply(Long aLong) throws Exception {
                        return getTransactionByHash(transactionEntity);
                    }
                })
                .takeUntil(new Predicate<IndividualTransactionEntity>() {
                    @Override
                    public boolean test(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                        return individualTransactionEntity.isCompleted();
                    }
                })
                .doOnNext(new Consumer<IndividualTransactionEntity>() {
                    @Override
                    public void accept(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                        if (individualTransactionEntity.isCompleted()) {
//                            boolean success = TransactionInfoDao.insertTransaction(individualTransactionEntity.buildIndividualTransactionInfoEntity());
                            boolean success = false;
                            if (success) {
                                EventPublisher.getInstance().sendUpdateIndividualWalletTransactionEvent(individualTransactionEntity);
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<IndividualTransactionEntity>() {
                    @Override
                    public void accept(IndividualTransactionEntity individualTransactionEntity) throws Exception {
                        Log.e(TAG, "getIndividualTransactionByLoop 轮询交易成功" + Thread.currentThread().getName());
                    }
                });
    }
}
