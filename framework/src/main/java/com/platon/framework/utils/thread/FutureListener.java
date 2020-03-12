package com.platon.framework.utils.thread;

public interface FutureListener<T> {
	
	public void onFutureBegin(YXFuture<T> future);
	
	public void onFutureDone(YXFuture<T> future);
}
