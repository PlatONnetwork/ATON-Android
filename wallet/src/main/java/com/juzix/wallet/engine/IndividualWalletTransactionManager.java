package com.juzix.wallet.engine;

import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.NumericUtil;

import org.spongycastle.util.encoders.Hex;
import org.web3j.abi.PlatOnTypeEncoder;
import org.web3j.abi.datatypes.generated.Int64;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
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

/**
 * @author matrixelement
 */
public class IndividualWalletTransactionManager {

    private IndividualWalletTransactionManager() {

    }

    public static IndividualWalletTransactionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public IndividualWalletEntity getBalanceByAddress(IndividualWalletEntity walletEntity) {
        walletEntity.setBalance(Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress()));
        return walletEntity;
    }

    public IndividualTransactionEntity getTransactionByHash(String transactionHash, long createTime, String walletName, String memo) {
        try {
            Transaction transaction = Web3jManager.getInstance().getTransactionByHash(transactionHash);
            if (transaction != null) {
                transaction.setCreates(String.valueOf(createTime));
                BigDecimalUtil.mul(transaction.getGas().doubleValue(), transaction.getGasPrice().doubleValue());
                IndividualTransactionEntity entity = new IndividualTransactionEntity.Builder(UUID.randomUUID().toString(), createTime, walletName)
                        .hash(transactionHash)
                        .fromAddress(transaction.getFrom())
                        .toAddress(transaction.getTo())
                        .value(BigDecimalUtil.div(transaction.getValue().toString(), "1E18"))
                        .blockNumber(NumericUtil.decodeQuantity(transaction.getBlockNumberRaw(), BigInteger.ZERO).longValue())
                        .energonPrice(BigDecimalUtil.div(BigDecimalUtil.mul(transaction.getGas().doubleValue(), transaction.getGasPrice().doubleValue()), 1E18))
                        .memo(memo)
                        .build();
                return entity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String sendTransaction(String privateKey, String from, String toAddress, String amount, long gasPrice, long gasLimit) {

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

    public IndividualTransactionEntity getLatestBlockNumber(IndividualTransactionEntity transactionEntity) {
        transactionEntity.setLatestBlockNumber(Web3jManager.getInstance().getLatestBlockNumber());
        return transactionEntity;
    }

    private static class InstanceHolder {
        private static volatile IndividualWalletTransactionManager INSTANCE = new IndividualWalletTransactionManager();
    }


}
