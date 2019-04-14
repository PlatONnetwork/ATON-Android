package com.juzix.wallet.component.ui;

import com.juzhen.framework.util.NumberParserUtils;
import com.juzix.wallet.entity.CandidateEntity;
import com.juzix.wallet.utils.BigDecimalUtil;

import java.util.Comparator;

/**
 * @author matrixelement
 */

public enum SortType {

    SORTED_BY_DEFAULT {
        @Override
        public Comparator<CandidateEntity> getComparator() {
            return new DefaultComparator();
        }
    }, SORTED_BY_REGION {
        @Override
        public Comparator<CandidateEntity> getComparator() {
            return new RegionComparator();
        }
    }, SORTED_BY_REWARD {
        @Override
        public Comparator<CandidateEntity> getComparator() {
            return new RewardComparator();
        }
    };

    public abstract Comparator<CandidateEntity> getComparator();

    static class RewardComparator implements Comparator<CandidateEntity> {
        @Override
        public int compare(CandidateEntity o1, CandidateEntity o2) {
            int compare = Integer.compare(o2.getFee(), o1.getFee());
            if (compare != 0) {
                return compare;
            }
            compare = Integer.compare(o1.getStatus().ordinal(), o2.getStatus().ordinal());
            if (compare != 0) {
                return compare;
            }
            compare = Double.compare(NumberParserUtils.parseDouble(o2.getDeposit()), NumberParserUtils.parseDouble(o1.getDeposit()));
            if (compare != 0) {
                return compare;
            }
            compare = Long.compare(o2.getVotedNum(), o1.getVotedNum());
            if (compare != 0) {
                return compare;
            }
            return Long.compare(o1.getCandidateExtraEntity().getTime(), o2.getCandidateExtraEntity().getTime());
        }
    }

    static class RegionComparator implements Comparator<CandidateEntity> {
        @Override
        public int compare(CandidateEntity o1, CandidateEntity o2) {
            if (o1.getRegionEntity() == null) {
                return 0;
            }
            if (o2.getRegionEntity() == null) {
                return 1;
            }
            return o1.getRegionEntity().getCountryPinyin().compareToIgnoreCase(o2.getRegionEntity().getCountryPinyin());
        }
    }

    static class DefaultComparator implements Comparator<CandidateEntity> {
        @Override
        public int compare(CandidateEntity o1, CandidateEntity o2) {

            double o1Amount = BigDecimalUtil.add(o1.getDeposit(), BigDecimalUtil.mul(String.valueOf(o1.getVotedNum()), o1.getTicketPrice()).toPlainString());
            double o2Amount = BigDecimalUtil.add(o2.getDeposit(), BigDecimalUtil.mul(String.valueOf(o2.getVotedNum()), o2.getTicketPrice()).toPlainString());

            return Double.compare(o2Amount,o1Amount);
        }
    }
}



