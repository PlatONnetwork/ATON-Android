package com.platon.aton.engine;

import android.support.annotation.Nullable;

import java.util.NoSuchElementException;

/**
 * 为解决RxJava2传递null异常
 *
 * @author ziv
 * @param <T>
 */
public class Optional<T> {

    /**
     * 接收到的返回结果
     */
    private final T data;

    public Optional(@Nullable T data) {
        this.data = data;
    }

    /**
     * 判断返回结果是否为null
     *
     * @return
     */
    public boolean isEmpty() {
        return this.data == null;
    }

    /**
     * 获取不能为null的返回结果，如果为null，直接抛异常，经过二次封装之后，这个异常最终可以在走向RxJava的onError()
     *
     * @return
     */
    public T get() {
        if (data == null) {
            throw new NoSuchElementException("No value present");
        }
        return data;
    }

    /**
     * 获取可以为null的返回结果
     *
     * @return
     */
    public T getIncludeNull() {
        return data;
    }
}