package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.DelegateRecordContract;
import com.platon.aton.component.ui.view.DelegateActivity;
import com.platon.framework.app.Constants;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;

import static org.junit.Assert.*;

public class DelegateRecordPresenterTest extends BaseTestCase {

    @Mock
    private DelegateRecordContract.View view;

    private DelegateRecordPresenter presenter;

    @Override
    public void initSetup() {

       // DelegateActivity delegateActivity = Robolectric.setupActivity(DelegateActivity.class);
       // view = delegateActivity.createView();
     /*   DelegateRecordFragment*/

        view = Mockito.mock(DelegateRecordContract.View.class);
        presenter = new DelegateRecordPresenter();
        presenter.attachView(view);

    }

    @Test
    public void getLoadDelegateRecordData(){


       // presenter.loadDelegateRecordData(Constants.VoteConstants.NEWEST_DATA, Constants.VoteConstants.REFRESH_DIRECTION, Constants.DelegateRecordType.All);

    }
}