package com.juzix.wallet.app;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author matrixelement
 */
public class SchedulersTransformer implements SingleTransformer {

    @Override
    public SingleSource apply(Single upstream) {
        return upstream.subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}
