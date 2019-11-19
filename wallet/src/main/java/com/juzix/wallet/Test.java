package com.juzix.wallet;

import com.juzhen.framework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class Test {

    public static void main(String[] args) {

//        ArrayList<String> list1 = new ArrayList<>();
//        list1.add("1");
//        list1.add("2");
//        list1.add("3");
//
//        ArrayList<String> list2 = new ArrayList<>();
//
//        list2.add("4");
//        list2.add("5");
//        list2.add("6");
//
//        System.out.println(list1.retainAll(list2));
//
//        for (int i = 0; i < list1.size(); i++) {
//            System.out.println(list1.get(i));
//        }

        BigDecimal bigDecimal = new BigDecimal("-3.1415926");

        //3.142
        System.out.println(bigDecimal.setScale(3, RoundingMode.UP).toPlainString());
        //3.141
        System.out.println(bigDecimal.setScale(3, RoundingMode.DOWN).toPlainString());
        //3.142
        System.out.println(bigDecimal.setScale(3, RoundingMode.CEILING).toPlainString());
        //3.141
        System.out.println(bigDecimal.setScale(3, RoundingMode.FLOOR).toPlainString());
        //3.142
        System.out.println(bigDecimal.setScale(3, RoundingMode.HALF_UP).toPlainString());
        //3.142
        System.out.println(bigDecimal.setScale(3, RoundingMode.HALF_DOWN).toPlainString());
        //3.142
        System.out.println(bigDecimal.setScale(3, RoundingMode.HALF_EVEN).toPlainString());

    }

}
