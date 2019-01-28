package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;

import com.juzhen.framework.network.NetConnectivity;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.CreateSharedWalletSecondStepContract;
import com.juzix.wallet.component.ui.dialog.CommonDialogFragment;
import com.juzix.wallet.component.ui.dialog.CreateContractDialogFragment;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.OnDialogViewClickListener;
import com.juzix.wallet.component.ui.view.AddressBookActivity;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.component.ui.view.PropertyFragment;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.engine.SharedWalletTransactionManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.entity.OwnerEntity;
import com.juzix.wallet.utils.BigDecimalUtil;
import com.juzix.wallet.utils.JZWalletUtil;

import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import io.reactivex.Flowable;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


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
            String errMsg = null;
            if (TextUtils.isEmpty(address)) {
                errMsg = string(R.string.address_cannot_be_empty);
                enabled = false;
            } else if (!JZWalletUtil.isValidAddress(address)) {
                errMsg = string(R.string.address_format_error);
                enabled = false;
            } else {
                addressSet.add(addressEntity.getAddress());
            }

            mEntityList.get(i).setErrorMsg(errMsg);
            getView().updateOwner(i);
        }

        if (!enabled) {
            return;
        }
        if (addressSet.size() != mEntityList.size()) {
            showLongToast(R.string.duplicateAddress);
            return;
        }
        if (!NetConnectivity.getConnectivityManager().isConnected()) {
            showLongToast(string(R.string.network_error));
            return;
        }

        validAddress(mEntityList);

    }

    @Override
    public void validPassword(String password, BigInteger gasPrice) {

        SharedWalletTransactionManager.getInstance()
                .validPassword(password, mWalletEntity.getKey())
                .compose(new SchedulersTransformer())
                .compose(LoadingTransformer.bindToLifecycle(getView().currentActivity()))
                .compose(bindToLifecycle())
                .subscribe(new Consumer<Credentials>() {
                    @Override
                    public void accept(Credentials credentials) throws Exception {

                        SharedWalletTransactionManager.getInstance()
                                .createSharedWallet(credentials, mWalletName, mWalletEntity.getPrefixAddress(), mRequiredSignatures, getAddressEntityList(), gasPrice);

                        MainActivity.actionStart(getContext(), MainActivity.TAB_PROPERTY, PropertyFragment.TAB_SHARED);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        CommonDialogFragment.createCommonTitleWithOneButton(string(R.string.validPasswordError), string(R.string.enterAgainTips), string(R.string.back), new OnDialogViewClickListener() {
                            @Override
                            public void onDialogViewClick(DialogFragment fragment, View view, Bundle extra) {
                                showInputWalletPasswordDialogFragment(password, gasPrice);
                            }
                        }).show(currentActivity().getSupportFragmentManager(), "showPasswordError");
                    }
                });
    }

    private void showInputWalletPasswordDialogFragment(String password, BigInteger price) {
        InputWalletPasswordDialogFragment.newInstance(password).setOnConfirmClickListener(new InputWalletPasswordDialogFragment.OnConfirmClickListener() {
            @Override
            public void onConfirmClick(String password) {
                validPassword(password, price);
            }
        }).show(currentActivity().getSupportFragmentManager(), "inputPassword");
    }

    private void validAddress(final List<CreateSharedWalletSecondStepContract.ContractEntity> addressEntityList) {

        Flowable.fromIterable(addressEntityList)
                .all(new Predicate<CreateSharedWalletSecondStepContract.ContractEntity>() {
                    @Override
                    public boolean test(CreateSharedWalletSecondStepContract.ContractEntity contractEntity) throws Exception {
                        return "0x".equals(Web3jManager.getInstance().getCode(contractEntity.getAddress()));
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean isAllAddressValid) throws Exception {
                        return isAllAddressValid;
                    }
                })
                .switchIfEmpty(new SingleSource<Boolean>() {
                    @Override
                    public void subscribe(SingleObserver<? super Boolean> observer) {
                        observer.onError(new Throwable(string(R.string.illegalWalletAddress)));
                    }
                })
                .map(new Function<Boolean, BigInteger>() {
                    @Override
                    public BigInteger apply(Boolean aBoolean) throws Exception {
                        return Web3jManager.getInstance().getWeb3j().ethGasPrice().send().getGasPrice();
                    }
                })
                .compose(new SchedulersTransformer())
                .compose(bindToLifecycle())
                .compose(LoadingTransformer.bindToLifecycle(currentActivity()))
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger gasPrice) throws Exception {
                        double feeAmount = getFeeAmount(gasPrice);
                        boolean isBalanceSufficient = mWalletEntity != null && mWalletEntity.getBalance() >= feeAmount;
                        if (isBalanceSufficient) {
                            CreateContractDialogFragment.newInstance(feeAmount).setOnSubmitClickListener(new CreateContractDialogFragment.OnSubmitClickListener() {
                                @Override
                                public void onSubmitClick() {
                                    InputWalletPasswordDialogFragment.newInstance("").setOnConfirmClickListener(new InputWalletPasswordDialogFragment.OnConfirmClickListener() {
                                        @Override
                                        public void onConfirmClick(String password) {
                                            validPassword(password, gasPrice);
                                        }
                                    }).show(currentActivity().getSupportFragmentManager(), "inputWalletPassword");
                                }
                            }).show(currentActivity().getSupportFragmentManager(), "createContract");
                        } else {
                            showLongToast(R.string.insufficientBalanceTips);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (isViewAttached() && !TextUtils.isEmpty(throwable.getMessage())) {
                            showLongToast(throwable.getMessage());
                        }
                    }
                });
    }

    private double getFeeAmount(BigInteger price) {
        double gasLimit = SharedWalletTransactionManager.INVOKE_GAS_LIMIT.doubleValue();
        double gasPrice = price.doubleValue();
        return BigDecimalUtil.div(String.valueOf(BigDecimalUtil.mul(gasPrice, gasLimit)), "1E18");
    }

    private ArrayList<OwnerEntity> getAddressEntityList() {
        ArrayList<OwnerEntity> addressEntityList = new ArrayList<>();
        for (CreateSharedWalletSecondStepContract.ContractEntity contractEntity : mEntityList) {
            addressEntityList.add(new OwnerEntity(UUID.randomUUID().toString(), contractEntity.getName(), contractEntity.getAddress()));
        }

        return addressEntityList;
    }
}
