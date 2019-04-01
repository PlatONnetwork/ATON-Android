package com.juzix.wallet.app;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;

/**
 * @author matrixelement
 */
public class ClickTransformer implements ObservableTransformer {
    @Override
    public ObservableSource apply(Observable upstream) {
        return upstream
                .throttleFirst(500, TimeUnit.MILLISECONDS);
    }
}
