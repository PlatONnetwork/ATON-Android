package com.juzix.wallet.engine;

import android.text.TextUtils;

import org.web3j.crypto.Credentials;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.StakingAmountType;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

public class DelegateManager {
    private static class InstanceHolder {
        private static volatile DelegateManager INSTANCE = new DelegateManager();
    }

    public static DelegateManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Single<BaseResponse> delegate(Credentials credentials, String amount, String nodeId, String type) {

        return Single.fromCallable(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                String chainId = NodeManager.getInstance().getChainId();
                DelegateContract delegateContract = DelegateContract.load(web3j, credentials, new DefaultGasProvider(), chainId);

                StakingAmountType stakingAmountType = TextUtils.equals(type, "balance") ? StakingAmountType.FREE_AMOUNT_TYPE : StakingAmountType.RESTRICTING_AMOUNT_TYPE;

                return delegateContract.delegate(nodeId, stakingAmountType, new BigInteger(amount)).send();
            }
        });
    }

}
