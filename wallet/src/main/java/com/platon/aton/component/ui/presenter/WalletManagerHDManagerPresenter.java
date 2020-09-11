package com.platon.aton.component.ui.presenter;

import com.platon.aton.app.LoadingTransformer;
import com.platon.aton.component.ui.contract.WalletManagerHDManagerContract;
import com.platon.aton.component.ui.view.ManageWalletActivity;
import com.platon.aton.db.entity.WalletEntity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.aton.event.EventPublisher;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.network.SchedulersTransformer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class WalletManagerHDManagerPresenter extends BasePresenter<WalletManagerHDManagerContract.View> implements WalletManagerHDManagerContract.Presenter {

    private ArrayList<Wallet> mWalletList = new ArrayList<>();

    @Override
    public void fetchHDWalletList(String parentId) {
        if (isViewAttached()) {
            List<Wallet> walletList = WalletManager.getInstance().getHDWalletListByParentId(parentId);
            if (!mWalletList.isEmpty()) {
                mWalletList.clear();
            }
            if (!walletList.isEmpty()) {
                mWalletList.addAll(walletList);
            }
          /*  if (mWalletList.isEmpty()) {
                PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG,true);
                PreferenceTool.putBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false);
                getView().showEmpty();
                return;
            }*/
            getView().showWalletList();
            getView().notifyWalletListChanged();
        }

    }

    @Override
    public void sortWalletList() {
        if (!mWalletList.isEmpty()) {
            Observable.fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    long updateTime = System.currentTimeMillis();
                    for (int i = 0; i < mWalletList.size(); i++) {
                        Wallet walletEntity = mWalletList.get(i);
                        updateTime += 10;
                        walletEntity.setUpdateTime(System.currentTimeMillis());
                        WalletDao.updateUpdateTimeWithUuid(walletEntity.getUuid(), updateTime);
                    }
                    return true;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aVoid) throws Exception {
                    EventPublisher.getInstance().sendWalletListOrderChangedEvent();
                }
            });
        }
    }



    @Override
    public void startAction(int position) {
        ManageWalletActivity.actionStart(currentActivity(), mWalletList.get(position));
    }

    @Override
    public void modifyName(String name,String uuid) {

        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return WalletDao.updateNameWithUuid(uuid, name);
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success;
                    }
                })
            /*    .map(new Function<Boolean, Boolean>() {
                    @Override
                    public Boolean apply(Boolean aBoolean) throws Exception {
                        return updateWalletName(uuid,name);
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean success) throws Exception {
                        return success;
                    }
                })*/
                .toSingle()
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().showWalletName(name);
                            //EventPublisher.getInstance().sendWalletNumberChangeEvent();
                        }
                    }
                });


    }

    @Override
    public boolean isExists(String walletName,String uuid) {

       return Flowable.fromCallable(new Callable<WalletEntity>() {
            @Override
            public WalletEntity call() throws Exception {
                return WalletDao.getWalletByUuid(uuid);
            }
        }).map(new Function<WalletEntity, Boolean>() {
            @Override
            public Boolean apply(WalletEntity walletEntity) throws Exception {
                return walletEntity.getName().toLowerCase().equals(walletName);
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

    @Override
    public void deleteHDWallet(Wallet rootWallet) {

       if(WalletManager.getInstance().deleteBatchWallet(rootWallet)){

           EventPublisher.getInstance().sendWalletNumberChangeEvent();
           EventPublisher.getInstance().sendWalletSelectedChangedEvent();
           EventPublisher.getInstance().sendOpenRightSidebarEvent(null, WalletTypeSearch.UNKNOWN_WALLET);
           getView().currentActivity().finish();
       }
    }

    @Override
    public ArrayList<Wallet> getDataSource() {
        return mWalletList;
    }


    public boolean updateWalletName(String uuid, String newName) {
        for (Wallet walletEntity : getDataSource()) {
            if (uuid.equals(walletEntity.getUuid())) {
                walletEntity.setName(newName);
                return true;
            }
        }
        return false;
    }
}
