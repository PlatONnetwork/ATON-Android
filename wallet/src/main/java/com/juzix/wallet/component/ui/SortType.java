package com.juzix.wallet.component.ui;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.App;
import com.juzix.wallet.R;
import com.juzix.wallet.entity.Candidate;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.utils.LanguageUtil;

import java.util.Comparator;
import java.util.Locale;

/**
 * @author matrixelement
 */

public enum SortType {

    SORTED_BY_NODE_RANKINGS {
        @Override
        public Comparator<VerifyNode> getComparator() {
            return new NodeRankingsComparator();
        }

        @Override
        public int getTextRes() {
            return R.string.msg_node_rankings;
        }

    }, SORTED_BY_DELEGATED_AMOUNT {
        @Override
        public Comparator<VerifyNode> getComparator() {
            return new DelegatedAmountComparator();
        }

        @Override
        public int getTextRes() {
            return R.string.msg_delegated_amount;
        }

    }, SORTED_BY_DELEGATOR_NUMBER {
        @Override
        public Comparator<VerifyNode> getComparator() {
            return new DelegatorNumberComparator();
        }

        @Override
        public int getTextRes() {
            return R.string.msg_delegator_number;
        }

    }, SORTED_BY_ANNUAL_YIELD {
        @Override
        public Comparator<VerifyNode> getComparator() {
            return new AnnualYieldComparator();
        }

        @Override
        public int getTextRes() {
            return R.string.msg_annual_yield;
        }
    };

    public abstract Comparator<VerifyNode> getComparator();

    public abstract int getTextRes();

    static class NodeRankingsComparator implements Comparator<VerifyNode> {

        @Override
        public int compare(VerifyNode o1, VerifyNode o2) {
            return Long.compare(NumberParserUtils.parseLong(o1.getRanking()), NumberParserUtils.parseLong(o2.getRanking()));
        }
    }

    static class DelegatedAmountComparator implements Comparator<VerifyNode> {

        @Override
        public int compare(VerifyNode o1, VerifyNode o2) {
            return Long.compare(NumberParserUtils.parseLong(o2.getDelegateSum()), NumberParserUtils.parseLong(o1.getDelegateSum()));
        }
    }

    static class DelegatorNumberComparator implements Comparator<VerifyNode> {

        @Override
        public int compare(VerifyNode o1, VerifyNode o2) {
            return Long.compare(NumberParserUtils.parseLong(o2.getDelegate()), NumberParserUtils.parseLong(o1.getDelegate()));
        }
    }

    static class AnnualYieldComparator implements Comparator<VerifyNode> {

        @Override
        public int compare(VerifyNode o1, VerifyNode o2) {
            return Long.compare(NumberParserUtils.parseLong(o2.getDelegatedRatePA()), NumberParserUtils.parseLong(o1.getDelegatedRatePA()));
        }
    }
}



