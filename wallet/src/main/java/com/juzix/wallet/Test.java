package com.juzix.wallet;

import io.reactivex.Observable;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public class Test {

    public static void main(String[] args) {

        System.out.println(getValue());
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
