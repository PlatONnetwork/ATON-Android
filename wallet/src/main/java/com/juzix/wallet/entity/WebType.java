package com.juzix.wallet.entity;

import android.support.annotation.IntDef;

@IntDef({
        WebType.WEB_TYPE_COMMON,
        WebType.WEB_TYPE_AGREEMENT
})
public @interface WebType {

    int WEB_TYPE_COMMON = 0;

    int WEB_TYPE_AGREEMENT = 1;
}
