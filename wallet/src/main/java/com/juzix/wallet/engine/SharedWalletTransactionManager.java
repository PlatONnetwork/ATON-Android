package com.juzix.wallet.engine;

import android.content.Context;
import android.text.TextUtils;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.SharedTransactionInfoDao;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionResult;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.FileUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.NumericUtil;

import org.web3j.crypto.Credentials;
import org.web3j.platon.contracts.Multisig;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Single;
import io.reactivex.SingleObserver;


/**
 * @author matrixelement
 */
public class SharedWalletTransactionManager {

    public final static String DEFAULT_CREATE_SHARED_WALLET_TO_ADDRESS = null;
    private final static int DEFAULT_POLLING_FREQUENCY = 2 * 1000;

    public static final int CODE_OK = 0;
    public static final int CODE_ERROR_PASSWORD = -100;
    public static final int CODE_ERROR_ADD_WALLET = -101;
    public static final int CODE_ERROR_DEPLOY = -102;
    public static final int CODE_ERROR_INIT_WALLET = -103;
    public static final int CODE_ERROR_SUBMIT_TRANSACTION = -104;
    public static final int CODE_ERROR_CONFIRM_TRANSACTION = -105;
    public static final int CODE_ERROR_REVOKE_TRANSACTION = -106;
    public static final int CODE_ERROR_WALLET_EXISTS = -200;
    public static final int CODE_ERROR_ILLEGAL_WALLET = -201;
    public static final int CODE_ERROR_UNLINKED_WALLET = -202;
    public static final int CODE_ERROR_UNKNOW = -999;
    //    public static final BigInteger GAS_PRICE = DefaultGasProvider.GAS_PRICE;
//    public static final BigInteger GAS_LIMIT = DefaultGasProvider.GAS_LIMIT;
    public static final BigInteger DEPLOY_GAS_LIMIT = BigInteger.valueOf(250000000L);
    public static final BigInteger INVOKE_GAS_LIMIT = BigInteger.valueOf(2000000L);
    private static final String BIN_NAME = "multisig.wasm";

    private ConcurrentHashMap<String, ArrayList<SharedTransactionEntity>> mSharedTransactionEntityMap = new ConcurrentHashMap<>();
    private OnUpdateCreateJointWalletProgressListener mUpdateCreateJointWalletProgressListener;

    private SharedWalletTransactionManager() {

    }

    public static SharedWalletTransactionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public int addWallet(Context context, String walletName, String contractAddress, String individualWalletAddress) {
        if ("0x".equals(Web3jManager.getInstance().getCode(contractAddress))) {
            return CODE_ERROR_ILLEGAL_WALLET;
        }
        ArrayList<SharedWalletEntity> walletList = SharedWalletManager.getInstance().getWalletList();
        for (SharedWalletEntity param : walletList) {
            if (param.getPrefixContractAddress().contains(contractAddress)) {
                return CODE_ERROR_WALLET_EXISTS;
            }
        }
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), contractAddress, web3j, new ReadonlyTransactionManager(web3j, individualWalletAddress), new StaticGasProvider(INVOKE_GAS_LIMIT, INVOKE_GAS_LIMIT));
        try {
            BigInteger required = multisig.getRequired().send();
            String[] owners = multisig.getOwners().send().split(":");
            if (owners == null || owners.length < 2) {
                return CODE_ERROR_ADD_WALLET;
            }

            String walletAddress = individualWalletAddress;

            if (individualWalletAddress != null && individualWalletAddress.toLowerCase().startsWith("0x")) {
                walletAddress = individualWalletAddress.replaceFirst("0x", "");
            }

            if (!Arrays.asList(owners).contains(walletAddress)) {
                return CODE_ERROR_UNLINKED_WALLET;
            }

            ArrayList<OwnerEntity> members = new ArrayList<>();
            int index = 1;
            for (String owner : owners) {
                OwnerEntity entity = new OwnerEntity(UUID.randomUUID().toString(), context.getString(R.string.member, String.valueOf(index)), owner);
                members.add(entity);
                index++;
            }
            return SharedWalletManager.getInstance().createWallet(walletName, contractAddress, individualWalletAddress, required.intValue(), members) ? CODE_OK : CODE_ERROR_ADD_WALLET;
        } catch (Exception exp) {
            exp.printStackTrace();
            return CODE_ERROR_ADD_WALLET;
        }
    }

    public int createWallet(Credentials credentials, String walletName, String individualWalletAddress, int requiredSignNumber, ArrayList<OwnerEntity> members,
                            BigInteger ethGasPrice, OnUpdateCreateJointWalletProgressListener listener) {

        SharedWalletEntity sharedWalletEntity = new SharedWalletEntity.Builder()
                .uuid(UUID.randomUUID().toString())
                .name(walletName)
                .contractAddress(null)
                .walletAddress(individualWalletAddress)
                .requiredSignNumber(requiredSignNumber)
                .owner(members)
                .avatar(SharedWalletManager.getInstance().getWalletAvatar())
                .linkWalletAddress(individualWalletAddress)
                .build();

        SharedWalletManager.getInstance().addWallet(sharedWalletEntity);

        if (listener != null) {
            listener.updateCreateJointWalletProgress(sharedWalletEntity.updateProgress(10));
        }

        TransactionManager transactionManager = new RawTransactionManager(Web3jManager.getInstance().getWeb3j(), credentials);
        String contractBinary = FileUtil.getStringFromAssets(App.getContext(), BIN_NAME);
        String contractAddress = "";
        try {
            EthSendTransaction transaction = transactionManager.sendTransaction(ethGasPrice, DEPLOY_GAS_LIMIT, DEFAULT_CREATE_SHARED_WALLET_TO_ADDRESS, Multisig.getDeployData(contractBinary), BigInteger.ZERO);
            String hash = transaction.getTransactionHash();
            if (!TextUtils.isEmpty(hash) && listener != null) {
                listener.updateCreateJointWalletProgress(sharedWalletEntity.updateProgress(25));
            }
            TransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(Web3jManager.getInstance().getWeb3j(), DEFAULT_POLLING_FREQUENCY, TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
            TransactionReceipt receipt = processor.waitForTransactionReceipt(hash);
            if (receipt != null && listener != null) {
                listener.updateCreateJointWalletProgress(sharedWalletEntity.updateProgress(50));
            }
            contractAddress = receipt.getContractAddress();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        if (TextUtils.isEmpty(contractAddress)) {
            return CODE_ERROR_DEPLOY;
        }
        ArrayList<SharedWalletEntity> walletList = SharedWalletManager.getInstance().getWalletList();
        for (SharedWalletEntity param : walletList) {
            if (!TextUtils.isEmpty(param.getPrefixContractAddress()) && param.getPrefixContractAddress().contains(contractAddress)) {
                return CODE_ERROR_WALLET_EXISTS;
            }
        }

        sharedWalletEntity.setContractAddress(contractAddress);

        String owner = "";
        int len = members.size();
        for (int i = 0; i < len; i++) {
            OwnerEntity member = members.get(i);
            member.setUuid(UUID.randomUUID().toString());
            owner += member.getPrefixAddress();
            if (i != len - 1) {
                owner += ":";
            }
        }
        try {
            String data = Multisig.initWalletData(owner, new BigInteger(String.valueOf(requiredSignNumber)));
            EthSendTransaction transaction = transactionManager.sendTransaction(ethGasPrice, INVOKE_GAS_LIMIT, contractAddress, data, BigInteger.ZERO);
            String hash = transaction.getTransactionHash();
            if (!TextUtils.isEmpty(hash) && listener != null) {
                listener.updateCreateJointWalletProgress(sharedWalletEntity.updateProgress(75));
            }
            TransactionReceiptProcessor processor = new PollingTransactionReceiptProcessor(Web3jManager.getInstance().getWeb3j(), DEFAULT_POLLING_FREQUENCY, TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);
            TransactionReceipt receipt = processor.waitForTransactionReceipt(hash);
            if (receipt != null && listener != null) {
                listener.updateCreateJointWalletProgress(sharedWalletEntity.updateProgress(100));
            }
            if (TextUtils.isEmpty(receipt.getBlockHash())) {
                return CODE_ERROR_ADD_WALLET;
            }

            SharedTransactionInfoEntity sharedTransactionInfoEntity = new SharedTransactionInfoEntity.Builder()
                    .uuid(sharedWalletEntity.getPrefixContractAddress() + receipt.getTransactionHash())
                    .hash(receipt.getTransactionHash())
                    .toAddress(sharedWalletEntity.getPrefixContractAddress())
                    .createTime(System.currentTimeMillis())
                    .fromAddress(sharedWalletEntity.getAddress())
                    .contractAddress(sharedWalletEntity.getPrefixContractAddress())
                    .walletName(sharedWalletEntity.getName())
                    .read(true)
                    .sharedWalletOwnerInfoEntityList(sharedWalletEntity.buildSharedWalletOwnerInfoEntityList())
                    .ownerWalletAddress(individualWalletAddress)
                    .transactionType(SharedTransactionEntity.TransactionType.CREATE_JOINT_WALLET.getValue())
                    .build();

            boolean saveTransactionSuccess = SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

            boolean saveSharedWalletSuccess = SharedWalletManager.getInstance().saveShareWallet(sharedWalletEntity);

            return saveTransactionSuccess && saveSharedWalletSuccess ? CODE_OK : CODE_ERROR_ADD_WALLET;
        } catch (Exception exp) {
            return CODE_ERROR_ADD_WALLET;
        }
    }

    public int submitTransaction(
            Credentials credentials,
            SharedWalletEntity sharedWalletEntity,
            String to,
            String amount,
            String memo,
            BigInteger gasPrice) {
        TransactionReceipt receipt = null;
        try {
            long time = System.currentTimeMillis();
            String data = org.spongycastle.util.encoders.Hex.toHexString(memo.getBytes(Charset.forName("UTF-8")));
            String value = Convert.toWei(amount, Convert.Unit.ETHER).toPlainString();
            Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), sharedWalletEntity.getPrefixContractAddress(), Web3jManager.getInstance().getWeb3j(), credentials, new StaticGasProvider(gasPrice, INVOKE_GAS_LIMIT));
            receipt = multisig.submitTransaction(to, sharedWalletEntity.getPrefixContractAddress(), value, data, BigInteger.valueOf(data.length()), BigInteger.valueOf(time), "").send();

            if (!receipt.isStatusOK()) {
                return CODE_ERROR_SUBMIT_TRANSACTION;
            }
            List<Multisig.SubmissionEventResponse> submissionEventResponses = multisig.getSubmissionEvents(receipt);

            if (submissionEventResponses == null || submissionEventResponses.isEmpty()) {
                return CODE_ERROR_SUBMIT_TRANSACTION;
            }

            String transactionId = submissionEventResponses.get(0).param1.toString();

            SharedTransactionInfoEntity sharedTransactionInfoEntity = new SharedTransactionInfoEntity.Builder()
                    .hash(receipt.getTransactionHash())
                    .createTime(time)
                    .memo(memo)
                    .transactionId(transactionId)
                    .walletName(sharedWalletEntity.getName())
                    .read(true)
                    .ownerWalletAddress(sharedWalletEntity.getAddress())
                    .contractAddress(sharedWalletEntity.getPrefixContractAddress())
                    .build();

            sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
            sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getAddress());
            sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
            sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
            sharedTransactionInfoEntity.setCreateTime(time);
            SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

            EventPublisher.getInstance().sendSharedTransactionSucceedEvent();

            multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), sharedWalletEntity.getPrefixContractAddress(), Web3jManager.getInstance().getWeb3j(), credentials, new StaticGasProvider(gasPrice, INVOKE_GAS_LIMIT));

            receipt = multisig.confirmTransaction(new BigInteger(transactionId)).send();

            if (!receipt.isStatusOK()) {
                return CODE_ERROR_SUBMIT_TRANSACTION;
            }

            sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
            sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getAddress());
            sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
            sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
            sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
            SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

            sharedTransactionInfoEntity.setUuid(sharedWalletEntity.getPrefixContractAddress() + transactionId);
            sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getPrefixContractAddress());
            sharedTransactionInfoEntity.setToAddress(to);
            sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
            sharedTransactionInfoEntity.setSharedWalletOwnerInfoEntityRealmList(sharedWalletEntity.buildSharedWalletOwnerInfoEntityList());
            sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue());
            sharedTransactionInfoEntity.setValue(NumberParserUtils.parseDouble(amount));
            SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

        } catch (Exception exp) {
            exp.printStackTrace();
            return CODE_ERROR_SUBMIT_TRANSACTION;
        }

        return CODE_OK;
    }

    public int confirmTransaction(SharedTransactionEntity sharedTransactionEntity, String password, String keyJson, String contractAddress, String transactionId, BigInteger gasPrice) {
        Credentials credentials = credentials(password, keyJson);
        if (credentials == null) {
            return CODE_ERROR_PASSWORD;
        }
        Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), contractAddress, Web3jManager.getInstance().getWeb3j(), credentials, new StaticGasProvider(gasPrice, INVOKE_GAS_LIMIT));
        TransactionReceipt receipt = null;
        try {
            receipt = multisig.confirmTransaction(new BigInteger(transactionId)).send();
            if (!receipt.isStatusOK()) {
                return CODE_ERROR_CONFIRM_TRANSACTION;
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            return CODE_ERROR_CONFIRM_TRANSACTION;
        }

        SharedTransactionInfoEntity sharedTransactionInfoEntity = new SharedTransactionInfoEntity.Builder()
                .hash(receipt.getTransactionHash())
                .memo(sharedTransactionEntity.getMemo())
                .transactionId(transactionId)
                .walletName(sharedTransactionEntity.getWalletName())
                .read(true)
                .ownerWalletAddress(sharedTransactionEntity.getOwnerWalletAddress())
                .contractAddress(sharedTransactionEntity.getContractAddress())
                .build();

        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
        sharedTransactionInfoEntity.setFromAddress(sharedTransactionEntity.getOwnerWalletAddress());
        sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

        sharedTransactionInfoEntity.setUuid(sharedTransactionInfoEntity.getContractAddress() + transactionId);
        sharedTransactionInfoEntity.setFromAddress(sharedTransactionInfoEntity.getContractAddress());
        sharedTransactionInfoEntity.setToAddress(sharedTransactionEntity.getToAddress());
        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
        sharedTransactionInfoEntity.setSharedWalletOwnerInfoEntityRealmList(sharedTransactionInfoEntity.getSharedWalletOwnerInfoEntityRealmList());
        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue());
        sharedTransactionInfoEntity.setValue(NumberParserUtils.parseDouble(sharedTransactionEntity.getValue()));
        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

        if (!TextUtils.isEmpty(receipt.getBlockHash())) {
            SharedTransactionInfoDao.getInstance().updateReadWithUuid(contractAddress + transactionId, true);
        }

        if (receipt != null && receipt.isStatusOK()) {

            return CODE_OK;
        } else {
            return CODE_ERROR_CONFIRM_TRANSACTION;
        }
    }

    public int revokeTransaction(SharedTransactionEntity sharedTransactionEntity, String password, String keyJson, String contractAddress, String transactionId, BigInteger gasPrice) {
        Credentials credentials = credentials(password, keyJson);
        if (credentials == null) {
            return CODE_ERROR_PASSWORD;
        }
        TransactionReceipt receipt = null;
        try {
            Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), contractAddress, Web3jManager.getInstance().getWeb3j(), credentials, new StaticGasProvider(gasPrice, INVOKE_GAS_LIMIT));
            receipt = multisig.revokeConfirmation(new BigInteger(transactionId)).send();
            if (!TextUtils.isEmpty(receipt.getBlockHash())) {
                SharedTransactionInfoDao.getInstance().updateReadWithUuid(contractAddress + transactionId, true);
            }

            SharedTransactionInfoEntity sharedTransactionInfoEntity = new SharedTransactionInfoEntity.Builder()
                    .hash(receipt.getTransactionHash())
                    .memo(sharedTransactionEntity.getMemo())
                    .transactionId(transactionId)
                    .walletName(sharedTransactionEntity.getWalletName())
                    .read(true)
                    .ownerWalletAddress(sharedTransactionEntity.getOwnerWalletAddress())
                    .contractAddress(sharedTransactionEntity.getContractAddress())
                    .build();

            sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
            sharedTransactionInfoEntity.setFromAddress(sharedTransactionEntity.getOwnerWalletAddress());
            sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
            sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
            sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
            SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

            sharedTransactionInfoEntity.setUuid(sharedTransactionInfoEntity.getContractAddress() + transactionId);
            sharedTransactionInfoEntity.setFromAddress(sharedTransactionInfoEntity.getContractAddress());
            sharedTransactionInfoEntity.setToAddress(sharedTransactionEntity.getToAddress());
            sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
            sharedTransactionInfoEntity.setSharedWalletOwnerInfoEntityRealmList(sharedTransactionInfoEntity.getSharedWalletOwnerInfoEntityRealmList());
            sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue());
            sharedTransactionInfoEntity.setValue(NumberParserUtils.parseDouble(sharedTransactionEntity.getValue()));
            SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);

        } catch (Exception exp) {
            exp.printStackTrace();
        }

        if (receipt != null && receipt.isStatusOK()) {
            return CODE_OK;
        } else {
            return CODE_ERROR_REVOKE_TRANSACTION;
        }
    }

    public boolean unRead() {
        return SharedTransactionInfoDao.getInstance().hasUnRead();
    }

    public void updateTransactionForRead(SharedWalletEntity walletEntity) {
        SharedTransactionInfoDao.getInstance().updateReadWithContractAddress(walletEntity.getPrefixContractAddress(), true);
        EventPublisher.getInstance().sendUpdateMessageTipsEvent(unRead());
    }

    public void updateTransactionForRead(SharedWalletEntity walletEntity, SharedTransactionEntity transactionEntity) {
        SharedTransactionInfoDao.getInstance().updateReadWithUuid(transactionEntity.getContractAddress() + transactionEntity.getTransactionId(), true);
        walletEntity.setUnread(walletEntity.getUnread() - 1);
        EventPublisher.getInstance().sendUpdateMessageTipsEvent(unRead());
    }

    public void updateTransactions() {
        ArrayList<SharedTransactionEntity> transactionEntities = new ArrayList<>();
        ArrayList<SharedWalletEntity> walletEntityList = SharedWalletManager.getInstance().getWalletList();
        for (int i = 0; i < walletEntityList.size(); i++) {
            transactionEntities.addAll(getWalletTransactions(walletEntityList.get(i)));
        }
        if (transactionEntities != null && !transactionEntities.isEmpty()) {
            ArrayList<SharedTransactionInfoEntity> entities = new ArrayList<>();
            for (SharedTransactionEntity entity : transactionEntities) {
                SharedTransactionInfoEntity transactionInfoEntity = new SharedTransactionInfoEntity.Builder()
                        .uuid(entity.getUuid())
                        .createTime(entity.getCreateTime())
                        .hash(entity.getHash())
                        .contractAddress(entity.getContractAddress())
                        .fromAddress(entity.getFromAddress())
                        .toAddress(entity.getToAddress())
                        .value(entity.getValue())
                        .memo(entity.getMemo())
                        .energonPrice(entity.getEnergonPrice())
                        .pending(entity.isPending())
                        .executed(entity.isExecuted())
                        .transactionId(entity.getTransactionId())
                        .transactionResult(entity.buildTransactionInfoResult())
                        .requiredSignNumber(entity.getRequiredSignNumber())
                        .blockNumber(entity.getBlockNumber())
                        .latestBlockNumber(entity.getLatestBlockNumber())
                        .read(entity.isRead())
                        .ownerWalletAddress(entity.getOwnerWalletAddress())
                        .transactionType(entity.getTransactionType())
                        .sharedWalletOwnerInfoEntityList(entity.buildSharedWalletOwnerInfoEntityList())
                        .build();
                entities.add(transactionInfoEntity);
            }
            SharedTransactionInfoDao.getInstance().insertTransaction(entities);
        }
    }

    public ArrayList<SharedTransactionEntity> getAllTransactionList() {
        ArrayList<SharedTransactionEntity> transactionEntities = new ArrayList<>();
        ArrayList<SharedTransactionInfoEntity> transactionInfoEntities = SharedTransactionInfoDao.getInstance().getTransactionInfoList();
        for (SharedTransactionInfoEntity entity : transactionInfoEntities) {
            SharedTransactionEntity transactionEntity = new SharedTransactionEntity.Builder(entity.getUuid(), entity.getCreateTime(), entity.getWalletName())
                    .hash(entity.getHash())
                    .contractAddress(entity.getContractAddress())
                    .fromAddress(entity.getFromAddress())
                    .toAddress(entity.getToAddress())
                    .value(entity.getValue())
                    .memo(entity.getMemo())
                    .energonPrice(entity.getEnergonPrice())
                    .pending(entity.isPending())
                    .executed(entity.isExecuted())
                    .transactionId(entity.getTransactionId())
                    .transactionResult(entity.buildTransactionResult())
                    .requiredSignNumber(entity.getRequiredSignNumber())
                    .blockNumber(entity.getBlockNumber())
                    .latestBlockNumber(entity.getLatestBlockNumber())
                    .read(entity.isRead())
                    .ownerEntityList(entity.buildOwnerEntityList())
                    .ownerWalletAddress(entity.getOwnerWalletAddress())
                    .transactionType(entity.getTransactionType())
                    .build();
            transactionEntities.add(transactionEntity);
        }
        return transactionEntities;
    }

    public ArrayList<SharedTransactionEntity> getTransactionListByContractAddress(String contractAddress) {
        ArrayList<SharedTransactionEntity> transactionEntities = new ArrayList<>();
        ArrayList<SharedTransactionInfoEntity> transactionInfoEntities = SharedTransactionInfoDao.getInstance().getTransactionListByContractAddress(contractAddress);
        for (SharedTransactionInfoEntity entity : transactionInfoEntities) {
            SharedTransactionEntity transactionEntity = new SharedTransactionEntity.Builder(entity.getUuid(), entity.getCreateTime(), entity.getWalletName())
                    .hash(entity.getHash())
                    .contractAddress(entity.getContractAddress())
                    .fromAddress(entity.getFromAddress())
                    .toAddress(entity.getToAddress())
                    .value(entity.getValue())
                    .memo(entity.getMemo())
                    .energonPrice(entity.getEnergonPrice())
                    .pending(entity.isPending())
                    .executed(entity.isExecuted())
                    .transactionId(entity.getTransactionId())
                    .transactionResult(entity.buildTransactionResult())
                    .requiredSignNumber(entity.getRequiredSignNumber())
                    .blockNumber(entity.getBlockNumber())
                    .latestBlockNumber(entity.getLatestBlockNumber())
                    .read(entity.isRead())
                    .ownerEntityList(entity.buildOwnerEntityList())
                    .ownerWalletAddress(entity.getOwnerWalletAddress())
                    .transactionType(entity.getTransactionType())
                    .build();
            transactionEntities.add(transactionEntity);
        }
        return transactionEntities;
    }


    private ArrayList<SharedTransactionEntity> getWalletTransactions(SharedWalletEntity walletEntity) {
        ArrayList<SharedTransactionEntity> entities = new ArrayList<>();
        Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), walletEntity.getPrefixContractAddress(),
                Web3jManager.getInstance().getWeb3j(),
                new ReadonlyTransactionManager(Web3jManager.getInstance().getWeb3j(), walletEntity.getPrefixAddress()),
                new StaticGasProvider(DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT));
        try {
            entities = getTransactions(walletEntity, multisig.getTransactionList(BigInteger.valueOf(0), BigInteger.valueOf(Long.MAX_VALUE)).send());
            if (entities.isEmpty()) {
                return entities;
            }
            String transactionIds = "";
            int len = entities.size();
            for (int i = 0; i < len; i++) {
                if (i == 0) {
                    transactionIds = "";
                } else {
                    transactionIds += ",";
                }
                transactionIds += entities.get(i).getTransactionId();
            }
            setTransactionResults(entities, multisig.getMultiSigList(transactionIds).send());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return entities;
    }

    private ArrayList<SharedTransactionEntity> getTransactions(SharedWalletEntity walletEntity, String transactionList) {
        ArrayList<SharedTransactionInfoEntity> entityList = SharedTransactionInfoDao.getInstance().getTransactionListByContractAddress(walletEntity.getPrefixContractAddress());
        ArrayList<SharedTransactionEntity> transactionEntities = new ArrayList<SharedTransactionEntity>();
        long latestBlockNumber = Web3jManager.getInstance().getLatestBlockNumber();
        String[] items = transactionList.split(":");
        int unread = 0;
        for (String item : items) {
            String[] contents = item.split("\\|");
            if (contents.length >= 9) {
                try {
                    String contractAddress = walletEntity.getPrefixContractAddress();
                    long createTime = Long.parseLong(contents[3]);
                    String fromAddress = contents[0];
                    String toAddress = contents[1];
                    String memo = new String(org.spongycastle.util.encoders.Hex.decode(contents[4]), Charset.forName("UTF-8"));
                    String transactionId = contents[8];
                    String uuid = contractAddress + transactionId;
                    String hash = "";
                    boolean read = false;
                    for (SharedTransactionInfoEntity entity : entityList) {
                        if ((!TextUtils.isEmpty(transactionId)) && transactionId.equals(entity.getTransactionId())) {
                            hash = entity.getHash();
                            read = entity.isRead();
                            break;
                        }
                    }
                    if (!read) {
                        unread++;
                    }
                    transactionEntities.add(new SharedTransactionEntity.Builder(uuid, createTime, "")
                            .hash(hash)
                            .contractAddress(contractAddress)
                            .fromAddress(fromAddress)
                            .toAddress(toAddress)
                            .value(BigDecimalUtil.div(contents[2], "1E18"))
                            .memo(memo)
                            .energonPrice(BigDecimalUtil.div(contents[5], "1E18"))
                            .pending("1".equals(contents[6]))
                            .executed("1".equals(contents[7]))
                            .transactionId(transactionId)
                            .transactionResult(initResults(uuid, walletEntity.getOwner()))
                            .requiredSignNumber(walletEntity.getRequiredSignNumber())
                            .blockNumber(getBlockNumberByHash(hash))
                            .latestBlockNumber(latestBlockNumber)
                            .ownerWalletAddress(walletEntity.getAddress())
                            .transactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue())
                            .ownerEntityList(walletEntity.getOwner())
                            .read(read)
                            .build());
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }

        }
        walletEntity.setUnread(unread);
        return transactionEntities;
    }

    private void setTransactionResults(ArrayList<SharedTransactionEntity> entities, String multiSigList) {
        if (TextUtils.isEmpty(multiSigList)) {
            return;
        }
        String[] items = multiSigList.split("\\|");
        for (String item : items) {
            String transactionId = "";
            String confirmList = "";
            String revokeList = "";
            String[] contents = item.split(":");
            switch (contents.length) {
                case 0:
                    break;
                case 1:
                    transactionId = contents[0];
                    break;
                case 2:
                    transactionId = contents[0];
                    confirmList = contents[1];
                    break;
                case 3:
                    transactionId = contents[0];
                    confirmList = contents[1];
                    revokeList = contents[2];
                    break;
            }
            setTransactionResults(entities, transactionId, confirmList, revokeList);
        }
    }

    private void setTransactionResults(ArrayList<SharedTransactionEntity> entities, String transactionId, String confirmList, String revokeList) {
        if (TextUtils.isEmpty(transactionId)) {
            return;
        }
        for (SharedTransactionEntity entity : entities) {
            if (transactionId.equals(String.valueOf(entity.getTransactionId()))) {
                ArrayList<TransactionResult> results = entity.getTransactionResult();
                for (TransactionResult result : results) {
                    if ((!TextUtils.isEmpty(confirmList)) && confirmList.contains(result.getAddress().substring(2))) {
                        result.setOperation(TransactionResult.OPERATION_APPROVAL);
                    } else if ((!TextUtils.isEmpty(revokeList)) && revokeList.contains(result.getAddress().substring(2))) {
                        result.setOperation(TransactionResult.OPERATION_REVOKE);
                    }
                }
            }
        }
    }

    private ArrayList<TransactionResult> initResults(String transactionUuid, ArrayList<OwnerEntity> owners) {
        ArrayList<TransactionResult> results = new ArrayList<>();
        for (OwnerEntity owner : owners) {
            TransactionResult result = new TransactionResult.Builder()
                    .uuid(transactionUuid + owner.getAddress())
                    .address(owner.getAddress())
                    .name(owner.getName())
                    .operation(TransactionResult.OPERATION_UNDETERMINED)
                    .build();
            results.add(result);
        }
        return results;
    }

    public long getBlockNumberByHash(String transactionHash) {
        if (TextUtils.isEmpty(transactionHash)) {
            return 0;
        }
        try {
            Transaction ethTransaction = Web3jManager.getInstance().getTransactionByHash(transactionHash);
            return NumericUtil.decodeQuantity(ethTransaction.getBlockNumberRaw(), BigInteger.ZERO).longValue();
        } catch (Exception exp) {

        }
        return 0;
    }

    public Credentials credentials(String password, String keyJson) {
        Credentials credentials = null;
        try {
            credentials = JZWalletUtil.loadCredentials(password, keyJson);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return credentials;
    }

    public Single<Credentials> validPassword(String password, String keyJson) {
        return Single
                .fromCallable(new Callable<Credentials>() {

                    @Override
                    public Credentials call() throws Exception {
                        return credentials(password, keyJson);
                    }
                })
                .onErrorResumeNext(new Single<Credentials>() {
                    @Override
                    protected void subscribeActual(SingleObserver<? super Credentials> observer) {
                        observer.onError(new Throwable());
                    }
                })
                .compose(new SchedulersTransformer());
    }

    private static class InstanceHolder {
        private static volatile SharedWalletTransactionManager INSTANCE = new SharedWalletTransactionManager();
    }

    public interface OnUpdateCreateJointWalletProgressListener {

        void updateCreateJointWalletProgress(SharedWalletEntity sharedWalletEntity);
    }
}
