//package com.juzix.wallet.entity;
//
//import android.text.TextUtils;
//
//import java.util.Comparator;
//
///**
// * @author matrixelement
// */
//public class RegionComparator implements Comparator<CandidateEntity> {
//
//    @Override
//    public int compare(CandidateEntity o1, CandidateEntity o2) {
//        if (!TextUtils.isEmpty(o1.getHost())) {
//            if (!TextUtils.isEmpty(o2.getHost())) {
//                return o1.getHost().compareToIgnoreCase(o2.getHost()) > 0 ? -1 : 1;
//            } else {
//                return 1;
//            }
//        } else {
//            if (!TextUtils.isEmpty(o2.getHost())) {
//                return -1;
//            } else {
//                return 0;
//            }
//        }
//    }
//}
