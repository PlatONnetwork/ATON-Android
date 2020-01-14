package com.juzix.wallet.delegate;

import android.app.Application;
import android.text.TextUtils;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.contract.DelegateDetailContract;
import com.juzix.wallet.component.ui.presenter.DelegateDetailPresenter;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateNodeDetail;
import com.juzix.wallet.entity.Node;
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

import io.reactivex.Flowable;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, packageName = "com.juzix.wallet", manifest = Config.NONE)
public class DelegateDetailTest {
    private DelegateDetailPresenter presenter;
    @Mock
    private DelegateDetailContract.View view;
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

        view = mock(DelegateDetailContract.View.class);
        presenter = new DelegateDetailPresenter(view);
        presenter.attachView(view);

        appSettings.init(app);
    }

    @Test
    public void testDelegateDetailRequestData() {
        String walletAddress = "0x4ded81199608adb765fb2fe029bbfdf57f538be8";
        ServerUtils.getCommonApi().getDelegateDetailList(ApiRequestBody.newBuilder()
                .put("addr", walletAddress)
                .build())
                .subscribe(new ApiSingleObserver<List<DelegateNodeDetail>>() {
                    @Override
                    public void onApiSuccess(List<DelegateNodeDetail> delegateDetails) {
//                        Log.debug("reuslt", "-------------->" + delegateDetails.size() + "" + delegateDetails.toString());
                        presenter.getView().showDelegateDetailData(delegateDetails);
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    @Test
    public void testMoveOut() {
        DelegateNodeDetail delegateDetail = mock(DelegateNodeDetail.class);
        delegateDetail.setWalletAddress("0xjfaskfjaskfa46545");
        delegateDetail.setNodeId("0x546a46ad4f5a65safa645");
        delegateDetail.setNodeName("node-123");
        delegateDetail.setNodeStatus("Active");
        delegateDetail.setReleased("134000000000000000000");
        delegateDetail.setDelegated("5238855000000000000000000");
        delegateDetail.setUrl("www.baidu.com");
        assertNotNull(delegateDetail);
//        presenter.moveOut(delegateDetail);

    }


    @Test
    public void testIsCanDelegate() {
        List<AccountBalance> accountBalances = new ArrayList<>();
        AccountBalance balance = mock(AccountBalance.class);
        balance.setAddr("0x5ad4fa56d4fa6s4f6asd");
        balance.setFree("12464000000000000000");
        balance.setLock("546800000000000000000");
        accountBalances.add(balance);
        assertNotNull(accountBalances);
        assertNotNull(balance);

        for (AccountBalance balance1 : accountBalances) {
            if (!TextUtils.equals(balance1.getLock(), "0") || !TextUtils.equals(balance1.getFree(), "0")) {
                view.showIsCanDelegate(balance1.getPrefixAddress(), "Node-123", "","", true);
                return;
            }
        }

    }

    @Test
    public void testSetWalletAddress() {
        List<DelegateNodeDetail> detailList = new ArrayList<>();

        DelegateNodeDetail delegateDetail = new DelegateNodeDetail();
        delegateDetail.setNodeId("0x546a46ad4f5a65safa645");
        delegateDetail.setNodeName("node-1");
        delegateDetail.setNodeStatus("Active");
        delegateDetail.setReleased("134000000000000000000");
        delegateDetail.setUrl("www.baidu.com");
        delegateDetail.setDelegated("546800000000000000000");
        detailList.add(delegateDetail);

        DelegateNodeDetail delegateDetail2 = new DelegateNodeDetail();
        delegateDetail.setNodeId("0x546a46ad4f5a65safa645");
        delegateDetail.setNodeName("node-2");
        delegateDetail.setNodeStatus("Active");
        delegateDetail.setReleased("134000000000000000000");
        delegateDetail.setUrl("www.baidu.com");
        delegateDetail.setDelegated("546800000000000000000");
        detailList.add(delegateDetail2);


        DelegateNodeDetail delegateDetail3 = new DelegateNodeDetail();
        delegateDetail.setNodeId("0x546a46ad4f5a65safa645");
        delegateDetail.setNodeName("node-3");
        delegateDetail.setNodeStatus("Active");
        delegateDetail.setReleased("134000000000000000000");
        delegateDetail.setUrl("www.baidu.com");
        delegateDetail.setDelegated("546800000000000000000");
        detailList.add(delegateDetail3);

        List<DelegateNodeDetail> list = Flowable.fromIterable(detailList)
                .map(new Function<DelegateNodeDetail, DelegateNodeDetail>() {
                    @Override
                    public DelegateNodeDetail apply(DelegateNodeDetail delegateDetail) throws Exception {
                        delegateDetail.setWalletAddress("0x564fa54fa65s4da65465af"); //给每个对象赋值钱包地址
                        return delegateDetail;
                    }
                }).toList().blockingGet();

        for (DelegateNodeDetail detail : list) {
            System.out.println(detail.getWalletAddress());
        }

    }


}
