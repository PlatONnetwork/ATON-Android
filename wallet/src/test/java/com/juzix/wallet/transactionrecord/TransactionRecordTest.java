package com.juzix.wallet.transactionrecord;

import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.network.ApiRequestBody;
import com.juzhen.framework.network.ApiResponse;
import com.juzhen.framework.network.ApiSingleObserver;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.component.ui.contract.TransactionRecordsContract;
import com.juzix.wallet.component.ui.presenter.TransactionRecordsPresenter;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.ServerUtils;
import com.juzix.wallet.entity.Transaction;
import com.juzix.wallet.rxjavatest.RxJavaTestSchedulerRule;
import com.juzix.wallet.schedulers.SchedulerTestProvider;
import com.juzix.wallet.utils.RxUtils;

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

import java.util.List;


import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class TransactionRecordTest {
    private TransactionRecordsPresenter presenter;

    @Mock
    private TransactionRecordsContract.View view;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SchedulerTestProvider schedulerTestProvider;

    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();


    @Before
    public void setup() {
        AppSettings appSettings = AppSettings.getInstance();
        NodeManager nodeManager = NodeManager.getInstance();
        //输出日志
        ShadowLog.stream = System.out;
        schedulerTestProvider = new SchedulerTestProvider();
        view = mock(TransactionRecordsContract.View.class);
        presenter = new TransactionRecordsPresenter(view);
        presenter.attachView(view);
        appSettings.init(RuntimeEnvironment.application);

    }

    @Test
    public void testTransationRecordData() {
        String[] addressList = {"0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd", "0x4ded81199608adb765fb2fe029bbfdf57f538be8", "0x7e4f77a7daaba0c90851d388df02783511c2befa"};

        ServerUtils.getCommonApi().getTransactionList(ApiRequestBody.newBuilder()
                .put("walletAddrs", addressList)
                .put("beginSequence", -1)
                .put("listSize", 10)
                .put("direction", "old")
                .build())
                .compose(RxUtils.getSingleSchedulerTransformer())
                .subscribe(new ApiSingleObserver<List<Transaction>>() {
                    @Override
                    public void onApiSuccess(List<Transaction> transactions) {
                        Log.debug("reuslt", "-------------->" + transactions.size() + "" + transactions.toString());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        Log.debug("shibai", "---------------" + response.getErrorCode() + "" + response.getErrMsg(RuntimeEnvironment.application));
                    }
                });

    }




}
