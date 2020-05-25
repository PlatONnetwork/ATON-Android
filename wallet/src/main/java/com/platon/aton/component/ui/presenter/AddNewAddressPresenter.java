package com.platon.aton.component.ui.presenter;

import android.text.TextUtils;
import android.view.View;

import com.platon.aton.R;
import com.platon.aton.component.ui.contract.AddNewAddressContract;
import com.platon.aton.db.entity.AddressEntity;
import com.platon.aton.db.sqlite.AddressDao;
import com.platon.aton.entity.Address;
import com.platon.aton.utils.JZWalletUtil;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class AddNewAddressPresenter extends BasePresenter<AddNewAddressContract.View> implements AddNewAddressContract.Presenter {

    private Address addressEntity;


    @Override
    public void getIntentData() {
        addressEntity = getView().getAddressFromIntent();
    }

    @Override
    public void loadAddressInfo() {
        if (isViewAttached()) {
            if (addressEntity != null) {
                getView().setAddressInfo(addressEntity);
            }
            getView().setBottonBtnText(addressEntity != null ? string(R.string.save) : string(R.string.add));
        }
    }

    @Override
    public void addAddress() {
        if (isViewAttached()) {

            String name = getView().getName().trim();
            String address = getView().getAddress();

            if (!checkAddressName(name)) {
                return;
            }

            if (!checkAddress(address)) {
                return;
            }


            String[] avatarArray = getContext().getResources().getStringArray(R.array.wallet_avatar);
            String avatar = avatarArray[new Random().nextInt(avatarArray.length)];

            AddressEntity addressInfoEntity = new AddressEntity(UUID.randomUUID().toString(), address, name, avatar);

            Single.fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    if (addressEntity == null) {
                        AddressEntity entity = AddressDao.getEntityWithAddress(addressInfoEntity.getAddress());
                        if (entity != null) {
                            return AddressDao.updateNameWithAddress(addressInfoEntity.getAddress(), addressInfoEntity.getName());
                        } else {
                            return AddressDao.insertAddressInfo(addressInfoEntity);
                        }
                    } else {
                        AddressEntity oldAddressInfo = new AddressEntity();
                        oldAddressInfo.setName(addressEntity.getName());
                        oldAddressInfo.setAddress(addressEntity.getAddress());
                        return AddressDao.updateAddressInfo(oldAddressInfo, addressInfoEntity);
                    }
                }
            }).compose(((BaseActivity) getView()).bindToLifecycle()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BiConsumer<Boolean, Throwable>() {
                @Override
                public void accept(Boolean aBoolean, Throwable throwable) throws Exception {
                    if (aBoolean.booleanValue()) {
                        getView().setResult(new Address(addressInfoEntity.getUuid(), addressInfoEntity.getName(), addressInfoEntity.getAddress(), addressInfoEntity.getAvatar()));
                    }
                }
            });

        }
    }

    @Override
    public boolean checkAddressName(String name) {

        String errMsg = null;

        if (TextUtils.isEmpty(name.trim())) {
            errMsg = string(R.string.address_name_cannot_be_empty);
        } else {
            if (name.length() > 20) {
                errMsg = string(R.string.address_name_length_error);
            }
        }

        if (TextUtils.isEmpty(errMsg)) {
            getView().setNameVisibility(View.GONE);
        } else {
            getView().showNameError(errMsg);
        }

        return TextUtils.isEmpty(errMsg);
    }

    @Override
    public boolean checkAddress(String address) {

        String errMsg = null;

        if (TextUtils.isEmpty(address)) {
            errMsg = string(R.string.address_cannot_be_empty);
        } else {
            if (!JZWalletUtil.isValidAddress(address)) {
                errMsg = string(R.string.address_format_error);
            }
        }

        if (TextUtils.isEmpty(errMsg)) {
            getView().setAddressVisibility(View.GONE);
        } else {
            getView().showAddressError(errMsg);
        }

        return TextUtils.isEmpty(errMsg);

    }

    @Override
    public void validQRCode(String text) {
        if (!JZWalletUtil.isValidAddress(text)) {
            showLongToast(string(R.string.scan_qr_code_failed_tips1));
            return;
        }
        if (isViewAttached()) {
            getView().showAddress(text);
        }
    }
}
