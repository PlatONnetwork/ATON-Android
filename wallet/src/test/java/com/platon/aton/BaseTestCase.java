package com.platon.aton;


import android.app.Activity;
import android.app.Application;
import android.support.v4.app.Fragment;

import com.platon.aton.engine.NodeManager;
import com.platon.aton.entity.Node;
import com.platon.framework.network.ApiResponse;
import com.platon.framework.utils.PreferenceTool;

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowLog;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;
import org.web3j.crypto.WalletApplication;
import org.web3j.crypto.bech32.AddressBehavior;
import org.web3j.crypto.bech32.AddressManager;

/**
 * 测试基类
 * 其他测试类需要的时候直接继承这个类，不需要每次去添加配置了
 *
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23,manifest = Config.NONE,constants = BuildConfig.class)
public abstract class BaseTestCase {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
  /*  @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();*/

    @Mock
    public NodeManager nodeManager;
    @Mock
    public Node node;

    public Application application;

    @Before
    public void setup() throws Exception {
        nodeManager = NodeManager.getInstance();
        node = new Node.Builder().build();
        nodeManager.setCurNode(node);
        //输出日志
        ShadowLog.stream = System.out;
        application = getApplication();

        ApiResponse.init(application);
        PreferenceTool.init(application);
        WalletApplication.init(WalletApplication.TESTNET, AddressManager.ADDRESS_TYPE_BECH32, AddressBehavior.CHANNLE_PLATON);

        initSetup();
        initData();
    }


    public Application getApplication() {
        return RuntimeEnvironment.application;
    }

    public String getString(int id) {
        return RuntimeEnvironment.application.getString(id);
    }

    public String getPkgName() {
        return RuntimeEnvironment.application.getPackageName();
    }

    public <T extends Activity> T startActivity(Class<T> activityClass) {
        return Robolectric.setupActivity(activityClass);
    }

    public void startFragment(Fragment fragment) {
        SupportFragmentTestUtil.startFragment(fragment);
    }


    public abstract void initSetup();
    public void initData(){};




}
