package com.platon.aton.entity;

import android.support.annotation.IntDef;

@IntDef({
        WalletSmoothScrollFromType.from_type_layoutDrawer,
        WalletSmoothScrollFromType.from_type_other
})
public @interface WalletSmoothScrollFromType {

    //从首页侧滑栏触发
    int from_type_layoutDrawer = 1;
    //从其他地方触发
    int from_type_other = 2;

}
