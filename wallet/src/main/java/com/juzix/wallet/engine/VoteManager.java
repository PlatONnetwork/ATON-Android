package com.juzix.wallet.engine;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.juzhen.framework.util.MapUtils;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.db.sqlite.TransactionDao;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.TransactionExtra;
import com.juzix.wallet.entity.TransactionStatus;
import com.juzix.wallet.entity.TransactionType;
import com.juzix.wallet.entity.VoteTrasactionExtra;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.AppUtil;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JSONUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.contracts.TicketContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.DefaultWasmGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * @author matrixelement
 */
public class VoteManager {

    private static final String TAG = VoteManager.class.getSimpleName();
    public static final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
    public static final BigInteger GAS_DEPLOY_CONTRACT = BigInteger.valueOf(240_943_980);

    private VoteManager() {

    }

    private static class InstanceHolder {
        private static volatile VoteManager INSTANCE = new VoteManager();
    }

    public static VoteManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<Long> getPoolRemainder() {
        return Single.fromCallable(new Callable<Long>() {
            @Override
            public Long call() {
                try {
                    Web3j web3j = Web3jManager.getInstance().getWeb3j();
                    TicketContract ticketContract = TicketContract.load(
                            web3j,
                            new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                            new DefaultWasmGasProvider());
                    return NumberParserUtils.parseLong(ticketContract.GetPoolRemainder().send());
                } catch (Exception exp) {
                    return -1L;
                }
            }
        });
    }

    public Single<String> getTicketPrice() {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() {
                try {
                    Web3j web3j = Web3jManager.getInstance().getWeb3j();
                    TicketContract ticketContract = TicketContract.load(
                            web3j,
                            new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                            new DefaultWasmGasProvider());
                    return ticketContract.GetTicketPrice().send();
                } catch (Exception exp) {
                    return "0";
                }
            }
        });
    }

    public Single<Transaction> submitVote(Credentials credentials, Wallet walletEntity, String nodeId, String nodeName, String ticketNum, String ticketPrice, String deposit, String feeAmount) {

        BigInteger value = BigDecimalUtil.mul(ticketPrice, ticketNum).toBigInteger();

        return voteTicket(credentials, ticketPrice, ticketNum, nodeId)
                .filter(new Predicate<TransactionReceipt>() {
                    @Override
                    public boolean test(TransactionReceipt transactionReceipt) throws Exception {
                        return transactionReceipt != null && !TextUtils.isEmpty(transactionReceipt.getTransactionHash());
                    }
                })
                .map(new Function<TransactionReceipt, Transaction>() {
                    @Override
                    public Transaction apply(TransactionReceipt transactionReceipt) throws Exception {
                        VoteTrasactionExtra voteTransactionExtra = new VoteTrasactionExtra(ticketPrice, ticketNum, nodeId, nodeName, deposit);
//                        TransactionExtra transactionExtra = new TransactionExtra(TransactionType.VOTETICKET.getTxTypeName(), JSONUtil.toJSONString(voteTransactionExtra), "");
//                        return new Transaction.Builder()
//                                .hash(transactionReceipt.getTransactionHash())
//                                .from(walletEntity.getPrefixAddress())
//                                .to(TicketContract.CONTRACT_ADDRESS)
//                                .senderWalletName(walletEntity.getName())
//                                .createTime(System.currentTimeMillis())
//                                .actualTxCost(feeAmount)
//                                .txInfo(JSONUtil.toJSONString(transactionExtra))
//                                .txType(TransactionType.VOTETICKET.getTxTypeName())
//                                .value(String.valueOf(value.doubleValue()))
//                                .txReceiptStatus(String.valueOf(TransactionStatus.PENDING.ordinal()))
//                                .chainId(NodeManager.getInstance().getChainId())
//                                .build();
                        return null;
                    }
                })
                .toSingle()
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction transaction) throws Exception {
                        boolean success = TransactionDao.insertTransaction(transaction.toTransactionEntity());
                        if (success) {
                            EventPublisher.getInstance().sendUpdateTransactionEvent(transaction);
                        }
                        getVotedTransactionByLoop(transaction);
                    }
                });
    }

    private Single<TransactionReceipt> voteTicket(Credentials credentials, String ticketPrice, String ticketNum, String candidateId) {
        return Single
                .fromCallable(new Callable<TransactionReceipt>() {
                    @Override
                    public TransactionReceipt call() throws Exception {
                        Web3j web3j = Web3jManager.getInstance().getWeb3j();
                        TicketContract ticketContract = TicketContract.load(web3j, credentials, 106, new DefaultWasmGasProvider());
                        return ticketContract.VoteTicket(new BigInteger(ticketNum), new BigInteger(ticketPrice), candidateId).send();
                    }
                });
    }

    /**
     * 发送投票交易
     *
     * @param hash
     * @return
     */
    private Single<TransactionReceipt> sendTransaction(String hash) {
        return Single.fromCallable(new Callable<TransactionReceipt>() {
            @Override
            public TransactionReceipt call() {
                PollingTransactionReceiptProcessor transactionReceiptProcessor = new PollingTransactionReceiptProcessor(
                        Web3jManager.getInstance().getWeb3j(), TransactionManager.DEFAULT_POLLING_FREQUENCY, TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
                TransactionReceipt receipt = null;
                try {
                    receipt = transactionReceiptProcessor.waitForTransactionReceipt(hash);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TransactionException e) {
                    e.printStackTrace();
                }
                return receipt;
            }
        });
    }

    private Single<String> getVoteTicketEventEvents(TransactionReceipt receipt) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                TicketContract ticketContract = TicketContract.load(web3j,
                        new ReadonlyTransactionManager(web3j, TicketContract.CONTRACT_ADDRESS),
                        new DefaultWasmGasProvider());
                List<TicketContract.VoteTicketEventEventResponse> responses = ticketContract.getVoteTicketEventEvents(receipt);
                if (responses != null && !responses.isEmpty()) {
                    return responses.get(0).param1;
                }
                return null;
            }
        });
    }

    @SuppressLint("CheckResult")
    private void getVotedTransactionByLoop(Transaction transaction) {
        sendTransaction(transaction.getHash())
                .flatMap(new Function<TransactionReceipt, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(TransactionReceipt transactionReceipt) throws Exception {
                        return getVoteTicketEventEvents(transactionReceipt);
                    }
                })
                .map(new Function<String, JSONObject>() {
                    @Override
                    public JSONObject apply(String resp) throws Exception {
                        return JSONObject.parseObject(resp);
                    }
                })
                .map(new Function<JSONObject, Transaction>() {
                    @Override
                    public Transaction apply(JSONObject jsonObject) throws Exception {
                        boolean success = jsonObject.getBoolean("Ret");
                        TransactionStatus transactionStatus = success ? TransactionStatus.SUCCESSED : TransactionStatus.FAILED;
                        Transaction trans = transaction.clone();
                        trans.setTxReceiptStatus(String.valueOf(transactionStatus.ordinal()));
                        return trans;
                    }
                })
                .onErrorResumeNext(new Function<Throwable, SingleSource<Transaction>>() {
                    @Override
                    public SingleSource<Transaction> apply(Throwable throwable) throws Exception {
                        Transaction trans = transaction.clone();
                        trans.setTxReceiptStatus(String.valueOf(TransactionStatus.FAILED.ordinal()));
                        return Single.just(trans);
                    }
                })
                .doOnSuccess(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction t) throws Exception {
                        if (t.getTxReceiptStatus() == TransactionStatus.FAILED) {
                            TransactionDao.insertTransaction(t.toTransactionEntity());
                        } else {
                            TransactionDao.deleteTransaction(t.getHash());
                        }
                        EventPublisher.getInstance().sendUpdateTransactionEvent(t);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Consumer<Transaction>() {
                    @Override
                    public void accept(Transaction tran) throws Exception {
                        Log.e(TAG, "投票完成" + tran.toString());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });
    }
}
