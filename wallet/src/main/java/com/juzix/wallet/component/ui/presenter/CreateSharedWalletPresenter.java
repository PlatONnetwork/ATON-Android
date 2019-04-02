package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.CreateSharedWalletContract;
import com.juzix.wallet.component.ui.dialog.SelectIndividualWalletDialogFragment;
import com.juzix.wallet.component.ui.view.CreateSharedWalletSecondStepActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;

import java.util.ArrayList;

/**
 * @author matrixelement
 */
public class CreateSharedWalletPresenter extends BasePresenter<CreateSharedWalletContract.View> implements CreateSharedWalletContract.Presenter {

    private IndividualWalletEntity walletEntity;

    public CreateSharedWalletPresenter(CreateSharedWalletContract.View view) {
        super(view);
    }

    @Override
    public void init() {
        ArrayList<IndividualWalletEntity> walletEntityList = IndividualWalletManager.getInstance().getWalletList();
        if (!walletEntityList.isEmpty()){
            for (IndividualWalletEntity walletEntity : walletEntityList) {
                if (walletEntity.getBalance() > 0) {
                    updateSelectOwner(walletEntity);
                    break;
                }
            }
        }
    }

    @Override
    public void showSelectWalletDialogFragment() {
        if (isViewAttached()) {
            SelectIndividualWalletDialogFragment.newInstance(walletEntity == null ? "" : walletEntity.getUuid(), true)
                    .setOnItemClickListener(new SelectIndividualWalletDialogFragment.OnItemClickListener() {
                        @Override
                        public void onItemClick(IndividualWalletEntity walletEntity) {
                            updateSelectOwner(walletEntity);
                        }
                    }).show(currentActivity().getSupportFragmentManager(), SelectIndividualWalletDialogFragment.CREATE_SHARED_WALLET);
        }
    }

    @Override
    public void updateSelectOwner(IndividualWalletEntity walletEntity) {
        this.walletEntity = walletEntity;
        if (isViewAttached()) {
            getView().updateSelectOwner(walletEntity);
            getView().setNextButtonEnable(!TextUtils.isEmpty(getView().getWalletName()) && walletEntity != null);
        }
    }

    @Override
    public void updateWalletName(String walletName) {
        if (isViewAttached()) {
            getView().setNextButtonEnable(!TextUtils.isEmpty(walletName) && walletName.length() <= 12 && walletEntity != null);
        }
    }

    @Override
    public void next() {

        if (isViewAttached()) {

            int sharedOwners = getView().getSharedOwners();
            int requiredSignatures = getView().getRequiredSignatures();
            String walletName = getView().getWalletName();

            if (!checkWalletName(walletName)) {
                return;
            }
            if (isExists(walletName)){
                return;
            }

            if (walletEntity == null) {
                return;
            }

            CreateSharedWalletSecondStepActivity.actionStartForResult(currentActivity(), CreateSharedWalletContract.View.REQUEST_CODE_CREATE_SHARED_WALLET_SECOND_STEP,
                    sharedOwners, requiredSignatures, walletName, walletEntity);
        }
    }

    @Override
    public boolean checkWalletName(String walletName) {

        String errMsg = null;

        if (TextUtils.isEmpty(walletName)) {
            errMsg = string(R.string.validWalletNameEmptyTips);
        }else if (walletName.length() > 12) {
            errMsg = string(R.string.wallet_name_length_error);
        }else if (isExists(walletName)){
            errMsg = string(R.string.wallet_name_exists);
        }

        getView().showWalletNameError(errMsg);

        return TextUtils.isEmpty(errMsg);
    }

    @Override
    public boolean isExists(String walletName) {
        return IndividualWalletManager.getInstance().walletNameExists(walletName) ? true : SharedWalletManager.getInstance().walletNameExists(walletName);
    }
}
