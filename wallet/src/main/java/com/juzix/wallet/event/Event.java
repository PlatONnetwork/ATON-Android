package com.juzix.wallet.event;

import com.juzhen.framework.network.NetState;
import com.juzix.wallet.entity.IndividualTransactionEntity;
import com.juzix.wallet.entity.NodeEntity;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.entity.SharedTransactionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;
import com.juzix.wallet.entity.VoteTransactionEntity;
import com.juzix.wallet.entity.WalletEntity;

public class Event {

    private Event() {

    }

    public static class NetWorkStateChangedEvent {

        public NetState netState;

        public NetWorkStateChangedEvent(NetState netState) {
            this.netState = netState;
        }
    }

    public static class UpdateSharedWalletTransactionEvent {

        public SharedTransactionEntity sharedTransactionEntity;

        public UpdateSharedWalletTransactionEvent(SharedTransactionEntity sharedTransactionEntity) {
            this.sharedTransactionEntity = sharedTransactionEntity;
        }
    }

    public static class UpdateIndividualWalletTransactionEvent {

        public IndividualTransactionEntity individualTransactionEntity;

        public UpdateIndividualWalletTransactionEvent(IndividualTransactionEntity individualTransactionEntity) {
            this.individualTransactionEntity = individualTransactionEntity;
        }
    }

    public static class UpdateCreateJointWalletProgressEvent {

        public SharedWalletEntity sharedWalletEntity;

        public UpdateCreateJointWalletProgressEvent(SharedWalletEntity sharedWalletEntity) {
            this.sharedWalletEntity = sharedWalletEntity;
        }
    }

    public static class UpdateCandidateRegionInfoEvent {

        public RegionEntity regionEntity;

        public UpdateCandidateRegionInfoEvent(RegionEntity regionEntity) {
            this.regionEntity = regionEntity;
        }
    }

    public static class UpdateSelectedWalletEvent {

        public WalletEntity walletEntity;

        public UpdateSelectedWalletEvent(WalletEntity walletEntity) {
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

    public static class RemoveSharedWalletEvent {

        public SharedWalletEntity sharedWalletEntity;

        public RemoveSharedWalletEvent(SharedWalletEntity sharedWalletEntity) {
            this.sharedWalletEntity = sharedWalletEntity;
        }
    }

    public static class UpdateAssetsTabEvent {
        public int tabIndex;

        public UpdateAssetsTabEvent(int tabIndex) {
            this.tabIndex = tabIndex;
        }
    }

    public static class UpdateVoteTransactionListEvent {

        public VoteTransactionEntity voteTransactionEntity;

        public UpdateVoteTransactionListEvent(VoteTransactionEntity voteTransactionEntity) {
            this.voteTransactionEntity = voteTransactionEntity;
        }
    }

    public static class NodeChangedEvent {

        public NodeEntity nodeEntity;

        public NodeChangedEvent(NodeEntity nodeEntity) {
            this.nodeEntity = nodeEntity;
        }
    }
}
