package com.platon.wallet.utils;

import android.app.Application;

import com.platon.framework.network.ApiResponse;
import com.platon.wallet.BuildConfig;
import com.platon.wallet.config.AppSettings;
import com.platon.wallet.engine.NodeManager;
import com.platon.wallet.entity.Node;
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

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class AddressFormatUtilTest {
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
    public void formatAddress() {
        String address = "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        String text = null;
        if (address != null) {
            String regex = "(\\w{10})(\\w*)(\\w{10})";
            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        assertEquals("0x2e95e3ce...7872dfb4fd", text);
    }

    @Test
    public void formatTransactionAddress() {
        String address = "0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        String text = null;
        if (address != null) {

            String regex = "(\\w{4})(\\w*)(\\w{4})";

            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        assertEquals("0x2e...b4fd", text);
    }
}