package com.juzix.wallet.event;

import com.juzhen.framework.network.NetState;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.entity.VoteTransaction;
import com.juzix.wallet.entity.Wallet;

public class Event {

    private Event() {

    }

    public static class NetWorkStateChangedEvent {

        public NetState netState;

        public NetWorkStateChangedEvent(NetState netState) {
            this.netState = netState;
        }
    }

    public static class UpdateTransactionEvent {

        public Transaction transaction;

        public UpdateTransactionEvent(Transaction transaction) {
            this.transaction = transaction;
        }
    }

    public static class UpdateSelectedWalletEvent {

        public Wallet walletEntity;

        public UpdateSelectedWalletEvent(Wallet walletEntity) {
            this.walletEntity = walletEntity;
        }
    }

    public static class UpdateSharedWalletUnreadMessageEvent {

        public String contractAddress;
        public boolean hasUnreadMessage;

        public UpdateSharedWalletUnreadMessageEvent(String contractAddress, boolean hasUnreadMessage) {
            this.contractAddress = contractAddress;
            this.hasUnreadMessage = hasUnreadMessage;
        }
    }

    public static class UpdateTransactionUnreadMessageEvent {

        public String uuid;
        /**
         * 是否未读
         */
        public boolean hasUnread;

        public UpdateTransactionUnreadMessageEvent(String uuid, boolean hasUnread) {
            this.uuid = uuid;
            this.hasUnread = hasUnread;
        }
    }

    public static class UpdateWalletListEvent {

        public UpdateWalletListEvent() {
        }
    }

    public static class UpdateAssetsTabEvent {
        public int tabIndex;

        public UpdateAssetsTabEvent(int tabIndex) {
            this.tabIndex = tabIndex;
        }
    }

    public static class UpdateVoteTransactionListEvent {

        public VoteTransaction voteTransactionEntity;

        public UpdateVoteTransactionListEvent(VoteTransaction voteTransactionEntity) {
            this.voteTransactionEntity = voteTransactionEntity;
        }
    }

    public static class NodeChangedEvent {

        public Node nodeEntity;

        public NodeChangedEvent(Node nodeEntity) {
            this.nodeEntity = nodeEntity;
        }
    }

    /**
     * 钱包列表顺序改变
     */
    public static class WalletListOrderChangedEvent{

        public WalletListOrderChangedEvent() {

        }
    }
}
