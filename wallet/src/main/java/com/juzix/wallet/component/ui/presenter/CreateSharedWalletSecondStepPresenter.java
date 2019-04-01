package com.juzix.wallet.component.ui.presenter;

import android.Manifest;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.juzhen.framework.network.NetConnectivity;
import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.R;
import com.juzix.wallet.app.Constants;
import com.juzix.wallet.app.LoadingTransformer;
import com.juzix.wallet.app.SchedulersTransformer;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.CreateSharedWalletSecondStepContract;
import com.juzix.wallet.component.ui.dialog.InputWalletPasswordDialogFragment;
import com.juzix.wallet.component.ui.dialog.SendTransactionDialogFragment;
import com.juzix.wallet.component.ui.view.AddNewAddressActivity;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.component.ui.view.ScanQRCodeActivity;
import com.juzix.wallet.component.ui.view.SelectAddressActivity;
import com.juzix.wallet.config.PermissionConfigure;
import com.juzix.wallet.db.entity.AddressInfoEntity;
import com.juzix.wallet.db.sqlite.AddressInfoDao;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
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
        for (int i = 0; i < mSharedOwners - 1; i++) {
            mEntityList.add(new CreateSharedWalletSecondStepContract.ContractEntity.Builder()
                    .name("")
                    .address("")
                    .errorMsg("")
                    .enabled(true)
                    .focus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NONE)
                    .build());
        }
        getView().showWalletInfo(mWalletEntity);
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
        SelectAddressActivity.actionStartForResult(currentActivity(), Constants.Action.ACTION_GET_ADDRESS, Constants.RequestCode.REQUEST_CODE_GET_ADDRESS);
    }

    @Override
    public void updateAddress(String address) {
        if (isViewAttached() && mPosition > -1) {
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
    public boolean needSaveAddressBook(String address){
        if (TextUtils.isEmpty(address)){
            return false;
        }
        if (!JZWalletUtil.isValidAddress(address)) {
            return false;
        }

        return !AddressInfoDao.getInstance().isExist(address);
    }

    @Override
    public boolean saveWallet(String name, String address) {
        String[] avatarArray = getContext().getResources().getStringArray(R.array.wallet_avatar);
        String avatar = avatarArray[new Random().nextInt(avatarArray.length)];
        return AddressInfoDao.getInstance().insertAddressInfo(new AddressInfoEntity(UUID.randomUUID().toString(), address, name, avatar));
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

        List<CreateSharedWalletSecondStepContract.ContractEntity> list  = new ArrayList<>();
        list.add(new CreateSharedWalletSecondStepContract.ContractEntity.Builder()
                .name(mWalletEntity.getName())
                .address(mWalletEntity.getPrefixAddress())
                .errorMsg("")
                .enabled(false)
                .focus(CreateSharedWalletSecondStepContract.ContractEntity.FOCUS_NONE)
                .build());
        list.addAll(mEntityList);
        validAddress(list);

    }

    @Override
    public void validPassword(Credentials credentials, BigInteger gasPrice, double feeAmount) {
        SharedWalletTransactionManager.getInstance().createSharedWallet(credentials, mWalletName, mWalletEntity.getPrefixAddress(), mRequiredSignatures, getAddressEntityList(), gasPrice, feeAmount);
        MainActivity.actionStart(getContext());
    }

    private void showInputWalletPasswordDialogFragment(BigInteger price, double feeAmount) {
        InputWalletPasswordDialogFragment.newInstance(mWalletEntity).setOnWalletPasswordCorrectListener(new InputWalletPasswordDialogFragment.OnWalletPasswordCorrectListener() {
            @Override
            public void onWalletPasswordCorrect(Credentials credentials) {
                validPassword(credentials, price, feeAmount);

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
                .compose(LoadingTransformer.bindToSingleLifecycle(currentActivity()))
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger gasPrice) throws Exception {
                        double feeAmount = getFeeAmount(gasPrice);
                        boolean isBalanceSufficient = mWalletEntity != null && mWalletEntity.getBalance() >= feeAmount;
                        if (isBalanceSufficient) {
                            SendTransactionDialogFragment
                                    .newInstance(string(R.string.create_contract), NumberParserUtils.getPrettyBalance(feeAmount), buildTransactionInfo(mWalletEntity.getName()))
                                    .setOnConfirmBtnClickListener(new SendTransactionDialogFragment.OnConfirmBtnClickListener() {
                                        @Override
                                        public void onConfirmBtnClick() {
                                            showInputWalletPasswordDialogFragment(gasPrice, feeAmount);
                                        }
                                    })
                                    .show(currentActivity().getSupportFragmentManager(), "sendTransaction");
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
        double gasLimit = SharedWalletTransactionManager.DEPLOY_GAS_LIMIT.doubleValue();
        double gasPrice = price.doubleValue();
        return BigDecimalUtil.div(String.valueOf(BigDecimalUtil.mul(gasPrice, gasLimit)), "1E18");
    }

    private Map<String, String> buildTransactionInfo(String walletName) {
        Map<String, String> map = new LinkedHashMap<>();
        map.put(string(R.string.execute_wallet), walletName);
        map.put(string(R.string.payment_info), string(R.string.fee_of_contract_creation));
        return map;
    }

    private ArrayList<OwnerEntity> getAddressEntityList() {
        ArrayList<OwnerEntity> addressEntityList = new ArrayList<>();
        addressEntityList.add(new OwnerEntity(UUID.randomUUID().toString(), mWalletEntity.getName(), mWalletEntity.getPrefixAddress()));
        for (CreateSharedWalletSecondStepContract.ContractEntity contractEntity : mEntityList) {
            addressEntityList.add(new OwnerEntity(UUID.randomUUID().toString(), contractEntity.getName(), contractEntity.getAddress()));
        }

        return addressEntityList;
    }
}
