package com.platon.aton.component.ui.presenter;


import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.MainContract;
import com.platon.aton.entity.VersionInfo;
import com.platon.aton.entity.WalletTypeSearch;
import com.platon.framework.app.Constants;
import com.platon.framework.utils.LogUtils;

import org.junit.Test;
import org.mockito.Mockito;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

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


    @Test
    public void checkKeywordsAddress(){

        String address = "lat1rwgwmhtm0j0y724vv55uugmlft9f0dgec9gq27";
        boolean isVailtAddress = presenter.checkKeywordsAddress(address);
        if(isVailtAddress){
            LogUtils.d("--------------地址验证有效");
        }else{
            LogUtils.d("--------------地址验证无效");
        }
    }


    @Test
    public void checkShouldUpdate(){


        VersionInfo versionInfo = new VersionInfo();
        versionInfo.setNewVersion("V0.13.2");
        versionInfo.setNeed(true);
        versionInfo.setForce(false);
        versionInfo.setUrl("");
        versionInfo.setDesc("1.升级 xxx \r\n2.升级 yyy");
        boolean isUpdate = presenter.shouldUpdate(versionInfo);
        if(isUpdate){
            LogUtils.d("--------------可以更新");
        }else{
            LogUtils.d("--------------不需要更新");
        }
    }






}
