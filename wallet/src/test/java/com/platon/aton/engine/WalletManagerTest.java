package com.platon.aton.engine;

import android.app.Application;

import com.platon.aton.BuildConfig;
import com.platon.aton.config.AppSettings;
import com.platon.aton.entity.AccountBalance;
import com.platon.aton.entity.Node;
import com.platon.aton.entity.Wallet;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.framework.network.ApiResponse;

import org.junit.Assert;
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
import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class WalletManagerTest {
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
        balance3.setFree("100000000");
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

        WalletManager.getInstance().setWalletList(list);

        Assert.assertEquals(WalletManager.getInstance().getTotal().blockingFirst().longValue(), 1000000001156584855L + 100000000L + 1000000005655655L + 10000000084489L);

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
        wallet2.setAddress("0X2E95e3ce0a54951eb9a99152A6D5827872DFB4FD");
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

        Assert.assertEquals(
                WalletManager.getInstance().getPositionByAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd"), 1);


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

        WalletManager.getInstance().setWalletList(list);

        Assert.assertEquals(WalletManager.getInstance().getAddressList().size(), 3);

        Assert.assertEquals(WalletManager.getInstance().getAddressList(), Arrays.asList("0xfb1b74328f936973a59620d683e1b1acb487d9e7", "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd", "0xca4b151b0b100ae53c9d78dd136905e681622ee7"));

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


        WalletManager.getInstance().setWalletList(list);

        Assert.assertEquals(WalletManager.getInstance().isWalletNameExists("003"), true);
        Assert.assertEquals(WalletManager.getInstance().isWalletNameExists("00888"), false);

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

        WalletManager.getInstance().setWalletList(list);

        Assert.assertEquals(WalletManager.getInstance().isWalletAddressExists("0xca4b151b0b100ae53c9d78dd136905e681622ee7"), true);
        Assert.assertEquals(WalletManager.getInstance().isWalletAddressExists("0XCA4B151B0B100AE53C9D78DD136905E681622EE7"), true);

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

        WalletManager.getInstance().setWalletList(list);

        String address = "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";

        Wallet wallet1 = WalletManager.getInstance().getWalletByAddress("0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd");
        Wallet wallet4 = WalletManager.getInstance().getWalletByAddress("0X2E95E3CE0A54951EB9A99152A6D5827872DFB4FD");

        Assert.assertNotEquals(wallet1, null);
        Assert.assertNotEquals(wallet4, null);

    }

}