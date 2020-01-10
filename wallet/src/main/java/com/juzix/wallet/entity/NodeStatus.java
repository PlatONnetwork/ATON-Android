package com.juzix.wallet.entity;

import android.support.annotation.StringDef;

@StringDef({
        NodeStatus.ACTIVE,
        NodeStatus.CANDIDATE,
        NodeStatus.EXITING,
        NodeStatus.EXITED
})
public @interface NodeStatus {

    /**
     * 活跃中
     */
    String ACTIVE = "Active";
    /**
     * 候选中
     */
    String CANDIDATE = "Candidate";
    /**
     * 退出中
     */
    String EXITING = "Exiting";
    /**
     * 已退出
     */
    String EXITED = "Exited";
}
