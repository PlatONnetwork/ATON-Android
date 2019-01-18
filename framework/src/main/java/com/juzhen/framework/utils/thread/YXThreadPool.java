package com.juzhen.framework.utils.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import android.os.Looper;
import android.util.Log;

public class YXThreadPool {
	private static final int		CORE_POOL_SIZE		= 4;
	private static final int		MAX_POOL_SIZE		= 8;
	private static final int		KEEP_ALIVE_TIME		= 10;						// 10 seconds
																					
	// Resource type
	
	public static final int			MODE_NONE			= 0;
	
	public static final int			MODE_CPU			= 1;
	
	public static final int			MODE_NETWORK		= 2;
	
	public static final JobContext	JOB_CONTEXT_STUB	= new JobContextStub();
	
	ResourceCounter					mCpuCounter			= new ResourceCounter(2);
	ResourceCounter					mNetworkCounter		= new ResourceCounter(2);
	
	// A Job is like a Callable, but it has an addition JobContext parameter.
	public interface Job<T> {
		
		public T run(JobContext jc);
	}
	
	public interface JobContext {
		
		boolean isCancelled();
		
		boolean setMode(int mode);
	}
	
	private static class JobContextStub implements JobContext {
		@Override
		public boolean isCancelled() {
			return false;
		}
		
		@Override
		public boolean setMode(int mode) {
			return true;
		}
	}
	
	private static class ResourceCounter {
		public int	value;
		
		public ResourceCounter(int v) {
			value = v;
		}
	}
	
	public static enum Priority {
		
		LOW(1), //
		
		NORMAL(2), //
		
		HIGH(3);
		
		int	priorityInt;
		
		Priority(int priority) {
			priorityInt = priority;
		}
	}
	
	private final Executor	mExecutor;
	
	public YXThreadPool() {
		this("thread-pool", CORE_POOL_SIZE, MAX_POOL_SIZE);
	}
	
	public YXThreadPool(String name, int coreSize, int maxSize) {
		if (coreSize <= 0)
			coreSize = 1;
		if (maxSize <= coreSize)
			maxSize = coreSize;
		
		mExecutor = new ThreadPoolExecutor(coreSize, maxSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new PriorityBlockingQueue<Runnable>(), new PriorityThreadFactory(name,
				android.os.Process.THREAD_PRIORITY_BACKGROUND));
	}
	
	public YXThreadPool(String name, int coreSize, int maxSize, BlockingQueue<Runnable> queue) {
		if (coreSize <= 0)
			coreSize = 1;
		if (maxSize <= coreSize)
			maxSize = coreSize;
		
		mExecutor = new ThreadPoolExecutor(coreSize, maxSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS, queue, new PriorityThreadFactory(name, android.os.Process.THREAD_PRIORITY_BACKGROUND));
	}
	
	// Submit a job to the thread pool. The listener will be called when the
	// job is finished (or cancelled).
	public <T> YXFuture<T> submit(Job<T> job, FutureListener<T> listener, Priority priority) {
		// handle the priority job.
		Worker<T> w = generateWorker(job, listener, priority);
		mExecutor.execute(w);
		return w;
	}
	
	public <T> YXFuture<T> submit(Job<T> job, FutureListener<T> listener) {
		return submit(job, listener, Priority.NORMAL);
	}
	
	public <T> YXFuture<T> submit(Job<T> job, Priority priority) {
		return submit(job, null, priority);
	}
	
	public <T> YXFuture<T> submit(Job<T> job) {
		return submit(job, null, Priority.NORMAL);
	}
	
	private <T> Worker<T> generateWorker(Job<T> job, FutureListener<T> listener, Priority priority) {
		final Worker<T> worker;
		switch (priority) {
			case LOW:
				worker = new PriorityWorker<T>(job, listener, priority.priorityInt, false);
				break;
			
			case NORMAL:
				worker = new PriorityWorker<T>(job, listener, priority.priorityInt, false);
				break;
			
			case HIGH:
				worker = new PriorityWorker<T>(job, listener, priority.priorityInt, true);
				break;
			
			default:
				worker = new PriorityWorker<T>(job, listener, priority.priorityInt, false);
				break;
		}
		return worker;
	}
	
	private class Worker<T> implements Runnable, YXFuture<T>, JobContext {
		private static final String	TAG	= "Worker";
		private Job<T>				mJob;
		private FutureListener<T>	mListener;
		private CancelListener		mCancelListener;
		private ResourceCounter		mWaitOnResource;
		private volatile boolean	mIsCancelled;
		private boolean				mIsDone;
		private T					mResult;
		private int					mMode;
		
		public Worker(Job<T> job, FutureListener<T> listener) {
			mJob = job;
			mListener = listener;
		}
		
		// This is called by a thread in the thread pool.
		public void run() {
			if (mListener != null)
				mListener.onFutureBegin(this);
			
			T result = null;
			
			// A job is in CPU mode by default. setMode returns false
			// if the job is cancelled.
			if (setMode(MODE_CPU)) {
				try {
					result = mJob.run(this);
				} catch (Throwable ex) {
					Log.w(TAG, "Exception in running a job", ex);
				}
			}
			
			synchronized (this) {
				setMode(MODE_NONE);
				mResult = result;
				mIsDone = true;
				notifyAll();
			}
			if (mListener != null)
				mListener.onFutureDone(this);
		}
		
		// Below are the methods for Future.
		public synchronized void cancel() {
			if (mIsCancelled)
				return;
			mIsCancelled = true;
			if (mWaitOnResource != null) {
				synchronized (mWaitOnResource) {
					mWaitOnResource.notifyAll();
				}
			}
			if (mCancelListener != null) {
				mCancelListener.onCancel();
			}
		}
		
		public boolean isCancelled() {
			return mIsCancelled;
		}
		
		public synchronized boolean isDone() {
			return mIsDone;
		}
		
		public synchronized T get() {
			while (!mIsDone) {
				try {
					wait();
				} catch (Exception ex) {
					Log.w(TAG, "ignore exception", ex);
					// ignore.
				}
			}
			return mResult;
		}
		
		public void waitDone() {
			get();
		}
		
		// Below are the methods for JobContext (only called from the
		// thread running the job)
		public synchronized void setCancelListener(CancelListener listener) {
			mCancelListener = listener;
			if (mIsCancelled && mCancelListener != null) {
				mCancelListener.onCancel();
			}
		}
		
		public boolean setMode(int mode) {
			// Release old resource
			ResourceCounter rc = modeToCounter(mMode);
			if (rc != null)
				releaseResource(rc);
			mMode = MODE_NONE;
			
			// Acquire new resource
			rc = modeToCounter(mode);
			if (rc != null) {
				if (!acquireResource(rc)) {
					return false;
				}
				mMode = mode;
			}
			
			return true;
		}
		
		private ResourceCounter modeToCounter(int mode) {
			if (mode == MODE_CPU) {
				return mCpuCounter;
			} else if (mode == MODE_NETWORK) {
				return mNetworkCounter;
			} else {
				return null;
			}
		}
		
		private boolean acquireResource(ResourceCounter counter) {
			while (true) {
				synchronized (this) {
					if (mIsCancelled) {
						mWaitOnResource = null;
						return false;
					}
					mWaitOnResource = counter;
				}
				
				synchronized (counter) {
					if (counter.value > 0) {
						counter.value--;
						break;
					} else {
						try {
							counter.wait();
						} catch (InterruptedException ex) {
							// ignore.
						}
					}
				}
			}
			
			synchronized (this) {
				mWaitOnResource = null;
			}
			
			return true;
		}
		
		private void releaseResource(ResourceCounter counter) {
			synchronized (counter) {
				counter.value++;
				counter.notifyAll();
			}
		}
	}
	
	static final AtomicLong	SEQ	= new AtomicLong(0);
	
	private class PriorityWorker<T> extends Worker<T> implements Comparable<PriorityWorker> {
		
		/**
		 * the bigger, the prior.
		 */
		private final int		mPriority;
		
		/**
		 * whether filo(with same {@link #mPriority}).
		 */
		private final boolean	mFilo;
		
		/**
		 * seq number.
		 */
		private final long		mSeqNum;
		
		public PriorityWorker(Job<T> job, FutureListener<T> listener, int priority, boolean filo) {
			super(job, listener);
			mPriority = priority;
			mFilo = filo;
			mSeqNum = SEQ.getAndIncrement();
		}
		
		@Override
		public int compareTo(PriorityWorker another) {
			return mPriority > another.mPriority ? -1 : (mPriority < another.mPriority ? 1 : subCompareTo(another));
		}
		
		private int subCompareTo(PriorityWorker another) {
			int result = mSeqNum < another.mSeqNum ? -1 : (mSeqNum > another.mSeqNum ? 1 : 0);
			return mFilo ? -result : result;
		}
	}
	
	// ------------- singleton ---------------------
	
	public static YXThreadPool getInstance() {
		return InstanceHolder.INSTANCE;
	}
	
	private static class InstanceHolder {
		public static final YXThreadPool	INSTANCE	= new YXThreadPool();
	}
	
	public static void runOnNonUIThread(final Runnable runnable) {
		if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
			YXThreadPool.getInstance().submit(new YXThreadPool.Job<Object>() {
				@Override
				public Object run(YXThreadPool.JobContext jc) {
					runnable.run();
					return null;
				}
			});
		} else {
			runnable.run();
		}
	}
}
