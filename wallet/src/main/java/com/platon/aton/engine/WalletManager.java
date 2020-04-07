package com.platon.aton.engine;

import android.text.TextUtils;

import com.platon.aton.app.CustomThrowable;
import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.network.ApiErrorCode;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.PreferenceTool;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.SingleSource;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import retrofit2.Response;


/**
 * @author ziv
 */
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

    /**
     * 记录上次的余额
     */
    private BigDecimal mSumAccountBalance = BigDecimal.ZERO;

    private WalletManager() {

    }

    private static class InstanceHolder {
        private static volatile WalletManager INSTANCE = new WalletManager();
    }

    public static WalletManager getInstance() {
        return WalletManager.InstanceHolder.INSTANCE;
    }

    public BigDecimal getSumAccountBalance() {
        return mSumAccountBalance;
    }

    public void setSumAccountBalance(BigDecimal sumAccountBalance) {
        this.mSumAccountBalance = sumAccountBalance;
    }

    public Wallet getSelectedWallet() {
        if (mWalletList.size() == 0) {
            return getWalletListFromDB().toFlowable().flatMap(new Function<List<Wallet>, Publisher<Wallet>>() {
                @Override
                public Publisher<Wallet> apply(List<Wallet> walletList) throws Exception {
                    return Flowable.fromIterable(walletList);
                }
            })
                    .filter(new Predicate<Wallet>() {
                        @Override
                        public boolean test(Wallet wallet) throws Exception {
                            return wallet.isSelected();
                        }
                    })
                    .firstElement()
                    .defaultIfEmpty(mWalletList.get(0))
                    .onErrorReturnItem(mWalletList.get(0))
                    .blockingGet();
        }
        return getSelectedWalletFromWalletList();
    }

    private Wallet getSelectedWalletFromWalletList() {

        return Flowable.fromIterable(mWalletList)
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return wallet.isSelected();
                    }
                })
                .firstElement()
                .defaultIfEmpty(mWalletList.get(0))
                .onErrorReturnItem(mWalletList.get(0))
                .blockingGet();
    }

    public void setWalletList(List<Wallet> walletList) {
        this.mWalletList = walletList;
    }

    /**
     * 获取选中钱包的地址
     *
     * @return
     */
    public String getSelectedWalletAddress() {

        Wallet selectedWallet = getSelectedWallet();

        return selectedWallet == null ? null : selectedWallet.getPrefixAddress();

    }

    public void init() {
        mWalletList.clear();
        mWalletList = getWalletListFromDB().blockingGet();
    }

    public Single<List<Wallet>> getWalletListFromDB() {
        return Flowable
                .fromCallable(new Callable<List<WalletEntity>>() {
                    @Override
                    public List<WalletEntity> call() throws Exception {
                        return WalletDao.getWalletInfoList();
                    }
                })
                .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
                    @Override
                    public Publisher<Wallet> apply(List<WalletEntity> walletEntities) throws Exception {
                        return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                            @Override
                            public Wallet apply(Integer integer) throws Exception {
                                if (integer == 0) {
                                    Wallet wallet = walletEntities.get(0).buildWallet();
                                    wallet.setSelected(true);
                                    return wallet;
                                } else {
                                    return walletEntities.get(integer).buildWallet();
                                }
                            }
                        });
                    }
                })
                .toList();
    }

    public void addWallet(Wallet wallet) {
        if (!mWalletList.contains(wallet)) {
            Wallet cloneWallet = wallet.clone();
            if (!isExistSelectedWallet()) {
                cloneWallet.setSelected(true);
                EventPublisher.getInstance().sendWalletSelectedChangedEvent();
            }
            mWalletList.add(cloneWallet);
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

    public void update() {

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
        if (mWalletList == null || mWalletList.isEmpty()) {
            return new ArrayList<>();
        }
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
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equalsIgnoreCase(walletEntity.getPrefixAddress())) {
                    return walletEntity.getName();
                }
            }
        }
        return "";
    }

    public boolean isObservedWallet(String walletAddress) {

        if (mWalletList.isEmpty()) {
            return false;
        }

        return Flowable
                .fromIterable(mWalletList)
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return wallet.getPrefixAddress().equalsIgnoreCase(walletAddress);
                    }
                })
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet wallet) throws Exception {
                        return wallet.isObservedWallet();
                    }
                })
                .defaultIfEmpty(false)
                .blockingFirst();

    }

    /**
     * 获取钱包头像
     */
    public String getWalletIconByWalletAddress(String walletAddress) {
        if (!mWalletList.isEmpty()) {
            for (Wallet walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equalsIgnoreCase(walletEntity.getPrefixAddress())) {
                    return walletEntity.getAvatar();
                }

            }
        }
        return null;
    }

    /**
     * 根据钱包地址获取钱包
     */
    public Wallet getWalletByWalletAddress(String walletAddress) {

        if (!mWalletList.isEmpty()) {
            for (Wallet walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equalsIgnoreCase(walletEntity.getPrefixAddress())) {
                    return walletEntity;
                }
            }
        }

        return null;
    }

    public List<Wallet> getWalletList() {
        if (mWalletList.isEmpty()) {
            return mWalletList = getWalletListFromDB().blockingGet();
        }
        Collections.sort(mWalletList);
        return mWalletList;
    }

    public String generateMnemonic() {
        return WalletServiceImpl.getInstance().generateMnemonic();
    }

    public String exportPrivateKey(String mnemonic) {
        return WalletServiceImpl.getInstance().exportPrivateKey(mnemonic);
    }

    private boolean isExistSelectedWallet() {
        if (mWalletList.isEmpty()) {
            return false;
        }

        return Flowable.fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet wallet) throws Exception {
                        return wallet.isSelected();
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
                .onErrorReturnItem(false)
                .blockingGet();
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
                if (param.getPrefixAddress().toLowerCase().equalsIgnoreCase(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setBackedUp(true);
            entity.setMnemonic("");
            entity.setChainId(NodeManager.getInstance().getChainId());
            addWallet(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
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
            if (param.getPrefixAddress().toLowerCase().equalsIgnoreCase(mWallet.getPrefixAddress().toLowerCase())) {
                return CODE_ERROR_WALLET_EXISTS;
            }
        }
        mWallet.setBackedUp(true);
        mWallet.setChainId(NodeManager.getInstance().getChainId());
        mWallet.setCreateTime(System.currentTimeMillis());
        mWallet.setName(String.format("%s%d", "Wallet", PreferenceTool.getInt(NodeManager.getInstance().getChainId(), 1)));
        addWallet(mWallet);
        WalletDao.insertWalletInfo(mWallet.buildWalletInfoEntity());
        PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
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
                if (param.getPrefixAddress().toLowerCase().equalsIgnoreCase(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setBackedUp(true);
            entity.setMnemonic("");
            entity.setChainId(NodeManager.getInstance().getChainId());
            addWallet(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
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
                if (param.getPrefixAddress().toLowerCase().equalsIgnoreCase(entity.getPrefixAddress().toLowerCase())) {
                    return CODE_ERROR_WALLET_EXISTS;
                }
            }
            entity.setBackedUp(true);
            entity.setMnemonic(JZWalletUtil.encryptMnemonic(entity.getKey(), mnemonic, password));
            entity.setChainId(NodeManager.getInstance().getChainId());
            addWallet(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
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


    public boolean updateBackedUpWithUuid(String uuid, boolean backedUp) {
        if (TextUtils.isEmpty(uuid)) {
            return false;
        }
        for (Wallet walletEntity : mWalletList) {
            if (uuid.equals(walletEntity.getUuid())) {
                walletEntity.setBackedUp(backedUp);
                break;
            }
        }
        return WalletDao.updateBackedUpWithUuid(uuid, backedUp);
    }

    public void updateWalletBackedUpPromptWithUUID(String uuid, boolean backedUpPrompt) {

        if (TextUtils.isEmpty(uuid)) {
            return;
        }

        if (mWalletList.isEmpty()) {
            return;
        }


        Flowable.range(0, mWalletList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return TextUtils.equals(mWalletList.get(integer).getUuid(), uuid);
                    }
                })
                .firstElement()
                .doOnSuccess(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        mWalletList.get(integer).setBackedUpPrompt(backedUpPrompt);
                    }
                })
                .subscribe();
    }

    public Wallet getWalletByAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return null;
        }

        for (Wallet walletEntity : mWalletList) {
            if (walletEntity.getPrefixAddress().toLowerCase().contains(address.toLowerCase())) {
                return walletEntity;
            }
        }
        return Wallet.getNullInstance();
    }

    public boolean deleteWallet(Wallet wallet) {
        for (Wallet walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                mWalletList.remove(walletEntity);
                break;
            }
        }
        if (!isExistSelectedWallet() && !mWalletList.isEmpty()) {
            mWalletList.get(0).setSelected(true);
            EventPublisher.getInstance().sendWalletSelectedChangedEvent();
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
        if (mWalletList == null || mWalletList.isEmpty()) {
            return false;
        }
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
        if (mWalletList == null || mWalletList.isEmpty()) {
            return false;
        }
        return Flowable
                .fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getPrefixAddress().toLowerCase().equalsIgnoreCase(prefixAddress.toLowerCase());
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
        return Observable
                .interval(0, 5, TimeUnit.SECONDS)
                .flatMap(new Function<Long, ObservableSource<BigDecimal>>() {
                    @Override
                    public ObservableSource<BigDecimal> apply(Long aLong) throws Exception {
                        List<String> walletAddress = WalletManager.getInstance().getAddressList();
                        return ServerUtils
                                .getCommonApi()
                                .getAccountBalance(ApiRequestBody.newBuilder()
                                        .put("addrs", walletAddress)
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
                                .map(new Function<AccountBalance, AccountBalance>() {
                                    @Override
                                    public AccountBalance apply(AccountBalance accountBalance) throws Exception {
                                        //保留小数点后8位，截断
                                        accountBalance.setFree(AmountUtil.getPrettyBalance(accountBalance.getFree(), 8));
                                        accountBalance.setLock(AmountUtil.getPrettyBalance(accountBalance.getLock(), 8));
                                        return accountBalance;
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
                                .defaultIfEmpty(BigDecimal.ZERO)
                                .onErrorReturnItem(BigDecimal.ZERO)
                                .doOnSuccess(new Consumer<BigDecimal>() {
                                    @Override
                                    public void accept(BigDecimal sumAccountBalance) throws Exception {
                                        if (!sumAccountBalance.equals(mSumAccountBalance)) {
                                            EventBus.getDefault().post(new Event.SumAccountBalanceChanged());
                                        }
                                        setSumAccountBalance(sumAccountBalance);
                                    }
                                })
                                .toObservable();
                    }
                });

    }

    public int getPositionByAddress(String address) {
        return Flowable
                .range(0, mWalletList.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return mWalletList.get(integer).getPrefixAddress().equalsIgnoreCase(address);
                    }
                })
                .firstElement()
                .defaultIfEmpty(-1)
                .onErrorReturnItem(-1)
                .blockingGet();
    }

    /**
     * 获取所有钱包的总计
     *
     * @return
     */
    public Observable<BigDecimal> getTotal() {
        return Flowable.fromIterable(mWalletList)
                .map(new Function<Wallet, AccountBalance>() {
                    @Override
                    public AccountBalance apply(Wallet wallet) throws Exception {
                        return wallet.getAccountBalance();
                    }
                }).map(new Function<AccountBalance, BigDecimal>() {

                    @Override
                    public BigDecimal apply(AccountBalance accountBalance) throws Exception {
                        return new BigDecimal(accountBalance.getFree());
                    }
                }).reduce(new BiFunction<BigDecimal, BigDecimal, BigDecimal>() {
                    @Override
                    public BigDecimal apply(BigDecimal balance1, BigDecimal balance2) throws Exception {
                        return balance1.add(balance2);
                    }
                }).toObservable();
    }


    /**
     * 一级排序按照可用余额从大到小排序，二级排序按照钱包创建时间从旧到新排序
     * 默认选中的钱包：按照钱包列表排序规则第一位的钱包
     *
     * @return
     */
    public Wallet getFirstSortedWallet() {
        if (mWalletList.isEmpty()) {
            return Wallet.getNullInstance();
        }

        Collections.sort(mWalletList, new BalanceComparator());

        Wallet wallet = getWalletByBalanceBiggerThanZero();

        if (wallet.isNull()) {
            Collections.sort(mWalletList, new CreateTimeComparator());
            wallet = mWalletList.get(0);
        }
        return wallet;
    }

    private Wallet getWalletByBalanceBiggerThanZero() {
        return Flowable
                .fromIterable(mWalletList)
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return BigDecimalUtil.isBiggerThanZero(wallet.getFreeBalance());
                    }
                })
                .defaultIfEmpty(Wallet.getNullInstance())
                .blockingFirst();
    }

    static class BalanceComparator implements Comparator<Wallet> {

        @Override
        public int compare(Wallet o1, Wallet o2) {
            return BigDecimalUtil.compareTo(o2.getFreeBalance(), o1.getFreeBalance());
        }
    }

    static class CreateTimeComparator implements Comparator<Wallet> {

        @Override
        public int compare(Wallet o1, Wallet o2) {
            return Long.compare(o1.getCreateTime(), o2.getCreateTime());
        }
    }

}
