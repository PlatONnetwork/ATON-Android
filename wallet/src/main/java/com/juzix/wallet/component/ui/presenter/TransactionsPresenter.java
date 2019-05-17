package com.juzix.wallet.component.ui.presenter;


import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.TransactionsContract;
import com.juzix.wallet.entity.WalletEntity;
import io.reactivex.disposables.Disposable;


/**
 * @author matrixelement
 */
public class TransactionsPresenter extends BasePresenter<TransactionsContract.View> implements TransactionsContract.Presenter {

    private static final String TAG = TransactionsPresenter.class.getSimpleName();
    private WalletEntity mWalletEntity;
    private Disposable mDisposable;

    public TransactionsPresenter(TransactionsContract.View view) {
        super(view);
    }

}
