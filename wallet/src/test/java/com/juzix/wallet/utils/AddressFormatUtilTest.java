package com.juzix.wallet.utils;

import android.app.Application;

import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.BaseTestCase;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.rxjavatest.RxJavaTestSchedulerRule;

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

import static org.junit.Assert.*;
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class AddressFormatUtilTest{
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
        String address ="0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        String text =null;
        if (address != null) {
            String regex = "(\\w{10})(\\w*)(\\w{10})";
            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Log.debug("格式化后的地址","=============" + text);
    }

    @Test
    public void formatTransactionAddress() {
        String address ="0x2e95e3ce0a54951eb9a99152a6d5827872dfb4fd";
        String text =null;
        if (address != null) {

            String regex = "(\\w{4})(\\w*)(\\w{4})";

            try {
                text = address.replaceAll(regex, "$1...$3");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.debug("格式化后的地址","=============" + text);

    }
}