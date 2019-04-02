package com.juzix.wallet.engine;

import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetCode;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class Web3jManager {

    private Web3j mWeb3j;

    private Web3jManager() {

    }

    public static Web3jManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void init(String url) {
        mWeb3j = Web3jFactory.build(new HttpService(url));
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
            EthSendTransaction ethSendTransaction = getTransactionManager(credentials).sendTransaction(gasPrice, gasLimit, to, data, value);
            transactionHash = ethSendTransaction.getTransactionHash();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return transactionHash;
    }

    public double getBalance(String address) {
        try {
            EthGetBalance ethGetBalance = Web3jManager.getInstance().getWeb3j().ethGetBalance(address, DefaultBlockParameterName.LATEST).send();
            if (ethGetBalance != null) {
                return Convert.fromWei(new BigDecimal(ethGetBalance.getBalance()), Convert.Unit.ETHER).doubleValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Transaction getTransactionByHash(String transactionHash) {
        try {
            EthTransaction ethTransaction = Web3jManager.getInstance().getWeb3j().ethGetTransactionByHash(transactionHash).send();
            Transaction transaction = ethTransaction.getTransaction();
            return transaction;
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return null;
    }

    public BigInteger getEstimateGas(String from, String to, String data) {
        try {
            return Web3jManager.getInstance().getWeb3j().ethEstimateGas(org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(from, to, data)).send().getAmountUsed();
        } catch (Exception e) {
            e.printStackTrace();
            return BigInteger.ZERO;
        }
    }

    public BigInteger getNonce(String from) {
        try {
            BigInteger nonce = Web3jManager.getInstance().getWeb3j().ethGetTransactionCount(from, DefaultBlockParameterName.LATEST).send().getTransactionCount();
            return nonce;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BigInteger.ZERO;
    }

    public long getLatestBlockNumber() {
        try {
            EthBlock ethBlock = Web3jManager.getInstance().getWeb3j().ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false).send();
            return ethBlock.getBlock().getNumber().longValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String getCode(String address) {
        try {
            EthGetCode code = Web3jManager.getInstance().getWeb3j().ethGetCode(address, DefaultBlockParameterName.LATEST).send();
            return code.getCode();
        } catch (Exception e) {
            e.printStackTrace();
            return "0x";
        }
    }

    public boolean isValidSharedWallet(String contractAddress) {
        return !"0x".equals(getCode(contractAddress));
    }

    public TransactionReceipt getTransactionReceipt(String transactionHash) {
        TransactionReceipt transactionReceipt = null;
        try {
            EthGetTransactionReceipt ethGetTransactionReceipt =
                    mWeb3j.ethGetTransactionReceipt(transactionHash).send();
            if (!ethGetTransactionReceipt.hasError() && ethGetTransactionReceipt != null) {
                transactionReceipt = ethGetTransactionReceipt.getTransactionReceipt();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (transactionReceipt == null) {
            transactionReceipt = new TransactionReceipt();
        }

        return transactionReceipt;
    }

    private static class InstanceHolder {
        private static volatile Web3jManager INSTANCE = new Web3jManager();
    }
}
