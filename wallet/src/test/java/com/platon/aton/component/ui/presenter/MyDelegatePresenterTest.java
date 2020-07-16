package com.platon.aton.component.ui.presenter;

import android.view.View;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.MyDelegateContract;
import com.platon.aton.component.ui.view.MyDelegateFragment;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.FragmentController;
import org.robolectric.shadows.support.v4.SupportFragmentTestUtil;

import static org.junit.Assert.*;

public class MyDelegatePresenterTest extends BaseTestCase {

    @Mock
    private MyDelegateContract.View view;
    private MyDelegatePresenter presenter;

    @Override
    public void initSetup() {

      /*  view = Mockito.mock(MyDelegateContract.View.class);
       // Robolectric.setupActivity(SampleActivity.class)
        presenter = new MyDelegatePresenter();
        presenter.attachView(view);*/

/*
        MyDelegateFragment myDelegateFragment = new MyDelegateFragment();
        //添加Fragment到Activity中，会触发Fragment的onCreateView()
        SupportFragmentTestUtil.startFragment(myDelegateFragment);
        //View view = myDelegateFragment.getView();
        presenter = myDelegateFragment.createPresenter();
        view = myDelegateFragment.createView();
        myDelegateFragment

        presenter.attachView(view);*/

    }


    @Test
    public void loadMyDelegateData(){

       // presenter.loadMyDelegateData();

    }
}