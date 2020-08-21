package com.platon.aton.utils;

import android.app.Application;
import android.util.Log;

import com.platon.aton.BuildConfig;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.PreferenceTool;

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
        PreferenceTool.init(app);

        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);

        //输出日志
        ShadowLog.stream = System.out;

    }

    @Test
    public void getPrettyBalance() {
        long defaultValue = 0;
        String value = "100000"; //parseLong 必须是整数类型的字符串，不能有小数点
        defaultValue = Long.parseLong(value);
        Log.d("======", "得到结果result---------->" + defaultValue);
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

    @Test
    public void amountTransf(){
        //乘以18个0
        String value10 = "10000000000000000000";//1.0E19
        String value100 = "100000000000000000000";//1.0E20
        String value100000 = "100000000000000000000000";
        String result10 = AmountUtil.formatAmountText(value10);
        String result100 = AmountUtil.formatAmountText(value100);
        String result100000 = AmountUtil.formatAmountText(value100000);
        System.out.println("---result10:" + result10);
        System.out.println("---result100:" + result100);
        System.out.println("---result100000:" + result100000);
    }

}
