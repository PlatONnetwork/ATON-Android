package com.platon.wallet.entity;

import android.support.annotation.IntDef;

@IntDef({
        VoteOptionType.VOTE_YES,
        VoteOptionType.VOTE_NO,
        VoteOptionType.VOTE_ABSTAIN
})
public @interface VoteOptionType {

    /**
     * 支持
     */
    int VOTE_YES = 1;
    /**
     * 反对
     */
    int VOTE_NO = 2;
    /**
     * 弃权
     */
    int VOTE_ABSTAIN = 3;

}
