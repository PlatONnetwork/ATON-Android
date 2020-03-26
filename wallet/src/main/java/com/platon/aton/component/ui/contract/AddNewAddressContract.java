package com.platon.aton.component.ui.contract;

import com.platon.aton.entity.Address;
import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

/**
 * @author matrixelement
 */
public class AddNewAddressContract {

    public interface View extends BaseViewImp {

        Address getAddressFromIntent();

        String getName();

        String getAddress();

        void showNameError(String errContent);

        void showAddressError(String errContent);

        void setNameVisibility(int visibility);

        void setAddressVisibility(int visibility);

        void setAddressInfo(Address addressInfo);

        void setResult(Address addressEntity);

        void setBottonBtnText(String text);

        void showAddress(String address);
    }

    public interface Presenter extends IPresenter<View> {

        void loadAddressInfo();

        void addAddress();

        boolean checkAddressName(String name);

        boolean checkAddress(String address);

        void validQRCode(String text);
    }
}
