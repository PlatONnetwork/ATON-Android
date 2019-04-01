package com.juzix.wallet.event;

import com.juzhen.framework.network.NetState;
import com.juzix.wallet.entity.RegionEntity;
import com.juzix.wallet.entity.SharedWalletEntity;

public class Event {

    private Event() {

    }

    public static class IndividualTransactionSucceedEvent {
        public IndividualTransactionSucceedEvent() {
        }
    }

    public static class SharedTransactionSucceedEvent {
        public SharedTransactionSucceedEvent() {
        }
    }

    public static class NodeChangedEvent {

        public NodeChangedEvent() {

        }
    }

    public static class NetWorkStateChangedEvent {

        public NetState netState;

        public NetWorkStateChangedEvent(NetState netState) {
            this.netState = netState;
        }
    }

    public static class UpdateSharedWalletTransactionEvent {
        public UpdateSharedWalletTransactionEvent() {
        }
    }

    public static class UpdateSharedWalletBalanceEvent {

        private String prefixContractAddress;
        private double balance;

        public UpdateSharedWalletBalanceEvent(String prefixContractAddress, double balance) {
            this.prefixContractAddress = prefixContractAddress;
            this.balance = balance;
        }
    }

    public static class UpdateIndividualWalletTransactionEvent {
        public UpdateIndividualWalletTransactionEvent() {
        }
    }

    public static class UpdateIndividualWalletBalanceEvent {
        public UpdateIndividualWalletBalanceEvent() {
        }
    }

    public static class UpdateMessageTipsEvent {
        public boolean unRead;

        public UpdateMessageTipsEvent(boolean unRead) {
            this.unRead = unRead;
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
}
