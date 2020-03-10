package com.platon.wallet.utils;

import com.platon.wallet.entity.QrCodeType;
import com.platon.wallet.entity.TransactionAuthorizationData;
import com.platon.wallet.entity.TransactionSignatureData;

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
