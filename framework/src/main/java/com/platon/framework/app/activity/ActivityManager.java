package com.platon.framework.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Window;
import android.view.WindowManager;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * 活动管理器。把所有活动的实例管理起来，并对外提供各种控制方法。
 * @author ziv
 */
public class ActivityManager {
    private ArrayDeque<Activity> managedActivityList;
    private Activity currActivity;

    private List<ForeGroundObserver> foreGroundObserverList = new ArrayList<ForeGroundObserver>();
    private boolean isForeGround = false;

    private static ActivityManager instance = new ActivityManager();

    private ActivityManager() {
        managedActivityList = new ArrayDeque<Activity>();
    }

    public static ActivityManager getInstance() {
        return instance;
    }

    public void addForeGroundObserver(ForeGroundObserver o) {
        this.foreGroundObserverList.add(o);
    }

    public void removeForeGroundObserver(ForeGroundObserver o) {
        this.foreGroundObserverList.remove(o);
    }

    void addManagedActivity(Activity act) {
        managedActivityList.push(act);
    }

    void removeManagedActivity(Activity act) {
        managedActivityList.remove(act);
    }

    void setCurrActivity(Activity act) {
        currActivity = act;
    }

    void setIsForeGround(boolean isForeGround) {
        this.isForeGround = isForeGround;
        if (isForeGround) {
            // 复制一份出来，否则遍历的过程中也来removeObserver的时候，会有问题。
            List<ForeGroundObserver> tmpList = new ArrayList<ForeGroundObserver>();
            for (ForeGroundObserver o : foreGroundObserverList) {
                tmpList.add(o);
            }

            for (ForeGroundObserver o : tmpList) {
                o.notifyForeGround();
            }
        }
    }

    public boolean isForeGround() {
        return this.isForeGround;
    }

    void setActivityAttribute(Activity act) {
        act.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
        act.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);// 设置默认键盘不弹出
        act.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // 设置设备为竖屏模式
    }

    public Activity getCurrActivity() {
        return this.currActivity;
    }

    // 如果当前活动是ManagedActivity，直接启动活动。
    public void startActivity(Intent intent) {
        currActivity.startActivity(intent);
    }

    public void finishAll() {
        currActivity = null;
        // 把所有的ManagedActivity都关闭
        while (!managedActivityList.isEmpty()) {
            Activity act = managedActivityList.pop();
            act.finish();
        }
    }

    public void backToRoot() {
        // 一直pop ManagedActivity出来，并关闭，直到只剩下1个了。
        // 最后一个就是Root了
        while (managedActivityList.size() > 1) {
            Activity act = managedActivityList.pop();
            act.finish();
        }
    }

    public Activity getRootActivity() {
        if (managedActivityList.isEmpty()) {
            return null;
        }
        return managedActivityList.getLast();
    }

    public boolean isActivityNone() {
        return managedActivityList.isEmpty();
    }

    public interface ForeGroundObserver {
        void notifyForeGround();
    }
}
