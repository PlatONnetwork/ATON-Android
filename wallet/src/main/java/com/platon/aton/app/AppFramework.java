package com.platon.aton.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.meituan.android.walle.WalleChannelReader;
import com.platon.aton.BuildConfig;
import com.platon.aton.engine.DeviceManager;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.engine.directory.DirectroyController;
import com.platon.aton.event.Event;
import com.platon.aton.event.EventPublisher;
import com.platon.framework.app.Constants;
import com.platon.framework.network.NetConnectivity;
import com.platon.framework.network.NetState;
import com.platon.framework.utils.LogUtils;
import com.platon.framework.utils.RUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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
        try{
            EventPublisher.getInstance().register(this);
            //注册网络状态变化
            NetConnectivity.getConnectivityManager().registerNetworkStateChange(new NetStateBroadcastReceiver());
            //初始化realm
            initRealm(context);
            //初始化节点配置
            NodeManager.getInstance().init();
            //初始化DeviceManager
            DeviceManager.getInstance().init(context, WalleChannelReader.getChannel(context));
            //初始化RUtils
            RUtils.init(context);
            //初始化Directroy
            DirectroyController.getInstance().init(context);
        }catch (Exception e){
           e.printStackTrace();
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNodeChangedEvent(Event.NodeChangedEvent event) {
        //初始化普通钱包
        WalletManager.getInstance().init();
    }

    private void initRealm(Context context) {
        Realm.init(context);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder()
                .name(Constants.DBName.PORTAL)
                .schemaVersion(Constants.DBName.VERSION)
                .migration(new ATONRealmMigration())
                .build());
    }

    static class NetStateBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final NetState state = (NetState) intent.getSerializableExtra(NetConnectivity.EXTRA_NETSTATE);
            EventPublisher.getInstance().sendNetWorkStateChangedEvent(state);
        }
    }

    static class ATONRealmMigration implements RealmMigration {
        @Override
        public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {

            //只有测试网络钱包迁移
            LogUtils.d("------------BuildConfig.RELEASE_TYPE:" + BuildConfig.RELEASE_TYPE);
            if(!BuildConfig.RELEASE_TYPE.equals("server.typeX")) {//测试网络(贝莱世界)
               return;
            }

            RealmSchema schema = realm.getSchema();
            if (oldVersion == 106) {
                //0.6.2.0-->0.7.5.0
                schema.get("NodeEntity")
                        .addField("chainId", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                            }
                        });

                //修改transactionStatus 从string到int
                schema.get("TransactionEntity")
                        .addField("txReceiptStatus_temp", Integer.class)
                        .addField("nodeId", String.class)
                        .addField("nodeName", String.class)
                        .addField("totalReward", String.class)
                        .addField("unDelegation", String.class)
                        .setRequired("txReceiptStatus_temp", true)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.setInt("txReceiptStatus_temp", Integer.valueOf(obj.getString("txReceiptStatus")));
                            }
                        })
                        .removeField("txReceiptStatus")
                        .renameField("txReceiptStatus_temp", "txReceiptStatus");

                //增加backedUp 0.7.5
                //删除链ID为104的钱包 0.7.4.0
                //链id 103-->94或者103-->96 0.7.4.1
                schema.get("WalletEntity")
                        .addField("backedUp", boolean.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {

                                obj.getDynamicRealm()
                                        .where("WalletEntity")
                                        .findAll()
                                        .setString("mnemonic", null);

                                obj.getDynamicRealm()
                                        .where("WalletEntity")
                                        .equalTo("chainId", "104")
                                        .findAll()
                                        .deleteAllFromRealm();
                            }
                        })
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm()
                                        .where("WalletEntity")
                                        .equalTo("chainId", "103")
                                        .findAll()
                                        .setString("chainId", BuildConfig.ID_MAIN_CHAIN);
                            }
                        });

                //增加VerifyNodeEntity,0.7.5增加isConsensus
                schema.create("VerifyNodeEntity")
                        .addField("nodeId", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("ranking", int.class)
                        .addField("name", String.class)
                        .addField("deposit", String.class)
                        .addField("url", String.class)
                        .addField("ratePA", long.class)
                        .addField("nodeStatus", String.class)
                        .addField("isInit", boolean.class)
                        .addField("isConsensus", boolean.class);

                schema.create("TransactionRecordEntity")
                        .addField("timeStamp", long.class, FieldAttribute.PRIMARY_KEY)
                        .addField("from", String.class)
                        .addField("to", String.class)
                        .addField("value", String.class)
                        .addField("chainId", String.class);

                oldVersion++;

            } else if (oldVersion == 107) {
                //0.7.4.0升级到0.7.5.0,链id 97--->94
                schema.get("WalletEntity")
                        .addField("backedUp", boolean.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm()
                                        .where("WalletEntity")
                                        .equalTo("chainId", "97")
                                        .findAll()
                                        .setString("chainId", BuildConfig.ID_MAIN_CHAIN);
                            }
                        });

                schema.get("NodeEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                            }
                        });

                //0.6.2.0-->0.7.5.0 增加TransactionRecordEntity
                schema.create("TransactionRecordEntity")
                        .addField("timeStamp", long.class, FieldAttribute.PRIMARY_KEY)
                        .addField("from", String.class)
                        .addField("to", String.class)
                        .addField("value", String.class)
                        .addField("chainId", String.class);

                //增加isConsensus
                schema.get("VerifyNodeEntity")
                        .addField("isConsensus", boolean.class);

                //增加三个字段
                schema.get("TransactionEntity")
                        .addField("nodeId", String.class)
                        .addField("nodeName", String.class)
                        .addField("totalReward", String.class)
                        .addField("unDelegation", String.class);

                oldVersion++;

            } else if (oldVersion == 108) {
                //0.7.4.1-->0.7.5.0 增加TransactionRecordEntity
                schema.create("TransactionRecordEntity")
                        .addField("timeStamp", long.class, FieldAttribute.PRIMARY_KEY)
                        .addField("from", String.class)
                        .addField("to", String.class)
                        .addField("value", String.class)
                        .addField("chainId", String.class);

                //增加isConsensus
                schema.get("VerifyNodeEntity")
                        .addField("isConsensus", boolean.class);

                schema.get("NodeEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                            }
                        });

                //0.7.4.0升级到0.7.5.0,链id 96--->94
                schema.get("WalletEntity")
                        .addField("backedUp", boolean.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm()
                                        .where("WalletEntity")
                                        .equalTo("chainId", "96")
                                        .findAll()
                                        .setString("chainId", BuildConfig.ID_MAIN_CHAIN);
                            }
                        });

                //增加三个字段
                schema.get("TransactionEntity")
                        .addField("nodeId", String.class)
                        .addField("nodeName", String.class)
                        .addField("totalReward", String.class)
                        .addField("unDelegation", String.class);

                oldVersion++;
            } else if (oldVersion == 109) {

                //0.7.6到0.8.0  95到94
                schema.get("NodeEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                            }
                        });

                //0.7.4.0升级到0.7.5.0,链id 96--->94
                schema.get("WalletEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm()
                                        .where("WalletEntity")
                                        .equalTo("chainId", "95")
                                        .findAll()
                                        .setString("chainId", BuildConfig.ID_MAIN_CHAIN);
                            }
                        });

                //增加三个字段
                schema.get("TransactionEntity")
                        .addField("nodeId", String.class)
                        .addField("nodeName", String.class)
                        .addField("totalReward", String.class)
                        .addField("unDelegation", String.class);

                oldVersion++;
            } else if (oldVersion == 110) {

                //增加三个字段
                schema.get("TransactionEntity")
                        .addField("nodeId", String.class)
                        .addField("nodeName", String.class)
                        .addField("totalReward", String.class)
                        .addField("unDelegation", String.class);

                oldVersion++;

            }else if(oldVersion == 111){

                schema.get("NodeEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                                LogUtils.d("------------clear NodeEntity Realm success");
                            }
                        });

                //链id 101--->102
                schema.get("WalletEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm()
                                        .where("WalletEntity")
                                        .equalTo("chainId", "101")
                                        .findAll()
                                        .setString("chainId", BuildConfig.ID_MAIN_CHAIN);

                                LogUtils.d("------------update chainId Realm success");
                            }
                        });
                oldVersion++;
            }else{
                //增加一个字段
                schema.get("TransactionEntity")
                        .addField("remark", String.class);

                oldVersion++;
            }

        }

    }
}

