package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.MainContract;
import com.platon.aton.entity.VersionInfo;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MainPresenterTest extends BaseTestCase {

    private MainContract.View view;

    private MainPresenter presenter;

    @Override
    public void initSetup() {
         view = Mockito.mock(MainContract.View.class);
         presenter = new MainPresenter();
         presenter.attachView(view);
    }

    @Test
    public void checkVersion(){
        presenter.checkVersion();
    }


  /*  @Test
    public void requestPermission(){
        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setUrl("http://www.baidu.com");
        versionInfo.setForce(true);
        versionInfo.setNeed(true);
        versionInfo.setNewVersion("1.3.0.1");
        presenter.requestPermission(versionInfo);
    }*/
}