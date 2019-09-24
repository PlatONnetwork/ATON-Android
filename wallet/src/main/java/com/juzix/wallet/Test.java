package com.juzix.wallet;

import com.juzix.wallet.utils.NumericUtil;

import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.utils.Numeric;

import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class Test {

    public static void main(String[] args) {


//        List<String> list = new ArrayList<>();
//
//        for (String string : list) {
//            System.out.println(string);
//        }

        Uint32 uint32 = new Uint32(1794);

        System.out.println(uint32.getValue().longValue());

        List<String> list = byteArrayToList(uint32.getValue().toByteArray());

        for (String b:list){
            System.out.println(b);
        }

//        Flowable
//                .interval(1, TimeUnit.SECONDS)
//                .doOnNext(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
////                        System.out.println(aLong);
//                    }
//                })
//                .takeUntil(new Predicate<Long>() {
//                    @Override
//                    public boolean test(Long aLong) throws Exception {
//                        return aLong == 5;
//                    }
//                })
//                .filter(new Predicate<Long>() {
//                    @Override
//                    public boolean test(Long aLong) throws Exception {
//                        return aLong == 5;
//                    }
//                })
//                .subscribe(new Consumer<Long>() {
//                    @Override
//                    public void accept(Long aLong) throws Exception {
//                        System.out.println(aLong);
//                    }
//                });
//
//
//        try {
//            Thread.sleep(1000000000000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

    }

    private static List<String> byteArrayToList(byte[] bytes) {

        List<String> list = new ArrayList<>();

        for (byte b : bytes) {
            list.add(String.valueOf(b));
        }
        return list;
    }

    private static int getValue() {
        return Observable.fromArray(1, 2, 3)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer > 3;
                    }
                })
                .firstElement()
                .switchIfEmpty(new SingleSource<Integer>() {
                    @Override
                    public void subscribe(SingleObserver<? super Integer> observer) {
                        observer.onError(new Throwable());
                    }
                })
                .onErrorReturnItem(null)
                .blockingGet();
    }
}
