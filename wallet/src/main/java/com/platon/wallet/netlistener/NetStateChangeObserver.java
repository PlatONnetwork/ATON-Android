package com.platon.wallet.netlistener;

public interface NetStateChangeObserver {
    void onNetDisconnected();


    void onNetConnected(NetworkType networkType);
}
