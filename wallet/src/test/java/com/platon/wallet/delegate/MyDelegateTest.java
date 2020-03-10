package com.platon.wallet.delegate;

import android.app.Application;

import com.platon.framework.app.log.Log;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.wallet.component.ui.contract.MyDelegateContract;
import com.platon.wallet.component.ui.presenter.MyDelegatePresenter;
import com.platon.wallet.config.AppSettings;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.engine.ServerUtils;
import com.platon.wallet.engine.WalletManager;
import com.platon.wallet.entity.DelegateInfo;
import com.platon.wallet.entity.Node;
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


import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27)
public class MyDelegateTest {

    private MyDelegatePresenter presenter;
    @Mock
    private MyDelegateContract.View view;
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

        view = Mockito.mock(MyDelegateContract.View.class);
        presenter = new MyDelegatePresenter(view);
        presenter.attachView(view);

        appSettings.init(app);
    }

    @Test
    public void testMyDelegateRequestData() {
        List<String> walletAddressList = WalletManager.getInstance().getAddressList();
        String[] wallets = {"0x4ded81199608adb765fb2fe029bbfdf57f538be8", "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd", "0x7988c2f40629ae0aa6e19580a2789cf85107aa5c", "0x92e3a249ad9d4ec96aaad9c8daa00d0f20dd911e", "0x66126d6aa50dcf8490db7fed419daa6b0dd54774"};
        ServerUtils.getCommonApi().getMyDelegateList(
                ApiRequestBody.newBuilder()
                        .put("walletAddrs", wallets)
                        .build())
                .subscribe(new ApiSingleObserver<List<DelegateInfo>>() {
                    @Override
                    public void onApiSuccess(List<DelegateInfo> infoList) {
                        Log.debug("reuslt", "-------------->" + infoList.size() + "" + infoList.toString());
                        presenter.getView().showMyDelegateData(infoList);
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });

    }

    @Test
    public void testShowMyDelegateData() {
        List<DelegateInfo> infoList = mock(List.class);
        DelegateInfo info = mock(DelegateInfo.class);
        infoList.add(info);
//        verify(infoList).add(info);
        assertNotNull(infoList);
        assertNotNull(info);
        view.showMyDelegateData(infoList);

    }


}
