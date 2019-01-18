package com.juzix.wallet.event;

import com.juzhen.framework.app.log.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * 在EventBus中的观察者通常有四种线程模型，分别是POSTING（默认）、MAIN、BACKGROUND与ASYNC。
 * 
 * POSTING：如果使用事件处理函数指定了线程模型为POSTING，
 * 那么该事件在哪个线程发布出来的，事件处理函数就会在这个线程中运行，也就是说发布事件和接收事件在同一个线程。
 * 在线程模型为PostThread的事件处理函数中尽量避免执行耗时操作，因为它会阻塞事件的传递，甚至有可能会引起ANR。
 * 
 * MAIN：如果使用事件处理函数指定了线程模型为MAIN
 * 那么不论事件是在哪个线程中发布出来的，该事件处理函数都会在UI线程中执行。该方法可以用来更新UI，但是不能处理耗时操作。
 * 
 * BACKGROUND：如果使用事件处理函数指定了线程模型为BACKGROUND，
 * 如果事件本来就是子线程中发布出来的，那么该事件处理函数直接在发布事件的线程中执行,
 * 那么如果事件是在UI线程中发布出来的，那么该事件处理函数就会在一个特定的后台线程中执行，所有分发到该线程上的函数会有序执行。因此禁止耗时操作。
 * 在此事件处理函数中禁止进行UI更新操作。
 * 
 * ASYNC：如果使用事件处理函数指定了线程模型为ASYNC，
 * 那么无论事件在哪个线程发布，该事件处理函数都会在新建的子线程中执行。同样，此事件处理函数中禁止进行UI更新操作。
 * 
 * 
	@Subscribe(threadMode = ThreadMode.POSTING)
	public void onMessageEventPostThread(MessageEvent messageEvent) {
	}
	
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onMessageEventMainThread(MessageEvent messageEvent) {
	}
	
	@Subscribe(threadMode = ThreadMode.BACKGROUND)
	public void onMessageEventBackgroundThread(MessageEvent messageEvent) {
	}
	
	@Subscribe(threadMode = ThreadMode.ASYNC)
	public void onMessageEventAsync(MessageEvent messageEvent) {
	}
 */
public final class BusProvider {

	private static final String TAG = "BusProvider";

	private static EventBus getInstance() {
		return BusProviderHodler.BUS;
	}

	private static class BusProviderHodler {
		private static final EventBus BUS = EventBus.getDefault();
	}

	private BusProvider() {
	}

	public static void unRegister(Object obj) {
		try {
			getInstance().unregister(obj);
		} catch (Exception e) {
			Log.debug(TAG, e.getMessage());
		}

	}

	public static void register(Object obj) {
		try {
			getInstance().register(obj);
		} catch (Exception e) {
			Log.debug(TAG, e.getMessage());
		}

	}
	
	public static void post(Object event) {
		try {
			getInstance().post(event);
		} catch (Exception e) {
			Log.debug(TAG, e.getMessage());
		}
	}
	
	public static void postSticky(Object event) {
		try {
			getInstance().postSticky(event);
		} catch (Exception e) {
			Log.debug(TAG, e.getMessage());
		}
	}
	
}
