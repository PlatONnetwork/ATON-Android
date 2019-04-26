package com.juzix.wallet.component.ui.presenter;

import com.juzhen.framework.network.SchedulersTransformer;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ManageSharedWalletContract;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.RxUtils;

import org.web3j.crypto.Credentials;

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
        mIndividualWalletEntity = IndividualWalletManager.getInstance().getWalletByAddress(mWalletEntity.getCreatorAddress());
    }

    @Override
    public void showWalletInfo() {
        if (isViewAttached()) {
            OwnerEntity firstOwner = null;
            List<OwnerEntity> owner = mWalletEntity.getOwner();
            for (OwnerEntity entity : owner){
                if (mWalletEntity.getPrefixCreatorAddress().contains(entity.getAddress())){
                    if (owner.remove(entity)){
                        firstOwner = entity;
                    }
                    break;
                }
            }
            if (firstOwner != null){
                if (mIndividualWalletEntity != null){
                    firstOwner.setName(mIndividualWalletEntity.getName());
                }
                owner.add(0, firstOwner);
            }

            getView().showWallet(mWalletEntity);
            getView().showMember(owner);
        }
    }


    @Override
    public void deleteAction(int type) {
        if (mIndividualWalletEntity != null) {
            getView().showPasswordDialog(type, -1, mIndividualWalletEntity);
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
                .compose(RxUtils.getSingleSchedulerTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().updateWalletName(name);
                            EventPublisher.getInstance().sendUpdateWalletListEvent();
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
                        List<OwnerEntity> owner = mWalletEntity.getOwner();
                        OwnerEntity addressEntity = owner.get(memberIndex);
                        addressEntity.setName(name);
                        return SharedWalletManager.getInstance().updateOwner(mWalletEntity.getUuid(), owner);
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isSuccess) throws Exception {
                        if (isSuccess && isViewAttached()) {
                            getView().updateWalletMemberName(name, memberIndex);
                            EventPublisher.getInstance().sendUpdateWalletListEvent();
                        }
                    }
                });
        ;
    }

    @Override
    public void validPassword(int viewType, Credentials credentials, int index) {
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
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
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

    @Override
    public boolean isExists(String walletName) {
        return IndividualWalletManager.getInstance().walletNameExists(walletName) ? true : SharedWalletManager.getInstance().walletNameExists(walletName);
    }
}
