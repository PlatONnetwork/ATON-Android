package com.platon.aton.engine;

import android.app.Application;

import com.platon.aton.BuildConfig;
import com.platon.aton.config.AppSettings;
import com.platon.aton.rxjavatest.RxJavaTestSchedulerRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * @author ziv
 * date On 2020-03-19
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23, manifest = Config.NONE, constants = BuildConfig.class)
public class DeviceManagerTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();
    @Rule
    public RxJavaTestSchedulerRule rule = new RxJavaTestSchedulerRule();

    Application application;

    @Before
    public void setup() {

        application = RuntimeEnvironment.application;

        AppSettings.getInstance().init(application);

        DeviceManager.getInstance().init(application, "PlatON");
    }

    @Test
    public void getChannel() {

        Assert.assertEquals(DeviceManager.getInstance().getChannel(), "PlatON");

    }

    @Test
    public void isGooglePlayChannel() {

        Assert.assertEquals(DeviceManager.getInstance().isGooglePlayChannel(), false);
    }

    @Test
    public void setDeviceId() {

        DeviceManager.getInstance().setDeviceID("9774d56d682e549c");

        Assert.assertEquals(DeviceManager.getInstance().getDeviceID(), "9774d56d682e549c");

    }

    @Test
    public void getOS() {

        Assert.assertEquals(DeviceManager.getInstance().getOS(), "android");

    }

    @Test
    public void getVersionName() {

        Assert.assertEquals(DeviceManager.getVersionName(application), null);
    }

    @Test
    public void getVersionCode() {

        Assert.assertEquals(DeviceManager.getVersionCode(application), 0);
    }

}

