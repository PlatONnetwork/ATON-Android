package com.platon.aton.engine;


import com.platon.aton.entity.RPCErrorCode;
import com.platon.aton.entity.RPCNonceResult;
import com.platon.framework.app.Constants;
import com.platon.framework.utils.LogUtils;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.PlatonBlock;
import org.web3j.protocol.core.methods.response.PlatonGasPrice;
import org.web3j.protocol.core.methods.response.PlatonGetBalance;
import org.web3j.protocol.core.methods.response.PlatonGetCode;
import org.web3j.protocol.core.methods.response.PlatonGetTransactionCount;
import org.web3j.protocol.core.methods.response.PlatonGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;
import org.web3j.protocol.core.methods.response.PlatonTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.ClientConnectionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.functions.Function;
import okhttp3.OkHttpClient;

public class Web3jManager {

    public final static BigInteger NONE_NONCE = BigInteger.valueOf(-1);

    private Web3j mWeb3j;

    private Web3jManager() {

    }

    public static Web3jManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void init(String url) {
        mWeb3j = Web3jFactory.build(new HttpService(url, new OkHttpClient().newBuilder()
                .connectTimeout(Constants.Common.TRANSACTION_SEND_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(Constants.Common.TRANSACTION_SEND_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .build(), false));
    }

    public Web3j getWeb3j() {
        return mWeb3j;
    }

    private TransactionManager getTransactionManager(Credentials credentials) {
        return new RawTransactionManager(mWeb3j, credentials);
    }

    public String getTransactionHash(Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String to,
                                     String data, BigInteger value) {
        String transactionHash = null;
        try {
            PlatonSendTransaction ethSendTransaction = getTransactionManager(credentials).sendTransaction(gasPrice, gasLimit, to, data, value);
            transactionHash = ethSendTransaction.getTransactionHash();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }

        return transactionHash;
    }

    public double getBalance(String address) {
        try {
            PlatonGetBalance ethGetBalance = Web3jManager.getInstance().getWeb3j().platonGetBalance(address, DefaultBlockParameterName.LATEST).send();
            if (ethGetBalance != null && !ethGetBalance.hasError()) {
                return Convert.fromVon(new BigDecimal(ethGetBalance.getBalance()), Convert.Unit.LAT).doubleValue();
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return 0;
    }

    public Transaction getTransactionByHash(String transactionHash) {
        try {
            PlatonTransaction ethTransaction = Web3jManager.getInstance().getWeb3j().platonGetTransactionByHash(transactionHash).send();
            Transaction transaction = ethTransaction.getTransaction();
            return transaction;
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return null;
    }

    public BigInteger getEstimateGas(String from, String to, String data) {
        try {
            return Web3jManager.getInstance().getWeb3j().platonEstimateGas(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(from, to, data)).send().getAmountUsed();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
            return BigInteger.ZERO;
        }
    }

    public RPCNonceResult getNonce(String from) {

        RPCNonceResult rpcNonceResult = null;

        try {
            PlatonGetTransactionCount ethGetTransactionCount = Web3jManager.getInstance().getWeb3j().platonGetTransactionCount(
                    from, DefaultBlockParameterName.PENDING).send();
            if (ethGetTransactionCount.getTransactionCount().intValue() == 0) {
                ethGetTransactionCount = Web3jManager.getInstance().getWeb3j().platonGetTransactionCount(
                        from, DefaultBlockParameterName.LATEST).send();
            }
            rpcNonceResult = new RPCNonceResult(RPCErrorCode.SUCCESS, ethGetTransactionCount.getTransactionCount());
        } catch (SocketTimeoutException e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
            rpcNonceResult = new RPCNonceResult(RPCErrorCode.SOCKET_TIMEOUT, NONE_NONCE);
        } catch (ClientConnectionException e) {
            rpcNonceResult = new RPCNonceResult(RPCErrorCode.CONNECT_TIMEOUT, NONE_NONCE);
        } catch (IOException e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return rpcNonceResult;
    }

    public long getLatestBlockNumber() {
        try {
            PlatonBlock ethBlock = Web3jManager.getInstance().getWeb3j().platonGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            return ethBlock.getBlock().getNumber().longValue();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }
        return 0;
    }

    public String getCode(String address) {
        try {
            PlatonGetCode code = Web3jManager.getInstance().getWeb3j().platonGetCode(address, DefaultBlockParameterName.LATEST).send();
            return code.getCode();
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
            return "0x";
        }
    }

    /**
     * 获取转账的gasPrice
     *
     * @return
     */
    public Single<BigInteger> getGasPrice() {
        return Single
                .fromCallable(new Callable<BigInteger>() {
                    @Override
                    public BigInteger call() {
                        BigInteger gasPrice = DefaultGasProvider.GAS_PRICE;
                        try {
                            PlatonGasPrice platonGasPrice = Web3jManager.getInstance().getWeb3j().platonGasPrice().send();
                            gasPrice = platonGasPrice.getGasPrice();
                        } catch (IOException e) {
                            LogUtils.e(e.getMessage(),e.fillInStackTrace());
                        }
                        return gasPrice;
                    }
                }).onErrorReturnItem(DefaultGasProvider.GAS_PRICE)
                .map(new Function<BigInteger, BigInteger>() {
                    @Override
                    public BigInteger apply(BigInteger bigInteger) throws Exception {
                        return getProcessedGasPrice(bigInteger);
                    }
                });
    }

    /**
     * 获取委托的gasPrice
     *
     * @return
     */
    public Single<BigInteger> getContractGasPrice() {
        return Single
                .fromCallable(new Callable<BigInteger>() {
                    @Override
                    public BigInteger call() {
                        BigInteger gasPrice = DefaultGasProvider.GAS_PRICE;
                        try {
                            PlatonGasPrice platonGasPrice = Web3jManager.getInstance().getWeb3j().platonGasPrice().send();
                            gasPrice = platonGasPrice.getGasPrice();
                        } catch (IOException e) {
                            LogUtils.e(e.getMessage(),e.fillInStackTrace());
                        }
                        return gasPrice;
                    }
                }).onErrorReturnItem(DefaultGasProvider.GAS_PRICE)
                .map(new Function<BigInteger, BigInteger>() {
                    @Override
                    public BigInteger apply(BigInteger bigInteger) throws Exception {
                        return getProcessedGasPrice(bigInteger).max(new BigInteger(AppConfigManager.getInstance().getMinGasPrice()));
                    }
                });
    }


    public TransactionReceipt getTransactionReceipt(String transactionHash) {
        TransactionReceipt transactionReceipt = null;
        try {
            PlatonGetTransactionReceipt ethGetTransactionReceipt =
                    mWeb3j.platonGetTransactionReceipt(transactionHash).send();
            if (!ethGetTransactionReceipt.hasError() && ethGetTransactionReceipt != null) {
                transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt();
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(),e.fillInStackTrace());
        }

        if (transactionReceipt == null) {
            transactionReceipt = new TransactionReceipt();
        }

        return transactionReceipt;
    }

    /**
     * 获取处理过的gasPrice，保证后面10位为0
     *
     * @param oldGasPrice
     * @return
     */
    private BigInteger getProcessedGasPrice(BigInteger oldGasPrice) {
        BigDecimal bigDecimal = new BigDecimal(oldGasPrice).divide(BigDecimal.valueOf(10).pow(10), RoundingMode.HALF_UP);
        return bigDecimal.multiply(BigDecimal.valueOf(10).pow(10)).toBigInteger();
    }

    private static class InstanceHolder {
        private static volatile Web3jManager INSTANCE = new Web3jManager();
    }

}
