package com.juzix.wallet.engine;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.FlowableSchedulersTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.db.entity.SharedTransactionInfoEntity;
import com.juzix.wallet.db.sqlite.AddressInfoDao;
import com.juzix.wallet.db.sqlite.SharedTransactionInfoDao;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.TransactionResult;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.FileUtil;
import com.juzix.wallet.utils.JSONUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.NumericUtil;
import com.juzix.wallet.utils.ToastUtil;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.web3j.crypto.Credentials;
import org.web3j.platon.contracts.Multisig;
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
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiFunction;
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

    //交易contractAddress唯一标识
    private List<String> mTransactionContractAddressList = new ArrayList<>();

    public List<String> getTransactionContractAddressList() {
        return mTransactionContractAddressList;
    }

    public Single<Boolean> addWallet(Context context, String walletName, String contractAddress, String individualWalletAddress) {

        return checkSharedWallet(contractAddress)
                .flatMap(new Function<Boolean, SingleSource<List<String>>>() {
                    @Override
                    public SingleSource<List<String>> apply(Boolean aBoolean) throws Exception {
                        return getSharedWalletOwnerList(contractAddress, individualWalletAddress);
                    }
                }).filter(new Predicate<List<String>>() {
                    @Override
                    public boolean test(List<String> strings) throws Exception {
                        return checkWalletAddress(strings, individualWalletAddress);
                    }
                }).switchIfEmpty(new SingleSource<List<String>>() {
                    @Override
                    public void subscribe(SingleObserver<? super List<String>> observer) {
                        observer.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_UNLINKED_WALLET));
                    }
                }).flatMap(new Function<List<String>, SingleSource<List<OwnerEntity>>>() {
                    @Override
                    public SingleSource<List<OwnerEntity>> apply(List<String> strings) throws Exception {
                        return getOwnerList(context, strings);
                    }
                }).zipWith(getRequired(contractAddress, individualWalletAddress), new BiFunction<List<OwnerEntity>, BigInteger, Boolean>() {
                    @Override
                    public Boolean apply(List<OwnerEntity> ownerEntityList, BigInteger requiredValue) throws Exception {
                        return SharedWalletManager.getInstance().createWallet(walletName, contractAddress, individualWalletAddress, requiredValue.intValue(), ownerEntityList);
                    }
                }).filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).switchIfEmpty(new SingleSource<Boolean>() {
                    @Override
                    public void subscribe(SingleObserver<? super Boolean> observer) {
                        observer.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ADD_SHARE_WALLET));
                    }
                });
    }

    public void createSharedWallet(Credentials credentials, String walletName, String individualWalletAddress, int requiredSignNumber, List<OwnerEntity> members,
                                   BigInteger ethGasPrice, double feeAmount) {

        long time = System.currentTimeMillis();
        SharedWalletEntity sharedWalletEntity = new SharedWalletEntity.Builder()
                .uuid(UUID.randomUUID().toString())
                .name(walletName)
                .creatorAddress(individualWalletAddress)
                .requiredSignNumber(requiredSignNumber)
                .owner(members)
                .avatar(SharedWalletManager.getInstance().getWalletAvatar())
                .createTime(time)
                .updateTime(time)
                .progress(10)
                .build();

        SharedWalletManager.getInstance().addOrUpdateWallet(sharedWalletEntity);

        deployContractAddress(credentials, sharedWalletEntity.getUuid(), ethGasPrice)
                .map(new Function<TransactionReceipt, SharedTransactionInfoEntity>() {

                    @Override
                    public SharedTransactionInfoEntity apply(TransactionReceipt receipt) throws Exception {

                        return new SharedTransactionInfoEntity.Builder()
                                .uuid(UUID.randomUUID().toString())
                                .hash(receipt.getTransactionHash())
                                .toAddress(receipt.getContractAddress())
                                .createTime(System.currentTimeMillis())
                                .fromAddress(sharedWalletEntity.getCreatorAddress())
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
                        sharedWalletEntity.setAddress(sharedTransactionInfoEntity.getContractAddress());
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
                                .uuid(UUID.randomUUID().toString())
                                .hash(receipt.getTransactionHash())
                                .toAddress(receipt.getContractAddress())
                                .createTime(System.currentTimeMillis())
                                .fromAddress(sharedWalletEntity.getCreatorAddress())
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

    private Single<List<String>> getSharedWalletOwnerList(String contractAddress, String individualWalletAddress) {

        return Single.create(new SingleOnSubscribe<List<String>>() {
            @Override
            public void subscribe(SingleEmitter<List<String>> emitter) {
                String[] owners = null;
                try {
                    owners = getMultisig(contractAddress, individualWalletAddress).getOwners().send().split(":");
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (owners == null || owners.length < 2) {
                        emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ADD_SHARE_WALLET));
                    } else {
                        emitter.onSuccess(Arrays.asList(owners));
                    }
                }
            }
        });
    }

    private Single<Boolean> checkSharedWallet(String contractAddress) {
        return Single.create(new SingleOnSubscribe<Boolean>() {
            @Override
            public void subscribe(SingleEmitter<Boolean> emitter) throws Exception {
                boolean isValidSharedWallet = Web3jManager.getInstance().isValidSharedWallet(contractAddress);
                if (isValidSharedWallet) {
                    emitter.onSuccess(isValidSharedWallet);
                } else {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_ILLEGAL_WALLET));
                }
            }
        });
    }

    private Boolean checkWalletAddress(List<String> ownerEntityList, String individualWalletAddress) {

        String tempWalletAddress = individualWalletAddress;
        if (individualWalletAddress != null && individualWalletAddress.toLowerCase().startsWith("0x")) {
            tempWalletAddress = individualWalletAddress.replaceFirst("0x", "");
        }

        return ownerEntityList.contains(tempWalletAddress);
    }

    private Single<List<OwnerEntity>> getOwnerList(Context context, List<String> owners) {
        return Flowable.range(0, owners.size())
                .map(new Function<Integer, OwnerEntity>() {
                    @Override
                    public OwnerEntity apply(Integer integer) throws Exception {
                        String address = owners.get(integer);
                        String dbName = AddressInfoDao.getInstance().getAddressNameByAddress(getFormatAddress(address));
                        return new OwnerEntity(UUID.randomUUID().toString(), TextUtils.isEmpty(dbName) ? context.getString(R.string.user_with_serial_number, integer + 1) : dbName, address);
                    }
                })
                .toList();
    }

    private Single<BigInteger> getRequired(String contractAddress, String individualWalletAddress) {
        return Single.fromCallable(new Callable<BigInteger>() {
            @Override
            public BigInteger call() throws Exception {
                return getMultisig(contractAddress, individualWalletAddress).getRequired().send();
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
                                        List<SharedWalletEntity> walletList = SharedWalletManager.getInstance().getWalletList();
                                        for (SharedWalletEntity param : walletList) {
                                            if (!TextUtils.isEmpty(param.getPrefixAddress()) && param.getPrefixAddress().contains(transactionReceipt.getContractAddress())) {
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

    private Flowable<TransactionReceipt> initWallet(Credentials credentials, String uuid, List<OwnerEntity> members, BigInteger ethGasPrice, String contractAddress, int requiredSignNumber) {

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

    private String sendInitWalletTransaction(Credentials credentials, List<OwnerEntity> members, BigInteger ethGasPrice, String contractAddress, int requiredSignNumber) {
        String data = Multisig.initWalletData(getOwners(members), new BigInteger(String.valueOf(requiredSignNumber)));
        String transactionHash = Web3jManager.getInstance().getTransactionHash(credentials, ethGasPrice, INVOKE_GAS_LIMIT, contractAddress, data, BigInteger.ZERO);
        return transactionHash;
    }

    private String getOwners(List<OwnerEntity> members) {
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
                                                                 BigInteger gasPrice) {

        String from = sharedWalletEntity.getPrefixAddress();
        long time = System.currentTimeMillis();
        Multisig multisig = Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), from, Web3jManager.getInstance().getWeb3j(), credentials, new StaticGasProvider(gasPrice, INVOKE_GAS_LIMIT));
        return Single
                .fromCallable(new Callable<TransactionReceipt>() {
                    @Override
                    public TransactionReceipt call() throws Exception {
                        mTransactionContractAddressList.add(sharedWalletEntity.getAddress());
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
                                .ownerWalletAddress(sharedWalletEntity.getCreatorAddress())
                                .contractAddress(sharedWalletEntity.getPrefixAddress())
                                .energonPrice(getGasUsed(gasUsed))
                                .requiredSignNumber(sharedWalletEntity.getRequiredSignNumber())
                                .transactionResult(JSONUtil.toJSONString(getTransactionResultList(sharedWalletEntity.getOwner(), null)))
                                .build();
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
                        sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getCreatorAddress());
                        sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
                        sharedTransactionInfoEntity.setCreateTime(time);
                        sharedTransactionInfoEntity.setRead(true);
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
                        TransactionReceipt transactionReceipt = getConfirmTransactionResult(multisig, sharedTransactionInfoEntity.getTransactionId());
                        if (transactionReceipt != null) {
                            sharedTransactionInfoEntity.setEnergonPrice(getGasUsed(transactionReceipt.getGasUsedRaw()));
                            sharedTransactionInfoEntity.setHash(transactionReceipt.getTransactionHash());
                            sharedTransactionInfoEntity.setTransactionResult(JSONUtil.toJSONString(updateTransactionResultList(sharedTransactionInfoEntity.getTransactionResultList(), sharedWalletEntity.getPrefixCreatorAddress())));
                        }
                        return sharedTransactionInfoEntity;
                    }
                })
                .map(new Function<SharedTransactionInfoEntity, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
                        sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getCreatorAddress());
                        sharedTransactionInfoEntity.setToAddress(sharedTransactionInfoEntity.getContractAddress());
                        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.EXECUTED_CONTRACT.getValue());
                        sharedTransactionInfoEntity.setRead(true);
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
                        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
                        sharedTransactionInfoEntity.setFromAddress(sharedWalletEntity.getPrefixAddress());
                        sharedTransactionInfoEntity.setToAddress(to);
                        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
                        sharedTransactionInfoEntity.setSharedWalletOwnerInfoEntityRealmList(sharedWalletEntity.buildSharedWalletOwnerInfoEntityList());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue());
                        sharedTransactionInfoEntity.setValue(NumberParserUtils.parseDouble(amount));
                        sharedTransactionInfoEntity.setRead(true);
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        SharedTransactionInfoEntity transactionInfoEntity = SharedTransactionInfoDao.getInstance().getSharedTransaction(sharedTransactionInfoEntity.getContractAddress(), sharedTransactionInfoEntity.getTransactionId(), SharedTransactionEntity.TransactionType.SEND_TRANSACTION);
                        //不能全部替换，只能更新某些字段
                        if (transactionInfoEntity == null) {
                            Log.e(TAG, "发送交易插入:" + sharedTransactionInfoEntity.toString());
                            boolean isSuccess = SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
                            if (isSuccess) {
                                mTransactionContractAddressList.remove(sharedTransactionInfoEntity.getContractAddress());
                            }
                        } else {
                            if (!transactionInfoEntity.isRead()) {
                                SharedTransactionInfoDao.getInstance().updateReadWithUuid(transactionInfoEntity.getUuid(), true);
                            }
                        }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        mTransactionContractAddressList.remove(sharedWalletEntity.getAddress());
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
                .map(new Function<TransactionReceipt, SharedTransactionInfoEntity>() {
                    @Override
                    public SharedTransactionInfoEntity apply(TransactionReceipt transactionReceipt) throws Exception {
                        return new SharedTransactionInfoEntity.Builder()
                                .hash(transactionReceipt.getTransactionHash())
                                .memo(sharedTransactionEntity.getMemo())
                                .transactionId(transactionId)
                                .walletName(sharedTransactionEntity.getWalletName())
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
                        sharedTransactionInfoEntity.setRead(true);
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
                        sharedTransactionInfoEntity.setUuid(UUID.randomUUID().toString());
                        sharedTransactionInfoEntity.setFromAddress(sharedTransactionInfoEntity.getContractAddress());
                        sharedTransactionInfoEntity.setToAddress(sharedTransactionEntity.getToAddress());
                        sharedTransactionInfoEntity.setCreateTime(System.currentTimeMillis());
                        sharedTransactionInfoEntity.setSharedWalletOwnerInfoEntityRealmList(sharedTransactionInfoEntity.getSharedWalletOwnerInfoEntityRealmList());
                        sharedTransactionInfoEntity.setTransactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue());
                        sharedTransactionInfoEntity.setValue(NumberParserUtils.parseDouble(sharedTransactionEntity.getValue()));
                        sharedTransactionInfoEntity.setRead(true);
                        return sharedTransactionInfoEntity;
                    }
                })
                .doOnSuccess(new Consumer<SharedTransactionInfoEntity>() {
                    @Override
                    public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                        //todo
//                        String uuid = SharedTransactionInfoDao.getInstance().getSharedTransactionUUID(sharedTransactionEntity.getContractAddress(), sharedTransactionEntity.getTransactionId(), SharedTransactionEntity.TransactionType.SEND_TRANSACTION);
//                        if (uuid != null) {
//                            sharedTransactionInfoEntity.setUuid(uuid);
//                            SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity);
//                        }
                    }
                });
    }

    public void updateTransactionForRead(SharedWalletEntity walletEntity, SharedTransactionEntity transactionEntity) {

        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Log.e(TAG, "updateReadWithUuid....");
                return SharedTransactionInfoDao.getInstance().insertTransaction(transactionEntity.buildSharedTransactionInfoEntity());
            }
        }).filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                Log.e(TAG, "updateReadWithUuid...." + aBoolean);
                return aBoolean;
            }
        }).map(new Function<Boolean, Boolean>() {
            @Override
            public Boolean apply(Boolean aBoolean) throws Exception {
                Log.e(TAG, "map....");
                return SharedTransactionInfoDao.getInstance().hasUnreadTransactionByContractAddress(walletEntity.getAddress());
            }
        }).filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        }).toSingle().compose(new SchedulersTransformer())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean hasUnread) {
                        EventPublisher.getInstance().sendUpdateSharedWalletUnreadMessageEvent(walletEntity.getPrefixAddress(), hasUnread);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.e(TAG, throwable.getMessage());
                    }
                });
    }

    /**
     * 更新所有钱包的所有交易列表
     */
    public void updateTransactionList() {
        Flowable.fromCallable(new Callable<List<SharedWalletEntity>>() {
            @Override
            public List<SharedWalletEntity> call() throws Exception {
                return SharedWalletManager.getInstance().getWalletList();
            }
        }).flatMap(new Function<List<SharedWalletEntity>, Publisher<SharedWalletEntity>>() {
            @Override
            public Publisher<SharedWalletEntity> apply(List<SharedWalletEntity> sharedWalletEntityList) throws Exception {
                return Flowable.fromIterable(sharedWalletEntityList);
            }
        }).flatMap(new Function<SharedWalletEntity, Publisher<List<SharedTransactionInfoEntity>>>() {
            @Override
            public Publisher<List<SharedTransactionInfoEntity>> apply(SharedWalletEntity sharedWalletEntity) throws Exception {
                return getSharedTransactionListFromNet(sharedWalletEntity).toFlowable();
            }
        }).filter(new Predicate<List<SharedTransactionInfoEntity>>() {
            @Override
            public boolean test(List<SharedTransactionInfoEntity> sharedTransactionEntities) throws Exception {
                return !sharedTransactionEntities.isEmpty();
            }
        })
                .doOnNext(new Consumer<List<SharedTransactionInfoEntity>>() {
                    @Override
                    public void accept(List<SharedTransactionInfoEntity> sharedTransactionEntities) throws Exception {
                        String contractAddress = sharedTransactionEntities.get(0).getContractAddress();
                        EventPublisher.getInstance().sendUpdateSharedWalletUnreadMessageEvent(contractAddress, getUnreadMessageCount(sharedTransactionEntities) > 0);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        EventPublisher.getInstance().sendUpdateSharedWalletTransactionEvent();
                    }
                });
    }

    /**
     * 获取未读消息数
     *
     * @param sharedTransactionEntities
     * @return
     */
    private Long getUnreadMessageCount(List<SharedTransactionInfoEntity> sharedTransactionEntities) {
        return Flowable.fromIterable(sharedTransactionEntities)
                .map(new Function<SharedTransactionInfoEntity, Boolean>() {
                    @Override
                    public Boolean apply(SharedTransactionInfoEntity sharedTransactionEntity) throws Exception {
                        return sharedTransactionEntity.isRead();
                    }
                }).filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean isRead) throws Exception {
                        return !isRead;
                    }
                }).count().blockingGet();
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
                    .transactionResult(entity.getTransactionResult())
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

    public Single<Credentials> validPassword(String password, String keyJson) {
        return Single
                .fromCallable(new Callable<Credentials>() {
                    @Override
                    public Credentials call() throws Exception {
                        return JZWalletUtil.getCredentials(password, keyJson);
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

    private Multisig getMultisig(String contractAddress, String walletAddress) {
        return Multisig.load(FileUtil.getStringFromAssets(App.getContext(), BIN_NAME), contractAddress,
                Web3jManager.getInstance().getWeb3j(),
                new ReadonlyTransactionManager(Web3jManager.getInstance().getWeb3j(), walletAddress),
                new StaticGasProvider(DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT));
    }

    private String getTransactionList(String contractAddress, String walletAddress) {
        String transactionList = "";
        try {
            transactionList = getMultisig(contractAddress, walletAddress).getTransactionList(BigInteger.valueOf(0), BigInteger.valueOf(Long.MAX_VALUE)).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactionList;
    }

    private String getMultiSigList(String contractAddress, String walletAddress, String transactionId) {
        String multiSigList = "";
        try {
            multiSigList = getMultisig(contractAddress, walletAddress).getMultiSigList(transactionId).send();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return multiSigList;
    }

    private Single<List<SharedTransactionInfoEntity>> getSharedTransactionListFromNet(SharedWalletEntity sharedWalletEntity) {

        return Flowable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return getTransactionList(sharedWalletEntity.getPrefixAddress(), sharedWalletEntity.getPrefixCreatorAddress());
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).map(new Function<String, String[]>() {
            @Override
            public String[] apply(String s) throws Exception {
                return s.split(":");
            }
        }).flatMap(new Function<String[], Publisher<String>>() {
            @Override
            public Publisher<String> apply(String[] strings) throws Exception {
                return Flowable.fromArray(strings);
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).map(new Function<String, String[]>() {
            @Override
            public String[] apply(String s) throws Exception {
                return s.split("\\|");
            }
        }).filter(new Predicate<String[]>() {
            @Override
            public boolean test(String[] strings) throws Exception {
                return strings.length >= 9;
            }
        }).map(new Function<String[], SharedTransactionEntity>() {
            @Override
            public SharedTransactionEntity apply(String[] contents) throws Exception {
                return parseSharedTransaction(contents, sharedWalletEntity.getPrefixAddress());
            }
        }).map(new Function<SharedTransactionEntity, SharedTransactionEntity>() {
            @Override
            public SharedTransactionEntity apply(SharedTransactionEntity transactionEntity) throws Exception {
                String multiSigList = getMultiSigList(sharedWalletEntity.getPrefixAddress(), sharedWalletEntity.getPrefixCreatorAddress(), transactionEntity.getTransactionId());
                String formatMultiSigList = multiSigList.replaceAll(":,", ":").replaceAll(",:", ":");
                transactionEntity.setWalletName(transactionEntity.getWalletName());
                transactionEntity.setLatestBlockNumber(Web3jManager.getInstance().getLatestBlockNumber());
                transactionEntity.setOwnerEntityList(sharedWalletEntity.getOwner());
                transactionEntity.setRequiredSignNumber(sharedWalletEntity.getRequiredSignNumber());
                transactionEntity.setOwnerWalletAddress(sharedWalletEntity.getCreatorAddress());
                transactionEntity.setBlockNumber(getBlockNumberByHash(getHashByTransactionId(transactionEntity.getTransactionId())));
                transactionEntity.setRead(getRead(sharedWalletEntity.getAddress(), transactionEntity.getTransactionId()));
                if (!TextUtils.isEmpty(formatMultiSigList)) {
                    String transactionResult = JSONUtil.toJSONString(getTransactionResultList(sharedWalletEntity.getOwner(), formatMultiSigList.split(":")));
                    transactionEntity.setTransactionResult(transactionResult);
                }
                return transactionEntity;
            }
        }).map(new Function<SharedTransactionEntity, SharedTransactionInfoEntity>() {
            @Override
            public SharedTransactionInfoEntity apply(SharedTransactionEntity sharedTransactionEntity) throws Exception {
                return sharedTransactionEntity.buildSharedTransactionInfoEntity();
            }
        }).doOnNext(new Consumer<SharedTransactionInfoEntity>() {
            @Override
            public void accept(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                String uuid = SharedTransactionInfoDao.getInstance().getSharedTransactionUUID(sharedTransactionInfoEntity.getContractAddress(), sharedTransactionInfoEntity.getTransactionId(), SharedTransactionEntity.TransactionType.SEND_TRANSACTION);
                if (uuid == null) {
                    uuid = UUID.randomUUID().toString();
                }
                sharedTransactionInfoEntity.setUuid(uuid);
                Log.e(TAG, "loopservice:" + sharedTransactionInfoEntity.toString());
                if (SharedTransactionInfoDao.getInstance().insertTransaction(sharedTransactionInfoEntity)) {
                    mTransactionContractAddressList.remove(sharedTransactionInfoEntity.getContractAddress());
                }
            }
        }).toList();
    }

    private String getTransactionHash(String contractAddress, String transactionId) {
        return Flowable.fromCallable(new Callable<List<SharedTransactionInfoEntity>>() {
            @Override
            public List<SharedTransactionInfoEntity> call() throws Exception {
                return SharedTransactionInfoDao.getInstance().getTransactionListByContractAddress(contractAddress);
            }
        }).flatMap(new Function<List<SharedTransactionInfoEntity>, Publisher<SharedTransactionInfoEntity>>() {
            @Override
            public Publisher<SharedTransactionInfoEntity> apply(List<SharedTransactionInfoEntity> sharedTransactionInfoEntities) throws Exception {
                return Flowable.fromIterable(sharedTransactionInfoEntities);
            }
        }).filter(new Predicate<SharedTransactionInfoEntity>() {
            @Override
            public boolean test(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                return sharedTransactionInfoEntity.getTransactionId() != null && sharedTransactionInfoEntity.getTransactionId().equals(transactionId);
            }
        }).map(new Function<SharedTransactionInfoEntity, String>() {
            @Override
            public String apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                return sharedTransactionInfoEntity.getHash();
            }
        }).firstElement().onErrorReturnItem("").defaultIfEmpty("").blockingGet();
    }

    private List<TransactionResult> updateTransactionResultList(List<TransactionResult> transactionResultList, String address) {

        List<TransactionResult> resultList = new ArrayList<>();

        if (transactionResultList != null && !transactionResultList.isEmpty()) {
            for (TransactionResult result : transactionResultList) {
                if (address.contains(result.getWithoutPrefixAddress().toLowerCase())) {
                    resultList.add(new TransactionResult(result.getUuid(), result.getName(), result.getAddress(), TransactionResult.Status.OPERATION_APPROVAL));
                } else {
                    resultList.add(result);
                }
            }
        }
        return resultList;
    }

    private List<TransactionResult> getTransactionResultList(List<OwnerEntity> ownerEntityList, String[] array) {

        List<TransactionResult> transactionResultList = new ArrayList<>();
        TransactionResult transactionResult = null;
        for (OwnerEntity ownerEntity : ownerEntityList) {
            transactionResult = getTransactionResult(ownerEntity, array);
            transactionResultList.add(transactionResult);
        }
        return transactionResultList;
    }

    private TransactionResult getTransactionResult(OwnerEntity ownerEntity, String[] array) {
        TransactionResult.Status status = TransactionResult.Status.OPERATION_UNDETERMINED;
        if (array != null && array.length >= 2) {
            if (!TextUtils.isEmpty(array[1]) && array[1].contains(ownerEntity.getWithoutPrefixAddress().substring(2).toLowerCase())) {
                status = TransactionResult.Status.OPERATION_APPROVAL;
            }

            if (array.length >= 3) {
                if (!TextUtils.isEmpty(array[2]) && array[2].contains(ownerEntity.getWithoutPrefixAddress().substring(2).toLowerCase())) {
                    status = TransactionResult.Status.OPERATION_REVOKE;
                }
            }
        }
        return new TransactionResult(ownerEntity.getUuid(), ownerEntity.getName(), ownerEntity.getAddress(), status);
    }

    /**
     * 根据transactionId获取交易对应的hash
     *
     * @param transactionId
     * @return
     */
    private String getHashByTransactionId(String transactionId) {
        return Single.fromCallable(new Callable<SharedTransactionInfoEntity>() {
            @Override
            public SharedTransactionInfoEntity call() throws Exception {
                return SharedTransactionInfoDao.getInstance().getTransactionByTransactionId(transactionId);
            }
        }).map(new Function<SharedTransactionInfoEntity, String>() {
            @Override
            public String apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                return sharedTransactionInfoEntity.getHash();
            }
        }).onErrorReturnItem("")
                .blockingGet();
    }

    /**
     * 只要内存中存在，就表示是自己发送的，已读状态就置为true
     *
     * @param contractAddress
     * @param transactionId
     * @return
     */
    private boolean getRead(String contractAddress, String transactionId) {
        if (mTransactionContractAddressList.contains(contractAddress)) {
            return true;
        } else {
            return Single.fromCallable(new Callable<SharedTransactionInfoEntity>() {
                @Override
                public SharedTransactionInfoEntity call() throws Exception {
                    return SharedTransactionInfoDao.getInstance().getSharedTransaction(contractAddress, transactionId, SharedTransactionEntity.TransactionType.SEND_TRANSACTION);
                }
            }).map(new Function<SharedTransactionInfoEntity, Boolean>() {
                @Override
                public Boolean apply(SharedTransactionInfoEntity sharedTransactionInfoEntity) throws Exception {
                    return sharedTransactionInfoEntity.isRead();
                }
            }).onErrorReturnItem(false)
                    .blockingGet();
        }
    }

    /**
     * 解析SharedTransaction
     *
     * @param contents
     * @param contractAddress
     * @return
     */
    private SharedTransactionEntity parseSharedTransaction(String[] contents, String contractAddress) {
        //交易发送方
        String fromAddress = contents[0];
        //交易接收方
        String toAddress = contents[1];
        //发送交易金额
        double sendAmount = BigDecimalUtil.div(contents[2], DEFAULT_WEI);
        //交易创建时间
        long createTime = NumberParserUtils.parseLong(contents[3]);
        //备注
        String memo = new String(contents[4].getBytes(), Charset.forName("UTF-8"));
        //手续费
        double energonPrice = BigDecimalUtil.div(contents[5], DEFAULT_WEI);
        boolean isPending = "1".equals(contents[6]);
        boolean isExecuted = "1".equals(contents[7]);
        String transactionId = contents[8];
        String walletName = SharedWalletManager.getInstance().getSharedWalletNameByContractAddress(contractAddress);

        return new SharedTransactionEntity.Builder(null, createTime, walletName)
                .fromAddress(fromAddress)
                .toAddress(toAddress)
                .value(sendAmount)
                .memo(memo)
                .energonPrice(energonPrice)
                .pending(isPending)
                .executed(isExecuted)
                .contractAddress(contractAddress)
                .transactionId(transactionId)
                .transactionType(SharedTransactionEntity.TransactionType.SEND_TRANSACTION.getValue())
                .build();
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

    private String getFormatAddress(String address) {
        if (address == null) {
            return "";
        }
        if (address.startsWith("0x")) {
            return address;
        }
        return "0x" + address;
    }

}