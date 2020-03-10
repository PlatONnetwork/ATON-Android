package com.platon.wallet.delegate;


import android.app.Application;

import com.platon.framework.app.log.Log;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.wallet.component.ui.contract.ValidatorsDetailContract;
import com.platon.wallet.component.ui.presenter.ValidatorsDetailPresenter;
import com.platon.wallet.config.AppSettings;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.engine.ServerUtils;
import com.platon.wallet.entity.AccountBalance;
import com.platon.wallet.entity.Node;
import com.platon.wallet.entity.VerifyNodeDetail;
import com.platon.wallet.rxjavatest.RxJavaTestSchedulerRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class ValidatorsDetailTest {
    private ValidatorsDetailPresenter presenter;
    @Mock
    private ValidatorsDetailContract.View view;
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

        view = Mockito.mock(ValidatorsDetailContract.View.class);
        presenter = new ValidatorsDetailPresenter(view);
        presenter.attachView(view);

        appSettings.init(app);
    }

    @Test
    public void testValidatorsDetailData() {
        String nodeId = "0xdac7931462dc0db97d9a0010c5719411810c06f90bd8b66432113a6f31bf9d1ab8a8f7db5bbbc76f4448ad6b3215cb527ba3276e185f9ba2360ef09be62d90c5";
        ServerUtils.getCommonApi().getNodeCandidateDetail(ApiRequestBody.newBuilder()
                .put("nodeId", nodeId)
                .build()).subscribe(new ApiSingleObserver<VerifyNodeDetail>() {
            @Override
            public void onApiSuccess(VerifyNodeDetail verifyNodeDetail) {
                presenter.getView().showValidatorsDetailData(verifyNodeDetail);
                Log.debug("result", "message" + "--------------------" + verifyNodeDetail.toString());

            }

            @Override
            public void onApiFailure(ApiResponse response) {

            }
        });
    }

    @Test
    public void testAccountBalance() {
        String[] walletAddress = {"0x4ded81199608adb765fb2fe029bbfdf57f538be8", "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd", "0x7988c2f40629ae0aa6e19580a2789cf85107aa5c", "0x92e3a249ad9d4ec96aaad9c8daa00d0f20dd911e", "0x66126d6aa50dcf8490db7fed419daa6b0dd54774"};
        ServerUtils.getCommonApi().getAccountBalance(ApiRequestBody.newBuilder()
                .put("addrs", walletAddress)
                .build())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {
                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                       if(accountBalances.size() > 0){
                           presenter.getView().showIsCanDelegate(true);
                       }else {
                           presenter.getView().showIsCanDelegate(false);
                       }

                        Log.debug("result", "message" + "--------------------" + accountBalances.toString());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });

    }

}
