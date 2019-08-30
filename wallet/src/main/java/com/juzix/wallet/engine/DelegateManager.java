package com.juzix.wallet.engine;

import android.text.TextUtils;

import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.StakingAmountType;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import io.reactivex.Single;


public class DelegateManager {
    private static class InstanceHolder {
        private static volatile DelegateManager INSTANCE = new DelegateManager();
    }

    public static DelegateManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<PlatonSendTransaction> delegate(Credentials credentials, String amount, String nodeId, String type) {

        return Single.fromCallable(new Callable<PlatonSendTransaction>() {
            @Override
            public PlatonSendTransaction call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                String chainId = NodeManager.getInstance().getChainId();
                DelegateContract delegateContract = DelegateContract.load(web3j, credentials, "100"); //todo 暂时写100做链id

                StakingAmountType stakingAmountType = TextUtils.equals(type, "balance") ? StakingAmountType.FREE_AMOUNT_TYPE : StakingAmountType.RESTRICTING_AMOUNT_TYPE;

                return delegateContract.delegateReturnTransaction(nodeId, stakingAmountType, Convert.toVon(amount, Convert.Unit.LAT).toBigInteger()).send();
            }
        });
    }


    /**
     * 获取委托结果，是否交易成功
     * @param platonSendTransaction
     * @return
     */
    public Single<BaseResponse> getDelegateReSult(PlatonSendTransaction platonSendTransaction) {
        return Single.fromCallable(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                DelegateContract delegateContract = DelegateContract.load(web3j);
                return delegateContract.getDelegateResult(platonSendTransaction).send();
            }
        });


    }

    public Single<PlatonSendTransaction> withdraw(Credentials credentials, String nodeId, String stakingBlockNum, String amount) {

        return Single.fromCallable(new Callable<PlatonSendTransaction>() {
            @Override
            public PlatonSendTransaction call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                String chainId = NodeManager.getInstance().getChainId();
                DelegateContract delegateContract = DelegateContract.load(web3j, credentials, new DefaultGasProvider(), "100");//todo 暂时写100
                return delegateContract.unDelegateReturnTransaction(nodeId, new BigInteger(stakingBlockNum), Convert.toVon(amount, Convert.Unit.LAT).toBigInteger()).send();
            }
        });

    }

    /**
     * 获取赎回结果，是否交易成功
     *
     * @param platonSendTransaction
     * @return
     */
    public Single<BaseResponse> getWithDrawResult(PlatonSendTransaction platonSendTransaction) {
        return Single.fromCallable(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                DelegateContract delegateContract = DelegateContract.load(web3j);
                return delegateContract.getUnDelegateResult(platonSendTransaction).send();
            }
        });

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
        }).onErrorReturnItem(new BigInteger("0"));
    }


}
