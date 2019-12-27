package com.juzix.wallet.component.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author matrixelement
 */
public class LoopService extends Service {

    public static final String ACTION = "com.juzix.wallet.component.service.LoopService";
    /**
     * 轮询时间
     */
    public static int MLOOP_INTERVAL_SECS = 3500;
    /**
     * 当前服务是否正在执行
     */
    public static boolean isServiceRuning = false;
    /**
     * 定时任务工具类
     */
    public static Timer timer = new Timer();

    private static Context context;

    public LoopService() {
        isServiceRuning = false;
    }

    //-------------------------------使用闹钟执行轮询服务------------------------------------

    /**
     * 启动轮询服务
     */
    public static void startLoopService(Context context) {
        if (context == null) {
            return;
        }
        quitLoopService(context);
        AlarmManager manager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), LoopService.class);
        intent.setAction(LoopService.ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // long triggerAtTime = SystemClock.elapsedRealtime() + 1000;
        /**
         * 闹钟的第一次执行时间，以毫秒为单位，可以自定义时间，不过一般使用当前时间。需要注意的是，本属性与第一个属性（type）密切相关，
         * 如果第一个参数对应的闹钟使用的是相对时间（ELAPSED_REALTIME和ELAPSED_REALTIME_WAKEUP），那么本属性就得使用相对时间（相对于系统启动时间来说），
         *      比如当前时间就表示为：SystemClock.elapsedRealtime()；
         * 如果第一个参数对应的闹钟使用的是绝对时间（RTC、RTC_WAKEUP、POWER_OFF_WAKEUP），那么本属性就得使用绝对时间，
         *      比如当前时间就表示为：System.currentTimeMillis()。
         */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), MLOOP_INTERVAL_SECS, pendingIntent);
    }

    /**
     * 停止轮询服务
     */
    public static void quitLoopService(Context context) {
        if (context == null) {
            return;
        }
        AlarmManager manager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context.getApplicationContext(), LoopService.class);
        intent.setAction(LoopService.ACTION);
        PendingIntent pendingIntent = PendingIntent.getService(context.getApplicationContext(), 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.cancel(pendingIntent);
        // 关闭轮询服务
        context.stopService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 启动轮询拉取消息
        if (!isServiceRuning) {
            startLoop();
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRuning = false;
        timer.cancel();
        timer = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * 启动轮询拉取消息
     */
    private void startLoop() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                isServiceRuning = true;

            }
        }, 0, MLOOP_INTERVAL_SECS);
    }

}
