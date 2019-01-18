package com.juzix.wallet.component.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

class BaseTracedService extends Service {

    private static class ServiceReference extends WeakReference<Service> {
        public ServiceReference(Service r, ReferenceQueue<? super Service> q) {
            super(r, q);
        }
    }

    /**
     * 垃圾Reference的队列（所引用的对象已经被回收，则将该引用存入队列中）
     */
    static ReferenceQueue<Service> sReferenceQueue = new ReferenceQueue<Service>();
    ;
    static LinkedList<ServiceReference> sStack = new LinkedList<ServiceReference>();

    private ServiceReference mRef;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mRef = new ServiceReference(this, sReferenceQueue);
        sStack.push(mRef);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sStack.remove(mRef);
    }

    private static void clean() {
        ServiceReference ref = null;
        while ((ref = (ServiceReference) sReferenceQueue.poll()) != null) {
            sStack.remove(ref);
        }
    }

    public static void stopAllService() {
        clean();
        Iterator<ServiceReference> it = sStack.iterator();
        while (it.hasNext()) {
            ServiceReference ref = it.next();
            if (ref != null && ref.get() != null) {
                Service service = ref.get();
                service.stopSelf();
            }
        }

        sStack.clear();
    }
}
