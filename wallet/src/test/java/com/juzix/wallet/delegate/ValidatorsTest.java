package com.juzix.wallet.delegate;

import android.app.Application;

import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.contract.ValidatorsContract;
import com.juzix.wallet.component.ui.presenter.ValidatorsPresenter;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.entity.VerifyNode;
import com.juzix.wallet.rxjavatest.RxJavaTestSchedulerRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class ValidatorsTest {
    private ValidatorsPresenter presenter;
    @Mock
    private ValidatorsContract.View view;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;

    @Before
    public void setup() {
        Application app = RuntimeEnvironment.application;
        ApiResponse.init(app);

        AppSettings appSettings = AppSettings.getInstance();
        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);

        //输出日志
        ShadowLog.stream = System.out;

        view = mock(ValidatorsContract.View.class);
        presenter = new ValidatorsPresenter(view);
        presenter.attachView(view);

        appSettings.init(app);
    }

    @Test
    public void testValidatorsListData() {
        ServerUtils.getCommonApi().getVerifyNodeList()
                .subscribe(new ApiSingleObserver<List<VerifyNode>>() {
                    @Override
                    public void onApiSuccess(List<VerifyNode> verifyNodeList) {
                        presenter.getView().showValidatorsDataOnAll(verifyNodeList);
                        Log.debug("result", "message--------------------" + verifyNodeList.size() + " =================" + verifyNodeList.toString());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    @Test
    public void testShowValidatorsDataOnAll() {
        List<VerifyNode> nodeList = mock(List.class);
        VerifyNode verifyNode = mock(VerifyNode.class);
        nodeList.add(verifyNode);
        verify(nodeList).add(verifyNode);
        assertNotNull(verifyNode);
        assertNotNull(nodeList);
        view.showValidatorsDataOnAll(nodeList);
        view.showValidatorsDataOnActive(nodeList);
        view.showValidatorsDataOnCadidate(nodeList);
    }

    @Test
    public void testSort() {
        List<VerifyNode> verifyNodeList = new ArrayList<>();

        VerifyNode verifyNode = new VerifyNode();
        verifyNode.setInit(true);
        verifyNode.setDeposit("1656");
        verifyNode.setName("node-1");
        verifyNode.setNodeId("0x46545644454");
        verifyNode.setNodeStatus("Active");
        verifyNode.setRanking(100);
        verifyNode.setUrl("");
        verifyNode.setRatePA("1250");
        verifyNodeList.add(verifyNode);

        VerifyNode verifyNode2 = new VerifyNode();
        verifyNode2.setInit(true);
        verifyNode2.setDeposit("1656");
        verifyNode2.setName("node-1");
        verifyNode2.setNodeId("0x46545644454");
        verifyNode2.setNodeStatus("Active");
        verifyNode2.setRanking(80);
        verifyNode2.setUrl("");
        verifyNode2.setRatePA("1280");
        verifyNodeList.add(verifyNode2);

        assertNotNull(verifyNodeList);
        assertEquals(verifyNodeList.size(), 2);

        android.util.Log.d("tag", presenter.sort(verifyNodeList).size() + "==========================="+presenter.sort(verifyNodeList).iterator().toString());
        for(VerifyNode node :verifyNodeList){
            System.out.println("rate ===" +node.getRatePA()+"--------------" +"rank ===" + node.getRanking());
        }

    }


}
