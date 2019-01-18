package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.AddressEntity;

/**
 * @author matrixelement
 */
public class AddNewAddressContract {

    public interface View extends IView {

        AddressEntity getAddressFromIntent();

        String getName();

        String getAddress();

        void showNameError(String errContent);

        void showAddressError(String errContent);

        void setNameVisibility(int visibility);

        void setAddressVisibility(int visibility);

        void setAddressInfo(AddressEntity addressInfo);

        void setResult(AddressEntity addressEntity);

        void setBottonBtnText(String text);

        void setAddNewAddressButtonEnable(boolean enable);

        void showAddress(String address);
    }

    public interface Presenter extends IPresenter<View> {

        void loadAddressInfo();

        void addAddress();

        boolean checkAddressName(String name);

        boolean checkAddress(String address);

        void updateAddNewAddressButtonStatus();

        void validQRCode(String text);
    }
}
