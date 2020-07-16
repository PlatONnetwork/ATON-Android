package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.DelegateContract;
import com.platon.aton.component.ui.view.DelegateActivity;
import com.platon.aton.entity.EstimateGasResult;
import com.platon.framework.utils.LogUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.web3j.platon.StakingAmountType;

import static org.junit.Assert.*;

public class DelegatePresenterTest extends BaseTestCase {

    @Mock
    private DelegateContract.View view;

    private DelegatePresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(DelegateContract.View.class);
        //DelegateActivity delegateActivity = Robolectric.setupActivity(DelegateActivity.class);

        presenter = new DelegatePresenter();
        presenter.attachView(view);
    }


    @Test
    public void checkDelegateAmount(){

        EstimateGasResult estimateGasResult = new EstimateGasResult();
        estimateGasResult.setBlockGasLimit("500000000000");
        estimateGasResult.setFree("5983375239628300134343588424455034335218");
        estimateGasResult.setGasLimit("1379820");
        estimateGasResult.setGasPrice("500000000000");
        estimateGasResult.setLock("0");
        estimateGasResult.setMinDelegation("20000000000000000000");
        estimateGasResult.setNonce("44");

        presenter.setmEstimateGasResult(estimateGasResult);
        presenter.checkDelegateAmount("100");

        Mockito.verify(view).showTips(Mockito.anyBoolean(),Mockito.anyString());
    }


    @Test
    public void updateDelegateButtonState(){

        EstimateGasResult estimateGasResult = new EstimateGasResult();
        estimateGasResult.setBlockGasLimit("500000000000");
        estimateGasResult.setFree("5983375239628300134343588424455034335218");
        estimateGasResult.setGasLimit("1379820");
        estimateGasResult.setGasPrice("500000000000");
        estimateGasResult.setLock("0");
        estimateGasResult.setMinDelegation("20000000000000000000");
        estimateGasResult.setNonce("44");

        presenter.setmEstimateGasResult(estimateGasResult);
        presenter.updateDelegateButtonState();
        Mockito.verify(view).setDelegateButtonState(Mockito.anyBoolean());

    }


    @Test
    public void checkDelegateParam(){
        EstimateGasResult estimateGasResult = new EstimateGasResult();
        estimateGasResult.setBlockGasLimit("500000000000");
        estimateGasResult.setFree("5983375239628300134343588424455034335218");
        estimateGasResult.setGasLimit("1379820");
        estimateGasResult.setGasPrice("500000000000");
        estimateGasResult.setLock("0");
        estimateGasResult.setMinDelegation("20000000000000000000");
        estimateGasResult.setNonce("44");

        StakingAmountType stakingAmountType = StakingAmountType.getStakingAmountType(1000);

        presenter.setmEstimateGasResult(estimateGasResult);
       /* String result = presenter.checkDelegateParam(estimateGasResult,stakingAmountType);
        LogUtils.i("------result :" + result);*/

    }

/*    @Test
    public void getEstimateGas(){
        String prefixAddress = "lax1jxeg784p2vuemglc7cy59mzgq50heg3gawvxkj";
        String nodeId = "0x411a6c3640b6cd13799e7d4ed286c95104e3a31fbb05d7ae0004463db648f26e93f7f5848ee9795fb4bbb5f83985afd63f750dc4cf48f53b0e84d26d6834c20c";
        presenter.getEstimateGas(prefixAddress,nodeId);
    }*/





}