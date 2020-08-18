package com.platon.aton.component.ui.presenter;

import android.text.TextUtils;

import com.platon.aton.BaseTestCase;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.engine.Web3jManager;
import com.platon.aton.entity.Node;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.aton.schedulers.SchedulerTestProvider;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.web3j.platon.BaseResponse;
import org.web3j.platon.contracts.DelegateContract;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.PlatonSendTransaction;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


public class TransactionDetailPresenterTest extends BaseTestCase {

    private TransactionDetailPresenter presenter;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SchedulerTestProvider schedulerTestProvider = new SchedulerTestProvider();

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();




    @Override
    public void initSetup() {

    }

    @Test
    public void testLoadData() {

        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
            }
        }).flatMap(new Function<String, SingleSource<String>>() {
            @Override
            public SingleSource<String> apply(String s) throws Exception {
                if (TextUtils.isEmpty(s)) {
                    return Single.fromCallable(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
                        }
                    });
                } else {
                    return Single.just(s);
                }
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("====================" + s);
            }
        });

    }


    @Test
    public void testUpdateTransactionDetailInfo() {
        Single.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
            }
        }).flatMap(new Function<String, SingleSource<String>>() {
            @Override
            public SingleSource<String> apply(String s) throws Exception {

                if (TextUtils.isEmpty(s)) {
                    return Single.fromCallable(new Callable<String>() {
                        @Override
                        public String call() throws Exception {
                            return "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
                        }
                    });
                } else {
                    return Single.just(s);
                }
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("====================" + s);
            }
        });
    }

    @Test
    public void testLoadDelegateResult() {

        PlatonSendTransaction sendContractTransaction = new PlatonSendTransaction();
        sendContractTransaction.setResult("0x39a50f94dd1524c092db55c621e30ffeb7072b5e1a7c90224911958208d24e7d");
        Single.fromCallable(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                DelegateContract delegateContract = DelegateContract.load(web3j);
                return delegateContract.getUnDelegateResult(sendContractTransaction).send();
            }
        }).delay(5000, TimeUnit.SECONDS)
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        System.out.println(response.code + "==================" + response.data);
                    }
                });

    }


}
