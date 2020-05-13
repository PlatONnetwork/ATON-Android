package com.platon.aton.component.ui.presenter;

import android.support.annotation.StringDef;

/**
 * @author ziv
 */
@StringDef({
        Direction.DIRECTION_OLD,
        Direction.DIRECTION_NEW
})
public @interface Direction {

    String DIRECTION_OLD = "old";

    String DIRECTION_NEW = "new";
}
