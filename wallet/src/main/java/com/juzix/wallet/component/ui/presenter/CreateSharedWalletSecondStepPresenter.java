package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.juzhen.framework.network.NetConnectivity;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.CreateSharedWalletSecondStepContract;
import com.juzix.wallet.component.ui.dialog.CreateContractDialogFragment;
import com.juzix.wallet.component.ui.view.AddressBookActivity;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.component.ui.view.PropertyFragment;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.event.EventPublisher;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JZWalletUtil;
import com.juzix.wallet.utils.ToastUtil;

import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import io.reactivex.functions.Consumer;


/**
 * @author matrixelement
 */
public class CreateSharedWalletSecondStepPresenter extends BasePresenter<CreateSharedWalletSecondStepContract.View> implements CreateSharedWalletSecondStepContract.Presenter {

    private int mSharedOwners;
    private int mRequiredSignatures;
    private String mWalletName;
    private IndividualWalletEntity mWalletEntity;
    private int mPosition;
    private List<CreateSharedWalletSecondStepContract.ContractEntity> mEntityList;

    public CreateSharedWalletSecondStepPresenter(CreateSharedWalletSecondStepContract.View view) {
        super(view);
        mSharedOwners = view.getSharedOwnersFromIntent();
        mRequiredSignatures = view.getRequiredSignaturesFromIntent();
        mWalletName = view.getWalletNameFromIntent();
        mWalletEntity = view.getWalletEntityFromIntent();
    }

    @Override
    public void init() {
        mPosition = -1;
        mEntityList = new ArrayList<>();
        mEntityList.add(new CreateSharedWalletSecondStepContract.ContractEntity.Builder()
                .name(mWalletEntity.getName())
                .address(mWalletEntity.getPrefixAddress())
                .errorMsg("")
                .enabled(false)
                .focus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NONE)
                .build());
        for (int i = 0; i < mSharedOwners - 1; i++) {
            CreateSharedWalletSecondStepContract.ContractEntity contractEntity = new CreateSharedWalletSecondStepContract.ContractEntity.Builder()
                    .name("")
                    .address("")
                    .errorMsg("")
                    .enabled(true)
                    .focus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NONE)
                    .build();

            mEntityList.add(new CreateSharedWalletSecondStepContract.ContractEntity.Builder()
                    .name("")
                    .address("")
                    .errorMsg("")
                    .enabled(true)
                    .focus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NONE)
                    .build());
        }
        getView().showOwnerList(mEntityList);
    }

    @Override
    public void scanAddress() {
        BaseActivity activity = currentActivity();
        requestPermission(activity, 100, new PermissionConfigure.PermissionCallback() {
            @Override
            public void onSuccess(int what, @NonNull List<String> grantPermissions) {
                ScanQRCodeActivity.startActivityForResult(activity, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
            }

            @Override
            public void onHasPermission(int what) {
                ScanQRCodeActivity.startActivityForResult(activity, Constants.RequestCode.REQUEST_CODE_SCAN_QRCODE);
            }

            @Override
            public void onFail(int what, @NonNull List<String> deniedPermissions) {

            }
        }, Manifest.permission.CAMERA);
    }

    @Override
    public void selectAddress() {
        AddressBookActivity.actionStartForResult(currentActivity(), Constants.Action.ACTION_GET_ADDRESS, Constants.RequestCode.REQUEST_CODE_GET_ADDRESS);
    }

    @Override
    public void updateAddress(String address) {
        if (isViewAttached() && mPosition > 0) {
            inputAddress(mPosition, address);
            getView().updateOwner(mPosition);
        }
    }

    @Override
    public void inputAddress(int position, String address) {
        if (isViewAttached()) {
            mEntityList.get(position).setAddress(address);
            boolean enabled = true;
            for (CreateSharedWalletSecondStepContract.ContractEntity entity : mEntityList) {
                if (TextUtils.isEmpty(entity.getAddress())) {
                    enabled = false;
                    break;
                }
            }
            getView().setCreateSharedWalletBtnEnable(enabled);
        }
    }

    @Override
    public void inputName(int position, String name) {
        if (isViewAttached()) {
            mEntityList.get(position).setName(name);
        }
    }

    @Override
    public void verifyAddress(int position) {
        if (mPosition == -1) {
            mPosition = position;
            return;
        }
        CreateSharedWalletSecondStepContract.ContractEntity contractEntity = mEntityList.get(mPosition);
        int focus = contractEntity.getFocus();
        if (mPosition == position && focus == CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NAME) {
            return;
        }
        if (focus == CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_ADDRESS) {
            String address = contractEntity.getAddress();
            String errorMsg = "";
            if (TextUtils.isEmpty(address)) {
                errorMsg = string(R.string.address_cannot_be_empty);
            } else if (!JZWalletUtil.isValidAddress(address)) {
                errorMsg = string(R.string.address_format_error);
            }
            contractEntity.setErrorMsg(errorMsg);
            if (isViewAttached()) {
                getView().updateOwner(mPosition);
            }
        }
        mPosition = position;
    }

    @Override
    public void focusName(int position) {
        for (CreateSharedWalletSecondStepContract.ContractEntity contractEntity : mEntityList) {
            contractEntity.setFocus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NONE);
        }
        mEntityList.get(position).setFocus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NAME);
    }

    @Override
    public void focusAddress(int position) {
        for (CreateSharedWalletSecondStepContract.ContractEntity contractEntity : mEntityList) {
            contractEntity.setFocus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NONE);
        }
        mEntityList.get(position).setFocus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_ADDRESS);
    }

    @Override
    public void createContract() {
        boolean enabled = true;
        HashSet<String> addressSet = new HashSet<String>();
        for (int i = 0; i < mEntityList.size(); i++) {
            CreateSharedWalletSecondStepContract.ContractEntity addressEntity = mEntityList.get(i);
            String address = addressEntity.getAddress();
            if (TextUtils.isEmpty(address)) {
                mEntityList.get(i).setErrorMsg(string(R.string.address_cannot_be_empty));
                getView().updateOwner(i);
                enabled = false;
            } else if (!JZWalletUtil.isValidAddress(address)) {
                mEntityList.get(i).setErrorMsg(string(R.string.address_format_error));
                getView().updateOwner(i);
                enabled = false;
            } else {
                addressSet.add(addressEntity.getAddress());
            }
        }

        if (!enabled) {
            return;
        }
        if (addressSet.size() != mEntityList.size()) {
            showLongToast(R.string.duplicateAddress);
            return;
        }
        if (!NetConnectivity.getConnectivityManager().isConnected()) {
            ToastUtil.showLongToast(currentActivity(), string(R.string.network_error));
            return;
        }
        validAddress(mEntityList);

    }

    private void validAddress(final List<CreateSharedWalletSecondStepContract.ContractEntity> addressEntityList) {
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                boolean hasContractAddress = false;
                for (CreateSharedWalletSecondStepContract.ContractEntity entity : addressEntityList) {
                    String code = Web3jManager.getInstance().getCode(entity.getAddress());
                    if (!"0x".equals(code)) {
                        hasContractAddress = true;
                        break;
                    }
                }
                mHandler.sendEmptyMessage(hasContractAddress ? MSG_VALID_ADDRESS_FAILD : MSG_VALID_ADDRESS_OK);
            }
        }.start();
    }

    private void getFeeAmount() {
        showLoadingDialog();
        new Thread() {
            @Override
            public void run() {
                try {
                    BigInteger gasPrice = Web3jManager.getInstance().getWeb3j().ethGasPrice().send().getGasPrice();
//                    String data = SharedWalletTransactionManager.getInstance().deployData();
//                    BigInteger deployGasLimit = Web3jManager.getInstance().getEstimateGas(mWalletEntity.getPrefixAddress(), null, data);
                    Message msg = mHandler.obtainMessage();
                    msg.what = MSG_GET_GAS_OK;
                    Bundle bundle = new Bundle();
                    bundle.putString("gasPrice", gasPrice.toString());
                    bundle.putString("gasLimit", SharedWalletTransactionManager.INVOKE_GAS_LIMIT.toString());
                    msg.setData(bundle);
                    mHandler.sendMessage(msg);
                } catch (Exception exp) {
                    exp.printStackTrace();
                    mHandler.sendEmptyMessage(MSG_GET_GAS_FAILED);
                }
            }
        }.start();
    }

    private ArrayList<OwnerEntity> getAddressEntityList() {
        ArrayList<OwnerEntity> addressEntityList = new ArrayList<>();
        for (CreateSharedWalletSecondStepContract.ContractEntity contractEntity : mEntityList) {
            addressEntityList.add(new OwnerEntity(UUID.randomUUID().toString(), contractEntity.getName(), contractEntity.getAddress()));
        }

        return addressEntityList;
    }

    private void createWallet(Credentials credentials, String gasPrice) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                int code = SharedWalletTransactionManager.getInstance().createWallet(credentials, mWalletName, mWalletEntity.getPrefixAddress(), mRequiredSignatures, getAddressEntityList(),
                        new BigInteger(gasPrice), new SharedWalletTransactionManager.OnUpdateCreateJointWalletProgressListener() {
                            @Override
                            public void updateCreateJointWalletProgress(SharedWalletEntity sharedWalletEntity) {
                                EventPublisher.getInstance().sendUpdateCreateJointWalletProgressEvent(sharedWalletEntity);
                            }
                        });
                switch (code) {
                    case SharedWalletTransactionManager.CODE_OK:
                        mHandler.sendEmptyMessage(MSG_CREATE_WALLET_OK);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_PASSWORD:
                        mHandler.sendEmptyMessage(MSG_VALID_PWD_FAILED);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_ADD_WALLET:
                        mHandler.sendEmptyMessage(MSG_CREATE_WALLET_FAILED);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_DEPLOY:
                        mHandler.sendEmptyMessage(MSG_CREATE_WALLET_FAILED);
                        break;
                    case SharedWalletTransactionManager.CODE_ERROR_WALLET_EXISTS:
                        mHandler.sendEmptyMessage(MSG_WALLET_EXISTS);
                        break;
                }
            }
        }).start();
    }


    @Override
    public void validPassword(String password, String gasPrice, String gasLimit) {

        SharedWalletTransactionManager.getInstance()
                .validPassword(password, mWalletEntity.getKey())
                .compose(LoadingTransformer.bindToLifecycle(getView().currentActivity()))
                .subscribe(new Consumer<Credentials>() {
                    @Override
                    public void accept(Credentials credentials) throws Exception {

                        createWallet(credentials, gasPrice);

                        if (isViewAttached()) {
                            MainActivity.actionStart(getContext(), MainActivity.TAB_PROPERTY, PropertyFragment.TAB_SHARED);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached()) {
                            getView().showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips));
                        }
                    }
                });
    }

    private static final int MSG_VALID_ADDRESS_OK = 2;
    private static final int MSG_GET_GAS_OK = 1;
    private static final int MSG_CREATE_WALLET_OK = 0;
    private static final int MSG_VALID_PWD_FAILED = -1;
    private static final int MSG_CREATE_WALLET_FAILED = -2;
    private static final int MSG_WALLET_EXISTS = -3;
    private static final int MSG_GET_GAS_FAILED = -4;
    private static final int MSG_VALID_ADDRESS_FAILD = -5;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (!isViewAttached()) {
                return;
            }
            CreateSharedWalletSecondStepContract.View view = getView();
            switch (msg.what) {
                case MSG_GET_GAS_OK:
                    Bundle data = msg.getData();
                    String gasPrice = data.getString("gasPrice");
                    String gasLimit = data.getString("gasLimit");
                    double feeAmount = BigDecimalUtil.mul(Double.parseDouble(gasPrice), Double.parseDouble(gasLimit));
                    feeAmount = BigDecimalUtil.div(String.valueOf(feeAmount), "1E18");
                    if (mWalletEntity.getBalance() < feeAmount) {
                        showLongToast(R.string.insufficientBalanceTips);
                    } else {
                        CreateContractDialogFragment.newInstance(feeAmount).setOnSubmitClickListener(new CreateContractDialogFragment.OnSubmitClickListener() {
                            @Override
                            public void onSubmitClick() {
                                getView().showPasswordDialog(gasPrice, gasLimit);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "createContract");
                    }

                    dismissLoadingDialogImmediately();
                    break;
                case MSG_CREATE_WALLET_OK:
                    dismissLoadingDialogImmediately();
                    if (view != null) {
                        view.dimissPasswordDialog();
                    }
                    BaseActivity activity = currentActivity();
                    activity.setResult(BaseActivity.RESULT_OK);
                    activity.finish();
                    break;
                case MSG_VALID_PWD_FAILED:
                    if (view != null) {
                        view.showErrorDialog(string(R.string.validPasswordError), string(R.string.enterAgainTips));
                    }
                    dismissLoadingDialogImmediately();
                    break;
                case MSG_CREATE_WALLET_FAILED:
                    showLongToast(string(R.string.createWalletFailed));
                    break;
                case MSG_WALLET_EXISTS:
                    dismissLoadingDialogImmediately();
                    if (view != null) {
                        view.dimissPasswordDialog();
                    }
                    showLongToast(string(R.string.walletExists));
                    break;
                case MSG_GET_GAS_FAILED:
                    dismissLoadingDialogImmediately();
                    break;
                case MSG_VALID_ADDRESS_OK:
                    dismissLoadingDialogImmediately();
                    if (mWalletEntity.getBalance() <= 0) {
                        showLongToast(R.string.insufficientBalanceTips);
                        return;
                    }
                    getFeeAmount();
                    break;
                case MSG_VALID_ADDRESS_FAILD:
                    showLongToast(R.string.illegalWalletAddress);
                    dismissLoadingDialogImmediately();
                    break;
            }
        }
    };

}
