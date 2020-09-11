package com.platon.aton.utils;


import android.app.Application;
import android.util.Log;

import com.platon.aton.BuildConfig;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.PreferenceTool;

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
        PreferenceTool.init(app);

        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);

        //输出日志
        ShadowLog.stream = System.out;



    }

    @Test
    public void   parseLong(){
        long  defaultValue = 0;
        String value = "100000"; //parseLong 必须是整数类型的字符串，不能有小数点
        defaultValue = Long.parseLong(value);
        Log.d("======", "得到结果result---------->" + defaultValue);

    }


    @Test
    public  void  parseDouble(){
        double resultValue = 0;
        String value ="10.648798787987979";
        resultValue = Double.parseDouble(value);
        Log.d("======", "得到结果result---------->" + new BigDecimal(resultValue));
        Log.d("======", "得到结果result---------->" + resultValue);

    }

    @Test
    public  void parseFloat(){
        float resultValue = 0;
        String  value = "10.648798787987979";
        resultValue = Float.parseFloat(value);
        Log.d("======", "得到结果result---------->" + new BigDecimal(resultValue));
        Log.d("======", "得到结果result---------->" + resultValue);
    }

    @Test
    public  void  parseInt(){
        int resultValue =0;
        String  value = "10";
        resultValue = Integer.parseInt(value);
        Log.d("======", "得到结果result---------->" + new BigDecimal(resultValue));
        Log.d("======", "得到结果result---------->" + resultValue);
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

        Log.d("======", "得到结果result---------->" + newStr);
    }

    @Test
    public void test(){
        String value10 = "10000000000000000000";//1.0E19
        String value100 = "100000000000000000000";//1.0E20
        String value100000 = "100000000000000000000000";//9.999999999999999E22???
        String value1000000 = "1000000000000000000000000";//9.999999999999999E22???
        Double result10 = NumberParserUtils.parseDouble(value10);
        Double result100 = NumberParserUtils.parseDouble(value100);
        Double result100000 = NumberParserUtils.parseDouble(value100000);
        Double result1000000 = NumberParserUtils.parseDouble(value1000000);
        System.out.println("---result10:" + result10);
        System.out.println("---result100:" + result100);
        System.out.println("---result100000:" + result100000);
        System.out.println("---result1000000:" + result1000000);
    }



}
