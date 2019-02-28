package com.juzix.wallet.engine;

import com.juzix.wallet.event.EventPublisher;

public class SystemManager {
    private volatile boolean mIsFinished;
    private          Monitor mMonitor;
    private static int sRefreshTime = 5000;

    private SystemManager() {

    }

    public static SystemManager getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public void start() {
        stop();
        mIsFinished = false;
        mMonitor = new Monitor();
        mMonitor.start();
    }

    public void stop() {
        mIsFinished = true;
        if (mMonitor != null) {
            try {
                mMonitor.interrupt();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            mMonitor = null;
        }
    }

    void refreshSharedTransactionList(){
        new Thread(){
            @Override
            public void run() {
                SharedWalletTransactionManager.getInstance().updateTransactions();
                EventPublisher.getInstance().sendUpdateSharedWalletTransactionEvent();
                EventPublisher.getInstance().sendUpdateMessageTipsEvent(SharedWalletTransactionManager.getInstance().unRead());
            }
        }.start();
    }

    void refreshVoteTransactionList(){
        new Thread(){
            @Override
            public void run() {
                TicketManager.getInstance().updateVoteTickets();
            }
        }.start();
    }

    private class Monitor extends Thread {
        @Override
        public void run() {
            while (!mIsFinished) {
                try {
                    refreshSharedTransactionList();
                    refreshVoteTransactionList();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                try {
                    Thread.sleep(sRefreshTime);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
        }
    }

    private static class InstanceHolder {
        private static volatile SystemManager INSTANCE = new SystemManager();
    }
}
