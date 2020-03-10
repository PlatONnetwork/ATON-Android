package com.platon.wallet.utils;


import android.app.Application;
import com.platon.framework.app.log.Log;
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

import java.math.BigDecimal;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class NumberParserUtilsTest {
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
    public void   parseLong(){
        long  defaultValue = 0;
        String value = "100000"; //parseLong 必须是整数类型的字符串，不能有小数点
        defaultValue = Long.parseLong(value);
        Log.debug("======", "得到结果result---------->" + defaultValue);

    }


    @Test
    public  void  parseDouble(){
        double resultValue = 0;
        String value ="10.648798787987979";
        resultValue = Double.parseDouble(value);
        Log.debug("======", "得到结果result---------->" + new BigDecimal(resultValue));
        Log.debug("======", "得到结果result---------->" + resultValue);

    }

    @Test
    public  void parseFloat(){
        float resultValue = 0;
        String  value = "10.648798787987979";
        resultValue = Float.parseFloat(value);
        Log.debug("======", "得到结果result---------->" + new BigDecimal(resultValue));
        Log.debug("======", "得到结果result---------->" + resultValue);
    }

    @Test
    public  void  parseInt(){
        int resultValue =0;
        String  value = "10";
        resultValue = Integer.parseInt(value);
        Log.debug("======", "得到结果result---------->" + new BigDecimal(resultValue));
        Log.debug("======", "得到结果result---------->" + resultValue);
    }

    @Test
    public  void getPrettyNumber(){
        String number ="65546546.654681161500";
        String bigDecimalStr ="";
        String newStr =null;
        bigDecimalStr = BigDecimal.valueOf(Double.parseDouble(number)).stripTrailingZeros().toPlainString();
        if (bigDecimalStr.endsWith(".00") || bigDecimalStr.endsWith(".0")) {
            newStr= bigDecimalStr.substring(0, bigDecimalStr.lastIndexOf("."));
        }

        Log.debug("======", "得到结果result---------->" + newStr);
    }



}
