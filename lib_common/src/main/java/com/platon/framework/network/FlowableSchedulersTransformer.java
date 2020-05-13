package com.platon.framework.network;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class FlowableSchedulersTransformer implements FlowableTransformer {

    @Override
    public Publisher apply(Flowable upstream) {
        return upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
