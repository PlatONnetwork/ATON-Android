package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.WalletManagerContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.view.BackupMnemonicPhraseActivity;
import com.juzix.wallet.component.ui.view.ManageIndividualWalletActivity;
import com.juzix.wallet.component.ui.view.ManageSharedWalletActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.sqlite.IndividualWalletInfoDao;
import com.juzix.wallet.db.sqlite.SharedWalletInfoDao;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.WalletEntity;

import org.web3j.crypto.Credentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class WalletManagerPresenter extends BasePresenter<WalletManagerContract.View> implements WalletManagerContract.Presenter {

    private final static String TAG = WalletManagerPresenter.class.getSimpleName();

    private ArrayList<WalletEntity> mWalletList = new ArrayList<>();

    public WalletManagerPresenter(WalletManagerContract.View view) {
        super(view);
    }

    @Override
    public void fetchWalletList() {
        if (isViewAttached()) {
            List<IndividualWalletEntity> walletList1 = IndividualWalletManager.getInstance().getWalletList();
            List<SharedWalletEntity>     walletList2 = SharedWalletManager.getInstance().getWalletList();
            if (!mWalletList.isEmpty()){
                mWalletList.clear();
            }
            if (!walletList1.isEmpty()){
                mWalletList.addAll(walletList1);
            }
            if (!walletList2.isEmpty()){
                mWalletList.addAll(walletList2);
            }
            if (mWalletList.isEmpty()){
                AppSettings.getInstance().setOperateMenuFlag(true);
                AppSettings.getInstance().setFaceTouchIdFlag(false);
                getView().showEmpty();
                return;
            }
            Collections.sort(mWalletList, new Comparator<WalletEntity>() {
                @Override
                public int compare(WalletEntity o1, WalletEntity o2) {
                    return Long.compare(o1.getUpdateTime(),  o2.getUpdateTime());
                }
            });
            getView().showWalletList();
            getView().notifyWalletListChanged();
        }

    }

    @Override
    public void sortWalletList() {
        if (!mWalletList.isEmpty()){
            Observable.fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    long updateTime = System.currentTimeMillis();
                    for (int i = 0; i < mWalletList.size(); i++){
                        WalletEntity walletEntity = mWalletList.get(i);
                        updateTime += 10;
                        walletEntity.setUpdateTime(updateTime);
                        if (walletEntity instanceof IndividualWalletEntity){
                            IndividualWalletInfoDao.updateUpdateTimeWithUuid(walletEntity.getUuid(), updateTime);
                        }else {
                            SharedWalletInfoDao.updateUpdateTimeWithUuid(walletEntity.getUuid(), updateTime);
                        }
                    }
                    return null;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aVoid) throws Exception {

                }
            });
        }
    }

    @Override
    public void backupWallet(int position) {
        IndividualWalletEntity walletEntity = (IndividualWalletEntity) mWalletList.get(position);
        InputWalletPasswordDialogFragment.newInstance((IndividualWalletEntity) walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, (IndividualWalletEntity) walletEntity, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void startAction(int position) {
       WalletEntity entity = mWalletList.get(position);
       if (entity instanceof IndividualWalletEntity){
           ManageIndividualWalletActivity.actionStart(currentActivity(), (IndividualWalletEntity) entity);
       }else {
           ManageSharedWalletActivity.actionStart(currentActivity(), (SharedWalletEntity) entity);
       }
    }

    @Override
    public ArrayList<WalletEntity> getDataSource() {
        return mWalletList;
    }
}
