package com.platon.aton.transactionrecord;

import android.util.Log;

import com.platon.aton.component.ui.contract.TransactionRecordsContract;
import com.platon.aton.component.ui.presenter.TransactionRecordsPresenter;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.entity.Transaction;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.aton.schedulers.SchedulerTestProvider;
import com.platon.aton.utils.RxUtils;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.framework.utils.PreferenceTool;

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
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class TransactionRecordPresenterTest {
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
        PreferenceTool.init(RuntimeEnvironment.application);
        NodeManager nodeManager = NodeManager.getInstance();
        //输出日志
        ShadowLog.stream = System.out;
        schedulerTestProvider = new SchedulerTestProvider();
        view = mock(TransactionRecordsContract.View.class);
        presenter = new TransactionRecordsPresenter();
        presenter.attachView(view);

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
                        Log.d("reuslt", "-------------->" + transactions.size() + "" + transactions.toString());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {
                        Log.d("shibai", "---------------" + response.getErrorCode() + "" + response.getErrMsg(RuntimeEnvironment.application));
                    }
                });

    }


    @Test
    public void testGetValidBiggerSequence() {
        List<Transaction> list = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber("10556");
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        transaction.setSequence(1025);
        list.add(transaction);

        Transaction transaction2 = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber("10556");
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        transaction.setSequence(1028);
        list.add(transaction2);

        Transaction transaction3 = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber("10556");
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setSequence(1035);
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        list.add(transaction3);

        long sequence = Flowable
                .range(0, list.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return list.get(integer).getSequence() != 0;
                    }
                })
                .map(new Function<Integer, Long>() {
                    @Override
                    public Long apply(Integer integer) throws Exception {
                        return list.get(integer).getSequence();
                    }
                })
                .firstElement()
                .blockingGet();

        assertNotNull(sequence);
        System.out.println(sequence);

    }

    @Test
    public void testGetValidSmallerSequence() {
        List<Transaction> list = new ArrayList<>();
        Transaction transaction = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber("10556");
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        transaction.setSequence(1025);
        list.add(transaction);

        Transaction transaction2 = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber("10556");
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        transaction.setSequence(1028);
        list.add(transaction2);

        Transaction transaction3 = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber("10556");
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setSequence(1035);
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        list.add(transaction3);

        Transaction transaction4 = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber("10556");
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setSequence(1055);
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        list.add(transaction4);


        long smallSequence =Flowable
                .range(0, list.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return list.get(integer).getSequence() != 0;
                    }
                })
                .lastElement()
                .map(new Function<Integer, Long>() {
                    @Override
                    public Long apply(Integer integer) throws Exception {
                        return list.get(integer).getSequence();
                    }
                })
                .blockingGet();

        assertNotNull(smallSequence);
        System.out.println(smallSequence);

    }


}
