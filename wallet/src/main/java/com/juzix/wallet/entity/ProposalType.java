package com.juzix.wallet.entity;

import android.support.annotation.IntDef;

@IntDef({
        ProposalType.UPGRADE_PROPOSAL,
        ProposalType.TEXT_PROPOSAL,
        ProposalType.PARAMETER_PROPOSAL,
        ProposalType.CANCEL_PROPOSAL
})
public @interface ProposalType {

    /**
     * 文本提案
     */
    int TEXT_PROPOSAL = 1;
    /**
     * 升级提案
     */
    int UPGRADE_PROPOSAL = 2;
    /**
     * 参数提案
     */
    int PARAMETER_PROPOSAL = 3;
    /**
     * 取消提案
     */
    int CANCEL_PROPOSAL = 4;
}
