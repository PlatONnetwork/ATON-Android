package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.IContext;
import com.platon.aton.entity.Address;

import java.util.List;

/**
 * @author matrixelement
 */
public class SelectAddressContract {

    public interface View extends IContext {

        void notifyAddressListChanged(List<Address> addressEntityList);

        void setResult(Address addressEntity);

        String getAction();
    }

    public interface Presenter extends IPresenter<SelectAddressContract.View> {

        void fetchAddressList();

        void selectAddress(int position);

    }
}
