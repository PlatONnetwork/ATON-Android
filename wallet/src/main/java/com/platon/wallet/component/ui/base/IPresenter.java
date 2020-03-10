package com.platon.wallet.component.ui.base;

public interface IPresenter<T extends IView> {

    void attachView(T view);

    void detachView();

    T getView();

    Boolean isViewAttached();
}
