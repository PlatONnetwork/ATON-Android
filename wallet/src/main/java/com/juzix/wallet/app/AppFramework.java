package com.juzix.wallet.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.juzhen.framework.network.NetConnectivity;
import com.juzhen.framework.network.NetState;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.config.JZAppConfigure;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.engine.SystemManager;
import com.juzix.wallet.event.EventPublisher;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * @author matrixelement
 */
public class AppFramework {

    private final static String TAG = AppFramework.class.getSimpleName();

    private static final AppFramework APP_FRAMEWORK = new AppFramework();

    private Context mContext;

    public Context getContext() {
        return mContext;
    }

    private AppFramework() {

    }

    public static AppFramework getAppFramework() {
        return APP_FRAMEWORK;
    }

    public void initAppFramework(Context context) {

        mContext = context;
        JZAppConfigure.getInstance().init(context);
        EventPublisher.getInstance().register(this);
        //注册网络状态变化
        registerNetStateChangedBC();
        //初始化realm
        initRealm(context);
        //初始化RUtils
        RUtils.init(context);
        //初始化偏好设置
        AppSettings.getInstance().init(context);
        //初始化节点配置
        NodeManager.getInstance().init();
        //初始化普通钱包
        IndividualWalletManager.getInstance().init();
        //初始化共享钱包
        SharedWalletManager.getInstance().init();
        //启动计时器
        SystemManager.getInstance().start();
    }

    private void initRealm(Context context) {
        Realm.init(context);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name(Constants.DBName.PORTAL)
                .schemaVersion(Constants.DBName.VERSION)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        if (oldVersion == 100) {
                            // Create a WalletEntity class
                            RealmSchema schema = realm.getSchema();
                            schema.get("AddressInfoEntity").addField("avatar", String.class);
                            oldVersion++;
                        } else if (oldVersion == 101) {
                            RealmSchema schema = realm.getSchema();
                            schema.get("NodeInfoEntity").addField("isMainNetworkNode", boolean.class);
                            oldVersion++;
                        } else if (oldVersion == 102) {
                            RealmSchema schema = realm.getSchema();
                            schema.get("IndividualTransactionInfoEntity").addField("blockNumber", long.class);
                            oldVersion++;
                        }
                    }
                })
                .build());
    }

    /**
     * 注册网络状态变化
     */
    private void registerNetStateChangedBC() {
        IntentFilter intentFilter = new IntentFilter(NetConnectivity.ACITION_CONNECTIVITY_CHANGE);
        mContext.registerReceiver(new NetStateBroadcastReceiver(), intentFilter);
    }

    static class NetStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final NetState state = (NetState) intent.getSerializableExtra(NetConnectivity.EXTRA_NETSTATE);
            EventPublisher.getInstance().sendNetWorkStateChangedEvent(state);
        }
    }


}

