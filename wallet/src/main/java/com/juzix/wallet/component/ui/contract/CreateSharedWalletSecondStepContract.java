package com.juzix.wallet.component.ui.contract;

import com.juzix.wallet.component.ui.base.IPresenter;
import com.juzix.wallet.component.ui.base.IView;
import com.juzix.wallet.entity.IndividualWalletEntity;

import org.web3j.crypto.Credentials;

import java.math.BigInteger;
import java.util.List;

public class CreateSharedWalletSecondStepContract {

    public static class ContractEntity {
        public static final int FOCUS_NONE = 0;
        public static final int FOCUS_NAME = 1;
        public static final int FOCUS_ADDRESS = 2;
        public String name;
        public String address;
        public String errorMsg;
        public boolean enabled;
        public int focus;

        private ContractEntity(Builder builder) {
            setName(builder.name);
            setAddress(builder.address);
            setErrorMsg(builder.errorMsg);
            setEnabled(builder.enabled);
            setFocus(builder.focus);
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getFocus() {
            return focus;
        }

        public void setFocus(int focus) {
            this.focus = focus;
        }

        public static final class Builder {
            private String name;
            private String address;
            private String errorMsg;
            private boolean enabled;
            public int focus;

            public Builder() {
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder address(String address) {
                this.address = address;
                return this;
            }

            public Builder errorMsg(String errorMsg) {
                this.errorMsg = errorMsg;
                return this;
            }

            public Builder enabled(boolean enabled) {
                this.enabled = enabled;
                return this;
            }

            public Builder focus(int focus) {
                this.focus = focus;
                return this;
            }

            public ContractEntity build() {
                return new ContractEntity(this);
            }
        }
    }

    public interface View extends IView {

        int getSharedOwnersFromIntent();

        int getRequiredSignaturesFromIntent();

        String getWalletNameFromIntent();

        IndividualWalletEntity getWalletEntityFromIntent();

        void showWalletInfo(IndividualWalletEntity walletEntity);

        void showOwnerList(List<ContractEntity> ownerEntityList);

        void updateOwner(int position);

        void setCreateSharedWalletBtnEnable(boolean enable);
    }

    public interface Presenter extends IPresenter<View> {

        void init();

        void scanAddress();

        void selectAddress();

        void inputAddress(int position, String address);

        void inputName(int position, String name);

        void verifyAddress(int position);

        void updateAddress(String address);

        void createContract();

        void validPassword(Credentials credentials, BigInteger gasPrice, double feeAmount);

        void focusName(int position);

        void focusAddress(int position);

        boolean needSaveAddressBook(String address);

        boolean saveWallet(String name, String address);
    }
}
