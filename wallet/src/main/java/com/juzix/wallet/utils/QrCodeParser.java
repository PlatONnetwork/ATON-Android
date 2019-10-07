package com.juzix.wallet.utils;

import android.view.View;

import com.juzix.wallet.component.adapter.TabAdapter;
import com.juzix.wallet.component.ui.view.AddNewAddressActivity;
import com.juzix.wallet.component.ui.view.ImportWalletActivity;
import com.juzix.wallet.component.ui.view.MainActivity;
import com.juzix.wallet.component.ui.view.TransactionAuthorizationDetailActivity;
import com.juzix.wallet.entity.QrCodeType;
import com.juzix.wallet.entity.TransactionAuthorizationData;
import com.juzix.wallet.entity.TransactionSignatureData;

public class QrCodeParser {

    public static @QrCodeType
    int parseQrCode(String data) {

        TransactionAuthorizationData transactionAuthorizationData = JSONUtil.parseObject(data, TransactionAuthorizationData.class);

        if (transactionAuthorizationData != null && transactionAuthorizationData.getTransactionAuthorizationDetail() != null) {
            return QrCodeType.TRANSACTION_AUTHORIZATION;
        }

        TransactionSignatureData transactionSignatureData = JSONUtil.parseObject(data, TransactionSignatureData.class);

        if (transactionSignatureData != null && transactionSignatureData.getSignedDatas() != null && !transactionSignatureData.getSignedDatas().isEmpty()) {
            return QrCodeType.TRANSACTION_SIGNATURE;
        }

        if (JZWalletUtil.isValidAddress(data)) {
            return QrCodeType.WALLET_ADDRESS;
        }
        if (JZWalletUtil.isValidKeystore(data)) {
            return QrCodeType.WALLET_KEYSTORE;
        }
        if (JZWalletUtil.isValidMnemonic(data)) {
            return QrCodeType.WALLET_MNEMONIC;
        }
        if (JZWalletUtil.isValidPrivateKey(data)) {
            return QrCodeType.WALLET_PRIVATEKEY;
        }
        return QrCodeType.NONE;
    }


}
