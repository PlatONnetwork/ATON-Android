package com.juzix.wallet.engine;

import com.juzix.wallet.R;
import com.juzix.wallet.component.ui.base.BaseActivity;
import com.juzix.wallet.component.ui.view.AddNewAddressActivity;
import com.juzix.wallet.component.ui.view.ImportIndividualWalletActivity;
import com.juzix.wallet.component.ui.view.SendIndividualTransationActivity;
import com.juzix.wallet.entity.IndividualWalletEntity;
import com.juzix.wallet.utils.JZWalletUtil;

import java.util.ArrayList;

public class QRCodeParser {
    public static void parseMainQRCode(BaseActivity activity, String data) {
        if (JZWalletUtil.isValidAddress(data)) {
            if (IndividualWalletManager.getInstance().getWalletList().isEmpty()){
                AddNewAddressActivity.actionStartWithAddress(activity, data);
            }else {
                ArrayList<IndividualWalletEntity> walletEntities = IndividualWalletManager.getInstance().getWalletList();
                IndividualWalletEntity entity = null;
                for (int i = 0; i < walletEntities.size(); i++){
                    if (walletEntities.get(i).getBalance() > 0){
                        entity = walletEntities.get(i);
                        break;
                    }
                }
                if (walletEntities.isEmpty() || entity == null){
                    AddNewAddressActivity.actionStartWithAddress(activity, data);
                }else {
                    SendIndividualTransationActivity.actionStart(activity, data, entity);
                }
            }
            return;
        }
        if (JZWalletUtil.isValidKeystore(data)) {
            ImportIndividualWalletActivity.actionStart(activity, 0, data);
            return;
        }
        if (JZWalletUtil.isValidPrivateKey(data)) {
            ImportIndividualWalletActivity.actionStart(activity, 2, data);
            return;
        }
        if (JZWalletUtil.isValidMnemonic(data)) {
            ImportIndividualWalletActivity.actionStart(activity, 1, data);
            return;
        }
        activity.showLongToast(activity.string(R.string.unrecognized));
    }

    public static void parseImportQRCode(BaseActivity activity, String code) {
        if (JZWalletUtil.isValidKeystore(code)){
            ImportIndividualWalletActivity.actionStart(activity,0, code);
            return;
        }
        if (JZWalletUtil.isValidPrivateKey(code)){
            ImportIndividualWalletActivity.actionStart(activity,2, code);
            return;
        }
        if (JZWalletUtil.isValidMnemonic(code)){
            ImportIndividualWalletActivity.actionStart(activity,1, code);
            return;
        }
        activity.showLongToast(activity.string(R.string.unrecognized));
    }

    private static void getTotalAssets() {

    }
}
