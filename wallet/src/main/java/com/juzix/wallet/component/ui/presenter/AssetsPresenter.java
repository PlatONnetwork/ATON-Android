package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.text.TextUtils;

import com.juzhen.framework.network.SchedulersTransformer;
import com.juzix.wallet.R;
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
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.RxUtils;
import com.tbruyelle.rxpermissions2.RxPermissions;
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
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class AssetsPresenter extends BasePresenter<AssetsContract.View> implements AssetsContract.Presenter {

    private static final String TAG = AssetsPresenter.class.getSimpleName();

    private List<WalletEntity> mWalletList = new ArrayList<>();
    private Disposable mDisposable;
    private static final int REFRESH_TIME = 5000;

    public AssetsPresenter(AssetsContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        getView().showCurrentItem(0);
    }

    @Override
    public void start() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }

        mDisposable = Flowable
                .fromIterable(mWalletList)
                .map(new Function<WalletEntity, Double>() {
                    @Override
                    public Double apply(WalletEntity walletEntity) throws Exception {
                        double balance = Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
                        walletEntity.setBalance(balance == 0 ? walletEntity.getBalance() : balance);
//                        Log.d("aaaaaa", "数量1111111111-------->" + walletEntity.getBalance());
                        return balance == 0 ? walletEntity.getBalance() : balance;
                    }
                })
                .reduce(new BiFunction<Double, Double, Double>() {
                    @Override
                    public Double apply(Double aDouble, Double aDouble2) throws Exception {
//                        Log.d("aaaaaa", "数量33333333333333------>" + aDouble + aDouble2);
                        return aDouble + aDouble2;
                    }
                })
                .toSingle()
                .compose(bindUntilEvent(FragmentEvent.STOP))
                .compose(RxUtils.getSingleSchedulerTransformer())
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(REFRESH_TIME, TimeUnit.MILLISECONDS);
                    }
                })
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double balance) throws Exception {
                        if (isViewAttached()) {
//                            Log.d("aaaaaa", "数量2222222222----------->" + balance);
                            getView().showTotalBalance(balance);
                            WalletEntity walletEntity = WalletManager.getInstance().getSelectedWallet();
                            if (walletEntity != null) {
                                getView().showBalance(walletEntity.getBalance());
                            }
                        }
                    }
                });

//        mDisposable = Single.fromCallable(new Callable<Double>() {
//            @Override
//            public Double call() {
//                double totalBalance = 0d;
//                try {
//                    for (WalletEntity walletEntity : mWalletList) {
//                        String address = walletEntity.getPrefixAddress();
//                        double balance = Web3jManager.getInstance().getBalance(address);
//                        walletEntity.setBalance(balance);
//                        totalBalance += balance;
//                    }
//                } catch (Exception exp) {
//                    exp.printStackTrace();
//                }
//                return totalBalance;
//            }
//        })
//                .compose(bindUntilEvent(FragmentEvent.STOP))
//                .compose(RxUtils.getSingleSchedulerTransformer())
//                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
//                    @Override
//                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
//                        return objectFlowable.delay(REFRESH_TIME, TimeUnit.MILLISECONDS);
//
//                    }
//                })
//                .subscribe(new Consumer<Double>() {
//                    @Override
//                    public void accept(Double balance) throws Exception {
//                        if (isViewAttached()) {
//                            getView().showTotalBalance(balance);
//                            WalletEntity walletEntity = WalletManager.getInstance().getSelectedWallet();
//                            if (walletEntity != null) {
//                                getView().showBalance(walletEntity.getBalance());
//                            }
//                        }
//                    }
//                });
    }

    @Override
    public void fetchWalletList() {
        if (!isViewAttached()) {
            return;
        }
        Single.fromCallable(new Callable<Double>() {
            @Override
            public Double call() {
                refreshWalletList();
                return 0D;
            }
        })
                .compose(new SchedulersTransformer())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double o) throws Exception {
                        if (isViewAttached()) {
                            show();
                        }
                    }
                });
    }

    @Override
    public List<WalletEntity> getRecycleViewDataSource() {
        return mWalletList;
    }

    @Override
    public void clickRecycleViewItem(WalletEntity walletEntity) {
        WalletManager.getInstance().setSelectedWallet(walletEntity);
        getView().showWalletList(walletEntity);
        getView().showWalletInfo(walletEntity);
        getView().setArgument(walletEntity);
    }

    @Override
    public void scanQRCode() {
        new RxPermissions(currentActivity())
                .request(Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean success) throws Exception {
                        if (isViewAttached() && success) {
                            ScanQRCodeActivity.startActivityForResult(currentActivity(), MainActivity.REQ_ASSETS_TAB_QR_CODE);
                        }
                    }
                });

    }

    @Override
    public void createIndividualWallet() {
        CreateIndividualWalletActivity.actionStart(getContext());
    }

    @Override
    public void createSharedWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()) {
            showLongToast(R.string.noWalletTips);
            return;
        }
        double totalBalance = 0.0D;
        for (IndividualWalletEntity walletEntity : walletEntityList) {
            totalBalance = BigDecimalUtil.add(totalBalance, walletEntity.getBalance());
        }
        if (totalBalance <= 0) {
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
        if (walletEntityList.isEmpty()) {
            showLongToast(R.string.noWalletTips);
            return;
        }
        AddSharedWalletActivity.actionStart(getContext());
    }

    @Override
    public void backupWallet() {
        IndividualWalletEntity walletEntity = (IndividualWalletEntity) WalletManager.getInstance().getSelectedWallet();
        InputWalletPasswordDialogFragment.newInstance(walletEntity).setOnWalletCorrectListener(new InputWalletPasswordDialogFragment.OnWalletCorrectListener() {
            @Override
            public void onCorrect(Credentials credentials, String password) {
                BackupMnemonicPhraseActivity.actionStart(getContext(), password, walletEntity, 1);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    @Override
    public boolean needBackup(WalletEntity walletEntity) {
        if (walletEntity == null) {
            return false;
        }
        if (walletEntity instanceof SharedWalletEntity) {
            return false;
        }
        IndividualWalletEntity entity = (IndividualWalletEntity) walletEntity;
        if (!TextUtils.isEmpty(entity.getMnemonic())) {
            return true;
        }
        return false;
    }

    @Override
    public void updateCreateJointWallet(SharedWalletEntity sharedWalletEntity) {
        if (sharedWalletEntity == null) {
            return;
        }
        if (sharedWalletEntity.getProgress() == 100) {
            SharedWalletManager.getInstance().updateWalletFinished(sharedWalletEntity.getUuid(), true);
            sharedWalletEntity.updateFinished(true);
        }
        getView().notifyAllChanged();

    }

    @Override
    public void updateUnreadMessage(String contractAddress, boolean hasUnreadMessage) {
        if (mWalletList == null || mWalletList.isEmpty()) {
            return;
        }
        for (int i = 0; i < mWalletList.size(); i++) {
            WalletEntity walletEntity = mWalletList.get(i);
            if (walletEntity instanceof SharedWalletEntity && contractAddress.equals(walletEntity.getAddress())) {
                ((SharedWalletEntity) walletEntity).setHasUnreadMessage(hasUnreadMessage);
                break;
            }
        }
        getView().notifyAllChanged();

    }

    private boolean isSelected(WalletEntity selectedWallet) {
        if (selectedWallet == null) {
            return false;
        }
        for (int i = 0; i < mWalletList.size(); i++) {
            if (mWalletList.get(i) == selectedWallet) {
                return true;
            }
        }
        return false;
    }

    private WalletEntity getSelectedWallet() {
        for (int i = 0; i < mWalletList.size(); i++) {
            WalletEntity walletEntity = mWalletList.get(i);
            if (walletEntity instanceof IndividualWalletEntity) {
                return walletEntity;
            }
            if (walletEntity instanceof SharedWalletEntity) {
                SharedWalletEntity sharedWalletEntity = (SharedWalletEntity) walletEntity;
                if (sharedWalletEntity.isFinished()) {
                    return sharedWalletEntity;
                }
            }
        }
        return null;
    }

    private void show() {
        if (!isViewAttached()) {
            return;
        }
        if (mWalletList.isEmpty()) {
            getView().showTotalBalance(0);
            getView().showEmptyView(true);
            return;
        }
        getView().showEmptyView(false);
        WalletEntity walletEntity = WalletManager.getInstance().getSelectedWallet();
        if (isSelected(walletEntity)) {
            getView().showWalletList(walletEntity);
            getView().showWalletInfo(walletEntity);
        } else {
            //挑选一个当前选中的钱包
            walletEntity = getSelectedWallet();
            WalletManager.getInstance().setSelectedWallet(walletEntity);
            getView().showWalletList(walletEntity);
            getView().showWalletInfo(walletEntity);
            getView().setArgument(walletEntity);
        }
    }

    private void refreshWalletList() {//联名钱包相关的先屏蔽掉
        List<IndividualWalletEntity> walletList1 = IndividualWalletManager.getInstance().getWalletList();
//        List<SharedWalletEntity> walletList2 = SharedWalletManager.getInstance().getWalletList();
        if (!mWalletList.isEmpty()) {
            mWalletList.clear();
        }
        if (!walletList1.isEmpty()) {
            mWalletList.addAll(walletList1);
        }
//        if (!walletList2.isEmpty()) {
//            mWalletList.addAll(walletList2);
//        }
        if (mWalletList.isEmpty()) {
            return;
        }
        Collections.sort(mWalletList, new Comparator<WalletEntity>() {
            @Override
            public int compare(WalletEntity o1, WalletEntity o2) {
                return Long.compare(o1.getUpdateTime(), o2.getUpdateTime());
            }
        });
    }
}
