package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SelectAddressContract;
import com.juzix.wallet.db.entity.AddressEntity;
import com.juzix.wallet.db.sqlite.AddressInfoDao;
import com.juzix.wallet.entity.Address;
import com.juzix.wallet.utils.CommonUtil;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class SelectAddressPresenter extends BasePresenter<SelectAddressContract.View> implements SelectAddressContract.Presenter {

    public SelectAddressPresenter(SelectAddressContract.View view) {
        super(view);
    }

    private List<Address> addressEntityList;

    @Override
    public void fetchAddressList() {
        Flowable.fromIterable(AddressInfoDao.getAddressInfoList()).filter(new Predicate<AddressEntity>() {
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
    public void selectAddress(int position) {
        if (addressEntityList != null && addressEntityList.size() > position) {
            if (isViewAttached()) {
                String action = getView().getAction();
                Address addressEntity = addressEntityList.get(position);
                if (Constants.Action.ACTION_GET_ADDRESS.equals(action)) {
                    getView().setResult(addressEntity);
                } else {
                    CommonUtil.copyTextToClipboard(currentActivity(), addressEntity.getAddress());
                }
            }
        }
    }
}
