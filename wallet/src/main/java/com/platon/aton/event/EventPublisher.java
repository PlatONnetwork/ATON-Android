package com.platon.aton.event;

import com.platon.aton.entity.Node;
import com.platon.aton.entity.Transaction;
import com.platon.aton.entity.Wallet;
import com.platon.framework.network.NetState;

public class EventPublisher {

    private static final String TAG = "Portal.EventPublisher";

    private static EventPublisher instance = new EventPublisher();

    private EventPublisher() {
    }

    public static EventPublisher getInstance() {
        return instance;
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

    public void sendNetWorkStateChangedEvent(NetState netState) {
        BusProvider.post(new Event.NetWorkStateChangedEvent(netState));
    }

    public void sendUpdateTransactionEvent(Transaction transaction) {
        BusProvider.post(new Event.UpdateTransactionEvent(transaction));
    }

    public void sendDeleteTransactionEvent(Transaction transaction) {
        BusProvider.post(new Event.DeleteTransactionEvent(transaction));
    }

    public void sendUpdateSelectedWalletEvent(Wallet entity) {
        BusProvider.post(new Event.UpdateSelectedWalletEvent(entity));
    }

    public void sendUpdateWalletListEvent() {
        BusProvider.post(new Event.UpdateWalletListEvent());
    }

    public void sendNodeChangedEvent(Node nodeEntity) {
        BusProvider.post(new Event.NodeChangedEvent(nodeEntity));
    }

    public void sendWalletListOrderChangedEvent() {
        BusProvider.post(new Event.WalletListOrderChangedEvent());
    }

    public void sendUpdateDelegateEvent() {
        BusProvider.post(new Event.UpdateDelegateDetailEvent());
    }

    public void sendUpdateValidatorsDetailEvent() {
        BusProvider.post(new Event.UpdateValidatorsDetailEvent());
    }

    public void sendUpdateDelegateTabEvent() {
        BusProvider.post(new Event.UpdateDelegateTabEvent());
    }

    public void sendUpdateValidatorsTabEvent() {
        BusProvider.post(new Event.UpdateValidatorsTabEvent());
    }

    public void sendTabChangeUpdateValidatorsEvent() {
        BusProvider.post(new Event.UpdateTabChangeEvent());
    }

    public void sendRefreshPageEvent() {
        BusProvider.post(new Event.UpdateRefreshPageEvent());
    }

    public void sendShowMyDelegateGuide() {
        BusProvider.post(new Event.MyDelegateGuide());
    }

    public void sendShowValidatorsGuide() {
        BusProvider.post(new Event.ValidatorsGuide());
    }

    public void sendWalletNumberChangeEvent() {
        BusProvider.post(new Event.WalletNumberChangeEvent());
    }

    public void sendWalletSelectedChangedEvent() {
        BusProvider.post(new Event.WalletSelectedChangedEvent());
    }

    public void sendBackedUpWalletSuccessedEvent(String uuid) {
        BusProvider.post(new Event.BackedUpWalletSuccessedEvent(uuid));
    }
}
