package com.platon.aton.component.service;

import com.platon.aton.BaseTestCase;

import org.junit.Test;

public class LoopServiceTest extends BaseTestCase {


    @Override
    public void initSetup() {

    }


    @Test
    public void startLoopService(){
        LoopService.startLoopService(application);
    }


    @Test
    public void quitLoopService(){
        LoopService.quitLoopService(application);

    }


    public void startLoop(){

    }






}
