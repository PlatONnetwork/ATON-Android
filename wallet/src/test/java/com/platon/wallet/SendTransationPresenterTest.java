package com.platon.wallet;

import android.app.Application;
import android.text.TextUtils;

import com.platon.framework.network.ApiRequestBody;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.network.ApiSingleObserver;
import com.platon.framework.util.LogUtils;
import com.platon.wallet.component.ui.contract.SendTransationContract;
import com.platon.wallet.component.ui.presenter.SendTransactionPresenter;
import com.platon.wallet.config.AppSettings;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.engine.ServerUtils;
import com.platon.wallet.engine.Web3jManager;
import com.platon.wallet.entity.AccountBalance;
import com.platon.wallet.entity.Node;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.wallet.schedulers.SchedulerTestProvider;
import com.platon.wallet.utils.AddressFormatUtil;

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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class SendTransationPresenterTest {

    private SendTransactionPresenter presenter;
    @Mock
    private SendTransationContract.View view;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SchedulerTestProvider schedulerTestProvider;

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

        view = mock(SendTransationContract.View.class);
        presenter = new SendTransactionPresenter(view);
        presenter.attachView(view);

        appSettings.init(app);
    }

    @Test
    public void testFetchDefaultWalletInfo() {
        ServerUtils
                .getCommonApi()
                .getAccountBalance(ApiRequestBody.newBuilder()
                        .put("addrs", new String[]{"0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd"})
                        .build())
                .subscribe(new ApiSingleObserver<List<AccountBalance>>() {
                    @Override
                    public void onApiSuccess(List<AccountBalance> accountBalances) {
                        for (AccountBalance balance : accountBalances) {
                            System.out.println(balance.getFree() + "==============" + balance.getLock());
                        }
                    }

                    @Override
                    public void onApiFailure(ApiResponse response) {

                    }
                });


        Web3jManager.getInstance().getGasPrice()
                .subscribe(new Consumer<BigInteger>() {
                    @Override
                    public void accept(BigInteger bigInteger) throws Exception {
                        LogUtils.e("gasPrice为：  " + bigInteger.longValue());
                        BigInteger minGasPrice = bigInteger.divide(BigInteger.valueOf(2));
                        BigInteger maxGasPrice = bigInteger.multiply(BigInteger.valueOf(6));
                        System.out.println("minGasPrice" + minGasPrice + "====================" + "maxGasPrice" + maxGasPrice);
                    }
                });

    }


    @Test
    public void testSubmit() {

    }

    @Test
    public void testGetWalletNameFromAddress() {
        List<Wallet> mWalletList = new ArrayList<>();
        Wallet wallet = new Wallet();
        wallet.setName("wallet-1");
        wallet.setChainId("103");
        wallet.setUuid(UUID.randomUUID().toString());
        wallet.setAddress("0xaf4af5a4f5asd5fas6df");
        mWalletList.add(wallet);

        String name = Single.fromCallable(new Callable<Flowable<String>>() {
            @Override
            public Flowable<String> call() throws Exception {
                return Flowable.fromIterable(mWalletList)
                        .map(new Function<Wallet, String>() {
                            @Override
                            public String apply(Wallet wallet) throws Exception {
                                return wallet.getName();
                            }
                        });
            }
        }).map(new Function<Flowable<String>, String>() {

            @Override
            public String apply(Flowable<String> stringFlowable) throws Exception {
                return stringFlowable.toString();
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return !TextUtils.isEmpty(s);
            }
        }).switchIfEmpty(new SingleSource<String>() {
            @Override
            public void subscribe(SingleObserver<? super String> observer) {
                String addressName = mWalletList.get(0).getName();
                observer.onSuccess(TextUtils.isEmpty(addressName) ? addressName : String.format("%s(%s)", addressName, AddressFormatUtil.formatTransactionAddress(addressName)));
            }
        }).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {

                return !TextUtils.isEmpty(s);
            }
        }).defaultIfEmpty(AddressFormatUtil.formatAddress(""))
                .toSingle().blockingGet();


        assertNotNull(name);
        System.out.println("====================="+name);

    }


}
