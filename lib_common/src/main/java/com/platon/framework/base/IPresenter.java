package com.platon.framework.base;

public interface IPresenter<T extends IView> {

    void attachView(T view);

    void detachView();

    T getView();

    boolean isViewAttached();
}
