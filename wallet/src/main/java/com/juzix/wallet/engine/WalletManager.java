package com.juzix.wallet.engine;

import com.juzix.wallet.entity.WalletEntity;
import com.juzix.wallet.event.EventPublisher;

public class WalletManager {

    private WalletEntity mSelectedWallet;

    private final static class InstanceHolder {
        private final static WalletManager WALLET_MANAGER = new WalletManager();
    }

    public static WalletManager getInstance() {
        return InstanceHolder.WALLET_MANAGER;
    }

    public WalletEntity getSelectedWallet() {
        return mSelectedWallet;
    }

    public void setSelectedWallet(WalletEntity mSelectedWallet) {
        this.mSelectedWallet = mSelectedWallet;
        EventPublisher.getInstance().sendUpdateSelectedWalletEvent(mSelectedWallet);
    }

    public String getSelectedWalletAddress() {
        return mSelectedWallet == null ? null : mSelectedWallet.getPrefixAddress();
    }
}
