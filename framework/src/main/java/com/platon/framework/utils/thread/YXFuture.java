package com.platon.framework.utils.thread;

public interface YXFuture<T> {
	
	public void cancel();
	
	public boolean isCancelled();
	
	public boolean isDone();
	
	public T get();
	
	public void waitDone();
	
	void setCancelListener(CancelListener listener);
	
	public static interface CancelListener {
		public void onCancel();
	}
}
