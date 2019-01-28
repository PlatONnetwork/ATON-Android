package com.juzix.wallet.component.ui.presenter;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.ManageSharedWalletContract;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

import java.util.ArrayList;
import java.util.List;

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
    public void start() {
        if (isViewAttached()) {

            String ownerName = null;
            String ownerAddress = null;

            if (mIndividualWalletEntity != null) {
                ownerName = mIndividualWalletEntity.getName();
                ownerAddress = mIndividualWalletEntity.getPrefixAddress();
            } else {
                OwnerEntity ownerEntity = getSharedWalletOwner(mWalletEntity);
                ownerName = ownerEntity.getName();
                ownerAddress = ownerEntity.getPrefixAddress();
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
            getView().showPasswordDialog(type, -1);
        } else {
            deleteWallet();
        }
    }

    @Override
    public void modifyWalletName(String name) {
        showLoadingDialog();
        ManageSharedWalletContract.View view = getView();
        if (view != null) {
            new Thread() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(SharedWalletManager.getInstance().updateWalletName(mWalletEntity.getUuid(), name) ? MSG_MODIFY_WALLET_NAME_OK : MSG_MODIFY_WALLET_NAME_FAILED);
                }
            }.start();
        }
    }

    @Override
    public void modifyMemberName(int memberIndex, String name) {
        showLoadingDialog();
        ManageSharedWalletContract.View view = getView();
        if (view != null) {
            new Thread() {
                @Override
                public void run() {
                    ArrayList<OwnerEntity> owner = mWalletEntity.getOwner();
                    OwnerEntity addressEntity = owner.get(memberIndex);
                    addressEntity.setName(name);
                    mHandler.sendEmptyMessage(SharedWalletManager.getInstance().updateOwner(mWalletEntity.getUuid(), owner) ? MSG_MODIFY_MEMBER_NAME_OK : MSG_MODIFY_MEMBER_NAME_FAILED);
                }
            }.start();
        }
    }

    @Override
    public void validPassword(int viewType, String password, int index) {
        ManageSharedWalletContract.View view = getView();
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                if (view != null) {
                    Message msg = mHandler.obtainMessage();
                    msg.arg1 = viewType;
                    msg.arg2 = index;
                    msg.what = IndividualWalletManager.getInstance().isValidWallet(mIndividualWalletEntity, password) ? MSG_VALID_PWD_OK : MSG_VALID_PWD_FAILED;
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    @Override
    public void deleteWallet() {
        ManageSharedWalletContract.View view = getView();
        if (view != null) {
            new Thread() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessage(SharedWalletManager.getInstance().deleteWallet(mWalletEntity.getUuid()) ? MSG_DELETE_WALLET_OK : MSG_DELETE_WALLET_FAILED);
                }
            }.start();
        }
    }

    private static final int MSG_VALID_PWD_OK = 0;
    private static final int MSG_VALID_PWD_FAILED = 1;
    private static final int MSG_DELETE_WALLET_OK = 2;
    private static final int MSG_DELETE_WALLET_FAILED = 3;
    private static final int MSG_MODIFY_WALLET_NAME_OK = 4;
    private static final int MSG_MODIFY_WALLET_NAME_FAILED = 5;
    private static final int MSG_MODIFY_MEMBER_NAME_OK = 6;
    private static final int MSG_MODIFY_MEMBER_NAME_FAILED = 7;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ManageSharedWalletContract.View view = getView();
            switch (msg.what) {
                case MSG_VALID_PWD_OK:
                    int viewType = msg.arg1;
                    switch (viewType) {
                        case ManageSharedWalletContract.View.TYPE_MODIFY_WALLET_NAME:
                            if (view != null) {
                                view.dimissPasswordDialog();
                                view.showModifyWalletNameDialog();
                            }
                            dismissLoadingDialogImmediately();
                            break;
                        case ManageSharedWalletContract.View.TYPE_MODIFY_MEMBER_NAME:
                            if (view != null) {
                                view.dimissPasswordDialog();
                                view.showModifyMemberNameDialog(msg.arg2);
                            }
                            dismissLoadingDialogImmediately();
                            break;
                        case ManageSharedWalletContract.View.TYPE_DELETE_WALLET:
                            if (view != null) {
                                view.dimissPasswordDialog();
                            }
                            deleteWallet();
                            break;
                    }
                    break;
                case MSG_VALID_PWD_FAILED:
                    if (view != null) {
                        view.showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips));
                    }
                    dismissLoadingDialogImmediately();
                    break;

                case MSG_DELETE_WALLET_OK:
                    dismissLoadingDialogImmediately();
                    currentActivity().finish();
                    break;

                case MSG_DELETE_WALLET_FAILED:
                    dismissLoadingDialogImmediately();
                    break;

                case MSG_MODIFY_WALLET_NAME_OK:
                    dismissLoadingDialogImmediately();
                    currentActivity().finish();
                    break;

                case MSG_MODIFY_WALLET_NAME_FAILED:
                    dismissLoadingDialogImmediately();
                    break;

                case MSG_MODIFY_MEMBER_NAME_OK:
                    dismissLoadingDialogImmediately();
                    currentActivity().finish();
                    break;

                case MSG_MODIFY_MEMBER_NAME_FAILED:
                    dismissLoadingDialogImmediately();
                    break;
            }
        }
    };
}
