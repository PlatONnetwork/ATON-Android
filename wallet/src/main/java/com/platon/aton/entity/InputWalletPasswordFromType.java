package com.platon.aton.entity;

import android.support.annotation.IntDef;

@IntDef({
        InputWalletPasswordFromType.BACKUPS,
        InputWalletPasswordFromType.TRANSACTION
})
public @interface InputWalletPasswordFromType {

    /**
     * 备份
     */
    int BACKUPS = 1;
    /**
     * 交易
     */
    int TRANSACTION = 2;

}
