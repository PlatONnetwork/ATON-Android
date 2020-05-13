package com.platon.aton.engine;

import android.text.TextUtils;

import com.platon.aton.app.CustomThrowable;
import com.platon.aton.db.sqlite.TransactionDao;
import com.platon.aton.entity.RPCTransactionResult;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.TransactionStatus;
import com.platon.aton.entity.TransactionType;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.BigIntegerUtil;
import com.platon.aton.utils.NumberParserUtils;

import org.web3j.abi.datatypes.BytesType;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.platon.ContractAddress;
import org.web3j.platon.FunctionType;
import org.web3j.platon.PlatOnFunction;
import org.web3j.platon.StakingAmountType;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.platon.contracts.RewardContract;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.GasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


public class DelegateManager {

    private static class InstanceHolder {
        private static volatile DelegateManager INSTANCE = new DelegateManager();
    }

    public static DelegateManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<Transaction> delegate(Credentials credentials, String to, String amount, String nodeId, String nodeName, String transactionType, StakingAmountType stakingAmountType, GasProvider gasProvider,String nonce) { //这里新修改，传入GasProvider

        return Single
                .fromCallable(new Callable<RPCTransactionResult>() {
                    @Override
                    public RPCTransactionResult call() throws Exception {
                        Web3j web3j = Web3jManager.getInstance().getWeb3j();
                        String chainId = NodeManager.getInstance().getChainId();
                        DelegateContract delegateContract = DelegateContract.load(web3j, credentials, NumberParserUtils.parseLong(chainId));
                        PlatOnFunction function = new PlatOnFunction(FunctionType.DELEGATE_FUNC_TYPE,
                                Arrays.asList(new Uint16(stakingAmountType.getValue())
                                        , new BytesType(Numeric.hexStringToByteArray(nodeId))
                                        , new Uint256(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger())), gasProvider);
                        return TransactionManager.getInstance().sendContractTransaction(delegateContract, credentials, function,nonce).blockingGet();
                    }
                })
                .flatMap(new Function<RPCTransactionResult, SingleSource<RPCTransactionResult>>() {
                    @Override
                    public SingleSource<RPCTransactionResult> apply(RPCTransactionResult transactionResult) throws Exception {
                        return createRPCTransactionResult(transactionResult);
                    }
                })
                .flatMap(new Function<RPCTransactionResult, SingleSource<Transaction>>() {
                    @Override
                    public SingleSource<Transaction> apply(RPCTransactionResult transactionResult) throws Exception {
                        return insertTransaction(credentials, transactionResult.getHash(), to, amount, nodeId, nodeName, BigIntegerUtil.mul(gasProvider.getGasLimit(), gasProvider.getGasPrice()), transactionType);
                    }
                });
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

    private Single<Transaction> insertTransaction(Credentials credentials, String hash, String to, String amount, String nodeId, String nodeName, String feeAmount, String transactionType) {

        return Single.just(new Transaction.Builder()
                .from(credentials.getAddress())
                .to(to)
                .timestamp(System.currentTimeMillis())
                .txType(transactionType)
                .value(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString())
                .actualTxCost(feeAmount)
                .unDelegation(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger().toString())
                .nodeName(nodeName)
                .nodeId(nodeId)
                .chainId(NodeManager.getInstance().getChainId())
                .txReceiptStatus(TransactionStatus.PENDING.ordinal())
                .hash(hash)
                .remark("")
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
                        TransactionManager.getInstance().putPendingTransaction(transaction.getFrom(), transaction.getTimestamp());
                        TransactionManager.getInstance().putTask(transaction.getHash(), TransactionManager.getInstance().getTransactionByLoop(transaction));
                    }
                })
                .toSingle();
    }

    /**
     * 赎回委托
     *
     * @param credentials
     * @param to
     * @param nodeId
     * @param nodeName
     * @param feeAmount
     * @param stakingBlockNum
     * @param amount
     * @param transactionType
     * @param gasProvider
     * @return
     */
    public Single<Transaction> withdrawDelegate(Credentials credentials, String to, String nodeId, String nodeName, String feeAmount, String stakingBlockNum, String amount, String transactionType, GasProvider gasProvider,String nonce) {

        return Single.fromCallable(new Callable<RPCTransactionResult>() {
            @Override
            public RPCTransactionResult call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                String chainId = NodeManager.getInstance().getChainId();
                DelegateContract delegateContract = DelegateContract.load(web3j, credentials, NumberParserUtils.parseLong(chainId));
                PlatOnFunction platOnFunction = new PlatOnFunction(FunctionType.WITHDREW_DELEGATE_FUNC_TYPE,
                        Arrays.asList(new Uint64(new BigInteger(stakingBlockNum))
                                , new BytesType(Numeric.hexStringToByteArray(nodeId))
                                , new Uint256(Convert.toVon(amount, Convert.Unit.LAT).toBigInteger())), gasProvider);
                return TransactionManager.getInstance().sendContractTransaction(delegateContract, credentials, platOnFunction,nonce).blockingGet();
            }
        }).flatMap(new Function<RPCTransactionResult, SingleSource<RPCTransactionResult>>() {
            @Override
            public SingleSource<RPCTransactionResult> apply(RPCTransactionResult rpcTransactionResult) throws Exception {
                return createRPCTransactionResult(rpcTransactionResult);
            }
        })
                .flatMap(new Function<RPCTransactionResult, SingleSource<Transaction>>() {
                    @Override
                    public SingleSource<Transaction> apply(RPCTransactionResult rpcTransactionResult) throws Exception {
                        return insertTransaction(credentials, rpcTransactionResult.getHash(), to, amount, nodeId, nodeName, feeAmount, transactionType);
                    }
                });

    }

    /**
     * 领取奖励
     *
     * @param credentials
     * @param feeAmount
     * @param amount
     * @param gasProvider
     * @return
     */
    public Observable<Transaction> withdrawDelegateReward(Credentials credentials, String feeAmount, String amount, GasProvider gasProvider,String nonce) {
        return Single.fromCallable(new Callable<RPCTransactionResult>() {
            @Override
            public RPCTransactionResult call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                String chainId = NodeManager.getInstance().getChainId();
                RewardContract rewardContract = RewardContract.load(web3j, credentials, NumberParserUtils.parseLong(chainId));
                PlatOnFunction function = new PlatOnFunction(FunctionType.WITHDRAW_DELEGATE_REWARD_FUNC_TYPE, gasProvider);
                return TransactionManager.getInstance().sendContractTransaction(rewardContract, credentials, function,nonce).blockingGet();
            }
        })
                .flatMap(new Function<RPCTransactionResult, SingleSource<RPCTransactionResult>>() {
                    @Override
                    public SingleSource<RPCTransactionResult> apply(RPCTransactionResult transactionResult) throws Exception {
                        return createRPCTransactionResult(transactionResult);
                    }
                })
                .flatMap(new Function<RPCTransactionResult, SingleSource<Transaction>>() {
                    @Override
                    public SingleSource<Transaction> apply(RPCTransactionResult transactionResult) throws Exception {
                        return insertTransaction(credentials, transactionResult.getHash(), ContractAddress.REWARD_CONTRACT_ADDRESS, amount, "", "", feeAmount, String.valueOf(TransactionType.CLAIM_REWARDS.getTxTypeValue()));
                    }
                })
                .toObservable();
    }

}
