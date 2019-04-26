package com.juzix.wallet.component.ui.presenter;


import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SharedWalletContract;
import com.juzix.wallet.component.ui.view.AddSharedWalletActivity;
import com.juzix.wallet.component.ui.view.CreateSharedWalletActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import org.reactivestreams.Publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class SharedWalletPresenter extends BasePresenter<SharedWalletContract.View> implements SharedWalletContract.Presenter {

    private final static String TAG = SharedWalletPresenter.class.getSimpleName();

    public SharedWalletPresenter(SharedWalletContract.View view) {
        super(view);
    }

    @Override
    public void fetchSharedWalletList() {

        if (isViewAttached()) {

            List<SharedWalletEntity> sharedWalletList = SharedWalletManager.getInstance().getWalletList();

            getView().notifyWalletListChanged(sharedWalletList);

            getView().updateWalletBalance(getTotalBalance(sharedWalletList));

            fetchWalletBalance(sharedWalletList);

        }
    }

    @Override
    public void createWallet() {
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
        CreateSharedWalletActivity.actionStart(currentActivity());
    }

    @Override
    public void addWallet() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (walletEntityList.isEmpty()) {
            showLongToast(R.string.noWalletTips);
            return;
        }
        AddSharedWalletActivity.actionStart(currentActivity());
    }

    private double getTotalBalance(List<SharedWalletEntity> sharedWalletEntityList) {

        double totalBalance = 0D;

        if (sharedWalletEntityList == null || sharedWalletEntityList.isEmpty()) {
            return totalBalance;
        }

        for (SharedWalletEntity sharedWalletEntity : sharedWalletEntityList) {
            totalBalance += sharedWalletEntity.getBalance();
        }

        return totalBalance;
    }

    private void fetchWalletBalance(List<SharedWalletEntity> walletEntityList) {

        Flowable.fromIterable(walletEntityList)
                .map(new Function<SharedWalletEntity, SharedWalletEntity>() {
                    @Override
                    public SharedWalletEntity apply(SharedWalletEntity walletEntity) throws Exception {
                        double balance = Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
                        walletEntity.setBalance(balance);
                        return walletEntity;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<SharedWalletEntity>() {
                    @Override
                    public void accept(SharedWalletEntity walletEntity) throws Exception {
                        //更新balance
                        if (isViewAttached()) {
                            getView().updateItem(walletEntity);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<SharedWalletEntity, Double>() {
                    @Override
                    public Double apply(SharedWalletEntity walletEntity) throws Exception {
                        return walletEntity.getBalance();
                    }
                })
                .reduce(new BiFunction<Double, Double, Double>() {
                    @Override
                    public Double apply(Double aDouble, Double aDouble2) throws Exception {
                        return aDouble + aDouble2;
                    }
                })
                .repeatWhen(new Function<Flowable<Object>, Publisher<?>>() {
                    @Override
                    public Publisher<?> apply(Flowable<Object> objectFlowable) throws Exception {
                        return objectFlowable.delay(5, TimeUnit.SECONDS);
                    }
                })
                .compose(new FlowableSchedulersTransformer())
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Double>() {
                    @Override
                    public void accept(Double totalBalance) throws Exception {
                        if (isViewAttached()) {
                            getView().updateWalletBalance(totalBalance);
                        }
                    }
                });
    }
}
