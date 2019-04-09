package com.juzix.wallet.component.ui.presenter;

import android.text.TextUtils;
import android.view.View;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.AddNewAddressContract;
import com.juzix.wallet.db.entity.AddressInfoEntity;
import com.juzix.wallet.db.sqlite.AddressInfoDao;
import com.juzix.wallet.entity.AddressEntity;
import com.juzix.wallet.utils.JZWalletUtil;

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

    private AddressEntity addressEntity;

    public AddNewAddressPresenter(AddNewAddressContract.View view) {
        super(view);
        addressEntity = view.getAddressFromIntent();
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

            AddressInfoEntity addressInfoEntity = new AddressInfoEntity(UUID.randomUUID().toString(), address, name, avatar);

            Single.fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    if (addressEntity == null) {
                        AddressInfoEntity entity = AddressInfoDao.getInstance().getEntityWithAddress(addressInfoEntity.getAddress());
                        if (entity != null){
                            return AddressInfoDao.getInstance().updateNameWithAddress(addressInfoEntity.getAddress(), addressInfoEntity.getName());
                        }else {
                            return AddressInfoDao.getInstance().insertAddressInfo(addressInfoEntity);
                        }
                    } else {
                        AddressInfoEntity oldAddressInfo = new AddressInfoEntity();
                        oldAddressInfo.setName(addressEntity.getName());
                        oldAddressInfo.setAddress(addressEntity.getAddress());
                        return AddressInfoDao.getInstance().updateAddressInfo(oldAddressInfo, addressInfoEntity);
                    }
                }
            }).compose(((BaseActivity) getView()).bindToLifecycle()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BiConsumer<Boolean, Throwable>() {
                @Override
                public void accept(Boolean aBoolean, Throwable throwable) throws Exception {
                    if (aBoolean.booleanValue()) {
                        getView().setResult(new AddressEntity(addressInfoEntity.getUuid(), addressInfoEntity.getName(), addressInfoEntity.getAddress(), addressInfoEntity.getAvatar()));
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
            if (name.length() > 12) {
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
            //TODO 不是以0x开头的则提示格式错误，之后统一地址显示
            if (!JZWalletUtil.isValidAddress(address) || !address.startsWith("0x")) {
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
        if (isViewAttached()){
            getView().showAddress(text);
        }
    }
}
