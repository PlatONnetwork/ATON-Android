package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.SortType;
import com.platon.aton.component.ui.contract.ValidatorsContract;
import com.platon.aton.entity.VerifyNode;
import com.platon.framework.utils.LogUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ValidatorsPresenterTest extends BaseTestCase {

    @Mock
    private ValidatorsContract.View view;

    ValidatorsPresenter presenter;

    @Override
    public void initSetup() {
        view = Mockito.mock(ValidatorsContract.View.class);
        presenter = new ValidatorsPresenter();
        presenter.attachView(view);
    }

    @Test
    public void getVerifyNodeList()
    {

        List<VerifyNode> allVerifyNodeList = new ArrayList<>();
        VerifyNode verifyNode = new VerifyNode();
        verifyNode.setDelegate("14");
        verifyNode.setDelegatedRatePA("142953636899007300000000000");
        verifyNode.setDelegateSum("100012587998108680000000000");
        verifyNode.setConsensus(true);
        verifyNode.setInit(false);
        verifyNode.setName("chendai-node4");
        verifyNode.setNodeId("0x411a6c3640b6cd13799e7d4ed286c95104e3a31fbb05d7ae0004463db648f26e93f7f5848ee9795fb4bbb5f83985afd63f750dc4cf48f53b0e84d26d6834c20c");
        verifyNode.setNodeStatus("Active");
        verifyNode.setRanking(1);
        verifyNode.setUrl("");

        VerifyNode verifyNode2 = new VerifyNode();
        verifyNode2.setDelegate("13");
        verifyNode2.setDelegatedRatePA("3511399609683013000000000000");
        verifyNode2.setDelegateSum("4071642302000000000000");
        verifyNode2.setConsensus(true);
        verifyNode2.setInit(false);
        verifyNode2.setName("chendai-node3");
        verifyNode2.setNodeId("0x77fffc999d9f9403b65009f1eb27bae65774e2d8ea36f7b20a89f82642a5067557430e6edfe5320bb81c3666a19cf4a5172d6533117d7ebcd0f2c82055499050");
        verifyNode2.setNodeStatus("Active");
        verifyNode2.setRanking(2);
        verifyNode2.setUrl("");
        allVerifyNodeList.add(verifyNode);
        allVerifyNodeList.add(verifyNode2);


        String keywords = "";
        String nodeStatus = "All";

        List<VerifyNode> verifyNodeList = presenter.getVerifyNodeList(allVerifyNodeList,nodeStatus, SortType.SORTED_BY_ANNUAL_YIELD,keywords);
        LogUtils.i("-------verifyNodeList:" + verifyNodeList.size());

    }

}