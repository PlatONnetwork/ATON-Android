package com.juzix.wallet.entity;

import android.support.annotation.IntDef;

import jnr.ffi.annotations.In;

@IntDef({
        ReportType.DUPLICATE_SIGN
})
public @interface ReportType {

    /**
     * 双签
     */
    int DUPLICATE_SIGN = 1;
}
