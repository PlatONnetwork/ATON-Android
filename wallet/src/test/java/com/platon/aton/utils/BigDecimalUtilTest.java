package com.platon.aton.utils;

import android.app.Application;

import com.platon.framework.app.log.Log;
import com.platon.framework.network.ApiResponse;
import com.platon.aton.BuildConfig;
import com.platon.aton.config.AppSettings;
import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;

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
public class BigDecimalUtilTest {
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
    public void add() {
        double result = 0D;
        double v1 = 456456456.46554;
        double v2 = 4545889963333333.6456;
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        result = b1.add(b2).doubleValue();
        Log.debug("======", "得到结果result" + result);

    }

    @Test
    public void add2() {
        BigDecimal result = BigDecimal.ZERO;
        String  v1 = "456456456.46554";
        String v2 = "4545889963333333.6456";
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        result = b1.add(b2);

        Log.debug("======", "得到结果result---------->" + result);
    }

    @Test
    public void sub(){
        double result = 0D;
        double v1 = 456456456.46554;
        double v2 = 4545889963333333.6456;
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        result = b1.subtract(b2).doubleValue();

        Log.debug("======", "得到结果result---------->" + result);
    }


    @Test
    public void sub2(){
        double result = 0D;
        String  v1 = "456456456.46554";
        String v2 = "4545889963333333.6456";

        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        result = b1.subtract(b2).doubleValue();

        Log.debug("======", "得到结果result---------->" + result);
    }

    @Test
    public void mul(){
        double result = 0D;
        double v1 = 456456456.46554;
        double v2 = 4545889963333333.6456;
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        result = b1.multiply(b2).doubleValue();
//        2.075000824145397E24
        //2075000824145397095071744
        Log.debug("======", "得到结果result---------->" + new BigDecimal(result).toPlainString());
    }

    @Test
    public  void mul2(){
        BigDecimal result = new BigDecimal(0);
        String  v1 = "456456456.46554";
        String v2 = "4545889963333333.6456";
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        result = b1.multiply(b2);
        //2075000824145397036066336.138972624
        Log.debug("======", "得到结果result---------->" + result.toPlainString());
    }

    @Test
    public void div(){
        double result = 0D;
        double v1 = 4545889963333333.6456;
        double v2 = 456456456.46554;
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        result = b1.divide(b2, 3, BigDecimal.ROUND_HALF_UP).doubleValue();
        //9959087.88000000081956386566162109375  BigDecimal.valueOf(value).toPlainString()
        Log.debug("======", "得到结果result---------->" + new BigDecimal(result).toPlainString());
        Log.debug("======", "得到结果result---------->" + BigDecimal.valueOf(result).toPlainString());

    }

    @Test
    public void div2(){
        String result = BigDecimal.ZERO.toPlainString();
        String  v1 = "4545889963333333.6456";
        String v2 = "456456456.46554";
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        result = b1.divide(b2, 3, BigDecimal.ROUND_HALF_UP).toPlainString();
        //9959087.880
        Log.debug("======", "得到结果result---------->" + new BigDecimal(result).toPlainString());
    }

}