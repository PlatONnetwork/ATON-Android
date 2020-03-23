package com.platon.aton.engine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ziv
 * date On 2020-03-19
 */
public class AppConfigManagerTest {


    @Before
    public void setUp() {

        AppConfigManager.getInstance().init();

    }


    @Test
    public void getMinGasPrice() {
        Assert.assertNotEquals(AppConfigManager.getInstance().getMinGasPrice(), "10000000000");
    }

    @Test
    public void getMinDelegation() {
        Assert.assertNotEquals(AppConfigManager.getInstance().getMinGasPrice(), null);
    }

    @Test
    public void getTimeout() {
        Assert.assertNotEquals(AppConfigManager.getInstance().getMinGasPrice(), "86400000");
    }
}
