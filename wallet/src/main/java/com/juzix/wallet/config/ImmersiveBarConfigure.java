package com.juzix.wallet.config;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.View;

import com.juzix.wallet.config.ImmersionBar.BarHide;
import com.juzix.wallet.config.ImmersionBar.ImmersionBar;


public class ImmersiveBarConfigure {


    public static void statusBarView(Activity activity, View view) {
        ImmersionBar.with(activity)
                .statusBarView(view)
                .navigationBarEnable(false)
                .init();
    }

    public static void statusBarView(Fragment fragment, View view) {
        ImmersionBar.with(fragment)
                .statusBarView(view)
                .navigationBarEnable(false)
                .init();
    }


    public static void statusBarColor(Activity activity, int color) {
        ImmersionBar.with(activity)
                .statusBarColor(color)
                .navigationBarEnable(false)
                .init();
    }

    public static void hideBar(Activity activity) {
        ImmersionBar.with(activity)
                .fitsSystemWindows(false)
                .transparentStatusBar()
                .statusBarDarkFont(true, 0.2f)
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                .init();
    }

    public static void destroy(Activity activity) {
        //不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        ImmersionBar.with(activity).destroy();
    }

    public static void statusBarColor(Fragment fragment, int color) {
        ImmersionBar.with(fragment)
                .fitsSystemWindows(true)
                .statusBarColor(color)
                .statusBarDarkFont(true, 0.2f)
                .hideBar(BarHide.FLAG_SHOW_BAR)
                .init();
    }

    public static void hideBar(Fragment fragment) {
        ImmersionBar.with(fragment)
                .fitsSystemWindows(false)
                .transparentStatusBar()
                .statusBarDarkFont(true, 0.2f)
                .hideBar(BarHide.FLAG_HIDE_STATUS_BAR)
                .init();
    }

    public static void destroy(Fragment fragment) {
        //不调用该方法，如果界面bar发生改变，在不关闭app的情况下，退出此界面再进入将记忆最后一次bar改变的状态
        ImmersionBar.with(fragment).destroy();
    }

}
