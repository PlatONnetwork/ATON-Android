package com.platon.framework.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * @author ziv
 */
public class NetConnectivity {

    public static final String TAG = NetConnectivity.class.getSimpleName();
    public static final String ACITION_CONNECTIVITY_CHANGE = "com.juzhen.framework.conn.CONNECTIVITY_CHANGE";
    public static final String EXTRA_NETSTATE = "com.juzhen.framework.conn.EXTRA_NETSTATE";
    private static final NetConnectivity CONNECTIVITY_MANAGER = new NetConnectivity();

    private Context mContext;
    private BroadcastReceiver mReceiver;
    private LocalBroadcastManager mLocalBroadcastManager;

    private NetConnectivity() {

    }

    public static NetConnectivity getConnectivityManager() {
        return CONNECTIVITY_MANAGER;
    }

    public void init(Context context) {
        mContext = context;
        // 注册物理网络监听和设备状态变化监听
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(context);
        mReceiver = new InnerBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        context.registerReceiver(mReceiver, intentFilter);
    }

    public void registerNetworkStateChange(BroadcastReceiver receiver) {
        mLocalBroadcastManager.registerReceiver(receiver, new IntentFilter(NetConnectivity.ACITION_CONNECTIVITY_CHANGE));
    }

    private void sendNetworkStateChange(NetState state) {
        Intent intent = new Intent(ACITION_CONNECTIVITY_CHANGE);
        intent.putExtra(EXTRA_NETSTATE, state);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    private boolean checkNetWork(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        if (netInfo == null) {
            return false;
        }
        for (int i = 0; i < netInfo.length; i++) {
            if (netInfo[i].isConnected()) {
                return true;
            }
        }
        return false;
    }

    private NetworkInfo getConnectedNetWork(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return null;
        }
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        if (netInfo == null) {
            return null;
        }
        for (NetworkInfo networkInfo : netInfo) {
            if (networkInfo.isConnected()) {
                return networkInfo;
            }
        }
        return null;
    }

    /**
     * 获取，网络变化的最后的状态
     *
     * @return
     */
    public boolean isConnected() {
        boolean result = false;
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.getActiveNetworkInfo();
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null) {
                return networkInfo.isConnected();
            }
        }
        return result;
    }

    /**
     * 接收物理网络变化通知和设备状态变化的类
     */
    private class InnerBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                final NetworkInfo info = cm.getActiveNetworkInfo();
                boolean connected = info != null && info.isConnected();
                Log.e(TAG, connected ? "网络连接" : "网络断开");
                if (connected) {
                    sendNetworkStateChange(NetState.CONNECTED);
                } else {
                    sendNetworkStateChange(NetState.NOTCONNECTED);
                }

            }
        }
    }
}
