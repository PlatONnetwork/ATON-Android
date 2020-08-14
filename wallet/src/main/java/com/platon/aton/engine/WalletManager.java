package com.platon.aton.engine;

import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.platon.aton.BuildConfig;
import com.platon.aton.app.CustomThrowable;
import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.Bech32Address;
import com.platon.aton.entity.TransactionWallet;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletDepth;
import com.platon.aton.entity.WalletSelectedIndex;
import com.platon.aton.entity.WalletType;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.aton.utils.AddressFormatUtil;
import com.platon.aton.utils.AmountUtil;
import com.platon.aton.utils.BigDecimalUtil;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.app.Constants;
import com.platon.framework.network.ApiErrorCode;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.PreferenceTool;

import org.greenrobot.eventbus.EventBus;
import org.reactivestreams.Publisher;
import org.web3j.crypto.WalletApplication;
import org.web3j.crypto.bech32.AddressBech32;
import org.web3j.crypto.bech32.AddressBehavior;
import org.web3j.crypto.bech32.AddressManager;
import org.web3j.crypto.bech32.Bech32;

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
import io.reactivex.SingleObserver;
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

    /**
     * 初始化钱包环境
     */
    public void initWalletNet(){
        String chainId = NodeManager.getInstance().getChainId();
        if(chainId.equals(BuildConfig.ID_INNERTEST_NET)){//作为主网
            WalletApplication.init(WalletApplication.MAINNET, AddressManager.ADDRESS_TYPE_BECH32, AddressBehavior.CHANNLE_PLATON);
        }else{
            WalletApplication.init(WalletApplication.TESTNET, AddressManager.ADDRESS_TYPE_BECH32, AddressBehavior.CHANNLE_PLATON);
        }
        perInit();
    }

    public boolean isMainNetWalletAddress(){
        if(NodeManager.getInstance().getChainId().equals(BuildConfig.ID_INNERTEST_NET)){
            return true;
        }else{
            return false;
        }
    }



    public BigDecimal getSumAccountBalance() {
        return mSumAccountBalance;
    }

    public void setSumAccountBalance(BigDecimal sumAccountBalance) {
        this.mSumAccountBalance = sumAccountBalance;
    }

    /**
     * 获取选中钱包
     * @return
     */
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
                            return wallet.getSelectedIndex() == WalletSelectedIndex.SELECTED;
                        }
                    })
                    .firstElement()
                    .defaultIfEmpty(Wallet.getNullInstance())
                    .onErrorReturnItem(Wallet.getNullInstance())
                    .blockingGet();
        }
        return getSelectedWalletFromWalletList();
    }

    private Wallet getSelectedWalletFromWalletList() {

        return Flowable.fromIterable(mWalletList)
                .filter(new Predicate<Wallet>() {
                    @Override
                    public boolean test(Wallet wallet) throws Exception {
                        return wallet.getSelectedIndex() == WalletSelectedIndex.SELECTED;
                    }
                })
                .firstElement()
                .defaultIfEmpty(mWalletList.get(0))
                .onErrorReturnItem(mWalletList.get(0))
                .blockingGet();
    }

    /**
     *  更新首页wallet:
     *     1、更新cache data及选中状态
     *     2、更新DB选中钱包状态
     *     3、发布更新通知
     * @param selectedWallet
     */
    public void addAndSelectedWalletStatusNotice(Wallet selectedWallet){
       if(mWalletList.size() > 0){
           //重置状态为未选中
           for(int i = 0; i < mWalletList.size() ; i++) {
               if(mWalletList.get(i).getSelectedIndex() == WalletSelectedIndex.SELECTED){
                   mWalletList.get(i).setSelectedIndex(WalletSelectedIndex.UNSELECTED);
               }
           }
       }
        WalletManager.getInstance().addWallet(selectedWallet);



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


    /**
     * 老地址转换Bech32
     */
    public void perInit(){
        WalletDao.updateBetch32AddressWithWallet();
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
                                    wallet.setSelectedIndex(WalletSelectedIndex.SELECTED);
                                    //wallet.setSelected(true);
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

    public Single<List<Wallet>> getWalletInfoListByOrdinaryAndSubWallet() {
        return Flowable
                .fromCallable(new Callable<List<WalletEntity>>() {
                    @Override
                    public List<WalletEntity> call() throws Exception {
                        return WalletDao.getWalletInfoListByOrdinaryAndSubWallet();
                    }
                })
                .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
                    @Override
                    public Publisher<Wallet> apply(List<WalletEntity> walletEntities) throws Exception {
                        return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                            @Override
                            public Wallet apply(Integer integer) throws Exception {
                                return walletEntities.get(integer).buildWallet();
                            }
                        });
                    }
                })
                .toList();
    }


    public Single<List<Wallet>> getWalletListFromDBByOrdinaryAndHD() {
        return Flowable
                .fromCallable(new Callable<List<WalletEntity>>() {
                    @Override
                    public List<WalletEntity> call() throws Exception {
                        return WalletDao.getWalletInfoListByOrdinaryAndHD();
                    }
                })
                .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
                    @Override
                    public Publisher<Wallet> apply(List<WalletEntity> walletEntities) throws Exception {
                        return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                            @Override
                            public Wallet apply(Integer integer) throws Exception {
                                return walletEntities.get(integer).buildWallet();
                            }
                        });
                    }
                })
                .toList();
    }


    public Single<List<Wallet>> getHDWalletListDBByParentId(String parendId) {
        return Flowable
                .fromCallable(new Callable<List<WalletEntity>>() {
                    @Override
                    public List<WalletEntity> call() throws Exception {
                        return WalletDao.getHDWalletListByParentId(parendId);
                    }
                })
                .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
                    @Override
                    public Publisher<Wallet> apply(List<WalletEntity> walletEntities) throws Exception {
                        return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                            @Override
                            public Wallet apply(Integer integer) throws Exception {
                                return walletEntities.get(integer).buildWallet();
                            }
                        });
                    }
                })
                .toList();
    }

    public Wallet getWalletInfoByUuid(String uuid){
         return Flowable.fromCallable(new Callable<WalletEntity>(){
              @Override
              public WalletEntity call() throws Exception {
                  return WalletDao.getWalletByUuid(uuid);
              }
          }).map(new Function<WalletEntity, Wallet>() {

              @Override
              public Wallet apply(WalletEntity walletEntity) throws Exception {
                  return walletEntity.buildWallet();
              }
          }).blockingFirst();
    }


    public Wallet getWalletInfoByAddress(String address){
        return Flowable.fromCallable(new Callable<WalletEntity>(){
            @Override
            public WalletEntity call() throws Exception {
                return WalletDao.getWalletInfoByAddress(address);
            }
        }).map(new Function<WalletEntity, Wallet>() {

            @Override
            public Wallet apply(WalletEntity walletEntity) throws Exception {
                return walletEntity.buildWallet();
            }
        }).blockingFirst();
    }


    /**
     * 查询交易钱包数据
     * @return
     */
    public List<TransactionWallet>  getTransactionWalletData(){

        return Flowable.fromCallable(new Callable<List<Wallet>>() {

            @Override
            public List<Wallet> call() throws Exception {
                return getWalletListByOrdinaryAndHD();
            }
        }).flatMap(new Function<List<Wallet>, Publisher<TransactionWallet>>() {
            @Override
            public Publisher<TransactionWallet> apply(List<Wallet> wallets) throws Exception {
                return Flowable.range(0, wallets.size()).map(new Function<Integer, TransactionWallet>() {
                    @Override
                    public TransactionWallet apply(Integer integer) throws Exception {
                        TransactionWallet transactionWallet = new TransactionWallet();

                           Wallet wallet = wallets.get(integer);
                           transactionWallet.setWallet(wallet);
                           if(wallet.isHD() && wallet.getDepth() == 0){//母钱包
                               transactionWallet.setSubWallets(getHDWalletListByParentId(wallet.getUuid()));
                           }
                        return transactionWallet;
                    }
                });
            }
        }).toList().blockingGet();
    }


    /**
     * 查询所有钱包中【普通 + HD钱包中子钱包】数量
     * @return
     */
    public int getWalletInfoListByOrdinaryAndSubWalletNum(){
        return getWalletInfoListByOrdinaryAndSubWallet().blockingGet().size();
    }

    /**
     * 查询所有【普通钱包 + HD的母钱包】
     * @return
     */
    public List<Wallet> getWalletListByOrdinaryAndHD(){
        List<Wallet> wallets = getWalletListFromDBByOrdinaryAndHD().blockingGet();
        Collections.sort(wallets);
        return wallets;
    }

    /**
     * 查询HD钱包之子钱包根据parentId
     * @return
     */
    public List<Wallet> getHDWalletListByParentId(String parentId){
        List<Wallet> wallets = getHDWalletListDBByParentId(parentId).blockingGet();
        return wallets;
    }

    /**
     * 查询助记词根据Uuid
     * @param uuid
     * @return
     */
    public String getMnemonicByUuid(String uuid){
       Wallet wallet =  getWalletInfoByUuid(uuid);
       if(wallet != null){
           return wallet.getMnemonic();
       }
       return "";
    }


    /**
     * 查询钱包集合，根据walletType, name, address
     * @param walletType
     * @param name
     * @param address
     * @return
     */
    public List<Wallet> getWalletListByAddressAndNameAndType(@WalletTypeSearch int walletType,String name,String address){

        //查询所有钱包
        Flowable<List<Wallet>> flowable1 = Flowable.fromCallable(new Callable<List<WalletEntity>>() {
                    @Override
                    public List<WalletEntity> call() throws Exception {
                        return WalletDao.getWalletListByAddressAndNameAndType(walletType,name,address);
                    }
                })
                .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
                    @Override
                    public Publisher<Wallet> apply(List<WalletEntity> walletEntities) throws Exception {
                        return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                            @Override
                            public Wallet apply(Integer integer) throws Exception {
                                return walletEntities.get(integer).buildWallet();
                            }
                        });
                    }
                }).toList()
                  .toFlowable();

        //查询所有母钱包
        Flowable<List<Wallet>> flowable2 = Flowable.fromCallable(new Callable<List<WalletEntity>>() {
            @Override
            public List<WalletEntity> call() throws Exception {
                return WalletDao.getHDParentWalletList();
            }
        }) .flatMap(new Function<List<WalletEntity>, Publisher<Wallet>>() {
            @Override
            public Publisher<Wallet> apply(List<WalletEntity> walletEntities) throws Exception {
                return Flowable.range(0, walletEntities.size()).map(new Function<Integer, Wallet>() {
                    @Override
                    public Wallet apply(Integer integer) throws Exception {
                        return walletEntities.get(integer).buildWallet();
                    }
                });
            }
        }).toList()
          .toFlowable();

        //合并
        return Flowable.zip(flowable1, flowable2, new BiFunction<List<Wallet>, List<Wallet>, List<Wallet>>() {
            @Override
            public List<Wallet> apply(List<Wallet> walletList, List<Wallet> parentWalletList) throws Exception {

                //为子钱包集合赋值母钱包名称
                for (int i = 0; i < walletList.size(); i++) {
                    String subWalletParentId = walletList.get(i).getParentId();

                    for (Wallet parentWallet : parentWalletList) {
                         if(parentWallet.getUuid().equals(subWalletParentId)){
                             walletList.get(i).setParentWalletName(parentWallet.getName());
                             break;
                         }
                    }
                }
                return walletList;
            }
        }).blockingFirst();
    }




    public void addWallet(Wallet wallet) {
        Wallet cloneWallet = wallet.clone();
        if (!mWalletList.contains(wallet)) {//首页钱包list中不存在

            if (!isExistSelectedWallet()) {
                cloneWallet.setSelectedIndex(WalletSelectedIndex.SELECTED);
                cloneWallet.setShow(true);
                //检查是否存在同一组钱包
                boolean isSameParentIdWallet = false;
                for(int i = 0; i < mWalletList.size(); i++) {

                    if(cloneWallet.isHD() && mWalletList.get(i).getParentId() != null && mWalletList.get(i).getParentId().equals(cloneWallet.getParentId())){

                        //更新DB中HD分组钱包首页是否显示状态
                        updateSubWalletIsShowByUuid(mWalletList.get(i),cloneWallet);
                        //更新cache
                        mWalletList.remove(i);
                        mWalletList.add(i,cloneWallet);
                        isSameParentIdWallet = true;
                        break;
                    }
                }

                if(!isSameParentIdWallet){
                    mWalletList.add(cloneWallet);
                }
            }
        }else{//首页钱包list中存在

            if (!isExistSelectedWallet()) {
                cloneWallet.setSelectedIndex(WalletSelectedIndex.SELECTED);
                cloneWallet.setShow(true);
                for (int i = 0; i < mWalletList.size(); i++) {
                     if(cloneWallet.getUuid().equals(mWalletList.get(i).getUuid())){
                         mWalletList.remove(i);
                         mWalletList.add(i,cloneWallet);
                         break;
                     }
                }
            }
        }

        EventPublisher.getInstance().sendWalletNumberChangeEvent();
        EventPublisher.getInstance().sendWalletSelectedChangedEvent();
        EventPublisher.getInstance().sendOpenRightSidebarEvent(null,WalletTypeSearch.UNKNOWN_WALLET);


    }

    public void updateAccountBalance(AccountBalance accountBalance) {
        if (mWalletList.isEmpty() || accountBalance == null) {
            return;
        }

        int position = getPositionByAddress(accountBalance.getPrefixAddress());

        if (position == -1) {
            return;
        }

        //更新缓存钱包金额
        mWalletList.get(position).setAccountBalance(accountBalance);
        /*  Wallet wallet = mWalletList.get(position);
            wallet.setAccountBalance(accountBalance);*/
    }

    /**
     * 更新HD分层钱包组，钱包是否首页显示状态
     * @param oldWallet
     * @param newWallet
     * @return
     */
    public boolean updateSubWalletIsShowByUuid(Wallet oldWallet, Wallet newWallet) {

        return Flowable
                    .fromCallable(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return WalletDao.updateSubWalletIsShowByUuid(oldWallet.getUuid(),false);
                        }
                    }).filter(new Predicate<Boolean>() {
                        @Override
                        public boolean test(Boolean aBoolean) throws Exception {
                            return aBoolean;
                        }
                    }).map(new Function<Boolean, Boolean>() {
                        @Override
                        public Boolean apply(Boolean aBoolean) throws Exception {
                            return WalletDao.updateSubWalletIsShowByUuid(newWallet.getUuid(),true);
                        }
                    })
                    .blockingFirst();
    }

    /**
     * 更新DB选中钱包，根据uuid
     * @param uuid
     * @return
     */
    public Boolean updateDBSelectedWalletByUuid(String uuid) {

        return Flowable
                .fromCallable(new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        return WalletDao.resetAllWalletSelectedIndex();
                    }
                }).filter(new Predicate<Boolean>(){

                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                }).map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {

                        return WalletDao.updateSubWalletSelectedIndexByUuid(uuid, WalletSelectedIndex.SELECTED);
                    }
                }).blockingFirst();
    }


    /**
     * 更新钱包排序顺序，根据uuid
     * @param wallet
     * @param sortIndex
     * @return
     */
    public Boolean updateDBWalletSortIndexByUuid(Wallet wallet, int sortIndex) {


        WalletDao.updateWalletSortIndexWithUuid(wallet.getUuid(),sortIndex);
        if(wallet.isHD() && wallet.getDepth() == 0){//此钱包为母钱包，同步更新旗下子钱包sortIndex
            WalletDao.updateBatchWalletSortIndexWithParentId(wallet.getUuid(),sortIndex);
        }
        return true;

      /*  return Flowable
                .fromCallable(new Callable<Boolean>() {

                    @Override
                    public Boolean call() throws Exception {
                        return WalletDao.updateWalletSortIndexWithUuid(wallet.getUuid(),sortIndex);
                    }
                }).filter(new Predicate<Boolean>(){

                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return wallet.isHD() && wallet.getDepth() == 0;//判断是否是HD分组钱包母钱包，如果是同步更新旗下子钱包一样的sortIndex
                    }
                }).map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {

                        return WalletDao.updateBatchWalletSortIndexWithParentId(wallet.getUuid(),sortIndex);
                    }
                }).blockingFirst();*/
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


    /**
     * 查询DB中所有钱包地址(用于交易记录)
     * @return
     */
    public List<String> getAddressListFromDB(){

        return   Flowable.fromCallable(new Callable<List<WalletEntity>>() {

                        @Override
                        public List<WalletEntity> call() throws Exception {
                            return WalletDao.getAllWalletList();
                        }
                    }).map(new Function<List<WalletEntity>, List<String>>() {

                        @Override
                        public List<String> apply(List<WalletEntity> walletEntities) throws Exception {
                            List<String> addressList = new ArrayList<>();
                            if(walletEntities.size() > 0){
                                for (int i = 0; i < walletEntities.size(); i++) {
                                    String address = walletEntities.get(i).buildWallet().getPrefixAddress();
                                    addressList.add(address);
                                }
                            }
                            return addressList;
                        }
                    }).blockingFirst();
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



    /**
     * 根据钱包地址获取钱包名称(先查缓存，再查DB)
     * @param address
     * @return
     */
    public Single<String> getWalletNameFromAddress(String address) {
        return Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {

                String walletName = WalletManager.getInstance().getWalletNameByWalletAddress(address);
                return TextUtils.isEmpty(walletName) ? walletName : String.format("%s(%s)", walletName, AddressFormatUtil.formatTransactionAddress(address));
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).switchIfEmpty(new SingleSource<String>() {
            @Override
            public void subscribe(SingleObserver<? super String> observer) {
                String addressName = WalletDao.getWalletNameByAddress(address);
                //String addressName = AddressDao.getAddressNameByAddress(address);
                observer.onSuccess(TextUtils.isEmpty(addressName) ? addressName : String.format("%s(%s)", addressName, AddressFormatUtil.formatTransactionAddress(address)));
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).defaultIfEmpty(address)
                .toSingle();
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
     * 根据钱包地址获取钱包名称(从缓存中获取)
     * @param walletAddress
     * @return
     */
    public String getWalletNameByWalletAddress(String walletAddress) {
        boolean isCacheExists = false;
        if (!mWalletList.isEmpty()) {
            for (Wallet walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equalsIgnoreCase(walletEntity.getPrefixAddress())) {
                    isCacheExists = true;
                    return walletEntity.getName();
                }
            }
        }

        if(!isCacheExists){
            String address = WalletDao.getWalletNameByAddress(walletAddress);;
            return TextUtils.isEmpty(address) ? "" : address;
        }
        return "";
    }

    /**
     * 获取钱包头像
     */
    public String getWalletIconByWalletAddress(String walletAddress) {

        boolean isCacheExists = false;
        if (!mWalletList.isEmpty()) {
            for (Wallet walletEntity : mWalletList) {
                if (!TextUtils.isEmpty(walletAddress) && walletAddress.equalsIgnoreCase(walletEntity.getPrefixAddress())) {
                    isCacheExists = true;
                    return walletEntity.getAvatar();
                }
            }
        }

        if(!isCacheExists){
           return WalletDao.getWalletAvatarByAddress(walletAddress);
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
                        return wallet.getSelectedIndex() == WalletSelectedIndex.SELECTED;
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


    public Single<List<Wallet>> createWalletList(String name, String password){

        return createMnemonic()
                 .flatMap(new Function<String, SingleSource<? extends List<Wallet>>>() {
                     @Override
                     public SingleSource<? extends List<Wallet>> apply(String mnemonic) throws Exception {
                         return importMnemonicWalletList(mnemonic, name, password);
                     }
                 });
    }

    public int importKeystore(String store, String name, String password) {

        try {
            //兼容老keyStore，进行转换
            JSONObject keystoreJSON = JSON.parseObject(store);
            if (keystoreJSON.containsKey("address")) {
                Object addressObj = keystoreJSON.get("address");
                if(addressObj instanceof String){
                    String address = keystoreJSON.getString("address");
                    AddressBech32 addressBech32 = AddressManager.getInstance().executeEncodeAddress(address);
                    keystoreJSON.remove("address");
                    keystoreJSON.put("address", addressBech32);
                    store = keystoreJSON.toString();
                }
            }
            if (!JZWalletUtil.isValidKeystore(store)) {
                return CODE_ERROR_KEYSTORE;
            }
            if (TextUtils.isEmpty(name)) {
                return CODE_ERROR_NAME;
            }
            if (TextUtils.isEmpty(password)) {
                return CODE_ERROR_PASSWORD;
            }

            Wallet entity = WalletServiceImpl.getInstance().importKeystore(store, name, password);
            if (entity == null) {
                return CODE_ERROR_PASSWORD;
            }
            if(isWalletAddressExists(entity.getPrefixAddress().toLowerCase())){
                return CODE_ERROR_WALLET_EXISTS;
            }
            entity.setBackedUp(true);
            entity.setMnemonic("");
            entity.setChainId(NodeManager.getInstance().getChainId());
            addAndSelectedWalletStatusNotice(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
            return CODE_OK;
        } catch (Exception exp) {
            LogUtils.d(exp.getMessage(),exp.fillInStackTrace());
            return CODE_ERROR_KEYSTORE;
        }
    }

    public int importWalletAddress(String walletAddress) {
        if (!JZWalletUtil.isValidAddress(walletAddress)) {
            return CODE_ERROR_INVALIA_ADDRESS;
        }

        //转换地址
        String originalAddress = Bech32.addressDecodeHex(walletAddress);
        AddressBech32 addressBech32 = AddressManager.getInstance().executeEncodeAddress(originalAddress);
        Bech32Address bech32Address = new Bech32Address(addressBech32.getMainnet(),addressBech32.getTestnet());

        Wallet mWallet = new Wallet();
        mWallet.setAddress(originalAddress);
        mWallet.setBech32Address(bech32Address);
        mWallet.setUuid(UUID.randomUUID().toString());
        mWallet.setAvatar(WalletServiceImpl.getInstance().getWalletAvatar(WalletType.ORDINARY_WALLET));

        if(isWalletAddressExists(mWallet.getPrefixAddress().toLowerCase())){
            return CODE_ERROR_WALLET_EXISTS;
        }

       /* for (Wallet param : mWalletList) {
            if (param.getOriginalAddress().toLowerCase().equalsIgnoreCase(mWallet.getOriginalAddress().toLowerCase())) {
                return CODE_ERROR_WALLET_EXISTS;
            }
        }*/
        mWallet.setBackedUp(true);
        mWallet.setChainId(NodeManager.getInstance().getChainId());
        mWallet.setCreateTime(System.currentTimeMillis());
        mWallet.setName(String.format("%s%d", "Wallet", PreferenceTool.getInt(NodeManager.getInstance().getChainId(), 1)));
        addAndSelectedWalletStatusNotice(mWallet);
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
            if(isWalletAddressExists(entity.getPrefixAddress().toLowerCase())){
                return CODE_ERROR_WALLET_EXISTS;
            }
            entity.setBackedUp(true);
            entity.setMnemonic("");
            entity.setChainId(NodeManager.getInstance().getChainId());
            addAndSelectedWalletStatusNotice(entity);
            WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
            return CODE_OK;
        } catch (Exception exp) {
            return CODE_ERROR_UNKNOW;
        }
    }


    public Single<Wallet> importMnemonicGenerateWallet(String mnemonic, String name, String password, int... index) {
        return Single.create(new SingleOnSubscribe<Wallet>() {
            @Override
            public void subscribe(SingleEmitter<Wallet> emitter) throws Exception {
                Wallet walletEntity = WalletServiceImpl.getInstance().importMnemonic(mnemonic, name, password,index);
                if (walletEntity == null) {
                    emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED));
                } else {
                    walletEntity.setMnemonic(JZWalletUtil.encryptMnemonic(walletEntity.getKey(), mnemonic, password));
                    walletEntity.setChainId(NodeManager.getInstance().getChainId());
                    emitter.onSuccess(walletEntity);
                }
            }
        });
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

    private Single<List<Wallet>> importMnemonicWalletList(String mnemonic, String name, String password){
         return Single.create(new SingleOnSubscribe<List<Wallet>>() {
             @Override
             public void subscribe(SingleEmitter<List<Wallet>> emitter) throws Exception {
                 List<Wallet> walletEntitys = WalletServiceImpl.getInstance().importMnemonicWalletList(mnemonic, name, password);

                 if (walletEntitys == null || isWalletAddressExists(walletEntitys.get(0).getPrefixAddress().toLowerCase())
                     || isWalletAddressExists(walletEntitys.get(1).getPrefixAddress().toLowerCase())) {

                     emitter.onError(new CustomThrowable(CustomThrowable.CODE_ERROR_CREATE_WALLET_FAILED));
                 } else {
                     for (int i = 0; i < walletEntitys.size(); i++) {
                          if(i == 0){
                              walletEntitys.get(i).setMnemonic(JZWalletUtil.encryptMnemonic(walletEntitys.get(i).getKey(), mnemonic, password));
                          }
                         walletEntitys.get(i).setChainId(NodeManager.getInstance().getChainId());
                     }
                     emitter.onSuccess(walletEntitys);
                 }
             }
         });

    }

    public int importMnemonic(String mnemonic, String name, String password, @WalletType int walletType) {
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
            if(walletType == WalletType.ORDINARY_WALLET){//普通钱包

                Wallet entity = WalletServiceImpl.getInstance().importMnemonic(mnemonic, name, password);
                if (entity == null) {
                    return CODE_ERROR_PASSWORD;
                }
                if(isWalletAddressExists(entity.getPrefixAddress().toLowerCase())){
                    return CODE_ERROR_WALLET_EXISTS;
                }
                entity.setBackedUp(true);
                //entity.setMnemonic(JZWalletUtil.encryptMnemonic(entity.getKey(), mnemonic, password));//导入助记词，不需要保存助记词
                entity.setChainId(NodeManager.getInstance().getChainId());
                addAndSelectedWalletStatusNotice(entity);
                WalletDao.insertWalletInfo(entity.buildWalletInfoEntity());
            }else{//HD钱包

                List<Wallet> entityList = WalletServiceImpl.getInstance().importMnemonicWalletList(mnemonic, name, password);
                if (entityList == null) {
                    return CODE_ERROR_PASSWORD;
                }

                if(isWalletAddressExists(entityList.get(0).getPrefixAddress().toLowerCase())
                   || isWalletAddressExists(entityList.get(1).getPrefixAddress().toLowerCase())){
                    return CODE_ERROR_WALLET_EXISTS;
                }
                List<WalletEntity> walletEntitieList = new ArrayList<>();
                for (int i = 0; i < entityList.size(); i++) {
                     if(i == 0){
                         entityList.get(i).setBackedUp(true);
                         entityList.get(i).setMnemonic(JZWalletUtil.encryptMnemonic(entityList.get(i).getKey(), mnemonic, password));
                     }
                    entityList.get(i).setChainId(NodeManager.getInstance().getChainId());
                    walletEntitieList.add(entityList.get(i).buildWalletInfoEntity());
                }
                addAndSelectedWalletStatusNotice(entityList.get(1));
                //addWallet(entityList.get(1));
                WalletDao.insertWalletInfoList(walletEntitieList);
            }


            PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG, false);
            return CODE_OK;
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
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


    public boolean updateBackedUpWithUuid(Wallet wallet, boolean backedUp) {
        if (TextUtils.isEmpty(wallet.getUuid())) {
            return false;
        }
        for (Wallet walletEntity : mWalletList) {
            if (wallet.getUuid().equals(walletEntity.getUuid())) {
                walletEntity.setBackedUp(backedUp);
                break;
            }
        }
        if(wallet.isHD() && wallet.getDepth() == 1){//子钱包
            Wallet rootWallet = WalletManager.getInstance().getWalletInfoByUuid(wallet.getParentId());
            return WalletDao.updateBackedUpWithUuid(rootWallet.getUuid(), backedUp);
        }else{
            return WalletDao.updateBackedUpWithUuid(wallet.getUuid(), backedUp);
        }

    }

    public void updateWalletBackedUpPromptWithUUID(String uuid, boolean isBackedUp) {

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
                        Wallet wallet = mWalletList.get(integer);
                        mWalletList.get(integer).setBackedUp(isBackedUp);
                    }
                })
                .subscribe();
    }

    public Wallet getWalletByAddress(String address) {

        boolean isCacheExists = false;
        if (TextUtils.isEmpty(address)) {
            return null;
        }

        for (Wallet walletEntity : mWalletList) {
            if (walletEntity.getPrefixAddress().toLowerCase().contains(address.toLowerCase())) {

                if(walletEntity.isHD() && walletEntity.getDepth() == WalletDepth.DEPTH_ONE){
                    Wallet rootWallet = WalletManager.getInstance().getWalletInfoByUuid(walletEntity.getParentId());
                    walletEntity.setMnemonic(rootWallet.getMnemonic());
                    walletEntity.setKey(rootWallet.getKey());
                    isCacheExists = true;
                }
                return walletEntity;
            }
        }

        if(!isCacheExists){

            Wallet wallet = WalletManager.getInstance().getWalletInfoByAddress(address);
            if(wallet.isHD() && wallet.getDepth() == WalletDepth.DEPTH_ONE){
                Wallet rootWallet = WalletManager.getInstance().getWalletInfoByUuid(wallet.getParentId());
                wallet.setMnemonic(rootWallet.getMnemonic());
                wallet.setKey(rootWallet.getKey());
            }
            return wallet;
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
            mWalletList.get(0).setSelectedIndex(WalletSelectedIndex.SELECTED);
            EventPublisher.getInstance().sendWalletSelectedChangedEvent();
        }

        if(wallet.isHD() && wallet.getDepth() == WalletDepth.DEPTH_ONE){//子钱包删除

           return Flowable
                        .fromCallable(new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return WalletDao.deleteWalletInfo(wallet.getUuid());
                            }
                        }).map(new Function<Boolean, List<WalletEntity>>() {
                        @Override
                        public List<WalletEntity> apply(Boolean aBoolean) throws Exception {
                            return WalletDao.getHDWalletListByParentId(wallet.getParentId());
                        }
                    }).map(new Function<List<WalletEntity>, Boolean>() {
                        @Override
                        public Boolean apply(List<WalletEntity> walletEntities) throws Exception {
                            if(walletEntities.size() == 0){
                                return WalletDao.deleteWalletInfo(wallet.getParentId());
                            }else{
                                //删除子钱包当前首页显示的钱包后，默认将子钱包组第一个设置首页显示
                                WalletManager.getInstance().addAndSelectedWalletStatusNotice(walletEntities.get(0).buildWallet());
                                return true;
                            }
                        }
                    }).blockingFirst();

        }else{//删除普通钱包
            return WalletDao.deleteWalletInfo(wallet.getUuid());
        }
    }


    public boolean deleteBatchWallet(Wallet rootWallet) {

        //删除缓存中子钱包
        for (Wallet walletEntity : mWalletList) {
            if (rootWallet.getUuid().equals(walletEntity.getParentId())) {
                mWalletList.remove(walletEntity);
                break;
            }
        }
        if (!isExistSelectedWallet() && !mWalletList.isEmpty()) {
            mWalletList.get(0).setSelectedIndex(WalletSelectedIndex.SELECTED);
            EventPublisher.getInstance().sendWalletSelectedChangedEvent();
        }

        //删除DB数据
       return Flowable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                //删除母钱包
                return WalletDao.deleteWalletInfo(rootWallet.getUuid());
            }
        }).filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        }).map(new Function<Boolean, List<WalletEntity>>() {
           @Override
           public List<WalletEntity> apply(Boolean aBoolean) throws Exception {
               return WalletDao.getHDWalletListByParentId(rootWallet.getUuid());
           }
       }).flatMap(new Function<List<WalletEntity>, Publisher<WalletEntity>>() {
           @Override
           public Publisher<WalletEntity> apply(List<WalletEntity> walletEntities) throws Exception {
               return Flowable.fromIterable(walletEntities);
           }
       }).map(new Function<WalletEntity, Boolean>() {

           @Override
           public Boolean apply(WalletEntity wallet) throws Exception {
               //删除所有子钱包
               return WalletDao.deleteWalletInfo(wallet.getUuid());
           }
       }).toList()
         .map(new Function<List<Boolean>, Boolean>() {
             @Override
             public Boolean apply(List<Boolean> booleanList) throws Exception {
                 boolean isDeleteSuccess = true;
                 for(int i = 0; i < booleanList.size(); i++) {
                     if(!booleanList.get(i)){
                         isDeleteSuccess = false;
                         break;
                     }
                 }
                 return isDeleteSuccess;
             }
         }).blockingGet();
    }


    public boolean isValidWallet(Wallet walletEntity, String password) {
        try {
            return JZWalletUtil.decrypt(walletEntity.getKey(), password) != null;
        } catch (Exception exp) {
            LogUtils.e(exp.getMessage(),exp.fillInStackTrace());
            return false;
        }
    }




    public boolean isWalletNameExistsFromDB(String walletName) {

        return Flowable.fromCallable(new Callable<WalletEntity>() {
            @Override
            public WalletEntity call() throws Exception {
                return WalletDao.getWalletByName(walletName);
            }
        }).map(new Function<WalletEntity, Boolean>() {
            @Override
            public Boolean apply(WalletEntity walletEntity) throws Exception {
                if(!TextUtils.isEmpty(walletEntity.getName())){
                    return walletEntity.getName().equals(walletName);
                }else{
                    return false;
                }
            }
        }).filter(new Predicate<Boolean>() {
            @Override
            public boolean test(Boolean aBoolean) throws Exception {
                return aBoolean;
            }
        }).firstElement()
                .defaultIfEmpty(false)
                .blockingGet();
    }

    public boolean isWalletNameExists(String walletName) {

        return isWalletNameExistsFromDB(walletName);


     /* if (mWalletList == null || mWalletList.isEmpty()) {
            return false;
        }
        return Flowable
                .fromIterable(mWalletList)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getName().equals(walletName);
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
                .blockingGet();*/
    }

    public boolean isWalletAddressExists(String prefixAddress) {

        boolean isCacheExists = false;
        if (TextUtils.isEmpty(prefixAddress)) {
            return false;
        }

        if (mWalletList == null || mWalletList.isEmpty()) {
            return false;
        }

        for (Wallet walletEntity : mWalletList) {
            if (walletEntity.getPrefixAddress().toLowerCase().equals(prefixAddress.toLowerCase())) {
                isCacheExists = true;
                return true;
            }
        }

        if(!isCacheExists){
            Wallet wallet = WalletManager.getInstance().getWalletInfoByAddress(prefixAddress);
           if(wallet != null && (wallet.getPrefixAddress() != null && !wallet.getPrefixAddress().equals("")) &&
              wallet.getPrefixAddress().equals(prefixAddress)){

               return true;
           }
        }
        return false;


/*
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
                .blockingGet();*/

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
        if (mWalletList == null || mWalletList.isEmpty()) {
            return Observable.just(BigDecimal.ZERO);
        }
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
