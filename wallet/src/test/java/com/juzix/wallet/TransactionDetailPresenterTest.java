package com.juzix.wallet;

import android.app.Application;
import android.text.TextUtils;

import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.component.ui.contract.IndividualTransactionDetailContract;
import com.juzix.wallet.component.ui.contract.SendTransationContract;
import com.juzix.wallet.component.ui.presenter.SendTransationPresenter;
import com.juzix.wallet.component.ui.presenter.TransactionDetailPresenter;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.db.sqlite.WalletDao;
import com.juzix.wallet.engine.DelegateManager;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.engine.Web3jManager;
import com.juzix.wallet.entity.Node;
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

import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class TransactionDetailPresenterTest {
    private TransactionDetailPresenter presenter;
    @Mock
    private IndividualTransactionDetailContract.View view;

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

        view = mock(IndividualTransactionDetailContract.View.class);
        presenter = new TransactionDetailPresenter(view);
        presenter.attachView(view);

        appSettings.init(app);

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

        PlatonSendTransaction sendTransaction = new PlatonSendTransaction();
        sendTransaction.setResult("0x39a50f94dd1524c092db55c621e30ffeb7072b5e1a7c90224911958208d24e7d");
        Single.fromCallable(new Callable<BaseResponse>() {
            @Override
            public BaseResponse call() throws Exception {
                Web3j web3j = Web3jManager.getInstance().getWeb3j();
                DelegateContract delegateContract = DelegateContract.load(web3j);
                return delegateContract.getUnDelegateResult(sendTransaction).send();
            }
        }).delay(5000, TimeUnit.SECONDS)
                .subscribe(new Consumer<BaseResponse>() {
                    @Override
                    public void accept(BaseResponse response) throws Exception {
                        System.out.println(response.status + "==================" + response.data);
                    }
                });

    }


}
