package com.juzix.wallet.utils;

import android.app.Application;

import com.juzhen.framework.app.log.Log;
import com.juzhen.framework.network.ApiResponse;
import com.juzix.wallet.BuildConfig;
import com.juzix.wallet.config.AppSettings;
import com.juzix.wallet.engine.NodeManager;
import com.juzix.wallet.entity.Node;
import com.juzix.wallet.rxjavatest.RxJavaTestSchedulerRule;

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

/**
 * @author ziv
 * date On 2020-02-13
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class AmountUtilsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();
    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;

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
    public void getPrettyBalance() {
        long defaultValue = 0;
        String value = "100000"; //parseLong 必须是整数类型的字符串，不能有小数点
        defaultValue = Long.parseLong(value);
        Log.debug("======", "得到结果result---------->" + defaultValue);
    }

    @Test
    public void getPrettyFee() {
        String value = String.valueOf(1000000000);

        AmountUtil.getPrettyFee(value, 8);
    }

    @Test
    public void convertVonToLat() {
        String value = String.valueOf(1000000000);

        AmountUtil.getPrettyFee(value, 8);
    }

    @Test
    public void formatAmountText() {
        String value = String.valueOf(1000000000);

        AmountUtil.getPrettyFee(value, 8);
    }

}
