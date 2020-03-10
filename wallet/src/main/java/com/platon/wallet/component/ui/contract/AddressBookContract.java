package com.platon.wallet.component.ui.contract;

import com.platon.wallet.component.ui.base.IPresenter;
import com.platon.wallet.component.ui.base.IView;
import com.platon.wallet.entity.Address;

import java.util.List;

/**
 * @author matrixelement
 */
public class AddressBookContract {

    public interface View extends IView {

        void notifyAddressListChanged(List<Address> addressEntityList);

        void setResult(Address addressEntity);

        String getAction();
    }

    public interface Presenter extends IPresenter<View> {

        void fetchAddressList();

        void deleteAddress(int position);

        void editAddress(int position);

        void selectAddress(int position);

    }
}
