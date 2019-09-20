package com.juzix.wallet;


import com.juzix.wallet.component.service.LoopService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ServiceController;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 27)
public class TestServiceTest {

    private ServiceController<LoopService> controller;
    private LoopService testService;

    @Before
    public void setUp() throws Exception {
        controller = Robolectric.buildService(LoopService.class);
        testService = controller.get();
    }

    /**
     * 控制Service生命周期进行验证
     *
     * @throws Exception
     */

    @Test
    public void testLifecycle() throws Exception {

        controller.create();
        // verify something

        controller.startCommand(0, 0);
        // verify something

//        controller.bind();
//        // verify something

        controller.unbind();
        // verify something

        controller.destroy();
        // verify something
    }
}
