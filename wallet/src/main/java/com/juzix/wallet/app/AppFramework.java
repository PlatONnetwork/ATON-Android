package com.juzix.wallet.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.juzhen.framework.network.HttpClient;
import com.juzhen.framework.network.NetConnectivity;
import com.juzhen.framework.network.NetState;
import com.juzhen.framework.network.RequestInfo;
import com.juzhen.framework.util.RUtils;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.config.JZAppConfigure;
import com.juzix.wallet.engine.IndividualWalletManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.SharedWalletManager;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

import io.realm.DynamicRealm;
import io.realm.DynamicRealmObject;
import io.realm.FieldAttribute;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;
import io.realm.RealmObjectSchema;
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
        //初始化节点配置
        NodeManager.getInstance().init();
        //初始化RUtils
        RUtils.init(context);
        //初始化偏好设置
        AppSettings.getInstance().init(context);
        //初始化网络模块
        HttpClient.getInstance().init(mContext, Constants.URL.URL_HTTP_C, buildMultipleUrlMap());
    }

    private Map<String, Object> buildMultipleUrlMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(RequestInfo.URL_IP, Constants.URL.URL_IP_SERVICE);
        return map;
    }

    private void initRealm(Context context) {
        Realm.init(context);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name(Constants.DBName.PORTAL)
                .schemaVersion(Constants.DBName.VERSION)
                .migration(new RealmMigration() {
                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
                        RealmSchema schema = realm.getSchema();
                        if (oldVersion == 104) {

                            schema.get("IndividualTransactionInfoEntity")
                                    .addField("completed", boolean.class)
                                    .addField("value", double.class)
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });

                            schema.get("IndividualWalletInfoEntity")
                                    .addField("mnemonic", String.class)
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });

                            schema.get("OwnerInfoEntity").addField("nodeAddress", String.class);

                            schema.get("NodeInfoEntity")
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.deleteFromRealm();
                                        }
                                    });

                            schema.get("RegionInfoEntity")
                                    .removeField("uuid")
                                    .addPrimaryKey("ip")
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });

                            schema.get("SharedTransactionInfoEntity")
                                    .removeField("transactionResult")
                                    .addField("transactionResult", String.class)
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });

                            schema.get("SharedWalletInfoEntity")
                                    .renameField("walletAddress", "creatorAddress")
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });
                            schema.get("SingleVoteInfoEntity").removeField("avatar");

                            schema.get("SharedWalletOwnerInfoEntity")
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });

                            schema.get("SingleVoteInfoEntity")
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });

                            schema.get("TicketInfoEntity")
                                    .addField("nodeAddress", String.class)
                                    .transform(new RealmObjectSchema.Function() {
                                        @Override
                                        public void apply(DynamicRealmObject obj) {
                                            obj.set("nodeAddress", "https://test-amigo.platon.network/test");
                                        }
                                    });

                            schema.remove("TransactionInfoResult");

                            schema.create("CandidateInfoEntity")
                                    .addField("candidateId", String.class, FieldAttribute.PRIMARY_KEY)
                                    .addField("deposit", String.class)
                                    .addField("blockNumber", long.class)
                                    .addField("owner", String.class)
                                    .addField("txIndex", int.class)
                                    .addField("from", String.class)
                                    .addField("fee", int.class)
                                    .addField("host", String.class)
                                    .addField("port", String.class)
                                    .addField("txHash", String.class)
                                    .addField("extra", String.class)
                                    .addField("nodeAddress", String.class)
                                    .addField("candidateName", String.class);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeChangedEvent(Event.NodeChangedEvent event) {
        //初始化普通钱包
        IndividualWalletManager.getInstance().init();
        //初始化共享钱包
        SharedWalletManager.getInstance().init();
        //初始化网络
        HttpClient.getInstance().init(mContext, event.nodeEntity.getHttpUrl(), buildMultipleUrlMap());
    }

    static class NetStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final NetState state = (NetState) intent.getSerializableExtra(NetConnectivity.EXTRA_NETSTATE);
            EventPublisher.getInstance().sendNetWorkStateChangedEvent(state);
        }
    }


}

