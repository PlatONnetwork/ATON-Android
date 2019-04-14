package com.juzix.wallet.component.ui.presenter;

import com.juzix.wallet.app.Constants;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.base.BasePresenter;
import com.juzix.wallet.component.ui.contract.SelectAddressContract;
import com.juzix.wallet.db.entity.AddressInfoEntity;
import com.juzix.wallet.db.sqlite.AddressInfoDao;
import com.juzix.wallet.entity.AddressEntity;
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

    private List<AddressEntity> addressEntityList;

    @Override
    public void fetchAddressList() {
        Flowable.fromIterable(AddressInfoDao.getAddressInfoList()).filter(new Predicate<AddressInfoEntity>() {
            @Override
            public boolean test(AddressInfoEntity addressInfoEntity) throws Exception {
                return addressInfoEntity != null;
            }
        }).compose(((BaseActivity) getView()).bindToLifecycle()).map(new Function<AddressInfoEntity, AddressEntity>() {
            @Override
            public AddressEntity apply(AddressInfoEntity addressInfoEntity) throws Exception {
                return new AddressEntity(addressInfoEntity.getUuid(), addressInfoEntity.getName(),addressInfoEntity.getAddress(),addressInfoEntity.getAvatar());
            }
        }).toList().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BiConsumer<List<AddressEntity>, Throwable>() {
            @Override
            public void accept(List<AddressEntity> addressEntities, Throwable throwable) throws Exception {
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
                AddressEntity addressEntity = addressEntityList.get(position);
                if (Constants.Action.ACTION_GET_ADDRESS.equals(action)) {
                    getView().setResult(addressEntity);
                } else {
                    CommonUtil.copyTextToClipboard(currentActivity(), addressEntity.getAddress());
                }
            }
        }
    }
}
