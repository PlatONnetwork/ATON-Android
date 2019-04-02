package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.app.CustomThrowable;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.AddSharedWalletContract;
import com.juzix.wallet.component.ui.dialog.SelectIndividualWalletDialogFragment;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class AddSharedWalletPresenter extends BasePresenter<AddSharedWalletContract.View> implements AddSharedWalletContract.Presenter {

    private IndividualWalletEntity walletEntity;

    public AddSharedWalletPresenter(AddSharedWalletContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (!walletEntityList.isEmpty()) {
            updateSelectOwner(walletEntityList.get(0));
        }
    }

    @Override
    public void updateSelectOwner(IndividualWalletEntity walletEntity) {
        this.walletEntity = walletEntity;
        if (isViewAttached() && walletEntity != null) {
            getView().setSelectOwner(walletEntity);
        }
    }

    @Override
    public void addWallet(String name, String contractAddress) {
        if (!checkWalletName(name)) {
            return;
        }
        if (!JZWalletUtil.isValidAddress(contractAddress)) {
            getView().showWalletAddressError(string(R.string.address_format_error));
            return;
        }
        if (isExists(name)) {
            return;
        }
        if (SharedWalletManager.getInstance().isWalletExist(contractAddress)) {
            showLongToast(string(R.string.walletExists));
            return;
        }

        SharedWalletTransactionManager
                .getInstance()
                .addWallet(name, contractAddress, walletEntity.getPrefixAddress())
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (isViewAttached()) {
                            currentActivity().finish();
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (throwable instanceof CustomThrowable) {
                            showLongToast(((CustomThrowable) throwable).getDetailMsgRes());
                        } else {
                            showLongToast(string(R.string.addWalletFailed));
                        }
                    }
                });
    }

    @Override
    public boolean checkWalletName(String walletName) {
        String errorMsg = null;
        if (TextUtils.isEmpty(walletName)) {
            errorMsg = string(R.string.validSharedWalletNameEmptyTips);
        } else if (walletName.length() > 12) {
            errorMsg = string(R.string.wallet_name_length_error);
        } else if (isExists(walletName)) {
            errorMsg = string(R.string.wallet_name_exists);
        }
        getView().showWalletNameError(errorMsg);

        return TextUtils.isEmpty(errorMsg);
    }

    @Override
    public boolean checkWalletAddress(String walletAddress) {
        String errorMsg = null;
        if (TextUtils.isEmpty(walletAddress)) {
            errorMsg = string(R.string.validWalletAddressEmptyTips);
        } else {
            if (!JZWalletUtil.isValidAddress(walletAddress)) {
                errorMsg = string(R.string.address_format_error);
            }
        }

        getView().showWalletAddressError(errorMsg);

        return TextUtils.isEmpty(errorMsg);
    }

    @Override
    public void showSelectOwnerDialogFragment() {
        if (isViewAttached()) {
            SelectIndividualWalletDialogFragment.newInstance(walletEntity == null ? "" : walletEntity.getUuid())
                    .setOnItemClickListener(new SelectIndividualWalletDialogFragment.OnItemClickListener() {
                        @Override
                        public void onItemClick(IndividualWalletEntity walletEntity) {
                            updateSelectOwner(walletEntity);
                        }
                    }).show(currentActivity().getSupportFragmentManager(), SelectIndividualWalletDialogFragment.SELECT_SHARED_WALLET_OWNER);
        }
    }

    @Override
    public void checkAddSharedWalletBtnEnable() {
        if (isViewAttached()) {

            String walletAddress = getView().getWalletAddress().trim();
            String walletName = getView().getWalletName().trim();

            getView().setAddSharedWalletBtnEnable(!TextUtils.isEmpty(walletAddress) && !TextUtils.isEmpty(walletName) && walletEntity != null);
        }
    }

    @Override
    public boolean isExists(String walletName) {
        return IndividualWalletManager.getInstance().walletNameExists(walletName) ? true : SharedWalletManager.getInstance().walletNameExists(walletName);
    }
}
