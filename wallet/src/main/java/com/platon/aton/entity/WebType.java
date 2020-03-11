package com.platon.aton.entity;

import android.support.annotation.IntDef;

@IntDef({
        WebType.WEB_TYPE_SUPPORT_FEEDBACK,
        WebType.WEB_TYPE_OFFICIAL_COMMUNITY,
        WebType.WEB_TYPE_COMMON,
        WebType.WEB_TYPE_AGREEMENT,
        WebType.WEB_TYPE_NODE_DETAIL
})
public @interface WebType {
    /**
     * 帮助与反馈
     */
    int WEB_TYPE_SUPPORT_FEEDBACK = 0;
    /**
     * 官方社区
     */
    int WEB_TYPE_OFFICIAL_COMMUNITY = 1;
    /**
     * 其他
     */
    int WEB_TYPE_COMMON = 2;
    /**
     * 使用协议
     */
    int WEB_TYPE_AGREEMENT = 3;
    /**
     * 节点详情
     */
    int WEB_TYPE_NODE_DETAIL = 4;
}
