package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.IndividualWalletContract;
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.Wallet;
import com.juzix.wallet.utils.RxUtils;

import org.reactivestreams.Publisher;

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
public class IndividualWalletPresenter extends BasePresenter<IndividualWalletContract.View> implements IndividualWalletContract.Presenter {

    private final static String TAG = IndividualWalletPresenter.class.getSimpleName();

    public IndividualWalletPresenter(IndividualWalletContract.View view) {
        super(view);
    }

    @Override
    public void fetchIndividualWalletList() {

        if (isViewAttached()) {

            List<Wallet> walletList = WalletManager.getInstance().getWalletList();

            getView().notifyWalletListChanged(walletList);

            getView().updateWalletBalance(getTotalBalance(walletList));

            fetchWalletBalance(walletList);
        }

    }

    private double getTotalBalance(List<Wallet> walletEntityList) {

        double totalBalance = 0D;

        if (walletEntityList == null || walletEntityList.isEmpty()) {
            return totalBalance;
        }

        for (Wallet walletEntity : walletEntityList) {
            totalBalance += walletEntity.getBalance();
        }

        return totalBalance;
    }

    private void fetchWalletBalance(List<Wallet> walletEntityList) {

        Flowable.fromIterable(walletEntityList)
                .map(new Function<Wallet, Wallet>() {
                    @Override
                    public Wallet apply(Wallet walletEntity) throws Exception {
                        double balance = Web3jManager.getInstance().getBalance(walletEntity.getPrefixAddress());
                        walletEntity.setBalance(balance);
                        return walletEntity;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<Wallet>() {
                    @Override
                    public void accept(Wallet walletEntity) throws Exception {
                        //更新balance
                        if (isViewAttached()) {
                            getView().updateItem(walletEntity);
                        }
                    }
                })
                .observeOn(Schedulers.io())
                .map(new Function<Wallet, Double>() {
                    @Override
                    public Double apply(Wallet walletEntity) throws Exception {
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
                .compose(RxUtils.getFlowableSchedulerTransformer())
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
