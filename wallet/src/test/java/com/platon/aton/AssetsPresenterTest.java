package com.platon.aton;

import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.AssetsContract;
import com.platon.aton.component.ui.presenter.AssetsPresenter;
import com.platon.aton.config.AppSettings;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.aton.schedulers.SchedulerTestProvider;

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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 27, manifest = Config.NONE)
public class AssetsPresenterTest {
    private AssetsPresenter presenter;

    @Mock
    private AssetsContract.View view;

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
        view = mock(AssetsContract.View.class);
        presenter = new AssetsPresenter();
        presenter.attachView(view);
        appSettings.init(RuntimeEnvironment.application);
    }

    @Test
    public void testFetchWalletsBalance() {
        List<AccountBalance> accountBalanceList = new ArrayList<>();
        AccountBalance balance = new AccountBalance();
        balance.setLock("1532000000000000000000");
        balance.setFree("135960000000000000000");
        balance.setAddr("0xfa42a4dfa5sd4f5asd6f5a");
        accountBalanceList.add(balance);

        AccountBalance balance2 = new AccountBalance();
        balance2.setLock("153289940000000000000000");
        balance2.setFree("1359826400000000000000");
        balance2.setAddr("0xfa42a4dfa5sd4f5asd6f5a");
        accountBalanceList.add(balance2);


        List<BigDecimal> decimalList =  Flowable.fromIterable(accountBalanceList)
                .map(new Function<AccountBalance, BigDecimal>() {
                    @Override
                    public BigDecimal apply(AccountBalance accountBalance) throws Exception {
                        return new BigDecimal(accountBalance.getFree());
                    }
                })
                .reduce(new BiFunction<BigDecimal, BigDecimal, BigDecimal>() {
                    @Override
                    public BigDecimal apply(BigDecimal balance1, BigDecimal banalce2) throws Exception {
                        return balance1.add(banalce2);
                    }
                })
                .toObservable()
                .toList().blockingGet();

        assertNotNull(decimalList);

        Flowable.fromIterable(accountBalanceList)
                .map(new Function<AccountBalance, BigDecimal>() {
                    @Override
                    public BigDecimal apply(AccountBalance accountBalance) throws Exception {
                        return new BigDecimal(accountBalance.getFree());
                    }
                })
                .reduce(new BiFunction<BigDecimal, BigDecimal, BigDecimal>() {
                    @Override
                    public BigDecimal apply(BigDecimal balance1, BigDecimal banalce2) throws Exception {
                        return balance1.add(banalce2);
                    }
                })
                .toObservable().subscribe(new CustomObserver<BigDecimal>() {
            @Override
            public void accept(BigDecimal bigDecimal) {
                System.out.println(bigDecimal.toPlainString());
            }
        });

    }

}
