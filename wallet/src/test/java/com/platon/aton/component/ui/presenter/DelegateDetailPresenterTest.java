package com.platon.aton.component.ui.presenter;

import android.app.Activity;
import android.content.Intent;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.DelegateDetailContract;
import com.platon.aton.component.ui.view.DelegateDetailActivity;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.framework.app.Constants;
import com.platon.framework.utils.LogUtils;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class DelegateDetailPresenterTest extends BaseTestCase {


    @Mock
    private DelegateDetailContract.View view;

    private DelegateDetailPresenter presenter;

    @Override
    public void initSetup() {
        view = Mockito.mock(DelegateDetailContract.View.class);
        presenter = new DelegateDetailPresenter();
        presenter.attachView(view);
    }



   /* @Test
    public void getLoadDelegateDetailData(){

        Intent intent = new Intent();
        intent.putExtra(Constants.Extra.EXTRA_DELEGATE_INFO, "HelloWorld");
        Activity activity = Robolectric.buildActivity(DelegateDetailActivity.class).withIntent(intent).create().get();
        assertEquals("HelloWorld", activity.getIntent().getExtras().getString("test"));


        presenter.loadDelegateDetailData();

    }*/

   @Test
   public void getBuildDelegateItemInfoList(){

       List<DelegateItemInfo> delegateInfoList = new ArrayList<>();

       DelegateItemInfo delegateItemInfo = new DelegateItemInfo();
       delegateItemInfo.setNodeId("101");
       delegateItemInfo.setWalletAddress("lat1x8z5nfgjnaryutayxxkzzkrfzenpwl7k6r2a8z");
       delegateItemInfo.setNodeName("节点一");

       DelegateItemInfo delegateItemInfo2 = new DelegateItemInfo();
       delegateItemInfo2.setNodeId("101");
       delegateItemInfo2.setWalletAddress("lax1x8z5nfgjnaryutayxxkzzkrfzenpwl7k4xcjfd");
       delegateItemInfo2.setNodeName("节点二");

       delegateInfoList.add(delegateItemInfo);
       delegateInfoList.add(delegateItemInfo2);


       List<DelegateItemInfo> delegateItemInfos = presenter.buildDelegateItemInfoList(delegateInfoList,"0xfb1b74328f936973a59620d683e1b1acb487d9e7");
       LogUtils.i("---delegateItemInfos之数据:" + delegateItemInfos.size());


   }





}