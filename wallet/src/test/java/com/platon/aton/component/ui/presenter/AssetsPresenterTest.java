package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.app.CustomObserver;
import com.platon.aton.component.ui.contract.AssetsContract;
import com.platon.aton.entity.AccountBalance;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;

import static junit.framework.TestCase.assertNotNull;


public class AssetsPresenterTest  extends BaseTestCase {
    private AssetsPresenter presenter;

    @Mock
    private AssetsContract.View view;

    @Override
    public void initSetup() {
        view = Mockito.mock(AssetsContract.View.class);
        presenter = new AssetsPresenter();
        presenter.attachView(view);
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
