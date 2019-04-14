package com.juzix.wallet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Single;
import rx.Subscriber;
import rx.functions.Func1;

public class Test {

    public static void main(String[] args) {

        Single.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                return new ArrayList<>();
            }
        }).toObservable()
                .flatMap(new Func1<List<String>, Observable<String>>() {
                    @Override
                    public Observable<String> call(List<String> strings) {
                        return Observable.from(strings);
                    }
                }).map(new Func1<String, Integer>() {
            @Override
            public Integer call(String s) {
                return 1;
            }
        })
                .toList()
                .subscribe(new Subscriber<List<Integer>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<Integer> integers) {
                        System.out.println("onNext"+integers);
                    }
                });


    }
}
