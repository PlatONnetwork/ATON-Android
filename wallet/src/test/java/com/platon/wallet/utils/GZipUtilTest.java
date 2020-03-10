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


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class GZipUtilTest {
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
    public void compress() {
        String str = "{\"qrCodeData\":[{\"amount\":\"100004999975506010000000000\",\"chainId\":\"101\",\"from\":\"0xca4b151b0b100ae53c9d78dd136905e681622ee7\",\"functionType\":1004,\"gasLimit\":\"48988\",\"gasPrice\":\"499999750000\",\"nodeId\":\"0x411a6c3640b6cd13799e7d4ed286c95104e3a31fbb05d7ae0004463db648f26e93f7f5848ee9795fb4bbb5f83985afd63f750dc4cf48f53b0e84d26d6834c20c\",\"nodeName\":\"节点02\",\"nonce\":\"0\",\"stakingBlockNum\":\"\",\"to\":\"0x1000000000000000000000000000000000000002\",\"typ\":0}],\"qrCodeType\":0,\"timestamp\":1572590948}";
        Log.debug("GzipUtilsTest", "压缩前=========" + "长度" + str.length() + "==========>" + "字节数组======" + str.getBytes());
        String zipStr = GZipUtil.compress(str);
        Log.debug("GzipUtilsTest", "压缩后=========" + "长度" + zipStr.length() + "==========>" + "字节数组======" + zipStr.getBytes());
    }

    @Test
    public void unCompress() {
        String str = "{\"qrCodeData\":[{\"amount\":\"100004999975506010000000000\",\"chainId\":\"101\",\"from\":\"0xca4b151b0b100ae53c9d78dd136905e681622ee7\",\"functionType\":1004,\"gasLimit\":\"48988\",\"gasPrice\":\"499999750000\",\"nodeId\":\"0x411a6c3640b6cd13799e7d4ed286c95104e3a31fbb05d7ae0004463db648f26e93f7f5848ee9795fb4bbb5f83985afd63f750dc4cf48f53b0e84d26d6834c20c\",\"nodeName\":\"节点02\",\"nonce\":\"0\",\"stakingBlockNum\":\"\",\"to\":\"0x1000000000000000000000000000000000000002\",\"typ\":0}],\"qrCodeType\":0,\"timestamp\":1572590948}";
        String zipStr = GZipUtil.compress(str);
        Log.debug("GzipUtilsTest", "压缩后=========" + "长度" + zipStr.length() + "==========>" + "字节数组======" + zipStr.getBytes() + "----------->" +zipStr);
        String unzipStr = GZipUtil.unCompress(zipStr);
        Log.debug("GzipUtilsTest", "解压缩=========" + "长度" + unzipStr.length() + "==========>" + "字节数组======" + unzipStr.getBytes() +"-------------->" +unzipStr);

    }

}