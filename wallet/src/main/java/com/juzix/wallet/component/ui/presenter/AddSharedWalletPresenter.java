package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.AddSharedWalletContract;
import com.juzix.wallet.component.ui.dialog.SelectIndividualWalletDialogFragment;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;

import java.util.ArrayList;

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
        if (!walletEntityList.isEmpty()){
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
        if (!checkWalletName(name)){
            return;
        }
        if (!JZWalletUtil.isValidAddress(contractAddress)) {
            getView().showWalletAddressError(string(R.string.address_format_error));
            return;
        }
        showLoadingDialog();
        new Thread(){
            @Override
            public void run() {
                int code = SharedWalletTransactionManager.getInstance().addWallet(currentActivity(), name, contractAddress, walletEntity.getPrefixAddress());
                switch (code) {
                    case SharedWalletTransactionManager.CODE_OK:
                        mHandler.sendEmptyMessage(MSG_OK);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_ADD_WALLET:
                        mHandler.sendEmptyMessage(MSG_ADD_WALLET_FAILED);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_WALLET_EXISTS:
                        mHandler.sendEmptyMessage(MSG_WALLET_EXISTS);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_ILLEGAL_WALLET:
                        mHandler.sendEmptyMessage(MSG_VALID_ADDRESS_FAILD);
                    case SharedWalletTransactionManager.CODE_ERROR_UNLINKED_WALLET:
                        mHandler.sendEmptyMessage(MSG_WALLET_UNLINKED);
                        break;
                }
            }
        }.start();
    }

    @Override
    public boolean checkWalletName(String walletName) {
        String errorMsg = null;
        if (TextUtils.isEmpty(walletName)) {
            errorMsg = string(R.string.validSharedWalletNameEmptyTips);
        } else {
            if (walletName.length() > 12) {
                errorMsg = string(R.string.wallet_name_length_error);
            }
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
            SelectIndividualWalletDialogFragment.newInstance(walletEntity == null ? "" : walletEntity.getUuid()).show(currentActivity().getSupportFragmentManager(), SelectIndividualWalletDialogFragment.SELECT_SHARED_WALLET_OWNER);
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

    private static final int MSG_OK = 1;
    private static final int MSG_ADD_WALLET_FAILED = -1;
    private static final int MSG_WALLET_EXISTS = -3;
    private static final int MSG_VALID_ADDRESS_FAILD = -4;
    private static final int MSG_WALLET_UNLINKED = -5;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_OK:
                    showLongToast(string(R.string.addSharedWalletSuccess));
                    dismissLoadingDialogImmediately();
                    MainActivity.actionStart(currentActivity());
                    currentActivity().finish();
                    break;
                case MSG_ADD_WALLET_FAILED:
                    showLongToast(string(R.string.addWalletFailed));
                    dismissLoadingDialogImmediately();
                    break;
                case MSG_WALLET_EXISTS:
                    dismissLoadingDialogImmediately();
                    showLongToast(string(R.string.walletExists));
                    break;
                case MSG_VALID_ADDRESS_FAILD:
                    showLongToast(R.string.illegalWalletAddress);
                    dismissLoadingDialogImmediately();
                    break;
                case MSG_WALLET_UNLINKED:
                    showLongToast(R.string.joint_wallet_can_not_added);
                    dismissLoadingDialogImmediately();
                    break;
                default:
                    break;
            }
        }
    };
}
