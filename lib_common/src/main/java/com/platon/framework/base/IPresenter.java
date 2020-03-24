package com.platon.framework.base;

public interface IPresenter<T extends BaseViewImp> {

    void attachView(T view);

    void detachView();

    T getView();

    boolean isViewAttached();
}
