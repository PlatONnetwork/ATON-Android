package com.platon.aton.engine;

import org.web3j.crypto.bech32.AddressManager;
import org.web3j.platon.ContractAddress;

public class ContractAddressManager {

    /**
     * 锁仓相关
     */
    public static final String RESTRICTING_PLAN_CONTRACT_ADDRESS = "1";
    /**
     * 质押相关
     */
    public static final String STAKING_CONTRACT_ADDRESS = "2";
    /**
     * 委托相关
     */
    public static final String DELEGATE_CONTRACT_ADDRESS = "3";
    /**
     * 节点相关
     */
    public static final String NODE_CONTRACT_ADDRESS = "4";
    /**
     * 举报惩罚相关
     */
    public static final String SLASH_CONTRACT_ADDRESS = "5";
    /**
     * 提案相关
     */
    public static final String PROPOSAL_CONTRACT_ADDRESS = "6";
    /**
     * 奖励
     */
    public static final String REWARD_CONTRACT_ADDRESS = "7";

    private static class InstanceHolder {
        private static volatile ContractAddressManager INSTANCE = new ContractAddressManager();
    }

    public static ContractAddressManager getInstance() {
        return ContractAddressManager.InstanceHolder.INSTANCE;
    }


    public String getPlanContractAddress(String contractAddressType) {

        String platContractAddress = "";
        switch (contractAddressType) {
            case RESTRICTING_PLAN_CONTRACT_ADDRESS:
                platContractAddress = ContractAddress.RESTRICTING_PLAN_CONTRACT_ADDRESS;
                break;
            case STAKING_CONTRACT_ADDRESS:
                platContractAddress = ContractAddress.STAKING_CONTRACT_ADDRESS;
                break;
            case DELEGATE_CONTRACT_ADDRESS:
                platContractAddress = ContractAddress.DELEGATE_CONTRACT_ADDRESS;
                break;
            case NODE_CONTRACT_ADDRESS:
                platContractAddress = ContractAddress.NODE_CONTRACT_ADDRESS;
                break;
            case SLASH_CONTRACT_ADDRESS:
                platContractAddress = ContractAddress.SLASH_CONTRACT_ADDRESS;
                break;
            case PROPOSAL_CONTRACT_ADDRESS:
                platContractAddress = ContractAddress.PROPOSAL_CONTRACT_ADDRESS;
                break;
            case REWARD_CONTRACT_ADDRESS:
                platContractAddress = ContractAddress.REWARD_CONTRACT_ADDRESS;
                break;
            default:

        }
        return AddressManager.getInstance().getAddress(platContractAddress);
    }


}

