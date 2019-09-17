package com.juzix.wallet.delegate;

import android.app.Application;
import android.util.Log;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.component.ui.contract.WithDrawContract;
import com.juzix.wallet.component.ui.presenter.WithDrawPresenter;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.entity.WithDrawBalance;
import com.juzix.wallet.rxjavatest.RxJavaTestSchedulerRule;
import com.juzix.wallet.utils.RxUtils;

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
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.List;

import io.reactivex.functions.Consumer;
import rx.Subscriber;

import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class WithDrawActivityTest {
    private WithDrawPresenter presenter;
    @Mock
    private WithDrawContract.View view;
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

        view = Mockito.mock(WithDrawContract.View.class);
        presenter = new WithDrawPresenter(view);
        presenter.attachView(view);

        appSettings.init(app);
    }

    @Test
    public void testBalanceType() {
        String walletAddress = "0x4ded81199608adb765fb2fe029bbfdf57f538be8";
        String nodeId = "0xdac7931462dc0db97d9a0010c5719411810c06f90bd8b66432113a6f31bf9d1ab8a8f7db5bbbc76f4448ad6b3215cb527ba3276e185f9ba2360ef09be62d90c5";
        ServerUtils.getCommonApi().getWithDrawBalance(ApiRequestBody.newBuilder()
                .put("addr", walletAddress)
                .put("nodeId", nodeId)
                .build())
                .subscribe(new ApiSingleObserver<List<WithDrawBalance>>() {
                    @Override
                    public void onApiSuccess(List<WithDrawBalance> balanceList) {
//                        presenter.getView().showBalanceType();
                        Log.d("result", "==============" + balanceList.toString());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    @Test
    public void testGetGas() {
        DelegateManager delegateManager = DelegateManager.getInstance();
        delegateManager.getGasPrice()
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger bigInteger) throws Exception {
                        assertNotNull(bigInteger);
                        System.out.println(bigInteger);
                    }
                });
    }

    @Test
    public void testGetWithDrawPrice() {
        String nodeId ="0x9e0b0968fd8977e41d321bc1f9d653f169368581028fd08c9419ddf28c1be1286aae51539aa416c2041261ae8aca4f3e2cd010dbbb1c79e14fdf432b38b8234a";
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        org.web3j.platon.contracts.DelegateContract delegateContract = org.web3j.platon.contracts.DelegateContract.load(web3j);
        delegateContract.getUnDelegateFeeAmount(new BigInteger("500000"), nodeId, new BigInteger("5132"), Convert.toVon("500", Convert.Unit.LAT).toBigInteger())
                .subscribe(new Subscriber<BigInteger>() {
                    @Override
                    public void onNext(BigInteger bigInteger) {
                        view.showWithDrawGasPrice(bigInteger.toString());
                        System.out.println(bigInteger);
                        assertNotNull(bigInteger);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                });

    }

}
