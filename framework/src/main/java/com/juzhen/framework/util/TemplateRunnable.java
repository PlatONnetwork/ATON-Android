package com.juzhen.framework.util;

public abstract class TemplateRunnable<E> implements Runnable {
	
	E	paramE;
	
	public TemplateRunnable(E e) {
		this.paramE = e;
	}
	
	@Override
	public void run() {
		doRun(this.paramE);
	}
	
	protected abstract void doRun(E e);
	
}
