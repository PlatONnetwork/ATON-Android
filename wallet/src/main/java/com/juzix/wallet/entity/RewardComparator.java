//package com.juzix.wallet.entity;
//
//import com.juzhen.framework.util.NumberParserUtils;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//
///**
// * @author matrixelement
// */
//public class RewardComparator implements Comparator<CandidateEntity> {
//
//    private int mVoteNum;
//    private List<Comparator<CandidateEntity>> mComparatorList = new ArrayList<Comparator<CandidateEntity>>();
//
//    public RewardComparator(int voteNum) {
//        mVoteNum = voteNum;
//        mComparatorList.add(new FeeComparator());
//        mComparatorList.add(new VotedNumKComparator());
//        mComparatorList.add(new DepositComparator());
//        mComparatorList.add(new VotedNumComparator());
//        mComparatorList.add(new JoinTimeComparator());
//    }
//
//    @Override
//    public int compare(CandidateEntity o1, CandidateEntity o2) {
//
//        for (Comparator comparator : mComparatorList) {
//            if (comparator.compare(o1, o2) > 0) {
//                return 1;
//            } else if (comparator.compare(o1, o2) < 0) {
//                return -1;
//            }
//        }
//
//        return 0;
//    }
//
//    class FeeComparator implements Comparator<CandidateEntity> {
//
//        @Override
//        public int compare(CandidateEntity o1, CandidateEntity o2) {
//            return Integer.compare(o2.getFee(), o1.getFee());
//        }
//    }
//
//    class VotedNumKComparator implements Comparator<CandidateEntity> {
//
//        @Override
//        public int compare(CandidateEntity o1, CandidateEntity o2) {
//
//            if (o1.getVotedNum() >= mVoteNum) {
//                if (o2.getVotedNum() >= mVoteNum) {
//                    return 0;
//                } else {
//                    return -1;
//                }
//            } else {
//                if (o2.getVotedNum() >= mVoteNum) {
//                    return 1;
//                } else {
//                    return 0;
//                }
//            }
//        }
//    }
//
//
//    class DepositComparator implements Comparator<CandidateEntity> {
//
//        @Override
//        public int compare(CandidateEntity o1, CandidateEntity o2) {
//            return Double.compare(NumberParserUtils.parseDouble(o2.getDeposit()), NumberParserUtils.parseDouble(o1.getDeposit()));
//        }
//    }
//
//    class VotedNumComparator implements Comparator<CandidateEntity> {
//
//        @Override
//        public int compare(CandidateEntity o1, CandidateEntity o2) {
//            return Long.compare(o2.getVotedNum(), o1.getVotedNum());
//        }
//
//    }
//
//    class JoinTimeComparator implements Comparator<CandidateEntity> {
//        @Override
//        public int compare(CandidateEntity o1, CandidateEntity o2) {
//            return Long.compare(o1.getCandidateExtraEntity().getTime(), o2.getCandidateExtraEntity().getTime());
//        }
//    }
//}
