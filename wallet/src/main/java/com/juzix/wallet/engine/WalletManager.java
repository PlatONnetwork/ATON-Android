package com.juzix.wallet.engine;

import android.text.TextUtils;
import android.widget.TextView;

import com.juzhen.framework.network.ApiErrorCode;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.util.LogUtils;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.entity.WalletEntity;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JZWalletUtil;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.bean.RestrictingItem;
import org.web3j.platon.contracts.RestrictingPlanContract;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.POST;


public class WalletManager {

    public static final int CODE_OK = 0;
    public static final int CODE_ERROR_NAME = -1;
    public static final int CODE_ERROR_PASSWORD = -2;
    public static final int CODE_ERROR_KEYSTORE = -3;
    public static final int CODE_ERROR_PRIVATEKEY = -4;
    public static final int CODE_ERROR_MNEMONIC = -5;
    public static final int CODE_ERROR_WALLET_EXISTS = -200;
    public static final int CODE_ERROR_UNKNOW = -999;

    private List<Wallet> mWalletList = new ArrayList<>();
    public static final int CODE_ERROR_INVALIA_ADDRESS = -5;

    //记录上次的余额
    private BigDecimal mSumAccountBalance = BigDecimal.ZERO;
    //当前选中的钱包
    private Wallet mSelectedWallet;

    private WalletManager() {

    }

    private static class InstanceHolder {
        private static volatile WalletManager INSTANCE = new WalletManager();
    }

    public BigDecimal getSumAccountBalance() {
        return mSumAccountBalance;
    }

    public void setSumAccountBalance(BigDecimal sumAccountBalance) {
        this.mSumAccountBalance = sumAccountBalance;
    }

    public Wallet getSelectedWallet() {
        return mSelectedWallet;
    }

    public void setSelectedWallet(Wallet mSelectedWallet) {
        this.mSelectedWallet = mSelectedWallet;
        EventPublisher.getInstance().sendUpdateSelectedWalletEvent(mSelectedWallet);
    }

    public String getSelectedWalletAddress() {
        return mSelectedWallet == null ? null : mSelectedWallet.getPrefixAddress();
    }

    public static WalletManager getInstance() {
        return WalletManager.InstanceHolder.INSTANCE;
    }

    public void init() {
        if (!mWalletList.isEmpty()) {
            mWalletList.clear();
        }

        List<WalletEntity> walletInfoList = WalletDao.getWalletInfoList();
        for (WalletEntity walletInfoEntity : walletInfoList) {
            try {
                mWalletList.add(walletInfoEntity.buildWalletEntity());
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }
    }

    public void addWallet(Wallet walletEntity) {
        if (!mWalletList.contains(walletEntity)) {
            mWalletList.add(walletEntity);
        }
    }

    public void updateAccountBalance(AccountBalance accountBalance) {
        if (mWalletList.isEmpty() || accountBalance == null) {
            return;
        }

        int position = getPositionByAddress(accountBalance.getPrefixAddress());

        if (position == -1) {
            return;
        }

        Wallet wallet = mWalletList.get(position);
        wallet.setAccountBalance(accountBalance);
    }

    /**
     * 根据钱包地址获取钱包账户信息
     *
     * @param address
     * @return
     */
    public AccountBalance getAccountBalance(String address) {
        int position = getPositionByAddress(address);
        if (position == -1) {
            return null;
        }

        return mWalletList.get(position).getAccountBalance();
    }

    public List<String> getAddressList() {
        return Flowable.fromIterable(mWalletList)
                .map(new Function<Wallet, String>() {
                    @Override
                    public String apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getPrefixAddress();
                    }
                }).collect(new Callable<List<String>>() {
                    @Override
                    public List<String> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<String>, String>() {
                    @Override
                    public void accept(List<String> strings, String s) throws Exception {
                        strings.add(s);
                    }
                }).blockingGet();
    }

    public String getWalletNameByWalletAddress(String walletAddress) {
        if (!mWalletList.isEmpty()) {
            for (Wallet walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equals(walletEntity.getPrefixAddress())) {
                    return walletEntity.getName();
                }
            }
        }

        return "";
    }

    /**
     * 获取钱包头像
     */
    public String getWalletIconByWalletAddress(String walletAddress) {
        if (!mWalletList.isEmpty()) {
            for (Wallet walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equals(walletEntity.getPrefixAddress())) {
                    return walletEntity.getAvatar();
                }

            }
        }
        return null;
    }

    /**
     * 根据钱包地址获取钱包
     */
    public Wallet getWalletEntityByWalletAddress(String walletAddress) {

        if (!mWalletList.isEmpty()) {
            for (Wallet walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equals(walletEntity.getPrefixAddress())) {
                    return walletEntity;
                }
            }
        }

        return null;
    }

    /**
     * 获取第一个有效的(余额大于0)个人钱包，
     *
     * @return
     */
    public Wallet getFirstValidIndividualWalletBalance() {

        if (!mWalletList.isEmpty()) {
            for (int i = 0; i < mWalletList.size(); i++) {
                Wallet walletEntity = mWalletList.get(i);
                if (BigDecimalUtil.isBiggerThanZero(walletEntity.getFreeBalance())) {
                    return walletEntity;
                }
            }
        }

        return null;

    }


    public List<Wallet> getWalletList() {
        return mWalletList;
    }

    public String generateMnemonic() {
        return WalletServiceImpl.getInstance().generateMnemonic();
    }

    private Single<String> createMnemonic() {
        return Single.create(new SingleOnSubscribe<String>() {
            @Override
            public void subscribe(SingleEmitter<String> emitter) throws Exception {
                String mnemonic = generateMnemonic();
                if (JZWalletUtil.isValidMnemonic(mnemonic)) {
                    emitter.onSuccess(mnemonic);
                } else {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED));
                }
            }
        });
    }

    public Single<Wallet> createWallet(String name, String password) {

        return createMnemonic()
                .flatMap(new Function<String, SingleSource<? extends Wallet>>() {
                    @Override
                    public SingleSource<? extends Wallet> apply(String mnemonic) throws Exception {
                        return importMnemonic(mnemonic, name, password);
                    }
                });
    }

    public int importKeystore(String store, String name, String password) {
        if (!JZWalletUtil.isValidKeystore(store)) {
            return CODE_ERROR_KEYSTORE;
        }
        if (TextUtils.isEmpty(name)) {
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            Wallet entity = WalletServiceImpl.getInstance().importKeystore(store, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            for (Wallet param : mWalletList) {
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setMnemonic("");
            entity.setChainId(NodeManager.getInstance().getChainId());
            mWalletList.add(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }
    public int importWalletAddress(String walletAddress) {
        if (!JZWalletUtil.isValidAddress(walletAddress)) {
            return CODE_ERROR_INVALIA_ADDRESS;
        }
        Wallet mWallet = new Wallet();
        mWallet.setAddress(walletAddress);
        mWallet.setUuid(UUID.randomUUID().toString());
        mWallet.setAvatar(WalletServiceImpl.getInstance().getWalletAvatar());

        for (Wallet param : mWalletList) {
            if (param.getPrefixAddress().toLowerCase().equals(mWallet.getPrefixAddress().toLowerCase())) {
                return CODE_ERROR_WALLET_EXISTS;
            }
        }
        mWallet.setChainId(NodeManager.getInstance().getChainId());
//        mWallet.setMnemonic("");
        mWallet.setCreateTime(System.currentTimeMillis());
        mWallet.setUpdateTime(System.currentTimeMillis());
        mWallet.setName("LAT-Wallet" + AppSettings.getInstance().getWalletNameSequence());
        mWalletList.add(mWallet);
        WalletDao.insertWalletInfo(mWallet.buildWalletInfoEntity());
        AppSettings.getInstance().setOperateMenuFlag(false);
        return CODE_OK;
    }

    public int importPrivateKey(String privateKey, String name, String password) {
        if (!JZWalletUtil.isValidPrivateKey(privateKey)) {
            return CODE_ERROR_PRIVATEKEY;
        }
        if (TextUtils.isEmpty(name)) {
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            Wallet entity = WalletServiceImpl.getInstance().importPrivateKey(privateKey, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            for (Wallet param : mWalletList) {
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setMnemonic("");
            entity.setChainId(NodeManager.getInstance().getChainId());
            mWalletList.add(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }

    private Single<Wallet> importMnemonic(String mnemonic, String name, String password) {
        return Single.create(new SingleOnSubscribe<Wallet>() {
            @Override
            public void subscribe(SingleEmitter<Wallet> emitter) throws Exception {
                Wallet walletEntity = WalletServiceImpl.getInstance().importMnemonic(mnemonic, name, password);
                if (walletEntity == null || isWalletAddressExists(walletEntity.getPrefixAddress().toLowerCase())) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED));
                } else {
                    walletEntity.setMnemonic(JZWalletUtil.encryptMnemonic(walletEntity.getKey(), mnemonic, password));
                    walletEntity.setChainId(NodeManager.getInstance().getChainId());
                    emitter.onSuccess(walletEntity);
                }
            }
        });
    }

    public int importMnemonic(Wallet walletEntity, String mnemonic, String name, String password) {
        if (!JZWalletUtil.isValidMnemonic(mnemonic)) {
            return CODE_ERROR_MNEMONIC;
        }
        if (TextUtils.isEmpty(name)) {
            return CODE_ERROR_NAME;
        }
        if (TextUtils.isEmpty(password)) {
            return CODE_ERROR_PASSWORD;
        }
        try {
            Wallet entity = WalletServiceImpl.getInstance().importMnemonic(mnemonic, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            for (Wallet param : mWalletList) {
                if (param.getPrefixAddress().toLowerCase().equals(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setMnemonic("");
            entity.setChainId(NodeManager.getInstance().getChainId());
            mWalletList.add(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            AppSettings.getInstance().setOperateMenuFlag(false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }

//    public String exportKeystore(IndividualWalletEntity wallet, String password) {
//        return WalletServiceImpl.getInstance().exportKeystore(wallet, password);
//    }
//
//    public String exportPrivateKey(IndividualWalletEntity wallet, String password) {
//        return WalletServiceImpl.getInstance().exportPrivateKey(wallet, password);
//    }

    public boolean emptyMnemonic(String mnenonic) {
        String uuid = "";
        for (Wallet walletEntity : mWalletList) {
            if (mnenonic.equals(walletEntity.getMnemonic())) {
                walletEntity.setMnemonic("");
                uuid = walletEntity.getUuid();
                break;
            }
        }
        if (TextUtils.isEmpty(uuid)) {
            return false;
        }
        return WalletDao.updateMnemonicWithUuid(uuid, "");
    }

    public boolean updateWalletName(Wallet wallet, String newName) {
        for (Wallet walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                walletEntity.setName(newName);
                return true;
            }
        }
        return false;
    }

    public void updateWalletBalance(String address, double balance) {
        int position = -1;
        for (int i = 0; i < mWalletList.size(); i++) {
            Wallet walletEntity = mWalletList.get(i);
            if (address.equals(walletEntity.getPrefixAddress())) {
                position = i;
                break;
            }
        }
        if (position != -1) {
//            mWalletList.get(position).setBalance(balance);
        }
    }

    public Wallet getWalletByAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return null;
        }
        for (Wallet walletEntity : mWalletList) {
            if (walletEntity.getPrefixAddress().contains(address)) {
                return walletEntity;
            }
        }
        return null;
    }

    public boolean deleteWallet(Wallet wallet) {
        for (Wallet walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                mWalletList.remove(walletEntity);
                break;
            }
        }
        return WalletDao.deleteWalletInfo(wallet.getUuid());
    }

    public boolean isValidWallet(Wallet walletEntity, String password) {
        try {
            return JZWalletUtil.decrypt(walletEntity.getKey(), password) != null;
        } catch (Exception exp) {
            exp.printStackTrace();
            return false;
        }
    }

    public boolean isWalletNameExists(String walletName) {
        return Flowable
                .fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getName().toLowerCase().equals(walletName);
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .firstElement()
                .defaultIfEmpty(false)
                .blockingGet();
    }

    public boolean isWalletAddressExists(String prefixAddress) {
        return Flowable
                .fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getPrefixAddress().toLowerCase().equals(prefixAddress.toLowerCase());
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .firstElement()
                .defaultIfEmpty(false)
                .blockingGet();
    }

    public Observable<BigDecimal> getAccountBalance() {
        return ServerUtils
                .getCommonApi()
                .getAccountBalance(ApiRequestBody.newBuilder()
                        .put("addrs", WalletManager.getInstance().getAddressList())
                        .build())
                .toFlowable()
                .flatMap(new Function<Response<ApiResponse<List<AccountBalance>>>, Publisher<AccountBalance>>() {
                    @Override
                    public Publisher<AccountBalance> apply(Response<ApiResponse<List<AccountBalance>>> apiResponseResponse) throws Exception {
                        if (apiResponseResponse != null && apiResponseResponse.isSuccessful() && apiResponseResponse.body().getResult() == ApiErrorCode.SUCCESS) {
                            return Flowable.fromIterable(apiResponseResponse.body().getData());
                        }
                        return Flowable.error(new Throwable());
                    }
                })
                .doOnNext(new Consumer<AccountBalance>() {
                    @Override
                    public void accept(AccountBalance accountBalance) throws Exception {
                        WalletManager.getInstance().updateAccountBalance(accountBalance);
                    }
                })
                .map(new Function<AccountBalance, BigDecimal>() {
                    @Override
                    public BigDecimal apply(AccountBalance accountBalance) throws Exception {
                        return new BigDecimal(accountBalance.getFree());
                    }
                })
                .reduce(new BiFunction<BigDecimal, BigDecimal, BigDecimal>() {
                    @Override
                    public BigDecimal apply(BigDecimal balance1, BigDecimal banalce2) throws Exception {
                        return balance1.add(banalce2);
                    }
                })
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(5, TimeUnit.SECONDS);
                    }
                })
                .doOnNext(new Consumer<BigDecimal>() {
                    @Override
                    public void accept(BigDecimal sumAccountBalance) throws Exception {
                        if (!sumAccountBalance.equals(mSumAccountBalance)) {
                            LogUtils.e("钱包总余额已改变。。。");
                            EventBus.getDefault().post(new Event.SumAccountBalanceChanged());
                        }
                        setSumAccountBalance(sumAccountBalance);
                    }
                })
                .toObservable();
    }

    private int getPositionByAddress(String address) {
        return Flowable
                .range(0, mWalletList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return mWalletList.get(integer).getPrefixAddress().equals(address);
                    }
                })
                .firstElement()
                .defaultIfEmpty(-1)
                .onErrorReturnItem(-1)
                .blockingGet();
    }

}
