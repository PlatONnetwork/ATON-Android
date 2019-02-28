package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ManageSharedWalletContract;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;

/**
 * @author matrixelement
 */
public class ManageSharedWalletPresenter extends BasePresenter<ManageSharedWalletContract.View> implements ManageSharedWalletContract.Presenter {

    private SharedWalletEntity mWalletEntity;
    private IndividualWalletEntity mIndividualWalletEntity;

    public ManageSharedWalletPresenter(ManageSharedWalletContract.View view) {
        super(view);
        mWalletEntity = view.getWalletEntityFromIntent();
        mIndividualWalletEntity = IndividualWalletManager.getInstance().getWalletByAddress(mWalletEntity.getAddress());
    }

    @Override
    public void showWalletInfo() {
        if (isViewAttached()) {

            String ownerName = null;
            String ownerAddress = null;

            if (mIndividualWalletEntity != null) {
                ownerName = mIndividualWalletEntity.getName();
                ownerAddress = mIndividualWalletEntity.getPrefixAddress();
            } else {
                OwnerEntity ownerEntity = getSharedWalletOwner(mWalletEntity);
                if (ownerEntity != null){
                    ownerName = ownerEntity.getName();
                    ownerAddress = ownerEntity.getPrefixAddress();
                }
            }

            getView().showWallet(mWalletEntity);
            getView().showMember(mWalletEntity.getOwner());
            getView().showOwner(TextUtils.isEmpty(ownerName) ? "" : ownerName, TextUtils.isEmpty(ownerAddress) ? "" : ownerAddress);
        }
    }

    private OwnerEntity getSharedWalletOwner(SharedWalletEntity walletEntity) {

        if (walletEntity == null || walletEntity.getOwner() == null || walletEntity.getOwner().isEmpty()) {
            return null;
        }
        List<OwnerEntity> ownerEntityList = walletEntity.getOwner();
        for (OwnerEntity ownerEntity : ownerEntityList) {
            if (!TextUtils.isEmpty(ownerEntity.getAddress()) && ownerEntity.getAddress().equals(walletEntity.getAddress())) {
                return ownerEntity;
            }
        }

        return null;
    }


    @Override
    public void deleteAction(int type) {
        if (mIndividualWalletEntity != null) {
            getView().showPasswordDialog(type, -1, "");
        } else {
            deleteWallet();
        }
    }

    @Override
    public void modifyWalletName(String name) {

        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return SharedWalletManager.getInstance().updateWalletName(mWalletEntity.getUuid(), name);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().updateWalletName(name);
                        }
                    }
                });
    }

    @Override
    public void modifyMemberName(int memberIndex, String name) {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        ArrayList<OwnerEntity> owner = mWalletEntity.getOwner();
                        OwnerEntity addressEntity = owner.get(memberIndex);
                        addressEntity.setName(name);
                        return SharedWalletManager.getInstance().updateOwner(mWalletEntity.getUuid(), owner);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().updateWalletMemberName(name, memberIndex);
                        }
                    }
                });
        ;
    }

    @Override
    public void validPassword(int viewType, String password, int index) {

        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return IndividualWalletManager.getInstance().isValidWallet(mIndividualWalletEntity, password);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isViewAttached()) {
                            if (isSuccess) {
                                onValidPasswordSuccess(viewType, index);
                            } else {
                                getView().showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips), password);
                            }
                        }
                    }
                });
    }

    @Override
    public void deleteWallet() {
        Single
                .fromCallable(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        return SharedWalletManager.getInstance().deleteWallet(mWalletEntity.getUuid());
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            currentActivity().finish();
                        }
                    }
                });
    }

    private void onValidPasswordSuccess(int viewType, int index) {
        switch (viewType) {
            case ManageSharedWalletContract.View.TYPE_MODIFY_WALLET_NAME:
                if (isViewAttached()) {
                    getView().showModifyWalletNameDialog();
                }
                break;
            case ManageSharedWalletContract.View.TYPE_MODIFY_MEMBER_NAME:
                if (isViewAttached()) {
                    getView().showModifyMemberNameDialog(index);
                }
                break;
            case ManageSharedWalletContract.View.TYPE_DELETE_WALLET:
                deleteWallet();
                break;
            default:
                break;
        }
    }
}
