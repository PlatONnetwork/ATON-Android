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
import com.juzix.wallet.engine.WalletManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.event.Event;
import com.juzix.wallet.event.EventPublisher;

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
        NetConnectivity.getConnectivityManager().registerNetworkStateChange(new NetStateBroadcastReceiver());
        //初始化realm
        initRealm(context);
        //初始化节点配置
        NodeManager.getInstance().init();
        //初始化RUtils
        RUtils.init(context);
        //初始化偏好设置
        AppSettings.getInstance().init(context);
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
            RealmSchema schema = realm.getSchema();

            if (oldVersion == 104) {

                schema.get("IndividualTransactionInfoEntity")
                        .addField("completed", boolean.class)
                        .addField("value", double.class)
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("IndividualTransactionInfoEntity").findAll().deleteAllFromRealm();
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

                schema.get("OwnerInfoEntity")
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("OwnerInfoEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("NodeInfoEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeInfoEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("RegionInfoEntity")
                        .removeField("uuid")
                        .addPrimaryKey("ip")
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("RegionInfoEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("SharedTransactionInfoEntity")
                        .removeField("transactionResult")
                        .addField("transactionResult", String.class)
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("SharedTransactionInfoEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("SharedWalletInfoEntity")
                        .renameField("walletAddress", "creatorAddress")
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("SharedWalletInfoEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("SingleVoteInfoEntity")
                        .removeField("avatar")
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("SingleVoteInfoEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("SharedWalletOwnerInfoEntity")
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("SharedWalletOwnerInfoEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("TicketInfoEntity")
                        .addField("nodeAddress", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("TicketInfoEntity").findAll().deleteAllFromRealm();
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
            } else if (oldVersion == 105) {
                //删除联名钱包交易记录
                schema.remove("SharedTransactionInfoEntity");
                //删除联名钱包记录
                schema.remove("SharedWalletInfoEntity");
                //删除联名钱包SharedWalletOwnerInfoEntity
                schema.remove("SharedWalletOwnerInfoEntity");
                //删除节点数据
                schema.remove("CandidateInfoEntity");
                //删除区域信息数据
                schema.remove("RegionInfoEntity");
                //删除投票信息数据
                schema.remove("SingleVoteInfoEntity");
                //刪除票数据库
                schema.remove("TicketInfoEntity");
                //删除联名钱包OwnerInfoEntity
                schema.remove("OwnerInfoEntity");
                //增加普通钱包数据库字段
                schema.get("IndividualTransactionInfoEntity")
                        .removeField("uuid")
                        .addPrimaryKey("hash")
                        .removeField("completed")
                        .removeField("memo")
                        .addField("txType", String.class)
                        .addField("txReceiptStatus", String.class)
                        .addField("chainId", String.class)
                        .addField("actualTxCost", String.class)
                        .addField("txInfo", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.set("chainId", NodeManager.getInstance().getChainId(obj.get("nodeAddress")));
                            }
                        })
                        .removeField("nodeAddress");

                schema.get("IndividualWalletInfoEntity")
                        .addField("chainId", String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.set("chainId", NodeManager.getInstance().getChainId(obj.get("nodeAddress")));
                            }
                        })
                        .removeField("nodeAddress");

                //修改数据库名称
                schema.rename("IndividualTransactionInfoEntity", "TransactionEntity");
                schema.rename("IndividualWalletInfoEntity", "WalletEntity");
                //todo数据库字段修改
                schema.rename("AddressInfoEntity", "AddressEntity");
                schema.rename("NodeInfoEntity", "NodeEntity");

                schema.get("NodeEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                            }
                        });
                oldVersion++;
            } else if (oldVersion == 106) {

                schema.get("NodeEntity")
                        .addField("chainId",String.class)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                            }
                        });

                schema.get("TransactionEntity")
                        .addField("txReceiptStatus_temp", Integer.class).setRequired("txReceiptStatus_temp", true)
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.setInt("txReceiptStatus_temp", Integer.valueOf(obj.getString("txReceiptStatus")));
                            }
                        })
                        .removeField("txReceiptStatus")
                        .renameField("txReceiptStatus_temp", "txReceiptStatus");


                schema.create("VerifyNodeEntity")
                        .addField("nodeId", String.class, FieldAttribute.PRIMARY_KEY)
                        .addField("ranking", Integer.class).setRequired("ranking", true)
                        .addField("name", String.class)
                        .addField("deposit", String.class)
                        .addField("url", String.class)
                        .addField("ratePA", String.class)
                        .addField("nodeStatus", String.class)
                        .addField("isInit", boolean.class);

                schema.create("DelegateDetailEntity")
                        .addField("nodeId", String.class)
                        .addField("address", String.class)
                        .addField("delegationBlockNum", String.class);

                oldVersion++;

//                personSchema
//                        .addField("fullName", String.class, FieldAttribute.REQUIRED)
//                        .transform(new RealmObjectSchema.Function() {
//                            @Override
//                            public void apply(DynamicRealmObject obj) {
//                                obj.set("fullName", obj.getString("firstName") + " " + obj.getString("lastName"));
//                            }
//                        })
//                        .removeField("firstName")
//                        .removeField("lastName");
//                oldVersion++;


            }else if(oldVersion ==107){

                //删除节点地址的表
                schema.get("NodeEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("NodeEntity").findAll().deleteAllFromRealm();
                            }
                        });

                //删除链ID为104的钱包
                schema.get("walletEntity")
                        .transform(new RealmObjectSchema.Function() {
                            @Override
                            public void apply(DynamicRealmObject obj) {
                                obj.getDynamicRealm().where("walletEntity").equalTo("chainId","104").findAll().deleteAllFromRealm();
                            }
                        });

//                schema.get("VerifyNodeEntity")
//                        .addField("ratePA_temp",String.class)
//                        .transform(new RealmObjectSchema.Function() {
//                            @Override
//                            public void apply(DynamicRealmObject obj) {
//                                obj.setString("ratePA_temp",String.valueOf(obj.getInt("ratePA"))); //这里从int又改成string类型，注意，如果改成int类型的话，需要加setRequired(),string不需要，否则就会升级失败
//                            }
//                        })
//                        .removeField("ratePA")
//                        .renameField("ratePA_temp","ratePA");

                    oldVersion++;

            }
        }
    }
}

