package com.juzix.wallet.delegate;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;

import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.RobolectricApp;
import com.juzix.wallet.component.ui.CustomContextWrapper;
import com.juzix.wallet.component.ui.contract.DelegateContract;
import com.juzix.wallet.component.ui.presenter.DelegatePresenter;
import com.juzix.wallet.component.ui.view.DelegateActivity;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.AccountBalance;
import com.juzix.wallet.entity.DelegateHandle;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.rxjavatest.RxJavaTestSchedulerRule;
import com.juzix.wallet.utils.LanguageUtil;
import com.juzix.wallet.utils.RxUtils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.web3j.platon.StakingAmountType;
import org.web3j.protocol.Web3j;
import org.web3j.utils.Convert;

import java.math.BigInteger;
import java.util.List;

import io.reactivex.functions.Consumer;
import rx.Subscriber;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class DelegateActivityTest {
    private DelegatePresenter presenter;
    @Mock
    private DelegateContract.View view;
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;
//    @Mock
//    public CustomContextWrapper contextWrapper;

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

        appSettings.init(app);

        view = mock(DelegateContract.View.class);
        presenter = new DelegatePresenter(view);
        presenter.attachView(view);

//        contextWrapper = new CustomContextWrapper(RuntimeEnvironment.application);
    }

    @Test
    public void testIsDelegate() {
        String walletAddress = "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        String nodeId = "0x3fa7d5c08d2dbf31adb440b2a6b4c1f906bdba748681d528bbe40e57b9eaae07d50cdc3a487bba63766e13ddf80f47329d3cab77fc73ab30e010a9e5aea24622";
        ServerUtils.getCommonApi().getIsDelegateInfo(ApiRequestBody.newBuilder()
                .put("addr", walletAddress)
                .put("nodeId", nodeId)
                .build())
                .subscribe(new ApiSingleObserver<DelegateHandle>() {
                    @Override
                    public void onApiSuccess(DelegateHandle delegateHandle) {
                        presenter.getView().showIsCanDelegate(delegateHandle);
                        Log.d("result", "========================" + delegateHandle.isCanDelegation() + "====================" + delegateHandle.getMessage());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });
    }

    @Test
    public void testGetWalletBalance() {
        String[] walletAddressList = {"0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd", "0x4ded81199608adb765fb2fe029bbfdf57f538be8", "0x7e4f77a7daaba0c90851d388df02783511c2befa"};
        ServerUtils.getCommonApi().getAccountBalance(ApiRequestBody.newBuilder()
                .put("addrs", walletAddressList)
                .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {
                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                        view.getWalletBalanceList(accountBalances);
                        Log.d("result", "message" + "--------------------" + accountBalances.toString());
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
    public void testGetGasPrice() {
        String nodeId = "0x9e0b0968fd8977e41d321bc1f9d653f169368581028fd08c9419ddf28c1be1286aae51539aa416c2041261ae8aca4f3e2cd010dbbb1c79e14fdf432b38b8234a";
        Web3j web3j = Web3jManager.getInstance().getWeb3j();
        org.web3j.platon.contracts.DelegateContract delegateContract = org.web3j.platon.contracts.DelegateContract.load(web3j);
        StakingAmountType stakingAmountType = TextUtils.equals("balance", "balance") ? StakingAmountType.FREE_AMOUNT_TYPE : StakingAmountType.RESTRICTING_AMOUNT_TYPE;
        delegateContract.getDelegateFeeAmount(new BigInteger("500000"), nodeId, stakingAmountType, Convert.toVon("1000", Convert.Unit.LAT).toBigInteger())
                .subscribe(new Subscriber<BigInteger>() {
                    @Override
                    public void onNext(BigInteger integer) {
                        view.showGasPrice(integer.toString());
                        System.out.println(integer);
                        assertNotNull(integer);
                    }

                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                });
    }

//    @Test
//    public void testDelegateActivity() {
//        DelegateActivity delegateActivity = Robolectric.setupActivity(DelegateActivity.class);
//        assertNotNull("DelegateActivity not intstantitated", delegateActivity);
//    }


}
