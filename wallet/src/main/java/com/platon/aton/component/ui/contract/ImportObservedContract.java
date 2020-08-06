package com.platon.aton.component.ui.contract;

import com.platon.framework.base.BaseViewImp;
import com.platon.framework.base.IPresenter;

public class ImportObservedContract {

    public interface View extends BaseViewImp {

        String getDataFromIntent();

        void showQRCode(String QRCode);

        void enableImportObservedWallet(boolean isCan);

        String getWalletAddress();

        void enablePaste(boolean enabled);

        void showWalletNumber(int walletNum);

    }


    public interface Presenter extends IPresenter<View> {

        void init();

        void parseQRCode(String QRCode);

        void IsImportObservedWallet(String content, boolean isEnableCreate);

        void importWalletAddress(String walletAddress);

        void checkPaste();

        void loadDBWalletNumber();

    }
}
