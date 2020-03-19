package com.platon.aton.component.ui.contract;

import com.platon.aton.component.ui.base.IPresenter;
import com.platon.aton.component.ui.base.IView;
import com.platon.aton.entity.Wallet;

import java.util.ArrayList;

public class VerificationMnemonicContract {

    public static class DataEntity {
        private boolean checked;
        private String  mnemonic;

        private DataEntity(Builder builder) {
            setChecked(builder.checked);
            setMnemonic(builder.mnemonic);
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        public String getMnemonic() {
            return mnemonic;
        }

        public void setMnemonic(String mnemonic) {
            this.mnemonic = mnemonic;
        }

        public boolean isChecked() {
            return checked;
        }


        public static final class Builder {
            private boolean checked;
            private String  mnemonic;

            public Builder() {
            }

            public Builder checked(boolean val) {
                checked = val;
                return this;
            }

            public Builder mnemonic(String val) {
                mnemonic = val;
                return this;
            }

            public DataEntity build() {
                return new DataEntity(this);
            }
        }
    }

    public interface View extends IView {

        void showTopList(DataEntity[] list);

        void showBottomList(ArrayList<DataEntity> list);

        void showDisclaimerDialog();

        void showBackupFailedDialog();

        String getPasswordFromIntent();

        Wallet getWalletFromIntent();

        void setCompletedBtnEnable(boolean enable);

        void setClearBtnEnable(boolean enable);
    }

    public interface Presenter extends IPresenter<View> {
        void init();

        void checkTopListItem(int index);

        void checkBottomListItem(int index);

//        void uncheckItem(int index);

        void emptyChecked();

        void submit();
    }
}
