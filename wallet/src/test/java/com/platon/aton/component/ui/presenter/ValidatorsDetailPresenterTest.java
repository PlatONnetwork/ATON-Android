package com.platon.aton.component.ui.presenter;

import com.platon.aton.BaseTestCase;
import com.platon.aton.component.ui.contract.ValidatorsDetailContract;
import com.platon.aton.entity.DelegateItemInfo;
import com.platon.aton.entity.VerifyNodeDetail;
import com.platon.framework.utils.LogUtils;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class ValidatorsDetailPresenterTest extends BaseTestCase {

    private ValidatorsDetailContract.View view;
    private ValidatorsDetailPresenter presenter;

    @Override
    public void initSetup() {

        view = Mockito.mock(ValidatorsDetailContract.View.class);
        presenter = new ValidatorsDetailPresenter();
        presenter.attachView(view);
    }


    @Test
    public void getDelegateDetail(){

        VerifyNodeDetail mVerifyNodeDetail = new VerifyNodeDetail();
        mVerifyNodeDetail.setBlockOutNumber(100);
        mVerifyNodeDetail.setDelegate("200");
        mVerifyNodeDetail.setName("lucy");
        mVerifyNodeDetail.setNodeId("123");
        presenter.setmVerifyNodeDetail(mVerifyNodeDetail);

        DelegateItemInfo delegateItemInfo = presenter.getDelegateDetail();
        LogUtils.i("------delegateItemInfo:" + delegateItemInfo);

    }
}