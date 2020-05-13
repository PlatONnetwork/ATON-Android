package com.platon.aton.component.ui.presenter;

import com.platon.aton.component.ui.contract.AddressBookContract;
import com.platon.aton.component.ui.view.AddNewAddressActivity;
import com.platon.aton.db.entity.AddressEntity;
import com.platon.aton.db.sqlite.AddressDao;
import com.platon.aton.entity.Address;
import com.platon.aton.utils.CommonUtil;
import com.platon.framework.base.BaseActivity;
import com.platon.framework.base.BasePresenter;

import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class AddressBookPresenter extends BasePresenter<AddressBookContract.View> implements AddressBookContract.Presenter {

    private List<Address> addressEntityList;

    @Override
    public void fetchAddressList() {
        Flowable.fromIterable(AddressDao.getAddressInfoList()).filter(new Predicate<AddressEntity>() {
            @Override
            public boolean test(AddressEntity addressInfoEntity) throws Exception {
                return addressInfoEntity != null;
            }
        }).compose(((BaseActivity) getView()).bindToLifecycle()).map(new Function<AddressEntity, Address>() {
            @Override
            public Address apply(AddressEntity addressInfoEntity) throws Exception {
                return new Address(addressInfoEntity.getUuid(), addressInfoEntity.getName(),addressInfoEntity.getAddress(),addressInfoEntity.getAvatar());
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BiConsumer<List<Address>, Throwable>() {
            @Override
            public void accept(List<Address> addressEntities, Throwable throwable) throws Exception {
                addressEntityList = addressEntities;
                if (isViewAttached()) {
                    getView().notifyAddressListChanged(addressEntities);
                }
            }
        });
    }

    @Override
    public void deleteAddress(int position) {

        Single.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                if (addressEntityList != null && addressEntityList.size() > position) {
                    return AddressDao.deleteAddressInfo(addressEntityList.get(position).getAddress());
                } else {
                    return false;
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean.booleanValue()) {
                    fetchAddressList();
                }
            }
        });
    }

    @Override
    public void editAddress(int position) {
        if (addressEntityList != null && addressEntityList.size() > position) {
            AddNewAddressActivity.actionStartWithExtraForResult(currentActivity(), addressEntityList.get(position));
        }
    }

    @Override
    public void selectAddress(int position) {
        if (addressEntityList != null && addressEntityList.size() > position) {
            CommonUtil.copyTextToClipboard(currentActivity(), addressEntityList.get(position).getAddress());
        }
    }
}
