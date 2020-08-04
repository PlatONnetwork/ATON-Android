package com.platon.aton.component.ui.presenter;

import com.platon.aton.component.ui.contract.WalletManagerContract;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.view.BackupMnemonicPhraseActivity;
import com.platon.aton.component.ui.view.ManageWalletActivity;
import com.platon.aton.component.ui.view.WalletManagerHDManagerActivity;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.InputWalletPasswordFromType;
import com.platon.aton.entity.Wallet;
import com.platon.aton.event.EventPublisher;
import com.platon.framework.app.Constants;
import com.platon.framework.base.BasePresenter;
import com.platon.framework.utils.PreferenceTool;

import org.web3j.crypto.Credentials;

import java.util.ArrayList;
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

    private ArrayList<Wallet> mWalletList = new ArrayList<>();

    @Override
    public void fetchWalletList() {
        if (isViewAttached()) {
            List<Wallet> walletList = WalletManager.getInstance().getWalletListByOrdinaryAndHD();
            if (!mWalletList.isEmpty()) {
                mWalletList.clear();
            }
            if (!walletList.isEmpty()) {
                mWalletList.addAll(walletList);
            }
            if (mWalletList.isEmpty()) {
                PreferenceTool.putBoolean(Constants.Preference.KEY_OPERATE_MENU_FLAG,true);
                PreferenceTool.putBoolean(Constants.Preference.KEY_FACE_TOUCH_ID_FLAG, false);
                getView().showEmpty();
                return;
            }

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

                    List<Wallet> assetsWalletList = WalletManager.getInstance().getWalletList();
                    for (int i = 0; i < mWalletList.size(); i++) {
                        Wallet walletEntity = mWalletList.get(i);
                        int sortIndex = mWalletList.size() - i;
                        walletEntity.setSelectedIndex(sortIndex);

                        //更新DB
                        WalletManager.getInstance().updateDBWalletSortIndexByUuid(walletEntity,sortIndex);

                        //更新缓存,首页钱包排序排序索引(sortIndex)
                        for (int j = 0; j < assetsWalletList.size() ; j++) {
                             Wallet assetWallet =  assetsWalletList.get(j);
                             if(!walletEntity.isHD() && walletEntity.getUuid().equals(assetWallet.getUuid())){//普通钱包
                                 WalletManager.getInstance().getWalletList().get(j).setSortIndex(sortIndex);
                                 break;
                             }else if(walletEntity.isHD() && walletEntity.getUuid().equals(assetWallet.getParentId())){//HD分组钱包，则对应更新旗下子钱包
                                 WalletManager.getInstance().getWalletList().get(j).setSortIndex(sortIndex);
                                 break;
                             }
                        }
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
    public void backupWallet(int position) {
        Wallet walletEntity = mWalletList.get(position);
        InputWalletPasswordDialogFragment.newInstance(walletEntity, InputWalletPasswordFromType.BACKUPS).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password, Wallet wallet) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, wallet, BackupMnemonicPhraseActivity.BackupMnemonicExport.MAIN_ACTIVITY);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void startAction(int position) {
        Wallet wallet = mWalletList.get(position);
        if(wallet.isHD()){
            WalletManagerHDManagerActivity.actionStart(currentActivity(), wallet);
        }else{
            ManageWalletActivity.actionStart(currentActivity(), wallet);
        }

    }

    @Override
    public ArrayList<Wallet> getDataSource() {
        return mWalletList;
    }
}
