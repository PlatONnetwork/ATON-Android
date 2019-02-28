package com.juzix.wallet.engine;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.FlowableSchedulersTransformer;
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
import com.juzix.wallet.utils.ToastUtil;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.web3j.crypto.Credentials;
import org.web3j.platon.contracts.Multisig;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.ReadonlyTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;


/**
 * @author matrixelement
 */
public class SharedWalletTransactionManager {

    private static final String TAG = SharedWalletTransactionManager.class.getSimpleName();
    private static final String DEFAULT_WEI = "1E18";
    private static final String BIN_NAME = "multisig.wasm";

    public static final String DEFAULT_CREATE_SHARED_WALLET_TO_ADDRESS = null;
    public static final int CODE_OK = 0;
    public static final int CODE_ERROR_ADD_WALLET = -101;
    public static final int CODE_ERROR_WALLET_EXISTS = -200;
    public static final int CODE_ERROR_ILLEGAL_WALLET = -201;
    public static final int CODE_ERROR_UNLINKED_WALLET = -202;

    public static final BigInteger DEPLOY_GAS_LIMIT = BigInteger.valueOf(240_943_980L);
    public static final BigInteger INIT_GAS_LIMIT = BigInteger.valueOf(546_370L);
    public static final BigInteger INVOKE_GAS_LIMIT = BigInteger.valueOf(287_760L);
    public static final BigInteger APPROVE_GAS_LIMIT = BigInteger.valueOf(1_334_230L);
    public static final BigInteger REVOKE_GAS_LIMIT = BigInteger.valueOf(1_304_050L);

    public static SharedWalletTransactionManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private SharedWalletTransactionManager() {

    }

    private static class InstanceHolder {
        private static volatile SharedWalletTransactionManager INSTANCE = new SharedWalletTransactionManager();
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

    public void createSharedWallet(Credentials credentials, String walletName, String individualWalletAddress, int requiredSignNumber, ArrayList<OwnerEntity> members,
                                   BigInteger ethGasPrice, double feeAmount) {

        SharedWalletEntity sharedWalletEntity = new SharedWalletEntity.Builder()
                .uuid(UUID.randomUUID().toString())
                .name(walletName)
                .walletAddress(individualWalletAddress)
                .requiredSignNumber(requiredSignNumber)
                .owner(members)
                .avatar(SharedWalletManager.getInstance().getWalletAvatar())
                .progress(10)
                .build();

        SharedWalletManager.getInstance().addOrUpdateWallet(sharedWalletEntity);

        deployContractAddress(credentials, sharedWalletEntity.getUuid(), ethGasPrice)
                .map(new Function<TransactionReceipt, SharedTransactionInfoEntity>() {

                    @Override
                    public SharedTransactionInfoEntity apply(TransactionReceipt receipt) throws Exception {

                        return new SharedTransactionInfoEntity.Builder()
                                .uuid(receipt.getContractAddress() + receipt.getTransactionHash())
                                .hash(receipt.getTransactionHash())
                                .toAddress(receipt.getContractAddress())
                                .createTime(System.currentTimeMillis())
                                .fromAddress(sharedWalletEntity.getAddress())
                                .contractAddress(receipt.getContractAddress())
                                .walletName(walletName)
                                .read(true)
                                .sharedWalletOwnerInfoEntityList(sharedWalletEntity.buildSharedWalletOwnerInfoEntityList())
                                .ownerWalletAddress(individualWalletAddress)
                                .transactionType(SharedTransactionEntity.TransactionType.CREATE_JOINT_WALLET.getValue())
                                .energonPrice(getGasUsed(receipt.getGasUsedRaw()))
                                .build();
                    }
                })
                .doOnNext(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                        sharedWalletEntity.setContractAddress(sharedTransactionInfoEntity.getContractAddress());
                    }
                })
                .flatMap(new Function<SharedTransactionInfoEntity, Publisher<TransactionReceipt>>() {
                    @Override
                    public Publisher<TransactionReceipt> apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        return initWallet(credentials, sharedWalletEntity.getUuid(), members, ethGasPrice, sharedTransactionInfoEntity.getContractAddress(), requiredSignNumber);
                    }
                })
                .map(new Function<TransactionReceipt, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(TransactionReceipt receipt) throws Exception {
                        return new SharedTransactionInfoEntity.Builder()
                                .uuid(receipt.getContractAddress() + receipt.getTransactionHash())
                                .hash(receipt.getTransactionHash())
                                .toAddress(receipt.getContractAddress())
                                .createTime(System.currentTimeMillis())
                                .fromAddress(sharedWalletEntity.getAddress())
                                .contractAddress(receipt.getContractAddress())
                                .walletName(walletName)
                                .read(true)
                                .sharedWalletOwnerInfoEntityList(sharedWalletEntity.buildSharedWalletOwnerInfoEntityList())
                                .ownerWalletAddress(individualWalletAddress)
                                .transactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue())
                                .energonPrice(getGasUsed(receipt.getGasUsedRaw()))
                                .build();
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, Boolean>() {
                    @Override
                    public Boolean apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        boolean saveTransactionSuccess = SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                        boolean saveSharedWalletSuccess = SharedWalletManager.getInstance().saveShareWallet(sharedWalletEntity);
                        return saveTransactionSuccess && saveSharedWalletSuccess;
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean isSuccess) throws Exception {
                        return isSuccess;
                    }
                })
                .switchIfEmpty(new Flowable<Boolean>() {
                    @Override
                    protected void subscribeActual(Subscriber<? super Boolean> s) {
                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ADD_WALLET));
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        SharedWalletManager.getInstance().removeWallet(sharedWalletEntity);
                    }
                })
                .compose(new FlowableSchedulersTransformer())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof CustomThrowable) {
                            CustomThrowable customThrowable = (CustomThrowable) throwable;
                            ToastUtil.showLongToast(App.getContext(), customThrowable.getDetailMsgRes());
                        }
                    }
                });
    }

    private Flowable<TransactionReceipt> deployContractAddress(Credentials credentials, String uuid, BigInteger ethGasPrice) {

        return Flowable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return sendDeployContractAddressTransaction(credentials, ethGasPrice);
            }
        })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String transactionHash) throws Exception {
                        return !TextUtils.isEmpty(transactionHash);
                    }
                })
                .switchIfEmpty(new Flowable<String>() {
                    @Override
                    protected void subscribeActual(Subscriber<? super String> s) {
                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_DEPLOY));
                    }
                })
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String transactionHash) throws Exception {
                        SharedWalletEntity walletEntity = SharedWalletManager.getInstance().updateWalletProgress(uuid, 25);
                        EventPublisher.getInstance().sendUpdateCreateJointWalletProgressEvent(walletEntity);
                    }
                })
                .flatMap(new Function<String, Publisher<TransactionReceipt>>() {
                    @Override
                    public Publisher<TransactionReceipt> apply(String transactionHash) throws Exception {
                        return Flowable.interval(1, TimeUnit.SECONDS)
                                .flatMap(new Function<Long, Publisher<TransactionReceipt>>() {
                                    @Override
                                    public Publisher<TransactionReceipt> apply(Long aLong) throws Exception {
                                        return Flowable.just(Web3jManager.getInstance().getTransactionReceipt(transactionHash));
                                    }
                                })
                                .takeUntil(new Predicate<TransactionReceipt>() {
                                    @Override
                                    public boolean test(TransactionReceipt transactionReceipt) throws Exception {
                                        return !TextUtils.isEmpty(transactionReceipt.getTransactionHash());
                                    }
                                })
                                .doOnNext(new Consumer<TransactionReceipt>() {
                                    @Override
                                    public void accept(TransactionReceipt transactionReceipt) throws Exception {
                                        SharedWalletEntity walletEntity = SharedWalletManager.getInstance().updateWalletProgress(uuid, getProgressDeployContractAddress(uuid, transactionReceipt.getTransactionHash()));
                                        EventPublisher.getInstance().sendUpdateCreateJointWalletProgressEvent(walletEntity);
                                    }
                                })
                                .filter(new Predicate<TransactionReceipt>() {
                                    @Override
                                    public boolean test(TransactionReceipt transactionReceipt) throws Exception {
                                        return !TextUtils.isEmpty(transactionReceipt.getTransactionHash()) && !TextUtils.isEmpty(transactionReceipt.getContractAddress());
                                    }
                                })
                                .switchIfEmpty(new Flowable<TransactionReceipt>() {
                                    @Override
                                    protected void subscribeActual(Subscriber<? super TransactionReceipt> s) {
                                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_DEPLOY));
                                    }
                                })
                                .switchIfEmpty(new Flowable<TransactionReceipt>() {
                                    @Override
                                    protected void subscribeActual(Subscriber<? super TransactionReceipt> transactionreceipt) {
                                        transactionreceipt.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_DEPLOY));
                                    }
                                })
                                .flatMap(new Function<TransactionReceipt, Publisher<TransactionReceipt>>() {
                                    @Override
                                    public Publisher<TransactionReceipt> apply(TransactionReceipt transactionReceipt) throws Exception {
                                        ArrayList<SharedWalletEntity> walletList = SharedWalletManager.getInstance().getWalletList();
                                        for (SharedWalletEntity param : walletList) {
                                            if (!TextUtils.isEmpty(param.getPrefixContractAddress()) && param.getPrefixContractAddress().contains(transactionReceipt.getContractAddress())) {
                                                return Flowable.error(new CustomThrowable(CustomThrowable.CODE_ERROR_WALLET_EXISTS));
                                            }
                                        }
                                        return Flowable.just(transactionReceipt);
                                    }
                                })
                                .doOnError(new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Log.e(TAG, throwable.getMessage());
                                    }
                                });

                    }
                })
                .onErrorResumeNext(new Publisher<TransactionReceipt>() {
                    @Override
                    public void subscribe(Subscriber<? super TransactionReceipt> s) {
                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_DEPLOY));
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private int getProgressDeployContractAddress(String uuid, String transactionHash) {
        int preProgress = SharedWalletManager.getInstance().getProgressByUUID(uuid);
        int progress;
        if (TextUtils.isEmpty(transactionHash)) {
            progress = preProgress < 45 ? preProgress + 5 : 45;
        } else {
            progress = 50;
        }
        return progress;
    }

    private int getProgressInitWallet(String uuid, String transactionHash) {
        int preProgress = SharedWalletManager.getInstance().getProgressByUUID(uuid);
        int progress;
        if (TextUtils.isEmpty(transactionHash)) {
            progress = preProgress < 95 ? preProgress + 5 : 95;
        } else {
            progress = 100;
        }
        return progress;
    }

    private Flowable<TransactionReceipt> initWallet(Credentials credentials, String uuid, ArrayList<OwnerEntity> members, BigInteger ethGasPrice, String contractAddress, int requiredSignNumber) {

        return Flowable.fromCallable(new Callable<String>() {

            @Override
            public String call() throws Exception {
                return sendInitWalletTransaction(credentials, members, ethGasPrice, contractAddress, requiredSignNumber);
            }
        })
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String transactionHash) throws Exception {
                        return !TextUtils.isEmpty(transactionHash);
                    }
                })
                .switchIfEmpty(new Flowable<String>() {
                    @Override
                    protected void subscribeActual(Subscriber<? super String> s) {
                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ADD_WALLET));
                    }
                })
                .doOnNext(new Consumer<String>() {
                    @Override
                    public void accept(String transactionHash) throws Exception {
                        SharedWalletEntity walletEntity = SharedWalletManager.getInstance().updateWalletProgress(uuid, 75);
                        EventPublisher.getInstance().sendUpdateCreateJointWalletProgressEvent(walletEntity);
                    }
                })
                .flatMap(new Function<String, Publisher<TransactionReceipt>>() {
                    @Override
                    public Publisher<TransactionReceipt> apply(String transactionHash) throws Exception {
                        return Flowable.interval(1, TimeUnit.SECONDS)
                                .flatMap(new Function<Long, Publisher<TransactionReceipt>>() {
                                    @Override
                                    public Publisher<TransactionReceipt> apply(Long aLong) throws Exception {
                                        return Flowable.just(Web3jManager.getInstance().getTransactionReceipt(transactionHash));
                                    }
                                })
                                .takeUntil(new Predicate<TransactionReceipt>() {
                                    @Override
                                    public boolean test(TransactionReceipt transactionReceipt) throws Exception {
                                        return !TextUtils.isEmpty(transactionReceipt.getTransactionHash());
                                    }
                                })
                                .filter(new Predicate<TransactionReceipt>() {
                                    @Override
                                    public boolean test(TransactionReceipt receipt) throws Exception {
                                        return !TextUtils.isEmpty(receipt.getTransactionHash());
                                    }
                                })
                                .switchIfEmpty(new Flowable<TransactionReceipt>() {
                                    @Override
                                    protected void subscribeActual(Subscriber<? super TransactionReceipt> s) {
                                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ADD_WALLET));
                                    }
                                })
                                .doOnNext(new Consumer<TransactionReceipt>() {
                                    @Override
                                    public void accept(TransactionReceipt transactionReceipt) throws Exception {
                                        SharedWalletEntity walletEntity = SharedWalletManager.getInstance().updateWalletProgress(uuid, getProgressInitWallet(uuid, transactionReceipt.getTransactionHash()));
                                        EventPublisher.getInstance().sendUpdateCreateJointWalletProgressEvent(walletEntity);
                                    }
                                })
                                .map(new Function<TransactionReceipt, TransactionReceipt>() {
                                    @Override
                                    public TransactionReceipt apply(TransactionReceipt transactionReceipt) throws Exception {
                                        transactionReceipt.setContractAddress(contractAddress);
                                        return transactionReceipt;
                                    }
                                })
                                .onErrorResumeNext(new Publisher<TransactionReceipt>() {
                                    @Override
                                    public void subscribe(Subscriber<? super TransactionReceipt> s) {
                                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ADD_WALLET));
                                    }
                                });

                    }
                })
                .onErrorResumeNext(new Publisher<TransactionReceipt>() {
                    @Override
                    public void subscribe(Subscriber<? super TransactionReceipt> s) {
                        s.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ADD_WALLET));
                    }
                });
    }

    private String sendDeployContractAddressTransaction(Credentials credentials, BigInteger ethGasPrice) {
        String contractBinary = FileUtil.getStringFromAssets(App.getContext(), BIN_NAME);
        String deployData = Multisig.getDeployData(contractBinary);
        String transactionHash = Web3jManager.getInstance().getTransactionHash(credentials, ethGasPrice, DEPLOY_GAS_LIMIT, DEFAULT_CREATE_SHARED_WALLET_TO_ADDRESS, deployData, BigInteger.ZERO);
        return transactionHash;
    }

    private String sendInitWalletTransaction(Credentials credentials, ArrayList<OwnerEntity> members, BigInteger ethGasPrice, String contractAddress, int requiredSignNumber) {
        String data = Multisig.initWalletData(getOwners(members), new BigInteger(String.valueOf(requiredSignNumber)));
        String transactionHash = Web3jManager.getInstance().getTransactionHash(credentials, ethGasPrice, INVOKE_GAS_LIMIT, contractAddress, data, BigInteger.ZERO);
        return transactionHash;
    }

    private String getOwners(ArrayList<OwnerEntity> members) {
        String owner = "";

        if (members != null && !members.isEmpty()) {

            int len = members.size();

            for (int i = 0; i < len; i++) {

                OwnerEntity member = members.get(i);

                member.setUuid(UUID.randomUUID().toString());

                owner += member.getPrefixAddress();
                if (i != len - 1) {
                    owner += ":";
                }
            }
        }

        return owner;
    }

    private TransactionReceipt submitTransaction(Multisig multisig, String destination, String from, String memo, String transferAmount) {
        long time = System.currentTimeMillis();
        byte[] memos = memo.getBytes(Charset.forName("UTF-8"));
        String data = new String(memos);
        BigInteger length = BigInteger.valueOf(memos.length);
        String value = NumberParserUtils.getPrettyNumber(Convert.toWei(transferAmount, Convert.Unit.ETHER).doubleValue(), 0);
        TransactionReceipt receipt = null;
        try {
            receipt = multisig.submitTransaction(destination, from, value, data, length, BigInteger.valueOf(time), "").send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receipt;
    }

    private Single<String> getTransactionIdAndTransactionHash(Multisig multisig, TransactionReceipt receipt) {
        return Flowable.fromCallable(new Callable<List<Multisig.SubmissionEventResponse>>() {
            @Override
            public List<Multisig.SubmissionEventResponse> call() throws Exception {
                Log.e(TAG, "getTransactionIdAndTransactionHash is run " + Thread.currentThread().getName());
                return multisig.getSubmissionEvents(receipt);
            }
        })
                .flatMap(new Function<List<Multisig.SubmissionEventResponse>, Publisher<Multisig.SubmissionEventResponse>>() {
                    @Override
                    public Publisher<Multisig.SubmissionEventResponse> apply(List<Multisig.SubmissionEventResponse> submissionEventResponses) throws Exception {
                        return Flowable.fromIterable(submissionEventResponses);
                    }
                })
                .firstElement()
                .map(new Function<Multisig.SubmissionEventResponse, String>() {
                    @Override
                    public String apply(Multisig.SubmissionEventResponse submissionEventResponse) throws Exception {
                        return submissionEventResponse.param1.toString() + "&" + receipt.getTransactionHash() + "&" + receipt.getGasUsedRaw();
                    }
                })
                .toSingle()
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "getTransactionIdAndTransactionHash:" + throwable.getMessage());
                    }
                });
    }

    public Single<SharedTransactionInfoEntity> submitTransaction(Credentials credentials,
                                                                 SharedWalletEntity sharedWalletEntity,
                                                                 String to,
                                                                 String amount,
                                                                 String memo,
                                                                 BigInteger gasPrice,
                                                                 double feeAmount) {

        String from = sharedWalletEntity.getPrefixContractAddress();
        long time = System.currentTimeMillis();
        Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), from, Web3jManager.getInstance().getWeb3j(), credentials, new StaticGasProvider(gasPrice, INVOKE_GAS_LIMIT));
        return Single
                .fromCallable(new Callable<TransactionReceipt>() {
                    @Override
                    public TransactionReceipt call() throws Exception {
                        return submitTransaction(multisig, to, from, memo, amount);
                    }
                })
                .flatMap(new Function<TransactionReceipt, SingleSource<String>>() {
                    @Override
                    public SingleSource<String> apply(TransactionReceipt receipt) throws Exception {
                        return getTransactionIdAndTransactionHash(multisig, receipt);
                    }
                })
                .map(new Function<String, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(String s) throws Exception {
                        String transactionId = s.split("&", 3)[0];
                        String transactionHash = s.split("&", 3)[1];
                        String gasUsed = s.split("&", 3)[2];
                        return new SharedTransactionInfoEntity.Builder()
                                .hash(transactionHash)
                                .createTime(time)
                                .memo(memo)
                                .transactionId(transactionId)
                                .walletName(sharedWalletEntity.getName())
                                .read(true)
                                .ownerWalletAddress(sharedWalletEntity.getAddress())
                                .contractAddress(sharedWalletEntity.getPrefixContractAddress())
                                .energonPrice(getGasUsed(gasUsed))
                                .build();
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
                        sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getAddress());
                        sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
                        sharedTransactionInfoEntity.setCreateTime(time);
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                        EventPublisher.getInstance().sendSharedTransactionSucceedEvent();
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        TransactionReceipt transactionReceipt = getConfirmTransactionResult(multisig, sharedTransactionInfoEntity.getTransactionId());
                        if (transactionReceipt != null) {
                            sharedTransactionInfoEntity.setEnergonPrice(getGasUsed(transactionReceipt.getGasUsedRaw()));
                        }
                        return sharedTransactionInfoEntity;
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
                        sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getAddress());
                        sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
                        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        sharedTransactionInfoEntity.setUuid(sharedWalletEntity.getPrefixContractAddress() + sharedTransactionInfoEntity.getTransactionId());
                        sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getPrefixContractAddress());
                        sharedTransactionInfoEntity.setToAddress(to);
                        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
                        sharedTransactionInfoEntity.setSharedWalletOwnerInfoEntityRealmList(sharedWalletEntity.buildSharedWalletOwnerInfoEntityList());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue());
                        sharedTransactionInfoEntity.setValue(NumberParserUtils.parseDouble(amount));
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, "submitTransaction:" + throwable.getMessage());
                    }
                });
    }

    private TransactionReceipt getConfirmTransactionResult(Multisig multisig, String transactionId) {
        TransactionReceipt receipt = null;
        try {
            receipt = multisig.confirmTransaction(new BigInteger(transactionId)).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return receipt;
    }

    private TransactionReceipt sendTransaction(Credentials credentials, String contractAddress, String transactionId, BigInteger gasPrice, int type) {
        Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), contractAddress, Web3jManager.getInstance().getWeb3j(), credentials, new StaticGasProvider(gasPrice, INVOKE_GAS_LIMIT));
        TransactionReceipt receipt = null;
        try {
            if (type == 1) {
                receipt = multisig.confirmTransaction(new BigInteger(transactionId)).send();
            } else {
                receipt = multisig.revokeConfirmation(new BigInteger(transactionId)).send();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return receipt;
    }

    private double getGasUsed(String gasUsedRaw) {
        double gasUsed = NumericUtil.decodeQuantity(gasUsedRaw, BigInteger.ZERO).doubleValue();
        return BigDecimalUtil.div(BigDecimalUtil.mul(gasUsed, 1E9), 1E18);
    }

    /**
     * 共享钱包发送交易
     *
     * @param sharedTransactionEntity
     * @param credentials
     * @param contractAddress
     * @param transactionId
     * @param gasPrice
     * @param type
     * @return
     */
    public Single<SharedTransactionInfoEntity> sendTransaction(SharedTransactionEntity sharedTransactionEntity, Credentials credentials, String contractAddress, String transactionId, BigInteger gasPrice, int type) {

        return Single.fromCallable(new Callable<TransactionReceipt>() {
            @Override
            public TransactionReceipt call() throws Exception {
                return sendTransaction(credentials, contractAddress, transactionId, gasPrice, type);
            }
        })
                .filter(new Predicate<TransactionReceipt>() {
                    @Override
                    public boolean test(TransactionReceipt transactionReceipt) throws Exception {
                        return transactionReceipt != null && transactionReceipt.isStatusOK();
                    }
                })
                .switchIfEmpty(new Single<TransactionReceipt>() {
                    @Override
                    protected void subscribeActual(SingleObserver<? super TransactionReceipt> observer) {
                        observer.onError(new Throwable());
                    }
                })
                .doOnSuccess(new Consumer<TransactionReceipt>() {
                    @Override
                    public void accept(TransactionReceipt transactionReceipt) throws Exception {
                        if (!TextUtils.isEmpty(transactionReceipt.getBlockHash())) {
                            SharedTransactionInfoDao.getInstance().updateReadWithUuid(contractAddress + transactionId, true);
                        }
                    }
                })
                .map(new Function<TransactionReceipt, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(TransactionReceipt transactionReceipt) throws Exception {
                        return new SharedTransactionInfoEntity.Builder()
                                .hash(transactionReceipt.getTransactionHash())
                                .memo(sharedTransactionEntity.getMemo())
                                .transactionId(transactionId)
                                .walletName(sharedTransactionEntity.getWalletName())
                                .read(true)
                                .ownerWalletAddress(sharedTransactionEntity.getOwnerWalletAddress())
                                .contractAddress(sharedTransactionEntity.getContractAddress())
                                .energonPrice(getGasUsed(transactionReceipt.getGasUsedRaw()))
                                .build();
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
                        sharedTransactionInfoEntity.setFromAddress(sharedTransactionEntity.getOwnerWalletAddress());
                        sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
                        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        sharedTransactionInfoEntity.setUuid(sharedTransactionInfoEntity.getContractAddress() + transactionId);
                        sharedTransactionInfoEntity.setFromAddress(sharedTransactionInfoEntity.getContractAddress());
                        sharedTransactionInfoEntity.setToAddress(sharedTransactionEntity.getToAddress());
                        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
                        sharedTransactionInfoEntity.setSharedWalletOwnerInfoEntityRealmList(sharedTransactionInfoEntity.getSharedWalletOwnerInfoEntityRealmList());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue());
                        sharedTransactionInfoEntity.setValue(NumberParserUtils.parseDouble(sharedTransactionEntity.getValue()));
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                    }
                });
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
                        .walletName(entity.getWalletName())
                        .build();
                entities.add(transactionInfoEntity);
            }
            SharedTransactionInfoDao.getInstance().insertTransaction(entities);
        }
    }

    public List<SharedTransactionEntity> getAllTransactionList() {
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
                .filter(new Predicate<Credentials>() {
                    @Override
                    public boolean test(Credentials credentials) throws Exception {
                        return credentials != null;
                    }
                })
                .switchIfEmpty(new Single<Credentials>() {
                    @Override
                    protected void subscribeActual(SingleObserver<? super Credentials> observer) {
                        observer.onError(new Throwable());
                    }
                });

    }

    private List<SharedTransactionEntity> getWalletTransactions(SharedWalletEntity walletEntity) {
        List<SharedTransactionEntity> entities = new ArrayList<>();
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

    private List<SharedTransactionEntity> getTransactions(SharedWalletEntity walletEntity, String transactionList) {
        List<SharedTransactionInfoEntity> entityList = SharedTransactionInfoDao.getInstance().getTransactionListByContractAddress(walletEntity.getPrefixContractAddress());
        List<SharedTransactionEntity> transactionEntities = new ArrayList<SharedTransactionEntity>();
        long latestBlockNumber = Web3jManager.getInstance().getLatestBlockNumber();
        String[] items = transactionList.split(":");
        int unread = 0;
        for (String item : items) {
            String[] contents = item.split("\\|");
            if (contents.length >= 9) {
                try {
                    String contractAddress = walletEntity.getPrefixContractAddress();
                    String fromAddress = contents[0];
                    String toAddress = contents[1];
                    double sendAmount = BigDecimalUtil.div(contents[2], DEFAULT_WEI);
                    long createTime = NumberParserUtils.parseLong(contents[3]);
                    String memo = new String(contents[4].getBytes(), Charset.forName("UTF-8"));
                    double energonPrice = BigDecimalUtil.div(contents[5], DEFAULT_WEI);
                    boolean isPending = "1".equals(contents[6]);
                    boolean isExecuted = "1".equals(contents[7]);
                    String transactionId = contents[8];
                    String uuid = contractAddress + transactionId;
                    String hash = null;
                    String walletName = IndividualWalletManager.getInstance().getWalletNameByWalletAddress(walletEntity.getPrefixAddress());
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
                    transactionEntities.add(new SharedTransactionEntity.Builder(uuid, createTime, walletName)
                            .hash(hash)
                            .contractAddress(contractAddress)
                            .fromAddress(fromAddress)
                            .toAddress(toAddress)
                            .value(sendAmount)
                            .memo(memo)
                            .energonPrice(energonPrice)
                            .pending(isPending)
                            .executed(isExecuted)
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

    private void setTransactionResults(List<SharedTransactionEntity> entities, String multiSigList) {
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

    private void setTransactionResults(List<SharedTransactionEntity> entities, String transactionId, String confirmList, String revokeList) {
        if (TextUtils.isEmpty(transactionId)) {
            return;
        }
        for (SharedTransactionEntity entity : entities) {
            if (transactionId.equals(String.valueOf(entity.getTransactionId()))) {
                ArrayList<TransactionResult> results = entity.getTransactionResult();
                for (TransactionResult result : results) {
                    if ((!TextUtils.isEmpty(confirmList)) && confirmList.contains(result.getAddress().substring(2).toLowerCase())) {
                        result.setOperation(TransactionResult.OPERATION_APPROVAL);
                    } else if ((!TextUtils.isEmpty(revokeList)) && revokeList.contains(result.getAddress().substring(2).toLowerCase())) {
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

    private long getBlockNumberByHash(String transactionHash) {
        if (TextUtils.isEmpty(transactionHash)) {
            return 0;
        }
        try {
            Transaction ethTransaction = Web3jManager.getInstance().getTransactionByHash(transactionHash);
            return NumericUtil.decodeQuantity(ethTransaction.getBlockNumberRaw(), BigInteger.ZERO).longValue();
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return 0;
    }

}