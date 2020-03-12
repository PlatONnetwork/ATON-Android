package com.platon.aton.app;



import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;

public abstract class CustomObserver<T> extends AtomicReference<Disposable> implements Observer<T>, Disposable {

    boolean done;

    @Override
    public void onSubscribe(Disposable d) {
        if (d == null) {
            RxJavaPlugins.onError(new NullPointerException("disposable is null"));
            return;
        }
        DisposableHelper.setOnce(this, d);
    }

    @Override
    public void onNext(T t) {
        if (done) {
            return;
        }
        try {
            accept(t);
        } catch (Exception e) {
            onError(e);
        }
    }

    @Override
    public void onError(Throwable e) {
        if (done) {
            RxJavaPlugins.onError(e);
            return;
        }
        accept(e);
    }

    @Override
    public void onComplete() {
        if (done) {
            return;
        }
        done = true;
    }

    @Override
    public void dispose() {
        DisposableHelper.dispose(this);
    }

    @Override
    public boolean isDisposed() {
        return get() == DisposableHelper.DISPOSED;
    }

    public abstract void accept(T t);

    public void accept(Throwable throwable) {

    }
}
