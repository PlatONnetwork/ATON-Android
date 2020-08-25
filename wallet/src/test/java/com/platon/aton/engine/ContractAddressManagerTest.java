package com.platon.aton.engine;

import com.platon.aton.BaseTestCase;
import com.platon.framework.utils.LogUtils;

import org.junit.Test;

public class ContractAddressManagerTest extends BaseTestCase {



    @Override
    public void initSetup() {

    }


    @Test
    public void getPlanContractAddress(){

       String contractAddress =  ContractAddressManager.getInstance().getPlanContractAddress(ContractAddressManager.REWARD_CONTRACT_ADDRESS);
        LogUtils.e("----contractAddress:" + contractAddress);
    }
}
