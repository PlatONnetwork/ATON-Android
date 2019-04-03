package com.juzix.wallet.event;

import android.content.Context;

import com.juzhen.framework.network.NetState;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.WalletEntity;

public class EventPublisher {

    private static final String TAG = "Portal.EventPublisher";

    public static final String ACTION_LOGIN = "com.juzix.wallet.ACTION_LOGIN";

    private Context context;

    private static EventPublisher instance = new EventPublisher();

    private EventPublisher() {
    }

    public static EventPublisher getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.context = context;
    }

    public static String getTag() {
        return TAG;
    }

    public void register(Object obj) {
        BusProvider.register(obj);
    }

    public void unRegister(Object obj) {
        BusProvider.unRegister(obj);
    }

    public void sendIndividualTransactionSucceedEvent() {
        BusProvider.post(new Event.IndividualTransactionSucceedEvent());
    }

    public void sendSharedTransactionSucceedEvent() {
        BusProvider.post(new Event.SharedTransactionSucceedEvent());
    }

    public void sendNodeChangedEvent() {
        BusProvider.post(new Event.NodeChangedEvent());
    }

    public void sendNetWorkStateChangedEvent(NetState netState) {
        BusProvider.post(new Event.NetWorkStateChangedEvent(netState));
    }

    public void sendUpdateSharedWalletTransactionEvent() {
        BusProvider.post(new Event.UpdateSharedWalletTransactionEvent());
    }

    public void sendUpdateSharedWalletBlanceEvent(String prefixContractAddress, double balance) {
        BusProvider.post(new Event.UpdateSharedWalletBalanceEvent(prefixContractAddress, balance));
    }

    public void sendUpdateIndividualWalletTransactionEvent() {
        BusProvider.post(new Event.UpdateIndividualWalletTransactionEvent());
    }

    public void sendUpdateIndividualWalletBlanceEvent() {
        BusProvider.post(new Event.UpdateIndividualWalletBalanceEvent());
    }

    public void sendUpdateMessageTipsEvent(boolean unRead) {
        BusProvider.post(new Event.UpdateMessageTipsEvent(unRead));
    }

    public void sendUpdateCreateJointWalletProgressEvent(SharedWalletEntity sharedWalletEntity) {
        BusProvider.post(new Event.UpdateCreateJointWalletProgressEvent(sharedWalletEntity));
    }

    public void sendUpdateCandidateRegionInfoEvent(RegionEntity regionEntity) {
        BusProvider.post(new Event.UpdateCandidateRegionInfoEvent(regionEntity));
    }

    public void sendUpdateSelectedWalletEvent(WalletEntity entity) {
        BusProvider.post(new Event.UpdateSelectedWalletEvent(entity));
    }
}
