package com.juzix.wallet.engine;

import android.text.TextUtils;

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

import io.reactivex.Single;
import rx.Observable;


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

    public Single<PlatonSendTransaction> withdraw(Credentials credentials, String nodeId, String stakingBlockNum, String amount) {

        return Single.fromCallable(new Callable<PlatonSendTransaction>() {
            @Override
            public PlatonSendTransaction call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                String chainId = NodeManager.getInstance().getChainId();
                DelegateContract delegateContract = DelegateContract.load(web3j, credentials, new DefaultGasProvider(), chainId);
                return delegateContract.unDelegateReturnTransaction(nodeId, new BigInteger(stakingBlockNum), Convert.toVon(amount, Convert.Unit.LAT).toBigInteger()).send();
            }
        });

    }

}
