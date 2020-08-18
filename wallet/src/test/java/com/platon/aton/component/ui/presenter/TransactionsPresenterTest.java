package com.platon.aton.component.ui.presenter;


import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.TransactionsContract;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.aton.entity.Transaction;

import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


public class TransactionsPresenterTest extends BaseTestCase {
    private TransactionsPresenter presenter;
    @Mock
    private TransactionsContract.View view;

    @Override
    public void initSetup() {
        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);

        view = mock(TransactionsContract.View.class);
        presenter = new TransactionsPresenter();
        presenter.attachView(view);
    }

   /* @Test

    public void testGetTransactionListFromNet(){
        String []  walletAddress = {"0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd","0xa1abdb4917cf36c253b1fea6dae6c4755c347fa0"};
        ServerUtils
                .getCommonApi()
                .getTransactionList(ApiRequestBody.newBuilder()
                        .put("walletAddrs", walletAddress)
                        .put("beginSequence", -1)
                        .put("listSize", 20)
                        .put("direction", "new")
                        .build())
        .subscribe(new ApiSingleObserver<List<Transaction>>() {
            @Override
            public void onApiSuccess(List<Transaction> list) {
                Log.d("result", "============" + list.toString());
            }

            @Override
            public void onApiFailure(ApiResponse response) {

            }
        });

    }
*/




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
