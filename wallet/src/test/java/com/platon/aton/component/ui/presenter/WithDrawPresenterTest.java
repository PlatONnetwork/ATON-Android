package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.WithDrawContract;
import com.platon.aton.entity.Bech32Address;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.DelegationValue;
import com.platon.aton.entity.Wallet;
import com.platon.aton.entity.WithDrawBalance;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class WithDrawPresenterTest extends BaseTestCase {

    private WithDrawContract.View view;
    private WithDrawPresenter presenter;

    @Override
    public void initSetup() {
        view = Mockito.mock(WithDrawContract.View.class);
        presenter = new WithDrawPresenter();
        presenter.attachView(view);
    }


    @Test
    public void checkWithDrawAmount(){

        DelegationValue delegationValue = new DelegationValue();
        delegationValue.setFree("234456");
        delegationValue.setLock("345");
        delegationValue.setMinDelegation("100");

        String withdrawAmount = "150";

        presenter.setmDelegationValue(delegationValue);
        presenter.checkWithDrawAmount(withdrawAmount);

        Mockito.verify(view).showTips(Mockito.anyBoolean(),Mockito.anyString());
    }


    @Test
    public void getBalanceType(){

        DelegationValue delegationValue = new DelegationValue();
        delegationValue.setFree("234456");
        delegationValue.setLock("345");
        delegationValue.setMinDelegation("100");
        presenter.setmDelegationValue(delegationValue);


        Wallet wallet = new Wallet();
        wallet.setUuid("9eed1da8-58e9-4b10-8bea-2bfdd1cac433");
        wallet.setAvatar("avatar_3");
        wallet.setAddress("0x0d395e21b23f8d920f8556cbf573af9ba2ad1a59");
        wallet.setChainId("101");
        wallet.setName("Ella");
        wallet.setKeystorePath("UTC--2020-06-28T06-36-42.751--b1ca4a13ee33ed68d097223f186f65864ecdb98c9b35c664566f096761649aa7d9c9c2bc317785a5c2580b305803e5f29aafce422e49579792623575215675e7.json");
        Bech32Address bech32Address = new Bech32Address("lat1p5u4ugdj87xeyru92m9l2ua0nw326xjekflsf8","lax1p5u4ugdj87xeyru92m9l2ua0nw326xjeevdl8g");
        wallet.setBech32Address(bech32Address);
        presenter.setmWallet(wallet);

        DelegateItemInfo delegateItemInfo = new DelegateItemInfo();
        delegateItemInfo.setNodeName("测试");
        delegateItemInfo.setNodeId("101");
        presenter.setmDelegateDetail(delegateItemInfo);

       // presenter.getBalanceType();


    }


    @Test
    public void getWithDrawGasPrice(){

        WithDrawBalance mWithDrawBalance = new WithDrawBalance();
        mWithDrawBalance.setGasLimit("1000");
        mWithDrawBalance.setGasPrice("200");
        presenter.setmWithDrawBalance(mWithDrawBalance);

        presenter.getWithDrawGasPrice("20");
        Mockito.verify(view).showWithDrawGasPrice(Mockito.anyString());

    }





}