package com.platon.aton.delegate;

import android.app.Application;

import com.platon.framework.app.log.Log;
import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.aton.component.ui.contract.DelegateRecordContract;
import com.platon.aton.component.ui.presenter.DelegateRecordPresenter;
import com.platon.aton.config.AppSettings;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.ServerUtils;
import com.platon.aton.engine.WalletManager;
import com.platon.aton.entity.Node;
import com.platon.aton.entity.Transaction;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;

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
@Config(sdk = 27, manifest = Config.NONE)
public class DelegateRecordTest {
    private DelegateRecordPresenter presenter;
    @Mock
    private DelegateRecordContract.View view;
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

        view = mock(DelegateRecordContract.View.class);
        presenter = new DelegateRecordPresenter(view);
        presenter.attachView(view);

        appSettings.init(app);
    }


    @Test
    public void testDelegateRecord() {
        String[] wallets = {"0x4ded81199608adb765fb2fe029bbfdf57f538be8", "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd", "0x7988c2f40629ae0aa6e19580a2789cf85107aa5c", "0x92e3a249ad9d4ec96aaad9c8daa00d0f20dd911e", "0x66126d6aa50dcf8490db7fed419daa6b0dd54774"};
        ServerUtils.getCommonApi().getDelegateRecordList(ApiRequestBody.newBuilder()
                .put("beginSequence", -1)
                .put("listSize", 10)
                .put("direction", "new")
                .put("type", "all")
                .put("walletAddrs", wallets)
                .build())
                .subscribe(new ApiSingleObserver<List<Transaction>>() {
                    @Override
                    public void onApiSuccess(List<Transaction> list) {
                        presenter.getView().showDelegateRecordData(list);
                        Log.debug("result", "message----------------------" + list.size() + "=============" + list.toString());
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });

    }

    @Test
    public void testShowDelegateRecordData() {
        List<Transaction> transactionList = mock(List.class);
        Transaction transaction = mock(Transaction.class);
        transactionList.add(transaction);
        assertNotNull(transactionList);
        assertNotNull(transaction);
        view.showDelegateRecordData(transactionList);
    }

    @Test
    public void testSetWalletAddessAndIcon() {
        List<Transaction> list = new ArrayList<>();

        Transaction transaction = new Transaction();
        transaction.setActualTxCost("0.005648");
        transaction.setBlockNumber(10556);
        transaction.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction.setNodeName("Node-1");
        transaction.setFrom("0x4ded81199608adb765fb2fe029bbfdf57f538be8");
        list.add(transaction);

        Transaction transaction2 = new Transaction();
        transaction2.setActualTxCost("0.005648");
        transaction2.setBlockNumber(10556);
        transaction2.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction2.setNodeName("Node-2");
        transaction2.setFrom("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        list.add(transaction2);

        Transaction transaction3 = new Transaction();
        transaction3.setActualTxCost("0.005648");
        transaction3.setBlockNumber(10556);
        transaction3.setNodeId("0xfa4a45sfa54s4dfa5s");
        transaction3.setNodeName("Node-3");
        transaction3.setFrom("0x7e4f77a7daaba0c90851d388df02783511c2befa");
        list.add(transaction3);

        List<Transaction> transactionList = Flowable.fromIterable(list)
                .map(new Function<Transaction, Transaction>() {
                    @Override
                    public Transaction apply(Transaction tras) throws Exception {
                        tras.setWalletName(WalletManager.getInstance().getWalletNameByWalletAddress(tras.getFrom()));
                        tras.setWalletIcon(WalletManager.getInstance().getWalletIconByWalletAddress(tras.getFrom()));
                        return tras;
                    }
                }).toList().blockingGet();

        for (Transaction bean : transactionList) {
            System.out.println("--------------------"+bean.getWalletName() + " ============== " + bean.getWalletIcon());
        }
    }


}
