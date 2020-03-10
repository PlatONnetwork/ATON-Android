package com.platon.wallet.engine;

import android.app.Application;
import android.text.TextUtils;

import com.platon.framework.app.log.Log;
import com.platon.framework.network.ApiResponse;
import com.platon.wallet.BuildConfig;
import com.platon.wallet.config.AppSettings;
import com.platon.wallet.entity.AccountBalance;
import com.platon.wallet.entity.Node;
import com.platon.wallet.entity.Wallet;
import com.platon.wallet.rxjavatest.RxJavaTestSchedulerRule;

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
import java.util.concurrent.Callable;

import io.reactivex.Flowable;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class WalletManagerTest{
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

        appSettings.init(app);

    }
    @Test
    public void getTotal() {
        List<Wallet> list = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);

        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);

        list.add(wallet3);


        Wallet wallet4 = new Wallet();
        wallet4.setName("004");
        wallet4.setCreateTime(1115448486);
        AccountBalance balance4 = new AccountBalance();
        balance4.setFree("1000000001156584855");
        balance4.setLock("0");
        wallet4.setAccountBalance(balance4);

        list.add(wallet4);

        Flowable.fromIterable(list)
                .map(new Function<Wallet, AccountBalance>() {
                    @Override
                    public AccountBalance apply(Wallet wallet) throws Exception {
                        return wallet.getAccountBalance();
                    }
                }).map(new Function<AccountBalance, BigDecimal>() {

            @Override
            public BigDecimal apply(AccountBalance accountBalance) throws Exception {
                return new BigDecimal(accountBalance.getFree());
            }
        }).reduce(new BiFunction<BigDecimal, BigDecimal, BigDecimal>() {
            @Override
            public BigDecimal apply(BigDecimal balance1, BigDecimal balance2) throws Exception {
                Log.debug("============", "--------->" + balance1.add(balance2));
                return balance1.add(balance2);
            }
        }).toObservable();

    }

    @Test
    public void getPositionByAddress() {
        List<Wallet> list = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        wallet.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        wallet2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);

        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);

        list.add(wallet3);


        Wallet wallet4 = new Wallet();
        wallet4.setName("004");
        wallet4.setCreateTime(1115448486);
        AccountBalance balance4 = new AccountBalance();
        balance4.setFree("1000000001156584855");
        balance4.setLock("0");
        wallet4.setAccountBalance(balance4);
        list.add(wallet4);

        int position = Flowable
                .range(0, list.size())
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return list.get(integer).getPrefixAddress().equalsIgnoreCase("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
                    }
                })
                .firstElement()
                .defaultIfEmpty(-1)
                .onErrorReturnItem(-1)
                .blockingGet();
        Log.debug("位置===", "=========选中的位置" + position);
    }


    @Test
    public void getAddressList() {
        List<Wallet> list = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        wallet.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        wallet2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);

        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);

        list.add(wallet3);


        Wallet wallet4 = new Wallet();
        wallet4.setName("004");
        wallet4.setCreateTime(1115448486);
        AccountBalance balance4 = new AccountBalance();
        balance4.setFree("1000000001156584855");
        balance4.setLock("0");
        wallet4.setAccountBalance(balance4);
        list.add(wallet4);

        List<String> addressList = Flowable.fromIterable(list)
                .map(new Function<Wallet, String>() {
                    @Override
                    public String apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getPrefixAddress();
                    }
                }).collect(new Callable<List<String>>() {
                    @Override
                    public List<String> call() throws Exception {
                        return new ArrayList<>();
                    }
                }, new BiConsumer<List<String>, String>() {
                    @Override
                    public void accept(List<String> strings, String s) throws Exception {
                        strings.add(s);
                    }
                }).blockingGet();


        Log.debug("=========", "地址列表" + addressList.toString());

    }


    @Test
    public void isWalletNameExists() {

        List<Wallet> list = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        wallet.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        wallet2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);
        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);
        list.add(wallet3);


        Wallet wallet4 = new Wallet();
        wallet4.setName("004");
        wallet4.setCreateTime(1115448486);
        AccountBalance balance4 = new AccountBalance();
        balance4.setFree("1000000001156584855");
        balance4.setLock("0");
        wallet4.setAccountBalance(balance4);
        list.add(wallet4);

        boolean isExitName = Flowable
                .fromIterable(list)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getName().toLowerCase().equals("002");
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .firstElement()
                .defaultIfEmpty(false)
                .blockingGet();

        Log.debug("=========", "名称是否存在" + "====>" + isExitName);

    }

    @Test
    public void isWalletAddressExists() {
        List<Wallet> list = new ArrayList<>();

        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        wallet.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        wallet2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);
        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);
        list.add(wallet3);


        Wallet wallet4 = new Wallet();
        wallet4.setName("004");
        wallet4.setCreateTime(1115448486);
        AccountBalance balance4 = new AccountBalance();
        balance4.setFree("1000000001156584855");
        balance4.setLock("0");
        wallet4.setAccountBalance(balance4);
        list.add(wallet4);

        String address = "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";

        boolean isExitAddress = Flowable
                .fromIterable(list)
                .map(new Function<Wallet, Boolean>() {
                    @Override
                    public Boolean apply(Wallet walletEntity) throws Exception {
                        return walletEntity.getPrefixAddress().toLowerCase().equalsIgnoreCase(address.toLowerCase());
                    }
                })
                .filter(new Predicate<Boolean>() {
                    @Override
                    public boolean test(Boolean aBoolean) throws Exception {
                        return aBoolean;
                    }
                })
                .firstElement()
                .defaultIfEmpty(false)
                .blockingGet();

        Log.debug("=========", "地址是否存在" + "====>" + isExitAddress);

    }

    @Test
    public void getWalletByAddress() {
        List<Wallet> list = new ArrayList<>();
        Wallet wallet = new Wallet();
        wallet.setCreateTime(1115448481);
        wallet.setName("001");
        wallet.setAddress("0xfb1b74328f936973a59620d683e1b1acb487d9e7");
        AccountBalance balance = new AccountBalance();
        balance.setFree("10000000084489");
        balance.setLock("0");
        wallet.setAccountBalance(balance);
        list.add(wallet);

        Wallet wallet2 = new Wallet();
        wallet2.setCreateTime(1115448485);
        wallet2.setName("002");
        wallet2.setAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        AccountBalance balance2 = new AccountBalance();
        balance2.setFree("1000000005655655");
        balance2.setLock("0");
        wallet2.setAccountBalance(balance2);
        list.add(wallet2);

        Wallet wallet3 = new Wallet();
        wallet3.setName("003");
        wallet3.setAddress("0xca4b151b0b100ae53c9d78dd136905e681622ee7");
        wallet3.setCreateTime(1115448483);
        AccountBalance balance3 = new AccountBalance();
        balance3.setFree("10000000056556584855");
        balance3.setLock("0");
        wallet3.setAccountBalance(balance3);
        list.add(wallet3);

        String address = "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        Wallet wa = null;
        if (TextUtils.isEmpty(address)) {
            return;
        }

        for (Wallet walletEntity : list) {
            if (walletEntity.getPrefixAddress().toLowerCase().contains(address.toLowerCase())) {
                wa = walletEntity;
                Log.debug("=============", "钱包信息====" + "========>" + wa.toString());
                return;
            }
        }

    }

}