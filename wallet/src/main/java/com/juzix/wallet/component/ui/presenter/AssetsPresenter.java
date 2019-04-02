package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.AssetsContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.view.AddSharedWalletActivity;
import com.juzix.wallet.component.ui.view.BackupMnemonicPhraseActivity;
import com.juzix.wallet.component.ui.view.CreateIndividualWalletActivity;
import com.juzix.wallet.component.ui.view.CreateSharedWalletActivity;
import com.juzix.wallet.component.ui.view.ImportIndividualWalletActivity;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.trello.rxlifecycle2.android.FragmentEvent;

import org.reactivestreams.Publisher;
import org.web3j.crypto.Credentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class AssetsPresenter extends BasePresenter<AssetsContract.View> implements AssetsContract.Presenter{

    private ArrayList<WalletEntity> mWalletList = new ArrayList<>();
    private WalletEntity            mSelectedWallet;
    private Disposable              mDisposable;
    private static final int        REFRESH_TIME = 5000;
    private double mBalance;

    public AssetsPresenter(AssetsContract.View view) {
        super(view);
    }

    @Override
    public void start() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mDisposable = Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() {
                refreshWalletList();
                return mBalance;
            }
        })
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(new SchedulersTransformer())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(REFRESH_TIME, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        show();
                    }
                });
    }

    @Override
    public void fetchWalletList(){
        if (!isViewAttached()){
            return;
        }
        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() {
                refreshWalletList();
                return mBalance;
            }
        })
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        show();
                    }
                });
    }

    @Override
    public ArrayList<WalletEntity> getRecycleViewDataSource() {
        return mWalletList;
    }

    @Override
    public void clickRecycleViewItem(WalletEntity walletEntity) {
        mSelectedWallet = walletEntity;
        getView().showWalletList(mSelectedWallet);
        getView().showWalletInfo(mSelectedWallet);
        getView().showWalletTab(mSelectedWallet, 0);
    }

    @Override
    public void scanQRCode() {
        final BaseActivity activity = currentActivity();
        requestPermission(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                Intent intent = new Intent(currentActivity(), ScanQRCodeActivity.class);
                activity.startActivityForResult(intent, MainActivity.REQ_ASSETS_TAB_QR_CODE);
            }

            @Override
            public void onHasPermission(int what) {
                Intent intent = new Intent(currentActivity(), ScanQRCodeActivity.class);
                activity.startActivityForResult(intent, MainActivity.REQ_ASSETS_TAB_QR_CODE);
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {

            }
        }, Manifest.permission.CAMERA);
    }

    @Override
    public void createIndividualWallet() {
        CreateIndividualWalletActivity.actionStart(getContext());
    }

    @Override
    public void createSharedWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.noWalletTips);
            return;
        }
        double totalBalance = 0.0D;
        for (IndividualWalletEntity walletEntity : walletEntityList) {
            totalBalance = BigDecimalUtil.add(totalBalance, walletEntity.getBalance());
        }
        if (totalBalance <= 0){
            showLongToast(R.string.insufficientBalanceTips);
            return;
        }
        CreateSharedWalletActivity.actionStart(getContext());
    }

    @Override
    public void importIndividualWallet() {
        ImportIndividualWalletActivity.actionStart(getContext());
    }

    @Override
    public void addSharedWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()){
            showLongToast(R.string.noWalletTips);
            return;
        }
        AddSharedWalletActivity.actionStart(getContext());
    }

    @Override
    public void backupWallet() {
        InputWalletPasswordDialogFragment.newInstance((IndividualWalletEntity) mSelectedWallet).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, (IndividualWalletEntity) mSelectedWallet, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public boolean needBackup(WalletEntity walletEntity) {
        if (walletEntity instanceof SharedWalletEntity){
            return false;
        }

        IndividualWalletEntity entity = (IndividualWalletEntity) mSelectedWallet;
        if (!TextUtils.isEmpty(entity.getMnemonic())){
           return true;
        }

        return false;
    }

    private boolean isSelected(WalletEntity selectedWallet){
        if (selectedWallet == null){
            return false;
        }
        for (int i = 0; i < mWalletList.size(); i++){
            if (mWalletList.get(i) == selectedWallet){
                return true;
            }
        }
        return false;
    }

    private WalletEntity getSelectedWallet(){
        for (int i = 0; i < mWalletList.size(); i++){
            WalletEntity walletEntity = mWalletList.get(i);
            if (walletEntity instanceof IndividualWalletEntity){
                return walletEntity;
            }
            if (walletEntity instanceof SharedWalletEntity){
                SharedWalletEntity sharedWalletEntity = (SharedWalletEntity) walletEntity;
                if (sharedWalletEntity.isFinished()) {
                   return sharedWalletEntity;
                }
            }
        }
        return null;
    }

    private void show(){
        if (!isViewAttached()){
            return;
        } 
//        getView().showTotalBalance(mBalance);
        if (mWalletList.isEmpty()){
            getView().showEmptyView(true);
            return;
        }
        getView().showEmptyView(false);
        if (isSelected(mSelectedWallet)){
            getView().showWalletList(mSelectedWallet);
            getView().showWalletInfo(mSelectedWallet);
        }else {
            //挑选一个当前选中的钱包
            mSelectedWallet = getSelectedWallet();
            getView().showWalletList(mSelectedWallet);
            getView().showWalletInfo(mSelectedWallet);
            getView().showWalletTab(mSelectedWallet, 0);
        }
    }

    private void refreshWalletList(){
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
            return;
        }
        Collections.sort(mWalletList, new Comparator<WalletEntity>() {
            @Override
            public int compare(WalletEntity o1, WalletEntity o2) {
                return Long.compare(o1.getUpdateTime(),  o2.getUpdateTime());
            }
        });
        refreshBalance();
    }

    private void refreshBalance() {
        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() {
                try {
                    mBalance = 0d;
                    for (WalletEntity walletEntity : mWalletList) {
                        String address = walletEntity.getPrefixAddress();
                        double balance = Web3jManager.getInstance().getBalance(address);
                        walletEntity.setBalance(balance);
                        mBalance += balance;
                    }
                }catch (Exception exp){
                    exp.printStackTrace();
                }
                return mBalance;
            }
        })
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        if (isViewAttached()){
                            getView().showTotalBalance(balance);
                            if (mSelectedWallet != null){
                                getView().showBalance(mSelectedWallet.getBalance());
                            }
                        }
                    }
                });
    }
}
