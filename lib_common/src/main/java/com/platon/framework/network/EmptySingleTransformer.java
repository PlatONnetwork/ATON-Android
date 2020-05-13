package com.platon.framework.network;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.functions.Consumer;

/**
 * Created by Villey on 2017/8/4.
 */

public class EmptySingleTransformer<U> implements SingleTransformer<U, U> {

    public static <U> EmptySingleTransformer<U> get() {
        return new EmptySingleTransformer<U>();
    }

    @Override
    public SingleSource<U> apply(Single<U> upstream) {
        return upstream.doOnSuccess(new Consumer<U>() {
            @Override
            public void accept(U u) throws Exception {

            }
        });
    }
}
