package com.platon.aton.netlistener;

public interface NetStateChangeObserver {
    void onNetDisconnected();


    void onNetConnected(NetworkType networkType);
}
