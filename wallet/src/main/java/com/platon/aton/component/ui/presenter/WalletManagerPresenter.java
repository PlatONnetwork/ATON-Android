package com.platon.aton.component.ui.presenter;

import com.platon.aton.component.ui.contract.WalletManagerContract;
import com.platon.aton.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.platon.aton.component.ui.view.BackupMnemonicPhraseActivity;
import com.platon.aton.component.ui.view.ManageWalletActivity;
import com.platon.aton.db.sqlite.WalletDao;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Wallet;
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
            List<Wallet> walletList = WalletManager.getInstance().getWalletList();
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
                    long updateTime = System.currentTimeMillis();
                    for (int i = 0; i < mWalletList.size(); i++) {
                        Wallet walletEntity = mWalletList.get(i);
                        updateTime += 10;
                        walletEntity.setUpdateTime(updateTime);
                        WalletDao.updateUpdateTimeWithUuid(walletEntity.getUuid(), updateTime);
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
        Wallet walletEntity = mWalletList.get(position);
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, walletEntity, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public void startAction(int position) {
        ManageWalletActivity.actionStart(currentActivity(), mWalletList.get(position));
    }

    @Override
    public ArrayList<Wallet> getDataSource() {
        return mWalletList;
    }
}
