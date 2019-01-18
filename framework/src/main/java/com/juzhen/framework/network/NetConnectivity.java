package com.juzhen.framework.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetConnectivity {

	public static final String ACITION_CONNECTIVITY_CHANGE = "com.juzhen.conn.CONNECTIVITY_CHANGE";
	public static final String EXTRA_NETSTATE = "com.juzhen.conn.EXTRA_NETSTATE";
	public static final String TAG = NetConnectivity.class.getSimpleName();
	private static final NetConnectivity CONNECTIVITY_MANAGER = new NetConnectivity();
	/**
	 * 上一次收到物理网络广播的时候，网络的状态。刚开始的时候，为未连接
	 */
	private boolean mLastNetState = false;
	/**
	 * 默认netType
	 */
	private final static int DEFAULT_NETTYPE = -999;
	/**
	 * 上一次收到物理网络广播的时候，网络的类型
	 */
	private int mLastNetType = DEFAULT_NETTYPE;
	/**
	 * 上一次WIFI连接的SSID。edge网络为null
	 */
	private String mLastWifiSSID = null;

	private Context mContext;

	private BroadcastReceiver receiver;

	private NetConnectivity() {
	}

	public static NetConnectivity getConnectivityManager() {
		return CONNECTIVITY_MANAGER;
	}

	public void init(Context ctx) {
		this.mContext = ctx;
		// 注册物理网络监听和设备状态变化监听
		receiver = new InnerBroadcastReceiver();
		IntentFilter intentFilter1 = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
		ctx.registerReceiver(receiver, intentFilter1);
		// 初始化检测网络状态
		mLastNetState = checkNetWork(ctx);
	}

	/**
	 * 接收物理网络变化通知和设备状态变化的类
	 */
	private class InnerBroadcastReceiver extends BroadcastReceiver {
		@SuppressWarnings("unused")
		@Override
		public void onReceive(Context context, Intent intent) {

			if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				@SuppressWarnings("deprecation")
				NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				NetworkInfo otherNetworkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_OTHER_NETWORK_INFO);

				boolean isFailover = intent.getBooleanExtra(ConnectivityManager.EXTRA_IS_FAILOVER, false);
				boolean noConn = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);

				if (otherNetworkInfo != null) {
					// 网络切换要开始了，我们先主动断开。
					dealNetworkStateChange(false);
					// 有网络存在，切换，因此，这个时候，网络当成断开处理。
					mLastNetState = false;
					mLastNetType = DEFAULT_NETTYPE;
					return;
				} else {
					// 先拿到当前的物理网络状态和网络类型，wifi网络还需要拿到SSID
					NetworkInfo currNetworkInfo = getConnectedNetWork(context);
					boolean currNetState = false;
					int currNetType = DEFAULT_NETTYPE;
					String currWifiSSID = null;
					if (currNetworkInfo != null) {
						currNetState = currNetworkInfo.isConnected();
						currNetType = currNetworkInfo.getType();
						if (currNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
							WifiInfo wifiInfo = wifiManager.getConnectionInfo();
							if (wifiInfo != null) {
								currWifiSSID = wifiInfo.getSSID();
							}
						}
					}

					if (currNetState != mLastNetState) {
						// 和上一次相比，物理网络状态变化了，那么肯定需要处理！
						dealNetworkStateChange(currNetState);
					} else {
						// 和上一次相比，物理网络状态没有变化。

						// 检查一下网络类型是否变化了，如果变化了，我们需要重连。
						if (mLastNetState == true) {
							if (currNetType != mLastNetType) {
								// closeAndTry();
							} else if ((currWifiSSID == null && mLastWifiSSID != null)
									|| (currWifiSSID != null && !currWifiSSID.equals(mLastWifiSSID))) {
								// 如果是wifi热点切换了，我们需要重连。
								// closeAndTry();
							}
						}
					}
					// 处理完毕了，记录Last的值。。
					mLastNetState = currNetState;
					mLastNetType = currNetType;
					mLastWifiSSID = currWifiSSID;
					return;
				}
			}
		}
	}

	private void dealNetworkStateChange(boolean currNetState) {

		Intent intent = new Intent(ACITION_CONNECTIVITY_CHANGE);
		intent.putExtra(EXTRA_NETSTATE, currNetState ? NetState.CONNECTED : NetState.NOTCONNECTED);
		mContext.sendBroadcast(intent);
	}

	private boolean checkNetWork(Context ctx) {
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null) {
			return false;
		}
		NetworkInfo[] netinfo = cm.getAllNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		for (int i = 0; i < netinfo.length; i++) {
			if (netinfo[i].isConnected()) {
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
		NetworkInfo[] netinfo = cm.getAllNetworkInfo();
		if (netinfo == null) {
			return null;
		}
		for (NetworkInfo networkInfo : netinfo) {
			if (networkInfo.isConnected()) {
				return networkInfo;
			}
		}
		return null;
	}

	// 获取，网络变化的最后的状态
	public boolean isConnected() {
		// return mLastNetState;
		boolean result = false;
		ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		cm.getActiveNetworkInfo();
		if (cm != null) {
			NetworkInfo networkInfo = cm.getActiveNetworkInfo();
			if (networkInfo != null) {
				result = networkInfo.isConnected();
			}
		}
		return result;
	}
}
